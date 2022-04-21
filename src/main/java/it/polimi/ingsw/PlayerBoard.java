package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class PlayerBoard {
    private final String nickName;
    private final Tower towerColor;
    private final int numTowersLimit;
    private final SchoolBoard schoolBoard;
    private List<AssistantCard> deck;
    private Integer lastUsed;
    private int towers;
    private int coin;

    /**
     * Constructor of PlayerBoard class.
     * It is used for the easy mode game.
     * The player's nickName and towerColor are assigned.
     * Then, the schoolBoard and the AssistantCard deck is created.
     * There are no coin.
     *
     * @param nickName   name of the player
     * @param towerColor color of the player's tower in the game
     * @param numTowers number of towers of the player
     */
    public PlayerBoard(String nickName, Tower towerColor, int numTowers) {
        this.nickName = nickName;
        if( numTowers == 8){
            this.schoolBoard = new SchoolBoard(7 ,10);
        }else {
            this.schoolBoard = new SchoolBoard( 9,10);
        }
        this.deck = new ArrayList<AssistantCard>();
        int numCard;
        int numSteps;
        for (int i =0; i < 10; i++) {
            numCard = i + 1;
            numSteps = numCard/2;
            if (i % 2 != 0) {
                numSteps += 1;
            }
            this.deck.add(new AssistantCard(numCard, numSteps));
        }
        this.towers = numTowers;
        this.numTowersLimit = numTowers;
        this.towerColor = towerColor;
        this.coin = 0;
    }

    /**
     * Constructor of PlayerBoard class.
     * It is used for the easy mode game.
     * The player's nickName and towerColor are assigned.
     * Then, the schoolBoard and the AssistantCard deck is created.
     * There are no coin.
     *
     * @param nickName   name of the player
     * @param towerColor color of the player's tower in the game
     * @param coin       number of coins that are assigned to a player,it should be always two in the expert mode game.
     */
    public PlayerBoard(String nickName, Tower towerColor, int numTowers, int coin) {
        this.nickName = nickName;
        if( numTowers == 8){
            this.schoolBoard = new SchoolBoard(7 ,10);
        }else{
            this.schoolBoard = new SchoolBoard( 9,10);
        }
        this.deck = new ArrayList<>();
        int numCard;
        int numSteps;
        for (int i =0; i < 10; i++) {
            numCard = i + 1;
            numSteps = numCard/2;
            if (i % 2 != 0) {
                numSteps += 1;
            }
            this.deck.add(new AssistantCard(numCard, numSteps));
        }
        this.towers = numTowers;
        this.numTowersLimit = numTowers;
        this.towerColor = towerColor;
        this.coin = coin;
    }

    /**
     * Makes the assistant card at the position number passed unusable.
     * If the card was already use return false, otherwise true.
     *
     * @param position the number of card that has to be used
     * @return the output of the legacy of the action
     */
    public boolean useAssistantCard(int position) {
        if (!deck.get(position-1).isUsed()) {
            deck.get(position-1).use();
            this.lastUsed = position;
            return true;
        }
        return false;
    }

    /**
     * Returns the number of the last assistant card used by the player.
     */
    public int getLastCard() throws NotLastCardUsedException{
        if(lastUsed == null) {
            throw new NotLastCardUsedException("It is the first round. No card can be take before");
        }
        return lastUsed;
    }

    public ArrayList<Integer> showUsableCards() {
        ArrayList<Integer> usableCards = new ArrayList<Integer>();
        for (int i = 0; i < deck.size(); i++) {
            if (!deck.get(i).isUsed()) {
                usableCards.add(deck.get(i).getPriority());
            }
        }
        return usableCards;
    }

    /**
     * Decreases the coin reserve of a certain amount.
     * If the reserve is not enough returns false, otherwise returns true.
     *
     * @param coin amount of coin to decrease
     * @return the outcome of the action. True if coin are decreised, otherwise false.
     */
    public boolean pay(int coin) {
        if (this.coin >= coin) {
            this.coin -= coin;
            return true;
        }
        return false;
    }

    /**
     * Increases of one the amount of coins
     */
    public void increaseCoinBudget() {
        this.coin += 1;
    }

    /**
     * Coin getter
     */
    public int getCoin() {
        return coin;
    }

    /**
     * Returns the player tower color.
     *
     * @return color of the tower assigned to the player
     */
    public Tower getTowerColor() {
        return towerColor;
    }

    /**
     * Adds a student in the DiningRoom.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */
    public boolean addStudentDiningRoom(Color studentColor) {
        return schoolBoard.addStudentDiningRoom(studentColor);

    }

    /**
     * Adds a student in the Entrance.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */
    public boolean addStudentEntrance(Color studentColor) {
        return schoolBoard.addStudentEntrance(studentColor);

    }

    /**
     * Removes a student from the Dining Room.
     * Returns true if the student was present, otherwise false.
     *
     * @param studentColor is the student to remove from the room.
     * @return outcome of the removal
     */
    public boolean removeStudentDiningRoom(Color studentColor) {
        return schoolBoard.removeStudentDiningRoom(studentColor);
    }

    /**
     * Removes a student from the Entrance.
     * Returns true if the student was present, otherwise false.
     *
     * @param studentColor is the student to remove from the room.
     * @return outcome of the removal
     */
    public boolean removeStudentEntrance(Color studentColor) {
        return schoolBoard.removeStudentEntrance(studentColor);
    }

    /**
     * Counts all students in Dining room
     *
     * @return number of all students in dining room
     */
    public int countStudentsDiningRoom(){
        return schoolBoard.countStudentsDiningRoom();
    }

    /**
     * Counts all students in entrance
     *
     * @return number of all students in the entrance
     */
    public int countStudentsEntrance(){
        return schoolBoard.countStudentsEntrance();
    }

    /**
     * Counts students of certain color in dining room
     *
     * @return number students of certain color in dining room
     */
    public int countStudentsDiningRoom(Color color){
        return schoolBoard.countStudentsDiningRoom(color);
    }

    /**
     * Counts students of certain color in entrance
     *
     * @return number students of certain color in entrance
     */
    public int countStudentsEntrance(Color color){
        return schoolBoard.countStudentsEntrance(color);
    }

    /**
     * Increases of the parameter the amount of towers.
     * Returns true if the final number of towers is not above the limit, otherwise false.
     *
     * @param numTower number of towers to increase
     * @return the outcome of the action
     */
    public boolean addTower(int numTower) {
        if (this.towers + numTower < numTowersLimit && numTower>0){
            this.towers += numTower;
            return true;
        }
        return false;
    }

    /**
     * Decreases the amount of towers.
     * Returns true if the final number of towers is above or equal zero, otherwise false.
     *
     * @param numTower number of towers to decrease
     * @return the outcome of the action
     */
    public boolean removeTower(int numTower) {
        if (this.towers - numTower >= 0) {
            this.towers -= numTower;
            return true;
        }
        return false;
    }

    /**
     * Returns the number of unsettled towers
     *
     * @return number of available towers
     */
    public int getAvailableTowers() {
        return towers;
    }
}