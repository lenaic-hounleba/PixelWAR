package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Gère la connexion d'un client côté serveur.
 * Chaque instance tourne dans un thread séparé et traite les messages reçus.
 */
public class ClientSocket implements Runnable {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Serveur serveur;
    private String pseudo;
    private boolean cooldown = false;

    /**
     * Constructeur.
     * @param socket socket du client connecté
     * @param serveur référence au serveur principal
     * @throws IOException si erreur d'initialisation des flux
     */
    public ClientSocket(Socket socket, Serveur serveur) throws IOException {
        this.socket  = socket;
        this.serveur = serveur;
        this.output  = new ObjectOutputStream(socket.getOutputStream());
        this.input   = new ObjectInputStream(socket.getInputStream());
    }
    
    /** @return pseudo du joueur associé à cette connexion */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Envoie un objet au client via le flux de sortie.
     * @param obj objet à envoyer
     * @throws IOException si erreur réseau
     */
    public void envoyer(Object obj) throws IOException {
        output.writeObject(obj);
    }

    /**
     * Boucle principale du thread.
     * Écoute les objets reçus et les dispatche vers traiterPixel ou traiterRequest.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Object obj = input.readObject();

                if (obj instanceof Pixel) {
                    traiterPixel((Pixel) obj);

                } else if (obj instanceof Request) {
                    traiterRequest((Request) obj);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client déconnecté : " + pseudo);
            serveur.retirerClient(this);
            serveur.diffuser(new Request(RequestType.QUIT, pseudo));
        }
    }

    /**
     * Traite une demande de pose de pixel.
     * Vérifie le cooldown du joueur et l'embargo du pixel avant de valider.
     * @param pixel pixel envoyé par le client
     * @throws IOException si erreur réseau
     */
    private void traiterPixel(Pixel pixel) throws IOException {
        // si le joueur est encore en cooldown
    	if (serveur.getGrille().estVerrouille(pixel.getX(), pixel.getY())) {
    	    envoyer(new PixelVerrouilleException(serveur.getGrille().getTimestamp(pixel.getX(), pixel.getY())));
    	    return;
    	}

    	if (cooldown) {
    	    envoyer(new CooldownException());
    	    return;
    	}

        serveur.getGrille().setPixel(
            pixel.getX(), pixel.getY(),
            pixel.getCouleur(),
            pixel.getJoueur()
        );
        serveur.getGrille().verrouiller(pixel.getX(), pixel.getY());
        
        long ts = System.currentTimeMillis();
        pixel.setTimestamp(ts);
        serveur.getGrille().setTimestamp(pixel.getX(), pixel.getY(), ts);
        // Confirme au client expéditeur en premier
        envoyer(pixel);
        
        // cooldown 30s pour ce joueur
        cooldown = true;
        new Timer().schedule(new TimerTask() {
            public void run() {
                cooldown = false;
            }
        }, 30000);
        
        // libérer le pixel après 1 min en garde la couleur, juste déverrouillé
        new Timer().schedule(new TimerTask() {
            public void run() {
                serveur.getGrille().deverrouiller(pixel.getX(), pixel.getY());
                // notifie tous les clients que ce pixel est libéré
                serveur.diffuser(new Request(RequestType.LIBERE, pixel.getX() + "," + pixel.getY()));
            }
        }, 60000);
        
        // diffuse aux autresclients seulement
        serveur.diffuserSauf(pixel, this);
    }

    /**
     * Traite une requête JOIN ou QUIT.
     * Pour JOIN : vérifie le pseudo, envoie l'état de la grille et la liste des joueurs.
     * Pour QUIT : diffuse la déconnexion et retire le client.
     * @param req requête reçue
     * @throws IOException si erreur réseau
     */
    private void traiterRequest(Request req) throws IOException {
        switch (req.getType()) {
	        case JOIN:
	            String nouveauPseudo = req.getPseudo();
	            if (serveur.getPseudos().contains(nouveauPseudo)) {
	                envoyer(new Request(RequestType.PSEUDO_PRIS, nouveauPseudo));
	                return;
	            }
	            pseudo = nouveauPseudo;
	            System.out.println(pseudo + " a rejoint la partie !");
	            for (String p : serveur.getPseudos()) {
	                if (!p.equals(pseudo)) {
	                    envoyer(new Request(RequestType.JOIN, p));
	                }
	            }
	            // envoyer l'état actuel de la grille au nouveau client
	            for (int x = 0; x < serveur.getGrille().getColonnes(); x++) {
	                for (int y = 0; y < serveur.getGrille().getLignes(); y++) {
	                    Pixel pix = serveur.getGrille().getPixel(x, y);
	                    if (!pix.getCouleur().equals("WHITE")) {
	                        if (serveur.getGrille().estVerrouille(x, y)) {
	                            pix.setTimestamp(serveur.getGrille().getTimestamp(x, y));
	                        } else {
	                            pix.setTimestamp(0);
	                        }
	                        pix.setJoueur("");
	                        envoyer(pix);
	                    }
	                }
	            }
	            serveur.diffuserSauf(req, this);
	            break;

            case QUIT:
                System.out.println(pseudo + " a quitté la partie !");
                serveur.diffuser(req);
                serveur.retirerClient(this);
                break;
		default:
			break;
        }
    }

    /**
     * Ferme le socket du client.
     * @throws IOException si erreur de fermeture
     */
    public void close() throws IOException {
        socket.close();
    }
}