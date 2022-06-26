package it.polimi.ingsw.client.gui.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class SpecialCardsMenuController extends GUIController {

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

    List<SpecialCardName> list;

    public void initialize(){
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
    }

    @Override
    public void loadContent(){
        try {
            Map<SpecialCardName, Integer> specialCards = gui.getViewState().getUsableSpecialCards();
            list = specialCards.keySet().stream().toList();
            loadCorrectImage(specialCardImage1, list.get(0), specialCardName1, description1);
            loadCorrectImage(specialCardImage2, list.get(1), specialCardName2, description2);
            loadCorrectImage(specialCardImage3, list.get(2), specialCardName3, description3);

            if (gui.getViewState().getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)){
                if (gui.getViewState().getActivePlayer().equals(gui.getViewState().getPlayerTower()) && !gui.getViewState().getSpecialCardUsage()) {
                    if (gui.getViewState().getPlayerCoins() >= specialCards.get(list.get(0))) {
                        use1.setVisible(true);
                        use1.setDisable(false);
                    } else if (gui.getViewState().getPlayerCoins() >= specialCards.get(list.get(1))) {
                        use2.setVisible(true);
                        use2.setDisable(false);
                    } else if (gui.getViewState().getPlayerCoins() >= specialCards.get(list.get(2))) {
                        use3.setVisible(true);
                        use3.setDisable(false);
                    }
                }
            }

        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void useSpecialCard1(ActionEvent event){
        gui.getConnectionSocket().chooseSpecialCard(list.get(0).name());
    }

    @FXML
    public void useSpecialCard2(ActionEvent event){
        gui.getConnectionSocket().chooseSpecialCard(list.get(1).name());
    }

    @FXML
    public void useSpecialCard3(ActionEvent event){
        gui.getConnectionSocket().chooseSpecialCard(list.get(2).name());
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
    }


}
