package it.polimi.ingsw.client.view;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;

import java.util.HashMap;
import java.util.Map;

public class SchoolBoardState {

    private Tower player;
    private Map<Color, Integer> schoolEntranceOccupancy;
    private Map<Color, Integer> diningRoomOccupancy;
    private int lastAssistantCardUsed;
    private int coins;

    public SchoolBoardState(Tower player){

        this.player = player;
        schoolEntranceOccupancy = new HashMap<>();
        diningRoomOccupancy = new HashMap<>();
        for (Color color : Color.values()){
            schoolEntranceOccupancy.put(color, 0);
            diningRoomOccupancy.put(color, 0);
        }
    }

    public void fillDiningRoom(Map<Color, Integer> diningRoomOccupancy){
        this.diningRoomOccupancy.clear();
        for (Color color : Color.values()){
            this.diningRoomOccupancy.put(color, 0);
        }
        this.diningRoomOccupancy.putAll(diningRoomOccupancy);
        //for(Color color:this.diningRoomOccupancy.keySet())
            //System.out.println("Color in dining room "+color+" have "+this.diningRoomOccupancy.get(color)+" students");
    }

    public void fillSchoolEntrance(Map<Color, Integer> schoolEntranceOccupancy){
        this.schoolEntranceOccupancy.clear();
        for (Color color : Color.values()){
            this.schoolEntranceOccupancy.put(color, 0);
        }
        this.schoolEntranceOccupancy.putAll(schoolEntranceOccupancy);
    }


    public Map<Color, Integer> getSchoolEntranceOccupancy(){
        Map<Color, Integer> returnMap = new HashMap<Color,Integer>();
        returnMap.putAll(this.schoolEntranceOccupancy);
        return returnMap;
    }

    public Map<Color, Integer> getDiningRoomOccupancy(){
        Map<Color, Integer> returnMap = new HashMap<Color,Integer>();
        returnMap.putAll(this.diningRoomOccupancy);
        return returnMap;
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

    /**
     * Sets the coin owned by this player
     */
    public void setCoins(int coins){
        this.coins = coins;
    }

    /**
     * Returns the coin owned by this player
     * @return the coin owned by this player
     */
    public int getCoins(){
        return this.coins;
    }
}
