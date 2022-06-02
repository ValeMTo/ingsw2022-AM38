package it.polimi.ingsw.model.specialCards;

import java.util.ArrayList;
import java.util.List;

public enum SpecialCardName {
    PRIEST,
    HERALD,
    POSTMAN,
    HERBALIST,
    ARCHER,
    JUGGLER,
    KNIGHT,
    COOKER,
    BARD,
    PRINCESS,
    GAMBLER,
    CHEESEMAKER;

    /**
     * Convert from String to the SpecialCardName enum type
     * @param specialCardName : name that we want to convert into enum value
     * @return the converted enum value or null if incorrect
     */
    public static SpecialCardName convertFromStringToEnum(String specialCardName){
        for(SpecialCardName name : SpecialCardName.values()){
            if(specialCardName.equalsIgnoreCase(name.toString()))
                return name;
        }
        return null;
    }

    /**
     * Returns the list of special cards names of the special cards with students
     * @return
     */
    public static List<SpecialCardName> getSpecialCardsWithStudents(){
        List<SpecialCardName> specialCardsToReturn = new ArrayList<>();
        specialCardsToReturn.add(PRIEST);
        specialCardsToReturn.add(JUGGLER);
        specialCardsToReturn.add(PRINCESS);
        return specialCardsToReturn;
    }
}

