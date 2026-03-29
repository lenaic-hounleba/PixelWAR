package application;

import java.io.Serializable;

/**
 * Représente une requête envoyée entre client et serveur.
 * Utilisée pour les actions JOIN et QUIT.
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private RequestType type;
    private String pseudo;

    /**
     * Constructeur de la requête.
     * @param type type de la requête (JOIN, QUIT...)
     * @param pseudo pseudo du joueur concerné
     */
    public Request(RequestType type, String pseudo) {
        this.type   = type;
        this.pseudo = pseudo;
    }

    /** @return type de la requête */
    public RequestType getType() 
    { 
    	return type; 
    }
    
    /** @return pseudo du joueur */
    public String getPseudo() 
    { 
    	return pseudo; 
    }
}