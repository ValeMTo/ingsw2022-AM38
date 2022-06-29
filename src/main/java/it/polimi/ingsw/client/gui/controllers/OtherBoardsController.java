package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.*;

public class OtherBoardsController extends GUIController {

    private final List<Label> entranceStudLabelsP1 = new ArrayList<Label>();   // P1  is the first other player
    private final List<Label> diningStudLabelsP1 = new ArrayList<Label>();
    private final List<Label> entranceStudLabelsP11 = new ArrayList<Label>();   // P11 is the second other player (if there are 3 total players)
    private final List<Label> diningStudLabelsP11 = new ArrayList<Label>();
    private final Map<Tower, Image> towersImgMap = new HashMap<>();
    private final String towersPath = "/graphics/board/towers/";

    private Tower otherPlayer1;
    private Tower otherPlayer11;

    private int numOfPlayers;

    @FXML
    private Label labelPlayer1;
    @FXML
    private Label labelPlayer11;

    @FXML
    private AnchorPane playerboardArea1;
    @FXML
    private AnchorPane playerboardArea11;

    @FXML
    private Label num_towersInBoard1;
    @FXML
    private Label num_playerCoins1;

    @FXML
    private ImageView towersInBoard1;
    @FXML
    private ImageView towersInBoard11;

    @FXML
    private Label num_entRed1;
    @FXML
    private Label num_entYellow1;
    @FXML
    private Label num_entPink1;
    @FXML
    private Label num_entBlue1;
    @FXML
    private Label num_entGreen1;

    @FXML
    private Label num_dinRed1;
    @FXML
    private Label num_dinYellow1;
    @FXML
    private Label num_dinPink1;
    @FXML
    private Label num_dinBlue1;
    @FXML
    private Label num_dinGreen1;


    @FXML
    private Label num_towersInBoard11;
    @FXML
    private Label num_playerCoins11;

    @FXML
    private Label num_entRed11;
    @FXML
    private Label num_entYellow11;
    @FXML
    private Label num_entPink11;
    @FXML
    private Label num_entBlue11;
    @FXML
    private Label num_entGreen11;

    @FXML
    private Label num_dinRed11;
    @FXML
    private Label num_dinYellow11;
    @FXML
    private Label num_dinPink11;
    @FXML
    private Label num_dinBlue11;
    @FXML
    private Label num_dinGreen11;


    @FXML
    public void initialize(){
        createOtherPlayerBoards();
        createTowers();
    }


    public void loadContent() {
        labelPlayer1.setVisible(true);
        playerboardArea1.setVisible(true);
        labelPlayer11.setVisible(true);
        playerboardArea11.setVisible(true);
        // Enables or disables the second "other" playerboard according to the number of players in viewState

        showInfoAlert("Current players in the game", "The players are: " + gui.getViewState().getPlayers());

        numOfPlayers = gui.getViewState().getPlayers().size();

        if (numOfPlayers == 2) {
            labelPlayer11.setVisible(false);
            playerboardArea11.setVisible(false);
        }

        updateOtherSchoolboards();

    }

        public void createOtherPlayerBoards() {

            entranceStudLabelsP1.add(num_entBlue1);
            entranceStudLabelsP1.add(num_entGreen1);
            entranceStudLabelsP1.add(num_entRed1);
            entranceStudLabelsP1.add(num_entYellow1);
            entranceStudLabelsP1.add(num_entPink1);

            diningStudLabelsP1.add(num_dinBlue1);
            diningStudLabelsP1.add(num_dinGreen1);
            diningStudLabelsP1.add(num_dinPink1);
            diningStudLabelsP1.add(num_dinRed1);
            diningStudLabelsP1.add(num_dinYellow1);


            entranceStudLabelsP11.add(num_entBlue11);
            entranceStudLabelsP11.add(num_entGreen11);
            entranceStudLabelsP11.add(num_entRed11);
            entranceStudLabelsP11.add(num_entYellow11);
            entranceStudLabelsP11.add(num_entPink11);

            diningStudLabelsP11.add(num_dinBlue11);
            diningStudLabelsP11.add(num_dinGreen11);
            diningStudLabelsP11.add(num_dinPink11);
            diningStudLabelsP11.add(num_dinRed11);
            diningStudLabelsP11.add(num_dinYellow11);



        }


        public void createTowers() {
            towersImgMap.put(Tower.WHITE, new Image(getClass().getResourceAsStream(towersPath + "white_tower.png")));
            towersImgMap.put(Tower.BLACK, new Image(getClass().getResourceAsStream(towersPath + "black_tower.png")));
            towersImgMap.put(Tower.GRAY, new Image(getClass().getResourceAsStream(towersPath + "gray_tower.png")));
        }


        public void updateOtherSchoolboards() {
            Map<Color, Integer> schoolEntranceOccupancy;
            Map<Color, Integer> diningRoomOccupancy;
            Collection<Tower> otherPlayers = gui.getViewState().getTowers();
            List<Tower> otherPlayersList = gui.getViewState().getTowers().stream().toList();

            for(Tower tower : otherPlayersList) {
                if(!(tower.equals(gui.getViewState().getPlayerTower())))
                    otherPlayer1 = tower;   // keeps only the otherPlayers
            }

            // Updates first other player's board:

            labelPlayer1.setText(gui.getViewState().getNamePlayer(otherPlayer1));
            diningRoomOccupancy = gui.getViewState().getDiningRoomOccupancy(otherPlayer1);
            schoolEntranceOccupancy = gui.getViewState().getSchoolEntranceOccupancy(otherPlayer1);

            int otherPlayer1Coins = gui.getViewState().getPlayerCoins(otherPlayer1);
            num_playerCoins1.setText("x " + otherPlayer1Coins);
            num_towersInBoard1.setText("x " + gui.getViewState().getTowerLeft(otherPlayer1));
            towersInBoard1.setImage(towersImgMap.get(otherPlayer1));

            for(Label label : entranceStudLabelsP1){
                String str = label.getId();
                str = str.replace("num_ent","");
                str = str.replace("1","");
                Color color = Color.toColor(str);
                label.setText("x " + schoolEntranceOccupancy.get(color).toString());
            }

            for(Label label : diningStudLabelsP1){
                String str = label.getId();
                str = str.replace("num_din","");
                str = str.replace("1","");
                Color color = Color.toColor(str);
                label.setText("x " + diningRoomOccupancy.get(color).toString());
            }





            // if there is a third player, it loads and updates the second other playerboard

            if(numOfPlayers == 3) {
;
                for(Tower tower : otherPlayersList) {
                    if(!(tower.equals(gui.getViewState().getPlayerTower())) && !(tower.equals(otherPlayer1))) {
                        otherPlayer11 = tower;   // keeps only the otherPlayers
                    }
                }
                labelPlayer11.setText(gui.getViewState().getNamePlayer(otherPlayer11));
                diningRoomOccupancy = gui.getViewState().getDiningRoomOccupancy(otherPlayer11);
                schoolEntranceOccupancy = gui.getViewState().getSchoolEntranceOccupancy(otherPlayer11);

                int otherPlayer11Coins = gui.getViewState().getPlayerCoins(otherPlayer11);
                num_playerCoins11.setText("x " + otherPlayer11Coins);
                num_towersInBoard11.setText("x " + gui.getViewState().getTowerLeft(otherPlayer11));
                towersInBoard11.setImage(towersImgMap.get(otherPlayer11));

                for(Label label : entranceStudLabelsP11){
                    String str = label.getId();
                    str = str.replace("num_ent","");
                    str = str.replace("11","");
                    Color color = Color.toColor(str);
                    label.setText("x " + schoolEntranceOccupancy.get(color).toString());
                }

                for(Label label : diningStudLabelsP11){
                    String str = label.getId();
                    str = str.replace("num_din","");
                    str = str.replace("11","");
                    Color color = Color.toColor(str);
                    label.setText("x " + diningRoomOccupancy.get(color).toString());
                }
            }


        }


}

