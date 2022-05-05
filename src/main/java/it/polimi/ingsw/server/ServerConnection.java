package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.GameModeAlreadySetException;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;
import it.polimi.ingsw.exceptions.NumberOfPlayersAlreadySetException;
import it.polimi.ingsw.messages.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerConnection implements Runnable {
    private final Socket inSocket;
    private Scanner inputReader;
    private PrintWriter writer;

    public ServerConnection(Socket clientSocket) {
        inSocket = clientSocket;
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
        String jsonInput = null;
        do {
            error = !addPlayer();
        } while (error);

        do {
            error = !choosePlayerNum();


        } while (error);

        do {
            error = !chooseGameMode();
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

    /**
     * Wait for a message from the client to add the player, then calls the Server addPlayer method
     *
     * @return true if it could add the player correctly, otherwise false
     */
    public boolean addPlayer() {
        boolean error = true;
        System.out.println("ADD PLAYER - Waiting for a message ");
        String jsonInput = inputReader.nextLine();
        System.out.println("ADD PLAYER - I got a message " + jsonInput);
        JsonObject json = new Gson().fromJson(jsonInput, JsonObject.class);
        if (json == null) return false;
        disconnectionManager(json);
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal() && json.get("SetType").getAsInt() == SetTypeEnum.SET_NICKNAME.ordinal()) {
            String player = json.get("nickname").getAsString();
            try {
                if (!Server.addPlayer(player)) {
                    error = true;
                    System.out.println("ADD PLAYER - ERROR - Lobby is full");
                    writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.LOBBY_IS_FULL, "ADD PLAYER - ERROR - No more players can be added!"));
                    System.out.println("ADD PLAYER - Sending message " + MessageGenerator.errorWithStringMessage(ErrorTypeEnum.LOBBY_IS_FULL, "ADD PLAYER - ERROR - No more players can be added!"));

                } else {
                    error = false;
                    System.out.println("ADD PLAYER - Player name" + player + "has been set");
                    writer.println(MessageGenerator.okMessage());
                    System.out.println("ADD PLAYER - Sending message " + MessageGenerator.okMessage());
                }
            } catch (NicknameAlreadyTakenException exc) {
                System.out.println("ADD PLAYER - ERROR - Nickname" + player + "already taken");
                writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NICKNAME_ALREADY_TAKEN, "The nickname has been already taken"));
                error = true;
            }
        } else {
            writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
            error = true;
            System.out.println("ADD PLAYER - Sending message " + MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
        }
        writer.flush();
        return !error;
    }

    /**
     * Waits the message of playerNumber choose and manage the case the number of player has been already set requesting a confirmation to continue with the number set
     *
     * @return true if the choice of player number has been successful or the player choose to continue with the already set parameter false otherwise
     */
    public boolean choosePlayerNum() {
        boolean ok = false;
        int numOfPlayers = 0;
        System.out.println("PLAYER NUM - Waiting for a message ");
        String jsonInput = inputReader.nextLine();
        System.out.println("PLAYER NUM - I got a message " + jsonInput);
        JsonObject json = new Gson().fromJson(jsonInput, JsonObject.class);
        if (json == null) return false;
        disconnectionManager(json);
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal() && json.get("SetType").getAsInt() == SetTypeEnum.SELECT_NUMBER_OF_PLAYERS.ordinal()) {
            numOfPlayers = json.get("SetNumberOfPlayers").getAsInt();
            try {
                if (Server.setNumOfPlayers(numOfPlayers)) {
                    ok = true;
                    writer.print(MessageGenerator.okMessage());
                    System.out.println("PLAYER NUM - Sending message " + MessageGenerator.okMessage());

                } else {
                    ok = false;
                    writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.INVALID_INPUT, "The input is invalid or out of bound 2 or 3 players allowed"));
                    System.out.println("PLAYER NUM - ERROR - Sending message " + MessageGenerator.errorWithStringMessage(ErrorTypeEnum.INVALID_INPUT, "The input is invalid or out of bound 2 or 3 players allowed"));

                }
            } catch (NumberOfPlayersAlreadySetException exc) {
                writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NUMBER_OF_PLAYERS_ALREADY_SET, "Already set num players"));
                System.out.println("PLAYER NUM -Error - Exception number of player already set");
                writer.flush();
                return false;
            }
        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal()) {
            System.out.println("PLAYER NUM - Number of players confirmed");
            return true;
        } else {
            writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
            System.out.println("PLAYER NUM - Sending message " + MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
        }
        writer.flush();
        return ok;
    }

    /**
     * Waits for the message of choice of the game mode, manage the set, send of incorrect values, the disconnection or the ok.
     *
     * @return : true if the gameMode was successfully set
     */
    public boolean chooseGameMode() {
        boolean ok = false;
        boolean isExpert = false;
        System.out.println("GAME MODE -Waiting for a message ");
        String jsonInput = inputReader.nextLine();
        System.out.println("GAME MODE - I got a message " + jsonInput);
        JsonObject json = new Gson().fromJson(jsonInput, JsonObject.class);
        if (json == null) return false;
        disconnectionManager(json);
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal() && json.get("SetType").getAsInt() == SetTypeEnum.SET_GAME_MODE.ordinal()) {
            isExpert = json.get("SetExpertGameMode").getAsBoolean();
            try {
                Server.setGameMode(isExpert);
                ok = true;
                writer.print(MessageGenerator.okMessage());
                System.out.println("GAME MODE -Sending message " + MessageGenerator.okMessage());
            } catch (GameModeAlreadySetException exc) {
                ok = false;
                writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GAME_MODE_ALREADY_SET, "The gameMode is already set"));
                System.out.println("GAME MODE - ERROR - Sending message " + MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GAME_MODE_ALREADY_SET, "The gameMode is already set"));
            }
        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal()) {
            System.out.println("PLAYER NUM - Number of players confirmed");
            writer.println(MessageGenerator.okMessage());
        } else {
            writer.println(MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
            System.out.println("GAME MODE - ERROR -Sending message " + MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
        }
        writer.flush();
        return ok;
    }

}
