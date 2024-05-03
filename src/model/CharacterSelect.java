package model;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CharacterSelect extends VBox {

	private ImageView circleImage;
	private ImageView characterImage;

	private String circleNotChosen = "view/resources/characters/grey_circle.png";
	private String circleChosen = "view/resources/characters/chosen_circle.png";

	private CHARACTER character;

	private boolean isCharacterChosen;

	public CharacterSelect(CHARACTER character) {
		circleImage = new ImageView(circleNotChosen);
		characterImage = new ImageView(character.getUrl());
		this.character = character;
		isCharacterChosen = false;
		this.setAlignment(Pos.CENTER);
		this.setSpacing(20);
		this.getChildren().add(circleImage);
		this.getChildren().add(characterImage);
	}

	public CHARACTER getCharacter() {
		return character;
	}

	public boolean getIsCharacterChosen() {
		return isCharacterChosen;
	}

	public void setIsCharacterChosen(boolean isCharacterChosen) {
		this.isCharacterChosen = isCharacterChosen;
		String imageToSet = this.isCharacterChosen ? circleChosen : circleNotChosen;
		circleImage.setImage(new Image(imageToSet));
	}
}
