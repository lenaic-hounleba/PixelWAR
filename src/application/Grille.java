package application;

/**
 * Représente la grille de pixels du jeu Pixel War.
 * Gère l'état des pixels, leur verrouillage (embargo) et les timestamps.
 */
public class Grille {
	private Pixel[][] pixels;
	private int colonnes;
	private int lignes;
	private boolean[][] verrouille;
	private long[][] timestamps;
	
	/**
     * Constructeur de la grille.
     * Initialise tous les pixels en blanc et les verrous à false.
     * @param col nombre de colonnes
     * @param lign nombre de lignes
     */
	public Grille(int col, int lign)
	{
		this.colonnes = col;
		this.lignes = lign;
		this.pixels = new Pixel[col][lign];
		this.verrouille = new boolean[col][lign];
		this.timestamps = new long[col][lign];
		for(int i = 0; i < col; i++)
		{
	        for(int j = 0; j < lign; j++)
	        {
	        	this.pixels[i][j] = new Pixel(i, j);
	        }
		}
	}
	
	/**
     * Retourne le pixel aux coordonnées (x, y).
     * @param x coordonnée x
     * @param y coordonnée y
     * @return le pixel, ou null si hors limites
     */
	public Pixel getPixel(int x, int y) {
	    if(x >= 0 && x < colonnes && y >= 0 && y < lignes)
	        return pixels[x][y];
	    return null;
	}
	
	/**
     * Retourne le nombre de pixels libres (couleur WHITE).
     * @return nombre de pixels libres
     */
	public int getNbPixelsLibre()
	{
		int nb = 0;
		for(int i=0; i<this.colonnes; i++)
		{
			for(int j=0; j<this.lignes; j++)
			{
				if(this.pixels[i][j].getCouleur().equals("WHITE"))
				{
					nb++;
				}
			}
		}
		return nb;
	}
	
	 /**
     * Retourne le nombre de pixels actuellement verrouillés (sous embargo).
     * @return nombre de pixels verrouillés
     */
	public int getNbPixelsVerouilles() {
	    int nb = 0;
	    for(int i=0; i<this.colonnes; i++)
	        for(int j=0; j<this.lignes; j++)
	            if(verrouille[i][j])
	                nb++;
	    return nb;
	}
	
	/**
     * Modifie la couleur et le joueur d'un pixel.
     * @param x coordonnée x
     * @param y coordonnée y
     * @param color couleur en format String (ex: "#FF0000")
     * @param player pseudo du joueur
     * @return true si succès, false si pixel hors limites
     */
	public boolean setPixel(int x, int y, String color, String player)
	{
		Pixel pik = this.getPixel(x, y);
		if(pik == null)
		{
			return false;
		}
		pik.setCouleur(color);
		pik.setJoueur(player);
		return true;
	}
	
	/**
     * Remet un pixel en blanc et efface le joueur associé.
     * @param x coordonnée x
     * @param y coordonnée y
     */
	public void resetPixel(int x, int y) {
	    Pixel pik = this.getPixel(x, y);
	    if(pik != null) {
	        pik.resetPixel();
	    }
	}
	
	/**
     * Retourne le nombre de colonnes de la grille.
     * @return nombre de colonnes
     */
	public int getColonnes() {
	    return colonnes;
	}
	
	 /**
     * Retourne le nombre de lignes de la grille.
     * @return nombre de lignes
     */
	public int getLignes() {
	    return lignes;
	}
	
	/**
     * Vérifie si un pixel est verrouillé (sous embargo).
     * @param x coordonnée x
     * @param y coordonnée y
     * @return true si verrouillé, false sinon
     */
	public boolean estVerrouille(int x, int y) {
	    Pixel pik = getPixel(x, y);
	    if (pik == null) return false;
	    return verrouille[x][y];
	}

	/**
     * Verrouille un pixel — le rend indisponible pendant l'embargo.
     * @param x coordonnée x
     * @param y coordonnée y
     */
	public void verrouiller(int x, int y) {
	    verrouille[x][y] = true;
	}

	/**
     * Déverrouille un pixel après la fin de l'embargo (1 minute).
     * @param x coordonnée x
     * @param y coordonnée y
     */
	public void deverrouiller(int x, int y) {
	    verrouille[x][y] = false;
	}
	
	/**
     * Enregistre le timestamp de verrouillage d'un pixel.
     * @param x coordonnée x
     * @param y coordonnée y
     * @param t timestamp en millisecondes
     */
	public void setTimestamp(int x, int y, long t) { 
		timestamps[x][y] = t;
	}
	
	 /**
     * Retourne le timestamp de verrouillage d'un pixel.
     * @param x coordonnée x
     * @param y coordonnée y
     * @return timestamp en millisecondes
     */
	public long getTimestamp(int x, int y) { 
		return timestamps[x][y]; 
	}
}
