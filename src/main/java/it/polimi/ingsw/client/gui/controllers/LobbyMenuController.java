package it.polimi.ingsw.client.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;



public class LobbyMenuController extends GUIController {

    @FXML
    private ListView<String> listOnlinePlayers= new ListView<>();;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button quitButton;

    @FXML
    public void initialize() {
        quitButton.setOnAction(this::quitGame);
    }


    @FXML
    private void quitGame(ActionEvent event) {
        quit();
    }

    public void addNicknameInLobby(String nickname) {
        listOnlinePlayers.getItems().add(nickname);
        if (gui.getViewState().getNickname().equals(nickname)){
            welcomeLabel.setText("Welcome in Eriantys lobby!");
        } else {
            welcomeLabel.setText(nickname + "just arrived!");
        }
        /*
        * Settare tutti i nomi nella lobby in una tabella
        */
    }

}
