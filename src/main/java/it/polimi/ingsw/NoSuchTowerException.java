package it.polimi.ingsw;

public class NoSuchTowerException extends Exception{
    NoSuchTowerException(String motive){
        super("ERROR: The Tower value is null, not possessed by any player or other. Motive of the issue: "+motive);
    }
}
