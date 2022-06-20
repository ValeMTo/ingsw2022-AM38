package it.polimi.ingsw.client;

import java.util.ArrayList;
import java.util.List;

public class GameSettings {
    private int numPlayers;
    private Boolean isExpert;
    private List<String> actualClients;

    public GameSettings(List<String> actualClients, Boolean isExpert, int numPlayers){
        this.actualClients = new ArrayList<>();
        if (actualClients != null){
            this.actualClients.addAll(actualClients);
        }
        this.isExpert=isExpert;
        this.numPlayers=numPlayers;
    }

    public Boolean getExpert() {
        return isExpert;
    }

    public int getNumberActualClients() {
        int num=0;
        for (String name : actualClients){
            if (name != null){
                num+=1;
            }
        }
        return num;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public List<String> getActualClients(){
        List<String> list = new ArrayList<>();
        list.addAll(actualClients);
        return list;
    }
}
