package it.polimi.ingsw.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Game is the easy controller of each match
 */
public class Game{
    private final int id;
    private List<String> players = new ArrayList<>();
    private final boolean isExpert;
    private final int numOfPlayer;

    public Game(List<String> players, int id, boolean isExpert, int numOfPlayer){
        this.id = id;
        this.isExpert = isExpert;
        this.numOfPlayer = numOfPlayer;
        this.players.addAll(players);
    }
}
