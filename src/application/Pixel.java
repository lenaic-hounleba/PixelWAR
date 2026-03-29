package application;

import java.io.Serializable;

/**
 * Représente un pixel de la grille du jeu Pixel War.
 * Chaque pixel possède une position, une couleur, un joueur associé et un timestamp de verrouillage.
 * Implémente Serializable pour pouvoir être envoyé via ObjectOutputStream.
 */
public class Pixel implements Serializable {
	private static final long serialVersionUID = 1L; 
	private int x;
	private int y;
	private String couleur;
	private String joueur;
	private long timestamp;
	
	/**
     * Constructeur du pixel.
     * Initialise le pixel en blanc sans joueur associé.
     * @param xx coordonnée x
     * @param yy coordonnée y
     */
	public Pixel(int xx, int yy)
	{
		this.x = xx;
		this.y = yy;
		this.couleur = "WHITE";
		this.joueur = "";
	}
	
	/**
     * Retourne le timestamp de verrouillage du pixel.
     * @return timestamp en millisecondes
     */
	public long getTimestamp() { 
		return timestamp; 
	}
	
	/**
     * Définit le timestamp de verrouillage du pixel.
     * @param t timestamp en millisecondes
     */
	public void setTimestamp(long t) { 
		this.timestamp = t; 
	}
	
	/**
     * Retourne la coordonnée x du pixel.
     * @return coordonnée x
     */
	public int getX()
	{
		return this.x;
	}
	
	/**
     * Retourne la coordonnée y du pixel.
     * @return coordonnée y
     */
	public int getY() 
	{
		return this.y;
	}
	
	/**
     * Retourne le pseudo du joueur associé au pixel.
     * @return pseudo du joueur
     */
	public String getJoueur()
	{
		return this.joueur;
	}
	
	 /**
     * Retourne la couleur du pixel en format String.
     * @return couleur (ex: "WHITE" ou "#FF0000")
     */
	public String getCouleur()
	{
		return this.couleur;
	}
	
	/**
     * Définit la coordonnée x du pixel.
     * @param new_x nouvelle coordonnée x
     */
	public void setX(int new_x)
	{
		this.x = new_x;
	}
	
	 /**
     * Définit la coordonnée y du pixel.
     * @param new_y nouvelle coordonnée y
     */
	public void setY(int new_y)
	{
		this.y = new_y;
	}
	
	/**
     * Définit la couleur du pixel.
     * @param color couleur en format String (ex: "#FF0000")
     */
	public void setCouleur(String color)
	{
		this.couleur = color;
	}
	
	 /**
     * Définit le joueur associé au pixel.
     * @param player pseudo du joueur
     */
	public void setJoueur(String player)
	{
		this.joueur = player;
	}
	
	/**
     * Remet le pixel à son état initial : couleur blanche et sans joueur.
     */
	public void resetPixel()
	{
		this.couleur = "WHITE";
		this.joueur = "";
	}
}
