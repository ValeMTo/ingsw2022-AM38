package it.polimi.ingsw.controller.mvc;

import it.polimi.ingsw.server.ClientHandler;

import java.util.List;

public class ModelListener implements Listener {

    // Updates all the clients
    @Override
    public void update(String message, List<ClientHandler> clients) {
        for( ClientHandler client : clients){
            client.asyncSend(message);
        }
    }

    // Updates one specific client
    public void update(String message, ClientHandler client) {
        client.asyncSend(message);
    }

}
