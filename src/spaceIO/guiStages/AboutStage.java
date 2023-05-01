/*
* About stage
*   - Shows the about section of the program, including the dev, sources, etc.
 *      - Class Structure:
 *          StackPane (Root)
 *          |--- VBox
 *               |--- (1) StackPane
 *                      |--- HBox
 *                           |--- Canvas (title)
 *                           |--- CustomButton (back)
 *               |--- (2) Canvas (About Image)
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

// Used in loading custom fonts
import java.io.InputStream;

public class AboutStage {
    // JavaFX Nodes, used on building the scene
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

    // Graphics Contexts for canvases
    private GraphicsContext gcBg;
    private GraphicsContext gcTop;
    private GraphicsContext gcBody;
    private GraphicsContext gcTitle;

    // Back button
    private CustomButton back;

    // Images
    private Image mainBg;
    private Image topBarBg;
    private Image aboutImg;

    // Stores mainMenu so that it won't be loaded again
    private StartStage mainMenu;

    public AboutStage(StartStage mainMenu) {
        // Main Menu
        this.mainMenu = mainMenu;

        // Loading the font
        InputStream is = getClass().getResourceAsStream("/assets/fonts/boston.ttf");
        this.font = Font.loadFont(is, 32);

        // Loading the needed images
        this.mainBg = new Image("/assets/backgrounds/menuBlurredBg.png", GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT, true, false);
        this.topBarBg = new Image("/assets/backgrounds/topBar.png", GameStage.WINDOW_WIDTH, 50, true, false);
        this.aboutImg = new Image("/assets/gui/about.png", GameStage.WINDOW_WIDTH, 750, true, false);

        // Setting up the background
        this.bg = new Canvas(GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
        this.gcBg = bg.getGraphicsContext2D();
        gcBg.setImageSmoothing(false);
        gcBg.drawImage(mainBg, 0, 0, GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT);

        // Setting up the top bar
        this.topBarGradient = new Canvas(GameStage.WINDOW_WIDTH, 50);
        this.gcTop = topBarGradient.getGraphicsContext2D();
        gcTop.setImageSmoothing(false);
        gcTop.drawImage(topBarBg, 0, 0, GameStage.WINDOW_WIDTH, 50);

        // Writing the title text on top bar
        this.title  = new Canvas(170, 50);
        this.gcTitle = title.getGraphicsContext2D();
        gcTitle.setImageSmoothing(false);
        gcTitle.setFont(this.font);
        gcTitle.setFill(Color.web("#FFD080"));
        gcTitle.fillText("ABOUT", 0, 38);

        // Back button
        this.back = new CustomButton("BACK", CustomButton.BUTTON_INVERTED);
        back.renderButton();
        setBackAction();    // Makes back button functional

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
        gcBody.drawImage(aboutImg, 0, 0, 800, 750);

        // Composing the main vbox
        this.mainVBox = new VBox();
        mainVBox.getChildren().addAll(topBarRoot, body);

        // Composing the root
        this.root = new StackPane();
        root.getChildren().addAll(bg, mainVBox);

        // Setting up the scene
        this.scene = new Scene(root, 800, 800);
    }

    // Event listener for back button
    private void setBackAction() {
        back.getCanvas().setOnMouseClicked(event -> {
            mainMenu.setStage(stage);
        });
    }

    // Used to transition to this stage
    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setResizable(false);


        this.stage.setTitle("space.io - About");
        this.stage.setScene(scene);
        this.stage.show();
    }
}
