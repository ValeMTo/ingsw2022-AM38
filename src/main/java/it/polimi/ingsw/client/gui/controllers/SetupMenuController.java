package it.polimi.ingsw.client.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;

public class SetupMenuController extends GUIController{

    @FXML
    private ToggleButton expertToggle;
    @FXML
    private ToggleButton numPlayersToggle;
    @FXML
    private Button acceptButton;
    @FXML
    private Button quitButton;

    // If the user doesn't change the settings, the default ones are  Easy mode and 2 players
    private boolean isExpert = false;
    private int numOfPlayers = 2;


    @FXML
    public void initialize() {
        isExpert = false;
        numOfPlayers = 2;
        acceptButton.setOnAction(this::acceptPlay);
        quitButton.setOnAction(this::quitGame);
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

    @FXML
    private void acceptPlay(ActionEvent event){
        gui.getConnectionSocket().setGameMode(isExpert);
        gui.getConnectionSocket().setNumberOfPlayers(numOfPlayers);
        LobbyMenuController controller = (LobbyMenuController) gui.getController("lobbyScene.fxml");
        controller.addNicknameInLobby(gui.getViewState().getNickname());
        gui.setNextStage("lobbyScene.fxml");
    }

    public void quitGame(ActionEvent event){
        quit();
    }

    // Pick number of players
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
