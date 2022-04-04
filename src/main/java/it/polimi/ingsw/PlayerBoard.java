package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class PlayerBoard {
    private final String nickName;
    private SchoolBoard schoolBoard;
    private List<AssistantCard> deck;
    private int lastUsed;
    private int towers;
    private final Tower towerColor;
    private final int numTowersLimit;
    private int coin;

    /**
     * Constructor of PlayerBoard class.
     * It is used for the easy mode game.
     * The player's nickName and towerColor are assigned.
     * Then, the schoolBoard and the AssistantCard deck is created.
     * There are no coin.
     *
     * @param nickName name of the player
     * @param towerColor color of the player's tower in the game
     */
    public PlayerBoard(String nickName, Tower towerColor, int numTowers){
        this.nickName = nickName;
        this.schoolBoard = new SchoolBoard();
        this.deck = new ArrayList<AssistantCard>();
        for(int i=0; i<10; i++){
            int numCard = i+1;
            int numPriority = numCard;
            if( i%2 != 0 ){
                numPriority += 1;
            }
            this.deck.add(new AssistantCard(numCard, numPriority));
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
     * @param nickName name of the player
     * @param towerColor color of the player's tower in the game
     * @param coin number of coins that are assigned to a player,it should be always two in the expert mode game.
     */
    public PlayerBoard(String nickName, Tower towerColor, int numTowers, int coin){
        this.nickName = nickName;
        this.schoolBoard = new SchoolBoard();
        this.deck = new ArrayList<>();
        for(int i=0; i<10; i++){
            int numCard = i+1;
            int numPriority = numCard;
            if( i%2 != 0 ){
                numPriority += 1;
            }
            this.deck.add(new AssistantCard(numCard, numPriority));
        }
        this.towers = numTowers;
        this.numTowersLimit = numTowers;
        this.towerColor = towerColor;
        this.coin = coin;
    }

    /**
     * Make the assistant card at the position number passed unusable.
     * If the card was already use return false, otherwise true.
     *
     * @param position the number of card that has to be used
     * @return the output of the legacy of the action
     */
    public boolean useAssistantCard(int position ){
        if(!deck.get(position).isUsed()){
            deck.get(position).use();
            this.lastUsed = position;
            return true;
        }
        return false;
    }

    /**
     * Return the number of the last assistant card used by the player.
     */
    public int getLastCard(){
        return lastUsed;
    }

    public ArrayList<Integer> showUsableCards(){
        ArrayList<Integer> usableCards = new ArrayList<>();
        for(int i=0; i<10; i++){
            if(!deck.get(i+1).isUsed){
                usableCards.add(i+1);
            }
        }
        return usableCards;
    }

    /**
     * Decrease the coin reserve of a certain amount.
     * If the reserve is not enough returns false, otherwise returns true.
     *
     * @param coin amount of coin to decrease
     * @return the outcome of the action. True if coin are decreised, otherwise false.
     */
    public boolean pay(int coin){
        if (this.coin > coin) {
            this.coin -= coin;
            return true;
        }
        return false;
    }

    /**
     * Increase of one the amount of coins
     */
    public void increaseCoinBudget(){
        this.coin+=1;
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
    public boolean addStudentDiningRoom(Color studentColor){
        return schoolBoard.addStudentDiningRoom(studentColor);

    }

    /**
     * Adds a student in the Entrance.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */
    public boolean addStudentEntrance(Color studentColor){
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
     * Increase of the parameter the amount of towers.
     * Returns true if the final number of towers is not above the limit, otherwise false.
     *
     * @param numTower number of towers to increase
     * @return the outcome of the action
     */
    public boolean addTower(int numTower){
        if (this.towers + numTower <= numTowersLimit) {
            this.towers += numTower;
            return true;
        }
        return false;
    }

    /**
     * Decrease of the parameter the amount of towers.
     * Returns true if the final number of towers is above or equal zero, otherwise false.
     *
     * @param numTower number of towers to decrease
     * @return the outcome of the action
     */
    public boolean removeTower(int numTower){
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
    public int getAvailableTowers(){
        return towers;
    }



}
