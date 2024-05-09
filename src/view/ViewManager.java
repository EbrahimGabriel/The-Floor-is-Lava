package view;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.CHARACTER;
import model.CharacterSelect;
import model.GameButton;
import model.GameSubScene;
import model.InfoLabel;
//this is the view for the main menu and others
public class ViewManager {

	private static final int HEIGHT = 500;
	private static final int WIDTH = 700;

	private AnchorPane mainPane;
	private Scene mainScene;
	private Stage mainStage;

	private final static int MENU_BUTTON_START_X = 100;
	private final static int MENU_BUTTON_START_Y = 150;

	private GameSubScene playSubScene;
	private GameSubScene characterSelectSubScene;
	private GameSubScene helpSubScene;
	private GameSubScene creditsSubScene;

	private GameSubScene sceneToHide;

	List<GameButton> menuButtons;

	List<CharacterSelect> characterList;
	private CHARACTER chosenCharacter;

	public ViewManager() {
		menuButtons = new ArrayList<>();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane,WIDTH,HEIGHT);
		mainStage = new Stage();
		mainStage.setScene(mainScene);

		createSubScenes();
		createButtons();
		createBackground();
		//createTitle(); // uncomment when better title logo is found

	}

	public Stage getMainStage() {
		return mainStage;
	}

	//SubScene related Stuff
	private void showSubScene(GameSubScene subScene) {
		if (sceneToHide != null) {
			sceneToHide.moveSubScene();
		}

		subScene.moveSubScene();
		sceneToHide = subScene;
	}

	private void createSubScenes() {

		playSubScene = new GameSubScene();
		mainPane.getChildren().add(playSubScene);

		createCharacterSelectSubScene();

		helpSubScene = new GameSubScene();
		mainPane.getChildren().add(helpSubScene);

		creditsSubScene = new GameSubScene();
		mainPane.getChildren().add(creditsSubScene);

	}

	private void createCharacterSelectSubScene() {
		characterSelectSubScene = new GameSubScene();
		mainPane.getChildren().add(characterSelectSubScene);

		InfoLabel chooseCharacterLabel = new InfoLabel("Choose your character");
		chooseCharacterLabel.setLayoutY(110);
		chooseCharacterLabel.setLayoutY(25);
		characterSelectSubScene.getPane().getChildren().add(chooseCharacterLabel);
		characterSelectSubScene.getPane().getChildren().add(createCharactersToSelect());
		characterSelectSubScene.getPane().getChildren().add(createPlayButton());

	}

	private HBox createCharactersToSelect() {
		HBox box = new HBox();
		box.setSpacing(20);
		characterList = new ArrayList<>();
		for (CHARACTER character: CHARACTER.values()) {
			CharacterSelect characterToPick = new CharacterSelect(character);
			characterList.add(characterToPick);
			box.getChildren().add(characterToPick);

			characterToPick.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					for (CharacterSelect character: characterList) {
						character.setIsCharacterChosen(false);
					}
					characterToPick.setIsCharacterChosen(true);
					chosenCharacter = characterToPick.getCharacter();
				}
			});
		}
		box.setLayoutX(300 - (110*2));
		box.setLayoutY(100);
		return box;
	}
	// ------

	//Button related Stuff
	private void addMenuButton(GameButton button) {
		button.setLayoutX(MENU_BUTTON_START_X);
		button.setLayoutY(MENU_BUTTON_START_Y + menuButtons.size() * 100);
		menuButtons.add(button);
		mainPane.getChildren().add(button);
	}

	private void createButtons() {
		createStartButton();
		createHelpButton();
		createCreditsButton();
		createExitButton();
	}

	private GameButton createPlayButton() {
		GameButton playButton = new GameButton("Play");
		playButton.setLayoutX(100);
		playButton.setLayoutY(200);

		playButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (chosenCharacter != null) {
					GameViewManager gameManager = new GameViewManager();
					gameManager.createNewGame(mainStage, chosenCharacter);
				}
			}
		});
		return playButton;
	}

	private void createStartButton() {
		GameButton startButton = new GameButton("Start");
		addMenuButton(startButton);

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showSubScene(characterSelectSubScene);
			}
		});
	}

	private void createHelpButton() {
		GameButton helpButton = new GameButton("Help");
		addMenuButton(helpButton);

		helpButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showSubScene(helpSubScene);
			}
		});
	}

	private void createCreditsButton() {
		GameButton creditsButton = new GameButton("Credits");
		addMenuButton(creditsButton);

		creditsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showSubScene(creditsSubScene);
			}
		});
	}

	private void createExitButton() {
		GameButton exitButton = new GameButton("Exit");
		addMenuButton(exitButton);

		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mainStage.close();
			}
		});
	};

	// ------
	//Images on title screen
	private void createBackground() {
		Image backgroundImage = new Image("view/resources/background.jpg", 1000, 500, false, true);
		BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, null);
		mainPane.setBackground(new Background(background));
	}

	private void createTitle() {
		ImageView title = new ImageView("view/resources/background.jpg"); //missing image
		title.setLayoutX(400);
		title.setLayoutY(50);

		title.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				title.setEffect(new DropShadow());
			}
		});

		title.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				title.setEffect(null);
			}
		});

		mainPane.getChildren().add(title);
	}
	// ------
}
