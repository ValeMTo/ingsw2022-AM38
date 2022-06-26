package it.polimi.ingsw.client.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class EndController extends GUIController{

    @FXML
    private Button quitButton;
    @FXML
    private Label endText;
    @FXML
    private Label creditsButton;

    @FXML
    public void initialize() {
        quitButton.setOnAction(this::quitGame);
        creditsButton.setOnMouseClicked(this::showCredits);
    }

    @Override
    public void loadContent(){
        endText.setText("");
        //TODO: aggiungere contenuto vincitore
    }

    @FXML
    private void quitGame(ActionEvent event) {
        quit();
    }

    @FXML
    public void showCredits(MouseEvent event) {
        gui.setNextStage("creditsScene.fxml");
    }
}
