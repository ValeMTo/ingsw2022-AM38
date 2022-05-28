package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.messages.AnswerTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.messages.MessageTypeEnum;
import it.polimi.ingsw.messages.UpdateTypeEnum;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMessageParser {
    Gson gson = new Gson();
    ViewState view;

    public ViewMessageParser(ViewState view) {
        this.view = view;
    }

    private Map<Color,Integer> getStudentMapFromJson(JsonObject json){
        Map<String, Number> students = gson.fromJson(json.get("StudentsMap"), HashMap.class);
        Map<Color, Integer> studentsWithColors = new HashMap<>();
        for(String s:students.keySet()){
            studentsWithColors.put(Color.toColor(s),students.get(s).intValue());
        }
        return studentsWithColors;
    }
    public void parse(String jsonFromServer) {

        if (jsonFromServer != null) {
            System.out.println("VIEW MESSAGE PARSER - Got message " + jsonFromServer);
        }
        JsonObject json = gson.fromJson(jsonFromServer, JsonObject.class);

        if (json.get("MessageType").equals(MessageTypeEnum.ERROR.ordinal())) {
            System.out.println(json.get("ErrorType") + " - " + json.get("errorString"));
            view.setTurnShown(false);

        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.UPDATE.ordinal()) {
            if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.ASSISTANT_CARD_UPDATE.ordinal()) {
                Tower tower = Tower.toTower(json.get("PlayerTower").getAsString());
                view.useAssistantCard(tower, json.get("LastUsed").getAsInt());
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.CURRENT_PLAYER_UPDATE.ordinal()) {
                System.out.println("VIEW MESSAGE PARSER - CURRENT PLAYER UPDATE to tower "+json.get("CurrentPlayer").getAsString());
                view.setActivePlayer(Tower.toTower(json.get("CurrentPlayer").getAsString()));
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.SETUP_UPDATE.ordinal()){
                Map<String, String> players = gson.fromJson(json.get("PlayersMapping"), HashMap.class);
                Map<String, Tower> playersWithTower = new HashMap<>();
                for(String s:players.keySet()){
                    playersWithTower.put(s,Tower.toTower(players.get(s)));
                }
                view.setViewState(playersWithTower, json.get("isExpertMode").getAsBoolean());
            }

            else if(json.get("UpdateType").getAsInt() == UpdateTypeEnum.PHASE_UPDATE.ordinal())
            {
                view.setCurrentPhase(PhaseEnum.values()[json.get("CurrentPhase").getAsInt()]);
            }
            else if(json.get("UpdateType").getAsInt() == UpdateTypeEnum.ISLAND_VIEW_UPDATE.ordinal())
            {
                IslandView islandToAdd= new IslandView(json.get("position").getAsInt());
                islandToAdd.setTower(Tower.toTower(json.get("TowerColor").getAsString()));
                islandToAdd.setTowerNumber(json.get("NumOfTowers").getAsInt());
                islandToAdd.setStudentMap(getStudentMapFromJson(json));
                List<IslandView> islands = view.getIslands();
                for(IslandView island:islands)
                    if(island.getPosition()==islandToAdd.getPosition()) {
                        islands.remove(island);
                    }
                islands.add(islandToAdd);
            }

        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.ANSWER.ordinal()) {
            if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.LOBBY_ANSWER.ordinal()) {
                view.setGameSettings(json.get("actualPlayers").getAsInt(), json.get("isExpert").getAsBoolean(), json.get("numOfPlayers").getAsInt());
            }
        }

    }
}