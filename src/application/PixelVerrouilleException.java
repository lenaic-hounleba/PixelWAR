package application;

/**
 * Exception levée quand un joueur tente de colorier un pixel sous embargo.
 * Contient le timestamp de verrouillage pour calculer le temps restant.
 */
public class PixelVerrouilleException extends Exception {
    private static final long serialVersionUID = 1L;
    private long timestamp; 

    /**
     * Constructeur.
     * @param timestamp heure de verrouillage du pixel en millisecondes
     */
    public PixelVerrouilleException(long timestamp) {
        super("Ce pixel est déjà réservé !");
        this.timestamp = timestamp;
    }

    /** @return timestamp de verrouillage en millisecondes */
    public long getTimestamp() { 
    	return timestamp; 
    }
}