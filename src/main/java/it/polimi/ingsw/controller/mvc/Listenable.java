package it.polimi.ingsw.controller.mvc;

import it.polimi.ingsw.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class Listenable<T> {

    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener Listener) {
        synchronized (listeners) {
            listeners.add(Listener);
        }
    }

    public void removeListener(Listener Listener) {
        synchronized (listeners) {
            listeners.remove(Listener);
        }
    }

    public void notify(Listener listener, String message, List<ClientHandler> clients){
        if(clients==null)
            return;
        for(ClientHandler client:clients){
            System.out.println("NOTIFY - I am gonna notify "+client.getNickName());
        }
        listener.update(message, clients);
    }
}
