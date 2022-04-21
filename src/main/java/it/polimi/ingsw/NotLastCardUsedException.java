package it.polimi.ingsw;

public class NotLastCardUsedException extends Exception{
    public NotLastCardUsedException(String errorMessage){
        super(errorMessage);
    }

}
