package view;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.CHARACTER;
import model.CharacterSelect;
import model.GameButton;
import model.GameClient;
import model.GameData;
import model.GameServer;
import model.GameSubScene;
import model.InfoLabel;

public class LobbyViewManager {
	private static final int HEIGHT = 540;
    private static final int WIDTH = 960;

    private AnchorPane lobbyPane;
    private Scene lobbyScene;
    private Stage lobbyStage;
    private Stage menuStage;

    private final static int MENU_BUTTON_START_X = 700;
    private final static int MENU_BUTTON_START_Y = 140;

    private final String FONT_PATH = "model/resources/rainyhearts.ttf";

    private GameSubScene playSubScene;
    private GameSubScene characterSelectSubScene;

    private GameSubScene sceneToHide;

    List<GameButton> lobbyButtons;

    List<CharacterSelect> characterList;
    private CHARACTER chosenCharacter = CHARACTER.BLUE; //default is blue

    private TextField chatInput;
    private TextArea chatLog;

    private GameClient client;
    private String name;

    GameData[] players = new GameData[2];

    public LobbyViewManager(String type, String ip, int port, String name) {
    	this.name = name;
    	if (type == "create") {
    		createServer(ip, port);
    	}

    	else if (type == "join") {
    		joinServer(ip, port);
    	}
    	initializeLobby();
    }

    private void initializeLobby() {
        lobbyButtons = new ArrayList<>();
        lobbyPane = new AnchorPane();
        lobbyScene = new Scene(lobbyPane,WIDTH,HEIGHT);
        lobbyStage = new Stage();
        lobbyStage.setScene(lobbyScene);
        lobbyStage.setResizable(false);

        createBackground();
        createSubScenes();
        // createBackgroundBehindButtons();

        createButtons();
        createChat();
    	while (!client.ready) {continue;}
    }

    public void createLobby(Stage menuStage) {
        this.menuStage = menuStage;
        this.menuStage.hide();
//        this.chosenCharacter = chosenCharacter;
//        createBackground();
        lobbyStage.show();
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
        lobbyPane.getChildren().add(playSubScene);

        createCharacterSelectSubScene();
    }

    private void createCharacterSelectSubScene() {
        characterSelectSubScene = new GameSubScene();
        lobbyPane.getChildren().add(characterSelectSubScene);

        InfoLabel chooseCharacterLabel = new InfoLabel("Choose your character");
        chooseCharacterLabel.setLayoutX(125);
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
                    // updates your character in other clients
                    client.sendPlayerData(chosenCharacter, name);
                }
            });
        }
        box.setLayoutX(300 - (110*1.6));
        box.setLayoutY(100);
        return box;
    }

    private void addMenuButton(GameButton button) {
        button.setLayoutX(MENU_BUTTON_START_X);
        button.setLayoutY(MENU_BUTTON_START_Y + lobbyButtons.size() * 70);
        lobbyButtons.add(button);
        lobbyPane.getChildren().add(button);
    }

    private void createButtons() {
        createStartButton();
        createExitButton();
        createSendButton();
    }

    private GameButton createPlayButton() {
        GameButton playButton = new GameButton("Play");
        playButton.setLayoutX(160);
        playButton.setLayoutY(230);

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (chosenCharacter != null) {
                    GameViewManager gameManager = new GameViewManager(client, players);
                    gameManager.createNewGame(lobbyStage);
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

    private void createExitButton() {
        GameButton exitButton = new GameButton("Exit");
        addMenuButton(exitButton);

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lobbyStage.close();
            }
        });
    }

    private void createSendButton() {
        GameButton sendButton = new GameButton("Send");
        addMenuButton(sendButton);

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	client.sendChat(chatInput.getText());
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
            backgroundImageView.fitWidthProperty().bind(lobbyPane.widthProperty());
            backgroundImageView.fitHeightProperty().bind(lobbyPane.heightProperty());

            // Add the background image to the mainPane
            lobbyPane.getChildren().add(backgroundImageView);
        } catch (Exception e) {
            // Print detailed error message
            System.err.println("Error creating background: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // ------ networking stuff --------

    private void createServer(String ip, int port) {
    	GameServer server = new GameServer(port, ip);
    	server.start();
    	client = new GameClient(ip, port, this::onMessageReceived, name);
    }

    private void joinServer(String ip, int port) {
    	client = new GameClient(ip, port, this::onMessageReceived, name);
    }

    private void onMessageReceived(GameData data) {
    	if (data.type.equals("chat")) {
    	  	chatLog.appendText(data.msg + '\n');
    	}

    	else if (data.type.equals("player")) {
    		if (players[data.playerNum] == null) players[data.playerNum] = new GameData();
    		players[data.playerNum].playerNum = data.playerNum;
    		players[data.playerNum].name = data.name;
    		players[data.playerNum].character = data.character;
    	}
    	// no game data should be received at this point
  }

    private void createChat() {
        chatInput = new TextField();
        chatInput.setLayoutX(0);
        chatInput.setLayoutY(300);
        chatLog = new TextArea();
        chatLog.setLayoutX(0);
        chatLog.setLayoutY(0);
        lobbyPane.getChildren().addAll(chatInput, chatLog);
    }
}
