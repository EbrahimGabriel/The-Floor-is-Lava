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
import model.InfoLabel;

public class GameViewManager {

    private static final int WINDOW_WIDTH = 960;
    private static final int WINDOW_HEIGHT = 540;
    private final String BACKGROUND_IMAGE_GAMEOVER = "model/resources/gameover.png";
    private Pane gamePane;
    private Scene gameScene;
    private Stage gameStage;
    private Stage lobbyStage;

    private boolean isUpPressed;
    private boolean isDownPressed;
    private boolean isLeftPressed;
    private boolean isRightPressed;
    private boolean isInvincible = false;

    private AnimationTimer gameTimer;

    private Timeline lavaSpawnTimeline;

    private ImageView[] playerLives;
    private int playerLife = 3;
    private int playerNum;
    private int winnerNum;
    private boolean gameEnded = false;
//    private int killCount = 2;

    // -- networking related --
    private GameClient client;
    private GameData[] players;
    private ImageView[] characters = new ImageView[4];


    public GameViewManager(GameClient client, GameData[] players) {
        initializeStage();
        this.client = client;
        this.players = players;
        //use game view's consumer to receive game data
        this.client.setGameConsumer(this::onMessageReceived);
        this.client.useGameConsumer();
        this.playerNum = this.client.getPlayerNum();
        createKeyListeners();
    }

    private void initializeStage() {
        gamePane = new Pane();
        gameScene = new Scene(gamePane, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameStage = new Stage();
        gameStage.setScene(gameScene);
        gameStage.setResizable(false);
    }

    public void createNewGame(Stage lobbyStage) {
        this.lobbyStage = lobbyStage;
        this.lobbyStage.hide();
        createBackground();
        createCharacters(players);
        createGameElements();
        createNormalTiles();
        if (client.getPlayerNum() == 0) createLavaTiles();
        createGameLoop();
        gameStage.show();
        for (ImageView character : characters) {
        	character.toFront();
        }
    }

    // set the background
    private void createBackground() {
        Image backgroundImage = new Image(getClass().getResourceAsStream("/view/resources/ingame_bg.png"));
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        gamePane.setBackground(new Background(background));
    }

    // set the UI
    private void createGameElements() {
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
            playerLives[i] = new ImageView(players[playerNum].character.getUrlLife());
            playerLives[i].setFitWidth(30);
            playerLives[i].setFitHeight(30);
            playerLives[i].setLayoutX(130 + (i * 50));
            playerLives[i].setLayoutY(40);
            gamePane.getChildren().add(playerLives[i]);
        }
        client.sendGameData(playerLife, (int) characters[playerNum].getLayoutX(), (int) characters[playerNum].getLayoutY());

//        Label killCountLabel = new Label("Kills:  " + killCount); // increment kill count soon when there is an implementation for killing enemies
//        killCountLabel.setFont(customFont);
//        killCountLabel.setTextFill(Color.YELLOW);
//        killCountLabel.setLayoutX(340); // Adjust these values to position the label
//        killCountLabel.setLayoutY(40); // Adjust these values to position the label
//        gamePane.getChildren().add(killCountLabel);
    }

    // create sprites for each player
    private void createCharacters(GameData[] players) {
    	for (GameData player : players) {
    		if (player.playerNum == 0) {
    			player.xpos = WINDOW_WIDTH/2 + 100;
        		player.ypos = WINDOW_HEIGHT/2 + 100;
    		}
    		else if (player.playerNum == 1) {
    			player.xpos = WINDOW_WIDTH/2 - 100;
        		player.ypos = WINDOW_HEIGHT/2 + 100;
    		}

    		else if (player.playerNum == 2) {
    			player.xpos = WINDOW_WIDTH/2 - 100;
        		player.ypos = WINDOW_HEIGHT/2 - 100;
    		}

    		else if (player.playerNum == 3) {
    			player.xpos = WINDOW_WIDTH/2 + 100;
        		player.ypos = WINDOW_HEIGHT/2 - 100;
    		}
    		characters[player.playerNum] = new ImageView(player.character.getUrl());
    		characters[player.playerNum].setLayoutX(player.xpos);
    		characters[player.playerNum].setLayoutY(player.ypos);
            gamePane.getChildren().add(characters[player.playerNum]);
            characters[player.playerNum].toFront();
    	}
    	client.sendGameData(playerLife, (int) characters[playerNum].getLayoutX(), (int) characters[playerNum].getLayoutY());
    }

    private void createGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                moveCharacter();
                checkIfElementsCollide();
                checkPlayerCollisions();
            }
        };

        gameTimer.start();
    }

    // moves the character, UPDATE TO ONLY AFFECT OWN PLAYER CHARACTER
    private void moveCharacter() {
//        int tileSize = 100;
        int topPadding = 100;
        int bottomPadding = 88;
        int leftPadding = 70;
        double rightPadding = 140.5;

        if (isUpPressed && !isDownPressed) {
            if (characters[playerNum].getLayoutY() - 5 >= topPadding) {
            	characters[playerNum].setLayoutY(characters[playerNum].getLayoutY() - 5);
            	client.sendGameData(playerLife, (int) characters[playerNum].getLayoutX(), (int) characters[playerNum].getLayoutY());
            }
        }

        if (!isUpPressed && isDownPressed) {
            if (characters[playerNum].getLayoutY() + 5 <= WINDOW_HEIGHT - bottomPadding - characters[playerNum].getFitHeight()) {
            	characters[playerNum].setLayoutY(characters[playerNum].getLayoutY() + 5);
            	client.sendGameData(playerLife, (int) characters[playerNum].getLayoutX(), (int) characters[playerNum].getLayoutY());
            }
        }

        if (isLeftPressed && !isRightPressed) {
            if (characters[playerNum].getLayoutX() - 5 >= leftPadding) {
            	characters[playerNum].setLayoutX(characters[playerNum].getLayoutX() - 5);
            	client.sendGameData(playerLife, (int) characters[playerNum].getLayoutX(), (int) characters[playerNum].getLayoutY());
            }
        }

        if (!isLeftPressed && isRightPressed) {
            if (characters[playerNum].getLayoutX() + 5 <= WINDOW_WIDTH - rightPadding - characters[playerNum].getFitWidth()) {
            	characters[playerNum].setLayoutX(characters[playerNum].getLayoutX() + 5);
            	client.sendGameData(playerLife, (int) characters[playerNum].getLayoutX(), (int) characters[playerNum].getLayoutY());
            }
        }
    }

    // create the game over screen, possibly needs update
    private void showGameOverPopup() {
    	if (gameEnded) return;
        // Load the image
        Image image = new Image(BACKGROUND_IMAGE_GAMEOVER);
        ImageView imageView = new ImageView(image);

        // Set the size of the image view
        imageView.setFitWidth(500);  // width of a typical dialog box
        imageView.setFitHeight(300);
        InfoLabel label = new InfoLabel(players[winnerNum].name + " wins!");
        label.setLayoutY(100);
        // Create the main menu button using GameButton
        GameButton lobbyButton = new GameButton("Back to Lobby");
        lobbyButton.setOnAction(event -> {
            client.useLobbyConsumer();
            gameStage.close(); // close the game stage
            lobbyStage.show();
        });

        lobbyButton.setTextFill(Color.DARKGRAY);

        // Create a spacer with VBox layout
        VBox spacer = new VBox();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox vbox = new VBox(10, spacer, lobbyButton);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(0, 0, 40, 0)); // Add 20px padding at the bottom

        // Create a StackPane to hold the image view and VBox
        StackPane stackPane = new StackPane(imageView, vbox, label);

        // Position the stack pane
        stackPane.setLayoutX(WINDOW_WIDTH / 2 - imageView.getFitWidth() / 2);
        stackPane.setLayoutY(WINDOW_HEIGHT / 2 - imageView.getFitHeight() / 2);

        Platform.runLater(() -> {
            gamePane.getChildren().add(stackPane);
            stackPane.toFront();  // bring the game over popup to the front
        });

        if (playerNum == 0) lavaSpawnTimeline.stop();
        gameEnded = true;
    }

    private void removeLife() {
        playerLife--;
        gamePane.getChildren().remove(playerLives[playerLife]);

        client.sendGameData(playerLife, (int) characters[playerNum].getLayoutX(), (int) characters[playerNum].getLayoutY());
        if(playerLife <= 0) {
    		gamePane.getChildren().remove(characters[playerNum]);
        	characters[playerNum].setVisible(false);
        	gameTimer.stop();
        }

    }

    private boolean checkWinner() {
    	int count = 0;
    	for (GameData player : players) {
    		if (player.lives == 0) count++;
    		else winnerNum = player.playerNum;
    	}
    	if (count == 3) return true;
    	return false;
    }

    private void checkIfElementsCollide() {
        if (isInvincible) {
            return;
        }

        ObservableList<Node> allNodes = gamePane.getChildren();

        for (Node node : allNodes) {
            if (node instanceof ImageView && node.getUserData() != null && node.getUserData().equals("lava_tile")) {
                if (characters[playerNum].getBoundsInParent().intersects(node.getBoundsInParent())) {
                	characters[playerNum].toFront();  // bring the character to the front
                    removeLife();
                    startInvincibilityPeriod();
                    break;
                }
            }
        }
    }

    private void checkPlayerCollisions() {
    	for (GameData player : players) {
    		if (playerNum == player.playerNum) continue;
    		if (characters[playerNum].getBoundsInParent().intersects(characters[player.playerNum].getBoundsInParent())) {
    			int moveX = 0, moveY = 0;
    			if (characters[playerNum].getLayoutX() < characters[player.playerNum].getLayoutX()) moveX = -5;
    			else moveX = 5;

    			if (characters[playerNum].getLayoutY() < characters[player.playerNum].getLayoutY()) moveY = -5;
    			else moveY = 5;

    			client.sendPushData(playerNum, (int) characters[playerNum].getLayoutX() + moveX, (int) characters[playerNum].getLayoutY() + moveY);
    			client.sendPushData(player.playerNum, (int) characters[player.playerNum].getLayoutX() - moveX, (int) characters[player.playerNum].getLayoutY() - moveY);
    		}
    	}
    }

    // start invincibility period for 1 second
    private void startInvincibilityPeriod() {
        isInvincible = true;

        Timeline invincibilityTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> isInvincible = false));
        invincibilityTimeline.play();
    }


    // spawns the normal tiles
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

    // spawns lava tiles randomly every 2 seconds
    private void createLavaTiles() {
        lavaSpawnTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> spawnLavaTile()));
        lavaSpawnTimeline.setCycleCount(Timeline.INDEFINITE);
        lavaSpawnTimeline.play();
    }

    private void spawnLavaTile() {
    	// Filter to find normal tiles
        List<ImageView> normalTiles = gamePane.getChildren().stream()
            .filter(node -> node instanceof ImageView && "normal_tile".equals(node.getUserData()))
            .map(node -> (ImageView) node)
            .collect(Collectors.toList());

        // Choose a random normal tile to replace
        int tileToReplace = (int) (Math.random() * normalTiles.size());

        // send tile data to replace!
        client.sendTileData(tileToReplace);
    }
    // replaces normal tile with lava tile
    private void replaceTile(int tileToReplace) {
        List<ImageView> normalTiles = gamePane.getChildren().stream()
                .filter(node -> node instanceof ImageView && "normal_tile".equals(node.getUserData()))
                .map(node -> (ImageView) node)
                .collect(Collectors.toList());

     // Debugging: Check if normalTiles is null or empty
        if (normalTiles == null || normalTiles.isEmpty() || normalTiles.size() == 0) {
            System.err.println("No normal tiles found to replace with lava tiles.");
            return; // Exit early if no normal tiles are found
        }

        ImageView normalTile = normalTiles.get(tileToReplace);

        // Debugging: Ensure normalTile is not null
        if (normalTile == null) {
            System.err.println("Selected normal tile is null.");
            return; // Exit early if the selected normal tile is null
        }

    	int tileSize = 100;
        Image lavaImage = new Image(getClass().getResourceAsStream("/view/resources/lava.png"));
        ImageView lavaTile = new ImageView(lavaImage);
        lavaTile.setFitWidth(tileSize);
        lavaTile.setFitHeight(tileSize);

        // Replace the normal tile with a lava tile
        lavaTile.setLayoutX(normalTile.getLayoutX());
        lavaTile.setLayoutY(normalTile.getLayoutY());
        lavaTile.setUserData("lava_tile"); // Set user data to identify as lava tile
        Platform.runLater(() -> {
            gamePane.getChildren().remove(normalTile);
            gamePane.getChildren().add(lavaTile);
        });
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

    // -- networking stuff --
    //here, only game data should be received
    private void onMessageReceived(GameData data) {
    	if (data.type.equals("game")) {
    		if (characters[data.playerNum] == null || players[data.playerNum] == null) return;
    		characters[data.playerNum].setLayoutX(data.xpos);
    		characters[data.playerNum].setLayoutY(data.ypos);
    		players[data.playerNum].lives = data.lives;
    		players[data.playerNum].xpos = data.xpos;
    		players[data.playerNum].ypos = data.ypos;

    		if (data.lives == 0) {
    			Platform.runLater(() -> {
        			gamePane.getChildren().remove(characters[data.playerNum]);
    			});
    			if (checkWinner()) {
                	client.sendGameEnd(winnerNum);
                }
    		}
    	}

    	else if (data.type.equals("push")) {
    		if (characters[data.playerNum] == null || players[data.playerNum] == null) return;
    		characters[data.playerNum].setLayoutX(data.xpos);
    		characters[data.playerNum].setLayoutY(data.ypos);
    		players[data.playerNum].xpos = data.xpos;
    		players[data.playerNum].ypos = data.ypos;
    	}

    	else if (data.type.equals("tile")) {
    		replaceTile(data.xpos); // im just using xpos to reuse
    	}

    	else if (data.type.equals("gameend")) {
            showGameOverPopup();
    	}
    }
}
