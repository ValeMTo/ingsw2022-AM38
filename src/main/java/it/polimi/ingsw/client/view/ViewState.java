package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.GameSettings;
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
    private GameSettings gameSettings;


    public ViewState(){
        isTheCommander = false;
        this.usableCards = new ArrayList<Integer>();
        this.players = new HashMap<>();
        this.isExpert = false;
        activeView = true;
        currentPhase = PhaseEnum.CREATING_GAME;
        professors = new HashMap<>();
        islands = new ArrayList<>();
        gameSettings=null;
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

    public String getNamePlayer(Tower towerPlayer){
        for (String name : players.keySet()){
            if (players.get(name).equals(towerPlayer)){
                return name;
            }
        }
        return null;
    }
    public void setUsableCards(List<Integer> usableCards) {
        this.usableCards.clear();
        this.usableCards.addAll(usableCards);

    }
    public void setGameSettings(int actualPlayers, boolean isExpert, int numOfPlayers){
        this.gameSettings = new GameSettings(actualPlayers, isExpert, numOfPlayers);
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
        synchronized (this) {
            return this.currentPhase;
        }
    }

    public void setCurrentPhase(PhaseEnum currentPhase) {
        synchronized (this) {
            System.out.println("VIEW - Changing phase from: "+this.currentPhase+" to: "+currentPhase);
            this.currentPhase = currentPhase;
        }
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

    public void useAssistantCard(Tower player, int numCard){
        if (player.equals(playerTower)){
            usableCards.remove(numCard);
        }

        findSchoolBoard(player).setLastAssistantCardUsed(numCard);

    }

    public GameSettings getGameSettings(){
        return gameSettings;
    }
}
