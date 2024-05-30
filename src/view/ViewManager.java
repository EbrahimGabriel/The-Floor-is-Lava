package view;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.GameButton;
import model.GameSubScene;
import model.InfoLabel;

public class ViewManager {
    // private static final String[] BUTTON_TEXTS = { "START GAME", "MULTIPLAYER", "QUIT" };
    // private static final String FOOTER_TEXT = "CMSC 137 B-4L GROUP 5";
    private static final int HEIGHT = 540;
    private static final int WIDTH = 960;

    private AnchorPane mainPane;
    private Scene mainScene;
    private Stage mainStage;

    private final static int MENU_BUTTON_START_X = 700;
    private final static int MENU_BUTTON_START_Y = 140;

    private final String FONT_PATH = "model/resources/rainyhearts.ttf";

    private TextField ipInput;
    private TextField portInput;
    private TextField nameInput;
    private String ip;
    private int port;
    private String name;

    private GameSubScene playSubScene;
    private GameSubScene lobbySubScene;
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
        mainStage.setResizable(false);

        createBackground();
        createSubScenes();
        createButtons();
        // createBackgroundBehindButtons();
    }

    public Stage getMainStage() {
        return mainStage;
    }

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

        createLobbySubScene();

        helpSubScene = new GameSubScene();
        mainPane.getChildren().add(helpSubScene);

        creditsSubScene = new GameSubScene();
        mainPane.getChildren().add(creditsSubScene);

    }

    private void createLobbySubScene() {
        lobbySubScene = new GameSubScene();
        mainPane.getChildren().add(lobbySubScene);

        InfoLabel nameLabel = new InfoLabel("Name");
        nameLabel.setLayoutX(50);
        nameLabel.setLayoutY(0);

        InfoLabel ipLabel = new InfoLabel("IP Address");
        ipLabel.setLayoutX(50);
        ipLabel.setLayoutY(75);

        InfoLabel portLabel = new InfoLabel("Port");
        portLabel.setLayoutX(50);
        portLabel.setLayoutY(150);

        nameInput = new TextField();
        nameInput.setLayoutX(200);
        nameInput.setLayoutY(10);

        ipInput = new TextField();
        ipInput.setLayoutX(200);
        ipInput.setLayoutY(85);

        portInput = new TextField();
        portInput.setLayoutX(200);
        portInput.setLayoutY(160);

        lobbySubScene.getPane().getChildren().addAll(nameLabel, ipLabel, portLabel,
        		nameInput, ipInput, portInput,
        		createCreateButton(), createJoinButton());
    }

    private void addMenuButton(GameButton button) {
        button.setLayoutX(MENU_BUTTON_START_X);
        button.setLayoutY(MENU_BUTTON_START_Y + menuButtons.size() * 70);
        menuButtons.add(button);
        mainPane.getChildren().add(button);
    }

    private void createButtons() {
        createMultiplayerButton();
        createHelpButton();
        createCreditsButton();
        createExitButton();
    }

    private GameButton createCreateButton() {
        GameButton createButton = new GameButton("Create");
        createButton.setLayoutX(320);
        createButton.setLayoutY(230);

        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if (ipInput.getText() != null && !ipInput.getText().isEmpty()
            			&& portInput.getText() != null && !portInput.getText().isEmpty()) {
    				ip = ipInput.getText();
    				port = Integer.parseInt(portInput.getText());
    				name = nameInput.getText();
                	LobbyViewManager lobbyManager = new LobbyViewManager("create", ip, port, name);
                	lobbyManager.createLobby(mainStage);
            	}

            	else {
            		// TODO: ADD ERROR PROMPT
            	}
        	}
        });
        return createButton;
    }

    private GameButton createJoinButton() {
    	GameButton joinButton = new GameButton("Join");
    	joinButton.setLayoutX(80);
    	joinButton.setLayoutY(230);

    	joinButton.setOnAction(new EventHandler<ActionEvent>() {
    		@Override
    		public void handle(ActionEvent event) {
    			if (ipInput.getText() != null && !ipInput.getText().isEmpty()
    					&& portInput.getText() != null && !portInput.getText().isEmpty()) {
    				ip = ipInput.getText();
    				port = Integer.parseInt(portInput.getText());
    				name = nameInput.getText();
    				LobbyViewManager lobbyManager = new LobbyViewManager("join", ip, port, name);
                	lobbyManager.createLobby(mainStage);
            	}

            	else {
            		// TODO: ADD ERROR PROMPT
            	}
    		}
    	});
    	return joinButton;
    }

    // trying to start single player game here
    // private void createStartButton() {
    //     GameButton startButton = new GameButton("Start");
    //     addMenuButton(startButton);

    //     startButton.setOnAction(new EventHandler<ActionEvent>() {
    //         @Override
    //         public void handle(ActionEvent event) {
    //             showSubScene(playSubScene);
    //         }
    //     });
    // }

    private void createMultiplayerButton() {
        GameButton multiplayerButton = new GameButton("Multiplayer");
        addMenuButton(multiplayerButton);

        multiplayerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showSubScene(lobbySubScene);
            }
        });
    }

    private void createHelpButton() {
        GameButton helpButton = new GameButton("Help");
        addMenuButton(helpButton);

        helpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ImageView imageView = new ImageView(new Image("/model/resources/mechanicsf.png"));
                helpSubScene.getPane().getChildren().add(imageView);
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
                ImageView imageView = new ImageView(new Image("/model/resources/credits_500x300.jpg"));
                creditsSubScene.getPane().getChildren().add(imageView);
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
    }

    private void createBackground() {
        try {
            String imageUrl = "/view/resources/bg_2.png";
            Image backgroundImage = new Image(imageUrl);

            // Check if the image loading was successful
            if (backgroundImage.isError()) {
                throw new RuntimeException("Error loading background image: " + imageUrl);
            }

            // Create and configure the ImageView for the background image
            ImageView backgroundImageView = new ImageView(backgroundImage);
            // backgroundImageView.setPreserveRatio(true);
            // backgroundImageView.setSmooth(false);
            // backgroundImageView.setCache(false);
            backgroundImageView.fitWidthProperty().bind(mainPane.widthProperty());
            backgroundImageView.fitHeightProperty().bind(mainPane.heightProperty());

            // Add the background image to the mainPane
            mainPane.getChildren().add(backgroundImageView);
        } catch (Exception e) {
            // Print detailed error message
            System.err.println("Error creating background: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createBackgroundBehindButtons() {
        Rectangle background = new Rectangle(20, 20, 252, 560); // Position (50, 50) and size (500, 900)
        background.setFill(Color.rgb(0, 0, 0, 0.8)); // Semi-transparent black
        mainPane.getChildren().add(background);

        // Load the custom font
        Font customFont = null;
        try {
            customFont = Font.loadFont(getClass().getResourceAsStream("/" + FONT_PATH), 60);
        } catch (Exception e) {
            System.err.println("Error loading font: " + e.getMessage());
            e.printStackTrace();
        }

        // Create a new label
        Label label = new Label("The Floor");
        label.setFont(customFont); // Set the custom font
        label.setTextFill(Color.WHITE); // Set the text color to white
        label.setLayoutX(30); // Set the X position of the label
        label.setLayoutY(80); // Set the Y position of the label
        label.setAlignment(Pos.CENTER); // Set the alignment to center

        // Create a new label
        Label newLabel = new Label("is Lava");
        newLabel.setFont(customFont); // Set the custom font
        newLabel.setTextFill(Color.WHITE); // Set the text color to white
        newLabel.setLayoutX(60); // Set the X position of the new label
        newLabel.setLayoutY(123); // Set the Y position of the new label to be greater than the Y position of the existing label
        newLabel.setAlignment(Pos.CENTER); // Set the alignment to center

        // Add the label to the mainPane
        mainPane.getChildren().add(label);
        mainPane.getChildren().add(newLabel);
    }
}