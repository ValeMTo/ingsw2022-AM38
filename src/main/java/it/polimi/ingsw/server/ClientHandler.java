package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static it.polimi.ingsw.messages.ErrorTypeEnum.NICKNAME_ALREADY_TAKEN;

public class ClientHandler implements Runnable {
    private final Socket inSocket;
    private Scanner inputReader;
    private PrintWriter writer;
    private String playerName;
    private Gson gson;

    public ClientHandler(Socket clientSocket) {
        inSocket = clientSocket;
        gson=new Gson();
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
     * Phase handler of the connection with the client
     */
    @Override
    public void run() {
        boolean error;
        do {
            error = !addPlayer();
        } while (error);
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
    public boolean addPlayer() {
        this.playerName = receiveNickname();
        lobbyRequestHandler();
        if(Server.getLobbyNumberOfActivePlayers() == 0){
            Server.setLobbySettings(receiveGamemode(), receiveNumOfPlayers());
        }else {
            if(!getQuickAnswer()){
                return false;
            }
        }
        Server.addPlayerInLobby(this);
        return true;
    }

    /**
     * Wait for a yes or no message
     *
     * @return the exit of the message
     */
    private boolean getQuickAnswer(){
        System.out.println("YES OR NO MESSAGE - Waiting for a message ");
        JsonObject json = getMessage();
        return (json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal());

    }

    /**
     * Receive the nickname of a user that wants to play
     *
     * @return the nickname chosen by the player
     */
    private String receiveNickname(){
        System.out.println("ADD PLAYER - Waiting for a message ");
        JsonObject json = getMessage();
        String nickname = null;
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal()) {
            if (json.get("SetType").getAsInt() == SetTypeEnum.SET_NICKNAME.ordinal()) {
                nickname = json.get("nickname").getAsString();
                try {
                    Server.blockPlayerName(nickname);
                } catch (NicknameAlreadyTakenException e) {
                    writer.print(MessageGenerator.errorWithStringMessage(NICKNAME_ALREADY_TAKEN, "Already taken nickname"));
                    writer.flush();
                }
            }
        } else{
            receiveNickname();
        }
        return nickname;
    }

    /**
     * Send a message with the number of active users in the lobby room
     */
    private void lobbyRequestHandler(){
        System.out.println("LOBBY REQUEST - Waiting for a message ");
        JsonObject json = getMessage();
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.REQUEST.ordinal()) {
            if (json.get("RequestType").getAsInt() == RequestTypeEnum.LOBBY_REQUEST.ordinal()) {
                writer.print(MessageGenerator.numberPlayerlobbyMessage(Server.getLobbyNumberOfActivePlayers()));
            }
        } else{
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
        Boolean gamemode =null;
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal()) {
            if (json.get("SetType").getAsInt() == SetTypeEnum.SET_GAME_MODE.ordinal()) {
                gamemode = json.get("gamemode").getAsBoolean();
                writer.print(MessageGenerator.okMessage());
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
        System.out.println("GAMEMODE SET - Waiting for a message ");
        JsonObject json = getMessage();
        Integer numOfPlayers = null;
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal()) {
            if (json.get("SetType").getAsInt() == SetTypeEnum.SELECT_NUMBER_OF_PLAYERS.ordinal()) {
                numOfPlayers = json.get("numOfPlayers").getAsInt();
                writer.print(MessageGenerator.okMessage());
            }
        } else {
            lobbyRequestHandler();
        }
        return numOfPlayers;
    }


}
