/*
 *  Instructions Stage Class
 *      - Class that handles the instructions on how to play the game
 *      - Class Structure:
 *          StackPane (Root)
 *          |--- VBox
 *               |--- (1) StackPane
 *                      |--- HBox
 *                           |--- Canvas (title)
 *                           |--- CustomButton (back)
 *               |--- (2) Canvas (Instructions Image)
 *          |--- Canvas (background)
 */
package spaceIO.guiStages;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import spaceIO.guiElements.CustomButton;
import spaceIO.GameStage;

import java.io.InputStream;

public class InstructionsStage {
    // JavaFX's nodes used to build the scene
    private Scene scene;
    private Stage stage;
    private Canvas bg;
    private StackPane root;
    private VBox mainVBox;
    private StackPane topBarRoot;
    private HBox topBarMain;
    private Canvas topBarGradient;
    private Canvas title;
    private Canvas body;
    private Font font;

    private GraphicsContext gcBg;
    private GraphicsContext gcTop;
    private GraphicsContext gcBody;
    private GraphicsContext gcTitle;

    // Back button
    private CustomButton back;

    private Image mainBg;
    private Image topBarBg;
    private Image instructionsImg;

    // Stores the mainMenu so that it won't have to be loaded again
    private StartStage mainMenu;

    public InstructionsStage(StartStage mainMenu) {
        // Main Menu
        this.mainMenu = mainMenu;

        // Loading the font
        InputStream is = getClass().getResourceAsStream("/assets/fonts/boston.ttf");
        this.font = Font.loadFont(is, 32);

        // Loading the needed images
        this.mainBg = new Image("/assets/backgrounds/menuBlurredBg.png", GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT, true, false);
        this.topBarBg = new Image("/assets/backgrounds/topBar.png", GameStage.WINDOW_WIDTH, 50, true, false);
        this.instructionsImg = new Image("/assets/gui/instructions.png", GameStage.WINDOW_WIDTH, 750, true, false);

        // Setting up the background
        this.bg = new Canvas(GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
        this.gcBg = bg.getGraphicsContext2D();
        gcBg.setImageSmoothing(false);
        gcBg.drawImage(mainBg, 0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_WIDTH);

        // Setting up the top bar
        this.topBarGradient = new Canvas(GameStage.WINDOW_WIDTH, 50);
        this.gcTop = topBarGradient.getGraphicsContext2D();
        gcTop.setImageSmoothing(false);
        gcTop.drawImage(topBarBg, 0, 0, 800, 50);

        // Writing the title text on top bar
        this.title  = new Canvas(170, 50);
        this.gcTitle = title.getGraphicsContext2D();
        gcTitle.setImageSmoothing(false);
        gcTitle.setFont(this.font);
        gcTitle.setFill(Color.web("#FFD080"));
        gcTitle.fillText("HOW TO PLAY", 0, 38);

        // Back button
        this.back = new CustomButton("BACK", CustomButton.BUTTON_INVERTED);
        back.renderButton();
        setBackAction();

        // Composing the topBar
        this.topBarMain = new HBox();
        topBarMain.getChildren().addAll(
                new Canvas(47, 1),
                title,
                new Canvas(495, 1),
                back.getCanvas()
        );

        this.topBarRoot = new StackPane();
        topBarRoot.getChildren().addAll(topBarGradient, topBarMain);



        // Setting up the main instructions
        this.body = new Canvas(800,750);
        this.gcBody = body.getGraphicsContext2D();
        gcBody.setImageSmoothing(false);
        gcBody.drawImage(instructionsImg, 0, 0, 800, 750);

        // Composing the main vbox
        this.mainVBox = new VBox();
        mainVBox.getChildren().addAll(topBarRoot, body);

        // Composing the root
        this.root = new StackPane();
        root.getChildren().addAll(bg, mainVBox);

        // Setting up the scene
        this.scene = new Scene(root, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);
    }

    // Sets the back button's action
    private void setBackAction() {
        back.getCanvas().setOnMouseClicked(event -> {
            mainMenu.setStage(stage);
        });
    }

    // Used for transitioning between scenes
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setResizable(false);


        this.stage.setTitle("space.io - How to Play?");
        this.stage.setScene(scene);
        this.stage.show();
    }
}
