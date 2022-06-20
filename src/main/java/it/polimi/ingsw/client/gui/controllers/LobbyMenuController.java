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
    private Label gameMode;
    @FXML
    private Label expectedPlayers;

    @FXML
    public void initialize() {
        quitButton.setOnAction(this::quitGame);
    }


    @FXML
    private void quitGame(ActionEvent event) {
        quit();
    }

    public void setLobbySettings(){
        Integer numPlayers = gui.getViewState().getGameSettings().getNumPlayers();
        expectedPlayers.setText("> Expected players :  " + numPlayers.toString());
        if(gui.getViewState().isExpert()){
            gameMode.setText("> GameMode :  " + "Expert");
        }
        else{
            gameMode.setText("> GameMode :  " + "Easy");
        }
    }

    public void addNicknameInLobby(String nickname) {
        if (gui.getViewState().getNickname().equals(nickname)){
            welcomeLabel.setText("Hello " + nickname + "!  Welcome in Eriantys lobby!");
        } else {
            welcomeLabel.setText(nickname + " just arrived!");
        }
        listOnlinePlayers.getItems().clear();   // needed to refresh the list of online players before updating it
        listOnlinePlayers.getItems().addAll(gui.getViewState().getOnlinePlayers());

    }







}


