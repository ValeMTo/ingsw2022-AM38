package it.polimi.ingsw.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.awt.event.MouseAdapter;
import java.util.Optional;

public class HomeMenuController extends GUIController  {

    @FXML
    private Label creditsButton;



    @FXML
    public void initialize(){
        creditsButton.setOnMouseClicked(this::showCredits);
    }

    /** Method startPlay changes the stage scene to the login scene when the button "Play" is pressed.
     *
     **/
    public void startPlay() {
        gui.setNextStage("loginMenu.fxml");
    }

    public void showCredits(MouseEvent event) {
        gui.setNextStage("creditsScene.fxml");
    }



}


