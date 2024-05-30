package model;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
// this is a component for a button; contains the basic design for every button in the game
public class GameButton extends Button {

	private final String FONT_PATH = "src/model/resources/rainyhearts.ttf";
	// Font customFont = Font.loadFont(getClass().getResourceAsStream("/view/resources/ArchitypeVierkantW00.ttf"), 30);

	private final String BUTTON_PRESSED_STYLE = "-fx-background-color: transparent; -fx-background-image: url('model/resources/button_bg.png'); -fx-text-fill: black;";
	private final String BUTTON_FREE_STYLE = "-fx-background-color: transparent; -fx-background-image: url('model/resources/button_bg.png'); -fx-text-fill: black;";

	public GameButton(String text) {
		setText(text);
		setButtonFont();
		setPrefWidth(150);
		setPrefHeight(49);
		setStyle(BUTTON_FREE_STYLE);
		initializeButtonListeners();
	}

	private void setButtonFont() {
		try {
			setFont(Font.loadFont(getClass().getResourceAsStream("/view/resources/ArchitypeStedelijkW00.ttf"), 25));
		} catch (NullPointerException e) {
			setFont(Font.font("Verdana", 16));
		}
	}

	private void setButtonPressedStyle() {
		setStyle(BUTTON_PRESSED_STYLE);
		setPrefHeight(45);
		setLayoutY(getLayoutY() + 4);
	}

	private void setButtonFreeStyle() {
		setStyle(BUTTON_FREE_STYLE);
		setPrefHeight(49);
		setLayoutY(getLayoutY() - 4);
	}

	private void initializeButtonListeners() {
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					setButtonPressedStyle();
				}
			}
		});

		setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					setButtonFreeStyle();
				}
			}
		});

		setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setEffect(new DropShadow());
			}
		});

		setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				setEffect(null);
			}
		});
	}
}
