package it.polimi.ingsw.client.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class MyBoardGuiController extends GUIController {


    @FXML
    private AnchorPane deck;
    @FXML
    private AnchorPane mySchoolBoard;

    @FXML
    public void initialize(){

    }


    public void setupBoard() {
        // prende le cose da viewState e  fa la setup iniziale di tutti gli elementi della board
        setupAssistantCards();
        setupMySchoolBoard();

    }



    private void setupAssistantCards(){
        deck.setVisible(true);
    }

    private void setupMySchoolBoard(){

    }

}
