package it.polimi.ingsw.connectionManagement;

import it.polimi.ingsw.client.Reader;
import it.polimi.ingsw.server.ClientHandler;

import java.util.TimerTask;

public class DisconnectionTimer extends TimerTask {
    private Reader reader;
    private ClientHandler clientHandler;
    public DisconnectionTimer(Reader reader){
        this.reader = reader;
    }

    public DisconnectionTimer(ClientHandler clientHandler){
        this.clientHandler = clientHandler;
    }

    @Override
    public void run(){
        if(this.reader!=null)
        {
            if(!reader.isHasReceivedMessageFromTimerStart()){
                System.out.println("CONNECTION ERROR - DISCONNECTION TIMER - client connection lost, disconnecting! Shutting down the clientHandler");
                reader.disconnect();
            }
            else
            {
                reader.setHasReceivedMessageFromTimerStart(false);
            }
        }
        if(this.clientHandler!=null)
        {
            if(!clientHandler.getHasReceivedMessageFromTimerStart()){
                System.out.println("CONNECTION ERROR - DISCONNECTION TIMER - client connection lost, disconnecting! Shutting down the clientHandler");
                clientHandler.disconnectionManager();
            }
            else
            {
                clientHandler.setHasReceivedMessageFromTimerStart(false);
            }
        }
    }
}
