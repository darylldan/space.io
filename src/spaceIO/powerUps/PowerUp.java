package spaceIO.powerUps;

import javafx.scene.image.Image;
import spaceIO.Sprite;

public class PowerUp extends Sprite {
    public static final String TYPE_SPEEDBOOST = "speedboost";
    public static final String TYPE_IMMUNE = "immune";

    private String type;

    private Image powerupImage;

    public PowerUp(int xPos, int yPos, String type) {
        super(xPos, yPos);

        this.type = type;
        powerupImage = setImage(type);
        this.loadImage(powerupImage);
    }

    private Image setImage(String type) {
        if (type.equals(TYPE_SPEEDBOOST)) {
            return new Image("assets/elements/speedboost.png", 30, 30, true, false);
        } else if (type.equals(TYPE_IMMUNE)) {
            return new Image("assets/elements/immune.png", 30, 30, true, false);
        }

        else return new Image("assets/generic_pup.png", 30, 30, true, false);
    }

}
