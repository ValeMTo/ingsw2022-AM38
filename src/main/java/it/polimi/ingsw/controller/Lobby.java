package it.polimi.ingsw.controller;

import it.polimi.ingsw.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private List<ClientHandler> queue = new ArrayList<>();
    private boolean isExpert;
    private int numOfPlayers;


    public int getNumOfActiveUsers(){
        return queue.size();
    }

    public void setIsExpert(boolean isExpert){
        this.isExpert = isExpert;
    }

    public void setNumOfPlayers(int numOfPlayers){
        this.numOfPlayers = numOfPlayers;
    }

    public int getNumOfPlayers(){
        return numOfPlayers;
    }

    public void addPlayer(ClientHandler client){
        queue.add(client);
        numOfPlayers+=1;
    }

    public boolean getGamemode(){
        return isExpert;
    }
}
