package application;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextFlow;
import javafx.scene.control.ListView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

/**
 * Contrôleur JavaFX de l'interface Pixel War.
 * Gère les interactions utilisateur, la grille d'affichage et la communication avec le serveur.
 */
public class Controller {

	@FXML
	private GridPane colorGrid;
	@FXML
	private Canvas warGrid;
	@FXML
	private Rectangle selectedColor;
	@FXML
	private Label nbPixelsLibresLabel;
	@FXML
	private Label nbPixelsVerouillesLabel;
	@FXML
	private TextFlow connexion_steps;
	@FXML
	private TextFlow jouer_steps;
	@FXML
	private TextField pseudoFiled;
	@FXML
	private TextField hostField;
	@FXML
	private TextField portField;
	@FXML
	private Label connexion_etat;
	@FXML
	private ListView<String> listViewJoueurs;
	@FXML
	private Label ChanceTimer;
	@FXML
	private Label pixelTimer;
	@FXML
	private Label mesPixelsLabel;
	private Timeline timelineCooldown;
	private Timeline timelineEmbargo;
	@FXML
	private Label notif_label;
	@FXML
	private Button connexion_btn;
	@FXML
	private VBox grilleContainer;
	
	private static final int COLS = 18;
	private static final int ROWS = 17;
	
	/** Grille locale des pseudos des joueurs par pixel. */
	private String[][] grilleJoueurs = new String[COLS][ROWS];
	
	/** Grille locale d'affichage des couleurs. */
	private Color[][] grille = new Color[COLS][ROWS];
	
	/** File des timestamps des pixels sous embargo (ordre d'arrivée). */
	private java.util.Queue<Long> fileTimestamps = new java.util.LinkedList<>();

	private Color currentColor = Color.RED;
	private static final Color[] PALETTE = { Color.RED, Color.ORANGE, Color.YELLOW, Color.LIMEGREEN, Color.GREEN,
			Color.CYAN, Color.DEEPSKYBLUE, Color.BLUE, Color.PURPLE, Color.MAGENTA, Color.HOTPINK, Color.WHITE,
			Color.LIGHTGRAY, Color.GRAY, Color.DARKGRAY, Color.BLACK, Color.GOLD, Color.SADDLEBROWN, Color.TAN,
			Color.BEIGE, Color.CORAL, Color.TOMATO, Color.SALMON, Color.KHAKI, Color.OLIVE, Color.TEAL, Color.NAVY,
			Color.MAROON };

	private Client client;

	 /**
     * Initialisation appelée automatiquement au chargement du FXML.
     * Met en place la grille, la palette et les instructions.
     */
	@FXML
	public void initialize() {
		initGrille();
		buildColorGrid();
		dessinerGrille();
		gererClicCanvas();
		updateCurrentColor();
		updateNbPixels();
		afficherStepsConnexion();
		afficherStepsJouer();
		
		grilleContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
		    double size = Math.min(newVal.doubleValue() - 20, grilleContainer.getHeight() - 20);
		    if (size > 0) { warGrid.setWidth(size * COLS / Math.max(COLS, ROWS)); warGrid.setHeight(size * ROWS / Math.max(COLS, ROWS)); dessinerGrille(); }
		});
		
		grilleContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
		    double size = Math.min(grilleContainer.getWidth() - 20, newVal.doubleValue() - 20);
		    if (size > 0) { warGrid.setWidth(size * COLS / Math.max(COLS, ROWS)); warGrid.setHeight(size * ROWS / Math.max(COLS, ROWS)); dessinerGrille(); }
		});
		
		warGrid.widthProperty().addListener((obs, oldVal, newVal) -> dessinerGrille());
		
		warGrid.heightProperty().addListener((obs, oldVal, newVal) -> dessinerGrille());
	}

	/**
     * Initialise la grille locale en blanc et sans joueur.
     */
	private void initGrille() {
		for (int x = 0; x < COLS; x++)
			for (int y = 0; y < ROWS; y++) {
				grille[x][y] = Color.WHITE;
				grilleJoueurs[x][y] = "";
			}
	}

	/**
     * Construit la palette de couleurs dans le GridPane.
     * Chaque case est un Rectangle cliquable qui met à jour la couleur courante.
     */
	private void buildColorGrid() {
		int index = 0;
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 7; col++) {
				if (index >= PALETTE.length)
					break;

				Rectangle rect = new Rectangle(45, 25);
				rect.setFill(PALETTE[index]);

				final Color couleur = PALETTE[index];
				rect.setOnMouseClicked(e -> {
					currentColor = couleur;
					updateCurrentColor();
				});

				rect.setOnMouseEntered(e -> rect.setStroke(Color.BLACK));
				rect.setOnMouseExited(e -> rect.setStroke(null));

				colorGrid.add(rect, col, row);
				index++;
			}
		}
	}

	/**
     * Redessine toute la grille de pixels sur le Canvas.
     */
	private void dessinerGrille() {
	    GraphicsContext gc = warGrid.getGraphicsContext2D();
	    double cellW = warGrid.getWidth() / COLS;
	    double cellH = warGrid.getHeight() / ROWS;
	    for (int x = 0; x < COLS; x++) {
	        for (int y = 0; y < ROWS; y++) {
	            gc.setFill(grille[x][y]);
	            gc.fillRect(x * cellW, y * cellH, cellW, cellH);
	            gc.setStroke(Color.DARKGRAY);
	            gc.strokeRect(x * cellW, y * cellH, cellW, cellH);
	        }
	    }
	}

	/**
     * Gère le clic sur la grille de pixels.
     * Crée un pixel avec la couleur courante et l'envoie au serveur.
     */
	private void gererClicCanvas() {
		warGrid.setOnMouseClicked(e -> {
			int x = (int) (e.getX() / (warGrid.getWidth() / COLS));
			int y = (int) (e.getY() / (warGrid.getHeight() / ROWS));

			if (x >= 0 && x < COLS && y >= 0 && y < ROWS) {
				if (client == null) {
					notif_label.setText("Non connecté !");
					return;
				}
				Pixel p = new Pixel(x, y);
				p.setCouleur(currentColor.toString());
				p.setJoueur(client.getPseudo());

				try {
					client.envoyerPixel(p);
					
					notif_label.setText("");
				} catch (IOException ex) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Erreur");
					alert.setHeaderText(null);
					alert.setContentText("Erreur réseau, vérifiez votre connexion !");
					alert.showAndWait();
				}
			}
		});
	}

	 /**
     * Gère le clic sur le bouton Connexion.
     * Vérifie les champs, connecte le client et démarre l'écoute du serveur.
     */
	@FXML
	private void onConnexion() {
		String pseudo = pseudoFiled.getText();
		String ip = hostField.getText();
		String portText = portField.getText();

		if (pseudo.isEmpty() || ip.isEmpty() || portText.isEmpty()) {
		    Alert alert = new Alert(Alert.AlertType.ERROR);
		    alert.setTitle("Erreur");
		    alert.setHeaderText(null);
		    alert.setContentText("Veuillez remplir tous les champs !");
		    alert.showAndWait();
		    return;
		}

		int port;
		try {
		    port = Integer.parseInt(portText);
		} catch (NumberFormatException e) {
		    Alert alert = new Alert(Alert.AlertType.ERROR);
		    alert.setTitle("Erreur");
		    alert.setHeaderText(null);
		    alert.setContentText("Le port doit être un nombre !");
		    alert.showAndWait();
		    return;
		}

		client = new Client(pseudo);
		boolean ok = client.connecter(ip, port);

		if (ok) {
			try {
				client.envoyerJoin();
				listViewJoueurs.getItems().add(pseudo);
				connexion_etat.setText("Connecté !");
				connexion_btn.setDisable(true);
				//griser les champs apres conn
				pseudoFiled.setDisable(true);
				hostField.setDisable(true);
				portField.setDisable(true);
				pseudoFiled.setStyle("-fx-opacity: 0.5;");
				hostField.setStyle("-fx-opacity: 0.5;");
				portField.setStyle("-fx-opacity: 0.5;");
				
				ecouterServeur();
			} catch (IOException e) {
				connexion_etat.setText("Erreur JOIN !");
			}
		} else {
			connexion_etat.setText("Échec connexion !");
		}
	}

	 /**
     * Lance un thread d'écoute des messages du serveur.
     * Traite les Pixel, exceptions et requêtes reçus.
     */
	private void ecouterServeur() {
		new Thread(() -> {
			try {
				while (true) {
					Object obj = client.recevoir();

					if (obj instanceof Pixel) {
						Pixel p = (Pixel) obj;
						Platform.runLater(() -> {
							grille[p.getX()][p.getY()] = Color.web(p.getCouleur());
							dessinerGrille();
							//cooldown seulement si c'est mon pixel confirmé
							if (!p.getJoueur().isEmpty() && p.getJoueur().equals(client.getPseudo())) {
							    lancerTimerCooldown();
							}
							grilleJoueurs[p.getX()][p.getY()] = p.getJoueur();
							
							//ajouter le timestamp dans la file si pixel colorié
							if (!p.getCouleur().equals("WHITE")) {
								if (p.getTimestamp() > 0) {
								    fileTimestamps.add(p.getTimestamp());
								}
							}
							updateNbPixels();
						});
					} else if (obj instanceof PixelVerrouilleException) {
						PixelVerrouilleException ex = (PixelVerrouilleException) obj;
						Platform.runLater(() -> {
						    notif_label.setText("Pixel verrouillé !");
						    new Timeline(new KeyFrame(Duration.seconds(3), ev -> notif_label.setText(""))).play();
						    mettreAJourTimerEmbargo(ex.getTimestamp());
						});
					} else if (obj instanceof CooldownException) {
					    Platform.runLater(() -> {
					        notif_label.setText("Attends le cooldown !");
					        new Timeline(new KeyFrame(Duration.seconds(3), ev -> notif_label.setText(""))).play();
					    });
					} else if (obj instanceof Request) {
						Request req = (Request) obj;
						Platform.runLater(() -> traiterRequest(req));
					}
				}
			} catch (IOException e) {
				Platform.runLater(() -> {
					connexion_etat.setText("Serveur injoignable !");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Erreur");
					alert.setHeaderText(null);
					alert.setContentText("Le serveur n'est plus joignable !");
					alert.showAndWait();
					// réactiver les champs
					connexion_btn.setDisable(false);
					pseudoFiled.setDisable(false);
					hostField.setDisable(false);
					portField.setDisable(false);
					pseudoFiled.setStyle("");
					hostField.setStyle("");
					portField.setStyle("");
					client = null;
					listViewJoueurs.getItems().clear();
					fileTimestamps.clear();
					if (timelineCooldown != null) timelineCooldown.stop();
					if (timelineEmbargo != null) timelineEmbargo.stop();
					ChanceTimer.setText("00:30");
					pixelTimer.setText("01:00");
					initGrille();
					dessinerGrille();
					updateNbPixels();
				});
			}
		}).start();
	}

	 /**
     * Traite les requêtes reçues du serveur (JOIN, QUIT, LIBERE, PSEUDO_PRIS).
     * @param req requête à traiter
     */
	private void traiterRequest(Request req) {
		switch (req.getType()) {
		case JOIN:
			listViewJoueurs.getItems().add(req.getPseudo());
			break;
		case QUIT:
			listViewJoueurs.getItems().remove(req.getPseudo());
			break;
		case LIBERE:
		    fileTimestamps.poll();
		    mettreAJourTimerEmbargo();
		    updateNbPixels();
		    break;
		case PSEUDO_PRIS:
		    Platform.runLater(() -> {
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setTitle("Erreur");
		        alert.setHeaderText(null);
		        alert.setContentText("Ce pseudo est déjà utilisé !");
		        alert.showAndWait();
		        //réactiver les champs
		        connexion_btn.setDisable(false);
		        pseudoFiled.setDisable(false);
		        hostField.setDisable(false);
		        portField.setDisable(false);
		        pseudoFiled.setStyle("");
		        hostField.setStyle("");
		        portField.setStyle("");
		        client = null;
		    });
		    break;
		}
	}

	/** Met à jour le rectangle de couleur choisie. */
	private void updateCurrentColor() {
		selectedColor.setFill(currentColor);
	}

	/**
     * Met à jour les compteurs de pixels (libres, verrouillés, mes pixels).
     */
	private void updateNbPixels() {
		int libres = 0;
		for (int x = 0; x < COLS; x++)
			for (int y = 0; y < ROWS; y++)
				if (grille[x][y].equals(Color.WHITE))
					libres++;

		nbPixelsLibresLabel.setText(String.valueOf(libres));
		nbPixelsVerouillesLabel.setText(String.valueOf(fileTimestamps.size()));

		//compter mes pixels
		int mesPixels = 0;
		for (int x = 0; x < COLS; x++)
			for (int y = 0; y < ROWS; y++)
				if (client != null && grilleJoueurs[x][y].equals(client.getPseudo()))
					mesPixels++;
		mesPixelsLabel.setText(String.valueOf(mesPixels));
	}

	/** Affiche les étapes de connexion dans le TextFlow. */
	private void afficherStepsConnexion() {
	    String[] lignes = {
	        "1. Entre ton pseudo\n",
	        "2. Saisis l'adresse serveur\n",
	        "3. Saisis le port\n",
	        "4. Clique sur Connexion\n"
	    };
	    for (String ligne : lignes) {
	        javafx.scene.text.Text t = new javafx.scene.text.Text(ligne);
	        t.setFill(javafx.scene.paint.Color.web("#c9d1d9"));
	        t.setFont(javafx.scene.text.Font.font(15));
	        connexion_steps.getChildren().add(t);
	    }
	}

	/** Affiche les règles du jeu dans le TextFlow. */
	private void afficherStepsJouer() {
	    String[] lignes = {
	        "• Choisis une couleur\n",
	        "• Clique sur un pixel libre\n",
	        "• Attends le cooldown\n",
	        "• Le + de pixels gagne !\n"
	    };
	    for (String ligne : lignes) {
	        javafx.scene.text.Text t = new javafx.scene.text.Text(ligne);
	        t.setFill(javafx.scene.paint.Color.web("#c9d1d9"));
	        t.setFont(javafx.scene.text.Font.font(15));
	        jouer_steps.getChildren().add(t);
	    }
	}

	/**
     * Lance le timer de cooldown joueur (30 secondes).
     * Empêche le joueur de poser un pixel pendant ce délai.
     */
	private void lancerTimerCooldown() {
		if (timelineCooldown != null)
			timelineCooldown.stop();
		final int[] secondes = { 30 };
		timelineCooldown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
			secondes[0]--;
			ChanceTimer.setText(String.format("00:%02d", secondes[0]));
			if (secondes[0] <= 0) {
				ChanceTimer.setText("00:30");
			}
		}));
		timelineCooldown.setCycleCount(30);
		timelineCooldown.play();
	}

	/**
     * Met à jour le timer d'embargo en utilisant le timestamp le plus ancien de la file.
     */
	private void mettreAJourTimerEmbargo() {
	    if (timelineEmbargo != null) timelineEmbargo.stop();
	    pixelTimer.setText("01:00");
	}

	 /**
     * Lance le timer d'embargo avec un timestamp précis.
     * Affiche le temps restant avant que le pixel soit de nouveau écrasable.
     * @param timestamp heure de verrouillage du pixel en millisecondes
     */
	private void mettreAJourTimerEmbargo(long timestamp) {
	    if (timelineEmbargo != null) timelineEmbargo.stop();
	    timelineEmbargo = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
	        long restant = 60000 - (System.currentTimeMillis() - timestamp);
	        if (restant <= 0) {
	            pixelTimer.setText("01:00");
	            timelineEmbargo.stop();
	        } else {
	            int sec = (int)(restant / 1000);
	            pixelTimer.setText(String.format("00:%02d", sec));
	        }
	    }));
	    timelineEmbargo.setCycleCount(Timeline.INDEFINITE);
	    timelineEmbargo.play();
	}

	 /**
     * Gère le clic sur le bouton Déconnexion.
     * Déconnecte le client et réinitialise l'interface.
     */
	@FXML
	private void onDeconnexion() {
		if (client != null) {
			client.deconnecter();
			client = null;
		}
		// réinitialise l'interface
		connexion_etat.setText("DECONNECTE");
		connexion_btn.setDisable(false);
		listViewJoueurs.getItems().clear();
		pseudoFiled.setDisable(false);
		hostField.setDisable(false);
		portField.setDisable(false);
		pseudoFiled.setStyle("");
		hostField.setStyle("");
		portField.setStyle("");
		initGrille();
		
		dessinerGrille();
		updateNbPixels();
		fileTimestamps.clear();
		nbPixelsVerouillesLabel.setText("0"); 
		if (timelineCooldown != null) timelineCooldown.stop();
		if (timelineEmbargo != null) timelineEmbargo.stop();
		ChanceTimer.setText("00:30");
		pixelTimer.setText("01:00");
	}
}