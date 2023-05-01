/*
 * EnemyPlanet Class - an extension of the Planet Class
 *      - Enemy planet can move in a random direction for random amount of seconds (max 10 secs)
 *      - Possesses same attributes with the planet class, can eat asteroids or other planets, and pick up power ups.
 */
package spaceIO.planets;

import javafx.scene.image.Image;
import spaceIO.asteroids.Asteroids;

import java.util.Random;

public class EnemyPlanet extends Planet{
    // Used for random movement
    private long timeUntilMovement;
    private int movementDirection;

    /*
    *  Values of movementDirection and their directions
    *  0 - UP
    *  1 - UP-RIGHT
    *  2 - RIGHT
    *  3 - DOWN-RIGHT
    *  4 - DOWN
    *  5 - DOWN-LEFT
    *  6 - LEFT
    *  7 - UP-LEFT
    */

    private boolean isMoving;
    private Random rand;

    public EnemyPlanet(String type, int x, int y) {
        super(type, x, y);

        isMoving = false;
        rand = new Random();
    }

    // Generates a direction for a planet to move on
    private void generateDirection() {
        movementDirection = rand.nextInt(1,8);
    }

    // Generates how long will a planet move to a certain direction
    public int generateMoveDuration() {
        return rand.nextInt(1, 11);
    }

    // Starts moving the planet
    public void startMoving() {
        isMoving = true;
        generateDirection();
        selfMove();
    }

    // Moves the planet depending on the direction
    public void selfMove() {
        switch(movementDirection) {
            case 0: // UP
                this.setDY(this.getSpeed() * -1);
                break;
            case 1: // UP-RIGHT
                this.setDY(this.getSpeed() * -1);
                this.setDX(this.getSpeed());
                break;
            case 2: // RIGHT
                this.setDX(this.getSpeed());
                break;
            case 3: // RIGHT-DOWN
                this.setDX(this.getSpeed());
                this.setDY(this.getSpeed());
                break;
            case 4: // DOWN
                this.setDY(this.getSpeed());
                break;
            case 5: // DOWN LEFT
                this.setDY(this.getSpeed());
                this.setDX(this.getSpeed() * -1);
                break;
            case 6: // LEFT
                this.setDX(this.getSpeed() * -1);
                break;
            case 7:
                this.setDY(this.getSpeed() * -1);
                this.setDX(this.getSpeed() * -1);
                break;
            default:
                this.setDY(this.getSpeed());
        }
    }

    // Stops moving the planet
    public void stopMoving() {
        isMoving = false;
        this.setDY(0);
        this.setDX(0);
    }

    /* ----------- Same eating attributes from the planet class ----------- */

    @Override
    public void eat(Planet planet) {
        score += planet.getScore();
        planetImage = new Image("assets/elements/enemyPlanet.png", score, score, true, false);

        loadImage(planetImage);
        this.setHeight(score);
        this.setWidth(score);
    }

    @Override
    public void eat(Asteroids asteroid) {
        score += asteroid.getValue();
        // I can probably use set width method

        planetImage = new Image("assets/elements/enemyPlanet.png", score, score, true, false);

        loadImage(planetImage);
        this.setHeight(score);
        this.setWidth(score);
    }

    public boolean isMoving() {
        return isMoving;
    }

    // Stores the time that the planet will stop moving
    public void setMoveDuration(long gameTimeUntil) {
        timeUntilMovement = gameTimeUntil;
    }

    public long getTimeUntilMovement() {
        return timeUntilMovement;
    }
}
