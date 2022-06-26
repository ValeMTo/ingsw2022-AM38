package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.connectionManagement.disconnectionTimer;
import it.polimi.ingsw.connectionManagement.pingPongTimer;
import it.polimi.ingsw.messages.ConnectionTypeEnum;
import it.polimi.ingsw.messages.MessageTypeEnum;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the continuous channel to read jsonObject from Scanner given
 */
public class Reader implements Runnable {
    private final BufferedReader inputReader;
    private final ViewMessageParser viewHandler;
    private Boolean hasReceivedMessageFromTimerStart = false;
    private ConnectionSocket connectionSocket;

    public Reader(BufferedReader inputReader, ViewMessageParser viewHandler) {
        this.inputReader = inputReader;
        this.viewHandler = viewHandler;
    }

    /**
     * Get hasReceivedMessageFromTimerStart for the timer thread to be aware if in this time the client has received something
     */
    public boolean isHasReceivedMessageFromTimerStart() {
        synchronized (hasReceivedMessageFromTimerStart) {
            return this.hasReceivedMessageFromTimerStart;
        }
    }

    /**
     * Set hasReceivedMessageFromTimerStart for the timer thread to be aware if in this time the client has received something
     */
    public void setHasReceivedMessageFromTimerStart(boolean hasReceivedMessageFromTimerStart) {
        synchronized (this.hasReceivedMessageFromTimerStart) {
            this.hasReceivedMessageFromTimerStart = hasReceivedMessageFromTimerStart;
        }
    }

    /**
     * Disconnection method, used if the server does not respond to the pings
     */
    public void disconnect() {
        try {
            connectionSocket.disconnect();
            inputReader.close();
            System.out.println("READER - GRACEFUL DISCONNECTION -");
        } catch (IOException exc) {
            System.out.println("READER - GRACEFUL DISCONNECTION - not possible to close the reader, shutting down");
        }
        System.exit(1);
    }

    public void setConnectionSocket(ConnectionSocket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        String input;
        Timer pingPongTimer = new Timer("Ping pong timer");
        TimerTask pingPongtask = new pingPongTimer(this, connectionSocket);
        Timer disconnectionTimer = new Timer("Disconnection timer");
        TimerTask disconnectionTask = new disconnectionTimer(this);
        pingPongTimer.scheduleAtFixedRate(pingPongtask,5000,5000);
        disconnectionTimer.scheduleAtFixedRate(disconnectionTask,30000,30000);
        JsonObject json;
        Gson gson = new Gson();
        while (true) {
            try {
                //System.out.println("READER - waiting for message");
                input = inputReader.readLine();
                setHasReceivedMessageFromTimerStart(true);
                //System.out.println("READER - got message "+input);
                viewHandler.parse(input);
                json = gson.fromJson(inputReader,JsonObject.class);
                if(json.get("MessageType").getAsInt()== MessageTypeEnum.CONNECTION.ordinal() && json.get("ConnectionType").getAsInt()== ConnectionTypeEnum.CLOSE_CONNECTION.ordinal())
                    disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }


}

