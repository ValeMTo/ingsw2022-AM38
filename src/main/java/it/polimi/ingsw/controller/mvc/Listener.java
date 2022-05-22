package it.polimi.ingsw.controller.mvc;

public interface Listener<T> {

    void update(T message);

    void update();
}
