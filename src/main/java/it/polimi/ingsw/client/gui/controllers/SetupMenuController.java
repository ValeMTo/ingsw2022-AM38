package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ConnectionSocket;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

public class SetupMenuController extends GUIController{

    ConnectionSocket connectionSocket;
    @FXML
    private ToggleButton expertToggle;
    @FXML
    private ToggleButton numPlayersToggle;

    // If the user doesn't change the settings, the default ones are  Easy mode and 2 players
    private boolean isExpert = false;
    private int numOfPlayers = 2;


    @FXML
    public void initialize() {
        isExpert = false;
        numOfPlayers = 2;
    }

    // On click on expertToggle
    public void setExpertMode() {
        if (expertToggle.isSelected()) {
            expertToggle.setStyle("-fx-background-color: #46b9e7; ");
            isExpert = true;
        } else {
            expertToggle.setStyle("-fx-background-color: GRAY; ");
            isExpert = false;
        }

    }

    public void startLogin(){

    }

    // On click on numPlayersToggle
    public void changeNumPlayers() {
        if (numPlayersToggle.isSelected()) {
            numPlayersToggle.setText("3");
            numOfPlayers = 3;
        } else {
            numPlayersToggle.setText("2");
            numOfPlayers = 2;
        }

    }
}
