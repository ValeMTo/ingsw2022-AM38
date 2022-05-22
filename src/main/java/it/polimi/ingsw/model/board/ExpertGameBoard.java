package it.polimi.ingsw.model.board;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.exceptions.LocationNotAllowedException;
import it.polimi.ingsw.exceptions.NotLastCardUsedException;
import it.polimi.ingsw.model.player.PlayerBoard;
import it.polimi.ingsw.model.specialCards.Herbalist;
import it.polimi.ingsw.model.specialCards.SpecialCard;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import it.polimi.ingsw.model.specialCards.SpecialCardWithStudent;

import java.util.*;

public class ExpertGameBoard extends GameBoard {
    private final int specialCardsNum = 3;
    protected SpecialCard[] specialCards;
    private boolean towerInfluence = true;
    private Color noInfluenceByColor;
    private int moreInfluenceQuantity = 0;
    private boolean motherNatureIncreasedMove = false;
    private boolean professorsUpdateTieEffect = false;
    private int increasedMovement = 0;


    /**
     * Constructor for the expert GameBoard
     *
     * @param numPlayer : number of players of the match
     */
    public ExpertGameBoard(int numPlayer, List<String> playersNicknames) {
        super(numPlayer, playersNicknames);
        specialCards = new SpecialCard[specialCardsNum];
        Random random = new Random();
        int i = 0, cardNum;
        Set<Integer> alreadyUsedCardNum = new TreeSet<Integer>();
        while (i < specialCardsNum) {
            cardNum = random.nextInt(12);
            //
            //int final int card = cardNum;
            //SpecialCard s = SpecialCardName.values().stream.filter((x)->x.ordinal()==card);
            switch (cardNum) {
                case 0:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCardWithStudent(SpecialCardName.PRIEST);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 1:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.HERALD);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 2:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.POSTMAN);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 3:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new Herbalist();
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 4:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.ARCHER);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 5:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCardWithStudent(SpecialCardName.JUGGLER);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 6:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.KNIGHT);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 7:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.COOKER);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 8:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.BARD);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 9:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCardWithStudent(SpecialCardName.PRINCESS);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 10:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.GAMBLER);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
                case 11:
                    if (!alreadyUsedCardNum.contains(cardNum)) {
                        specialCards[i] = new SpecialCard(SpecialCardName.CHEESEMAKER);
                        alreadyUsedCardNum.add(cardNum);
                        i++;
                    }
                    break;
            }
        }
    }

    /**
     * Returns a Map with the position of each special card
     *
     * @return : a Map with the SpecialCardName and the position of the instantiated special cards
     */
    public Map<SpecialCardName, Integer> getMapOfSpecialCardNames() {
        Map<SpecialCardName, Integer> returnMap = new HashMap<SpecialCardName, Integer>();
        for (int i = 0; i < specialCards.length; i++)
            returnMap.put(specialCards[i].getName(), i);
        return returnMap;
    }

    /**
     * Returns a Set with the specialCardNames
     *
     * @return : a Set with the SpecialCardNames of the instantiated special cards
     */
    public Set<SpecialCardName> getSetOfSpecialCardNames() {
        Set<SpecialCardName> returnSet = new TreeSet<SpecialCardName>();
        for (SpecialCard specialCard : specialCards)
            returnSet.add(specialCard.getName());
        return returnSet;
    }

    /**
     * Returns an array with the specialCardNames
     *
     * @return : an array with the SpecialCardNames of the instantiated special cards
     */
    public SpecialCardName[] getArrayOfSpecialCardNames(){
        SpecialCardName[] returnArray = new SpecialCardName[specialCards.length];
        for(int i = 0; i<specialCards.length;i++)
            returnArray[i] = specialCards[i].getName();
        return returnArray;
    }


    /**
     * Compute the influence on a specific Island.
     * This method is called if motherNature is moved on this Island or if special card trigger the influence computation
     * Also call if the Tower has changed the protected method to merge the islands if more islands with same tower color are adjacent
     *
     * @param island : Island's position where the influence must be computed
     * @return : return the Tower of the Player who has more influence
     */
    public Tower computeInfluence(int island) throws IslandOutOfBoundException { //It could be returning a Tower... but player nickname is also ok...
        if (island < 1 || island > islands[islands.length - 1].getPosition())
            throw new IslandOutOfBoundException(1, islands[islands.length - 1].getPosition());
        // Update the professors accordingly to the professorsUpdateTieEffect special effect flag
        if (this.professorsUpdateTieEffect) updateProfessorOwnershipIfTie();
        else updateProfessorOwnership();
        Tower islandTower = islands[island - 1].getTower();
        // If the Island we want to compute the influence is disabled, we re-enable it and do not compute influence, just return the old TowerColor
        if (!this.islands[island - 1].isInfluenceEnabled()) {
            islands[island - 1].enableInfluence();
            return islandTower;
        }

        //Otherwise, if the influence computation is enabled, first we create a support HashMap for the influence
        Map<Tower, Integer> computationMap = new HashMap<Tower, Integer>();
        Tower professorTower = null;
        Tower playerWithMoreInfluence = null;

        //Initialize to 0 the support HashMap for players score
        for (PlayerBoard player : players) {
            computationMap.put(player.getTowerColor(), 0);
        }

        //Compute the influence given by the professors controlled if the Color is enabled
        for (Color professorColor : Color.values()) {
            professorTower = professors.get(professorColor);
            if (professorTower != null && professorColor != noInfluenceByColor) {
                //professorColor = (Color)professors.keySet().toArray()[i];
                computationMap.put(professorTower, computationMap.get(professorTower) + islands[island - 1].studentNumber(professorColor));
            }
        }

        //Compute the influence given by the Towers in the Islands if the Tower Influence is enabled
        if (islandTower != null && towerInfluence) {
            computationMap.put(islandTower, computationMap.get(islandTower) + islands[island - 1].getTowerNumber());

        }

        //Finally, increase the influence if the player has bonuses
        if (moreInfluenceQuantity != 0) {
            Tower currentPlayerTower = players[currentPlayer].getTowerColor();
            computationMap.put(currentPlayerTower, computationMap.get(currentPlayerTower) + moreInfluenceQuantity);
        }
        boolean tie = false;
        int maxValue = 0;
        int computationValue;
        //Compute the Tower Color, the player who has the maximum value of influence or if we have a tie between players.
        for (PlayerBoard player : players) {
            computationValue = computationMap.get(player.getTowerColor());
            if (computationValue > maxValue) {
                playerWithMoreInfluence = player.getTowerColor();
                tie = false;
                maxValue = computationValue;
            } else if (computationValue == maxValue) tie = true;
        }

        //If tie nothing changes
        if (tie) return islands[island - 1].getTower();

        //Remove the old Towers from the player and add to the player that had the towers
        int towerToChange = islands[island - 1].getTowerNumber();
        if (playerWithMoreInfluence != null) {
            islandTower = islands[island - 1].getTower();
            if (islandTower != null) {
                for (PlayerBoard player : players) {
                    if (player.getTowerColor() == islandTower) {
                        player.addTower(towerToChange);
                    }
                    //Set the new number of tower of the player with maximum influence on the island
                    if (player.getTowerColor() == playerWithMoreInfluence) {
                        player.removeTower(towerToChange);
                    }
                }
                //Set the new Tower Color in Island and the tower number to 1 if its first tower is added

            }
            islands[island - 1].setTower(playerWithMoreInfluence);

            if (islands[island - 1].getTowerNumber() == 0) islands[island - 1].setTowerNumber(1);
        }
        groupIslands(island);
        return playerWithMoreInfluence; //return tower name...

    }

    /**
     * Update the professors' ownership in case of a tie of the max number of students of the professor's color
     * between the player specified and others players, the professor will be updated as controlled by this player
     */
    public void updateProfessorOwnershipIfTie() {
        this.updateProfessorOwnership(); //We reset as usual the professors.
        PlayerBoard playerUpdate = players[currentPlayer];
        Tower playerTower;
        int tieValue, playerStudentsByColor;
        //Search the player by its TowerColor
        if (playerUpdate != null) {
            Tower professorTower;
            // For each Professor's Color, we compute if there is a tie (professorTower is null)
            // and then compute if the player has the same students as at least another player
            for (Color professorColor : Color.values()) {
                professorTower = professors.get(professorColor);
                if (professorTower == null) {
                    tieValue = 0;
                    for (int j = 0; j < players.length; j++) {
                        playerStudentsByColor = players[j].countStudentsDiningRoom(professorColor);
                        if (playerStudentsByColor >= tieValue) {
                            tieValue = playerStudentsByColor;
                        }
                    }
                    // We set the professor Tower as the one of the player if the player has the maximum amount of students.
                    if (playerUpdate.countStudentsDiningRoom(professorColor) == tieValue)
                        professors.put(professorColor, playerUpdate.getTowerColor());
                }
            }
        }
    }

    /**
     * Disable the influence enable flag of a specified Island to prevent the normal influence computation changes
     *
     * @param position : position of the Island we want to disable the influence computation (1 up to 12)
     * @return : true island is successfully disabled, false if not or if the island was already disabled
     */
    @Override
    public boolean disableInfluence(int position) throws IslandOutOfBoundException {
        if (position > islands[islands.length - 1].getPosition())
            throw new IslandOutOfBoundException(1, islands[islands.length - 1].getPosition());
        if (islands[position].isInfluenceEnabled()) {
            islands[position].disableInfluence();
            return true;
        }
        return false;
    }

    /**
     * Set the flag that increase the influence score for a particular player during the influence computation
     */
    public void increaseInfluence() {
        moreInfluenceQuantity = 2;
    }

    /**
     * Disable the towerInfluence in order to not count the towers in the influence score computation
     */
    public void disableTowerInfluence() {
        towerInfluence = false;
    }

    /**
     * Disable the score given by a particular Color during the influence score computation
     *
     * @param color : color that does not give points in the influence computation
     */
    public void disableColorInfluence(Color color) {
        noInfluenceByColor = color;
    }

    /**
     * Reset all the flags of the special card to their default value, disabling all the effects of the special cards that only have active effects in the turn.
     */
    public void resetAllTurnFlags() {
        ArrayList<Color> temporaryNoInfluenceColor = new ArrayList<Color>();
        towerInfluence = true;
        moreInfluenceQuantity = 0;
        motherNatureIncreasedMove = false;
        noInfluenceByColor = null;
        increasedMovement = 0;
        professorsUpdateTieEffect = false;
    }

    /**
     * Pay the coins of the active player to use a particular special card
     *
     * @param cost : expense of the activation of a SpecialCard, expressed in coin
     * @return : true if the SpecialCard has been pay, false if the cost is too much and the expense could not be pay.
     */
    public boolean paySpecialCard(int cost) {
        return players[currentPlayer].pay(cost);
    }

    /**
     * Increase the movement of the MotherNature in this round
     */
    public void increaseMovementMotherNature() {
        increasedMovement = 2;
        motherNatureIncreasedMove = true;
    }

    /**
     * Activates the special effect
     */
    public void professorsUpdateTieEffect() {
        this.professorsUpdateTieEffect = true;
    }


    /**
     * Move motherNature considering the increased movement
     *
     * @param destinationIsland : Position of the island where the motherNature should be moved
     * @return
     */
    @Override
    public boolean moveMotherNature(int destinationIsland) throws IslandOutOfBoundException {
        if (destinationIsland < 1 || destinationIsland > islands[islands.length - 1].getPosition())
            throw new IslandOutOfBoundException(1, islands[islands.length - 1].getPosition());
        int maxMovement = 0;
        try {
            maxMovement += players[currentPlayer].getLastCardSteps();
        } catch (NotLastCardUsedException exc) {
            exc.printStackTrace();
            return false;
        }
        if (this.motherNatureIncreasedMove) maxMovement += increasedMovement;
        if (destinationIsland >= motherNature && (destinationIsland - motherNature) <= maxMovement) {
            motherNature = destinationIsland;
            return true;
        } else if (destinationIsland <= motherNature && (islands[islands.length - 1].getPosition() - destinationIsland + motherNature) <= maxMovement) {
            motherNature = destinationIsland;
            return true;
        }
        return false;
    }

    /**
     * Add a student to a specific location using a position value to choose the right player/island/specialCard/cloud.
     * Override the method in GameBoard to support the specialCard add and use the super method for the rest
     *
     * @param location : where the student will end
     * @param student  : student's color
     * @param position : island position or nr. of player or nr. of island (for island 1-12) or nr. of specialCard(0-3)
     * @return : the outcome of the add (true if correctly add, false if not possible)
     * @throws LocationNotAllowedException     : if Location field is incorrect
     * @throws FunctionNotImplementedException : if the specialCard does not implement the add student
     */
    @Override
    public boolean addStudent(StudentCounter location, Color student, int position) throws LocationNotAllowedException, FunctionNotImplementedException, IndexOutOfBoundsException {
        if (location == StudentCounter.CARD) {
            if (position < 0 || position > (specialCards.length - 1)) return false;
            return specialCards[position].addStudent(student);
        }
        return super.addStudent(location, student, position);

    }

    /**
     * Remove a student to a specific location using a position value to choose the right player/specialCard/cloud.
     * Override the method in GameBoard to support the specialCard remove and use the super method for the rest
     *
     * @param location : where the student will end
     * @param student  : student's color
     * @param position : player nr. or nr. of specialCard(0-3) or cloud nr. [0; (numPlayers-1)]
     * @return : the outcome of the remove (true if correctly removed, false if not possible)
     * @throws LocationNotAllowedException     : if Location field is incorrect
     * @throws FunctionNotImplementedException : if the specialCard does not implement the remove student
     */

    @Override
    public boolean removeStudent(StudentCounter location, Color student, int position) throws LocationNotAllowedException, FunctionNotImplementedException, IndexOutOfBoundsException {
        if (location == StudentCounter.CARD) {
            if (position < 0 || position > (specialCards.length - 1)) return false;

            return specialCards[position].removeStudent(student);

        }
        return super.removeStudent(location, student, position);

    }

    /**
     * Gets the cost of a particular special card
     *
     * @param specialCardName : name of the special card to pay
     * @return true if the special card exists and is one of the initialized special cards, false if not
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    @Override
    public boolean getSpecialCardCost(SpecialCardName specialCardName, Integer cost) throws FunctionNotImplementedException {
        for (SpecialCard specialCard : specialCards) {
            if (specialCard.getName().equals(specialCardName)) {
                cost = specialCard.getCostCoin();
                return true;
            }
        }
        cost = null;
        return false;
    }
}