package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.ClientCLI;
import it.polimi.ingsw.client.GameSettings;
import it.polimi.ingsw.client.gui.MainGUI;
import it.polimi.ingsw.client.gui.controllers.LobbyMenuController;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.controller.SpecialCardRequiredAction;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Cloud;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import javafx.application.Platform;

import java.util.*;

/**
 * ViewState contains all the information to visualize the current state of the gameBoard
 */
public class ViewState {
    private Map<String, Tower> players = new HashMap<>();
    private List<IslandView> islands = new ArrayList<>();
    private Map<Cloud, Integer> clouds = new HashMap<>();
    private List<String> onlinePlayers;
    private List<SchoolBoardState> schoolBoards = new ArrayList<>();
    private boolean isExpert;
    private boolean isTheCommander;
    private PhaseEnum currentPhase;
    private boolean activeView;
    private List<Integer> usableCards = new ArrayList<>();
    private Tower playerTower;
    private Map<Color, Tower> professors = new HashMap<>();
    private int motherNature;
    private boolean isEndOfMatch = false;
    private GameSettings gameSettings;
    private Map<SpecialCardName,Integer> usableSpecialCards = new HashMap<>();
    private Map<SpecialCardName,Map<Color,Integer>> specialCardWithStudents = new HashMap<>();
    private int herbalistTiles = 0;


    private String nickName = null;

    private Tower activePlayer;
    private boolean turnShown;
    private ClientCLI awaitingCLI;
    private MainGUI awaitingGUI;
    private boolean isCli;
    private SpecialCardRequiredAction specialCardRequiredAction;//TODO !!!

    public ViewState(boolean isCli){
        this.onlinePlayers =  new ArrayList<>();
        this.isCli = isCli;
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
     * Add a new online player to the online players list in the ViewState
     */
    public void addOnlinePlayer(String nickName){
        onlinePlayers.add(nickName);
    }

    /**
     * Return onlinePlayers list
     */
    public List<String> getOnlinePlayers(){
        List<String> list = new ArrayList<>();
        list.addAll(onlinePlayers);
        return list;
    }

    /**
     * Return the number of online players
     */
    public int getNumberOfOnlinePlayer(){
        return onlinePlayers.size();
    }


    /**
     * Sets the CLI to be notify from wait
     * @param awaitingCLI
     */
    public synchronized void setAwaitingCLI(ClientCLI awaitingCLI){
        this.awaitingCLI = awaitingCLI;
    }

    /**
     * Sets the GUI to be notify from wait
     * @param awaitingGUI
     */
    public synchronized void setAwaitingGUI(MainGUI awaitingGUI){
        this.awaitingGUI = awaitingGUI;
    }


    /**
     * Sets the flag that tells the CLI has correctly shown the options and menues
     * @param turnShown
     */
    public synchronized void setTurnShown(boolean turnShown){
        this.turnShown = turnShown;
        if(!turnShown&&this.playerTower!=null&&this.playerTower.equals(this.activePlayer)) {
            //System.out.println("VIEW STATE - setTurnShown - The view is active!");
            setActiveView(true);
        }
        if(awaitingCLI!=null&&!turnShown) {
            //System.out.println("VIEW STATE - setTurnShown - Hey CLI, it's time to wake up!");
            synchronized (awaitingCLI) {
                awaitingCLI.notifyAll();
            }
        }
    }

    /**
     * Sets the Herbalist tiles number
     * @param numOfTiles
     */
    public synchronized void setHerbalistTiles(int numOfTiles){
        this.herbalistTiles = numOfTiles;
        wake();
    }

    /**
     * Gets the herbalist tiles number
     * @return
     */
    public synchronized int getHerbalistTiles(){
        return this.herbalistTiles;
    }

    /**
     * Sets the student map of the special card with students
     */
    public synchronized void setSpecialCardWithStudents(SpecialCardName specialCard, Map<Color,Integer> studentMap){
        if(this.specialCardWithStudents==null) {
            this.specialCardWithStudents = new HashMap<>();
        }
        Map<Color,Integer> studentsToAdd = new HashMap<>();
        if(studentMap!=null) {
            studentsToAdd.putAll(studentMap);
        }
        this.specialCardWithStudents.put(specialCard,studentsToAdd);
        wake();
    }

    /**
     * Gets the map of a particular special card
     * @param specialCard
     * @return
     */
    public synchronized Map<Color,Integer> getSpecialCardStudents(SpecialCardName specialCard){
        Map<Color,Integer> studentsToReturn = new HashMap<>();
        if(this.specialCardWithStudents.get(specialCard)!=null)
            studentsToReturn.putAll(this.specialCardWithStudents.get(specialCard));
        return studentsToReturn;
    }

    /**
     * Returns the map with the special cards that contains students and the students map
     * @return
     */
    public synchronized Map<SpecialCardName,Map<Color,Integer>> getSpecialCardWithStudents(){
        Map<SpecialCardName,Map<Color,Integer>> specialCardNameMapToReturn = new HashMap<>();
        if(this.specialCardWithStudents!=null)
            specialCardNameMapToReturn.putAll(this.specialCardWithStudents);
        return specialCardNameMapToReturn;
    }

    /**
     * Gets the special card required action
     * @return
     */
    public synchronized SpecialCardRequiredAction getSpecialPhase(){
        return this.specialCardRequiredAction;
    }

    /**
     * Sets the current phase and the required action fot the use of the special card
     * @param phase
     * @param specialCardRequiredAction
     */
    public synchronized void setSpecialCardPhase(PhaseEnum phase, SpecialCardRequiredAction specialCardRequiredAction){
        this.currentPhase = phase;
        this.specialCardRequiredAction = specialCardRequiredAction;
        this.setTurnShown(false);
    }

    /**
     *
     * @return true if the player has enough coins to use at least one special card
     */
    public synchronized boolean playerHasCoinsToUseASpecialCard(){
        if(usableSpecialCards==null)
            return false;
        int coins = getPlayerCoins();
        for(Integer cost:usableSpecialCards.values())
            if(coins>cost)
                return true;
        return false;
    }

    /**
     * Gets the flag that tells the CLI has correctly shown the options and menues
     * @return
     */
    public synchronized boolean getTurnShown(){
        return this.turnShown;
    }


    public synchronized void setViewState(Map<String, Tower> players, boolean isExpert) {
        //for(String player:players.keySet())
            //System.out.println("VIEW STATE - ADDING PLAYERS - Player "+player+" with tower "+players.get(player));
        this.clouds = new HashMap<>();
        this.usableSpecialCards = new HashMap<>();
        this.usableCards = new ArrayList<>();
        this.players = new HashMap<>();
        this.players.putAll(players);
        this.isExpert = isExpert;
        activeView = true;
        currentPhase = PhaseEnum.CREATING_GAME;
        for (int i = 1; i < 11; i++)
            usableCards.add(i);
        this.schoolBoards = new ArrayList<>();
        for(Tower tower:players.values())
        {
            SchoolBoardState schoolBoardState = new SchoolBoardState(tower);
            this.schoolBoards.add(schoolBoardState);
            //System.out.println("VIEW STATE - setViewState - adding new SchoolBoard "+schoolBoardState+" of "+schoolBoardState.getPlayer());
        }
        playerTower = players.get(this.nickName);
        if(awaitingCLI!=null)
        synchronized (awaitingCLI) {
            //System.out.println("VIEW STATE - Hey CLI, it's time to wake up!");
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
            //System.out.println("VIEW STATE - I am not the active player");
        }
        else {
            this.activeView = true;
            this.turnShown = false;
            //System.out.println("VIEW STATE - I am the active player");
        }
        if(awaitingCLI!=null)
            synchronized (awaitingCLI) {
                //System.out.println("VIEW STATE - Hey CLI, it's time to wake up!");
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

    public synchronized void setNickname(String nickname){
        this.nickName = nickname;
    }
    public synchronized String getNamePlayer(Tower towerPlayer){
        //System.out.println("VIEW STATE - Requested tower player "+towerPlayer);
        for (String name : players.keySet()){
            if (players.get(name).equals(towerPlayer)){
                return name;
            }
        }
        //System.out.println("VIEW STATE - GetNamePlayer - OH NO");
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
            //System.out.println("VIEW STATE - setActivePlayerAndPhase - I am the chosen one I am  "+this.playerTower+" as "+this.activePlayer);
        }
        else {
            this.activeView = false;
            //System.out.println("VIEW STATE - setActivePlayerAndPhase - I am not the chosen one I am  "+this.playerTower+" not "+this.activePlayer);
        }
        if(awaitingCLI!=null)
            synchronized (awaitingCLI) {
                //System.out.println("VIEW STATE - setActivePlayerAndPhase - Hey CLI, it's time to wake up!");
                awaitingCLI.notifyAll();
            }
    }
    public synchronized void setUsableCards(List<Integer> usableCards) {
        this.usableCards.clear();
        this.usableCards.addAll(usableCards);

    }
    public synchronized void setGameSettings(List<String> actualPlayers, boolean isExpert, int numOfPlayers){
        this.gameSettings = new GameSettings(actualPlayers, isExpert, numOfPlayers);
        this.isExpert = gameSettings.getExpert();
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

    public synchronized void setProfessors(Map<Color, Tower> professors) {
        this.professors = new HashMap<>(professors);
        if(this.currentPhase==PhaseEnum.ACTION_MOVE_STUDENTS && this.playerTower.equals(this.activePlayer))
            if(awaitingCLI!=null)
                awaitingCLI.printForMoveStudents();
    }

    public synchronized PhaseEnum getCurrentPhase() {
        synchronized (this) {
            return this.currentPhase;
        }
    }

    public synchronized void setCurrentPhase(PhaseEnum currentPhase) {
            //System.out.println("VIEW - Changing phase from: "+this.currentPhase+" to: "+currentPhase);
            this.currentPhase = currentPhase;
            if(this.activePlayer.equals(this.playerTower)) {
                this.activeView = true;
                this.turnShown = false;
                //System.out.println("VIEW STATE - SetCurrentPhase - I am the chosen one I am  "+this.playerTower+" as "+this.activePlayer);
            }
            else {
                this.activeView = false;
                //System.out.println("VIEW STATE - SetCurrentPhase - I am not the chosen one I am  "+this.playerTower+" not "+this.activePlayer);
            }
        if(awaitingCLI!=null)
            synchronized (awaitingCLI) {
            //System.out.println("VIEW STATE - Hey CLI, it's time to wake up!");
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
        if(awaitingCLI!=null) {
            if (this.currentPhase == PhaseEnum.ACTION_MOVE_STUDENTS && this.playerTower.equals(this.activePlayer))
                awaitingCLI.printForMoveStudents();
            else if (this.currentPhase == PhaseEnum.ACTION_MOVE_MOTHER_NATURE && this.playerTower.equals(this.activePlayer))
                awaitingCLI.printForMoveMotherNature();
        }
    }

    public synchronized List<IslandView> getIslands() {
        return new ArrayList<>(islands);
    }

    public synchronized void setIslands(List<IslandView> islands) {
        this.islands.clear();
        this.islands.addAll(islands);
        if(awaitingCLI!=null) {
            if (this.currentPhase == PhaseEnum.ACTION_MOVE_STUDENTS && this.playerTower.equals(this.activePlayer))
                awaitingCLI.printForMoveStudents();
            else if (this.currentPhase == PhaseEnum.ACTION_MOVE_MOTHER_NATURE && this.playerTower.equals(this.activePlayer))
                awaitingCLI.printForMoveMotherNature();
        }
    }

    /**
     * Gets in upper case, as a list of Strings, the special card names of this game
     * @return
     * @throws FunctionNotImplementedException : if it not the expert game mode
     */
    public synchronized List<String> getUpperCaseSpecialCards() throws FunctionNotImplementedException{
        if(!isExpert)
            throw new FunctionNotImplementedException();
        if(usableSpecialCards!=null)
        {
            List<String> returnList = new ArrayList<>();
            for(SpecialCardName specialCard : usableSpecialCards.keySet())
                returnList.add(specialCard.name().toUpperCase());
            return returnList;
        }
        return new ArrayList<>();
    }

    /**
     * Returns the coins owned by this player
     * @return the coins owned by this player
     */
    public synchronized int getPlayerCoins(){
        SchoolBoardState schoolBoardState = findSchoolBoard(this.playerTower);
        if(schoolBoardState!=null)
        {
            return schoolBoardState.getCoins();
        }
        return 0;
    }

    /**
     * Returns the coins owned by the specified player
     * @return the coins owned by the specified  player
     */
    public synchronized int getPlayerCoins(Tower player){
        SchoolBoardState schoolBoardState = findSchoolBoard(player);
        if(schoolBoardState!=null)
        {
            return schoolBoardState.getCoins();
        }
        return 0;
    }

    public synchronized void setCoins(Tower player, int coins)
    {
        SchoolBoardState schoolBoardState = findSchoolBoard(player);
        if(schoolBoardState!=null)
        {
            schoolBoardState.setCoins(coins);
        }
    }

    public synchronized Tower getPlayerTower() {
        return playerTower;
    }

    public synchronized void setPlayerTower(Tower playerTower) {
        this.playerTower = playerTower;
    }

    private synchronized SchoolBoardState findSchoolBoard(Tower player){
        for (SchoolBoardState schoolboard: schoolBoards){
            if (schoolboard.getPlayer().equals(player)){
                return schoolboard;
            }
        }
        return null;
    }

    public synchronized void setDiningRoomOccupancy(Tower player, Map<Color, Integer> diningRoomOccupancy) {
        findSchoolBoard(player).fillDiningRoom(diningRoomOccupancy);
        if(awaitingCLI!=null) {
            if (this.currentPhase == PhaseEnum.ACTION_MOVE_STUDENTS && this.playerTower.equals(this.activePlayer))
                awaitingCLI.printForMoveStudents();
        }
    }

    public synchronized void setSchoolEntranceOccupancy(Tower player, Map<Color, Integer> schoolEntranceOccupancy) {
        findSchoolBoard(player).fillSchoolEntrance(schoolEntranceOccupancy);
        if(awaitingCLI!=null) {
            if (this.currentPhase == PhaseEnum.ACTION_MOVE_STUDENTS && this.playerTower.equals(this.activePlayer))
                awaitingCLI.printForMoveStudents();
        }
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

    /**
     * Sets the last used assistant card
     * @param tower
     * @param lastCardUsed
     */
    public void setLastCardUsed(Tower tower, int lastCardUsed){
        SchoolBoardState schoolBoardState = this.findSchoolBoard(tower);
        if(schoolBoardState!=null)
            schoolBoardState.setLastAssistantCardUsed(lastCardUsed);
    }
    public boolean isExpert(){
        return this.isExpert;
    }

    /**
     * Sets the specialCard of this particular game
     * @param specialCard : the list of names of the usable special cards
     */
    public synchronized void setUsableSpecialCard(Map<SpecialCardName,Integer> specialCard){
        if(usableSpecialCards==null)
            this.usableSpecialCards = new HashMap<>();
        else
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
        //System.out.println("VIEW STATE - setCloud - Updating the clouds "+clouds.size()+" clouds updating");
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

    /**
     * Sets the number of islands and remove the islands
     * @param numIslands
     */
    public synchronized void setIslandNumber(int numIslands){
        List<IslandView> islandsToModify = new ArrayList<>();
        islandsToModify.addAll(islands);
        for(IslandView island:islands)
        {
            if (island.getPosition()>numIslands)
                islandsToModify.remove(island);
        }
        islands = islandsToModify;
        if(awaitingCLI!=null) {
            if (this.currentPhase == PhaseEnum.ACTION_MOVE_STUDENTS && this.playerTower.equals(this.activePlayer))
                awaitingCLI.printForMoveStudents();
            else if (this.currentPhase == PhaseEnum.ACTION_MOVE_MOTHER_NATURE)
                awaitingCLI.printForMoveMotherNature();
        }
    }

    public synchronized String getNickname(){
        return nickName;
    }


    public MainGUI getAwaitingGUI() {
        return this.awaitingGUI;
    }

    public boolean isCli(){
        return isCli;
    }

    public void wake(){
        if (isCli == true){
            synchronized (awaitingCLI) {
                System.out.println("Waking the CLI");
                awaitingCLI.notifyAll();
            }
        } else {
            synchronized (awaitingGUI){
                System.out.println("Waking the GUI");
                awaitingGUI.notifyAll();
            }
        }
    }
}
