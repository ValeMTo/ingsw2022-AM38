package it.polimi.ingsw.client.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.client.view.SubPhaseEnum;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.controller.SpecialCardRequiredAction;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

import javax.swing.*;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialCardsMenuController extends GUIController {


    private final List<Label> showContentLabels = new ArrayList<Label>();
    private final List<ImageView> showContentStudIcons = new ArrayList<ImageView>();
    Color fromStudent;
    List<SpecialCardName> cardsList;
    @FXML
    private AnchorPane showContentArea;
    @FXML
    private AnchorPane noEntryBox;
    @FXML
    private AnchorPane studentsArea;
    @FXML
    private AnchorPane choiceBoxMenu;
    @FXML
    private AnchorPane chooseColorBox;
    @FXML
    private AnchorPane chooseIslandBox;
    @FXML
    private Label chooseColorLabel;
    @FXML
    private Label chooseIslandLabel;
    @FXML
    private Label showContentLabel;
    @FXML
    private ComboBox colorBox;
    @FXML
    private ComboBox islandBox;
    @FXML
    private ImageView noEntryImg;
    @FXML
    private Label numEntryTiles;
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
    private Label usageMessage;
    @FXML
    private ImageView coin1;
    @FXML
    private ImageView coin2;
    @FXML
    private ImageView coin3;
    @FXML
    private ImageView specialCardImage1;
    @FXML
    private ImageView specialCardImage2;
    @FXML
    private ImageView specialCardImage3;
    @FXML
    private Label specialCardName1;
    @FXML
    private Label specialCardName2;
    @FXML
    private Label specialCardName3;
    @FXML
    private Label description1;
    @FXML
    private Label description2;
    @FXML
    private Label description3;
    @FXML
    private Button use1;
    @FXML
    private Button use2;
    @FXML
    private Button use3;
    @FXML
    private Label availability1;
    @FXML
    private Label availability2;
    @FXML
    private Label availability3;

    @FXML
    private Button colorSend;
    @FXML
    private Button islandSend;

    @FXML
    private Button endEffect;


    @FXML
    private AnchorPane mainAnchor;

    SpecialCardName cardUsed;
    boolean first;
    int islandToSend;
    Color colorToSend;

    public void initialize() {

        first=true;
        cardUsed = null;
        usageMessage.setText("");
        availability1.setText("");
        availability2.setText("");
        availability3.setText("");

        coin1.setVisible(false);
        coin2.setVisible(false);
        coin3.setVisible(false);

        use1.setVisible(false);
        use2.setVisible(false);
        use3.setVisible(false);

        use1.setOnAction(this::useSpecialCard1);
        use2.setOnAction(this::useSpecialCard2);
        use3.setOnAction(this::useSpecialCard3);

        createArrays();  // create arrays of showContentArea and set visible/disable its elements

        choiceBoxMenu.setVisible(false);
        studentsArea.setVisible(false);
        noEntryBox.setVisible(false);

        endEffect.setVisible(false);
        endEffect.setDisable(true);
        endEffect.setOnAction(this::terminateEffect);
        showContentArea.setOnMouseEntered(this::updateUsageMessage);


    }

    @Override
    public void loadContent() {
        try {

            Map<SpecialCardName, Integer> specialCards = gui.getViewState().getUsableSpecialCards();// prezzo della specialCard viene correttamente aggiornato

            usageMessage.setText("Click on a card to display its content, if available.");

            System.out.println("The active specialCards are : " + specialCards);

            cardsList = specialCards.keySet().stream().toList();
            loadCorrectImage(specialCardImage1, cardsList.get(0), specialCardName1, description1);
            loadCorrectImage(specialCardImage2, cardsList.get(1), specialCardName2, description2);
            loadCorrectImage(specialCardImage3, cardsList.get(2), specialCardName3, description3);

            displayCardsCoin(cardsList, specialCards);   // displays a coin if the cost of the card has been incremented


            // Display "use" button if there are enough coins

            if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE) && !gui.getViewState().getSpecialCardUsage()) {
                if (gui.getViewState().getActivePlayer().equals(gui.getViewState().getPlayerTower()) && !gui.getViewState().getSpecialCardUsage()) {
                    if (gui.getViewState().getPlayerCoins() >= specialCards.get(cardsList.get(0))) {
                        use1.setVisible(true);
                    } else {
                        availability1.setText("You don't have enough coins to play this card.");
                    }
                    if (gui.getViewState().getPlayerCoins() >= specialCards.get(cardsList.get(1))) {
                        use2.setVisible(true);
                    } else {
                        availability2.setText("You don't have enough coins to play this card.");
                    }
                    if (gui.getViewState().getPlayerCoins() >= specialCards.get(cardsList.get(2))) {
                        use3.setVisible(true);
                    } else {
                        availability3.setText("You don't have enough coins to play this card.");
                    }
                }
            }

        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }


    }

    @FXML
    public void useSpecialCard1(ActionEvent event) {
        gui.getConnectionSocket().chooseSpecialCard(cardsList.get(0).name());
        Button clickedButton = (Button) event.getSource();
        Integer cardId = Integer.parseInt(clickedButton.getId().replace("use", ""));
        updateCardContent(cardId);
        use1.setVisible(false);
        use2.setVisible(false);
        use3.setVisible(false);
    }

    @FXML
    public void useSpecialCard2(ActionEvent event) {
        gui.getConnectionSocket().chooseSpecialCard(cardsList.get(1).name());
        Button clickedButton = (Button) event.getSource();
        Integer cardId = Integer.parseInt(clickedButton.getId().replace("use", ""));
        updateCardContent(cardId);
        use1.setVisible(false);
        use2.setVisible(false);
        use3.setVisible(false);
    }

    @FXML
    public void useSpecialCard3(ActionEvent event) {
        gui.getConnectionSocket().chooseSpecialCard(cardsList.get(2).name());
        Button clickedButton = (Button) event.getSource();
        Integer cardId = Integer.parseInt(clickedButton.getId().replace("use", ""));
        updateCardContent(cardId);
        use3.setVisible(false);
        use2.setVisible(false);
        use1.setVisible(false);
    }

    private String loadCorrectDescription(SpecialCardName card) {
        Gson parser = new Gson();
        JsonObject json = parser.fromJson(new InputStreamReader(getClass().getResourceAsStream("/json/SpecialCardsEffectsDescription.json")) {
        }, JsonObject.class);
        List<String> message = parser.fromJson(json.get(card.name()), List.class);
        String text = "";
        if (message != null) {
            for (int i = 0; i < message.size(); i++) {
                text += message.get(i);
            }
        }
        return text;
    }

    private void loadCorrectImage(ImageView image, SpecialCardName card, Label name, Label description) {
        name.setText(card.name());
        description.setTextAlignment(TextAlignment.CENTER);
        description.setWrapText(true);
        description.setText(loadCorrectDescription(card));

        if (card.equals(SpecialCardName.ARCHER)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/archer.jpg")));
        } else if (card.equals(SpecialCardName.BARD)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/bard.jpg")));
        } else if (card.equals(SpecialCardName.CHEESEMAKER)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/cheesemaker.jpg")));
        } else if (card.equals(SpecialCardName.COOKER)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/cooker.jpg")));
        } else if (card.equals(SpecialCardName.GAMBLER)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/gambler.jpg")));
        } else if (card.equals(SpecialCardName.HERALD)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/herald.jpg")));
        } else if (card.equals(SpecialCardName.HERBALIST)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/herbalist.jpg")));
        } else if (card.equals(SpecialCardName.JUGGLER)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/juggler.jpg")));
        } else if (card.equals(SpecialCardName.KNIGHT)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/knight.jpg")));
        } else if (card.equals(SpecialCardName.POSTMAN)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/postman.jpg")));
        } else if (card.equals(SpecialCardName.PRIEST)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/priest.jpg")));
        } else if (card.equals(SpecialCardName.PRINCESS)) {
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/princess.jpg")));
        }

        image.setOnMouseClicked(this::showContent);
        colorBox.setOnAction(this::chooseColor);
        islandBox.setOnAction(this::chooseIsland);
        //showContentArea.setOnMouseEntered(this::updateUsageMessage);

        islandSend.setOnAction(this::sendIsland);  //button
        colorSend.setOnAction(this::sendColor);    // button

    }

    public void displayCardsCoin(List<SpecialCardName> list, Map<SpecialCardName, Integer> updatedSpecialCards) {
        Map<SpecialCardName, Integer> initialCosts = gui.getViewState().getInitialCosts();
        System.out.println("Initial costs : " + initialCosts);
        System.out.println("updated costs : " + updatedSpecialCards);
        for (SpecialCardName card : updatedSpecialCards.keySet()) {
            if (initialCosts.containsKey(card)&&updatedSpecialCards.get(card) > initialCosts.get(card)) {
                int cardNum = list.indexOf(card) + 1;
                switch (cardNum) {
                    case 1:
                        coin1.setVisible(true);
                        break;
                    case 2:
                        coin2.setVisible(true);
                        break;
                    case 3:
                        coin3.setVisible(true);
                        break;
                }
            }
        }
    }

    public void initialiseVisibleAndDisableSetting(SpecialCardName cardName) {

        if(gui.getViewState().isOptionalSpecialEffectUsage()){
            endEffect.setVisible(true);
            endEffect.setDisable(false);
        }

        Map<Color, Integer> studentsMap;
        List<Color> color = new ArrayList<>();

        for(ImageView icon : showContentStudIcons)
            icon.setEffect(null);

        showContentArea.setVisible(true);
        studentsArea.setVisible(false);
        noEntryBox.setVisible(false);
        chooseColorBox.setVisible(false);
        chooseIslandBox.setVisible(false);


        // resetting the studentsMap:
        for(Label l : showContentLabels) {
            l.setText("x 0");
        }

        colorBox.getItems().clear();
        colorBox.getItems().addAll(Color.values());
        islandBox.getItems().clear();
        islandBox.getItems().addAll(gui.getViewState().getUsableIslandPositions());

        if (gui.getViewState().getPlayerTower().equals(gui.getViewState().getActivePlayer()) && cardUsed != null) {
            System.out.println("I can see the choiceBox");
            choiceBoxMenu.setVisible(true);
        } else {
            choiceBoxMenu.setVisible(false);
        }
        System.out.println("Card name:" + cardName.name());
        if (cardName.equals(SpecialCardName.HERBALIST)) {     // herbalist requires an island choice
            noEntryBox.setVisible(true);
            chooseIslandBox.setVisible(true);

        } else if (cardName.equals(SpecialCardName.PRIEST)) {
            studentsArea.setVisible(true);
            chooseColorBox.setVisible(false);
            chooseIslandBox.setVisible(true);

        } else if (cardName.equals(SpecialCardName.JUGGLER)) {
            colorBox.getItems().clear();
            System.out.println(colorBox);
            if (first){
                studentsMap = gui.getViewState().getSpecialCardStudents(SpecialCardName.JUGGLER);
                for (Color item : studentsMap.keySet()){
                    if (studentsMap.get(item) >0) {
                        color.add(item);
                    }
                }
                colorBox.getItems().addAll(color);
            }
            studentsArea.setVisible(true);
            chooseColorBox.setVisible(true);


        } else if (cardName.equals(SpecialCardName.PRINCESS)) {
            studentsArea.setVisible(true);
            //studentsMap = gui.getViewState().getSpecialCardStudents(SpecialCardName.PRINCESS);
            /*
            chooseColorBox.setVisible(true);

            for (Color item : studentsMap.keySet()){
                if (studentsMap.get(item) >0) {
                    color.add(item);
                }
            }
            colorBox.getItems().addAll(color);
            */
        } else if (cardName.equals(SpecialCardName.HERALD)) {
            chooseIslandBox.setVisible(true);
        } else if (cardName.equals(SpecialCardName.COOKER)) {
            chooseColorBox.setVisible(true);
        } else if (cardName.equals(SpecialCardName.GAMBLER)) {
            chooseColorBox.setVisible(true);
        } else if (cardName.equals(SpecialCardName.POSTMAN)) {
            choiceBoxMenu.setVisible(false);
        } else if (cardName.equals(SpecialCardName.BARD)) {
            chooseColorBox.setVisible(true);
            colorBox.getItems().clear();
            System.out.println(colorBox);

            Map<Color,Integer> studMapEntrance = new HashMap<Color,Integer>();
            studMapEntrance = gui.getViewState().getSchoolEntranceOccupancy(gui.getViewState().getPlayerTower());
            Map<Color,Integer> studMapDining = new HashMap<Color,Integer>();
            studMapDining = gui.getViewState().getDiningRoomOccupancy(gui.getViewState().getPlayerTower());
            for (Color item : studMapEntrance.keySet()){
                if (studMapEntrance.get(item) >0) {
                    color.add(item);
                }
            }
            colorBox.getItems().addAll(color);

        }


    }

    private void showStudentMap(SpecialCardName cardName) {
        Map<Color, Integer> studentsMap = gui.getViewState().getSpecialCardStudents(cardName);
        System.out.println("Current card studentsMap is : " + studentsMap);

        for (Label label : showContentLabels) {
            label.setDisable(false);
            label.setVisible(true);
            String str = label.getId().replace("num_content", "");
            Color color = Color.toColor(str);
            if (studentsMap.get(color) != null) {
                label.setText("x " + studentsMap.get(color).toString());
                System.out.println("label : x  " + studentsMap.get(color).toString());
            }
        }
    }

    // Receives the card img id  as an integer. cardId is 1 for the specialCardImage1 ,  2 for the specialCardImage2, etc.
    public void updateCardContent(Integer cardId) {

        SpecialCardName cardName = cardsList.get(cardId - 1);
        cardUsed = cardName;
        showContentLabel.setText(cardName.toString());
        //usageMessage.setText("");

        chooseColorBox.setOnMouseEntered(this::chooseColor);   // New line

        initialiseVisibleAndDisableSetting(cardName);

        if (cardName.equals(SpecialCardName.HERBALIST)) {
            Integer numTiles = gui.getViewState().getHerbalistTiles();
            numEntryTiles.setText(numTiles.toString());

        } else if (cardName.equals(SpecialCardName.PRIEST)) {
            showStudentMap(cardName);

        } else if (cardName.equals(SpecialCardName.JUGGLER)) {
            showStudentMap(cardName);

        } else if (cardName.equals(SpecialCardName.PRINCESS)) {
            showStudentMap(cardName);

        } else if (cardName.equals(SpecialCardName.HERALD)) {
            List<IslandView> islands = gui.getViewState().getIslands();
            for (IslandView island : islands) {
                islandBox.getItems().add(island.getPosition());
            }
        } else if (cardName.equals(SpecialCardName.COOKER) || cardName.equals(SpecialCardName.GAMBLER)) {

        } else if (cardName.equals(SpecialCardName.POSTMAN)) {
            usageMessage.setText("You are allowed to move MotherNature up to 2 additional islands than the steps allowed by the AssistantCard you chose. Please return to the board and complete the movement.");
        } else if (cardName.equals(SpecialCardName.BARD)) {

        }
    }

    @FXML
    private void chooseColor(Event event) {
        System.out.println("Choose color method");

        if (cardUsed.equals(SpecialCardName.PRIEST)) {
            chooseColorBox.setVisible(false);
            chooseIslandBox.setVisible(true);


        } else if (cardUsed.equals(SpecialCardName.JUGGLER)){

            if(gui.getViewState().getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE)) {
                colorBox.getItems().clear();
                Map<Color, Integer> studentsMapEntrance = gui.getViewState().getSchoolEntranceOccupancy(gui.getViewState().getPlayerTower());
                List<Color> color = new ArrayList<>();
                for (Color item : studentsMapEntrance.keySet()){
                    if (studentsMapEntrance.get(item) >0) {
                        color.add(item);
                    }
                }
                colorBox.getItems().addAll(color);
            }
            else if(gui.getViewState().getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD)) {
                colorBox.getItems().clear();
            }

            /*
            if (!first){     // because the first time the juggler is used, its studentsmap has already been initialized by the ShowStudentMap
                colorBox.getItems().clear();
                Map<Color, Integer> studentsMap = gui.getViewState().getSchoolEntranceOccupancy(gui.getViewState().getPlayerTower());
                List<Color> color = new ArrayList<>();
                for (Color item : studentsMap.keySet()){
                    if (studentsMap.get(item) >0) {
                        color.add(item);
                    }
                }
                colorBox.getItems().addAll(color);
                first = true;
            }else{
                first=false;
            }
            */

        } else if (cardUsed.equals(SpecialCardName.BARD)){   // modified:  checks required action

            if(gui.getViewState().getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE)) {
                colorBox.getItems().clear();
                Map<Color, Integer> studentsMapEntrance = gui.getViewState().getSchoolEntranceOccupancy(gui.getViewState().getPlayerTower());
                List<Color> color = new ArrayList<>();
                for (Color item : studentsMapEntrance.keySet()){
                    if (studentsMapEntrance.get(item) >0) {
                        color.add(item);
                    }
                }
                colorBox.getItems().addAll(color);
            }

            else if(gui.getViewState().getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM)) {
                colorBox.getItems().clear();
                Map<Color, Integer> studentsMapDining = gui.getViewState().getDiningRoomOccupancy(gui.getViewState().getPlayerTower());
                List<Color> color = new ArrayList<>();
                for (Color item : studentsMapDining.keySet()){
                    if (studentsMapDining.get(item) >0) {
                        color.add(item);
                    }
                }
                colorBox.getItems().addAll(color);
            }
            /*
            if (!first){
                colorBox.getItems().clear();
                Map<Color, Integer> studentsMapDining = gui.getViewState().getDiningRoomOccupancy(gui.getViewState().getPlayerTower());
                List<Color> color = new ArrayList<>();
                for (Color item : studentsMapDining.keySet()){
                    if (studentsMapDining.get(item) >0) {
                        color.add(item);
                    }
                }
                colorBox.getItems().addAll(color);
                first=true;
            }else {
                first=false;
            }
            */
        }

        if(colorBox.getValue() != null)
            colorToSend = Color.toColor(colorBox.getValue().toString());

        /*
        try {
            colorBox.setValue(null);
        }catch (NullPointerException e){
            System.out.println("zero value");
        }
        */
        //updateCardContent(cardsList.indexOf(cardUsed)+1);     // update card content area  after interaction

    }

    @FXML
    private void chooseIsland(Event event) {
        System.out.println("choose island method");

        if (islandBox.getValue()!=null) {
            Integer pos = Integer.parseInt(islandBox.getValue().toString());
            islandToSend = pos;
        }
        /*
        try {
            islandBox.setValue(null);
        }catch (NullPointerException e){
            System.out.println("zero value");
        }

         */
        //updateCardContent(cardsList.indexOf(cardUsed)+1);     // update card content area  after interaction
    }

    @FXML
    private void chooseStudFromCard(MouseEvent event) {
        ImageView clickedIcon = (ImageView) event.getSource();
        clickedIcon.setEffect(new Glow());
        colorToSend = Color.toColor(clickedIcon.getId().replace("content",""));
        gui.getConnectionSocket().chooseColor(colorToSend);
        updateCardContent(cardsList.indexOf(cardUsed)+1);     // update card content area  after interaction
    }


    @FXML
    public void sendColor(ActionEvent event) {
        System.out.println("Sending color # " + colorToSend.toString());
        gui.getConnectionSocket().chooseColor(colorToSend);
    }

    @FXML
    public void sendIsland(ActionEvent event) {
        System.out.println("Sending island # " + islandToSend);
        gui.getConnectionSocket().chooseIsland(islandToSend);
    }

    @FXML
    public void showContent(MouseEvent event) {

        ImageView clickedImg = (ImageView) event.getSource();
        Integer imgId = Integer.parseInt(clickedImg.getId().replace("specialCardImage", ""));
        updateCardContent(imgId);

    }

    //Called every time the mouse hovers on the main AnchorPane
    public void updateUsageMessage(MouseEvent event) {
        usageMessage.setText("");   //initialization before setting the actual message
        if(gui.getViewState().getCurrentPhase().equals(PhaseEnum.SPECIAL_CARD_USAGE) && gui.getViewState().getPlayerTower().equals(gui.getViewState().getActivePlayer()) && gui.getViewState().getSpecialPhase() != null) {
            String message = gui.getViewState().getSpecialPhase().toString();
            usageMessage.setText(message);
        }

    }

    /**
     * Update status message is called by MainGUI after receiving an update from ViewMessageParser
     */
    public void updateStatusMessage() {
        PhaseEnum currentPhase = gui.getViewState().getCurrentPhase();
        SubPhaseEnum currentSubPhase = gui.getViewState().getSubPhaseEnum();

    }

    @FXML
    public void terminateEffect(ActionEvent event) {
        gui.getConnectionSocket().sendTerminationSpecialCard();
        endEffect.setVisible(false);
        endEffect.setDisable(true);
    }

    private void createArrays() {
        showContentLabels.add(num_contentBlue);
        showContentLabels.add(num_contentGreen);
        showContentLabels.add(num_contentYellow);
        showContentLabels.add(num_contentRed);
        showContentLabels.add(num_contentPink);

        for (Label l : showContentLabels) {
            l.setVisible(true);
        }

        showContentStudIcons.add(contentRed);
        showContentStudIcons.add(contentYellow);
        showContentStudIcons.add(contentGreen);
        showContentStudIcons.add(contentBlue);
        showContentStudIcons.add(contentPink);

        for (ImageView icon : showContentStudIcons) {
            icon.setVisible(true);
            icon.setOnMouseClicked(this::chooseStudFromCard);
        }
    }

}
