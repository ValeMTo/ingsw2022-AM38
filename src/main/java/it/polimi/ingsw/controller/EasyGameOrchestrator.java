package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
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

    @Override
    public SpecialCardRequiredAction useSpecialCard(String cardName, String nextRequest) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    public SpecialCardRequiredAction chooseColor(Color color, String nextRequest) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    }

    @Override
    public SpecialCardRequiredAction chooseIsland(int position, String nextRequest) throws FunctionNotImplementedException, IslandOutOfBoundException {
        throw new FunctionNotImplementedException();
    }


}
