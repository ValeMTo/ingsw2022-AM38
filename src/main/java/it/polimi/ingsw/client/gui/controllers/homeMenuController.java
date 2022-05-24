package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.MainGUI;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class homeMenuController implements GUIController  {

    private MainGUI gui;



    /** Method startPlay changes the stage scene to the login scene when the button "Play" is pressed.
     *
     **/
    public void startPlay() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Start a game");
        alert.setHeaderText("Do you want to start a new game ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            gui.changeStage("loginMenu.fxml");
        }
    }

    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit game");
        alert.setHeaderText("Are you sure you want to exit the game ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Thanks for playing with Eriantys. Bye!");
            System.exit(0);
        }

    }


    @Override
    public void initGUI(MainGUI gui) {
        this.gui = gui;
    }



}
