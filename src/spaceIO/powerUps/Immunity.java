package spaceIO.powerUps;

import javafx.scene.image.Image;
import spaceIO.planets.Planet;

public class Immunity extends PowerUp{
    private Image pupImage;

    public static final int DURATION = 5;

    public Immunity(int xPos, int yPos, String type) {
        super(xPos, yPos, type);
        pupImage = new Image("assets/elements/immune.png", 30, 30, true, false);
        this.loadImage(pupImage);
    }

    public void initPayload(Planet player) {
        player.setImmune(true);
    }

    public static void endPayload(Planet player) {
        player.setImmune(false);
    }


}
