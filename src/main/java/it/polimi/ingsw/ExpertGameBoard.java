package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpertGameBoard extends GameBoard {
    protected SpecialCard[] specialCards;
    //protected final int playerNumber;
    private boolean towerInfluence = true;
    private List<Color> noInfluenceByColor = null;
    private int moreInfluenceQuantity = 0;
    private boolean motherNatureIncreasedMove = false;
    private int increasedMovement = 0;


    /**
     * Constructor for the expert GameBoard
     *
     * @param numPlayer : number of players of the match
     */
    public ExpertGameBoard(int numPlayer, List<String> playersNicknames) {
        super(numPlayer, playersNicknames);
    }


    /**
     * Compute the influence on a specific Island.
     * This method is called if motherNature is moved on this Island or if special card trigger the influence computation
     *
     * @param island : Island's position where the influence must be computed
     * @return : return the Tower of the Player who has more influence
     */
    public Tower computeInfluence(int island) { //It could be returning a Tower... but player nickname is also ok...
        Tower islandTower = islands[island].getTower();
        // If the Island we want to compute the influence is disabled, we re-enable it and do not compute influence, just return the old TowerColor
        if (!this.islands[island].isInfluenceEnabled()) {
            islands[island].enableInfluence();
            return islandTower;
        }

        //Otherwise, if the influence computation is enabled, first we create a support HashMap for the influence
        Map<Tower, Integer> computationMap = new HashMap<Tower, Integer>();
        Tower professorTower;
        Color professorColor;
        //Initialize to 0 the support HashMap
        for (int i = 0; i < players.length; i++) {
            computationMap.put(players[i].getTowerColor(), 0);
        }

        //Compute the influence given by the professors controlled if the Color is enabled
        for (int i = 0; i < professors.values().size(); i++) {
            professorTower = (Tower) professors.values().toArray()[i];
            professorColor = (Color) professors.keySet().toArray()[i];
            if (professorTower != null && !noInfluenceByColor.contains(professorColor)) {
                //professorColor = (Color)professors.keySet().toArray()[i];
                computationMap.put(professorTower, computationMap.get(professorTower) + islands[island].studentNumber(professorColor));
            }
        }

        //Compute the influence given by the Towers in the Islands if the Tower Influence is enabled
        if (islandTower != null && towerInfluence)
            computationMap.put(islandTower, computationMap.get(islandTower) + islands[island].getTowerNumber());

        //Finally, increase the influence if the player has bonuses
        if (moreInfluenceQuantity != 0) {
            Tower currentPlayerTower = players[currentPlayer].getTowerColor();
            computationMap.put(currentPlayerTower, computationMap.get(currentPlayerTower) + moreInfluenceQuantity);
        }
        boolean tie = false;
        int maxValue = 0;
        int computationValue;
        Tower playerWithMoreInfluence = null;
        //Compute the Tower Color, the player who has the maximum value of influence or if we have a tie between players.
        for (int i = 0; i < players.length; i++) {
            computationValue = computationMap.get(players[i].getTowerColor());
            if (computationValue > maxValue) {
                playerWithMoreInfluence = players[i].getTowerColor();
                tie = false;
                maxValue = computationValue;
            } else if (computationValue == maxValue) tie = true;
        }
        //Remove the old Towers from the player
        int towerToChange = islands[island].getTowerNumber();
        if (!tie && playerWithMoreInfluence != null) {
            islandTower = islands[island].getTower();
            if (islandTower != null) {
                for (int i = 0; i < players.length; i++) {
                    if (players[i].getTowerColor() == islandTower) {
                        players[i].addTower(towerToChange);
                    }
                }
                //Set the new Tower Color in Island
                islands[island].setTower(playerWithMoreInfluence);
                //Set the new number of tower of the player with maximum influence on the island
                for (int i = 0; i < players.length; i++) {
                    if (players[i].getTowerColor() == playerWithMoreInfluence) {
                        players[i].removeTower(towerToChange);
                    }
                }

            }
        }
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
            Color professorColor;
            Tower professorTower;
            // For each Professor's Color, we compute if there is a tie (professorTower is null)
            // and then compute if the player has the same students as at least another player
            for (int i = 0; i < Color.values().length; i++) {
                professorTower = (Tower) professors.values().toArray()[i];
                if (professorTower == null) {
                    professorColor = (Color) professors.keySet().toArray()[i];
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
     * @param position : position of the Island we want to disable the influence computation
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
        if (noInfluenceByColor == null) noInfluenceByColor = new ArrayList<Color>();
        noInfluenceByColor.add(color);
    }

    /**
     * Reset all the flags of the special card to their default value, disabling all the effects of the special cards that only have active effects in the turn.
     */
    public void resetAllTurnFlags() {
        ArrayList<Color> temporaryNoInfluenceColor = new ArrayList<Color>();
        temporaryNoInfluenceColor.addAll(noInfluenceByColor);
        towerInfluence = true;
        moreInfluenceQuantity = 0;
        motherNatureIncreasedMove = false;
        noInfluenceByColor.removeAll(temporaryNoInfluenceColor);
        increasedMovement = 0;
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
    }

    //TODO: needed a getStep method to have the steps given an assistant card or maybe a static method of the AssistantCard to give the steps value given the priority

    /**
     * Move motherNature considering the increased movement
     *
     * @param destinationIsland : Position of the island where the motherNature should be moved
     * @return
     */
    @Override
    public boolean moveMotherNature(int destinationIsland) {
        int maxMovement = 0;
        try {
            maxMovement += players[currentPlayer].getLastCardSteps();
        } catch (NotLastCardUsedException exc) {
            exc.printStackTrace();
            return false;
        }
        if (this.motherNatureIncreasedMove) maxMovement += increasedMovement;
        if (destinationIsland <= motherNature && destinationIsland - motherNature <= maxMovement) {
            motherNature = destinationIsland;
            return true;
        } else if (destinationIsland > motherNature && islands[islands.length].getPosition() - destinationIsland + motherNature <= maxMovement) {
            motherNature = destinationIsland;
            return true;
        }
        return false;
    }

}