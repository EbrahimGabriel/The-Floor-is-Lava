package model;

import javafx.animation.TranslateTransition;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.util.Duration;

import java.awt.Toolkit;

//remove later
import javafx.scene.text.Text;
//---

public class GameSubScene extends SubScene {

    private final String FONT_PATH = "src/model/resources/rainyhearts.ttf";
    private final String BACKGROUND_IMAGE = "/model/resources/subscene_bg3.png";

    private boolean isHidden = true;

    public GameSubScene() {
        super(new AnchorPane(), 500, 300);
        prefWidth(500);
        prefHeight(300);

        BackgroundImage image = new BackgroundImage(new Image(BACKGROUND_IMAGE, 500, 300, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);

        AnchorPane root2 = (AnchorPane) this.getRoot();
        root2.setBackground(new Background(image));

        //remove later
        Text t = new Text(10,50,"Test");
        root2.getChildren().add(t);
        //----

        // Set initial position to be off-screen
        setLayoutX(Toolkit.getDefaultToolkit().getScreenSize().width);
        setLayoutY(120);
    }

    public void moveSubScene() {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.seconds(0.3));
        transition.setNode(this);

        if (isHidden) {
            transition.setToX(-1270);
            isHidden = false;
        } else {
            transition.setToX(0);
            isHidden = true;
        }

        transition.play();
    }

    public AnchorPane getPane() {
        return (AnchorPane) this.getRoot();
    }
}