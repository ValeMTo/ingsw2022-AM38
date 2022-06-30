package it.polimi.ingsw.client.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.client.view.SubPhaseEnum;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.specialCards.SpecialCard;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static it.polimi.ingsw.model.board.Color.toColor;

public class SpecialCardsMenuController extends GUIController {




    private final List<Label> showContentLabels = new ArrayList<Label>();
    private final List<ImageView> showContentStudIcons = new ArrayList<ImageView>();


    @FXML
    private AnchorPane showContentArea;
    @FXML
    private AnchorPane studentsArea;
    @FXML
    private AnchorPane choiceBox;
    @FXML
    private AnchorPane firstColorBox;
    @FXML
    private AnchorPane secondColorBox;
    @FXML
    private AnchorPane chooseIslandBox;
    @FXML
    private Button confirmButton;
    @FXML
    private Label chooseColorLabel;
    @FXML
    private Label chooseIslandLabel;
    @FXML
    private Label showContentLabel;

    @FXML
    private ChoiceBox colorBox;
    @FXML
    private ChoiceBox islandBox;

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

    Color fromStudent;


    List<SpecialCardName> cardsList;

    public void initialize(){

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

        use1.setDisable(true);
        use2.setDisable(true);
        use3.setDisable(true);

        use1.setOnAction(this::useSpecialCard1);
        use2.setOnAction(this::useSpecialCard2);
        use3.setOnAction(this::useSpecialCard3);

        createArrays();  // create arrays of showContentArea and set visible/disable its elements

        showContentArea.setDisable(true);
        showContentArea.setVisible(false);
        studentsArea.setVisible(false);
        studentsArea.setDisable(true);

        noEntryImg.setVisible(false);
        noEntryImg.setDisable(true);
        numEntryTiles.setVisible(false);
        chooseColorLabel.setVisible(false);
        chooseIslandLabel.setVisible(false);
        colorBox.setVisible(false);
        colorBox.setDisable(true);
        islandBox.setVisible(false);
        islandBox.setDisable(true);
    }

    @Override
    public void loadContent(){
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

            if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)){
                if (gui.getViewState().getActivePlayer().equals(gui.getViewState().getPlayerTower()) && !gui.getViewState().getSpecialCardUsage()) {
                    if (gui.getViewState().getPlayerCoins() >= specialCards.get(cardsList.get(0))) {
                        use1.setVisible(true);
                        use1.setDisable(false);
                    }
                    else{
                        availability1.setText("You don't have enough coins to play this card.");
                    }
                    if (gui.getViewState().getPlayerCoins() >= specialCards.get(cardsList.get(1))) {
                        use2.setVisible(true);
                        use2.setDisable(false);
                    }
                    else{
                        availability2.setText("You don't have enough coins to play this card.");
                    }
                    if (gui.getViewState().getPlayerCoins() >= specialCards.get(cardsList.get(2))) {
                        use3.setVisible(true);
                        use3.setDisable(false);
                    }
                    else{
                        availability3.setText("You don't have enough coins to play this card.");
                    }
                }
            }

        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void useSpecialCard1(ActionEvent event){
        gui.getConnectionSocket().chooseSpecialCard(cardsList.get(0).name());
        Button clickedButton = (Button)event.getSource();
        Integer cardId = Integer.parseInt(clickedButton.getId().replace("use",""));
        updateCardContent(cardId);
    }

    @FXML
    public void useSpecialCard2(ActionEvent event){
        gui.getConnectionSocket().chooseSpecialCard(cardsList.get(1).name());
        Button clickedButton = (Button)event.getSource();
        Integer cardId = Integer.parseInt(clickedButton.getId().replace("use",""));
        updateCardContent(cardId);
    }

    @FXML
    public void useSpecialCard3(ActionEvent event){
        gui.getConnectionSocket().chooseSpecialCard(cardsList.get(2).name());
        Button clickedButton = (Button)event.getSource();
        Integer cardId = Integer.parseInt(clickedButton.getId().replace("use",""));
        updateCardContent(cardId);
    }

    private String loadCorrectDescription(SpecialCardName card){
        Gson parser = new Gson();
        JsonObject json = parser.fromJson(new InputStreamReader(getClass().getResourceAsStream("/json/SpecialCardsEffectsDescription.json")) {
        }, JsonObject.class);
        List<String> message = parser.fromJson(json.get(card.name()),List.class);
        String text= "";
        if (message != null) {
            for (int i = 0;i<message.size(); i++) {
                text += message.get(i);
            }
        }
        return text;
    }

    private void loadCorrectImage(ImageView image, SpecialCardName card, Label name, Label description){
        name.setText(card.name());
        description.setTextAlignment(TextAlignment.CENTER);
        description.setWrapText(true);
        description.setText(loadCorrectDescription(card));

        if(card.equals(SpecialCardName.ARCHER)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/archer.jpg")));
        } else if(card.equals(SpecialCardName.BARD)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/bard.jpg")));
        } else if(card.equals(SpecialCardName.CHEESEMAKER)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/cheesemaker.jpg")));
        } else if(card.equals(SpecialCardName.COOKER)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/cooker.jpg")));
        } else if(card.equals(SpecialCardName.GAMBLER)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/gambler.jpg")));
        } else if(card.equals(SpecialCardName.HERALD)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/herald.jpg")));
        } else if(card.equals(SpecialCardName.HERBALIST)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/herbalist.jpg")));
        } else if(card.equals(SpecialCardName.JUGGLER)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/juggler.jpg")));
        } else if(card.equals(SpecialCardName.KNIGHT)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/knight.jpg")));
        } else if(card.equals(SpecialCardName.POSTMAN)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/postman.jpg")));
        } else if(card.equals(SpecialCardName.PRIEST)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/priest.jpg")));
        } else if(card.equals(SpecialCardName.PRINCESS)){
            image.setImage(new Image(getClass().getResourceAsStream("/graphics/specialCards/princess.jpg")));
        }

        image.setOnMouseClicked(this::showContent);

    }




    public void displayCardsCoin(List<SpecialCardName> list, Map<SpecialCardName, Integer> updatedSpecialCards) {
        Map<SpecialCardName, Integer> initialCosts = gui.getViewState().getInitialCosts();
        System.out.println("Initial costs : " + initialCosts);
        System.out.println("updated costs : " + updatedSpecialCards);
        for(SpecialCardName card : list) {
            if(updatedSpecialCards.get(card) > initialCosts.get(card)){
                int cardNum = list.indexOf(card) +1 ;
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

    public void visibleAndDisableSetting(SpecialCardName cardName){
        choiceBox.setVisible(true);
        choiceBox.setDisable(false);
        studentsArea.setVisible(false);
        noEntryImg.setVisible(false);
        firstColorBox.setVisible(false);
        secondColorBox.setVisible(false);
        chooseIslandBox.setVisible(false);
        if(gui.getViewState().getSpecialCardUsage()) {
            if (cardName.equals(SpecialCardName.HERBALIST)) {
                noEntryImg.setVisible(true);
                chooseIslandBox.setVisible(true);
                chooseIslandBox.setDisable(false);

            } else if (cardName.equals(SpecialCardName.PRIEST)) {
                studentsArea.setVisible(true);
                firstColorBox.setVisible(true);
                firstColorBox.setDisable(false);
                chooseIslandBox.setVisible(true);
                chooseIslandBox.setDisable(false);

            } else if (cardName.equals(SpecialCardName.JUGGLER)) {
                studentsArea.setVisible(true);
                firstColorBox.setVisible(true);
                firstColorBox.setDisable(false);
                chooseIslandBox.setVisible(true);
                chooseIslandBox.setDisable(false);

            } else if (cardName.equals(SpecialCardName.PRINCESS)) {
                studentsArea.setVisible(true);
                firstColorBox.setVisible(true);
                firstColorBox.setDisable(false);

            } else if (cardName.equals(SpecialCardName.HERALD)) {
                chooseIslandBox.setVisible(true);
                chooseIslandBox.setDisable(false);

            } else if (cardName.equals(SpecialCardName.COOKER)) {
                firstColorBox.setVisible(true);
                firstColorBox.setDisable(false);
            } else if (cardName.equals(SpecialCardName.GAMBLER)) {
                firstColorBox.setVisible(true);
                firstColorBox.setDisable(false);
            } else if (cardName.equals(SpecialCardName.POSTMAN)) {
                choiceBox.setVisible(false);
            } else if (cardName.equals(SpecialCardName.BARD)) {
                firstColorBox.setVisible(true);
                firstColorBox.setDisable(false);
            }
        }else {
            choiceBox.setVisible(false);
            choiceBox.setDisable(true);
        }

    }

    public void confirmSpecialChoice(){

        SpecialCardName cardName = gui.getViewState().getSpecialCardinUse();

        if (cardName.equals(SpecialCardName.HERBALIST)) {
            gui.getConnectionSocket().chooseIsland();
        } else if (cardName.equals(SpecialCardName.PRIEST)) {
        } else if (cardName.equals(SpecialCardName.JUGGLER)) {
        } else if (cardName.equals(SpecialCardName.PRINCESS)) {
        } else if (cardName.equals(SpecialCardName.HERALD)) {
        } else if (cardName.equals(SpecialCardName.COOKER)) {
        } else if (cardName.equals(SpecialCardName.GAMBLER)) {
        } else if (cardName.equals(SpecialCardName.POSTMAN)) {
        } else if (cardName.equals(SpecialCardName.BARD)) {
        }
    }

    // Receives the card img id  as an integer. cardId is 1 for the specialCardImage1 ,  2 for the specialCardImage2, etc.
    public void updateCardContent(Integer cardId) {

        SpecialCardName cardName = cardsList.get(cardId-1);
        showContentLabel.setText(cardName.toString());

        visibleAndDisableSetting(cardName);

        Integer numTiles = gui.getViewState().getHerbalistTiles();
        numEntryTiles.setText(numTiles.toString());

        if(cardName.equals(SpecialCardName.HERBALIST)) {

        } else if(cardName.equals(SpecialCardName.PRIEST) || cardName.equals(SpecialCardName.JUGGLER) || cardName.equals(SpecialCardName.PRINCESS) ) {
            studentsArea.setVisible(true);

            Map<Color,Integer> studentsMap = gui.getViewState().getSpecialCardStudents(cardName);

            System.out.println("Current card studentsMap is : " + studentsMap);

            for(Label label : showContentLabels){
                label.setVisible(true);
                String str = label.getId().replace("num_content","");
                Color color = Color.toColor(str);
                if(studentsMap.get(color)!=null)
                    label.setText("x " + studentsMap.get(color).toString());
            }
        } else if(cardName.equals(SpecialCardName.HERALD)){
            List<IslandView> islands = gui.getViewState().getIslands();

            for(IslandView island : islands) {
                islandBox.getItems().add(island.getPosition());
            }
            // TODO: interaction with Herald
        }
        else if(cardName.equals(SpecialCardName.COOKER) || cardName.equals(SpecialCardName.GAMBLER)) {

            colorBox.getItems().addAll(Color.values());

            //TODO: interaction with COOKER  and GAMBLER
        }

        else if(cardName.equals(SpecialCardName.POSTMAN)) {
            usageMessage.setText("You are allowed to move MotherNature up to 2 additional islands than the steps allowed by the AssistantCard you chose. Please return to the board and complete the movement.");
        }
        else if(cardName.equals(SpecialCardName.BARD)) {
            usageMessage.setText("Return to the board to choose which students to swap between Entrance and DiningRoom");
        }
    }


    @FXML
    public void showContent(MouseEvent event) {

        ImageView clickedImg = (ImageView) event.getSource();
        Integer imgId = Integer.parseInt(clickedImg.getId().replace("specialCardImage",""));
        updateCardContent(imgId);

    }

    /**
     * Update status message is called by MainGUI after receiving an update from ViewMessageParser
     */
    public void updateStatusMessage() {
        PhaseEnum currentPhase = gui.getViewState().getCurrentPhase();
        SubPhaseEnum currentSubPhase = gui.getViewState().getSubPhaseEnum();

    }

    public Color getColorFromImage(ImageView image){
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


    private void createArrays() {
        showContentLabels.add(num_contentBlue);
        showContentLabels.add(num_contentGreen);
        showContentLabels.add(num_contentYellow);
        showContentLabels.add(num_contentRed);
        showContentLabels.add(num_contentPink);

        for (Label l : showContentLabels) {
            l.setVisible(false);
        }

        showContentStudIcons.add(contentRed);
        showContentStudIcons.add(contentYellow);
        showContentStudIcons.add(contentGreen);
        showContentStudIcons.add(contentBlue);
        showContentStudIcons.add(contentPink);

        for (ImageView icon : showContentStudIcons) {
            icon.setVisible(false);
            icon.setDisable(true);
        }
    }


    public void resetShowContent() {
        usageMessage.setText("");           // resets the usageMessage
        showContentArea.setDisable(false);
        showContentArea.setVisible(true);

        noEntryImg.setVisible(false);
        noEntryImg.setDisable(true);
        numEntryTiles.setVisible(false);

        colorBox.setDisable(true);
        colorBox.setVisible(false);
        islandBox.setDisable(true);
        islandBox.setVisible(false);

        for(Label l : showContentLabels){
            l.setVisible(false);
        }
        for(ImageView icon : showContentStudIcons){
            icon.setVisible(false);
            icon.setDisable(true);
        }
    }


}
