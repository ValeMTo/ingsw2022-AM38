package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;


public class LobbyMenuController extends GUIController{

    private String nicknameInLobby;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button quitButton;

    @FXML
    public void initialize() {
        welcomeLabel.setText("Hello " + nicknameInLobby + " !");
        quitButton.setOnAction(this::quitGame);
    }


    @FXML
    private void  quitGame(ActionEvent event){
        quit();
    }

    public void setNicknameInLobby(String nickname) {
        nicknameInLobby = nickname;
    }

}
