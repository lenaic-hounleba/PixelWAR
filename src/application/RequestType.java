package application;

/**
 * Types de requêtes échangées entre client et serveur.
 * JOIN : un joueur rejoint la partie.
 * QUIT : un joueur quitte la partie.
 * LIBERE : un pixel est libéré après embargo.
 * PSEUDO_PRIS : le pseudo choisi est déjà utilisé.
 */
public enum RequestType {
    JOIN,
    QUIT,
    LIBERE,
    PSEUDO_PRIS
}