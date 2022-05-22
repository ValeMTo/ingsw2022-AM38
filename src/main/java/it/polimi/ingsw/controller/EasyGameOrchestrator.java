package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.model.board.Color;

import java.util.List;

/**
 * GameOrchestrator implementation class in case of the easy game mode
 */
public class EasyGameOrchestrator extends GameOrchestrator {
    public EasyGameOrchestrator(List<String> players) {
        super(players, false);
    }

    /**
     * Throws FunctionNotImplementedException since the function is not implemented by the easy game mode
     */
    @Override
    public SpecialCardRequiredAction useSpecialCard(String cardName, String nextRequest) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    /**
     * Throws FunctionNotImplementedException since the function is not implemented by the easy game mode
     */
    @Override
    public SpecialCardRequiredAction chooseColor(Color color, String nextRequest) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    /**
     * Throws FunctionNotImplementedException since the function is not implemented by the easy game mode
     */
    @Override
    public SpecialCardRequiredAction chooseIsland(int position, String nextRequest) throws FunctionNotImplementedException, IslandOutOfBoundException {
        throw new FunctionNotImplementedException();
    }


    @Override
    public boolean moveMotherNature(int destinationIsland) throws IslandOutOfBoundException, IncorrectPhaseException {
        boolean returnValue = super.moveMotherNature(destinationIsland);
        // If the motherNature is moved correctly, updates the professors and computes the influence
        if (returnValue) {
            gameBoard.updateProfessorOwnership();
            gameBoard.computeInfluence(destinationIsland);
        }
        return returnValue;
    }

}
