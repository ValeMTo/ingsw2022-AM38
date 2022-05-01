package it.polimi.ingsw.exceptions;

public class NicknameAlreadyTakenException extends Exception{
    public NicknameAlreadyTakenException(){
        super("ERROR - Nickname chosen already taken");
    }
    public NicknameAlreadyTakenException(String errorMessage){
        super(errorMessage);
    }
}
