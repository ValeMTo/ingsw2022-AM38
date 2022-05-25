package it.polimi.ingsw.client.view;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;

import java.util.HashMap;
import java.util.Map;

public class SchoolBoardState {

    private Tower player;
    private Map<Color, Integer> SchoolEntranceOccupancy;
    private Map<Color, Integer> diningRoomOccupancy;
    private int lastAssistantCardUsed;

    public SchoolBoardState(Tower player){

        this.player = player;
        SchoolEntranceOccupancy = new HashMap<>();
        diningRoomOccupancy = new HashMap<>();
        for (Color color : Color.values()){
            SchoolEntranceOccupancy.put(color, 0);
            diningRoomOccupancy.put(color, 0);
        }
    }

    public void fillDiningRoom(Map<Color, Integer> diningRoomOccupancy){
        this.diningRoomOccupancy.clear();
        this.diningRoomOccupancy.putAll(diningRoomOccupancy);
    }

    public void fillSchoolEntrance(Map<Color, Integer> schoolEntranceOccupancy){
        this.SchoolEntranceOccupancy.clear();
        this.SchoolEntranceOccupancy.putAll(schoolEntranceOccupancy);
    }

    public Tower getPlayer(){
        return player;
    }

    public void setLastAssistantCardUsed(int lastAssistantCardUsed) {
        this.lastAssistantCardUsed = lastAssistantCardUsed;
    }

    public int getLastAssistantCardUsed(){
        return lastAssistantCardUsed;
    }
}
