package it.polimi.ingsw.controller.mvc;

import it.polimi.ingsw.server.ClientHandler;

import java.util.List;

public interface Listener<T> {

    void update(String message, List<ClientHandler> clients);
}
