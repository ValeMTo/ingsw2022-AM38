package it.polimi.ingsw.client.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EndController extends GUIController{

    @FXML
    private Button quitButton;
    @FXML
    private Label endText;
    @FXML
    private Label creditsButton;
    @FXML
    private ImageView motherNatureFinal;
    @FXML
    private Label leaderboard;
    @FXML
    private Label winnerMessage;

    @FXML
    public void initialize() {
        quitButton.setOnAction(this::quitGame);
        creditsButton.setOnMouseClicked(this::showCredits);
        endText.setText("the game has ended...");
        winnerMessage.setText("");

    }

    @Override
    public void loadContent(){

        endText.setText("The game has ended because of :" +  gui.getViewState().getEndingMotivation());

        Map<String,Integer> standing = gui.getViewState().getLeaderBoard();

        if(standing.get(gui.getViewState().getNickname()) == 1) {
            if(!isDraw(standing)) {
                winnerMessage.setText("Congratulations! You have won!");
            }
            else {
                winnerMessage.setText("The Game was tied!");
            }
        }
        else {
            winnerMessage.setText("Sorry... you have lost.");
        }

        // Displaying the final leaderboard :
        String outputLeaderboard = "";
        int stand = 1;
        List<String> standingNicknames = standing.keySet().stream().collect(Collectors.toList());
        while(!standing.isEmpty()){
            for(String s: standingNicknames)
                if(standing.containsKey(s))
                    if(standing.get(s)==stand) {
                        switch (stand) {
                            case 1:
                                outputLeaderboard = outputLeaderboard + stand + "st: " + s + " with " + gui.getViewState().getTowerLeft(gui.getViewState().getPlayerTower(s)) + " towers left \n";
                                break;
                            case 2:
                                outputLeaderboard = outputLeaderboard + stand + "nd: " + s + " with " + gui.getViewState().getTowerLeft(gui.getViewState().getPlayerTower(s)) + " towers left \n";
                                break;
                            case 3:
                                outputLeaderboard = outputLeaderboard + stand + "rd: " + s + " with " + gui.getViewState().getTowerLeft(gui.getViewState().getPlayerTower(s)) + " towers left \n";
                                break;
                        }
                        standing.remove(s);
                    }
            stand++;
        }

        leaderboard.setText(outputLeaderboard);

    }


    private boolean isDraw(Map<String, Integer> standing){
        int firstClassified = 0;
        for(String s: standing.keySet()){
            if(standing.get(s)==1)firstClassified++;
        }
        if(firstClassified>1)
            return true;
        return false;
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
