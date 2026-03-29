package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée de l'application JavaFX Pixel War.
 * Lance l'interface graphique à partir du fichier FXML.
 */
public class Main extends Application {
	
	/**
     * Initialise et affiche la fenêtre principale.
     * @param primaryStage fenêtre principale JavaFX
     * @throws Exception si le fichier FXML est introuvable
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
            getClass().getResource("inter_mini_prj.fxml")
        );
        Scene scene = new Scene(root);
        scene.getStylesheets().add(
        		getClass().getResource("pixelwar.css").toExternalForm()
        );
        primaryStage.setTitle("Pixel War || UBO L3 INFO IFA - UE Réseaux IHM - Mini projet ");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Point d'entrée Java — lance l'application JavaFX.
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        launch(args);
    }
}