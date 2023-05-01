/*
 * GameOverStage Class
 *      - Class that handles the stage being shown when the game is over.
 *      - Shows if the user won or not, and how did the user do during the game.
 */

package spaceIO.guiStages;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import spaceIO.guiElements.CustomButton;
import spaceIO.GameStage;
import spaceIO.planets.Planet;

import java.io.InputStream;

public class GameOverStage {
    // Used to show the player's stats
    private Planet playerPlanet;
    private long gameTime;

    // JavaFX's nodes used for composing the scene
    private Stage stage;
    private Scene scene;

    private Canvas bg;
    private StackPane root;
    private BorderPane mainPane;
    private VBox contents;
    private Canvas stats;
    private Canvas separator;

    // Custom button
    private CustomButton mainMenu;
    private CustomButton exit;

    private GraphicsContext gcBg;
    private GraphicsContext gcStats;
    private GraphicsContext gcSep;

    private Image background;
    private Image statsHolder;

    // Texts shown in the scene
    private Text result;
    private Text subTitle;
    private Text statHeader;
    private Font resultFont;
    private Font subTitleFont;

    // Titles and subtitles
    private final String winCond1 = "You were the last planet standing!";
    private final String winCond2 = "Big boi! You attained a score of 2000!";
    private final String winCond3 = "You survived for 150 seconds!";
    private final String loseSubTitle = "Better luck next time!";
    private final String win = "You Win!";
    private final String lose = "Game Over!";

    /*
     *   Outcomes:
     *   0 - Lost
     *   1 - Win - Last Planet Standing
     *   2 - Win - 2000 score
     *   3 - Win - 150 secs
     */

    // Class constructors
    public GameOverStage(Planet planet, long gameTime , int outcome) {
        this.playerPlanet = planet;
        this.gameTime = gameTime;

        // Loading images
        this.background = new Image("/assets/backgrounds/menuBg.png", GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT, true, false);
        this.statsHolder = new Image("/assets/gui/statsHolder.png", 259, 149, true, false);

        // Loading font
        InputStream is = getClass().getResourceAsStream("/assets/fonts/boston.ttf");
        resultFont = Font.loadFont(is, 105);
        InputStream is2 = getClass().getResourceAsStream("/assets/fonts/boston.ttf");
        subTitleFont = Font.loadFont(is2, 27);

        // Setting up the background
        this.bg = new Canvas(GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
        this.gcBg = bg.getGraphicsContext2D();
        gcBg.setImageSmoothing(false);
        gcBg.drawImage(background, 0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);

        // Composing the contents
        this.mainPane = new BorderPane();
        this.contents = new VBox();
        mainPane.setCenter(contents);
        contents.setAlignment(Pos.CENTER);

        // Setting up the results
        switch(outcome) {
            case 0:
                this.result = new Text(lose);
                this.subTitle = new Text(loseSubTitle);
                break;
            case 1:
                this.result = new Text(win);
                this.subTitle = new Text(winCond1);
                break;
            case 2:
                this.result = new Text(win);
                this.subTitle = new Text(winCond2);
                break;
            case 3:
                this.result = new Text(win);
                this.subTitle = new Text(winCond3);
                break;
            default:
                this.result = new Text(lose);
                this.subTitle = new Text("");
        }

        // Styling the texts
        this.result.setFont(resultFont);
        this.subTitle.setFont(subTitleFont);
        this.result.setFill(Color.web("#FFD080"));
        this.subTitle.setFill(Color.web("#FFD080"));
        this.result.setTextAlignment(TextAlignment.CENTER);
        this.subTitle.setTextAlignment(TextAlignment.CENTER);
        this.statHeader = new Text("How did you do?");
        statHeader.setFont(subTitleFont);
        statHeader.setFill(Color.web("#FFD080"));

        // Setting up player's stats
        this.stats = new Canvas(259, 149);
        this.gcStats = stats.getGraphicsContext2D();
        this.gcStats.setImageSmoothing(false);
        fillStats();

        // Separator line
        this.separator = new Canvas(440, 2);
        this.gcSep = separator.getGraphicsContext2D();
        gcSep.setImageSmoothing(false);
        gcSep.setFill(Color.web("#3E3264"));
        gcSep.fillRect(0, 0, separator.getWidth(), separator.getHeight());

        // Custom Buttons and their actions
        this.mainMenu = new CustomButton("Main Menu", CustomButton.BUTTON_NORMAL);
        this.exit = new CustomButton("Exit", CustomButton.BUTTON_NORMAL);
        mainMenu.renderButton();
        exit.renderButton();
        setMainMenuAction();
        setExitAction();

        // Adding it all to the VBox together with the spacing
        // The empty canvases are used for custom spacing
        contents.getChildren().addAll(
                result,
                subTitle,
                new Canvas(1, 35),
                statHeader,
                stats,
                new Canvas(1, 32),
                separator,
                new Canvas(1, 20),
                mainMenu.getCanvas(),
                new Canvas(1, 12),
                exit.getCanvas()
        );

        // Composing the gameover scene
        this.root = new StackPane(bg, mainPane);
        this.scene = new Scene(root, 800, 800);
    }

    // Writes the player's stats into the stat panel
    private void fillStats() {
        gcStats.drawImage(statsHolder, 0, 0, 259, 149);

        gcStats.setFont(subTitleFont);
        updateGUIFontSize(61);
        gcStats.setFill(Color.web("#FE546F"));
        gcStats.fillText(String.valueOf(playerPlanet.getScore() - 40),96, 56);    // Score

        updateGUIFontSize(29);
        gcStats.setFill(Color.web("#352A55"));
        gcStats.fillText(String.valueOf(playerPlanet.getAsteroidsEaten()), 62, 102);    // Asteroids eaten
        gcStats.fillText(String.valueOf(playerPlanet.getEnemyEaten()), 190, 102);   // Planets eaten
        gcStats.fillText(String.valueOf(gameTime), 114, 144);  // Time
    }

    // Changes the font size
    private void updateGUIFontSize(int fontSize) {
        Font currentFont = gcStats.getFont();
        Font newFont = Font.font(currentFont.getFamily(), fontSize);
        gcStats.setFont(newFont);
    }

    // Sets the action for the main menu button
    private void setMainMenuAction() {
        mainMenu.getCanvas().setOnMouseClicked(event -> {
            StartStage startStage = new StartStage();
            startStage.setStage(stage);
        });
    }

    // Sets the action for the exit button
    private void setExitAction() {
        exit.getCanvas().setOnMouseClicked(event -> {
            System.exit(0);
        });
    }

    // Used for stage transitioning
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setResizable(false);


        this.stage.setTitle("space.io - Game Over!");
        this.stage.setScene(scene);
        this.stage.show();
    }
}

