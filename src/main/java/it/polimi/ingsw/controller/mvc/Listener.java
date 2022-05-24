package it.polimi.ingsw.controller.mvc;

import it.polimi.ingsw.server.ClientHandler;

import java.util.List;

public interface Listener {

    public void update(String message, List<ClientHandler> clients);

    public void update(String message, ClientHandler client);
}
