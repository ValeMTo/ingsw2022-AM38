package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;


public class AcceptConditionsMenuController extends GUIController{

    @FXML
    private Label numberOfPlayersBox;
    @FXML
    private Label gamemodeBox;
    @FXML
    private Button acceptButton;
    @FXML
    private Button quitButton;
    @FXML
    private Label messageBox;
    @FXML
    private AnchorPane settingsBox;


    @FXML
    public void initialize(){
        acceptButton.setOnAction(this::acceptPlay);
        quitButton.setOnAction(this::quitGame);
    }

    @FXML
    private void  acceptPlay(ActionEvent event){
        gui.getConnectionSocket().acceptRules();
        LobbyMenuController controller = (LobbyMenuController) gui.getController("lobbyScene.fxml");
        controller.addNicknameInLobby(gui.getViewState().getNickname());
        for(String name : gui.getViewState().getOnlinePlayers()){
            controller.addNicknameInLobby(name);
        }
        gui.setNextStage("lobbyScene.fxml");
    }

    @FXML
    private void  quitGame(ActionEvent event){
        quit();
    }


    @Override
    public void showSettings() throws FunctionNotImplementedException {
        Integer numOfPlayers = gui.getViewState().getGameSettings().getNumPlayers();
        numberOfPlayersBox.setText(numOfPlayers.toString());

        if(gui.getViewState().getGameSettings().getExpert()){
            gamemodeBox.setText("Expert mode");
        } else {
            gamemodeBox.setText("Easy mode");
        }

    }





}
