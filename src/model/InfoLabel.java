package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.text.Font;
//this is a component for a basic design of a label in the game
public class InfoLabel extends Label {
	public final static String FONT_PATH = "src/model/resources/rainyhearts.ttf";
	public final static String BACKGROUND_IMAGE = "view/resources/label_panel.png";

	public InfoLabel(String text) {
		setPrefWidth(250);
		setPrefHeight(49);
		setText(text);
		setWrapText(true);
		setLabelFont();
		setAlignment(Pos.CENTER);

		BackgroundImage backgroundImage = new BackgroundImage(new Image(BACKGROUND_IMAGE, 250, 49, false, true),
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);
		setBackground(new Background(backgroundImage));
	}

	private void setLabelFont() {
		try {
			setFont(Font.loadFont(getClass().getResourceAsStream("/view/resources/ArchitypeStedelijkW00.ttf"), 25));
		} catch (NullPointerException e) {
			setFont(Font.font("Verdana", 16));
		}
	}
}
