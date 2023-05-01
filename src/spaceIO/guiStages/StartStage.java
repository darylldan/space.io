/*
 * StartStage Class or the Main Menu
 *      - Class that handles the main menu
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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import spaceIO.guiElements.CustomButton;
import spaceIO.GameStage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class StartStage {
    // Java FX Nodes that are used to compose the scene
    private Stage stage;
    private Scene scene;
    private StackPane root;
    private BorderPane mainLayout;
    private VBox centerVBox;
    private Canvas bg;
    private GraphicsContext gc;
    private Image bgImg;
    private Font font;

    // Main logo which contains a random custom pre-defined splash-text (like Minecraft)
    private Canvas mainLogo;
    private GraphicsContext gcLogo;
    private ArrayList<String> splashTexts;  // Stores the custom splash texts
    private int splashTextIndex; // A random index
    private Image logo;

    // CustomButtons
    private CustomButton play;
    private CustomButton instructions;
    private CustomButton about;
    private CustomButton exit;

    // Canvases used for spacing
    private Canvas spacing1;
    private Canvas spacing2;

    // Pre-loads the other stages
    private InstructionsStage instructionsStageWindow;
    private AboutStage aboutStageWindow;


    public StartStage() {
        // Loading the font
        InputStream is = getClass().getResourceAsStream("/assets/fonts/boston.ttf");
        this.font = Font.loadFont(is, 12);

        // Creating the background
        this.bg = new Canvas(GameStage.WINDOW_WIDTH,GameStage.WINDOW_HEIGHT);
        this.gc = bg.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        this.bgImg = new Image("/assets/backgrounds/menuBg.png", GameStage.WINDOW_WIDTH, GameStage.WINDOW_HEIGHT, true, false);
        setBg();

        // Creating the StackPane
        this.root = new StackPane();

        // Creating the Main layout
        this.mainLayout = new BorderPane();
        this.centerVBox = new VBox();
        centerVBox.setAlignment(Pos.CENTER);
        mainLayout.setCenter(centerVBox);


        // Lay outing the contents for centerVBox

        // Main Logo with splash text
        this.splashTexts = new ArrayList<>();
        readFile();
        this.splashTextIndex = new Random().nextInt(0,20);  // Picks a random text from the array
        this.mainLogo = new Canvas(350,115);
        this.gcLogo = mainLogo.getGraphicsContext2D();
        gcLogo.setImageSmoothing(false);

        this.logo = new Image("assets/gui/mainLogo.png", 349.6973, 115.4766, true, false);
        gcLogo.drawImage(logo, 0,0 );
        writeSplashText();      // Writes the splash text

        // Buttons
        this.play = new CustomButton("PLAY", CustomButton.BUTTON_BIG);
        this.instructions = new CustomButton("HOW TO PLAY", CustomButton.BUTTON_NORMAL);
        this.about = new CustomButton("ABOUT", CustomButton.BUTTON_NORMAL);
        this.exit = new CustomButton("EXIT", CustomButton.BUTTON_NORMAL);


        // Spacing
        this.spacing1 = new Canvas(1, 120);
        this.spacing2 = new Canvas(1, 15);

        // Adding contents to VBox
        centerVBox.getChildren().addAll(
                mainLogo,
                spacing1,
                play.getCanvas(),
                spacing2,
                instructions.getCanvas(),
                new Canvas(1, 4),
                about.getCanvas(),
                new Canvas(1, 4),
                exit.getCanvas()
        );

        // Rendering buttons
        play.renderButton();
        instructions.renderButton();
        about.renderButton();
        exit.renderButton();

        // Actions for buttons
        setInstructionsAction();
        setExitAction();
        setAboutAction();
        setPlayAction();

        // Writing the footer
        writeFooter();

        // Setting up scene
        this.scene = new Scene(root, 800, 800);
        this.root.getChildren().addAll(bg, mainLayout);

        // Preloading other screens
        this.instructionsStageWindow = new InstructionsStage(this);
        this.aboutStageWindow = new AboutStage(this);
    }

    // Draws the scene's background image
    private void setBg() {
        gc.drawImage(bgImg, 0,0, 800, 800);
    }

    // Writes the spash text into the logo
    private void writeSplashText() {
        Font newFont = Font.font(font.getFamily(), 15);
        gcLogo.setFont(newFont);
        gcLogo.setFill(Color.web("#352A55"));
        gcLogo.setTextAlign(TextAlignment.RIGHT);
        gcLogo.fillText(splashTexts.get(splashTextIndex), 342, 111);
    }

    // Writes the footer in the mainMenu
    private void writeFooter() {
        gc.setFont(font);
        gc.setFill(Color.web("#FFD080"));

        gc.fillText("A REQUIREMENT FOR CMSC 22 - UPLB", 10, 790);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText("@daryll#9975 on Discord <-> Version 1.0".toUpperCase(), 790, 790);
    }

    // Reads the custom splash texts stored in src/assets/misc/splash.txt
    private void readFile() {
        try {
            File splashTxt = new File("src/assets/misc/splash.txt");
            Scanner read = new Scanner(splashTxt);

            while(read.hasNextLine()) {
                String text = read.nextLine();
                splashTexts.add(text);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }

    }

    // Used to transitions to this stage
    public void setStage(Stage stage) {
        this.stage = stage;

        stage.setResizable(false);

        this.stage.setTitle("space.io - " + splashTexts.get(splashTextIndex));
        this.stage.setScene(scene);
        this.stage.show();
    }

    /* ----------- Actions to navigate to their respective scenes ----------- */
    private void setInstructionsAction() {
        instructions.getCanvas().setOnMouseClicked(event -> {
            instructionsStageWindow.setStage(stage);
        });
    }

    private void setAboutAction() {
        about.getCanvas().setOnMouseClicked(event -> {
            aboutStageWindow.setStage(stage);
        });
    }

    private void setExitAction() {
        exit.getCanvas().setOnMouseClicked(event -> {
            System.exit(0);
        });
    }

    // Starts a new space.io game
    private void setPlayAction() {
        play.getCanvas().setOnMouseClicked(event -> {
            GameStage theGameStage = new GameStage();
            theGameStage.setStage(stage);
        });
    }
}

