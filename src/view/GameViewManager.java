package view;

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

	public GameViewManager() {
		initializeStage();
		createKeyListeners();
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
		createGameLoop();
		gameStage.show();
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
