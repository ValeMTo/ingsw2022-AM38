package it.polimi.ingsw.connectionManagement;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.Reader;

import java.util.TimerTask;

public class pingPongTimer extends TimerTask {
    private final Reader reader;
    private final ConnectionSocket connectionSocket;

    public pingPongTimer(Reader reader, ConnectionSocket connectionSocket) {
        this.reader = reader;
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
        if (reader != null && connectionSocket != null) {
            if (!reader.isHasReceivedMessageFromTimerStart()) {
                System.out.println("TIMER PING PONG - Sending a ping");
                connectionSocket.sendPing();
            }
        }
    }
}
