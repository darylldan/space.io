/*
 *	space.io - an agar.io-like game created as a mini project for CMSC 22
 *
 * 	- This program is build on JavaFX and utilizes the AnimationTimer class to have an actual game logic working every
 *    frame. All the assets except the milky way vector and the font is created by me.
 *
 * 	@author : Daryll Dan C. Caponpon (base code is provided by the CMSC 22 teaching staff)
 *  @studentNo: 2021-68061
 */

package spaceIO;

import javafx.application.Application;
import javafx.stage.Stage;
import spaceIO.guiStages.StartStage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {
		StartStage startStage = new StartStage();
		startStage.setStage(stage);
	}

}
