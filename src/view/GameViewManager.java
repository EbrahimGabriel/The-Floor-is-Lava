package view;

import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.CHARACTER;
import model.GameButton;
import model.GameClient;
import model.GameData;

public class GameViewManager {

    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 540;
    private final String BACKGROUND_IMAGE_GAMEOVER = "model/resources/gameover.png";
    private Pane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Stage menuStage;
    private ImageView character;
    private boolean isUpPressed;
    private boolean isDownPressed;
    private boolean isLeftPressed;
    private boolean isRightPressed;
    private boolean isInvincible = false;

    private AnimationTimer gameTimer;

    private Timeline lavaSpawnTimeline;

    private ImageView[] playerLives;
    private int playerLife;
    private int killCount = 2;

    // -- networking related --
    private GameClient client;
    private GameData[] players;

    public GameViewManager(GameClient client, GameData[] players) {
        initializeStage();
        this.client = client;
        this.players = players;
        createKeyListeners();
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
        gamePane = new Pane();
        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.setResizable(false);
    }

    private void createBackground() {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/view/resources/ingame_bg.png"));
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        gamePane.setBackground(new Background(background));
    }

    private void createGameElements(CHARACTER chosenCharacter) {
        playerLife = 3;
        playerLives = new ImageView[3];

        Font customFont = Font.loadFont(getClass().getResourceAsStream("/view/resources/ArchitypeStedelijkW00.ttf"), 30);

        Label livesLabel = new Label("Lives:");
        livesLabel.setFont(customFont);
        livesLabel.setTextFill(Color.YELLOW);
        livesLabel.setLayoutX(70); // Adjust these values to position the label
        livesLabel.setLayoutY(40); // Adjust these values to position the label
        gamePane.getChildren().add(livesLabel);

        for(int i = 0; i < playerLives.length; i++) {
            playerLives[i] = new ImageView(chosenCharacter.getUrlLife());
            playerLives[i].setFitWidth(30);
            playerLives[i].setFitHeight(30);
            playerLives[i].setLayoutX(130 + (i * 50));
            playerLives[i].setLayoutY(40);
            gamePane.getChildren().add(playerLives[i]);
        }

        Label killCountLabel = new Label("Kills:  " + killCount); // increment kill count soon when there is an implementation for killing enemies
        killCountLabel.setFont(customFont);
        killCountLabel.setTextFill(Color.YELLOW);
        killCountLabel.setLayoutX(340); // Adjust these values to position the label
        killCountLabel.setLayoutY(40); // Adjust these values to position the label
        gamePane.getChildren().add(killCountLabel);

    }



    private void createCharacter(CHARACTER chosenCharacter) {
        character = new ImageView(chosenCharacter.getUrl());
        character.setLayoutX(WINDOW_WIDTH/2);
        character.setLayoutY(WINDOW_HEIGHT/2);
        gamePane.getChildren().add(character);
        character.toFront();
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
        int tileSize = 100;
        int topPadding = 100;
        int bottomPadding = 88;
        int leftPadding = 70;
        double rightPadding = 140.5;

        if (isUpPressed && !isDownPressed) {
            if (character.getLayoutY() - 5 >= topPadding) {
                character.setLayoutY(character.getLayoutY() - 5);
            }
        }

        if (!isUpPressed && isDownPressed) {
            if (character.getLayoutY() + 5 <= WINDOW_HEIGHT - bottomPadding - character.getFitHeight()) {
                character.setLayoutY(character.getLayoutY() + 5);
            }
        }

        if (isLeftPressed && !isRightPressed) {
            if (character.getLayoutX() - 5 >= leftPadding) {
                character.setLayoutX(character.getLayoutX() - 5);
            }
        }

        if (!isLeftPressed && isRightPressed) {
            if (character.getLayoutX() + 5 <= WINDOW_WIDTH - rightPadding - character.getFitWidth()) {
                character.setLayoutX(character.getLayoutX() + 5);
            }
        }
    }

    private void showGameOverPopup() {
        // Load the image
        Image image = new Image(BACKGROUND_IMAGE_GAMEOVER);
        ImageView imageView = new ImageView(image);

        // Set the size of the image view
        imageView.setFitWidth(500);  // width of a typical dialog box
        imageView.setFitHeight(300);

        // Create the main menu button using GameButton
        GameButton mainMenuButton = new GameButton("Main Menu");
        mainMenuButton.setOnAction(event -> {
            gameStage.close(); // close the game stage
            menuStage.show();
        });

        mainMenuButton.setTextFill(Color.DARKGRAY);

        // Create a spacer with VBox layout
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox vbox = new VBox(10, spacer, mainMenuButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(0, 0, 40, 0)); // Add 20px padding at the bottom

        // Create a StackPane to hold the image view and VBox
        StackPane stackPane = new StackPane(imageView, vbox);

        // Position the stack pane
        stackPane.setLayoutX(WINDOW_WIDTH / 2 - imageView.getFitWidth() / 2);
        stackPane.setLayoutY(WINDOW_HEIGHT / 2 - imageView.getFitHeight() / 2);

        Platform.runLater(() -> {
            gamePane.getChildren().add(stackPane);
            stackPane.toFront();  // bring the game over popup to the front
        });

        lavaSpawnTimeline.stop();
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
        if (isInvincible) {
            return;
        }

        ObservableList<Node> allNodes = gamePane.getChildren();

        for (Node node : allNodes) {
            if (node instanceof ImageView && node.getUserData() != null && node.getUserData().equals("lava_tile")) {
                if (character.getBoundsInParent().intersects(node.getBoundsInParent())) {
                    character.toFront();  // bring the character to the front
                    removeLife();
                    startInvincibilityPeriod();
                    break;
                }
            }
        }
    }


    private void startInvincibilityPeriod() {
        isInvincible = true;

        Timeline invincibilityTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> isInvincible = false));
        invincibilityTimeline.play();
    }

    public void createNewGame(Stage menuStage, CHARACTER chosenCharacter) {
        this.menuStage = menuStage;
        this.menuStage.hide();
        createBackground();
        createCharacter(chosenCharacter);
        createGameElements(chosenCharacter);
        createNormalTiles();
        createLavaTiles();
        createGameLoop();
        gameStage.show();
        character.toFront();
    }


    private void createLavaTiles() {
        lavaSpawnTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> spawnLavaTile()));
        lavaSpawnTimeline.setCycleCount(Timeline.INDEFINITE);
        lavaSpawnTimeline.play();
    }

    private void createNormalTiles() {
        int tileSize = 100;
        int topPadding = 100;
        int bottomPadding = 20;
        int leftPadding = 70;
        int rightPadding = 70;

        Image normalTileImage = new Image(getClass().getResourceAsStream("/view/resources/normal_tile.png"));

        int numCols = (WINDOW_WIDTH - leftPadding - rightPadding) / tileSize;
        int numRows = (WINDOW_HEIGHT - topPadding - bottomPadding) / tileSize;

        for (int col = 0; col < numCols; col++) {
            for (int row = 0; row < numRows; row++) {
                ImageView normalTile = new ImageView(normalTileImage);
                normalTile.setFitWidth(tileSize);
                normalTile.setFitHeight(tileSize);
                normalTile.setLayoutX(col * tileSize + leftPadding);
                normalTile.setLayoutY(row * tileSize + topPadding);
                normalTile.setUserData("normal_tile"); // Set user data to identify as normal tile
                gamePane.getChildren().add(normalTile);
            }
        }
    }


    private void spawnLavaTile() {
        int tileSize = 100;
        Image lavaImage = new Image(getClass().getResourceAsStream("/view/resources/lava.png"));
        ImageView lavaTile = new ImageView(lavaImage);
        lavaTile.setFitWidth(tileSize);
        lavaTile.setFitHeight(tileSize);

        // Filter to find normal tiles
        List<ImageView> normalTiles = gamePane.getChildren().stream()
            .filter(node -> node instanceof ImageView && "normal_tile".equals(node.getUserData()))
            .map(node -> (ImageView) node)
            .collect(Collectors.toList());

        // Debugging: Check if normalTiles is null or empty
        if (normalTiles == null || normalTiles.isEmpty()) {
            System.err.println("No normal tiles found to replace with lava tiles.");
            return; // Exit early if no normal tiles are found
        }

        // Choose a random normal tile to replace
        ImageView normalTile = normalTiles.get((int) (Math.random() * normalTiles.size()));

        // Debugging: Ensure normalTile is not null
        if (normalTile == null) {
            System.err.println("Selected normal tile is null.");
            return; // Exit early if the selected normal tile is null
        }

        // Replace the normal tile with a lava tile
        lavaTile.setLayoutX(normalTile.getLayoutX());
        lavaTile.setLayoutY(normalTile.getLayoutY());
        lavaTile.setUserData("lava_tile"); // Set user data to identify as lava tile
        gamePane.getChildren().remove(normalTile);
        gamePane.getChildren().add(lavaTile);
    }






}
