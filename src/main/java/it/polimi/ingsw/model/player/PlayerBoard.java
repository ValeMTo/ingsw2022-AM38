package it.polimi.ingsw.model.player;

import it.polimi.ingsw.mvc.Listenable;
import it.polimi.ingsw.mvc.Listener;
import it.polimi.ingsw.exceptions.AlreadyUsedException;
import it.polimi.ingsw.exceptions.NotLastCardUsedException;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.server.ClientHandler;

import java.util.*;

public class PlayerBoard extends Listenable {
    private Listener modelListener;
    private List<ClientHandler> clients = null;
    private final String nickName;
    private final Tower towerColor;
    private final int numTowersLimit;
    private final SchoolBoard schoolBoard;
    private final List<AssistantCard> deck;
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
     * @param numTowers  number of towers of the player
     */
    public PlayerBoard(String nickName, Tower towerColor, int numTowers){
        this.nickName = nickName;
        if (numTowers == 8) {
            this.schoolBoard = new SchoolBoard(7, 10);
        } else {
            this.schoolBoard = new SchoolBoard(9, 10);
        }
        this.deck = new ArrayList<AssistantCard>();
        int numCard;
        int numSteps;
        for (int i = 0; i < 10; i++) {
            numCard = i + 1;
            numSteps = numCard / 2;
            if ((i+1) % 2 != 0) {
                numSteps += 1;
            }
            this.deck.add(new AssistantCard(numCard, numSteps));
        }
        this.towers = numTowers;
        this.numTowersLimit = numTowers;
        this.towerColor = towerColor;
        this.coin = 10;
    }

    /**
     * Sets the listener and clients for the update and notify for changes
     * @param modelListener : the modelListener
     * @param clients : the clients to notify
     */
    public void setListenerAndClients(Listener modelListener, List<ClientHandler> clients){
        this.modelListener = modelListener;
        this.clients = new ArrayList<>();
        if(clients!=null)
            this.clients.addAll(clients);
        notifyUsablesCards();
        notifyPlayerBoard();
    }

    private void notifyPlayerBoard(){
        if(this.clients!=null && this.modelListener!=null) {
            Map<Color, Integer> diningRoom = new HashMap<>();
            Map<Color, Integer> schoolEntrance = new HashMap<>();
            diningRoom.putAll(this.schoolBoard.getDiningRoomOccupancy());
            schoolEntrance.putAll(this.schoolBoard.getSchoolEntranceOccupancy());
            notify(modelListener,MessageGenerator.schoolBoardViewUpdate(diningRoom,schoolEntrance,this.towerColor,this.towers,this.coin),clients);
        }
    }

    /**
     * Notify to the player that played the card the change of its usable cards and the last card used
     */
    private void notifyUsablesCards(){
        if(this.clients!=null && this.modelListener!=null){
            System.out.println("PLAYER BOARD - notify the usable cards of player with tower "+this.towerColor);
            ArrayList<Integer> listOfUsableCards = new ArrayList<>();
            for(AssistantCard card:deck)
            {
                if(!card.isUsed())
                    listOfUsableCards.add(card.getPriority());
            }
            List<ClientHandler> onlyTheOneClient = new ArrayList<>();
            for(ClientHandler client:clients) {
                // Sends the usable cards only to the client with the same name of this playerBoard
                if (client.getNickName().equalsIgnoreCase(this.nickName)) {
                    onlyTheOneClient.add(client);
                    notify(modelListener, MessageGenerator.assistantCardUpdateMessage(this.towerColor, listOfUsableCards), onlyTheOneClient);

                }
            }
        }
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
        if (numTowers == 8) {
            this.schoolBoard = new SchoolBoard(7, 10);
        } else {
            this.schoolBoard = new SchoolBoard(9, 10);
        }
        this.deck = new ArrayList<>();
        int numCard;
        int numSteps;
        for (int i = 0; i < 10; i++) {
            numCard = i + 1;
            numSteps = numCard / 2;
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
     * Sends the last played card
     */
    private void notifyLastCard(){
        if(this.clients!=null && this.modelListener!=null){
            notify(modelListener,MessageGenerator.lastUsedAssistantCardUpdateMessage(this.towerColor,this.lastUsed),clients);
        }
    }

    /**
     * Returns the nickname of the player
     *
     * @return the String of the nickname of the player
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Makes the assistant card at the position number passed unusable.
     * If the card was already use return false, otherwise true.
     *
     * @param position the number of card that has to be used
     * @return the output of the legacy of the action
     */
    public void useAssistantCard(int position) throws IndexOutOfBoundsException, AlreadyUsedException {
        if (position < 1 || position > deck.size())
            throw new IndexOutOfBoundsException("AssistantCardValues are from " + 1 + " to " + 12);
        if (!deck.get(position - 1).isUsed()) {
            deck.get(position - 1).use();
            this.lastUsed = position;
        } else {
            Set<Integer> usableCards = new HashSet<>();
            for (AssistantCard card : deck)
                if (!card.isUsed()) usableCards.add(card.getPriority());
            throw new AlreadyUsedException(usableCards);
        }
        notifyUsablesCards();
        notifyLastCard();
    }

    /**
     * Returns the number of the last assistant card used by the player.
     */
    public int getLastCard() throws NotLastCardUsedException {
        if (lastUsed == null) {
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
     * Automatically increase its coins if
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */
    public boolean addStudentDiningRoom(Color studentColor) {
        boolean addResult = false;
        addResult = schoolBoard.addStudentDiningRoom(studentColor);
        if (addResult && countStudentsDiningRoom(studentColor) % 3 == 0 && countStudentsDiningRoom(studentColor) != 0)
            increaseCoinBudget();
        notifyPlayerBoard();
        return addResult;

    }

    /**
     * Adds a student in the Entrance.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */
    public boolean addStudentEntrance(Color studentColor) {
        boolean returnValue = schoolBoard.addStudentEntrance(studentColor);
        if(returnValue)
            notifyPlayerBoard();
        return returnValue;
    }

    /**
     * Removes a student from the Dining Room.
     * Returns true if the student was present, otherwise false.
     *
     * @param studentColor is the student to remove from the room.
     * @return outcome of the removal
     */
    public boolean removeStudentDiningRoom(Color studentColor) {
        boolean returnValue =schoolBoard.removeStudentDiningRoom(studentColor);
        if(returnValue)
            notifyPlayerBoard();
        return returnValue;
    }

    /**
     * Removes a student from the Entrance.
     * Returns true if the student was present, otherwise false.
     *
     * @param studentColor is the student to remove from the room.
     * @return outcome of the removal
     */
    public boolean removeStudentEntrance(Color studentColor) {
        boolean returnValue = schoolBoard.removeStudentEntrance(studentColor);
        if(returnValue)
            notifyPlayerBoard();
        return returnValue;

    }

    /**
     * Counts all students in Dining room
     *
     * @return number of all students in dining room
     */
    public int countStudentsDiningRoom() {
        return schoolBoard.countStudentsDiningRoom();
    }

    /**
     * Counts all students in entrance
     *
     * @return number of all students in the entrance
     */
    public int countStudentsEntrance() {
        return schoolBoard.countStudentsEntrance();
    }

    /**
     * Counts students of certain color in dining room
     *
     * @return number students of certain color in dining room
     */
    public int countStudentsDiningRoom(Color color) {
        return schoolBoard.countStudentsDiningRoom(color);
    }

    /**
     * Counts students of certain color in entrance
     *
     * @return number students of certain color in entrance
     */
    public int countStudentsEntrance(Color color) {
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
        if (this.towers + numTower < numTowersLimit && numTower > 0) {
            this.towers += numTower;
            notifyPlayerBoard();
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
            notifyPlayerBoard();
            System.out.println("PLAYER BOARD of "+towerColor+ " - removeTower - actual num of towers "+this.towers+" num of tower after have removed "+numTower);
            return true;
        }
        else
            this.towers = 0;
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


    /**
     * Returns the available Assistant cards in a map with the Priority as Key and Steps as Value
     *
     * @return : a Map with the priority and steps of the assistant card if the card has not been used
     */
    public Map<Integer, Integer> getAvailableCards() {
        Map<Integer, Integer> availableCardsMap = new <Integer, Integer>HashMap();
        for (AssistantCard card : deck)
            if (!card.isUsed()) availableCardsMap.put(card.getPriority(), card.getSteps());
        return availableCardsMap;
    }

    /**
     * Returns the last card steps
     *
     * @return : the steps of the last card used
     * @throws NotLastCardUsedException : if the Last card does not exist since there is no card already used
     */
    public int getLastCardSteps() throws NotLastCardUsedException {
        for (AssistantCard card : deck)
            if (card.isUsed()&&lastUsed!=null&&lastUsed==card.getPriority())
                return card.getSteps();
        throw new NotLastCardUsedException("No card used! Steps cannot be determined");

    }
}
