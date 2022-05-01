package it.polimi.ingsw.exceptions;

public class FunctionNotImplementedException extends Exception{
    public FunctionNotImplementedException(){
        super("ERROR - Function not implemented for the Game mode set.\nGame mode is easy, this function is for expert mode only!\n");
    }
    public FunctionNotImplementedException(String errorMessage){
        super(errorMessage);
    }
}
