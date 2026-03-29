package application;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Serveur du jeu Pixel War.
 * Accepte les connexions des clients et gère la grille partagée.
 */
public class Serveur {
    private ServerSocket serverSocket;
    private Grille grille;
    private List<ClientSocket> clients;

    /**
     * Constructeur du serveur.
     * Initialise la grille et démarre l'écoute sur le port donné.
     * @param port port d'écoute du serveur
     */
    public Serveur(int port) {
        this.grille  = new Grille(18, 17);
        this.clients = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Serveur démarré sur le port " + port);
        } catch (IOException e) {
            System.err.println("Erreur création serveur : " + e.getMessage());
        }
    }

    /**
     * Démarre la boucle d'acceptation des clients.
     * Chaque client est géré dans un thread séparé via ClientSocket.
     * @throws IOException si erreur réseau
     */
    public void demarrer() throws IOException {
        while (true) {
            Socket socketClient = serverSocket.accept();
            System.out.println("Nouveau client connecté !");
            ClientSocket cs = new ClientSocket(socketClient, this);
            clients.add(cs);
            new Thread(cs).start();
        }
    }

    /**
     * Diffuse un objet à tous les clients connectés.
     * @param obj objet à diffuser
     */
    public void diffuser(Object obj) {
        for (ClientSocket cs : clients) {
            try {
                cs.envoyer(obj);
            } catch (IOException e) {
                System.err.println("Erreur diffusion : " + e.getMessage());
            }
        }
    }

    /**
     * Retire un client de la liste des connectés.
     * @param cs client à retirer
     */
    public void retirerClient(ClientSocket cs) {
        clients.remove(cs);
    }
    
    /**
     * Diffuse un objet à tous les clients sauf l'expéditeur.
     * @param obj objet à diffuser
     * @param expediteur client à exclure
     */
    public void diffuserSauf(Object obj, ClientSocket expediteur) {
        for (ClientSocket cs : clients) {
            if (cs != expediteur) {
                try {
                    cs.envoyer(obj);
                } catch (IOException e) {
                    System.err.println("Erreur diffusion : " + e.getMessage());
                }
            }
        }
    }

    /** @return la grille partagée du jeu */
    public Grille getGrille() { 
    	return grille; 
    }
    
    /**
     * Retourne la liste des pseudos des joueurs connectés.
     * @return liste des pseudos
     */
    public List<String> getPseudos() {
        List<String> pseudos = new ArrayList<>();
        for (ClientSocket cs : clients) {
            if (cs.getPseudo() != null) {
                pseudos.add(cs.getPseudo());
            }
        }
        return pseudos;
    }

    /**
     * Point d'entrée du serveur.
     * Le port peut être passé en argument, sinon 7777 par défaut.
     * @param args args[0] = port (optionnel)
     */
    public static void main(String[] args) {
    	int port = (args.length > 0) ? Integer.parseInt(args[0]) : 7777;
    	Serveur serveur = new Serveur(port);
        try {
            serveur.demarrer();
        } catch (IOException e) {
            System.err.println("Erreur serveur : " + e.getMessage());
        }
    }
}