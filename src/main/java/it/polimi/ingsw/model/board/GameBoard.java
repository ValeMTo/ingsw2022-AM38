package it.polimi.ingsw.model.board;

import it.polimi.ingsw.mvc.Listenable;
import it.polimi.ingsw.mvc.Listener;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.player.PlayerBoard;
import it.polimi.ingsw.model.specialCards.SpecialCard;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import it.polimi.ingsw.server.ClientHandler;

import java.util.*;

public abstract class GameBoard extends Listenable {
    protected Listener modelListener;
    protected List<ClientHandler> clients = null;
    protected final int initialIslandNumber = 12;
    protected int playerNumber;
    protected int currentPlayer; // The position in the array which has the active player.
    protected Map<Color, Tower> professors;
    protected PlayerBoard[] players;

    protected Bag bag;
    protected Island[] islands = new Island[initialIslandNumber];
    protected Cloud[] clouds;
    protected int numRound;
    protected int motherNature;
    protected Map<String, Integer> standing = new HashMap<>();


    public GameBoard(int playerNumber, List<String> playersNicknames) {

        this.playerNumber = playerNumber;
        currentPlayer = 0;
        professors = new HashMap<Color, Tower>();
        for (Color color : Color.values())
            professors.put(color, null);
        players = new PlayerBoard[playerNumber];
        int numTowersPerPlayer;
        int cloudStudentsLimit;
        if (playerNumber == 3) {
            numTowersPerPlayer = 6;
            cloudStudentsLimit = 4;
        } else {
            numTowersPerPlayer = 8;
            cloudStudentsLimit = 3;
        }
        players[0] = new PlayerBoard(playersNicknames.get(0), Tower.WHITE, numTowersPerPlayer);
        players[1] = new PlayerBoard(playersNicknames.get(1), Tower.BLACK, numTowersPerPlayer);
        if (playerNumber >= 3) players[2] = new PlayerBoard(playersNicknames.get(2), Tower.GRAY, numTowersPerPlayer);
        bag = new Bag();
        for (int i = 0; i < initialIslandNumber; i++)
            islands[i] = new Island(i + 1);
        clouds = new Cloud[playerNumber];
        for (int i = 0; i < playerNumber; i++)
            clouds[i] = new Cloud(cloudStudentsLimit);
        numRound = 1;
        motherNature = 1;
    }

    private void notifyClouds(){
        if(this.clients!=null && this.modelListener!=null){
            for(int i=0;i<clouds.length;i++)
                notify(modelListener,MessageGenerator.cloudViewUpdateMessage(i+1,clouds[i].getLimit(),clouds[i].getStudents()),clients);
        }
    }

    public void notifyArchipelago(){
        System.out.println("GAME BOARD - notifyArchipelago - notify the change on the archipelago "+MessageGenerator.archipelagoViewUpdateMessage(islands.length,motherNature));
        if(clients!=null && modelListener!=null)
            notify(modelListener,MessageGenerator.archipelagoViewUpdateMessage(islands.length,motherNature),clients);
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
        if(this.clients!=null && this.modelListener!=null){
            for(Island island:islands)
            {
                island.setListenerAndClients(modelListener,clients);
            }
            for(PlayerBoard playerBoard: players){
                playerBoard.setListenerAndClients(modelListener,clients);
            }
            System.out.println("GAME BOARD - notify the archipelago");
            notify(modelListener,MessageGenerator.archipelagoViewUpdateMessage(islands.length,motherNature),clients);
            System.out.println("GAME BOARD - notify the clouds");
            for(int i=0;i<clouds.length;i++)
            notify(modelListener,MessageGenerator.cloudViewUpdateMessage(i+1,clouds[i].getLimit(),clouds[i].getStudents()),clients);
            for(Island island:islands)
                island.setListenerAndClients(modelListener,clients);
        }
    }

    /**
     * Transfer all the students and tower from an Island to another. This effect is needed to create or increase a group of Islands.
     * A group of Island is simply an Island with a tower number greater than 1. The towerNumber parameter corresponds to the number of Islands involved in the Group
     *
     * @param islandReceiver : The Island that will receive the students and the tower(s) and maintain the index as island group
     * @param islandGiver    : The Island that give the students and the tower(s)
     * @return : true if the transfer is done, false if the transfer is not done so the two islands are not grouped into one
     */
    protected boolean transferIslandPossessionsIfSameTower(Island islandReceiver, Island islandGiver) {
        if (islandGiver == null || islandReceiver == null) return false;
        if (islandGiver.getTower() != null && islandReceiver.getTower() != null && islandReceiver.getTower().equals(islandGiver.getTower())) {
            System.out.println("GAME BOARD - transferIslandPossessionsIfSameTower - from island "+islandGiver.getPosition()+" to "+islandReceiver.getPosition());
            int towersNumber = islandReceiver.getTowerNumber() + islandGiver.getTowerNumber();
            // We update the number of tower on the island adding the Towers of the island merging
            islandReceiver.setTowerNumber(towersNumber);
            // We transfer all the student to the island that have the group Island's properties
            islandReceiver.addStudent(islandGiver.getStudentMap());
            if(islandGiver.getPosition()==motherNature) {
                this.motherNature = islandReceiver.getPosition();
                System.out.println("GAME BOARD - transferIslandPossessionsIfSameTower - mother nature transferred from"+islandGiver.getPosition()+" to "+islandReceiver.getPosition());
            }
            notifyArchipelago();
            return true;
        }
        return false;
    }

    /**
     * Groups the adjacent islands to the Island with the given position. Transfer everything to the island with the min position
     * then create a new array without the islands merged into the island with min position that substitute the islands array
     *
     * @param island : is the position of the island we want to verify the adjacent islands for grouping them if needed
     */
    protected void groupIslands(int island) {
        Tower islandTower = islands[island - 1].getTower();
        Island currentIsland = islands[island - 1];
        Island islandGiver = null, islandReceiver = null;
        List<Island> islandToRemove = new ArrayList<Island>();
        // Search the island after the island considered, set the island with min position as the receiver
        if (island < islands.length - 1) {
            islandReceiver = currentIsland;
            islandGiver = islands[island];
        } else if (island == islands.length - 1 && islands.length >= 2) {
            islandReceiver = islands[0];
            islandGiver = currentIsland;
        }
        // Update the island with min position, transferring all the possessed objects to that island if they have the same Tower color
        // (now more than one tower, indicates it is a group of islands)
        if (transferIslandPossessionsIfSameTower(islandReceiver, islandGiver)) islandToRemove.add(islandGiver);

        // Do the same thing with the island before the island considered
        if (island > 1) {
            islandReceiver = islands[island - 2];
            islandGiver = currentIsland;
        } else if (island == 1 && islands.length >= 2) {
            islandGiver = islands[islands.length - 1];
            islandReceiver = currentIsland;
        }
        // Compute the grouping with the island and the island before...
        if (transferIslandPossessionsIfSameTower(islandReceiver, islandGiver)) islandToRemove.add(islandGiver);
        // Now the grouping should have been correctly done. If we have islands to remove we now remove them from the array of Islands.
        Island[] newIslandsArray = new Island[islands.length - islandToRemove.size()];
        int counter = 0;
        for (int i = 0; i < islands.length; i++) {
            //Adds to the new array the islands which are not selected to be removed
            if (!islandToRemove.contains(islands[i])) {
                newIslandsArray[counter] = islands[i];
                counter++;
            }
        }
        islands = newIslandsArray;
        //Reset the position value for each island, after the array change
        for (int i = 1; i <= islands.length; i++) {
            islands[i - 1].setPosition(i);
        }
        notifyArchipelago();
        for (int i = 0; i < islands.length; i++) {
            islands[i].notifySomethingHasChanged();
        }
    }

    /**
     * Updates the status of the leaderBoard, computing the standings
     */
    private void leaderBoardUpdate(){
        Integer minTowerLeft = null;
        List<String> minTowerOwner = new ArrayList<>();
        int counter = 1, standingPosition = 1;
        while(counter <=playerNumber){
            minTowerLeft = null;
            minTowerOwner.clear();
            for(int i=0;i<playerNumber;i++)
            {
                if(minTowerLeft==null) {
                    minTowerLeft = players[i].getAvailableTowers();
                    minTowerOwner.add(players[i].getNickName());
                }
                else if(minTowerLeft>players[i].getAvailableTowers()) {
                    minTowerOwner.clear();
                    minTowerLeft = players[i].getAvailableTowers();
                    minTowerOwner.add(players[i].getNickName());
                } else if (minTowerLeft.equals(players[i].getAvailableTowers())) {
                    minTowerOwner.add(players[i].getNickName());
                }
            }
            for(String player : minTowerOwner) {
                this.standing.put(player, standingPosition);
                counter++;
            }
            standingPosition++;
        }
        if(clients!=null&&modelListener!=null&&standing!=null)
            notify(modelListener,MessageGenerator.leaderboardUpdateMessage(false,standing,""),clients);
    }

    /**
     * Verifies the conditions that bring to the end of the match
     *
     * @return NoEndOfMatch if the match ending conditions are not verified,
     * InstantEndOfMatch if the match end conditions are verified and the match end immediately
     * DelayedEndOfMatch if the match does end after the round is completed
     */
    public EndOfMatchCondition isEndOfMatch() {
        // Change the leaderBoard status
        leaderBoardUpdate();
        // Empty bag condition
        if (bag.isEmpty()) {
            System.out.println("GAME BOARD - isEndOfMatch - BAG IS EMPTY!");
            return EndOfMatchCondition.DelayedEndOfMatch;
        }
        // Player has used all his towers
        for (PlayerBoard player : players) {
            if (player.getAvailableTowers() == 0) {
                System.out.println("GAME BOARD - isEndOfMatch - A PLAYER HAS FINISHED ITS TOWERS! Player "+player.getNickName()+" with tower "+player.getTowerColor());
                return EndOfMatchCondition.InstantEndOfMatch;
            }
        }
        // No more assistant card (all 10 cards used)
        if (numRound == 11) {
            System.out.println("GAME BOARD - isEndOfMatch - ROUND FINISHED!");
            return EndOfMatchCondition.InstantEndOfMatch;
        }
        // Final round
        if (numRound == 10) {
            System.out.println("GAME BOARD - isEndOfMatch - LAST ROUND!");
            return EndOfMatchCondition.DelayedEndOfMatch;
        }
        for (PlayerBoard player : players) {
            if (player.getAvailableCards().size() == 0) {
                System.out.println("GAME BOARD - isEndOfMatch - ALL CARD USED!");
                return EndOfMatchCondition.DelayedEndOfMatch;
            }
        }
        // 3 Islands groups
        if (islands.length<=3) {
            System.out.println("GAME BOARD - isEndOfMatch - ONLY THREE ISLAND GROUPS!");
            return EndOfMatchCondition.InstantEndOfMatch;
        }
        return EndOfMatchCondition.NoEndOfMatch;
    }

    /**
     * Notifies with the leaderBoard update the end of match, it is executed when the round ends,
     * so it is effectively an end of match
     */
    public void notifyEndOfMatchLeaderBoard(){
        // Change the leaderBoard status
        leaderBoardUpdate();
        // Empty bag condition
        String message="";
        if (bag.isEmpty()) {
            message = " bags pawn ended ";
        }
        // Player has used all his towers
        for (PlayerBoard player : players) {
            if (player.getAvailableTowers() == 0) message+= " the player "+player.getNickName()+" has used all the towers ";
        }
        // No more assistant card (all 10 cards used)
        if (numRound >= 10) {
            message = "assistant card ended";
        }
        // 3 Islands groups
        if (islands[islands.length - 1].getPosition() < initialIslandNumber - 3) {
            message += " only three island group left ";
        }
        if(clients!=null&&modelListener!=null&&standing!=null)
            notify(modelListener,MessageGenerator.leaderboardUpdateMessage(true,standing,message),clients);
    }

    /**
     * Moves the motherNature to the destination Island if it is a possible movement
     *
     * @param destinationIsland : Position of the island where the motherNature should be moved
     * @return : true if the movement is allowed, false if motherNature cannot be moved in that island
     * @throws IslandOutOfBoundException if the island position is not contained in any of the current Islands/Islands groups
     */


    /**
     * Moves motherNature if the movement is allowed (the last played card has steps > distance to cover)
     *
     * @param destinationIsland : Island where we want to move motherNature
     * @return : true if the movement was allowed and motherNature has been moved, false if the destinationIsland is unreachable with the steps of the last card
     * @throws IslandOutOfBoundException : if in the destinationIsland's position there is not an island
     */
    public boolean moveMotherNature(int destinationIsland) throws IslandOutOfBoundException {
        int cardSteps;
        try {
            cardSteps = players[currentPlayer].getLastCardSteps();
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }
        if (destinationIsland < 1 || destinationIsland > islands[islands.length - 1].getPosition())
            throw new IslandOutOfBoundException(1, islands.length);

        //TODO: fix problem 1 not reachable... also motherNature MUST move from its position!
        if ((motherNature < destinationIsland && destinationIsland - motherNature <= cardSteps) || (motherNature > destinationIsland && islands.length - motherNature + destinationIsland <= cardSteps)) {
            motherNature = destinationIsland;
            System.out.println("GAME BOARD - moveMotherNature - motherNature correctly moved to "+destinationIsland+" notifying the clients");
            notifyArchipelago();
            return true;
        }
        return false;
    }

    /**
     * Fills the clouds with the students from the bag. If the bag is empty, then fills only the clouds until the students end.
     */
    public void fillClouds() {
        for (Cloud cloud : clouds) {
            while (!cloud.isFull() && !bag.isEmpty()) cloud.addStudent(bag.drawStudent());
            cloud.setHasBeenUsed(false);
        }
        notifyClouds();
    }

    /**
     * Adds a student to a particular location
     *
     * @param location : where the student will end
     * @param student  : student's color
     * @return the result of the add (true if correctly added, false if not)
     * @throws LocationNotAllowedException : if island
     */
    public boolean addStudent(StudentCounter location, Color student) throws LocationNotAllowedException, FunctionNotImplementedException {
        switch (location) {
            case BAG:
                return bag.addStudent(student);
            case DININGROOM:
                boolean retValue = players[currentPlayer].addStudentDiningRoom(student);
                updateProfessorOwnership();
                return retValue;
            case SCHOOLENTRANCE:
                return players[currentPlayer].addStudentEntrance(student);
            default:
                throw new LocationNotAllowedException("Islands, clouds and special cards are not allowed since this add method does not have a position value");
                //CARD case should be done overriding and then calling super...
        }
    }

    /**
     * Adds a student to a particular location and also specify the location (position of the island, nr. of cloud, nr. of player)
     *
     * @param location : where the student will end
     * @param student  : student's color
     * @param position : island position or nr. of player or nr. of island (for island 1-12)
     * @return : the result of the add (true if correctly added, false if not)
     */
    public boolean addStudent(StudentCounter location, Color student, int position) throws LocationNotAllowedException, FunctionNotImplementedException, IndexOutOfBoundsException {
        switch (location) {
            case BAG:
                return bag.addStudent(student);
            case DININGROOM:
                if (position >= 0 && position < playerNumber) {
                    boolean retValue = players[position].addStudentDiningRoom(student);
                    updateProfessorOwnership();
                    return retValue;
                }
                throw new IndexOutOfBoundsException("Player Position is from " + 0 + " to " + (players.length - 1));

            case SCHOOLENTRANCE:

                if (position >= 0 && position < playerNumber) return players[position].addStudentEntrance(student);
                throw new IndexOutOfBoundsException("Player Position is from " + 0 + " to " + (players.length - 1));
            case ISLAND:
                if (position <= islands.length && position > 0) return islands[position - 1].addStudent(student);
                throw new IndexOutOfBoundsException("Islands Position is from " + islands[0].getPosition() + " to " + islands[islands.length - 1].getPosition());
            case CLOUD:
                if (position <= clouds.length && position >= 1) return clouds[position - 1].addStudent(student);
                throw new IndexOutOfBoundsException("Cloud Position is from " + 1 + " to " + (clouds.length));
            case CARD:
                throw new FunctionNotImplementedException("Special card value is not allowed in addStudent since the add for special cards is for expert mode only");
            default:
                return false;
            //CARD case should be done overriding easy gameboard, catching the special card case and then calling super...
        }
    }

    /**
     * Removes a student to a particular location
     *
     * @param location : where the student will be removed
     * @param student  : student's color
     * @return the result of the remove (true if correctly removed, false if not)
     */
    public boolean removeStudent(StudentCounter location, Color student) throws LocationNotAllowedException, FunctionNotImplementedException {
        switch (location) {
            case DININGROOM:
                boolean retValue = players[currentPlayer].removeStudentDiningRoom(student);
                updateProfessorOwnership();
                return retValue;
            case SCHOOLENTRANCE:
                return players[currentPlayer].removeStudentEntrance(student);
            default:
                throw new LocationNotAllowedException("Islands, clouds and special cards are not allowed since this remove method does not have a position value");
        }
    }

    /**
     * Draws a student from the bag
     *
     * @return the student Color drawn, null if no students are into the bag
     */
    public Color drawFromBag() {
        return this.bag.drawStudent();
    }

    /**
     * Initializes the bag after the initial phase
     */
    public void initializeBag() {
        this.bag.initialise();
    }

    /**
     * Getter for the current player
     *
     * @return : the current player tower color
     */
    public Tower getCurrentPlayer() {
        return players[currentPlayer].getTowerColor();
    }

    /**
     * Getter for the current player
     *
     * @return : the current player tower color
     */
    public boolean setCurrentPlayer(Tower tower) throws NoSuchTowerException {
        for (PlayerBoard player : players)
            if (tower == player.getTowerColor()) {
                currentPlayer = this.getPlayerPosition(tower);
                return true;
            }
        throw new NoSuchTowerException("The tower " + tower + " cannot be found");

    }

    /**
     * Removes a student to a particular location and also specify the location (position of the island, nr. of cloud, nr. of player)
     *
     * @param location : where the student will be removed
     * @param student  : student's color
     * @param position : island position or nr. of player or nr. of island
     * @return : the result of the remove (true if correctly removed, false if not)
     */
    public boolean removeStudent(StudentCounter location, Color student, int position) throws LocationNotAllowedException, FunctionNotImplementedException, IndexOutOfBoundsException {
        switch (location) {
            case DININGROOM:
                if (position >= 0 && position < playerNumber) {
                    boolean retValue = players[position].removeStudentDiningRoom(student);
                    updateProfessorOwnership();
                    return retValue;
                }
                throw new IndexOutOfBoundsException("Player Position is from " + 0 + " to " + (players.length - 1));

            case SCHOOLENTRANCE:
                if (position >= 0 && position < playerNumber)
                    return players[position].removeStudentEntrance(student);
                throw new IndexOutOfBoundsException("Player Position is from " + 0 + " to " + (players.length - 1));

            case CLOUD:
                if (position <= clouds.length && position >= 1) return clouds[position - 1].removeStudent(student);
                throw new IndexOutOfBoundsException("Cloud Position is from " + 1 + " to " + (clouds.length));
            case CARD:
                throw new FunctionNotImplementedException("Special card value is not allowed in addStudent since the add for special cards is for expert mode only");
            default:
                throw new LocationNotAllowedException("removeStudent with position cannot be applied with island and bag since no remove is allowed in island and no remove from bag knowing the color to draw.");
        }
    }

    /**
     * Increases the Round nr.
     */
    public void increaseRound() {
        numRound++;
    }

    /**
     * Gets the round number
     *
     * @return : Round number
     */
    public int getNumRound() {
        return this.numRound;
    }

    /**
     * Uses the AssistantCard specified with numCard of the selected player.
     *
     * @param playerTower : tower color of the player we want to use the Card
     * @param numCard     : number of the card we want to use (=priority of the AssistantCard)
     * @return : true if the card is correctly used, false if it cannot be used
     */
    public boolean useAssistantCard(Tower playerTower, int numCard) throws IndexOutOfBoundsException, AlreadyUsedException {
        for (PlayerBoard player : players) {
            if (player.getTowerColor() == playerTower) {
                player.useAssistantCard(numCard);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns usable assistant cards
     *
     * @param playerTower : color tower of the player chosen
     * @return the hashmap of usable card with priority as key value
     * @throws NoSuchTowerException : that tower color is not applicable in this game
     */
    public Map<Integer, Integer> getUsableAssistantCard(Tower playerTower) throws NoSuchTowerException {
        if (playerTower == null) throw new NoSuchTowerException("Tower value is null");
        Map<Integer, Integer> list = new HashMap<Integer, Integer>();
        for (PlayerBoard player : players) {
            if (player.getTowerColor() == playerTower) return player.getAvailableCards();
        }
        throw new NoSuchTowerException("Tower value is not possessed by any player or incorrect");
    }

    /**
     * Gets the last assistantCard played by the specified player
     *
     * @param playerTower : the tower color of the player we are considering
     * @return : the number of the AssistantCard last used (its priority)
     * @throws NotLastCardUsedException : The player has not yet used a card, so there is no last card
     * @throws NoSuchTowerException     : the Tower's color specified is null, does not exist or is not possessed by any player
     */
    public int getLastAssistantCard(Tower playerTower) throws NotLastCardUsedException, NoSuchTowerException {
        if (playerTower == null) throw new NoSuchTowerException("Tower value is null");
        for (PlayerBoard player : players) {
            if (player.getTowerColor() == playerTower) return player.getLastCard();
        }
        throw new NoSuchTowerException("Tower value is not possessed by any player or incorrect");

    }


    /**
     * Returns the number of students of a given color possessed in the DiningRoom of a particular player
     *
     * @param playerTower : tower color of the player we consider
     * @param color       : color of the students we want to count in the DiningRoom
     * @return : the number of students of the color specified in the DiningRoom of the Player
     * @throws NoSuchTowerException : the Tower's color specified is null, does not exist or is not possessed by any player
     */
    public int getDiningRoomOccupation(Tower playerTower, Color color) throws NoSuchTowerException {
        if (playerTower == null) throw new NoSuchTowerException("Tower value is null");
        for (PlayerBoard player : players) {
            if (player.getTowerColor() == playerTower) return player.countStudentsDiningRoom(color);
        }
        throw new NoSuchTowerException("Tower value is not possessed by any player or incorrect");
    }

    /**
     * Returns the number of students in the DiningRoom of a particular player
     *
     * @param playerTower : tower color of the player we consider
     * @return : the number of students in the DiningRoom of the Player
     * @throws NoSuchTowerException : the Tower's color specified is null, does not exist or is not possessed by any player
     */
    public int getDiningRoomOccupation(Tower playerTower) throws NoSuchTowerException {
        if (playerTower == null) throw new NoSuchTowerException("Tower value is null");
        for (PlayerBoard player : players) {
            if (player.getTowerColor() == playerTower) return player.countStudentsDiningRoom();
        }
        throw new NoSuchTowerException("Tower value is not possessed by any player or incorrect");
    }

    /**
     * Returns the number of students of a given color in the SchoolBoard of a particular player
     *
     * @param playerTower : tower color of the player we consider
     * @param color       : color of the students we want to count in the DiningRoom
     * @return : the number of students in the SchoolBoard of the Player of that color
     * @throws NoSuchTowerException : the Tower's color specified is null, does not exist or is not possessed by any player
     */
    public int getSchoolEntranceOccupation(Tower playerTower, Color color) throws NoSuchTowerException {
        if (playerTower == null) throw new NoSuchTowerException("Tower value is null");
        for (PlayerBoard player : players) {
            if (player.getTowerColor() == playerTower) return player.countStudentsEntrance(color);
        }
        throw new NoSuchTowerException("Tower value is not possessed by any player or incorrect");
    }

    /**
     * Returns the number of students in the SchoolBoard of a particular player
     *
     * @param playerTower : tower color of the player we consider
     * @return : the number of students in the SchoolBoard of the Player of that color
     * @throws NoSuchTowerException : the Tower's color specified is null, does not exist or is not possessed by any player
     */
    public int getSchoolEntranceOccupation(Tower playerTower) throws NoSuchTowerException {
        if (playerTower == null) throw new NoSuchTowerException("Tower value is null");
        for (PlayerBoard player : players) {
            if (player.getTowerColor() == playerTower) return player.countStudentsEntrance();
        }
        throw new NoSuchTowerException("Tower value is not possessed by any player or incorrect");
    }

    public int getPlayerPosition(Tower towerColor) throws NoSuchTowerException {
        for (int i = 0; i < playerNumber; i++)
            if (players[i].getTowerColor() == towerColor) return i;
        throw new NoSuchTowerException(" no player with this tower");
    }

    /**
     * Updates the professor ownership in order to compute the influence. It considers the students in the PlayerBoard's DiningRooms to update the professors.
     */
    public void updateProfessorOwnership() {
        int maxStudent;
        boolean tie;
        Tower playerTowerWithMaxValue = null;
        for (Color color : Color.values()) {
            maxStudent = 0;
            tie = true;
            playerTowerWithMaxValue = null;
            for (PlayerBoard player : players) {
                if (player.countStudentsDiningRoom(color) > maxStudent) {
                    maxStudent = player.countStudentsDiningRoom(color);
                    tie = false;
                    playerTowerWithMaxValue = player.getTowerColor();
                } else if (player.countStudentsDiningRoom(color) == maxStudent) tie = true;
            }
            professors.remove(color);
            if (tie) {
                professors.put(color, null);
            } else {
                professors.put(color, playerTowerWithMaxValue);
            }
        }
        System.out.println("GAME BOARD - updateProfessorOwnership - notify the professor ownership");
        Map<Color,Tower> profMap = new HashMap<>();
        profMap.putAll(professors);
        if(modelListener!=null && clients!=null)
            notify(modelListener,MessageGenerator.professorsUpdateMessage(profMap),clients);
    }

    /**
     * Return the list of the clouds' positions that the player can choose
     * The cloud positions are in the range [1,numPlayer]
     *
     * @return : the list of the positions of the usable clouds
     */
    public Set<Integer> getUsableClouds() {
        Set<Integer> usableClouds = new HashSet<>();
        for (int i = 0; i < playerNumber; i++) {
            if (!clouds[i].isHasBeenUsed()) usableClouds.add(i + 1);
        }
        return usableClouds;
    }

    public Tower getPlayerTower(String nickname) {
        for (PlayerBoard player : players) {
            if (nickname.equalsIgnoreCase(player.getNickName())) return player.getTowerColor();
        }
        return null;
    }

    /**
     * Gets the number of actives islands
     *
     * @return : the number of single islands + island groups
     */
    public int getIslandNumber() {
        return this.islands.length;
    }

    /**
     * Tells if the bag is actually empty or if some students are contained into it
     *
     * @return : true if the bag is empty, false if not
     */

    public boolean isBagEmpty() {
        return this.bag.isEmpty();
    }

    /**
     * Tells if the diningRoom of the current player is occupied by at least one student or not
     * Used for the bard special card to avoid situation where there are not students in the dining room
     */
    public boolean isDiningRoomOccupied(){
        if(this.players[currentPlayer]!=null && this.players[currentPlayer].countStudentsDiningRoom()>0)
            return true;
        return false;
    }

    /**
     * Computes the influence on a given island and sets accordingly the towers
     *
     * @param island : position of the island we want to compute the influence
     * @return : the Tower which is present on the island at the end of the computation (null if none)
     */
    public abstract Tower computeInfluence(int island) throws IslandOutOfBoundException;

    /**
     * Update the professor OwnerShip favouring the current player if the professors are contended between him and other(s) player(s)
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract void updateProfessorOwnershipIfTie() throws FunctionNotImplementedException;

    /**
     * Disables the influence computation on a particular island with a no entry tile.
     * The method does not control if an island is already disable, so for not loose an entry tile the control must be done by the caller
     *
     * @param position : position of the island we want to disable the influence
     * @return : true if the island is successfully disabled. False otherwise
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     * @throws IslandOutOfBoundException       : thrown if the island integer is not correct since it exceed the correct range of value that the islands' positions have
     */
    public abstract boolean disableInfluence(int position) throws FunctionNotImplementedException, IslandOutOfBoundException;

    /**
     * Increases the influence of the current player by +2 in the influence computation
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract void increaseInfluence() throws FunctionNotImplementedException;

    /**
     * Disables the influence score given by a particular color
     *
     * @param color : color we want to disable the influence score
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract void disableColorInfluence(Color color) throws FunctionNotImplementedException;

    /**
     * Resets all the flags related to the SpecialCards' effects that end with the end of the turn
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract void resetAllTurnFlags() throws FunctionNotImplementedException;

    /**
     * Increase the allowed movement of motherNature by +2
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract void increaseMovementMotherNature() throws FunctionNotImplementedException;

    /**
     * Getter of cloud limit number
     */
    public int getCloudLimit(){
        return clouds[0].getStudentLimit();
    }

    /**
     * Returns a Set with the specialCardNames
     *
     * @return : a Set with the SpecialCardNames of the instantiated special cards
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract Set<SpecialCardName> getSetOfSpecialCardNames() throws FunctionNotImplementedException;

    /**
     * Pay the coins of the active player to use a particular special card
     *
     * @param cost : expense of the activation of a SpecialCard, expressed in coin
     * @return : true if the SpecialCard has been pay, false if the cost is too much and the expense could not be pay.
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract boolean paySpecialCard(int cost) throws FunctionNotImplementedException;

    /**
     * Gets the cost of a particular special card
     *
     * @param specialCardName : name of the special card to pay
     * @return true if the special card exists and is one of the initialized special cards, false if not
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */

    public abstract Integer getSpecialCardCost(SpecialCardName specialCardName) throws FunctionNotImplementedException;

    /**
     * Disable the towerInfluence in order to not count the towers in the influence score computation
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract void disableTowerInfluence() throws FunctionNotImplementedException;

    /**
     * Activates the special effect
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract void professorsUpdateTieEffect() throws FunctionNotImplementedException ;

    /**
     * Returns an array with the specialCardNames
     *
     * @return : an array with the SpecialCardNames of the instantiated special cards
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    public abstract SpecialCard[] getArrayOfSpecialCard() throws FunctionNotImplementedException ;
}
