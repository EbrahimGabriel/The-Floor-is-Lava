package view;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.CHARACTER;
import model.GameSubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.application.Platform;
import javafx.scene.text.Text;

public class GameViewManager {
    private AnchorPane gamePane;
    private Scene gameScene;
    private Stage gameStage;

    private static final int GAME_WIDTH = 700;
    private static final int GAME_HEIGHT = 500;
    private Stage menuStage;
    private ImageView character;
    private boolean isUpPressed;
    private boolean isDownPressed;
    private boolean isLeftPressed;
    private boolean isRightPressed;

    private AnimationTimer gameTimer;
    private boolean chatVisible = false;

    private GridPane gridPane1;
    private GridPane gridPane2;
    private GridPane chatPane;
    private TextField chatInput;
    private Label chatLog;
    private final static String BACKGROUND_IMAGE = "view/resources/background.jpg";
    private final String BACKGROUND_IMAGE_GAMEOVER = "model/resources/red_button_free.png";
	private final static String LAVA_TILE = "view/resources/lava.png";

    private ImageView[] lavaTiles;
    Random randomPositionGenerator;

    private ImageView[] playerLives;
    private int playerLife;

	

    public GameViewManager() {
        initializeStage();
        createKeyListeners();
        randomPositionGenerator = new Random();
    }

    private void createKeyListeners() {
        gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        isUpPressed = true;
                        break;
                    case S:
                        isDownPressed = true;
                        break;
                    case A:
                        isLeftPressed = true;
                        break;
                    case D:
                        isRightPressed = true;
                        break;
                    case SLASH:
                        chatVisible = !chatVisible;
                        chatPane.setVisible(!chatPane.isVisible());
                        chatInput.requestFocus();
                        break;
                    case ENTER:
                        if (chatVisible) {
                            String message = chatInput.getText();
                            chatLog.setText(chatLog.getText() + "\n" + message);
                            gamePane.requestFocus();
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W:
                        isUpPressed = false;
                        break;
                    case S:
                        isDownPressed = false;
                        break;
                    case A:
                        isLeftPressed = false;
                        break;
                    case D:
                        isRightPressed = false;
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initializeStage() {
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
    }

    public void createNewGame(Stage menuStage, CHARACTER chosenCharacter) {
        this.menuStage = menuStage;
        this.menuStage.hide();
        createBackground();
        createChat();
        createCharacter(chosenCharacter);
        createGameElements(chosenCharacter);
        createGameLoop();
        gameStage.show();
    }

    private void createGameElements(CHARACTER chosenCharacter) {
        playerLife = 3;
        playerLives = new ImageView[3];

        for(int i = 0; i < playerLives.length; i++) {
            playerLives[i] = new ImageView(chosenCharacter.getUrlLife());
            playerLives[i].setFitWidth(30);
            playerLives[i].setFitHeight(30);
            playerLives[i].setLayoutX(455 + (i * 50));
            playerLives[i].setLayoutY(80);
            gamePane.getChildren().add(playerLives[i]);
        }

        lavaTiles = new ImageView[3];
        for (int i = 0; i < lavaTiles.length; i++) {
            lavaTiles[i] = new ImageView(LAVA_TILE);
            setGameElementPosition(lavaTiles[i]);
            gamePane.getChildren().add(lavaTiles[i]);
        }
    }

    private void setGameElementPosition(ImageView image) {
        image.setLayoutX(randomPositionGenerator.nextInt(700));
        image.setLayoutY(randomPositionGenerator.nextInt(500));
    }

    private void createCharacter(CHARACTER chosenCharacter) {
        character = new ImageView(chosenCharacter.getUrl());
        character.setLayoutX(GAME_WIDTH/2);
        character.setLayoutY(GAME_HEIGHT/2);
        gamePane.getChildren().add(character);
    }

    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveCharacter();
                checkIfElementsCollide();
            }
        };

        gameTimer.start();
    }

    private void moveCharacter() {
        if (isUpPressed && !isDownPressed) {
            character.setLayoutY(character.getLayoutY() - 5);
        }

        if (!isUpPressed && isDownPressed) {
            character.setLayoutY(character.getLayoutY() + 5);
        }

        if (isLeftPressed && !isRightPressed) {
            character.setLayoutX(character.getLayoutX() - 5);
        }

        if (!isLeftPressed && isRightPressed) {
            character.setLayoutX(character.getLayoutX() + 5);
        }
    }

    private void createBackground() {
        gridPane1 = new GridPane();
        gridPane2 = new GridPane();

        for (int i = 0; i < 12; i++) {
            ImageView backgroundImage1 = new ImageView(BACKGROUND_IMAGE);
            ImageView backgroundImage2 = new ImageView(BACKGROUND_IMAGE);
            GridPane.setConstraints(backgroundImage1, i%3, i/3);
            GridPane.setConstraints(backgroundImage2, i%3, i/3);
            gridPane1.getChildren().add(backgroundImage1);
            gridPane2.getChildren().add(backgroundImage2);
        }

        gridPane2.setLayoutY(-1024);
        gamePane.getChildren().addAll(gridPane1, gridPane2);
    }

    private void createChat() {
        chatPane = new GridPane();
        chatInput = new TextField();
        chatLog = new Label();
        chatPane.getChildren().addAll(chatLog, chatInput);
        chatPane.setLayoutX(0);
        chatPane.setLayoutY(0);
        chatPane.setVisible(false);
        gamePane.getChildren().add(chatPane);
    }

    private void showGameOverPopup() {
		// Load the image
		Image image = new Image(BACKGROUND_IMAGE_GAMEOVER);
		ImageView imageView = new ImageView(image);
	
		// Set the size of the image view
		imageView.setFitWidth(400);  // width of a typical dialog box
		imageView.setFitHeight(200); 

		Text gameOverText = new Text("Game Over!");
		Button mainMenuButton = new Button("Main Menu");
		mainMenuButton.setOnAction(event -> {
			gameStage.close(); // close the game stage
        	menuStage.show(); 
		});

		// Create a VBox to hold the text and button
		// ImageView imageView = new ImageView(); // Declare and initialize the imageView variable
		VBox vbox = new VBox(10, gameOverText, mainMenuButton);
		vbox.setAlignment(Pos.CENTER);

		// Create a StackPane to hold the image view and VBox
		StackPane stackPane = new StackPane(imageView, vbox);

		// Position the stack pane
		stackPane.setLayoutX(GAME_WIDTH / 2 - imageView.getFitWidth() / 2);
		stackPane.setLayoutY(GAME_HEIGHT / 2 - imageView.getFitHeight() / 2);

		// Add the stack pane to the game pane
		Platform.runLater(() -> gamePane.getChildren().add(stackPane));
	}

    private void removeLife() {
        playerLife--;
        gamePane.getChildren().remove(playerLives[playerLife]);
        if(playerLife <= 0) {
            gameTimer.stop();
            showGameOverPopup();
        }
    }

    private void checkIfElementsCollide() {
        for (int i = 0; i < lavaTiles.length; i++) {
            if (character.getBoundsInParent().intersects(lavaTiles[i].getBoundsInParent())) {
                removeLife();
                setGameElementPosition(lavaTiles[i]);
            }
        }
    }
}