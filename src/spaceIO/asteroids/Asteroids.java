/*
 * Asteroids Class
 *      - Handles the food that the planet eats, an extension of the Sprite class
 */

package spaceIO.asteroids;

import javafx.scene.image.Image;
import spaceIO.Sprite;

public class Asteroids extends Sprite {
    private int value;
    public static Image asteroidImage;

    // Class constructor
    public Asteroids(int x, int y, int value) {
        super(x,y);
        this.value = value;     // The amount that the planets increase in size when this asteroid is eaten
        asteroidImage = new Image("/assets/elements/asteroid.png", 20, 20, true, false);
        this.loadImage(asteroidImage);
    }

    public int getValue() {
        return value;
    }
}
