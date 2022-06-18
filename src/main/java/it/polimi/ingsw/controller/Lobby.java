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


    public List<String> getActiveUsers() {
        List<String> list = new ArrayList<>();
        for (ClientHandler client : queue){
            System.out.println("GET ACTIVE PLAYERS - LOBBY" + client.getNickName());
            list.add(client.getNickName());
        }
        return list;
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

    public synchronized void addPlayer(ClientHandler client) {
        queue.add(client);
        players.add(client.getNickName());
        System.out.println("LOBBY - Adding new player named "+client.getNickName()+" (" + queue.size() + " of " + numOfPlayers + ")");

        for( ClientHandler person : queue){
            if (!person.getNickName().equals(client.getNickName())) {
                person.asyncSend(MessageGenerator.newPlayerUpdateMessage(client.getNickName()));
            }
        }
        //Creates a new GameOrchestrator and relatives messageParsers for the players
        synchronized (this) {
            if (queue.size() == numOfPlayers && queue.size() > 1) {
                System.out.println("LOBBY - Creating Game");

                if (isExpert) gameOrchestrator = new ExpertGameOrchestrator(players, id, queue);
                else gameOrchestrator = new EasyGameOrchestrator(players, id, queue);
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

    public synchronized MessageParser getMessageParser(ClientHandler client){
        for (MessageParser parser : messageParsers){
            if (parser.getName().equals(client.getNickName())){
                System.out.println("LOBBY - getMessageParser - giving a messageParser "+parser+" to "+client.getNickName());
                queue.remove(client);
                return parser;
            }
        }
        if(queue.size()<=0) {
            id++;
            emptyLobby();
        }
        return null;
    }


    public void emptyLobby() {

        for (ClientHandler client : queue) {
            if (gameOrchestrator !=null){
                client.setGameOrchestrator(gameOrchestrator);
            } else {
                System.err.println("GameOrchestrator is null. If it is a test, don't worry.");
            }
            queue.remove(client);
            System.out.println("LOBBY - client removed correctly returning a new message parser");
            //Reset the lobby as inactive
            if (queue.size() <= 0) {
                System.out.println("LOBBY - no more clients, reset lobby");
                players.clear();
                numOfPlayers = 0;
                gameOrchestrator = null;

            }
        }
    }

    public boolean getGamemode() {
        return isExpert;
    }
}
