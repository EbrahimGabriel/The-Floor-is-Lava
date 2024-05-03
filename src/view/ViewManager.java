package view;

import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.Stage;
import model.GameButton;
import model.GameSubScene;

public class ViewManager {

	private static final int HEIGHT = 500;
	private static final int WIDTH = 700;

	private AnchorPane mainPane;
	private Scene mainScene;
	private Stage mainStage;

	private final static int MENU_BUTTON_START_X = 100;
	private final static int MENU_BUTTON_START_Y = 150;

	private GameSubScene playSubScene;
	private GameSubScene helpSubScene;
	private GameSubScene creditsSubScene;

	private GameSubScene sceneToHide;

	List<GameButton> menuButtons;

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
		creditsSubScene = new GameSubScene();
		mainPane.getChildren().add(creditsSubScene);

		helpSubScene = new GameSubScene();
		mainPane.getChildren().add(helpSubScene);

		playSubScene = new GameSubScene();
		mainPane.getChildren().add(playSubScene);
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

	private void createStartButton() {
		GameButton startButton = new GameButton("Start");
		addMenuButton(startButton);

		startButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showSubScene(playSubScene);
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
		ImageView title = new ImageView("view/resources/background.jpg");
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
