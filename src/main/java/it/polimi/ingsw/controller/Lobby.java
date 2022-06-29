package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    private final List<ClientHandler> queue = new ArrayList<>();
    private final List<String> players = new ArrayList<>();
    private List<MessageParser> messageParsers;
    private boolean isExpert;
    private int numOfPlayers; //Number of players that will play
    private GameOrchestrator gameOrchestrator = null;
    private int id;

    public Lobby(){
        id =0;
        messageParsers = new ArrayList<>();
    }


    public synchronized List<String> getActiveUsers() {
        List<String> list = new ArrayList<>();
        for (ClientHandler client : queue){
            System.out.println("GET ACTIVE PLAYERS - LOBBY" + client.getNickName());
            list.add(client.getNickName());
        }
        return list;
    }

    public synchronized void setIsExpert(boolean isExpert) {
        this.isExpert = isExpert;
    }

    public synchronized int getNumOfPlayers() {
        return numOfPlayers;
    }

    public synchronized void setNumOfPlayers(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }

    public synchronized void addPlayer(ClientHandler client) {
        queue.add(client);
        players.add(client.getNickName());
        System.out.println("LOBBY - Adding new player named "+client.getNickName()+" (" + queue.size() + " of " + numOfPlayers + ")");

        for( ClientHandler person : queue){  //lo mando a tutti, compreso quello che si sta aggiungendo. Il caso viene poi gestito con  un if dentro a LobbyMenuController
                person.asyncSend(MessageGenerator.newPlayerUpdateMessage(client.getNickName()));
        }
        //Creates a new GameOrchestrator and relatives messageParsers for the players
        synchronized (this) {
            if (queue.size() == numOfPlayers && queue.size() > 1) {
                System.out.println("LOBBY - Creating Game flag isExpert: "+isExpert);
                if (isExpert) {
                    System.out.println("LOBBY - Creating expert Game");
                    gameOrchestrator = new ExpertGameOrchestrator(players, id, queue);
                    System.out.println("LOBBY - Expert Game created");
                }
                else {
                    System.out.println("LOBBY - Creating easy Game");
                    gameOrchestrator = new EasyGameOrchestrator(players, id, queue);
                    System.out.println("LOBBY - Easy Game Created");
                }
                System.out.println("LOBBY - Created gameOrchestrator!");
                for (ClientHandler clientParser : queue) {
                    System.out.println("LOBBY - Gonna create a messageParser for "+clientParser.getNickName()+"!");
                    MessageParser messageParserToAdd = new MessageParser(clientParser);
                    messageParserToAdd.setName(clientParser.getNickName());
                    messageParserToAdd.setGameOrchestrator(this.gameOrchestrator);
                    messageParsers.add(messageParserToAdd);
                    System.out.println("LOBBY - Gonna set a messageParser for "+clientParser.getNickName()+"!");
                    clientParser.setMessageParser(messageParserToAdd);
                }

                for (ClientHandler clients : queue) {
                    System.out.println("LOBBY - Gonna WakeUp clientHandlers!");
                    client.setIDGame(id);
                    synchronized (clients) {
                        System.out.println("LOBBY - WakeUp clientHandlers!");
                        clients.notifyAll();
                    }
                }
                id++;
                emptyLobby();
            }
            notifyAll();
        }
    }

    public synchronized void emptyLobby() {
        for (ClientHandler client : queue) {
            if (gameOrchestrator !=null){
                client.setGameOrchestrator(gameOrchestrator);
            } else {
                System.err.println("GameOrchestrator is null. If it is a test, don't worry.");
            }
        }
        players.clear();
        numOfPlayers = 0;
        gameOrchestrator = null;
        queue.clear();
        System.out.println("LOBBY - All client removed correctly returning a new message parser");
    }

    public boolean getGamemode() {
        return isExpert;
    }
}
