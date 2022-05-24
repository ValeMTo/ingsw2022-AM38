package it.polimi.ingsw.client.view;

import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;

import java.util.*;

/**
 * ViewState contains all the information to visualize the current state of the gameBoard
 */
public class ViewState {
    private Map<String, Tower> players;
    private List<IslandView> islands;
    private List<SchoolBoardState> schoolBoards;
    private boolean isExpert;
    private boolean isTheCommander;
    private PhaseEnum currentPhase;
    private boolean activeView;
    private List<Integer> usableCards;
    private Tower playerTower;
    private Map<Color, Tower> professors;
    private int motherNature;
    private boolean isEndOfMatch = false;


    public ViewState(){
        isTheCommander = false;
        this.usableCards = new ArrayList<Integer>();
        this.players = new HashMap<>();
        this.isExpert = false;
        activeView = true;
        currentPhase = PhaseEnum.PLANNING;
        professors = new HashMap<>();
        islands = new ArrayList<>();
    }


    public void setViewState(Map<String, Tower> players, boolean isExpert) {
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

        schoolBoards = new ArrayList<>();
        for (Tower playerPerson : players.values()){
            schoolBoards.add(new SchoolBoardState(playerPerson));
        }

        for (Color color : Color.values()) {
            professors.put(color, null);

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

    private SchoolBoardState findSchoolBoard(Tower player){
        for (SchoolBoardState schoolboard: schoolBoards){
            if (schoolboard.getPlayer().equals(player)){
                return schoolboard;
            }
        }
        return null;
    }

    public void setDiningRoomOccupancy(Tower player, Map<Color, Integer> diningRoomOccupancy) {
        findSchoolBoard(player).fillDiningRoom(diningRoomOccupancy);
    }

    public void setSchoolEntranceOccupancy(Tower player, Map<Color, Integer> schoolEntranceOccupancy) {
        findSchoolBoard(player).fillSchoolEntrance(schoolEntranceOccupancy);
    }

    public Map<Color,Integer> getDiningRoomOccupancy(Tower player){
        return findSchoolBoard(player).getDiningRoomOccupancy();
    }

    public Map<Color,Integer> getSchoolEntranceOccupancy(Tower player){
        return findSchoolBoard(player).getSchoolEntranceOccupancy();
    }

    public Collection<Tower> getTowers(){
        return this.players.values();
    }

    public boolean isTheCommander(){
        return isTheCommander;
    }

    public void setTheCommander(){
        isTheCommander= true;
    }
}
