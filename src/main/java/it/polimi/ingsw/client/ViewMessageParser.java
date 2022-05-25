package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.messages.AnswerTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.messages.MessageTypeEnum;
import it.polimi.ingsw.messages.UpdateTypeEnum;
import it.polimi.ingsw.model.board.Tower;

import java.util.HashMap;
import java.util.Map;

public class ViewMessageParser {
    Gson gson = new Gson();
    ViewState view;

    public ViewMessageParser(ViewState view) {
        this.view = view;
    }

    public void parse(String jsonFromServer) {

        if (jsonFromServer != null) {
            System.out.println("Got message " + jsonFromServer);
        }
        JsonObject json = gson.fromJson(jsonFromServer, JsonObject.class);

        if (json.get("MessageType").equals(MessageTypeEnum.ERROR.ordinal())) {
            System.out.println(json.get("ErrorType") + " - " + json.get("errorString"));

        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.UPDATE.ordinal()) {
            if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.ASSISTANT_CARD_UPDATE.ordinal()) {
                Tower tower = Tower.toTower(json.get("PlayerTower").getAsString());
                view.useAssistantCard(tower, json.get("LastUsed").getAsInt());
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.CURRENT_PLAYER_UPDATE.ordinal()) {
                if (view.getNamePlayer(view.getPlayerTower()).equals(json.get("CurrentPlayer").getAsString())) {
                    view.setActiveView(true);
                } else {
                    view.setActiveView(false);
                }
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.SETUP_UPDATE.ordinal()){
                Map<String, Tower> players = gson.fromJson(json.get("PlayersMapping"), HashMap.class);
                view.setViewState(players, json.get("isExpertMode").getAsBoolean());
            }

        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.ANSWER.ordinal()) {
            if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.LOBBY_ANSWER.ordinal()) {
                view.setGameSettings(json.get("actualPlayers").getAsInt(), json.get("isExpert").getAsBoolean(), json.get("numOfPlayers").getAsInt());
            }
        }

    }
}