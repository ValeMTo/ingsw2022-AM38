package it.polimi.ingsw.exceptions;

public class NoSuchTowerException extends Exception{
    public NoSuchTowerException(String motive){
        super("ERROR: The Tower value is null, not possessed by any player or other. Motive of the issue: "+motive);
    }
}
