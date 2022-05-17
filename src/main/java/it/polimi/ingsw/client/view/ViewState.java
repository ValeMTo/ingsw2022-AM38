package it.polimi.ingsw.client.view;

import it.polimi.ingsw.controller.PhaseEnum;
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
    private final Map<String, Tower> players;
    private final List<IslandView> islands;
    private final Map<Color, Integer> schoolEntranceOccupancy;
    private final Map<Color, Integer> diningRoomOccupancy;
    private final boolean isExpert;
    private PhaseEnum currentPhase;
    private boolean activeView;
    private final List<Integer> usableCards;
    private Tower playerTower;
    private Map<Color, Tower> professors;
    private int motherNature;
    private boolean isEndOfMatch = false;

    public ViewState(Map<String, Tower> players, boolean isExpert) {
        this.usableCards = new ArrayList<Integer>();
        this.players = new HashMap<>(players);
        this.isExpert = isExpert;
        activeView = true;
        currentPhase = PhaseEnum.PLANNING;
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

    public boolean isEndOfMatch() {
        return isEndOfMatch;
    }

    public void setEndOfMatch(boolean endOfMatch) {
        isEndOfMatch = endOfMatch;
    }

    public Map<Color, Tower> getProfessors() {
        return new HashMap<>(professors);
    }

    public void setProfessors(Map<Color, Tower> professors) {
        this.professors = new HashMap<>(professors);
    }

    public PhaseEnum getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(PhaseEnum currentPhase) {
        this.currentPhase = currentPhase;
    }

    public boolean isActiveView() {
        return activeView;
    }

    public void setActiveView(boolean activeView) {
        this.activeView = activeView;
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

    public Map<Color, Integer> getDiningRoomOccupancy() {
        return new HashMap<>(diningRoomOccupancy);
    }

    public void setDiningRoomOccupancy(Map<Color, Integer> diningRoomOccupancy) {
        this.diningRoomOccupancy.clear();
        this.diningRoomOccupancy.putAll(diningRoomOccupancy);
    }

    public Map<Color, Integer> getSchoolEntranceOccupancy() {
        return new HashMap<>(schoolEntranceOccupancy);
    }

    public void setSchoolEntranceOccupancy(Map<Color, Integer> schoolEntranceOccupancy) {
        this.schoolEntranceOccupancy.clear();
        this.schoolEntranceOccupancy.putAll(schoolEntranceOccupancy);
    }
}
