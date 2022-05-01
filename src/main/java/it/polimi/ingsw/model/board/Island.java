package it.polimi.ingsw.model.board;

import java.util.HashMap;

public class Island {
    private HashMap<Color, Integer> influence;
    private int position;
    private boolean influenceIsEnabled = true;
    private Tower towerColor = null;
    private int towerNumber = 0;

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
        return true;
    }

    /**
     * Sets the block to the influence computation
     * sets influenceIsEnabled to false
     */
    public void disableInfluence() {
        influenceIsEnabled = false;
    }

    /**
     * Enable the influence computation
     */
    public void enableInfluence() {
        influenceIsEnabled = true;
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
    }

    /**
     * Setter for the Number of towers on the Island (if Island groups are formed the nr. of Islands correspond to the nr. of towers)
     *
     * @param towerNum : number of towers
     */
    public void setTowerNumber(int towerNum) {
        this.towerNumber = towerNum;
    }

    /**
     * Setter method for the Island position. Used when Island groups are formed.
     *
     * @param pos : new position of the Island / Island group
     */
    public void setPosition(int pos) {
        this.position = pos;
    }

    /**
     * Getter for position of the island (same index if islands are grouped)
     * @return the position of the island on the game-board (between 1 and 12)
     */
    public int getPosition(){ return this.position;}

}


