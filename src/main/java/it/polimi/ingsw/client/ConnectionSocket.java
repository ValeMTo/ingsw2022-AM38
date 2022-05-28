package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.board.Tower;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionSocket {
    private static Socket socket = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String serverIP;
    private final int serverPort;

    private final Gson gson;
    private final boolean isActive;
    private BufferedReader socketIn;
    private PrintWriter socketOut;
    private ViewState viewState;
    private ViewMessageParser viewMessageParser;

    public ConnectionSocket(String serverIP, int serverPort, ViewState viewState) {
        this.isActive = true;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.gson = new Gson();
        this.viewState = viewState;

    }

    /**
     * Method setup initializes a new socket connection and handles the nickname-choice response.
     */
    public boolean setup() throws FunctionNotImplementedException {

        System.out.println("CONNECTION SOCKET - Trying to connect with the socket...");
        System.out.println("CONNECTION SOCKET - Opening a socket server communication on port " + serverPort);

        if (socket == null) socket = establishConnection(serverIP, serverPort);
        if (socket == null) return false;

        socketIn = createScanner(socket);

        viewMessageParser = new ViewMessageParser(viewState);
        Thread r = new Thread(new Reader(socketIn, viewMessageParser)); //Thread to read from the server channel - async
        r.start();
        socketOut = createWriter(socket);
        return true;
    }

    private JsonObject getMessage() {
        boolean error = false;
        JsonObject json;
        do {
            System.out.println("CONNECTION SOCKET - waiting for message");
            String jsonFromServer = null;
            try {
                jsonFromServer = socketIn.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (jsonFromServer != null) System.out.println("CONNECTION SOCKET - Got message " + jsonFromServer);
            json = gson.fromJson(jsonFromServer, JsonObject.class);
            error = json == null || !json.has("MessageType");
        } while (error);
        return json;
    }

    /**
     * Ask the server if it is the first player in the lobby room.
     */
    public void isTheFirst() {
        socketOut.print(MessageGenerator.lobbyRequestMessage());
        socketOut.flush();
        System.out.println("CONNECTION SOCKET - SEND REQUEST - Sending: " + MessageGenerator.lobbyRequestMessage());
    }

    public void acceptRules() {
        System.out.println("CONNECTION SOCKET - ACCEPT RULES - Sending: " + MessageGenerator.okRulesGameMessage());
        socketOut.print(MessageGenerator.okRulesGameMessage());
        socketOut.flush();
    }

    public void refuseRules() {
        System.out.println("CONNECTION SOCKET - ACCEPT RULES - Sending: " + MessageGenerator.nackRulesGameMessage());
        socketOut.print(MessageGenerator.nackRulesGameMessage());
        socketOut.flush();
    }

    public void refuse() {
        System.out.println("CONNECTION SOCKET - REFUSE - Sending: " + MessageGenerator.nackMessage());
        socketOut.print(MessageGenerator.nackMessage());
        socketOut.flush();
    }

    public void sendNickname(String nickname) {
        socketOut.print(MessageGenerator.nickNameMessage(nickname));
        socketOut.flush();
        System.out.println("CONNECTION SOCKET - SEND NICKNAME - Sending: " + MessageGenerator.nickNameMessage(nickname));
    }

    /**
     * send to the server the number of players of this match
     *
     * @param numOfPlayers number of player that can play
     * @return answer of the server
     */
    public void setNumberOfPlayers(int numOfPlayers) {
        System.out.println("CONNECTION SOCKET - SEND NICKNAME - Sending: " + MessageGenerator.selectNumberOfPlayersMessage(numOfPlayers));
        socketOut.print(MessageGenerator.selectNumberOfPlayersMessage(numOfPlayers));
        socketOut.flush();
    }

    /**
     * send to the server the gamemode of this match.
     *
     * @param isExpert game mode
     */
    public void setGameMode(boolean isExpert) {
        socketOut.print(MessageGenerator.setGameModeMessage(isExpert));
        System.out.println("SET GAME MODE - Sending: " + MessageGenerator.setGameModeMessage(isExpert));
        socketOut.flush();
    }

    /**
     * Ask the server the actual gamemode
     *
     * @return isExpert boolean
     */
    public Boolean getGameMode() {
        socketOut.print(MessageGenerator.gamemodeRequestMessage());
        socketOut.flush();
        System.out.println("CONNECTION SOCKET - SEND REQUEST - Sending: " + MessageGenerator.gamemodeRequestMessage());
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
        System.out.println("CONNECTION SOCKET - SEND REQUEST - Sending: " + MessageGenerator.numberOfPlayerRequestMessage());
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
        System.out.println("CONNECTION SOCKET - OK/Confirm message send " + MessageGenerator.okMessage());
    }

    /**
     * Send a disconnect message to disconnect from the server gracefully
     */
    public void disconnect() {
        socketOut.print(MessageGenerator.connectionMessage(ConnectionTypeEnum.CLOSE_CONNECTION));
        socketOut.flush();
        System.out.println("CONNECTION SOCKET - DISCONNECTION " + MessageGenerator.connectionMessage(ConnectionTypeEnum.CLOSE_CONNECTION));
    }

    /**
     * Waits until the last connecting player and then start a broadcast message from the server to start the game
     * with the Update - SETUP message
     *
     * @return the new ViewState created using the setup message fields
     */
    public void startGame() {
        socketOut.print(MessageGenerator.startGameRequestMessage());
        System.out.println(MessageGenerator.startGameRequestMessage());
        socketOut.flush();
    }

    public void setAssistantCard(int numberOfCard) {
        System.out.println("CONNECTION SOCKET - setAssistantCard - sending "+MessageGenerator.useAssistantCardMessage(numberOfCard));
        socketOut.print(MessageGenerator.useAssistantCardMessage(numberOfCard));
        socketOut.flush();
    }

    /**
     * Send the message to move a particular student from its schoolEntrance to the DiningRoom
     * @param color : student to move
     */
    public void moveStudentToDiningRoom(Color color) {
        socketOut.print(MessageGenerator.moveStudentMessage(color, StudentCounter.SCHOOLENTRANCE,StudentCounter.DININGROOM));
        socketOut.flush();
    }

    /**
     * Send the message to move a particular student from its schoolEntrance to the DiningRoom
     * @param color : student to move
     */
    public void moveStudentToIsland(Color color, int position) {
        socketOut.print(MessageGenerator.moveStudentMessage(color, StudentCounter.SCHOOLENTRANCE,StudentCounter.ISLAND,position));
        socketOut.flush();
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
            System.out.println("CONNECTION SOCKET - ERROR - Connection NOT established");
            // logger.log(Level.SEVERE, e.getMessage(), e);
            return null;

        }
        // System.out.println("Connection established");
        return socket;
    }

    private BufferedReader createScanner(Socket socket) {
        BufferedReader scanner = null;
        try {
            scanner = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("CONNECTION SOCKET - Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }

    private PrintWriter createWriter(Socket socket) {
        PrintWriter scanner = null;
        try {
            scanner = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("CONNECTION SOCKET - Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }

    public boolean isActive() {
        return isActive;
    }

    public void updateNewPhase(PhaseEnum phase){
        socketOut.print(MessageGenerator.phaseUpdateMessage(phase));
        socketOut.flush();
        System.out.println("CONNECTION SOCKET - CHANGE PHASE " + MessageGenerator.phaseUpdateMessage(phase));
    }


}