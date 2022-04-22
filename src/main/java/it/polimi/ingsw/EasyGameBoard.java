package it.polimi.ingsw;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Island currentIsland = islands[island];
        Tower currentPlayerTower = players[currentPlayer].getTowerColor();

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

        for (Color color : Color.values()) {
            if (professors.get(color) != null) { //if the professor belongs to someone
                //adds the amount of students of that color to the ownership of that person
                computationMap.put(professors.get(color), computationMap.get(professors.get(color)) + currentIsland.studentNumber(color));
            }
        }
        
        //Calculate the against influence
        int otherStudents = 0;
        for (Tower player : computationMap.keySet()) {
            if (!player.equals(currentPlayerTower)){
                otherStudents += computationMap.get(player);
            }
        }

        //if the current player has a major influence return it, otherwise it can conquer the island.
        if(computationMap.get(currentPlayerTower)>otherStudents){
            return currentPlayerTower;
        }

        return null;
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
}
