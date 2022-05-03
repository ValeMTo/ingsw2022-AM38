package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.GameModeAlreadySetException;
import it.polimi.ingsw.exceptions.NumberOfPlayersAlreadySetException;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.messages.MessageTypeEnum;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionSocket {
    private static Socket socket = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String serverIP;
    private final int serverPort;
    private final Scanner stdin;

    private final Gson gson;
    private Scanner socketIn;
    private PrintWriter socketOut;

    public ConnectionSocket(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.stdin = new Scanner(System.in);
        this.gson = new Gson();

    }


    /**
     * Method setup initializes a new socket connection and handles the nickname-choice response.
     */
    //@throws NicknameAlreadyTakenException when the nickname is already in use.
    public boolean setup() throws FunctionNotImplementedException {

        System.out.println("Trying to connect with the socket...");
        System.out.println("Opening a socket server communication on port " + serverPort);

        if (socket == null)
            socket = establishConnection(serverIP, serverPort);
        if (socket == null)
            return false;
        socketIn = createScanner(socket);
        socketOut = createWriter(socket);
        return true;
    }

    private JsonObject getMessage() {
        boolean error = false;
        Gson gson = new Gson();
        JsonObject json;
        do {
            System.out.println("waiting for message");
            String jsonFromServer = socketIn.nextLine();
            if (jsonFromServer == null)
                System.out.println("Got message " + jsonFromServer);
            json = new Gson().fromJson(jsonFromServer, JsonObject.class);
            if(json == null || !json.has("MessageType"))
                error = true;
            else
                error = false;
        }while(error);
        return json;
    }

    public boolean sendNickname(String nickname) {
        socketOut.print(MessageGenerator.nickNameMessage(nickname));
        socketOut.flush();
        System.out.println("SEND NICKNAME - Sending: " + MessageGenerator.nickNameMessage(nickname));
        JsonObject json = getMessage();
        if (json == null)
            return false;
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }

    public boolean sendNumberOfPlayers(int numOfPlayers) throws NumberOfPlayersAlreadySetException{
        socketOut.print(MessageGenerator.selectNumberOfPlayersMessage(numOfPlayers));
        socketOut.flush();
        System.out.println("SET NUM PLAYER - Sending: " + MessageGenerator.selectNumberOfPlayersMessage(numOfPlayers));
        JsonObject json = getMessage();
        if (json == null) return false;
        if(json.get("MessageType").getAsInt() == MessageTypeEnum.ERROR.ordinal() && json.get("MessageType").getAsInt() == ErrorTypeEnum.NUMBER_OF_PLAYERS_ALREADY_SET.ordinal())
            throw new NumberOfPlayersAlreadySetException();
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }
    public boolean setGameMode(boolean isExpert) throws GameModeAlreadySetException {
        socketOut.print(MessageGenerator.setGameModeMessage(isExpert));
        socketOut.flush();
        System.out.println("SET GAME MODE - Sending: " + MessageGenerator.setGameModeMessage(isExpert));
        JsonObject json = getMessage();
        if (json == null) return false;
        if(json.get("MessageType").getAsInt() == MessageTypeEnum.ERROR.ordinal() && json.get("MessageType").getAsInt() == ErrorTypeEnum.GAME_MODE_ALREADY_SET.ordinal())
            throw new GameModeAlreadySetException();
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }


    private Socket establishConnection(String serverIP, int serverPort) {
        Socket socket = null;
        try {
            socket = new Socket(serverIP, serverPort);
        } catch (IOException e) {
            System.out.println("ERROR - Connection NOT established");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        System.out.println("Connection established");
        return socket;
    }

    private Scanner createScanner(Socket socket) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }

    private PrintWriter createWriter(Socket socket) {
        PrintWriter scanner = null;
        try {
            scanner = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }
}