/*
 * GameStage
 * 		- Main GameStage for the game.
 */
package spaceIO;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GameStage {
	// Window properties
	public static final int WINDOW_HEIGHT = 800;
	public static final int WINDOW_WIDTH = 800;

	// Map properties
	public static final int MAP_HEIGHT = 2400;
	public static final int MAP_WIDTH = 2400;

	// JavaFX's nodes used to build the scene.
	private Scene scene;
	private Stage stage;
	private Canvas canvas;
	private Canvas background;
	private Canvas scoreBoard;
	private Canvas scoreBooardBg;
	private VBox root;
	private ScrollPane scrollPane;
	private StackPane stackPane;
	private StackPane stackPaneSB;

	private GraphicsContext gc;
	private GraphicsContext gcScore;
	private GraphicsContext gcB;

	// Main game timer, where most of the game logic is found
	private GameTimer gametimer;

	// Class constructor
	public GameStage() {
		// Composing the main scene
		this.root = new VBox();
		this.canvas = new Canvas(GameStage.MAP_WIDTH, GameStage.MAP_HEIGHT);
		this.background = new Canvas(GameStage.MAP_WIDTH, GameStage.MAP_HEIGHT);
		this.scoreBoard = new Canvas(WINDOW_WIDTH, 50);
		this.stackPane = new StackPane(background, canvas);
		this.scoreBooardBg = new Canvas(WINDOW_WIDTH, 50);
		this.gc = canvas.getGraphicsContext2D();
		this.gcScore = scoreBoard.getGraphicsContext2D();
		this.gcB = background.getGraphicsContext2D();
		gcB.setImageSmoothing(false);	// Makes the images crisper in canvas
		this.stackPaneSB = new StackPane(scoreBooardBg, scoreBoard);

		gcB.drawImage(new Image("/assets/backgrounds/gameBg.png"), 0,0, MAP_WIDTH, MAP_HEIGHT);
		this.scrollPane = new ScrollPane(stackPane);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		this.scoreBooardBg.getGraphicsContext2D().drawImage(new Image("/assets/backgrounds/statusbar.png", 800, 50, true, true), 0, 0);

		this.scene = new Scene(root, GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);

		// Instantiate an animation timer
		this.gametimer = new GameTimer(this.gc, this.scene, this.scrollPane, this.gcScore, this);
	}

	// Method to add the stage elements
	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setResizable(false);
		
		// Set stage elements here
		this.root.getChildren().addAll(stackPaneSB, scrollPane);
		this.stage.setTitle("space.io");
		this.stage.setScene(this.scene);

		//Invoke the start method of the animation timer
		this.gametimer.start();
		this.stage.show();
	}

	// Used to "zoom out" of the map
	public void setScale(double scale) {
		canvas.setScaleX(scale);
		canvas.setScaleY(scale);
		background.setScaleX(scale);
		background.setScaleY(scale);
	}

	public Stage getStage() {
		return stage;
	}
}

