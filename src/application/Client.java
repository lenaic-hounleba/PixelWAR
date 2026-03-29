package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Représente un client du jeu Pixel War.
 * Gère la connexion TCP au serveur et l'envoi/réception des objets.
 */
public class Client {
    private String pseudo;
    private int nbPixels;
    private InetAddress adresse;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    /**
     * Constructeur du client.
     * @param pseudo pseudo du joueur
     */
    public Client(String pseudo) {
        this.pseudo   = pseudo;
        this.nbPixels = 0;
    }

    /** @return pseudo du joueur */
    public String getPseudo()  { return pseudo; }
    
    /** @return nombre de pixels posés par ce joueur */
    public int getNbPixels()   { return nbPixels; }
    
    /** Incrémente le compteur de pixels du joueur. */
    public void ajouterPixel() { nbPixels++; }
    
    /** Décrémente le compteur de pixels du joueur. */
    public void retirerPixel() { if (nbPixels > 0) nbPixels--; }

    /**
     * Établit la connexion TCP avec le serveur.
     * @param adr adresse IP du serveur
     * @param port port du serveur
     * @return true si connexion réussie, false sinon
     */
    public boolean connecter(String adr, int port) {
        try {
            adresse = InetAddress.getByName(adr);
            socket  = new Socket(adresse, port);
            output  = new ObjectOutputStream(socket.getOutputStream());
            input   = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Adresse inconnue !");
            return false;
        } catch (IOException e) {
            System.err.println("Erreur connexion : " + e.getMessage());
            return false;
        }
    }

    /**
     * Envoie un pixel au serveur.
     * @param p pixel à envoyer
     * @throws IOException si erreur réseau
     */
    public void envoyerPixel(Pixel p) throws IOException {
        output.writeObject(p);
    }

    /**
     * Reçoit un objet du serveur (Pixel, Request, Exception...).
     * @return l'objet reçu, ou null si erreur
     * @throws IOException si connexion perdue
     */
    public Object recevoir() throws IOException {
        try {
            return input.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur lecture objet");
            return null;
        }
    }

    /**
     * Envoie une requête JOIN au serveur pour annoncer la connexion.
     * @throws IOException si erreur réseau
     */
    public void envoyerJoin() throws IOException {
        output.writeObject(new Request(RequestType.JOIN, pseudo));
    }

    /**
     * Envoie une requête QUIT au serveur pour annoncer la déconnexion.
     * @throws IOException si erreur réseau
     */
    public void envoyerQuit() throws IOException {
        output.writeObject(new Request(RequestType.QUIT, pseudo));
    }

    /**
     * Déconnecte proprement le client du serveur.
     * Envoie QUIT puis ferme le socket.
     */
    public void deconnecter() {
        try {
            this.envoyerQuit();
            socket.close();
        } catch (IOException e) {
            System.err.println("Erreur déconnexion : " + e.getMessage());
        }
    }
}