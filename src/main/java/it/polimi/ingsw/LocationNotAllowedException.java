package it.polimi.ingsw;

public class LocationNotAllowedException extends Exception{
    public LocationNotAllowedException(String motive){
     super("\nERROR - The Location enumeration value is not appropriate for this method. This exception is caused because : "+motive);
    }

}
