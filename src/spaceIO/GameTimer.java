/*
 * GameTimer - extension of the AnimationTimer class from JavaFX
 * 		- Contains the logic of the game, which is executed every frame of the game.
 */

package spaceIO;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import spaceIO.asteroids.Asteroids;
import spaceIO.guiStages.GameOverStage;
import spaceIO.planets.EnemyPlanet;
import spaceIO.planets.Planet;
import spaceIO.powerUps.Immunity;
import spaceIO.powerUps.PowerUp;
import spaceIO.powerUps.SpeedBoost;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/*
 * The GameTimer is a subclass of the AnimationTimer class. It must override the handle method. 
 */
 
public class GameTimer extends AnimationTimer{
	// Used for time keeping
	private long intTime;

	// Checks if a powerUp is spawned in the map
	private boolean powerUpSpawn;

	// Java FX Nodes used to build the scene
	private GraphicsContext gc;
	private GraphicsContext gcScore;
	private Scene theScene;
	private Planet myPlanet;	// Player's planet
	private ScrollPane camera;
	private GameStage gs;

	// Checks if the view is zoomed out
	private boolean isZoomedOut;

	// Tracks the entities in the map
	private ArrayList<EnemyPlanet> enemies;
	private ArrayList<Asteroids> asteroids;
	private ArrayList<PowerUp> powerUps;
	private ArrayList<EnemyPlanet> removedEnemies;

	// Tracking game data
	private int asteroidCount;
	private Planet speedBoostHolder;
	private Planet immunityHolder;
	private boolean isSpeedBoostActivated;
	private boolean isImmunityActivated;
	private long speedBoostInit;
	private long immunityInit;

	// Max number of asteroids and enemies
	private static final int MAX_ASTEROID = 50;
	private static final int MAX_ENEMIES = 10;

	// PowerUp durations
	private static final int POWERUP_INTERVAL = 10;
	private static final int POWERUP_LIFESPAN = 5;

	// Asteroid value
	private static final int ASTEROID_VALUE = 10;
	
	GameTimer(GraphicsContext gc, Scene theScene, ScrollPane scrollPane, GraphicsContext gcScore, GameStage gs){
		// Initializing the required variables
		this.gc = gc;
		this.gcScore = gcScore;
		this.theScene = theScene;
		this.gs = gs;
		this.myPlanet = new Planet(Planet.TYPE_PLAYER,GameStage.MAP_HEIGHT / 2,GameStage.MAP_WIDTH / 2);
		this.enemies = new ArrayList<>();
		this.removedEnemies = new ArrayList<>();
		this.asteroids = new ArrayList<>();
		this.powerUps = new ArrayList<>();
		this.powerUpSpawn = false;

		// Initializes the camera
		this.camera = scrollPane;
		updateCamera();

		// Initializes the initial time
		intTime = System.nanoTime();

		// Sets up the custom font
		InputStream is = getClass().getResourceAsStream("/assets/fonts/boston.ttf");
		Font font = Font.loadFont(is, 30);

		// Configures GCs
		gcScore.setFont(font);
		gcScore.setImageSmoothing(false);
		gc.setImageSmoothing(false);

		// Spawns NPC entities
		asteroidCount = 0;
		spawnAsteroids();
		spawnEnemies();

		// Call method to handle mouse click event
		this.handleKeyPressEvent();
	}

	// Main game logic
	@Override
	public void handle(long currentNanoTime) {
		// Time stuff
		long currTime = TimeUnit.NANOSECONDS.toSeconds(currentNanoTime);
		long startTime = TimeUnit.NANOSECONDS.toSeconds(intTime);
		long gameTime = currTime - startTime;

		if (currTime - startTime >= 1) {
			intTime++;
		}

		// Avoid trailing
		gc.clearRect(0,0, GameStage.MAP_WIDTH, GameStage.MAP_HEIGHT);

		// Calls the method to move the planet and update the camera position
		this.myPlanet.move();
		if (myPlanet.getScore() >= 400 && !(isZoomedOut)) {
			isZoomedOut = true;
			zoomOut();
		}

		if (isZoomedOut) {
			updateCameraZoomed();
		} else {
			updateCamera();
		}


		// Render the ui elements
		this.myPlanet.render(this.gc);
		renderEnemies();
		renderAsteroids();
		renderPowerUp();

		// Checks for planet-to-planet collision

		/*
		 *  Asteroids and planets are removed in the map using the collect and remove method since it is impossible
		 *  to modify the ArrayList while iterating it without getting a ConcurrentModificationException.
		 */
		for(EnemyPlanet enemyPlanet: enemies) {
			// Player planet to enemy planet collision checking
			if (enemyPlanet.collidesWith(myPlanet)) {
				if (myPlanet.getScore() >= enemyPlanet.getScore()) {
					if (!(enemyPlanet.immuneStatus())) {
						myPlanet.eat(enemyPlanet);
						removedEnemies.add(enemyPlanet);
					}
				} else {
					if (!(myPlanet.immuneStatus())) {
						enemyPlanet.eat(myPlanet);

						// End game
						this.stop();
						displayGameOverScene(0, gameTime);
					}
				}
			}

			// Enemy planet to enemy planet collision checking
			for (EnemyPlanet enemyPlanet2: enemies) {
				if (enemyPlanet2.equals(enemyPlanet)) {
					continue;
				}
				if (enemyPlanet.collidesWith(enemyPlanet2)) {
					if (enemyPlanet.getScore() >= enemyPlanet2.getScore()) {
						if (!(enemyPlanet2.immuneStatus())) {
							enemyPlanet.eat(enemyPlanet2);
							removedEnemies.add(enemyPlanet2);
						}
					} else{
						if (!(enemyPlanet.immuneStatus())) {
							enemyPlanet2.eat(enemyPlanet);
							removedEnemies.add(enemyPlanet);
						}
					}
				}
			}
		}

		// Removes all the enemies "removed" from the enemy arrayList
		enemies.removeAll(removedEnemies);
		removedEnemies.clear();		// Clears the removedEnemy array for another use


		// Checks for Asteroid-to-Planet collision
		/*
		 * Another way to circumvent the ConcurrentModificationException is to use an iterator, it works here in asteroid
		 * to planet collision but it does not work on planet to planet collision
		 */
		for(Iterator<Asteroids> a = asteroids.iterator(); a.hasNext();) {
			Asteroids asteroid = a.next();

			// Player's planet to asteroid collision
			if (asteroid.collidesWith(myPlanet)) {
				myPlanet.eat(asteroid);
				a.remove();
				asteroidCount--;
			}

			// Enemy planet to asteroid collision
			for(Planet p: enemies) {
				if (asteroid.collidesWith(p)) {
					p.eat(asteroid);
					a.remove();
					asteroidCount--;
				}
			}
		}

		// Creates additional asteroids to maintain the 50 asteroids at map at all times
		for(int i = asteroidCount; asteroidCount <= MAX_ASTEROID; i++) {
			asteroids.add(createAsteroid());
		}

		// Spawns powerup starting at 10 secs, then every 10 secs after that
		if (gameTime >= POWERUP_INTERVAL) {
			if (gameTime % POWERUP_INTERVAL == 0 && !powerUpSpawn) {
				spawnPowerUp();
				powerUpSpawn = true;
			}
		}

		// Powerups dissappears after 5 secs
		if (powerUpSpawn) {
			if (gameTime % POWERUP_LIFESPAN == 0 && !(gameTime % POWERUP_INTERVAL == 0)) {
				powerUps.clear();
				powerUpSpawn = false;
			}
		}

		// Planet to power-up collisions, also initializes the power up to take effect to the planet that eats it
		for(Iterator<PowerUp> pup = powerUps.iterator(); pup.hasNext();) {
			PowerUp powerup = pup.next();

			// Player's planet to powerUp collision
			if (powerup.collidesWith(myPlanet)) {
				if (powerup instanceof SpeedBoost) {
					isSpeedBoostActivated = true;
					speedBoostInit = gameTime;
					speedBoostHolder = myPlanet;
					((SpeedBoost) powerup).initPayload(myPlanet);
					pup.remove();
				} else if (powerup instanceof Immunity) {
					isImmunityActivated = true;
					immunityInit = gameTime;
					immunityHolder = myPlanet;
					((Immunity) powerup).initPayload(myPlanet);
					pup.remove();
				}
			}

			// Enemy planet to powerUp collision
			for(Planet p: enemies) {
				if (powerup.collidesWith(p)) {
					if (powerup instanceof SpeedBoost) {
						isSpeedBoostActivated = true;
						speedBoostInit = gameTime;
						speedBoostHolder = p;
						((SpeedBoost) powerup).initPayload(p);
						pup.remove();
					} else if (powerup instanceof Immunity) {
						isImmunityActivated = true;
						immunityInit = gameTime;
						immunityHolder = p;
						((Immunity) powerup).initPayload(p);
						pup.remove();
					}
				}
			}
		}

		// Speed boost payload
		if (isSpeedBoostActivated) {
			if (gameTime - speedBoostInit == SpeedBoost.DURATION) {
				SpeedBoost.endPayload(speedBoostHolder);
				isSpeedBoostActivated = false;
			}
		}

		// Immunity payload
		if (isImmunityActivated) {
			if (gameTime - immunityInit == Immunity.DURATION) {
				Immunity.endPayload(immunityHolder);
				isImmunityActivated = false;
			}
			renderImmuneOverlay(this.gc, immunityHolder);
		}

		// Checks if the enemy is moving, if it is, it calls the move() method, if it is not, it creates a new move direction for the enemy
		for (EnemyPlanet ep: enemies) {
			if (!(ep.isMoving())) {
				ep.setMoveDuration(gameTime + ep.generateMoveDuration());
				ep.startMoving();
			} else if (ep.isMoving()) {
				if (ep.getTimeUntilMovement() == gameTime) {
					ep.stopMoving();
				}
				ep.move();
			}
		}

		// Updates the status bar
		updateStatusBar(gameTime);

		// Win conditions
		if (gameTime >= GameStage.MAP_HEIGHT / 16) {	// Time limit
			displayGameOverScene(3, gameTime);
			this.stop();
		} else if (myPlanet.score >= GameStage.MAP_HEIGHT / 1.6) {	// Score Limit
			displayGameOverScene(2, gameTime);
			this.stop();
		} else if (enemies.isEmpty()) {  // Last planet standing
			displayGameOverScene(1, gameTime);
			this.stop();
		}
	}

	// Updates in-game time
	private void updateTime(int startTime) {
		gcScore.setFill(Color.web("#352A55"));
		gcScore.fillText(String.valueOf(startTime), 162, 37);
	}

	// Updates the camera by always centering the player's planet

	/*
	 * How does the camera work?
	 * 	- Camera is a ScrollPane. The horizontal and vertical scroll values of the ScrollPane is always updated
	 * 	  so that it centers the player's planet always.
	 */
	private void updateCamera() {
		double xCenter = (double) GameStage.MAP_WIDTH / 2;
		double yCenter = (double) GameStage.MAP_HEIGHT / 2;
		double xComp =  (myPlanet.x - xCenter) / GameStage.MAP_WIDTH;
		double yComp =  (myPlanet.y - yCenter) / GameStage.MAP_HEIGHT;
		double newX = (double) myPlanet.getX() / GameStage.MAP_WIDTH;
		double newY = (double) myPlanet.getY() / GameStage.MAP_HEIGHT;

		camera.setVvalue(newY + (yComp/2));
		camera.setHvalue(newX + (xComp/2));
	}

	// Updates the zoomed out camera so that if follows the player's planet

	/*
	 * How does the zoomed out camera work?
	 * 	- Nothing changes in the ScrollPane but the main game canvas is being scaled to 0.5. It means that the
	 * 	  calculation adjust the scroll bars so that it follows the player will show beyond the edges of the map.
	 *  - The fix I have done is very clunky, maybe buggy, but it does the job just fine. I can maybe create a
	 *    better implementation of it in the near future but not now, I will settle with this since I can't progress
	 *    further in the project without this being implemented.
	 *  - It stops updating the scroll bars when the player reaches certain coordinates, which in the updateCamera()
	 *    method, will show beyond the edges of the map.
	 */
	private void updateCameraZoomed() {
		double xCenter = (double) GameStage.MAP_WIDTH / 2;
		double yCenter = (double) GameStage.MAP_HEIGHT / 2;
		double xComp =  (myPlanet.x - xCenter) / GameStage.MAP_WIDTH;
		double yComp =  (myPlanet.y - yCenter) / GameStage.MAP_HEIGHT;
		double newX = (double) myPlanet.getX() / GameStage.MAP_WIDTH;
		double newY = (double) myPlanet.getY() / GameStage.MAP_HEIGHT;

		if (myPlanet.getX() <= 1000 || myPlanet.getX() >= 1400) {
			if (myPlanet.getY() <= 975 || myPlanet.getY() >= 1427) {
				return;
			} else {
				camera.setVvalue(newY + (yComp/2));
			}
		} else if (myPlanet.getY() <= 975 || myPlanet.getY() >= 1427) {
			if  (myPlanet.getX() <= 1000 || myPlanet.getX() >= 1400) {
				return;
			} else {
				camera.setHvalue(newX + (xComp/2));
			}
		} else {
			camera.setVvalue(newY + (yComp/2));
			camera.setHvalue(newX + (xComp/2));
		}

	}

	// Zooms out by scaling the map by 0.5, includes a fix which prevents the ScrollPane to act weird when scaling down
	private void zoomOut() {
		gs.setScale(0.5);

		if (camera.getHvalue() > 0.6275) {
			camera.setHvalue(0.6275);
		} else if (camera.getHvalue() < 0.3725) {
			camera.setHvalue(0.3725);
		}

		if (camera.getVvalue() > 0.6275) {
			camera.setVvalue(0.6275);
		} else if (camera.getVvalue() < 0.3725) {
			camera.setVvalue(0.3725);
		}
	}

	// Updates the status bar canvas at the top
	private void updateStatusBar(long gameTime) {
		gcScore.clearRect(0,0, GameStage.WINDOW_WIDTH, 50);

		updateGUIFontSize(30);

		updateTime((int) gameTime);
		updateScore();
		updateAsteroidsEaten();
		updateEnemiesEaten();
		updatePowerupGUI(gameTime);
	}

	/* ---------- Updates different parts of status bar ---------- */
	private void updateScore() {
		gcScore.setFill(Color.web("#FE546F"));
		gcScore.fillText(String.valueOf(myPlanet.getScore() - 40), 300, 37);
	}

	private void updateAsteroidsEaten() {
		gcScore.setFill(Color.web("#352A55"));
		gcScore.fillText(String.valueOf(myPlanet.getAsteroidsEaten()), 452, 37);
	}

	private void updateEnemiesEaten() {
		gcScore.setFill(Color.web("#352A55"));
		gcScore.fillText(String.valueOf(myPlanet.getEnemyEaten()), 591, 37);

	}

	private void updatePowerupGUI(long gameTime) {
		gcScore.setFill(Color.web("#352A55"));
		updateGUIFontSize(12);

		if (isSpeedBoostActivated && speedBoostHolder.equals(myPlanet)) {
			gcScore.setFill(Color.web("#04CBCF"));
			gcScore.fillText("SPEEDBOOST", 676, 36);
			gcScore.drawImage(new Image("/assets/statusBar/speedStatus.png", 13, 14.4, true, false), 742, 17, 15.6, 17.28);
			gcScore.setFill(Color.web("#FE546F"));
			updateGUIFontSize(23);
			gcScore.fillText(String.valueOf((int) (speedBoostInit + 5) - gameTime), 766, 34);
		} else if (isImmunityActivated && immunityHolder.equals(myPlanet)) {
			gcScore.setFill(Color.web("#FF9D7D"));
			gcScore.fillText("IMMUNITY", 676, 36);
			gcScore.drawImage(new Image("/assets/statusBar/immunityStatus.png", 18, 18, true, false), 742, 17, 15.6, 17.28);
			gcScore.setFill(Color.web("#FE546F"));
			updateGUIFontSize(23);
			gcScore.fillText(String.valueOf((int) (immunityInit + 5) - gameTime), 766, 34);
		} else {
			gcScore.setFill(Color.web("#352A55"));
			gcScore.drawImage(new Image("/assets/statusBar/emptyStatus.png", 18, 18, true, false), 742, 17, 15.6, 17.28);
		}
	}

	private void updateGUIFontSize(int fontSize) {
		Font currentFont = gcScore.getFont();
		Font newFont = Font.font(currentFont.getFamily(), fontSize);
		gcScore.setFont(newFont);
	}

	// Spawns enemies at random locations
	private void spawnEnemies() {
		Random rand = new Random();
		for(int i = 0; i < MAX_ENEMIES; i++) {
			int randomX = rand.nextInt(0, GameStage.MAP_WIDTH);
			int randomY = rand.nextInt(0, GameStage.MAP_HEIGHT);
			enemies.add(new EnemyPlanet(Planet.TYPE_ENEMY, randomX, randomY));
		}
	}

	// Renders the enemies
	private void renderEnemies() {
		for(Planet p: enemies) {
			p.render(this.gc);
		}
	}

	// Used for debugging purposes only
//	private void renderBounds() {
//		gc.setStroke(Color.RED);
//		gc.setLineWidth(2);
//		for (Planet p: enemies) {
//			Circle bounds = p.getBounds();
//			gc.strokeOval(bounds.getLayoutX(), bounds.getLayoutY(), bounds.getRadius(), bounds.getRadius());
//		}
//	}

	// Overlays the immune powerup logo to the planet that eats the immune powerup
	public void renderImmuneOverlay(GraphicsContext gc, Planet planet) {
		Image overlay = new Image("/assets/elements/immune.png", planet.getScore()/2, planet.getScore()/2, true, false);
		gc.drawImage(overlay, planet.x - planet.getScore()/4, planet.y - planet.getScore()/4);
	}

	// Creates an asteroid at a random location in the map
	private Asteroids createAsteroid() {
		Random rand = new Random();
		int randomX = rand.nextInt(0, GameStage.MAP_WIDTH);
		int randomY = rand.nextInt(0, GameStage.MAP_WIDTH);

		Asteroids ast = new Asteroids(randomX, randomY, ASTEROID_VALUE);
		asteroidCount++;
		return ast;
	}

	// Spawns initial asteroids
	private void spawnAsteroids() {
		for(int i = 0; i < MAX_ASTEROID; i++) {
			Asteroids ast = createAsteroid();
			asteroids.add(ast);
		}
	}

	// Renders asteroids
	private void renderAsteroids() {
		for(Asteroids a: asteroids) {
			a.render(this.gc);
		}
	}

	// Spawns powerUp within the random location in the viewable screen
	private void spawnPowerUp() {
		Random rand = new Random();
		int randX = rand.nextInt(myPlanet.getX() - 400, myPlanet.getX() + 400);
		int randY = rand.nextInt(myPlanet.getY() - 400, myPlanet.getY() + 400);
		int randType = rand.nextInt(1, 3);

		if (randType == 1) {
			powerUps.add(new SpeedBoost(randX, randY, PowerUp.TYPE_SPEEDBOOST));
		} else if (randType == 2) {
			powerUps.add(new Immunity(randX, randY, PowerUp.TYPE_IMMUNE));
		}
	}

	// Renders powerUp
	private void renderPowerUp() {
		for(PowerUp p: powerUps) {
			p.render(this.gc);
		}
	}

	// Method that will listen and handle the key press events
	private void handleKeyPressEvent() {
		theScene.setOnKeyPressed(new EventHandler<KeyEvent>(){
			public void handle(KeyEvent e){
            	KeyCode code = e.getCode();
                movePlanet(code);
			}
		});
		
		theScene.setOnKeyReleased(new EventHandler<KeyEvent>(){
		            public void handle(KeyEvent e){
		            	KeyCode code = e.getCode();
		                stopPlanet(code);
		            }
		        });
    }
	
	// Method that will move the planet depending on the key pressed
	private void movePlanet(KeyCode ke) {
		if(ke==KeyCode.W) {
			this.myPlanet.setDY(- 1 * myPlanet.getSpeed());
		}

		if(ke==KeyCode.A) {
			this.myPlanet.setDX(- 1 * myPlanet.getSpeed());
		}

		if(ke==KeyCode.S) {
			this.myPlanet.setDY(myPlanet.getSpeed());
		}
		
		if(ke==KeyCode.D) {
			this.myPlanet.setDX(myPlanet.getSpeed());
		}

		/* ------ DEBUGGING PURPOSES ONLY ------ */
		if(ke==KeyCode.C) {
			spawnPowerUp();
		}

		// Displays Game Over Scene
		if(ke == KeyCode.DIGIT4) {
			displayGameOverScene(0, 500);
			this.stop();
		}

		// Displays win condition 1
		if(ke == KeyCode.DIGIT1) {
			displayGameOverScene(1, 500);
			this.stop();
		}

		// Displays win condition 2
		if(ke == KeyCode.DIGIT2) {
			displayGameOverScene(2, 500);
			this.stop();
		}

		// Displays win condition 3
		if(ke == KeyCode.DIGIT3) {
			displayGameOverScene(3, 500);
			this.stop();
		}
		
		System.out.println(ke+" key pressed.");
   	}

	// Method that will stop the player planet's movement; set the planet's DX and DY to 0
	private void stopPlanet(KeyCode ke){
		this.myPlanet.setDX(0);
		this.myPlanet.setDY(0);
	}

	// Displays the gameOver stage with the outcome
	private void displayGameOverScene(int outcome, long gameTime){
		GameOverStage gameOverStage = new GameOverStage(myPlanet, gameTime, outcome);
		gameOverStage.setStage(gs.getStage());
	}
}
