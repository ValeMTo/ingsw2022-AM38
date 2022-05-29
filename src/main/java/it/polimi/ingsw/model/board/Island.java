package it.polimi.ingsw.model.board;

import com.sun.jdi.PrimitiveValue;
import it.polimi.ingsw.controller.mvc.Listenable;
import it.polimi.ingsw.controller.mvc.Listener;
import it.polimi.ingsw.controller.mvc.ModelListener;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.server.ClientHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Island extends Listenable {
    private Listener modelListener;
    private List<ClientHandler> clients = null;
    protected HashMap<Color, Integer> influence;
    protected int position;
    protected boolean influenceIsEnabled = true;
    protected Tower towerColor = null;
    protected int towerNumber = 0;

    /**
     * Constructor that initializes the HashMap and position
     *
     * @param position: is the initial position of the Island in the gameBoard
     */
    public Island(int position) {
        influence = new HashMap<Color, Integer>();
        this.position = position;
    }

    /**
     * Sets the listener and clients for the update and notify for changes
     * @param modelListener : the modelListener
     * @param clients : the clients to notify
     */
    public void setListenerAndClients(Listener modelListener, List<ClientHandler> clients){
        this.modelListener = modelListener;
        this.clients = new ArrayList<>();
        this.clients.addAll(clients);
        if(this.clients!=null && this.modelListener!=null){
            System.out.println("ISLAND "+this.position+" - notify my existence!");
            Map<Color,Integer> returnMap = new HashMap<>();
            returnMap.putAll(this.influence);
            notify(modelListener,MessageGenerator.islandViewUpdateMessage(this.position,returnMap,this.towerColor,this.towerNumber,this.isInfluenceEnabled()),clients);
        }
    }

    private void notifySomethingHasChanged(){
        if(this.clients!=null && this.modelListener!=null){
            System.out.println("ISLAND "+this.position+" - notify a change!");
            Map<Color,Integer> returnMap = new HashMap<>();
            returnMap.putAll(this.influence);
            notify(modelListener,MessageGenerator.islandViewUpdateMessage(this.position,returnMap,this.towerColor,this.towerNumber,this.isInfluenceEnabled()),clients);
        }
    }

    /**
     * Returns if any Player has tower in it.
     *
     * @return true if there are towers, false if not
     */
    public boolean isTaken() {
        if (towerColor == null)
            return false;
        return true;
    }

    /**
     * Adds a student of a particular Color on the Island
     *
     * @param color : color parameter to add the student
     * @return always true as the Island has no student limits
     */
    public boolean addStudent(Color color) {
        if (!influence.containsKey(color))
            influence.put(color, 1);
        else
            influence.put(color, influence.get(color) + 1);
        notifySomethingHasChanged();
        return true;
    }

    /**
     * Returns a copy of the student map of this island
     * @return
     */
    public Map<Color,Integer> getStudentMap(){
        Map<Color,Integer> studentMap = new HashMap<>();
        studentMap.putAll(this.influence);
        return studentMap;
    }

    /**
     * Adds in block students from a Map
     * @param studentsToAdd
     * @return
     */
    public boolean addStudent(Map<Color,Integer> studentsToAdd){
        for(Color color: studentsToAdd.keySet()){
            if (!influence.containsKey(color))
                influence.put(color, studentsToAdd.get(color));
            else
                influence.put(color, influence.get(color) + studentsToAdd.get(color));
        }
        notifySomethingHasChanged();
        return true;
    }

    /**
     * Sets the block to the influence computation
     * sets influenceIsEnabled to false
     */
    public void disableInfluence() {
        influenceIsEnabled = false;
        notifySomethingHasChanged();
    }

    /**
     * Enable the influence computation
     */
    public void enableInfluence() {
        boolean notify=false;
        if(!influenceIsEnabled)
            notify = true;
        influenceIsEnabled = true;
        if(notify)
            notifySomethingHasChanged();
    }

    /**
     * Returns the influence computation status
     *
     * @return influenceIsEnabled
     */
    public boolean isInfluenceEnabled() {
        return influenceIsEnabled;
    }

    /**
     * Counts the students by the given Color
     *
     * @param color : color of the students we want to count
     * @return the number of students with that color in the Island
     */
    public int studentNumber(Color color) {
        if (!influence.containsKey(color))
            return 0;
        return influence.get(color);
    }

    /**
     * Getter of the Tower Color of the towers placed in Island (null if none)
     *
     * @return null if no towers, Tower color of the towers in the Island
     */
    public Tower getTower() {
        return towerColor;
    }

    /**
     * Getter of the Tower numbers on the Islands (also coincide with islands number in the Island group)
     *
     * @return tower numbers in the Island
     */
    public int getTowerNumber() {
        return towerNumber;
    }

    /**
     * Setter for the Tower Color (conquest of an Island)
     *
     * @param tower : color of the Tower
     */
    public void setTower(Tower tower) {
        this.towerColor = tower;
        notifySomethingHasChanged();
    }

    /**
     * Setter for the Number of towers on the Island (if Island groups are formed the nr. of Islands correspond to the nr. of towers)
     *
     * @param towerNum : number of towers
     */
    public void setTowerNumber(int towerNum) {
        this.towerNumber = towerNum;
        notifySomethingHasChanged();
    }

    /**
     * Setter method for the Island position. Used when Island groups are formed.
     *
     * @param pos : new position of the Island / Island group
     */
    public void setPosition(int pos) {
        this.position = pos;
        notifySomethingHasChanged();
    }

    /**
     * Getter for position of the island (same index if islands are grouped)
     * @return the position of the island on the game-board (between 1 and 12)
     */
    public int getPosition(){ return this.position;}

}


