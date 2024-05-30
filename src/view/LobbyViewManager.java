package view;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
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
    private CHARACTER chosenCharacter = CHARACTER.BLUE; // default is blue

    private TextField chatInput;
    private TextArea chatLog;

    private GameServer server;
    private GameClient client;
    private String name;
    private boolean ready = false;

    GameData[] players = new GameData[4];

    private Label[] playerLabels = new Label[4];

    public LobbyViewManager(String type, String ip, int port, String name) {
        this.name = name;
        if (type.equals("create")) {
            createServer(ip, port);
        } else if (type.equals("join")) {
            joinServer(ip, port);
        }
        initializeLobby();
    }

    private void initializeLobby() {
        lobbyButtons = new ArrayList<>();
        lobbyPane = new AnchorPane();
        lobbyScene = new Scene(lobbyPane, WIDTH, HEIGHT);
        lobbyStage = new Stage();
        lobbyStage.setScene(lobbyScene);
        lobbyStage.setResizable(false);

        createBackground();
        createSubScenes();
        createChat();
        createButtons();
        createPlayerNameLabel(); // Add this line
        createPlayerLabels(); // Add this line
        showSubScene(characterSelectSubScene);
        while (!client.ready) {
            continue;
        }
    }

    public void createLobby(Stage menuStage) {
        this.menuStage = menuStage;
        this.menuStage.hide();
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

        if (client.getPlayerNum() == 0) {
            characterSelectSubScene.getPane().getChildren().add(createPlayButton());
        }
    }

    private HBox createCharactersToSelect() {
        HBox box = new HBox();
        box.setSpacing(20);
        characterList = new ArrayList<>();
        for (CHARACTER character : CHARACTER.values()) {
            CharacterSelect characterToPick = new CharacterSelect(character);
            characterList.add(characterToPick);
            box.getChildren().add(characterToPick);

            characterToPick.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    for (CharacterSelect character : characterList) {
                        character.setIsCharacterChosen(false);
                    }
                    characterToPick.setIsCharacterChosen(true);
                    chosenCharacter = characterToPick.getCharacter();
                    client.sendPlayerData(chosenCharacter, name, ready);
                }
            });
        }
        box.setLayoutX(300 - (110 * 1.6));
        box.setLayoutY(100);
        return box;
    }

    private void addMenuButton(GameButton button) {
        button.setLayoutX(MENU_BUTTON_START_X + lobbyButtons.size() * 100);
        button.setLayoutY(MENU_BUTTON_START_Y);
        lobbyButtons.add(button);
        characterSelectSubScene.getPane().getChildren().add(button);
    }

    private void createButtons() {
        createReadyButton();
        createExitButton();
    }

    private void startGame() {
        Platform.runLater(() -> {
            GameViewManager gameManager = new GameViewManager(client, players);
            gameManager.createNewGame(lobbyStage);
        });
    }

    private GameButton createPlayButton() {
        GameButton playButton = new GameButton("Play");
        playButton.setLayoutX(160);
        playButton.setLayoutY(230);

        playButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean allReady = true;
                for (GameData player : players) {
                    if (!player.ready)
                        allReady = false;
                }
                if (allReady)
                    client.sendGameStart();
            }
        });
        return playButton;
    }

    private void createExitButton() {
        GameButton exitButton = new GameButton("Exit");
        lobbyButtons.add(exitButton);
        lobbyPane.getChildren().add(exitButton);

        exitButton.setLayoutX(270);
        exitButton.setLayoutY(430);

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lobbyStage.close();
                client.close();
                if (server != null) server.close();
                menuStage.show();
            }
        });
    }

    private void createReadyButton() {
        GameButton sendButton = new GameButton("Ready");
        lobbyButtons.add(sendButton);
        lobbyPane.getChildren().add(sendButton);

        sendButton.setLayoutX(80);
        sendButton.setLayoutY(430);

        sendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ready = !ready;
                client.sendPlayerData(chosenCharacter, name, ready);
            }
        });
    }

    private void createBackground() {
        try {
            String imageUrl = "/view/resources/chat_bg.png";
            Image backgroundImage = new Image(imageUrl);

            if (backgroundImage.isError()) {
                throw new RuntimeException("Error loading background image: " + imageUrl);
            }

            ImageView backgroundImageView = new ImageView(backgroundImage);
            backgroundImageView.fitWidthProperty().bind(lobbyPane.widthProperty());
            backgroundImageView.fitHeightProperty().bind(lobbyPane.heightProperty());

            lobbyPane.getChildren().add(backgroundImageView);
        } catch (Exception e) {
            System.err.println("Error creating background: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createServer(String ip, int port) {
    	server = new GameServer(port, ip);
    	server.start();
    	client = new GameClient(ip, port, this::onMessageReceived, name);
        GameServer server = new GameServer(port, ip);
        server.start();
        client = new GameClient(ip, port, this::onMessageReceived, name);
    }

    private void joinServer(String ip, int port) {
        client = new GameClient(ip, port, this::onMessageReceived, name);
    }

    private void onMessageReceived(GameData data) {
        Platform.runLater(() -> {
            if (data.type.equals("chat")) {
                chatLog.appendText(data.msg + '\n');

                for (GameData player : players) {
                    System.out.println(player.lives + player.name);
                }
            } else if (data.type.equals("player")) {
                if (players[data.playerNum] == null) {
                    players[data.playerNum] = new GameData();
                    players[data.playerNum].lives = 3;
                }
                players[data.playerNum].playerNum = data.playerNum;
                players[data.playerNum].name = data.name;
                players[data.playerNum].character = data.character;
                players[data.playerNum].ready = data.ready;
                updatePlayerLabels();
            } else if (data.type.equals("gamestart")) {
                startGame();
            }
        });
    }

    private void createChat() {
        chatInput = new TextField();
        chatInput.setLayoutX(611);
        chatInput.setLayoutY(457);
        chatInput.setPrefWidth(300);
        chatInput.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        chatInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    client.sendChat(chatInput.getText());
                    chatInput.clear();
                }
            }
        });

        chatLog = new TextArea();
        chatLog.setLayoutX(608);
        chatLog.setLayoutY(55);
        chatLog.setPrefWidth(310);
        chatLog.setPrefHeight(390);
        chatLog.setStyle("-fx-background-color: transparent;");

        lobbyPane.getChildren().addAll(chatInput, chatLog);
    }

    private void setButtonFont(Label label) {
    try {
        label.setFont(Font.loadFont(getClass().getResourceAsStream("/view/resources/ArchitypeStedelijkW00.ttf"), 25));
    } catch (NullPointerException e) {
        label.setFont(Font.font("Verdana", 16));
    }
    }

    private void createPlayerNameLabel() {
        Label playerNameLabel = new Label("Player: ");
        playerNameLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        setButtonFont(playerNameLabel);
        playerNameLabel.setLayoutX(10);
        playerNameLabel.setLayoutY(10);
        lobbyPane.getChildren().add(playerNameLabel);
    }

    private void createPlayerLabels() {
        for (int i = 0; i < playerLabels.length; i++) {
            playerLabels[i] = new Label();
            playerLabels[i].setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
            setButtonFont(playerLabels[i]);
            playerLabels[i].setLayoutX(200 + i * 200);
            playerLabels[i].setLayoutY(10);
            lobbyPane.getChildren().add(playerLabels[i]);
        }
    }

    private void updatePlayerLabels() {
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                playerLabels[i].setText(players[i].name);
            } else {
                playerLabels[i].setText("Waiting...");
            }
        }
    }
}
