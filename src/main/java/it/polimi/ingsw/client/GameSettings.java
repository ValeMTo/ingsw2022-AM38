package it.polimi.ingsw.client;

public class GameSettings {
    private int numPlayers;
    private Boolean isExpert;
    private int actualClients;

    public GameSettings(int actualClients, Boolean isExpert, int numPlayers){
        this.actualClients=actualClients;
        this.isExpert=isExpert;
        this.numPlayers=numPlayers;
    }

    public Boolean getExpert() {
        return isExpert;
    }

    public int getActualClients() {
        return actualClients;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
