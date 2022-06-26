package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.model.board.Cloud;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Glow;
import javafx.scene.effect.Shadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import javafx.stage.Stage;

import java.util.*;
import java.util.List;

import static it.polimi.ingsw.model.board.Color.toColor;


public class MyBoardGuiController extends GUIController {

    private final String towersPath = "/graphics/board/towers/";
    private final HashMap<Tower, Image> towersImgMap = new HashMap<>();
    private final List<ImageView> deckArray = new ArrayList<ImageView>(10);
    private final ArrayList<Label> entranceStudLabels = new ArrayList<Label>();
    private final ArrayList<ImageView> entranceStudImages = new ArrayList<ImageView>(5);
    private final ArrayList<Label> diningRoomStudLabels = new ArrayList<Label>();
    private final ArrayList<Label> professorsLabels = new ArrayList<Label>(5);
    private final ArrayList<ImageView> archipelagoIslands = new ArrayList<ImageView>();
    private final ArrayList<ImageView> towersOnIslands = new ArrayList<ImageView>();
    private final ArrayList<ImageView> motherNatureOnIslands = new ArrayList<ImageView>();
    private final ArrayList<Line> islandBridges = new ArrayList<Line>();
    private final ArrayList<Label> showContentLabels = new ArrayList<Label>();
    private final ArrayList<ImageView> cloudsImgArray = new ArrayList<ImageView>();
    private final ArrayList<ImageView> noEntryTilesArray = new ArrayList<ImageView>();


    private Integer currentPlayerCoins;


    @FXML
    private AnchorPane deckArea;
    @FXML
    private AnchorPane playerboardArea;
    @FXML
    private AnchorPane entranceStudentsArea;
    @FXML
    private AnchorPane diningRoomStudentsArea;
    @FXML
    private AnchorPane controlsArea;
    @FXML
    private AnchorPane showContentArea;
    @FXML
    private AnchorPane archipelagoArea;
    @FXML
    private Label showContentLabel;
    @FXML
    private ImageView testImg;
    @FXML
    private AnchorPane cloudsArea;
    @FXML
    private AnchorPane professorsArea;

    @FXML
    private Button showSpecialCardsButton;
    @FXML
    private Label statusMessage;
    @FXML
    private Label statusHeader;

    private Stage stage;

    @FXML
    private Button showOtherBoardsButton;

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
    private ImageView contentRed;    // icon of the red student in the ShowContent area
    @FXML
    private ImageView contentYellow;
    @FXML
    private ImageView contentPink;
    @FXML
    private ImageView contentBlue;
    @FXML
    private ImageView contentGreen;

    @FXML
    private ImageView islandTower;
    @FXML
    private Label numTowers;

    @FXML
    private Label num_contentRed;   // label for the number of red students in the ShowContent Area
    @FXML
    private Label num_contentYellow;
    @FXML
    private Label num_contentPink;
    @FXML
    private Label num_contentBlue;
    @FXML
    private Label num_contentGreen;

    @FXML
    private ImageView profRed;      // ImageView of the Red professor
    @FXML
    private ImageView profYellow;
    @FXML
    private ImageView profPink;
    @FXML
    private ImageView profBlue;
    @FXML
    private ImageView profGreen;

    @FXML
    private Label red_owner;    // label for the owner of the red professor
    @FXML
    private Label yellow_owner;
    @FXML
    private Label pink_owner;
    @FXML
    private Label blue_owner;
    @FXML
    private Label green_owner;

    @FXML
    private ImageView cloud1;   // Imageview of the cloud #1
    @FXML
    private ImageView cloud2;
    @FXML
    private ImageView cloud3;

    @FXML
    private ImageView island1;   // ImageView of the island #1
    @FXML
    private ImageView island2;
    @FXML
    private ImageView island3;
    @FXML
    private ImageView island4;
    @FXML
    private ImageView island5;
    @FXML
    private ImageView island6;
    @FXML
    private ImageView island7;
    @FXML
    private ImageView island8;
    @FXML
    private ImageView island9;
    @FXML
    private ImageView island10;
    @FXML
    private ImageView island11;
    @FXML
    private ImageView island12;

    @FXML
    private Button moveDiningRoomButton;
    @FXML
    private Button moveIslandButton;

    @FXML
    private ImageView towerIsland1;   // ImageView of the tower on the island #1
    @FXML
    private ImageView towerIsland2;
    @FXML
    private ImageView towerIsland3;
    @FXML
    private ImageView towerIsland4;
    @FXML
    private ImageView towerIsland5;
    @FXML
    private ImageView towerIsland6;
    @FXML
    private ImageView towerIsland7;
    @FXML
    private ImageView towerIsland8;
    @FXML
    private ImageView towerIsland9;
    @FXML
    private ImageView towerIsland10;
    @FXML
    private ImageView towerIsland11;
    @FXML
    private ImageView towerIsland12;

    @FXML
    private ImageView motherIsland1;   // ImageView of the motherNature on the island #1
    @FXML
    private ImageView motherIsland2;
    @FXML
    private ImageView motherIsland3;
    @FXML
    private ImageView motherIsland4;
    @FXML
    private ImageView motherIsland5;
    @FXML
    private ImageView motherIsland6;
    @FXML
    private ImageView motherIsland7;
    @FXML
    private ImageView motherIsland8;
    @FXML
    private ImageView motherIsland9;
    @FXML
    private ImageView motherIsland10;
    @FXML
    private ImageView motherIsland11;
    @FXML
    private ImageView motherIsland12;


    @FXML
    private ImageView noEntry1;   // noEntryTiles imageviews on islands
    @FXML
    private ImageView noEntry2;
    @FXML
    private ImageView noEntry3;
    @FXML
    private ImageView noEntry4;
    @FXML
    private ImageView noEntry5;
    @FXML
    private ImageView noEntry6;
    @FXML
    private ImageView noEntry7;
    @FXML
    private ImageView noEntry8;
    @FXML
    private ImageView noEntry9;
    @FXML
    private ImageView noEntry10;
    @FXML
    private ImageView noEntry11;
    @FXML
    private ImageView noEntry12;

    private Color fromStudent;
    private ImageView destinationIsland;




    @FXML
    public void initialize() {

        createAssistantCards();
        createArchipelago();
        createSchoolBoard();
        createShowContentArea();
        createControlsArea();

    }

    /**
     * The setupBoard() methods initializes the initial setup of the board:
     * it setups the playerboard by setting the correct towerColor, then it sets the
     * player's tower, the clouds and the archipelago.
     * It is called after the SETUP_UPDATE message is received by the ViewMessageParser.
     */
    public void setupBoard() {

        if (gui.getViewState().getGameSettings().getExpert()){
            showSpecialCardsButton.setVisible(true);
            showSpecialCardsButton.setOnAction(this::showSpecialCards);
        }else {
            showSpecialCardsButton.setVisible(false);
        }

        showOtherBoardsButton.setOnAction(this::showOtherBoards);
        moveDiningRoomButton.setOnAction(this::pickDiningRoom);
        moveIslandButton.setOnAction(this::chooseIsland);
        // prende le cose da viewState e  fa la setup iniziale di tutti gli elementi della board
        System.out.println("executing setupBoard() ");
        setupTowers();
        setupClouds();

    }

    public void updateWholeBoard(){
        System.out.println("executing updateWholeBoard()...");
        updateMyPlayerBoard();
        updateArchipelago();
        updateProfessors();

        updatePhaseAction();



    }

    public void updateGameStatus(){
        PhaseEnum currentPhase = gui.getViewState().getCurrentPhase();
        Tower activePlayer = gui.getViewState().getActivePlayer();
        String activePlayerName = gui.getViewState().getPlayers().get(activePlayer);

        statusHeader.setText("Phase : " + currentPhase.toString() + "\nActive player : " + activePlayerName);

    }

    public void updatePhaseAction(){
        deckArea.setVisible(false);
        deckArea.setDisable(true);
        diningRoomStudentsArea.setDisable(true);
        entranceStudentsArea.setDisable(true);
        moveIslandButton.setVisible(false);
        moveIslandButton.setDisable(true);
        moveDiningRoomButton.setVisible(false);
        moveDiningRoomButton.setDisable(true);

        disableOff(archipelagoIslands);
        disableOff(cloudsImgArray);

        removeEffect(archipelagoIslands);
        removeEffect(cloudsImgArray);

        if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.PLANNING)){
                deckArea.setVisible(true);
            if (gui.getViewState().getActivePlayer().equals(gui.getViewState().getPlayerTower())){
                deckArea.setDisable(false);
                List<Integer> usableCard = gui.getViewState().getUsableCards();
                for(ImageView card : deckArray){
                    if (usableCard.contains(deckArray.indexOf(card))){
                        card.setDisable(false);

                    } else {
                        card.setVisible(false);
                        card.setDisable(true);
                    }
                }
            }
        } else if(gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS)){
            if (gui.getViewState().getActivePlayer().equals(gui.getViewState().getPlayerTower())){
                entranceStudentsArea.setDisable(false);
                Map<Color, Integer> entrance = gui.getViewState().getSchoolEntranceOccupancy(gui.getViewState().getPlayerTower());
                for (ImageView image : entranceStudImages){
                    if (entrance.get(getColorFromImage(image)) <= 0){
                        image.setDisable(true);
                        image.setEffect(new Shadow());
                    } else {
                        image.setDisable(false);
                    }
                }
            }

        } else if(gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)){
            if (gui.getViewState().getActivePlayer().equals(gui.getViewState().getPlayerTower())){
                destinationIsland = archipelagoIslands.get(gui.getViewState().getMotherNature()-1);
                destinationIsland.setEffect(null);
                int cardPriority = gui.getViewState().getLastUsedCard(gui.getViewState().getPlayerTower());
                for (ImageView image : archipelagoIslands){
                    if ((archipelagoIslands.indexOf(image) < (gui.getViewState().getMotherNature() + cardPriority/2 + cardPriority%2))
                            && archipelagoIslands.indexOf(image) >= gui.getViewState().getMotherNature()){
                        image.setDisable(false);
                    }else {
                        image.setDisable(true);
                        image.setEffect(new Shadow());
                    }
                }
            }
        } else if(gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_CHOOSE_CLOUD)){
            if (gui.getViewState().getActivePlayer().equals(gui.getViewState().getPlayerTower())){
                for (ImageView cloud : cloudsImgArray){
                    if (!gui.getViewState().getUsableClouds().contains(cloudsImgArray.indexOf(cloud)+1)){
                        cloud.setEffect(new Shadow());
                        cloud.setDisable(true);
                    } else {
                        cloud.setDisable(false);
                    }
                }

            }
        } else if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.END)) {
            gui.setNextStage("endScene.fxml");
        }
    };

    @FXML
    public void showSpecialCards(ActionEvent event){
        gui.loadSecondWindow("specialCardsScene.fxml");
    }

    @FXML
    public void showOtherBoards(ActionEvent event){
        gui.loadSecondWindow("otherBoardsScene.fxml");
    }
    

    /**
     * Initially sets the AssistantCards deck in the gui and the corresponding "pickCard()" method.
     * From here onwards,every time a card is picked, it is immediately "disabled" so that it cannot be clicked anymore
     * and its number is sent via the ConnectionSocket class.
     */
    public void createAssistantCards() {
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

        deckArea.setVisible(false);
        for(ImageView img : deckArray) {
            img.setOnMouseClicked(this::pickCard);
        }
    }

    @FXML
    public void pickCard(MouseEvent event){
        ImageView clickedImg = (ImageView) event.getSource();
        clickedImg.setEffect(new Glow());
        clickedImg.setDisable(true);
        int cardNum = deckArray.indexOf(clickedImg) +1 ;  // array indexes start at 0  whereas cards go from 1 to 10
        gui.getConnectionSocket().setAssistantCard(cardNum);

    }

    @FXML
    public void pickEntranceStudent(MouseEvent event){
        ImageView imagePicked = (ImageView) event.getSource();
        imagePicked.setEffect(new Glow());

        fromStudent = getColorFromImage(imagePicked);
        entranceStudentsArea.setDisable(true);
        moveDiningRoomButton.setVisible(true);
        moveDiningRoomButton.setDisable(false);
    }

    @FXML
    public void pickCloud(MouseEvent event){
        if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_CHOOSE_CLOUD)) {
            ImageView imagePicked = (ImageView) event.getSource();
            gui.getConnectionSocket().chooseCloud(getPositionFromImage(imagePicked, "cloud"));
            removeEffect(cloudsImgArray);
        }
    }

    private Color getColorFromImage(ImageView image){
        String str = image.getId();
        str = str.replace("ent","");
        str = str.replace("Stud","");
        return toColor(str);
    }

    private int getPositionFromImage(ImageView image, String name){
        String str = image.getId();
        str = str.replace(name,"");
        return Integer.parseInt(str);
    }

    @FXML
    public void chooseIsland(ActionEvent event){
        removeEffect(archipelagoIslands);
        if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS)) {
            removeEffect(entranceStudImages);
            gui.getConnectionSocket().moveStudentToIsland(fromStudent, getPositionFromImage(destinationIsland, "island"));
            fromStudent=null;
        }else if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)){
            gui.getConnectionSocket().moveMotherNature(getPositionFromImage(destinationIsland, "island"));
        }
    }

    private void removeEffect(List<ImageView> list){
        for (ImageView image : list){
            image.setEffect(null);
        }
    }

    private void disableOff(List<ImageView> list){
        for (ImageView image : list){
            image.setDisable(false);
        }
    }


    @FXML
    public void pickIsland(MouseEvent event){
        if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS)) {
            if (fromStudent != null) {
                destinationIsland.setEffect(null);
                destinationIsland = (ImageView) event.getSource();
                destinationIsland.setEffect(new Glow());
                diningRoomStudentsArea.setDisable(true);
                moveIslandButton.setVisible(true);
                moveIslandButton.setDisable(false);
            }
        } else if(gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)){
            destinationIsland.setEffect(null);
            destinationIsland = (ImageView) event.getSource();
            destinationIsland.setEffect(new Glow());
            moveIslandButton.setVisible(true);
            moveIslandButton.setDisable(false);
        }
    }

    public void pickDiningRoom(ActionEvent event){
        diningRoomStudentsArea.setDisable(true);
        removeEffect(archipelagoIslands);
        removeEffect(entranceStudImages);
        gui.getConnectionSocket().moveStudentToDiningRoom(fromStudent);

    }

    public void createSchoolBoard() {
        // adding labels in the SchoolBoard
        entranceStudLabels.add(num_entRed);
        entranceStudLabels.add(num_entYellow);
        entranceStudLabels.add(num_entPink);
        entranceStudLabels.add(num_entBlue);
        entranceStudLabels.add(num_entGreen);
        for(Label l : entranceStudLabels){
            l.setText("");
        }

        entranceStudImages.add(entBlueStud);
        entranceStudImages.add(entRedStud);
        entranceStudImages.add(entYellowStud);
        entranceStudImages.add(entPinkStud);
        entranceStudImages.add(entGreenStud);
        for(ImageView img : entranceStudImages) {
            img.setOnMouseClicked(this::pickEntranceStudent);
        }

        diningRoomStudLabels.add(num_dinRed);
        diningRoomStudLabels.add(num_dinYellow);
        diningRoomStudLabels.add(num_dinPink);
        diningRoomStudLabels.add(num_dinBlue);
        diningRoomStudLabels.add(num_dinGreen);
        for(Label l : diningRoomStudLabels){
            l.setText("");
        }
        //adding labels in the professors Area
        professorsLabels.add(blue_owner);
        professorsLabels.add(pink_owner);
        professorsLabels.add(green_owner);
        professorsLabels.add(yellow_owner);
        professorsLabels.add(red_owner);
        for(Label l : professorsLabels){
            l.setText("");
        }
    }


    public void createArchipelago() {

        //adding Island images to the archipelago
        archipelagoIslands.add(island1);
        archipelagoIslands.add(island2);
        archipelagoIslands.add(island3);
        archipelagoIslands.add(island4);
        archipelagoIslands.add(island5);
        archipelagoIslands.add(island6);
        archipelagoIslands.add(island7);
        archipelagoIslands.add(island8);
        archipelagoIslands.add(island9);
        archipelagoIslands.add(island10);
        archipelagoIslands.add(island11);
        archipelagoIslands.add(island12);

        for(ImageView img : archipelagoIslands) {
            img.setOnMouseEntered(this::showContent);
            img.setOnMouseClicked(this::pickIsland);
        }
        destinationIsland = archipelagoIslands.get(0); //initialise, otherwise error

        towersOnIslands.add(towerIsland1);
        towersOnIslands.add(towerIsland2);
        towersOnIslands.add(towerIsland3);
        towersOnIslands.add(towerIsland4);
        towersOnIslands.add(towerIsland5);
        towersOnIslands.add(towerIsland6);
        towersOnIslands.add(towerIsland7);
        towersOnIslands.add(towerIsland8);
        towersOnIslands.add(towerIsland9);
        towersOnIslands.add(towerIsland10);
        towersOnIslands.add(towerIsland11);
        towersOnIslands.add(towerIsland12);
        for(ImageView i : towersOnIslands){
            i.setVisible(false);
        }

        motherNatureOnIslands.add(motherIsland1);
        motherNatureOnIslands.add(motherIsland2);
        motherNatureOnIslands.add(motherIsland3);
        motherNatureOnIslands.add(motherIsland4);
        motherNatureOnIslands.add(motherIsland5);
        motherNatureOnIslands.add(motherIsland6);
        motherNatureOnIslands.add(motherIsland7);
        motherNatureOnIslands.add(motherIsland8);
        motherNatureOnIslands.add(motherIsland9);
        motherNatureOnIslands.add(motherIsland10);
        motherNatureOnIslands.add(motherIsland11);
        motherNatureOnIslands.add(motherIsland12);
        for(ImageView i : motherNatureOnIslands) {
            i.setVisible(false);
        }

        noEntryTilesArray.add(noEntry1);
        noEntryTilesArray.add(noEntry2);
        noEntryTilesArray.add(noEntry3);
        noEntryTilesArray.add(noEntry4);
        noEntryTilesArray.add(noEntry5);
        noEntryTilesArray.add(noEntry6);
        noEntryTilesArray.add(noEntry7);
        noEntryTilesArray.add(noEntry8);
        noEntryTilesArray.add(noEntry9);
        noEntryTilesArray.add(noEntry10);
        noEntryTilesArray.add(noEntry11);
        noEntryTilesArray.add(noEntry12);
        for(ImageView i : noEntryTilesArray){
            i.setVisible(false);
        }

    }



    public void createShowContentArea(){
        showContentLabels.add(num_contentBlue);
        showContentLabels.add(num_contentGreen);
        showContentLabels.add(num_contentPink);
        showContentLabels.add(num_contentRed);
        showContentLabels.add(num_contentYellow);

        showContentArea.setVisible(false);
        showContentArea.setDisable(true);
    }

    public void createControlsArea() {
        controlsArea.setVisible(true);
        controlsArea.setDisable(false);
        statusHeader.setText("");
        statusMessage.setText("");
    }


    /**
     * Method to show the content of a selected Island or Cloud.
     * @param event : the MouseEvent triggered when clicking on an Island or Cloud imageview in the board
     */
    @FXML
    public void showContent(MouseEvent event) {
        ImageView clickedImg = (ImageView) event.getSource();
        String src = clickedImg.getId();
        showContentArea.setVisible(true);
        showContentArea.setDisable(false);
        showContentLabel.setText(src);

        if(src.contains("island")) {     // if an island has been clicked

            updateArchipelago();

            int islandNum = Integer.parseInt(src.substring(6));
            //System.out.println("I clicked the island # " + islandNum);
            List<IslandView> islands = gui.getViewState().getIslands();
            Map<Color,Integer> studentsMap;

            studentsMap = islands.get(islandNum-1).getStudentMap();
            //System.out.println("The island # " + islands.get(j).getPosition() + " has" + studentsMap);
            for(Label label : showContentLabels) {
                String str = label.getId().replace("num_content","");
                label.setText("x " + studentsMap.get(Color.toColor(str)).toString());
            }
            // Shows the tower in the content panel if it is present on the selected island
            Tower tower = gui.getViewState().getIslands().get(islandNum-1).getTower();
            islandTower.setImage(towersImgMap.get(tower));
            numTowers.setText("x "+ islands.get(islandNum-1).getTowerNumber());

            if (islands.get(islandNum-1).getTowerNumber()>0){
                islandTower.setVisible(true);
                numTowers.setVisible(true);
            }else {
                islandTower.setVisible(false);
                numTowers.setVisible(false);
            }
        }

        else if(src.contains("cloud")){              // if a cloud has been clicked
            islandTower.setVisible(false);
            Integer cloudNum = Integer.parseInt(src.substring(5));
            Map<Cloud,Integer> clouds = gui.getViewState().getClouds();
            for(Cloud c : clouds.keySet() ) {
                if(clouds.get(c).equals(cloudNum)) {
                    Map<Color,Integer> studentsMap = c.getStudents();

                    //System.out.println("The cloud # "+ cloudNum + " has" + studentsMap);
                    for(Label label : showContentLabels) {
                        String str = label.getId().replace("num_content","");
                        if (studentsMap != null){
                            label.setText("x " + studentsMap.get(Color.toColor(str)).toString());
                        }else{
                            label.setText("x 0 ");
                        }
                    }
                }
            }
        }

    }


    public void updateArchipelago() {

        System.out.println("executing updateArchipelago() ");

        List<IslandView> islands = gui.getViewState().getIslands();

        int num = archipelagoIslands.size();
        for (int i= islands.size(); i< num; i++){
            archipelagoIslands.get(i).setDisable(true);
            archipelagoIslands.get(i).setVisible(false);
            archipelagoIslands.remove(i);
            noEntryTilesArray.remove(i);
            motherNatureOnIslands.remove(i);
            towersOnIslands.remove(i);
        }

        // Shows a noEntryTile on the islands where it is present
        for(ImageView img : noEntryTilesArray) {
            Integer position = Integer.parseInt(img.getId().replace("noEntry",""));
            for(IslandView island : islands){
                if(island.getPosition() == position){
                    if(!island.isInfluenceEnabled()) {
                        img.setVisible(true);
                    }
                    else{
                        img.setVisible(false);
                    }
                }
            }
        }

        // Shows mothernature on the island where it is present
        Integer motherPosition = gui.getViewState().getMotherNature();
        for(ImageView i : motherNatureOnIslands) {
            if (i.getId().replace("motherIsland", "").equals(motherPosition.toString())) {
                i.setVisible(true);
            }
            else{
                i.setVisible(false);
            }
        }

        // Shows a tower if it is present on the island, otherwise it sets the image of the tower invisible

        for(ImageView img : towersOnIslands) {
            Integer position = Integer.parseInt(img.getId().replace("towerIsland",""));
            for(IslandView island : islands){
                if(island.getPosition() == position){
                    if(island.getTower()!=null) {
                        img.setImage(towersImgMap.get(island.getTower()));
                        img.setVisible(true);
                    }
                    else{
                        img.setVisible(false);
                    }
                }
            }
        }
    }

    /**
     * Updates the PlayerBoard (schoolboard) of the player by getting the updated occupancies from the ViewState
     * and then sets them to the labels in the Entrance and DiningRoom.  Moreover, it updates the current coins
     * and towers owned by the player.
     */
    public void updateMyPlayerBoard() {

        System.out.println("executing updateMyPlayerboard()" );

        Map<Color, Integer> schoolEntranceOccupancy;
        Map<Color, Integer> diningRoomOccupancy;
        Tower playerTowerColor = gui.getViewState().getPlayerTower();

        diningRoomOccupancy = gui.getViewState().getDiningRoomOccupancy(playerTowerColor);
        schoolEntranceOccupancy =  gui.getViewState().getSchoolEntranceOccupancy(playerTowerColor);


        currentPlayerCoins = gui.getViewState().getPlayerCoins(playerTowerColor);
        num_playerCoins.setText("x " + currentPlayerCoins);
        num_towersInBoard.setText("x " + gui.getViewState().getTowerLeft(playerTowerColor));

        for(Label label : entranceStudLabels){
            String str = label.getId();
            str = str.substring(7);
            Color color = Color.toColor(str);
            label.setText("x " + schoolEntranceOccupancy.get(color).toString());
        }

        for(Label label : diningRoomStudLabels){
            String str = label.getId();
            str = str.substring(7);
            Color color = Color.toColor(str);
            label.setText("x " + diningRoomOccupancy.get(color).toString());
        }


    }



    /**
     * Initializes the tower icon in the board, according to the TowerColor assigned to the player.
     */
    public void setupTowers() {
        towersImgMap.put(Tower.WHITE, new Image(getClass().getResourceAsStream(towersPath + "white_tower.png")));
        towersImgMap.put(Tower.BLACK, new Image(getClass().getResourceAsStream(towersPath + "black_tower.png")));
        towersImgMap.put(Tower.GRAY, new Image(getClass().getResourceAsStream(towersPath + "gray_tower.png")));

        Tower playerTowerColor = gui.getViewState().getPlayerTower();   // sets the player's towerColor in the board

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



    public void updateProfessors() {
        System.out.println("executing updateProfessors() ");

        Map<Color, Tower> professors = gui.getViewState().getProfessors();
        for(Label label : professorsLabels){
            String str = label.getId().replace("_owner","");
            Color color = Color.toColor(str);
            if(professors.get(color) != null){
                label.setText(professors.get(color).toString());
            }
        }
    }

    public void setupClouds(){
        cloudsImgArray.add(cloud1);
        cloudsImgArray.add(cloud2);
        cloudsImgArray.add(cloud3);

        for(ImageView cloud : cloudsImgArray) {
            cloud.setOnMouseEntered(this::showContent);
            cloud.setOnMouseClicked(this::pickCloud);
            cloud.setDisable(true);
        }

        if(gui.getViewState().getTowers().size() == 2) {
            cloud3.setVisible(false);
            cloud3.setDisable(true);
            cloudsImgArray.remove(2);
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


    private void testMethod(){
        List<Integer> availableCards = gui.getViewState().getUsableCards();
        System.out.println("The usable cards now are : " + availableCards);
    }
}

