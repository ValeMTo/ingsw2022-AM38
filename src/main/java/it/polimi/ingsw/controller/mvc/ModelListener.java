package it.polimi.ingsw.controller.mvc;

import it.polimi.ingsw.server.ClientHandler;

import java.util.List;

public class ModelListener implements Listener {

    @Override
    public void update(String message, List<ClientHandler> clients) {
        for( ClientHandler client : clients){
            client.asyncSend(message);
        }
    }

    public void update(String message, ClientHandler client) {
        client.asyncSend(message);
    }

}
