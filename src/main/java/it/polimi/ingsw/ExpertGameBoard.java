package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpertGameBoard extends GameBoard {
    protected SpecialCard[] specialCards;
    //protected final int playerNumber;
    private boolean towerInfluence = true;
    private Color noInfluenceByColor;
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
    public Tower computeInfluence(int island) throws IslandOutOfBoundException { //It could be returning a Tower... but player nickname is also ok...
        if (island < 1 || island > islands[islands.length - 1].getPosition())
            throw new IslandOutOfBoundException(1, islands[islands.length - 1].getPosition());

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
        return islands[island - 1].getTower(); //return tower name...

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

    @Override
    public boolean addStudent(StudentCounter location, Color student, int position) throws LocationNotAllowedException, FunctionNotImplementedException {
        if (location == StudentCounter.CARD) {
            //TODO
        }
        return super.addStudent(location, student, position);
    }

}