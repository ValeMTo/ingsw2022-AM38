package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.GameOrchestrator;
import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;
import it.polimi.ingsw.messages.*;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Suspendable;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;


import static it.polimi.ingsw.messages.ErrorTypeEnum.NICKNAME_ALREADY_TAKEN;

public class ClientHandler implements Runnable {
    private final Socket inSocket;
    private final Gson gson;
    private Scanner inputReader;
    private PrintWriter writer;
    private String playerName;
    private MessageParser messageParser;
    private int id;
    private boolean disconnected = false;
    public ClientHandler(Socket clientSocket) {
        inSocket = clientSocket;
        gson = new Gson();
        try {
            inputReader = new Scanner(inSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
        }
        try {
            writer = new PrintWriter(inSocket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
        }
    }

    /**
     * Set the id game
     */
    public void setIDGame(int id){
        this.id = id;
    }

    /**
     * Phase handler of the connection with the client
     */
    @Override
    public void run() {
        String sendingMessage;
        messageParser = new MessageParser(this);
        messageParser.setName(this.playerName);
        String message;
        while (true&&!disconnected) {
            System.out.println("CLIENT HANDLER - player "+playerName+" waiting for message");
            message = inputReader.nextLine();
            System.out.println("CLIENT HANDLER - player "+this.getNickName()+" got message "+message);
            System.out.println(message);
            sendingMessage = messageParser.parseMessageToAction(message);
            System.out.println("CLIENT HANDLER - sending "+sendingMessage);
            writer.print(sendingMessage);
            writer.flush();
        }
    }

    /**
     * Sends message to the client connected.
     *
     * @param message : message to send
     */
    public void asyncSend(String message){
        System.out.println("ASYNC MESSAGE - Sending: " + message);
        if(writer != null) {
            writer.print(message);
            writer.flush();
        }else {
            System.out.println("Printer not available");
        }
    }

    /**
     * Setups the message parser, waiting that the Lobby is completed and the GameOrchestrator has been generated
     */
    private void setupMessageParser() {
        boolean set;
        do {
            try {
                System.out.println("CLIENT HANDLER of" + playerName + "- Waiting for messageParser");
                synchronized (this) {
                    this.messageParser = Server.getMessageParserFromLobby(this);
                    System.out.println("CLIENT HANDLER of" + playerName + "- Got a messageParser"+this.messageParser);
                    if (this.messageParser == null) {
                        this.wait();
                        this.messageParser = Server.getMessageParserFromLobby(this);
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            if (this.messageParser != null)
                System.out.println("CLIENT HANDLER of" + playerName + " - Got messageParser");
            set = null != this.messageParser;
            if (set) System.out.println("Set yes");
            else System.out.println("Set no");
        } while (!set);
        System.out.println("CLIENT HANDLER of" + playerName + " - messageParserSet sends setup...");
        writer.print(MessageGenerator.setupUpdateMessage(messageParser.getPlayersTower(), messageParser.getPlayersTower().size(), messageParser.isExpert()));
        writer.flush();
        System.out.println("CLIENT HANDLER of" + playerName + " - messageParserSet now waiting for messages...");
    }


    /**
     * When the lobby is full, the class receives the created messageParser
     *
     * @param messageParser
     */
    public void setMessageParser(MessageParser messageParser) {
        this.messageParser = messageParser;
        System.out.println("CLIENTHANDLER of " + this.playerName + " set messageParser");
    }

    /**
     * Manage the disconnection and controls the messages searching for the disconnection request
     */
    public void disconnectionManager() {
        Server.removePlayer(playerName);
        inputReader.close();
        writer.close();
        this.disconnected = true;
    }

    private JsonObject getMessage() {
        boolean error = false;
        JsonObject json;
        do {
            System.out.println("waiting for message");
            String jsonFromClient = inputReader.nextLine();
            if (jsonFromClient != null) System.out.println("Got message " + jsonFromClient);
            json = gson.fromJson(jsonFromClient, JsonObject.class);
            error = json == null || !json.has("MessageType");
        } while (error);
        return json;
    }

    /**
     * Gets the nickname of the player
     *
     * @return : the String of the player nickname
     */
    public String getNickName() {
        return this.playerName;
    }


    /**
     * accept/refuse the nickname of a user that wants to play
     *
     * @return the nickname chosen by the player
     */
    public String confirmNickname(String nickname) {
        String message = null;
        try {
            System.out.println("confirmNickname");
            Server.blockPlayerName(nickname);
            System.out.println("IL MIO NICKNAME è "+ nickname + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            setNickname(nickname);
            System.out.println("Sending OK: " + MessageGenerator.okNicknameAnswer(nickname));
            message = MessageGenerator.okNicknameAnswer(nickname);
            this.messageParser.setName(this.playerName);
        } catch (NicknameAlreadyTakenException e) {
            System.out.println("EXP Sending: " + MessageGenerator.errorWithStringMessage(NICKNAME_ALREADY_TAKEN, "Already taken nickname"));
            message = MessageGenerator.errorWithStringMessage(NICKNAME_ALREADY_TAKEN, "Already taken nickname");
        }

        return message;


    }

    /**
     * Set nickname
     */
    public void setNickname(String nickname){
        this.playerName = nickname;
    }

    /**
     * Send a message with the number of active users in the lobby room
     */
    public void lobbyRequestHandler() {
        System.out.println("I'm in lobbyRequestHandler");
        List<String> list = Server.getLobbyOfActivePlayers();
        System.out.println(list.size());
        if (list.size() == 0){
            System.out.println("Sending: " + MessageGenerator.answerlobbyMessage(null, null, Server.getGamemode(), Server.getNumOfPlayerGame()));
            writer.print(MessageGenerator.answerlobbyMessage(null, null , Server.getGamemode(), Server.getNumOfPlayerGame()));
        } else if (list.size()==1){
            System.out.println("Sending: " + MessageGenerator.answerlobbyMessage(list.get(0),null, Server.getGamemode(), Server.getNumOfPlayerGame()));
            writer.print(MessageGenerator.answerlobbyMessage(list.get(0), null, Server.getGamemode(), Server.getNumOfPlayerGame()));
        } else if(list.size()==2){
            System.out.println("Sending: " + MessageGenerator.answerlobbyMessage(list.get(0), list.get(1), Server.getGamemode(), Server.getNumOfPlayerGame()));
            writer.print(MessageGenerator.answerlobbyMessage(list.get(0), list.get(1) , Server.getGamemode(), Server.getNumOfPlayerGame()));
        }
        writer.flush();

    }

    /**
     * Receive the gamemode setting
     *
     * @return true if the expert mode was set, false otherwise.
     */
    private boolean receiveGamemode() {
        System.out.println("GAMEMODE SET - Waiting for a message ");
        JsonObject json = getMessage();
        Boolean gamemode = null;
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal()) {
            if (json.get("SetType").getAsInt() == SetTypeEnum.SET_GAME_MODE.ordinal()) {
                gamemode = json.get("SetExpertGameMode").getAsBoolean();
                writer.print(MessageGenerator.okMessage());
                System.out.println("Sending: " + MessageGenerator.okMessage());
                writer.flush();
            }
        } else {
            lobbyRequestHandler();
        }
        return gamemode;
    }

    /**
     * Receive the number of player setting
     *
     * @return number of player that will play the next match
     */
    private int receiveNumOfPlayers() {
        System.out.println("PLAYERS NUMBER SET - Waiting for a message ");
        JsonObject json = getMessage();
        Integer numOfPlayers = null;
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal()) {
            if (json.get("SetType").getAsInt() == SetTypeEnum.SELECT_NUMBER_OF_PLAYERS.ordinal()) {
                numOfPlayers = json.get("SetNumberOfPlayers").getAsInt();
                System.out.println("Sending: " + MessageGenerator.okMessage());
                writer.print(MessageGenerator.okMessage());
                writer.flush();
            }
        } else {
            lobbyRequestHandler();
        }
        return numOfPlayers;
    }

    public boolean sendGameMode(boolean isExpert) {
        System.out.println("SET GAME MODE - Sending: " + MessageGenerator.gamemodeMessage(isExpert));
        writer.print(MessageGenerator.gamemodeMessage(isExpert));
        writer.flush();
        JsonObject json = getMessage();
        System.out.println("Received: " + json);
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }

    public boolean sendNumberOfPlayers(int numOfPlayers) {
        System.out.println("SET GAME MODE - Sending: " + MessageGenerator.numberOfPlayerMessage(numOfPlayers));
        writer.print(MessageGenerator.numberOfPlayerMessage(numOfPlayers));
        writer.flush();
        JsonObject json = getMessage();
        System.out.println("Received: " + json);
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }

    public int getGameID(){
        return id;
    }

    public void setGameOrchestrator(GameOrchestrator gameOrchestrator){
        System.out.println("CLIENT HANDLER of" + playerName + " - Got GameOrchestrator! "+gameOrchestrator);
        messageParser.setGameOrchestrator(gameOrchestrator);
        messageParser.setName(this.playerName);
    }




}
