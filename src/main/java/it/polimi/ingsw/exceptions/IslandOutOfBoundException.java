package it.polimi.ingsw.exceptions;

public class IslandOutOfBoundException extends Exception{
    private final int lowerBound, higherBound;
    public IslandOutOfBoundException(int lowerBound, int higherBound){
        super("\nERROR - Island position is not allowed. The usable range is ["+lowerBound+", "+higherBound+"]\n");
        this.lowerBound = lowerBound;
        this.higherBound = higherBound;
    }
}
