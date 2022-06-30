package it.polimi.ingsw.model.board;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.model.player.PlayerBoard;
import it.polimi.ingsw.model.specialCards.SpecialCard;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EasyGameBoard extends GameBoard {

    public EasyGameBoard(int playerNumber, List<String> playersNicknames) {
        super(playerNumber, playersNicknames);
    }

    /**
     * Compute the influence on a specific Island.
     * This method is called if motherNature is moved on this Island
     *
     * @param island : Island's position where the influence must be computed
     * @return : return the Tower of the Player who has more influence
     */
    @Override
    public Tower computeInfluence(int island) throws IslandOutOfBoundException {
        if (island < 1 || island > islands.length)
            throw new IslandOutOfBoundException(1, islands[islands.length - 1].getPosition());
        updateProfessorOwnership();
        Island currentIsland = islands[island - 1];
        Tower playerTower = null;

        //Otherwise, if the influence computation is enabled, first we create a support HashMap for the influence
        Map<Tower, Integer> computationMap = new HashMap<Tower, Integer>();
        //Initialize to 0 the support HashMap
        for (int i = 0; i < players.length; i++) {
            computationMap.put(players[i].getTowerColor(), 0);
        }

        //Add the tower influence
        if (currentIsland.getTower() != null) {
            computationMap.put(currentIsland.getTower(), currentIsland.getTowerNumber());
        }

        //Add the score given by the students
        for (Color color : Color.values()) {
            if (professors.get(color) != null) { //if the professor belongs to someone
                //adds the amount of students of that color to the ownership of that person
                computationMap.put(professors.get(color), computationMap.get(professors.get(color)) + currentIsland.studentNumber(color));
            }
        }


        //Calculate the maximum influence
        int max = 0;
        boolean tie = false;
        for (Tower player : computationMap.keySet()) {
            if (computationMap.get(player) > max) {
                max = computationMap.get(player);
                playerTower = player;
                tie = false;
            } else if (computationMap.get(player) == max) tie = true;
        }

        //if the current player has a major influence return it, otherwise it can conquer the island.
        if (tie) {
            return islands[island - 1].getTower();
        }

        if (islands[island - 1].getTower() != null) {
            for (Tower player : computationMap.keySet())
                computationMap.put(player, computationMap.get(player) + islands[island - 1].getTowerNumber());
        }

        if (islands[island - 1].getTower() != null) {
            for (PlayerBoard player : players)
                player.addTower(islands[island - 1].getTowerNumber()); //Give back the towers to the player
        }
        islands[island - 1].setTower(playerTower);
        if (islands[island - 1].getTowerNumber() == 0) islands[island - 1].setTowerNumber(1);
        for (PlayerBoard player : players)
            if (player.getTowerColor().equals(playerTower)) {
                System.out.println("EASY GAME BOARD - computeInfluence - removing "+islands[island - 1].getTowerNumber()+" towers from player "+playerTower);
                player.removeTower(islands[island - 1].getTowerNumber());
            }
        Tower islandTowerToReturn = islands[island - 1].getTower();
        groupIslands(island);
        return islandTowerToReturn;
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public void updateProfessorOwnershipIfTie() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws IslandOutOfBoundException       : thrown if the island integer is not correct since it exceed the correct range of value that the islands' positions have
     */ public boolean disableInfluence(int position) throws FunctionNotImplementedException, IslandOutOfBoundException {
        throw new FunctionNotImplementedException();
    }


    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public void increaseInfluence() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public void disableColorInfluence(Color color) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public void resetAllTurnFlags() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public void increaseMovementMotherNature() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public Set<SpecialCardName> getSetOfSpecialCardNames() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public boolean paySpecialCard(int cost) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public void disableTowerInfluence() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public Integer getSpecialCardCost(SpecialCardName specialCardName) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */ public void professorsUpdateTieEffect() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    /**
     * Not usable in easy mode
     *
     * @throws FunctionNotImplementedException : if the game mode is easy, this method cannot be called as this functionality is for expert game only
     */
    @Override
    public SpecialCard[] getArrayOfSpecialCard() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    public void save(JSONObject jsonSave){
        super.save(jsonSave);
        jsonSave.put("IsExpert",false);
    }
}
