package it.polimi.ingsw;

import java.util.HashMap;

public class Island {
    private HashMap<Color,Integer> influence;
    private int position;
    private boolean influenceIsEnabled = true;
    private Tower towerColor = null;
    private int tower = 0;

    /**
     * Constructor that initialize the HashMap and position
     *
     * @param position: is the initial position of the Island in the gameBoard
     */
    public Island(int position){
        influence = new HashMap<Color,Integer>();
        this.position = position;
    }

    /**
     * Method that return if any Player has tower in it.
     *
     * @return true if there are towers, false if not
     */
    public boolean isTaken(){
        if(towerColor==null)
            return false;
        return true;
    }

    /**
     * Method to add a student of a particular Color on the Island
     *
     * @param color : color parameter to add the student
     * @return always true as the Island has no student limits
     */
    public boolean addStudent(Color color){
        if(!influence.containsKey(color))
            influence.put(color, 1);
        else
            influence.put(color, influence.get(color) + 1);
        return true;
    }

    /**
     * Method used to set the block to the influence computation
     *  sets influenceIsEnabled to false
     */
    //DEV. COMMENT - should it return a boolean or exception if already blocked? Or control is done before the call with isInfluenceEnabled?
    public void disableInfluence(){
        influenceIsEnabled = false;
    }

    /**
     * Method used to enable the influence computation
     *
     */
    public void enableInfluence(){
        influenceIsEnabled = true;
    }

    /**
     * Method that return the influence computation status
     *
     * @return influenceIsEnabled
     */
    public boolean isInfluenceEnabled(){
        return influenceIsEnabled;
    }

    /**
     * Method to count the students by the given Color
     *
     * @param color : color of the students we want to count
     * @return the number of students with that color in the Island
     */
    public int studentNumber(Color color){
        if(!influence.containsKey(color))
            return 0;
        return influence.get(color);
    }

    /**
     * Getter of the Tower Color of the towers placed in Island (null if none)
     *
     * @return null if no towers, Tower color of the towers in the Island
     */
    public Tower getTower(){
        return towerColor;
    }

    /**
     * Getter of the Tower numbers on the Islands (also coincide with islands number in the Island group)
     *
     * @return tower numbers in the Island
     */
    public int getTowerNumber(){
        return tower;
    }

    /**
     * Setter for the Tower Color (conquest of an Island)
     *
     * @param tower : color of the Tower
     */
    public void setTower(Tower tower){
        this.towerColor = tower;
    }

    /**
     * Setter for the Number of towers on the Island (if Island groups are formed the nr. of Islands correspond to the nr. of towers)
     *
     * @param num : number of towers
     */
    public void setTowerNumber(int num){
        this.tower = num;
    }

    /**
     * Setter method for the Island position. Used when Island groups are formed.
     *
     * @param pos : new position of the Island / Island group
     */
    public void setPosition(int pos){

    }



}


