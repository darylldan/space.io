/*
 * Custom Button Class
 *      - A GUI element class that I made that is supposed to be used as a button.
 *      - I can't seem to figure out how to modify the button's appearance in the JavaFX's own button class, so
 *        I made this class that is using a canvas for easy visual manipulation while utilizing the canvas'
 *        mouse event listeners to make it act like a button.
 *      - This class is in no way universal and is created to work only with space.io.
 *
 */
package spaceIO.guiElements;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

// Used in loading the font
import java.io.InputStream;

public class CustomButton {
    // Types of button styles that CustomButton can do
    public final static String BUTTON_BIG = "big";
    public final static String BUTTON_NORMAL = "normal";
    public final static String BUTTON_INVERTED = "inverted";

    private Canvas canvas;
    private GraphicsContext gc;
    private String label;
    private String type;
    private Font font;
    private long fontSize;

    // Class constructor
    public CustomButton(String label, String buttonType) {
        this.type = buttonType;

        // Different buttons have different sizes and font sizes
        switch(buttonType) {
            case BUTTON_BIG:
                canvas = new Canvas(300, 66);
                fontSize = 60;
                break;
            case BUTTON_NORMAL:
                canvas = new Canvas(300, 30);
                fontSize = 25;
                break;
            case BUTTON_INVERTED:
                canvas = new Canvas(85, 50);
                fontSize = 28;
                break;
            default:
                canvas = new Canvas(300, 19);
                fontSize = 24;
        }

        // Other necessary formatting that needs to be done
        this.label = label.toUpperCase();
        this.gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        // Loads the font
        loadFont();

        // Sets the listener for the hover effect
        setHoverHandler();
    }

    // Loads custom ttf font
    private void loadFont() {
        InputStream is = getClass().getResourceAsStream("/assets/fonts/boston.ttf");
        this.font = Font.loadFont(is, fontSize);
    }

    // Draws the button into the canvas
    /*
     * I think this method can be included in the class constructor, but I implemented it this way to match the
     * Sprite class that was provided to us. This implementation however can make it so that the developer can render
     * the button whenever he wanted but it could also be a bug source as it can be easily forgotten to call this function.
     */
    public void renderButton() {
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.setFont(font);

        if (type.equals(BUTTON_INVERTED)) {
            gc.setFill(Color.web("#FFD080"));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.web("#352A55"));
            gc.fillText(label, Math.round(canvas.getWidth() / 2), Math.round(canvas.getHeight() / 2) + 3);
        } else {
            gc.setFill(Color.web("#FFD080"));
            gc.fillText(label, Math.round(canvas.getWidth() / 2), Math.round(canvas.getHeight() / 2) + 3);
        }

    }

    // Redesigns the button when a mouse is being hovered.
    private void setHoverHandler() {
        gc.clearRect(0,0, canvas.getWidth(), canvas.getHeight());
        canvas.setOnMouseEntered(event -> {
            if (type.equals(BUTTON_INVERTED)) {
                gc.setFill(Color.web("#FFD080"));
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.setFill(Color.web("#FE546F"));
                gc.fillText(label, Math.round(canvas.getWidth() / 2), Math.round(canvas.getHeight() / 2) + 3);
            } else {
                gc.setFill(Color.web("#FFD080"));
                gc.fillRect(0,0 ,canvas.getWidth(), canvas.getHeight());
                gc.setFill(Color.web("#352A55"));
                gc.fillText(label, Math.round(canvas.getWidth() / 2), Math.round(canvas.getHeight() / 2) + 3);
            }
        });

        canvas.setOnMouseExited(event -> {
            renderButton();
        });
    }

    // Returns the canvas, mostly used in adding the button to panes, or setting the action for when the button is clicked.
    public Canvas getCanvas() {
        return canvas;
    }
}

