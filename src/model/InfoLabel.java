package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class InfoLabel extends Label {
	public final String FONT_PATH = "src/model/resources/rainyhearts.ttf";

	public InfoLabel(String text) {
		setPrefWidth(600);
		setPrefHeight(400);
		setPadding(new Insets(40, 40, 40, 40));
		setText(text);
		setWrapText(true);
		setLabelFont();
	}

	private void setLabelFont() {
		try {
		setFont(Font.loadFont(new FileInputStream(new File(FONT_PATH)), 16));
		} catch (FileNotFoundException e){
			setFont(Font.font("Verdana", 16));
		}
	}
}
