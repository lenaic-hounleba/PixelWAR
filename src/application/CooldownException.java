// CooldownException.java
package application;

/**
 * Exception levée quand un joueur tente de poser un pixel
 * alors que son cooldown de 30 secondes n'est pas écoulé.
 */
public class CooldownException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Constructeur. */
    public CooldownException() {
        super("Attends la fin de ton cooldown !");
    }
}