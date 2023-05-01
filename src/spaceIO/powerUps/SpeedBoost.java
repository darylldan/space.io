package spaceIO.powerUps;

import javafx.scene.image.Image;
import spaceIO.planets.Planet;

public class SpeedBoost extends PowerUp{
    private Image pupImage;

    public static final int DURATION = 5;

    public SpeedBoost(int xPos, int yPos, String type) {
        super(xPos, yPos, type);

        pupImage = new Image("assets/elements/speedboost.png", 30, 30, true, false);
        this.loadImage(pupImage);
    }

    public void initPayload(Planet player) {
        player.setSpeed((player.getSpeed() * 2) + 3);
    }

    public static void endPayload(Planet player) {
        player.disableCustomSpeed();
    }
}