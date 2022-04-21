package it.polimi.ingsw;

public class FunctionNotImplementedException extends Exception{
    public FunctionNotImplementedException(String errorMessage){
        super(errorMessage);
    }
}
