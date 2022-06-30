package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.server.ClientHandler;

import java.util.List;

/**
 * GameOrchestrator implementation class in case of the easy game mode
 */
public class EasyGameOrchestrator extends GameOrchestrator {
    public EasyGameOrchestrator(List<String> players, int id, List<ClientHandler> clients) {
        super(players, false, id, clients);
    }

    /**
     * Throws FunctionNotImplementedException since the function is not implemented by the easy game mode
     */
    @Override
    public String useSpecialCard(String cardName) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    /**
     * Throws FunctionNotImplementedException since the function is not implemented by the easy game mode
     */
    @Override
    public String chooseColor(Color color) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    /**
     * Throws FunctionNotImplementedException since the function is not implemented by the easy game mode
     */
    @Override
    public String chooseIsland(int position) throws FunctionNotImplementedException, IslandOutOfBoundException {
        throw new FunctionNotImplementedException();
    }

    /**
     * Throws FunctionNotImplementedException since the function is not implemented by the easy game mode
     */

    @Override
    public String terminateSpecialCardUsage() throws FunctionNotImplementedException
    {
        throw new FunctionNotImplementedException();
    }

}
