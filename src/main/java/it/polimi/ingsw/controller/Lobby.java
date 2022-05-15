package it.polimi.ingsw.controller;

import it.polimi.ingsw.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private List<ClientHandler> queue = new ArrayList<>();
    private List<String> players = new ArrayList<>();
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
        players.add(client.getNickName());
        System.out.println("LOBBY - Adding new player ("+queue.size()+" of "+numOfPlayers+")");
        //Creates a new GameOrchestrator and relatives messageParsers for the players
        if(queue.size()==numOfPlayers && queue.size()>1)
        {
            System.out.println("LOBBY - Creating Game");
            GameOrchestrator gameOrchestrator = new GameOrchestrator(players,isExpert);
            for(ClientHandler clientHandler:queue){
                clientHandler.setMessageParser(new MessageParser(gameOrchestrator,clientHandler.getNickName()));
            }
        }
    }

    public boolean getGamemode(){
        return isExpert;
    }
}
