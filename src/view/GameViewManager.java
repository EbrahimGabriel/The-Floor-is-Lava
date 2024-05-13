package view;

import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.CHARACTER;
import model.SmallInfoLabel;
//this is the main view for the gameplay loop
public class GameViewManager {
	//base stuff for the screen
	private AnchorPane gamePane;
	private Scene gameScene;
	private Stage gameStage;

	private static final int GAME_WIDTH = 700;
	private static final int GAME_HEIGHT = 500;
	//info to return back to menu and chosen character to display
	private Stage menuStage;
	private ImageView character;
	//for tracking inputs
	private boolean isUpPressed;
	private boolean isDownPressed;
	private boolean isLeftPressed;
	private boolean isRightPressed;

	private AnimationTimer gameTimer;
	private boolean chatVisible = false;

	private GridPane gridPane1;
	private GridPane gridPane2;
	//--- chat related ---
	private GridPane chatPane;
	private TextField chatInput;
	private Label chatLog;
	//--------------------
	private final static String BACKGROUND_IMAGE = "view/resources/background.jpg";
	private final static String LAVA_TILE = "view/resources/lava.png";

	private ImageView[] lavaTiles;
	Random randomPositionGenerator;

	private SmallInfoLabel pointsLabel;
	private ImageView[] playerLives;
	private int playerLife;
	private int points;	


	public GameViewManager() {
		initializeStage();
		createKeyListeners();
		randomPositionGenerator = new Random();
	}

	private void createKeyListeners() {
		gameScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.W) {
					isUpPressed = true;
				} else if (event.getCode() == KeyCode.S) {
					isDownPressed = true;
				}

				if (event.getCode() == KeyCode.A) {
					isLeftPressed = true;
				} else if (event.getCode() == KeyCode.D) {
					isRightPressed = true;
				}

				// open or close chat
				if (event.getCode() == KeyCode.SLASH) {
					chatVisible = !chatVisible;
					chatPane.setVisible(!chatPane.isVisible());
					chatInput.requestFocus();
				}

				if (event.getCode() == KeyCode.ENTER && chatVisible) {
					String message = chatInput.getText();
					chatLog.setText(chatLog.getText() + "\n" + message);
					gamePane.requestFocus();
				}
			}
		});

		gameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.W) {
					isUpPressed = false;
				} else if (event.getCode() == KeyCode.S) {
					isDownPressed = false;
				}

				if (event.getCode() == KeyCode.A) {
					isLeftPressed = false;
				} else if (event.getCode() == KeyCode.D) {
					isRightPressed = false;
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
		playerLife = 2;
		pointsLabel = new SmallInfoLabel("Points : 00");
		pointsLabel.setLayoutX(460);
		pointsLabel.setLayoutY(20);
		gamePane.getChildren().add(pointsLabel);
		playerLives = new ImageView[3];
	
		for(int i = 0; i < playerLives.length; i++) {
			playerLives[i] = new ImageView(chosenCharacter.getUrlLife());
			playerLives[i].setFitWidth(30);  // set width to 30 pixels
			playerLives[i].setFitHeight(30); // set height to 30 pixels
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

	//will probably move to scene before game starts rather than during in-game?
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
}
