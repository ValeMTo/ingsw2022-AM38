package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.messages.AnswerTypeEnum;
import it.polimi.ingsw.messages.ConnectionTypeEnum;
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

        if (socket == null) socket = establishConnection(serverIP, serverPort);
        if (socket == null) return false;
        socketIn = createScanner(socket);
        socketOut = createWriter(socket);
        return true;
    }

    private JsonObject getMessage() {
        boolean error = false;
        JsonObject json;
        do {
            System.out.println("waiting for message");
            String jsonFromServer = socketIn.nextLine();
            if (jsonFromServer != null) System.out.println("Got message " + jsonFromServer);
            json = gson.fromJson(jsonFromServer, JsonObject.class);
            error = json == null || !json.has("MessageType");
        } while (error);
        return json;
    }

    /**
     * Ask the server if it is the first player in the lobby room.
     * True if it is the first player, otherwise false.
     *
     * @return isTheFirst boolean to understand if this player is the first player in the room
     */
    public Boolean isTheFirst() {
        socketOut.print(MessageGenerator.lobbyRequestMessage());
        socketOut.flush();
        System.out.println("SEND REQUEST - Sending: " + MessageGenerator.lobbyRequestMessage());
        JsonObject json = getMessage();
        if (json == null) {
            isTheFirst();
        }
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.ANSWER.ordinal()) {
            if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.LOBBY_ANSWER.ordinal()) {
                return json.get("numOfPlayers").getAsInt() == 0;
            }
        }
        isTheFirst();
        return null;
    }

    public void accept(){
        System.out.println("ACCEPT - Sending: " + MessageGenerator.okMessage());
        socketOut.print(MessageGenerator.okMessage());
        socketOut.flush();
    }

    public void refuse(){
        System.out.println("REFUSE - Sending: " + MessageGenerator.nackMessage());
        socketOut.print(MessageGenerator.nackMessage());
        socketOut.flush();
    }

    public boolean sendNickname(String nickname) {
        socketOut.print(MessageGenerator.nickNameMessage(nickname));
        socketOut.flush();
        System.out.println("SEND NICKNAME - Sending: " + MessageGenerator.nickNameMessage(nickname));
        JsonObject json = getMessage();
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }

    /**
     * send to the server the number of players of this match
     *
     * @param numOfPlayers number of player that can play
     * @return answer of the server
     */
    public boolean setNumberOfPlayers(int numOfPlayers) {
        socketOut.print(MessageGenerator.selectNumberOfPlayersMessage(numOfPlayers));
        socketOut.flush();
        System.out.println("SET NUM PLAYER - Sending: " + MessageGenerator.selectNumberOfPlayersMessage(numOfPlayers));
        JsonObject json = getMessage();
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }

    /**
     * send to the server the gamemode of this match.
     *
     * @param isExpert game mode
     * @return answer of the server
     */
    public boolean setGameMode(boolean isExpert) {
        socketOut.print(MessageGenerator.setGameModeMessage(isExpert));
        System.out.println("SET GAME MODE - Sending: " + MessageGenerator.setGameModeMessage(isExpert));
        socketOut.flush();
        JsonObject json = getMessage();
        System.out.println("Received: " + json);
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }

    /**
     * Ask the server the actual gamemode
     *
     * @return isExpert boolean
     */
    public Boolean getGameMode() {
        socketOut.print(MessageGenerator.gamemodeRequestMessage());
        socketOut.flush();
        System.out.println("SEND REQUEST - Sending: " + MessageGenerator.gamemodeRequestMessage());
        JsonObject json = getMessage();

        if (json.get("MessageType").getAsInt() == MessageTypeEnum.ANSWER.ordinal()) {
            if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.GAMEMODE_ANSWER.ordinal()) {
                return json.get("gamemode").getAsBoolean();
            }
        }

        getGameMode();
        return null;
    }

    /**
     * Ask the server the actual number of players set
     *
     * @return number of players that will play in the next match
     */
    public Integer getNumberOfPlayers() {
        socketOut.print(MessageGenerator.numberOfPlayerRequestMessage());
        socketOut.flush();
        System.out.println("SEND REQUEST - Sending: " + MessageGenerator.numberOfPlayerRequestMessage());
        JsonObject json = getMessage();

        if (json.get("MessageType").getAsInt() == MessageTypeEnum.ANSWER.ordinal()) {
            if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.PLAYERS_NUMBER_ANSWER.ordinal()) {
                return json.get("numOfPlayers").getAsInt();
            }
        }
        getNumberOfPlayers();
        return null;
    }

    /**
     * Used to confirm a choice to the server such as continue with an already set parameter
     */
    public void sendOk() {
        socketOut.print(MessageGenerator.okMessage());
        socketOut.flush();
        System.out.println("OK/Confirm message send " + MessageGenerator.okMessage());
    }

    /**
     * Send a disconnect message to disconnect from the server gracefully
     */
    public void disconnect() {
        socketOut.print(MessageGenerator.connectionMessage(ConnectionTypeEnum.CLOSE_CONNECTION));
        socketOut.flush();
        System.out.println("DISCONNECTION " + MessageGenerator.connectionMessage(ConnectionTypeEnum.CLOSE_CONNECTION));
    }

    /**
     * Establishes the connection with the server
     *
     * @param serverIP   : Ip of the server
     * @param serverPort : server port
     * @return : the Socket of the connection with the Server
     */
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