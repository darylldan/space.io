/*
 * Planet Class
 * 		- Class of Planets, where they can eat asteroids, power ups, and other planets.
 * 		- They grew in size everytime they eat asteroids or other planets.
 * 		- If they eat other planets, they will grow by the size of the planet they have eaten.
 */
package spaceIO.planets;

import javafx.scene.image.Image;
import spaceIO.asteroids.Asteroids;
import spaceIO.GameStage;
import spaceIO.Sprite;

public class Planet extends Sprite {
	// Two types of planets
	public final static String TYPE_ENEMY = "enemy";
	public final static String TYPE_PLAYER = "player";

	// Planet's attributes
	private String type;
	private boolean alive;
	private boolean isImmune;
	private int customSpeed;
	private boolean hasCustomSpeed;

	// Planet's stats, their score starts with 40(their initial size in px), but the game subtracts 40 to their final score
	public int score;
	private int asteroidsEaten;
	private int enemyEaten;

	public static Image planetImage;

	// Initial planet radius
	private final static int PLANET_RADIUS = 40;

	// Class constructor
	public Planet(String type, int x, int y){
		super(x,y);
		this.type = type;
		this.score = PLANET_RADIUS;
		planetImage= setImage(type);
		this.alive = true;
		this.loadImage(planetImage);
		isImmune = false;
	}

	// Sets the image according to their appropriate type
	public Image setImage(String type) {
		if (type.equals(TYPE_PLAYER)) {
			return new Image("assets/elements/playerPlanet.png", score, score,true,false);
		} else if (type.equals(TYPE_ENEMY)) {
			return new Image("assets/elements/enemyPlanet.png", score, score,true,false);
		}

		return new Image("assets/elements/playerPlanet.png", score, score,true,false);
	}

	// Checks if the planet is still alive, never used, but I am afraid removing this will cause the program to break
	public boolean isAlive(){
		if(this.alive) return true;
		return false;
	} 

	// same with isAlive()
	public void die(){
    	this.alive = false;
    }

	// Eats an asteroid and increases its size by the asteroid's value
	public void eat(Asteroids asteroid) {
		score += asteroid.getValue();
		// I can probably use set width method
		planetImage= new Image("assets/elements/playerPlanet.png", score, score,true,false);

		loadImage(planetImage);
		this.setHeight(score);
		this.setWidth(score);
		asteroidsEaten++;
	}

	// Eats a planet and increases its size by the eaten planet's value
	public void eat(Planet planet) {
		score += planet.getScore();
		planetImage= new Image("assets/elements/playerPlanet.png", score, score,true,false);


		loadImage(planetImage);
		this.setHeight(score);
		this.setWidth(score);
		enemyEaten++;
	}

	// Moves the planet until it reaches the map borders
	public void move() {
		if(this.x+this.dx <= GameStage.MAP_WIDTH
				&& this.x+this.dx >=0 && this.y+this.dy <= GameStage.MAP_HEIGHT
				&& this.y+this.dy >=0){
    		this.x += this.dx;
    		this.y += this.dy;
    	}
	}

	// Computes for the speed of the planet
	public int getSpeed() {
		if (hasCustomSpeed) {
			return customSpeed;
		}

		int speed = 120/score;
		if (speed == 0) {
			return 1;
		}

		return 120/score;
	}

	// Sets custom speed
	public void setSpeed(int speed) {
		customSpeed = speed;
		hasCustomSpeed = true;
	}

	// Sets immune status of planet
	public void setImmune(Boolean val) {
		isImmune = val;
	}

	// Disables custom speed of the planet
	public void disableCustomSpeed() {
		hasCustomSpeed = false;
	}

	public int getScore() {
		return score;
	}

	public boolean immuneStatus() {
		return isImmune;
	}

	public int getAsteroidsEaten() {
		return asteroidsEaten;
	}

	public int getEnemyEaten() {
		return enemyEaten;
	}

}
