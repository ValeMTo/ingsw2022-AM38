package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import javafx.beans.DefaultProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.sql.Connection;
import java.util.*;

import static it.polimi.ingsw.model.board.Color.toColor;


public class MyBoardGuiController extends GUIController {

    private final String towersPath = "/graphics/board/towers/";
    private final HashMap<Tower, Image> towersImgMap = new HashMap<>();
    private final List<ImageView> deckArray = new ArrayList<ImageView>(10);
    private final List<Label> entranceStudLabels = new ArrayList<Label>();
    private final List<Label> diningRoomStudLabels = new ArrayList<Label>();
    private Tower playerTowerColor;
    private Integer currentPlayerCoins;


    @FXML
    private AnchorPane deckArea;
    @FXML
    private AnchorPane playerboardArea;
    @FXML
    private AnchorPane controlsArea;
    @FXML
    private ImageView testImg;



    @FXML
    private Button showSpecialCards;

    // Imports the assistantCards imageviews
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

    // Imports the SchoolBoard icons imageViews
    @FXML
    private ImageView entRedStud;
    @FXML
    private ImageView entYellowStud;
    @FXML
    private ImageView entPinkStud;
    @FXML
    private ImageView entBlueStud;
    @FXML
    private ImageView entGreenStud;
    @FXML
    private ImageView towersInBoard;
    @FXML
    private ImageView dinRedStud;
    @FXML
    private ImageView dinYellowStud;
    @FXML
    private ImageView dinPinkStud;
    @FXML
    private ImageView dinBlueStud;
    @FXML
    private ImageView dinGreenStud;
    @FXML
    private ImageView playerCoins;

    // Imports the Schoolboard's quantity labels for each icon
    @FXML
    private Label num_entRed;
    @FXML
    private Label num_entYellow;
    @FXML
    private Label num_entPink;
    @FXML
    private Label num_entBlue;
    @FXML
    private Label num_entGreen;
    @FXML
    private Label num_towersInBoard;
    @FXML
    private Label num_dinRed;
    @FXML
    private Label num_dinYellow;
    @FXML
    private Label num_dinPink;
    @FXML
    private Label num_dinBlue;
    @FXML
    private Label num_dinGreen;
    @FXML
    private Label num_playerCoins;


    @FXML
    public void initialize() {
        setupAssistantCards();

    }


    public void setupBoard() {
        // prende le cose da viewState e  fa la setup iniziale di tutti gli elementi della board
        setupAssistantCards();
        setupTowers();
        setupMyPlayerBoard();  // setups the PlayerBoard (schoolboard) by creating it. At first the labels are "x0"


    }

    /**
     * Initially sets the AssistantCards deck in the gui and the corresponding "pickCard()" method.
     * From here onwards,every time a card is picked, it is immediately "disabled" so that it cannot be clicked anymore
     * and its number is sent via the ConnectionSocket class.
     */
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

    public void pickCard(MouseEvent event){
        ImageView clickedImg = (ImageView) event.getSource();
        clickedImg.setEffect(createShadow());
        clickedImg.setDisable(true);
        int cardNum = deckArray.indexOf(clickedImg) +1 ;  // array indexes start at 0  whereas cards go from 1 to 10
        gui.getConnectionSocket().setAssistantCard(cardNum);
        for(ImageView card : deckArray)   // TODO: riabilitare cards al prossimo turno !!!
            card.setDisable(true);
    }

    /**
     * Creates the initial playerboard's arrayLists of labels (both for Entrance and DiningRoom).
     */
    public void setupMyPlayerBoard(){
        entranceStudLabels.add(num_entRed);
        entranceStudLabels.add(num_entYellow);
        entranceStudLabels.add(num_entPink);
        entranceStudLabels.add(num_entBlue);
        entranceStudLabels.add(num_entGreen);

        diningRoomStudLabels.add(num_dinRed);
        diningRoomStudLabels.add(num_dinYellow);
        diningRoomStudLabels.add(num_dinPink);
        diningRoomStudLabels.add(num_dinBlue);
        diningRoomStudLabels.add(num_dinGreen);
    }



    /**
     * Updates the PlayerBoard (schoolboard) of the player by getting the updated occupancies from the ViewState
     * and then sets them to the labels in the Entrance and DiningRoom.  Moreover, it updates the current coins owned
     * by the player.
     */
    public void updateMyPlayerBoard() {
        Map<Color, Integer> schoolEntranceOccupancy;
        Map<Color, Integer> diningRoomOccupancy;
        diningRoomOccupancy = gui.getViewState().getDiningRoomOccupancy(playerTowerColor);
        schoolEntranceOccupancy =  gui.getViewState().getSchoolEntranceOccupancy(playerTowerColor);

        currentPlayerCoins = gui.getViewState().getPlayerCoins(playerTowerColor);
        num_playerCoins.setText("x " + currentPlayerCoins);

        for(Label label : entranceStudLabels){
            String str = label.getId();
            str = str.substring(7);
            System.out.println(str);
            Color color = toColor(str.toUpperCase());
            label.setText("x " + schoolEntranceOccupancy.get(color).toString());
        }

        for(Label label : diningRoomStudLabels){
            String str = label.getId();
            str = str.substring(7);
            Color color = toColor(str);
            label.setText("x " + diningRoomOccupancy.get(color));
        }

    }




    /**
     * Initializes the tower icon in the board, according to the TowerColor assigned to the player.
     */
    public void setupTowers() {
        towersImgMap.put(Tower.WHITE, new Image(getClass().getResourceAsStream(towersPath + "white_tower.png")));
        towersImgMap.put(Tower.BLACK, new Image(getClass().getResourceAsStream(towersPath + "black_tower.png")));
        towersImgMap.put(Tower.GRAY, new Image(getClass().getResourceAsStream(towersPath + "gray_tower.png")));

        playerTowerColor = gui.getViewState().getPlayerTower();   // sets the player's towerColor in the board
        num_towersInBoard.setText(((Integer)gui.getViewState().getNumOfTowers().size()).toString());

        switch (playerTowerColor) {
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


    public void setupProfessors() {

    }

    public void updateProfessors() {
        Map<Color, Tower> professors = gui.getViewState().getProfessors();
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


    private void testMethod(){
        List<Integer> availableCards = gui.getViewState().getUsableCards();
        System.out.println("The usable cards now are : " + availableCards);
    }
}

