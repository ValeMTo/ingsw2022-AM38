package it.polimi.ingsw.connectionManagement;

import it.polimi.ingsw.client.CLI.ClientCLI;
import it.polimi.ingsw.client.Reader;

import java.util.TimerTask;

public class disconnectionTimer extends TimerTask {
    private Reader reader;
    public disconnectionTimer(Reader reader){
        this.reader = reader;
    }

    @Override
    public void run(){
        if(this.reader!=null)
        {
            if(!reader.isHasReceivedMessageFromTimerStart()){
                System.out.println("CONNECTION ERROR - DISCONNECTION TIMER - server connection lost, disconnecting! Shutting down");
                reader.disconnect();
            }
            else
            {
                reader.setHasReceivedMessageFromTimerStart(false);
            }
        }
    }
}
