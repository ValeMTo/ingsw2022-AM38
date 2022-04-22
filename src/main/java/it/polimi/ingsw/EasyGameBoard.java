package it.polimi.ingsw;

public class EasyGameBoard extends GameBoard{
    @Override
    public Tower computeInfluence(int island) throws IslandOutOfBoundException {
        return null;
    }

    @Override
    public void updateProfessorOwnershipIfTie() throws FunctionNotImplementedException {

    }

    @Override
    public boolean disableInfluence(int position) throws FunctionNotImplementedException, IslandOutOfBoundException {
        return false;
    }

    @Override
    public void increaseInfluence() throws FunctionNotImplementedException {

    }

    @Override
    public void disableColorInfluence(Color color) throws FunctionNotImplementedException {

    }

    @Override
    public void resetAllTurnFlags() throws FunctionNotImplementedException {

    }

    @Override
    public void increaseMovementMotherNature() throws FunctionNotImplementedException {

    }
}
