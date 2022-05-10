package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.LocationNotAllowedException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.GameBoard;
import it.polimi.ingsw.model.board.StudentCounter;

import java.util.*;

public abstract class GameOrchestrator {
    protected List<String> players;
    protected String planningOrder [];
    protected String actionOrder[];
    protected PhaseEnum currentPhase;
    protected int activePlayer;
    protected Object phaseBlocker = new Object();
    protected Object actionBlocker = new Object();
    protected int studentMovesLeft;
    protected final int maxStudentMoves = 3;
    protected GameBoard gameBoard;


    public GameOrchestrator(List<String> players){
        this.players = new ArrayList<String>();
        this.players.addAll(players);
        this.activePlayer = 0;
        planningOrder = new String[players.size()];
        for(int i=0;i<players.size();i++)
            planningOrder[i] = players.get(i);
        actionOrder = new String[players.size()];
        currentPhase =  PhaseEnum.PLANNING;
    }

    /**
     * Getter of the active payer, used to know if the player that want to do a move is the actual player or not
     * @return : the String of the nickName of the active player.
     */
    public String getActivePlayer(){
        synchronized (phaseBlocker) {
            return this.players.get(activePlayer);
        }
    }

    /**
     * Getter of the current phase, to know if the move is allowed in this phase
     * @return : return the actual phase
     */
    public PhaseEnum getCurrentPhase(){
        synchronized (phaseBlocker) {
            return this.currentPhase;
        }
    }

    /**
     * Moves the student to the diningRoom
     * @param color : Color of the student to move
     * @return : true if the student is correctly moved, otherwise false
     */
    public boolean moveStudent(Color color){
        synchronized (actionBlocker) {
            try {
                // Try to remove the student, if is removed correctly add the student to the DiningR.
                if (gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                    if (!gameBoard.addStudent(StudentCounter.DININGROOM, color)) {
                        // if it's not possible to add the student to the diningRoom then add back the student into the schoolEntrance
                        gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, color);
                        return false;
                    }
                    return true;
                }
                return false;
            } catch (Exception exc) {
                exc.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Moves te student to the Island
     * @param color : Color of the student to move from the SchoolEntrance to the Island
     * @param island : Number of the Island we want to move the student into
     * @return true if the student was added correctly
     */
    public boolean moveStudent(Color color,int island) throws IndexOutOfBoundsException{
        synchronized (actionBlocker) {
            try {
                if (gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                    if (!gameBoard.addStudent(StudentCounter.ISLAND, color, island)) {
                        gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, color);
                        return true;
                    }
                }
                return false;
            } catch (Exception exc) {
                exc.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Updates to the successive player of the planning phase
     * @return : true if now the activePlayer has been updated, false if the phase is incorrect or if we have finished the passive phase
     */
    public boolean updateNextPlanningPlayer(){
        synchronized (phaseBlocker) {
            if (currentPhase != PhaseEnum.PLANNING)
                return false;
            if (activePlayer >= players.size() - 1)
                return false;
            activePlayer++;
            return true;
        }
    }

    /**
     * Updates to the successive player of the action phase
     * @return : true if now the activePlayer has been updated, false if the phase is incorrect or if we have finished the passive phase
     */
    public boolean updateNextActivePlayer(){
        synchronized (phaseBlocker) {
            if (currentPhase == PhaseEnum.PLANNING) {
                if (activePlayer >= players.size() - 1)
                    return false;
                activePlayer++;
                return true;
            }
            return false;
        }
    }

    /**
     * Sets the array of the planning phase order, using the original List of players for the clockwise order and the last used cards
     */
    private void setPlanningOrder(){

    }

    /**
     * Sets the array of the action phase order, using the original List of players for the clockwise order and the last used cards
     */
    private void setActionOrder(){

    }

}
