package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.ClientCLI;
import it.polimi.ingsw.client.GameSettings;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Cloud;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.model.specialCards.SpecialCardName;

import java.util.*;

/**
 * ViewState contains all the information to visualize the current state of the gameBoard
 */
public class ViewState {
    private Map<String, Tower> players;
    private List<IslandView> islands;
    private Map<Cloud, Integer> clouds;
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
    private Map<SpecialCardName,Integer> usableSpecialCards;

    private String nickName = null;

    private Tower activePlayer;
    private boolean turnShown;
    private ClientCLI awaitingCLI;

    public ViewState(){
        this.turnShown = false;
        isTheCommander = false;
        this.usableCards = new ArrayList<Integer>();
        this.players = new HashMap<>();
        this.isExpert = false;
        activeView = false;
        currentPhase = PhaseEnum.CREATING_GAME;
        professors = new HashMap<>();
        islands = new ArrayList<>();
        gameSettings=null;
    }
    public ViewState(String nickname){
        super();
        this.nickName = nickname;
    }

    /**
     * Sets the CLI to be notify from wait
     * @param awaitingCLI
     */
    public synchronized void setAwaitingCLI(ClientCLI awaitingCLI){
        this.awaitingCLI = awaitingCLI;
    }

    /**
     * Sets the flag that tells the CLI has correctly shown the options and menues
     * @param turnShown
     */
    public synchronized void setTurnShown(boolean turnShown){
        this.turnShown = turnShown;
    }

    /**
     * Gets the flag that tells the CLI has correctly shown the options and menues
     * @return
     */
    public synchronized boolean getTurnShown(){
        return this.turnShown;
    }


    public synchronized void setViewState(Map<String, Tower> players, boolean isExpert) {
        for(String player:players.keySet())
            System.out.println("VIEW STATE - ADDING PLAYERS - Player "+player+" with tower "+players.get(player));

        this.clouds = new HashMap<>();
        this.usableSpecialCards = new HashMap<>();
        this.usableCards = new ArrayList<Integer>();
        this.players = new HashMap<>();
        this.players.putAll(players);
        this.isExpert = isExpert;
        activeView = true;
        currentPhase = PhaseEnum.CREATING_GAME;
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

        playerTower = players.get(this.nickName);
        if(awaitingCLI!=null)
        synchronized (awaitingCLI) {
            System.out.println("VIEW STATE - Hey CLI, it's time to wake up!");
            awaitingCLI.notifyAll();
        }
    }

    /**
     * Sets the active player
     * @param activePlayer : the active player to set
     */
    public synchronized void setActivePlayer(Tower activePlayer){
        this.activePlayer = activePlayer;
        this.turnShown = false;
        if(!this.activePlayer.equals(this.playerTower)) {
            this.activeView = false;
            System.out.println("VIEW STATE - I am not the active player");
        }
        else {
            this.activeView = true;
            this.turnShown = false;
            System.out.println("VIEW STATE - I am the active player");
        }
        if(awaitingCLI!=null)
            synchronized (awaitingCLI) {
                System.out.println("VIEW STATE - Hey CLI, it's time to wake up!");
                awaitingCLI.notifyAll();
        }
    }

    /**
     * Returns the active player
     * @return the tower of the active player
     */
    public synchronized Tower getActivePlayer(){
        return this.getPlayerTower();
    }
    public synchronized List<Integer> getUsableCards() {
        return new ArrayList<Integer>(usableCards);
    }

    public synchronized void setNamePlayer(String nickname){
        this.nickName = nickname;
    }
    public synchronized String getNamePlayer(Tower towerPlayer){
        System.out.println("VIEW STATE - Requested tower player "+towerPlayer);
        for (String name : players.keySet()){
            if (players.get(name).equals(towerPlayer)){
                return name;
            }
        }
        System.out.println("VIEW STATE - GetNamePlayer - OH NO");
        return null;
    }

    /**
     * Sets the active player and the phase together to avoid situations when the current phase is changed but not the player and vice-versa
     * @param currentPlayer
     * @param phase
     */
    public synchronized void setActivePlayerAndPhase(Tower currentPlayer, PhaseEnum phase){
        this.activePlayer = currentPlayer;
        this.currentPhase = phase;
        if(this.activePlayer.equals(this.playerTower)) {
            this.activeView = true;
            this.turnShown = false;
            System.out.println("VIEW STATE - setActivePlayerAndPhase - I am the chosen one I am  "+this.playerTower+" as "+this.activePlayer);
        }
        else {
            this.activeView = false;
            System.out.println("VIEW STATE - setActivePlayerAndPhase - I am not the chosen one I am  "+this.playerTower+" not "+this.activePlayer);
        }
        if(awaitingCLI!=null)
            synchronized (awaitingCLI) {
                System.out.println("VIEW STATE - setActivePlayerAndPhase - Hey CLI, it's time to wake up!");
                awaitingCLI.notifyAll();
            }
    }
    public synchronized void setUsableCards(List<Integer> usableCards) {
        this.usableCards.clear();
        this.usableCards.addAll(usableCards);

    }
    public synchronized void setGameSettings(int actualPlayers, boolean isExpert, int numOfPlayers){
        this.gameSettings = new GameSettings(actualPlayers, isExpert, numOfPlayers);
    }

    public synchronized boolean isEndOfMatch() {
        return isEndOfMatch;
    }

    public synchronized void setEndOfMatch(boolean endOfMatch) {
        isEndOfMatch = endOfMatch;
    }

    public synchronized Map<Color, Tower> getProfessors() {
        return new HashMap<>(professors);
    }

    public void setProfessors(Map<Color, Tower> professors) {
        this.professors = new HashMap<>(professors);
    }

    public synchronized PhaseEnum getCurrentPhase() {
        synchronized (this) {
            return this.currentPhase;
        }
    }

    public synchronized void setCurrentPhase(PhaseEnum currentPhase) {
            System.out.println("VIEW - Changing phase from: "+this.currentPhase+" to: "+currentPhase);
            this.currentPhase = currentPhase;
            if(this.activePlayer.equals(this.playerTower)) {
                this.activeView = true;
                this.turnShown = false;
                System.out.println("VIEW STATE - SetCurrentPhase - I am the chosen one I am  "+this.playerTower+" as "+this.activePlayer);
            }
            else {
                this.activeView = false;
                System.out.println("VIEW STATE - SetCurrentPhase - I am not the chosen one I am  "+this.playerTower+" not "+this.activePlayer);
            }
        if(awaitingCLI!=null)
            synchronized (awaitingCLI) {
            System.out.println("VIEW STATE - Hey CLI, it's time to wake up!");
            awaitingCLI.notifyAll();
        }
    }

    public synchronized boolean isActiveView() {
        return activeView;
    }

    public synchronized void setActiveView(boolean activeView) {
        this.activeView = activeView;
    }

    public synchronized int getMotherNature() {
        return motherNature;
    }

    public synchronized void setMotherNature(int motherNature) {
        this.motherNature = motherNature;
    }

    public synchronized List<IslandView> getIslands() {
        return new ArrayList<>(islands);
    }

    public synchronized void setIslands(List<IslandView> islands) {
        this.islands.clear();
        this.islands.addAll(islands);
    }

    public synchronized Tower getPlayerTower() {
        return playerTower;
    }

    public synchronized void setPlayerTower(Tower playerTower) {
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

    public synchronized void setDiningRoomOccupancy(Tower player, Map<Color, Integer> diningRoomOccupancy) {
        findSchoolBoard(player).fillDiningRoom(diningRoomOccupancy);
    }

    public synchronized void setSchoolEntranceOccupancy(Tower player, Map<Color, Integer> schoolEntranceOccupancy) {
        findSchoolBoard(player).fillSchoolEntrance(schoolEntranceOccupancy);
    }

    public synchronized Map<Color,Integer> getDiningRoomOccupancy(Tower player){
        return findSchoolBoard(player).getDiningRoomOccupancy();
    }

    public synchronized Map<Color,Integer> getSchoolEntranceOccupancy(Tower player){
        return findSchoolBoard(player).getSchoolEntranceOccupancy();
    }

    public synchronized Collection<Tower> getTowers(){
        return this.players.values();
    }

    public synchronized boolean isTheCommander(){
        return isTheCommander;
    }

    public synchronized void setTheCommander(){
        isTheCommander= true;
    }

    public synchronized void useAssistantCard(Tower player, int numCard){
        if (player.equals(playerTower)){
            System.out.println("VIEW STATE - This player has used card "+numCard);
            usableCards.remove(numCard);
        }
        findSchoolBoard(player).setLastAssistantCardUsed(numCard);
    }

    public boolean isExpert(){
        return this.isExpert;
    }

    /**
     * Sets the specialCard of this particular game
     * @param specialCard : the list of names of the usable special cards
     */
    public synchronized void setUsableSpecialCard(Map<SpecialCardName,Integer> specialCard){
        if(usableSpecialCards!=null)
            this.usableSpecialCards.clear();
        this.usableSpecialCards.putAll(specialCard);
    }

    /**
     * Returns a copy of the special card of this game,
     * @return : the map with the special card name and its cost (Integer)
     */
    public synchronized Map<SpecialCardName,Integer> getUsableSpecialCards() throws FunctionNotImplementedException {
        if(!isExpert) throw new FunctionNotImplementedException("VIEW STATE - ERROR - Function for expert game mode only");
        Map<SpecialCardName,Integer> returnList = new HashMap<>();
        returnList.putAll(this.usableSpecialCards);
        return returnList;
    }

    /**
     * Set the clouds with their own positions
     * @param clouds : the Map with the clouds and their position
     */
    public synchronized void setCloud(Map<Cloud,Integer> clouds){
        this.clouds.clear();
        this.clouds.putAll(clouds);
    }

    /**
     * Return the clouds stored into the ViewState
     * @return the map with the clouds and their respective positions
     */
    public synchronized Map<Cloud,Integer> getClouds(){
        Map<Cloud,Integer> returnMap = new HashMap<>();
        returnMap.putAll(this.clouds);
        return returnMap;
    }
    public synchronized GameSettings getGameSettings(){
        return gameSettings;
    }
}
