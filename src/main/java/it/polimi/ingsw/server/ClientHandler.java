package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.connectionManagement.DisconnectionTimer;
import it.polimi.ingsw.controller.GameOrchestrator;
import it.polimi.ingsw.controller.MessageParser;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;
import it.polimi.ingsw.messages.ConnectionTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import static it.polimi.ingsw.messages.ErrorTypeEnum.GENERIC_ERROR;
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
    private Boolean hasReceivedMessageFromTimerStart = true;
    private final Object hasReceivedMessageFromTimerStartBlocker = new Object();
    private Timer timer = new Timer();


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
    public void setIDGame(int id) {
        this.id = id;
    }

    /**
     * Returns the flag for connection flow management (used for disconnection timer)
     */
    public Boolean getHasReceivedMessageFromTimerStart() {
        synchronized (this.hasReceivedMessageFromTimerStartBlocker) {
            return this.hasReceivedMessageFromTimerStart;
        }
    }

    /**
     * Sets the flag used to control the connection flow
     *
     * @param hasReceivedMessageFromTimerStart
     */
    public void setHasReceivedMessageFromTimerStart(Boolean hasReceivedMessageFromTimerStart) {
        synchronized (this.hasReceivedMessageFromTimerStartBlocker) {
            this.hasReceivedMessageFromTimerStart = hasReceivedMessageFromTimerStart;
        }
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
        DisconnectionTimer timerTask = new DisconnectionTimer(this);
        timer.scheduleAtFixedRate(timerTask,15000,20000);
        while (!disconnected) {
            System.out.println("CLIENT HANDLER - player " + playerName + " waiting for message");
            try {
                message = inputReader.nextLine();
                setHasReceivedMessageFromTimerStart(true);
                System.out.println("CLIENT HANDLER - player " + this.getNickName() + " got message " + message);
                System.out.println(message);
                JsonObject json = new Gson().fromJson(message, JsonObject.class);
                if (messageParser != null)
                    sendingMessage = messageParser.parseMessageToAction(message);
                else
                    sendingMessage = MessageGenerator.errorWithStringMessage(GENERIC_ERROR, "ERROR - ClientHandler has no messageParser");
                System.out.println("CLIENT HANDLER - sending " + sendingMessage);
                writer.print(sendingMessage);
                writer.flush();
            }
            catch (IllegalStateException exception)
            {
                System.out.println("CLIENT HANDLER - player "+this.playerName+" inputReader closed - disconnecting");
                disconnectionManager();
            }
        }
        timer.cancel();
    }

    /**
     * Sends message to the client connected.
     *
     * @param message : message to send
     */
    public void asyncSend(String message) {
        System.out.println("ASYNC MESSAGE - Sending: " + message);
        if (writer != null) {
            writer.print(message);
            writer.flush();
        } else {
            System.out.println("Printer not available");
        }
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
        timer.cancel();
        if(this.messageParser!=null)
        {
            messageParser.disconnectClients();
        }
        disconnect();
    }

    /**
     * Disconnect the client without notifying the game orchestrator to close other connections
     */
    public void disconnect() {
        timer.cancel();
        Server.removePlayer(playerName);
        writer.print(MessageGenerator.connectionMessage(ConnectionTypeEnum.CLOSE_CONNECTION));
        writer.flush();
        inputReader.close();
        writer.close();
        this.disconnected = true;
        System.out.println("ERROR - CLIENT HANDLER - CLOSING THREAD OF CLIENT "+playerName+" DUE TO CONNECTION LOST");
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
            System.out.println("IL MIO NICKNAME è " + nickname + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
    public void setNickname(String nickname) {
        this.playerName = nickname;
    }

    /**
     * Send a message with the number of active users in the lobby room
     */
    public void lobbyRequestHandler() {
        System.out.println("I'm in lobbyRequestHandler");
        List<String> list = Server.getLobbyOfActivePlayers();
        System.out.println(list.size());
        if (list.size() == 0) {
            System.out.println("Sending: " + MessageGenerator.answerlobbyMessage(null, null, Server.getGamemode(), Server.getNumOfPlayerGame()));
            writer.print(MessageGenerator.answerlobbyMessage(null, null, Server.getGamemode(), Server.getNumOfPlayerGame()));
        } else if (list.size() == 1) {
            System.out.println("Sending: " + MessageGenerator.answerlobbyMessage(list.get(0), null, Server.getGamemode(), Server.getNumOfPlayerGame()));
            writer.print(MessageGenerator.answerlobbyMessage(list.get(0), null, Server.getGamemode(), Server.getNumOfPlayerGame()));
        } else if (list.size() == 2) {
            System.out.println("Sending: " + MessageGenerator.answerlobbyMessage(list.get(0), list.get(1), Server.getGamemode(), Server.getNumOfPlayerGame()));
            writer.print(MessageGenerator.answerlobbyMessage(list.get(0), list.get(1), Server.getGamemode(), Server.getNumOfPlayerGame()));
        }
        writer.flush();

    }

    public void setGameOrchestrator(GameOrchestrator gameOrchestrator) {
        System.out.println("CLIENT HANDLER of" + playerName + " - Got GameOrchestrator! " + gameOrchestrator);
        messageParser.setGameOrchestrator(gameOrchestrator);
        messageParser.setName(this.playerName);
    }
}
