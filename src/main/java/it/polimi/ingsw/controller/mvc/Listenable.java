package it.polimi.ingsw.controller.mvc;

import java.util.ArrayList;
import java.util.List;

public abstract class Listenable<T> {

    private final List<Listener<T>> listeners = new ArrayList<>();

    public void addListener(Listener<T> Listener) {
        synchronized (listeners) {
            listeners.add(Listener);
        }
    }

    public void removeListener(Listener<T> Listener) {
        synchronized (listeners) {
            listeners.remove(Listener);
        }
    }

    public void notify(Listener listener, String message){
        listener.update(message);
    }
}
