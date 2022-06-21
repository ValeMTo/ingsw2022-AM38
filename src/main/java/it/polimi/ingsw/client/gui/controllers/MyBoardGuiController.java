package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.board.Tower;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;


public class MyBoardGuiController extends GUIController {

    private final String towersPath = "/graphics/board/towers/";

    @FXML
    private AnchorPane deckArea;
    @FXML
    private AnchorPane mySchoolBoard;
    @FXML
    private Button showSpecialCards;

    @FXML
    private ImageView assistant1;

    @FXML
    public void initialize(){
        assistant1.setOnMouseClicked();
    }



    public void setupBoard() {
        // prende le cose da viewState e  fa la setup iniziale di tutti gli elementi della board
        setupAssistantCards();
        setupMySchoolBoard();


    }



    public void setupAssistantCards(){
        deckArea.setVisible(true);
    }

    public void loadTowers() {
        
    }


    private void setupMySchoolBoard(){


        }



    }

}
