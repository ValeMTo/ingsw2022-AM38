package it.polimi.ingsw.controller;

import it.polimi.ingsw.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final List<ClientHandler> queue = new ArrayList<>();
    private final List<String> players = new ArrayList<>();
    private boolean isExpert;
    private int numOfPlayers;
    private GameOrchestrator gameOrchestrator = null;
    private int id;

    public Lobby(){
        id =0;
    }


    public int getNumOfActiveUsers() {
        return queue.size();
    }

    public void setIsExpert(boolean isExpert) {
        this.isExpert = isExpert;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public void addPlayer(ClientHandler client) {

        queue.add(client);
        players.add(client.getNickName());
        System.out.println("LOBBY - Adding new player (" + queue.size() + " of " + numOfPlayers + ")");
        //Creates a new GameOrchestrator and relatives messageParsers for the players
        synchronized (this) {
            if (queue.size() == numOfPlayers && queue.size() > 1) {
                System.out.println("LOBBY - Creating Game");

                if (isExpert) gameOrchestrator = new ExpertGameOrchestrator(players, id, queue);
                else gameOrchestrator = new EasyGameOrchestrator(players, id, queue);
                System.out.println("LOBBY - Created gameOrchestrator!");
                for (ClientHandler clients : queue) {
                    System.out.println("LOBBY - Gonna WakeUp clientHandlers!");
                    client.setIDGame(id);
                    synchronized (clients) {
                        System.out.println("LOBBY - WakeUp clientHandlers!");
                        clients.notifyAll();
                    }
                }
                id++;
            }
        }
    }


    /**
     * Returns null if the GameHandler is not already created or the client does not exist into the lobby queue
     * also reset the lobby if the last ConnectionSocket got its personal MessageParser
     *
     * @param client : client that wants to have a MessageParser to connect to the Controller
     * @return a new MessageParser with the created GameOrchestrator or null if no GameOrchestrator has been created or the player does not exist into the lobby
     */
    public MessageParser getMessageParser(ClientHandler client) {
        // Not already set parser
        if (gameOrchestrator == null) {
            System.out.println("LOBBY - returning null orchestrator");
            return null;
        }
        if (queue.remove(client)) {
            System.out.println("LOBBY - client removed correctly returning a new message parser");
            MessageParser messageParser = new MessageParser(gameOrchestrator, client);
            //Reset the lobby as inactive
            if (queue.size() <= 0) {
                System.out.println("LOBBY - no more clients, reset lobby");
                players.clear();
                numOfPlayers = 0;
                gameOrchestrator = null;

            }
            //return the MessageParser
            return messageParser;
        }
        System.out.println("LOBBY - return null -- problem");
        return null;
    }

    public boolean getGamemode() {
        return isExpert;
    }
}
