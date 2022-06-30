package it.polimi.ingsw.connectionManagement;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.Reader;

import java.util.TimerTask;

public class PingPongTimer extends TimerTask {
    private final Reader reader;
    private final ConnectionSocket connectionSocket;

    public PingPongTimer(Reader reader, ConnectionSocket connectionSocket) {
        this.reader = reader;
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        if (reader != null && connectionSocket != null) {
                connectionSocket.sendPing();
        }
    }
}
