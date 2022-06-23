package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.board.Tower;
import javafx.beans.DefaultProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


public class MyBoardGuiController extends GUIController {

    private final String towersPath = "/graphics/board/towers/";
    private final HashMap<Tower, Image> towersImgMap = new HashMap<>();
    private final List<ImageView> deckArray = new ArrayList<ImageView>(10);


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
        setupAssistantCards();

    }


    public void setupBoard() {
        // prende le cose da viewState e  fa la setup iniziale di tutti gli elementi della board
        setupAssistantCards();
        setupTowers();
        setupMySchoolBoard();


    }


    public void setupAssistantCards() {

        deckArray.add(assistant1);
        deckArray.add(assistant2);
        deckArray.add(assistant3);
        deckArray.add(assistant4);
        deckArray.add(assistant5);
        deckArray.add(assistant6);
        deckArray.add(assistant7);
        deckArray.add(assistant8);
        deckArray.add(assistant9);
        deckArray.add(assistant10);

        deckArea.setVisible(true);
        for(ImageView img : deckArray) {
            img.setOnMouseClicked(this::pickCard);
        }
    }

    private void pickCard(MouseEvent event){
        ImageView clickedImg = (ImageView) event.getSource();
        clickedImg.setEffect(createShadow());
        clickedImg.setDisable(true);
        int cardNum = deckArray.indexOf(clickedImg) +1 ;  // array indexes start at 0  whereas cards go from 1 to 10
        System.out.println(cardNum);
        gui.getConnectionSocket().setAssistantCard(cardNum);
    }


    public void setupMySchoolBoard() {

    }

    /**
     * Initializes the tower icon in the board, according to the TowerColor assigned to the player.
     */
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

    /**
     * Creates a shadow effect to be applied to the used AssistantCards
     * @return the ColorAdjust effect element to be applied to the imageview of the assistantCard
     */
    private ColorAdjust createShadow(){
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(-0.32);
        colorAdjust.setBrightness(-0.59);
        colorAdjust.setSaturation(-1.0);
        return colorAdjust;
    }


}

