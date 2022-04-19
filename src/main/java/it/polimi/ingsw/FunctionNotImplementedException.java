package it.polimi.ingsw;

public class FunctionNotImplementedException extends Exception{
    public FunctionNotImplementedException(){
        super("ERROR - Function not implemented for the Game mode set.\nGame mode is easy, this function is for expert mode only!\n");
    }
    public FunctionNotImplementedException(String specific){
        super("ERROR - Function not implemented for the Game mode set.\nGame mode is easy, this function is for expert mode only!\n"+specific+"\n");
    }
}
