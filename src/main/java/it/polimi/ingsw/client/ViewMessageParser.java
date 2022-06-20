package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.gui.controllers.LobbyMenuController;
import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.messages.AnswerTypeEnum;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageTypeEnum;
import it.polimi.ingsw.messages.UpdateTypeEnum;
import it.polimi.ingsw.model.board.Cloud;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMessageParser {
    Gson gson = new Gson();
    ViewState view;

    public ViewMessageParser(ViewState view) {
        this.view = view;
    }

    private Map<Color,Integer> getStudentMapFromStringAndNumberMap(Map<String, Number> students){
        Map<Color, Integer> studentsWithColors = new HashMap<>();
        for(String s:students.keySet()){
            studentsWithColors.put(Color.toColor(s),students.get(s).intValue());
        }
        //for(Color color:studentsWithColors.keySet())
            //System.out.println("VIEW MESSAGE PARSER - getStudentMapFromJson - Map - Color "+color+" has nr of students: "+studentsWithColors.get(color));
        return studentsWithColors;
    }
    public void parse(String jsonFromServer) {
        System.out.println("viewMessageParser receive..." + jsonFromServer);

        if (jsonFromServer != null) {
            //System.out.println("VIEW MESSAGE PARSER - Got message " + jsonFromServer);
        }
        JsonObject json = gson.fromJson(jsonFromServer, JsonObject.class);

        if (json.get("MessageType").getAsInt()==MessageTypeEnum.ERROR.ordinal()) {
            view.setTurnShown(false);
            if (json.get("ErrorType").getAsInt() == ErrorTypeEnum.NICKNAME_ALREADY_TAKEN.ordinal()){
                System.out.println("NICKNAME ALREADY TAKEN");
                view.setNickname(null);
                view.wake();
            }
        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.UPDATE.ordinal()) {
            if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.ASSISTANT_CARD_UPDATE.ordinal()) {
                Tower tower = Tower.toTower(json.get("PlayerTower").getAsString());
                List<Number> usableAssistantCardNum = gson.fromJson(json.get("AvailableCards"),ArrayList.class);
                List<Integer> usableAssistantCardInt = new ArrayList<>();
                for(Number num: usableAssistantCardNum)
                    usableAssistantCardInt.add(num.intValue());
                view.setUsableCards(usableAssistantCardInt);
            }
            else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.LAST_USED_ASSISTANT_CARD_UPDATE.ordinal()) {
                view.setLastCardUsed(Tower.values()[json.get("PlayerTower").getAsInt()],json.get("LastUsed").getAsInt());
            }
            else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.CURRENT_PLAYER_UPDATE.ordinal()) {
                //System.out.println("VIEW MESSAGE PARSER - CURRENT PLAYER UPDATE to tower " + json.get("CurrentPlayer").getAsString());
                view.setActivePlayer(Tower.toTower(json.get("CurrentPlayer").getAsString()));
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.SETUP_UPDATE.ordinal()) {
                Map<String, String> players = gson.fromJson(json.get("PlayersMapping"), HashMap.class);
                Map<String, Tower> playersWithTower = new HashMap<>();
                for (String s : players.keySet()) {
                    playersWithTower.put(s, Tower.toTower(players.get(s)));
                }
                view.setViewState(playersWithTower, json.get("isExpertMode").getAsBoolean());

            } else if(json.get("UpdateType").getAsInt() == UpdateTypeEnum.PLAYER_UPDATE.ordinal()){
                String newPlayerNickname = json.get("Nickname").getAsString();
                view.addOnlinePlayer(newPlayerNickname);   // adds the player to the online players list in the ViewState

                if(!view.isCli()){
                    Platform.runLater(()->view.getAwaitingGUI().updateLobbyScene(newPlayerNickname));
                }

            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.PHASE_UPDATE.ordinal()) {
                view.setCurrentPhase(PhaseEnum.values()[json.get("CurrentPhase").getAsInt()]);
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.ISLAND_VIEW_UPDATE.ordinal()) {
                IslandView islandToAdd = new IslandView(json.get("position").getAsInt());
                if(json.get("TowerColor").getAsString()!=null&&!json.get("TowerColor").getAsString().equalsIgnoreCase("null"))
                    islandToAdd.setTower(Tower.values()[json.get("TowerColor").getAsInt()]);
                islandToAdd.setTowerNumber(json.get("NumOfTowers").getAsInt());
                Map<String, Number> students = gson.fromJson(json.get("StudentsMap"), HashMap.class);
                islandToAdd.setStudentMap(getStudentMapFromStringAndNumberMap(students));
                List<IslandView> islands = view.getIslands();
                List<IslandView> transformedIslands = new ArrayList<>();
                transformedIslands.addAll(islands);
                for (IslandView island : islands)
                    if (island.getPosition() == islandToAdd.getPosition()) {
                        transformedIslands.remove(island);
                    }
                transformedIslands.add(islandToAdd);
                view.setIslands(transformedIslands);
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.ARCHIPELAGO_VIEW_UPDATE.ordinal()) {
                view.setMotherNature(json.get("MotherNaturePosition").getAsInt());
                view.setIslandNumber(json.get("NumOfIslands").getAsInt());
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.PHASE_AND_CURRENT_PLAYER_UPDATE.ordinal()) {
                synchronized (view) {
                    view.setActivePlayerAndPhase(Tower.toTower(json.get("CurrentPlayer").getAsString()), PhaseEnum.values()[json.get("CurrentPhase").getAsInt()]);
                }
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.SCHOOL_BOARD_UPDATE.ordinal()) {
                Map<String, Number> students = gson.fromJson(json.get("SchoolEntranceMap"), HashMap.class);
                view.setSchoolEntranceOccupancy(Tower.values()[json.get("TowerColor").getAsInt()], getStudentMapFromStringAndNumberMap(students));
                students = gson.fromJson(json.get("DiningRoomMap"), HashMap.class);
                view.setDiningRoomOccupancy(Tower.values()[json.get("TowerColor").getAsInt()], getStudentMapFromStringAndNumberMap(students));
                view.setCoins(Tower.values()[json.get("TowerColor").getAsInt()],json.get("coins").getAsInt());
                //TODO: other sets
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.CLOUD_VIEW_UPDATE.ordinal()) {
                Map<Cloud, Integer> clouds = view.getClouds();
                Map<Cloud, Integer> modifiedClouds = view.getClouds();
                Cloud cloudToAdd = new Cloud(json.get("StudentsLimit").getAsInt());
                Map<String, Number> students = gson.fromJson(json.get("StudentsMap"), HashMap.class);
                cloudToAdd.setStudents(getStudentMapFromStringAndNumberMap(students));
                for (Cloud cloud : clouds.keySet()) {
                    if (clouds.get(cloud) == json.get("position").getAsInt()) {
                        modifiedClouds.remove(cloud);
                    }
                }
                modifiedClouds.put(cloudToAdd, json.get("position").getAsInt());
                view.setCloud(modifiedClouds);
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.PROFESSORS_UPDATE.ordinal()) {
             Map<String, String> professorStringMap = gson.fromJson(json.get("ProfessorsMap"),HashMap.class);
             Map<Color,Tower> professors = new HashMap<>();
             for(String color:professorStringMap.keySet())
             {
                 professors.put(Color.toColor(color),Tower.toTower(professorStringMap.get(color)));
             }
             view.setProfessors(professors);
            } else if (json.get("UpdateType").getAsInt() == UpdateTypeEnum.SPECIAL_CARD_UPDATE.ordinal()) {
                if(json.has("StudentsMap")) {
                    Map<String, Number> students = new HashMap<>(gson.fromJson(json.get("StudentsMap"), HashMap.class));
                    view.setSpecialCardWithStudents(SpecialCardName.values()[json.get("Name").getAsInt()], this.getStudentMapFromStringAndNumberMap(students));
                }
                if(json.has("NumTiles")){
                    view.setHerbalistTiles(json.get("NumTiles").getAsInt());
                }
                if(json.has("SpecialCardsMap")){
                    Map<String,Number> numMap = new HashMap<>(gson.fromJson(json.get("SpecialCardsMap"), HashMap.class));
                    Map<SpecialCardName,Integer> specialCards = new HashMap<>();
                    for(String card: numMap.keySet())
                    {
                        specialCards.put(SpecialCardName.convertFromStringToEnum(card), numMap.get(card).intValue());
                    }
                    view.setUsableSpecialCard(specialCards);
                }
            }
        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.ANSWER.ordinal()) {
            if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.LOBBY_ANSWER.ordinal()) {
                List<String> listOfPlayers = new ArrayList<>();
                if(json.get("player1") != null ){
                    listOfPlayers.add(json.get("player1").getAsString());
                    view.addOnlinePlayer(json.get("player1").getAsString());
                }
                if(json.get("player2") != null ){
                    listOfPlayers.add(json.get("player2").getAsString());
                    view.addOnlinePlayer(json.get("player2").getAsString());
                }
                view.setGameSettings(listOfPlayers, json.get("isExpert").getAsBoolean(), json.get("numOfPlayers").getAsInt());
                System.out.println("LOBBY ANSWER");
                view.wake();

            } else if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.ACCEPT_NICKNAME_ANSWER.ordinal()) {
                System.out.println("ACCEPT NICKNAME REQUEST");
                view.setNickname(json.get("nickname").getAsString());
                view.wake();
            }
        }
    }
}