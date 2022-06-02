package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.MainGUI;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class homeMenuController extends GUIController  {

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
            gui.setNextStage("loginMenu.fxml");
        }
    }


}
