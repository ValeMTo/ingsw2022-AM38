package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


import static it.polimi.ingsw.messages.ErrorTypeEnum.NICKNAME_ALREADY_TAKEN;

public class ClientHandler implements Runnable {
    private final Socket inSocket;
    private final Gson gson;
    private Scanner inputReader;
    private PrintWriter writer;
    private String playerName;
    private MessageParser messageParser = null;
    private int id;
    private boolean confirmation;

    public ClientHandler(Socket clientSocket) {
        confirmation=false;
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
        boolean error;
        while(!confirmation) {
            addPlayer();
        }
        setupMessageParser();
        String message;
        while (true) {
            System.out.println(playerName);
            message = inputReader.nextLine();
            System.out.println(message);
            System.out.println(message);
            writer.print(messageParser.parseMessageToAction(message));
            System.out.println("/n");
        }
    }

    /**
     * Sends message to the client connected.
     *
     * @param message : message to send
     */
    public void asyncSend(String message){
        System.out.println("ASYNC MESSAGE - Sending: " + message);
        writer.print(message);
        writer.flush();
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
     *
     * @param json : json witch it wants to control if it solicited a close connection
     */
    private void disconnectionManager(JsonObject json) {
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.CONNECTION.ordinal() && json.get("SetType").getAsInt() == ConnectionTypeEnum.CLOSE_CONNECTION.ordinal()) {
            writer.print(MessageGenerator.connectionMessage(ConnectionTypeEnum.CLOSE_CONNECTION));
            System.exit(1);
        }
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
     * Wait for a message from the client to add the player
     *
     * @return true if it could add the player correctly, otherwise false
     */
    //TODO: Il server NON deve mandare alcun messaggio. DEve rispondere a una richiesta da parte del client.
    //TODO: sistemare parser in modo da permettere al server di rispondere alle richieste anche nella fase di login.
    public void addPlayer() {

        this.playerName = receiveNickname();
        lobbyRequestHandler();
        if (Server.getLobbyNumberOfActivePlayers() == 0) {
            Server.setLobbySettings(receiveGamemode(), receiveNumOfPlayers());
        }
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
     * Receive the nickname of a user that wants to play
     *
     * @return the nickname chosen by the player
     */
    private String receiveNickname() {
        System.out.println("ADD PLAYER - Waiting for a message ");
        JsonObject json = getMessage();
        String nickname = null;
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal()) {
            if (json.get("SetType").getAsInt() == SetTypeEnum.SET_NICKNAME.ordinal()) {
                nickname = json.get("nickname").getAsString();
                try {
                    Server.blockPlayerName(nickname);
                    System.out.println("Sending: " + MessageGenerator.okMessage());
                    writer.print(MessageGenerator.okMessage());
                    writer.flush();
                } catch (NicknameAlreadyTakenException e) {
                    writer.print(MessageGenerator.errorWithStringMessage(NICKNAME_ALREADY_TAKEN, "Already taken nickname"));
                    writer.flush();
                    receiveNickname();
                }
            }
        } else {
            receiveNickname();
        }
        return nickname;
    }

    /**
     * Send a message with the number of active users in the lobby room
     */
    private void lobbyRequestHandler() {
        System.out.println("LOBBY REQUEST - Waiting for a message ");
        JsonObject json = getMessage();
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.REQUEST.ordinal()) {
            if (json.get("RequestType").getAsInt() == RequestTypeEnum.LOBBY_REQUEST.ordinal()) {
                writer.print(MessageGenerator.numberPlayerlobbyMessage(Server.getLobbyNumberOfActivePlayers()));
                System.out.println("Sending: " + MessageGenerator.numberPlayerlobbyMessage(Server.getLobbyNumberOfActivePlayers()));
                writer.flush();
            }
        } else {
            lobbyRequestHandler();
        }
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

    public void confirm(){
        confirmation=true;
    }

}
