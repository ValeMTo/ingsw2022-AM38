package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.board.Tower;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class MyBoardGuiController extends GUIController {

    private final String towersPath = "/graphics/board/towers/";
    private final HashMap<Tower, Image> towersImgMap = new HashMap<>();


    @FXML
    private AnchorPane deckArea;
    @FXML
    private AnchorPane playerboardArea;
    @FXML
    private AnchorPane controlsArea;
    @FXML
    private ImageView testImg;

    @FXML
    private ImageView towersInBoard;

    @FXML
    private Button showSpecialCards;

    @FXML
    private ImageView assistant1;
    @FXML
    private ImageView assistant2;
    @FXML
    private ImageView assistant3;
    @FXML
    private ImageView assistant4;
    @FXML
    private ImageView assistant5;
    @FXML
    private ImageView assistant6;
    @FXML
    private ImageView assistant7;
    @FXML
    private ImageView assistant8;
    @FXML
    private ImageView assistant9;
    @FXML
    private ImageView assistant10;


    @FXML
    public void initialize() {
        // assistant1.setOnMouseClicked();
    }


    public void setupBoard() {
        // prende le cose da viewState e  fa la setup iniziale di tutti gli elementi della board
        setupAssistantCards();
        setupTowers();
        setupMySchoolBoard();


    }


    public void setupAssistantCards() {
        deckArea.setVisible(true);
    }

    public void setupMySchoolBoard() {

    }


    public void setupTowers() {
        towersImgMap.put(Tower.WHITE, new Image(getClass().getResourceAsStream(towersPath + "white_tower.png")));
        towersImgMap.put(Tower.BLACK, new Image(getClass().getResourceAsStream(towersPath + "black_tower.png")));
        towersImgMap.put(Tower.GRAY, new Image(getClass().getResourceAsStream(towersPath + "gray_tower.png")));

        Tower towerColor = gui.getViewState().getPlayerTower();

        switch (towerColor) {
            case WHITE:
                towersInBoard.setImage(towersImgMap.get(Tower.WHITE));
                break;
            case BLACK:
                towersInBoard.setImage(towersImgMap.get(Tower.BLACK));
                break;
            case GRAY:
                towersInBoard.setImage(towersImgMap.get(Tower.GRAY));
                break;
        }
    }


}

