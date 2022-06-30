package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;


public class CreditsSceneController extends GUIController {

    private static final String ourCredits = "Developed as a group project for the Software Engineering 2022 Course at Politecnico di Milano.\nTeacher : Alessandro Margara\nProgramming language : Java\nLast update: July 2022";

    @FXML
    private Button returnButton;
    @FXML
    private Label creditsLabel;

    @FXML
    private Button demoEndingButton;




    @FXML
    public void initialize() {
        returnButton.setOnAction(this::returnToHome);
        creditsLabel.setText(ourCredits);

        demoEndingButton.setOnAction(this::showDemoEnding);
    }

    @FXML
    public void returnToHome(ActionEvent event) {
        gui.setNextStage("homeMenu.fxml");
    }

    @FXML
    public void showDemoEnding(ActionEvent event){
        gui.setNextStage("endScene.fxml");
    }


}
