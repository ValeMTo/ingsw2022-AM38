package it.polimi.ingsw.client.view;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewState contains all the information to visualize the current state of the gameBoard
 */
public class ViewState {
    List<Integer> usableCards;
    Tower playerTower;
    Map<Color, Integer> schoolEntranceOccupancy;
    Map<Color, Integer> diningRoomOccupancy;
    private Map<Color, Tower> professors;
    private final List<IslandView> islands;
    private int motherNature;


    public ViewState() {
        professors = new HashMap<>();
        islands = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            islands.add(new IslandView(i));
        }
        schoolEntranceOccupancy = new HashMap<>();
        diningRoomOccupancy = new HashMap<>();
        for (Color color : Color.values()) {
            professors.put(color, null);
            schoolEntranceOccupancy.put(color, 0);
            diningRoomOccupancy.put(color, 0);

        }
        motherNature = 1;
        for (int i = 1; i < 11; i++)
            usableCards.add(i);
    }

    public List<Integer> getUsableCards() {
        return new ArrayList<Integer>(usableCards);
    }

    public void setUsableCards(List<Integer> usableCards) {
        this.usableCards.clear();
        this.usableCards.addAll(usableCards);
    }

    public Map<Color, Tower> getProfessors() {
        return new HashMap<>(professors);
    }

    public void setProfessors(Map<Color, Tower> professors) {
        this.professors = new HashMap<>(professors);
    }

    public int getMotherNature() {
        return motherNature;
    }

    public void setMotherNature(int motherNature) {
        this.motherNature = motherNature;
    }

    public List<IslandView> getIslands() {
        return new ArrayList<>(islands);
    }

    public void setIslands(List<IslandView> islands) {
        this.islands.clear();
        this.islands.addAll(islands);
    }

    public Tower getPlayerTower() {
        return playerTower;
    }

    public void setPlayerTower(Tower playerTower) {
        this.playerTower = playerTower;
    }
}
