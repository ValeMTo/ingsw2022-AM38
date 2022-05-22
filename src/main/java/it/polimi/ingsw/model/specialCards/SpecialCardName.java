package it.polimi.ingsw.model.specialCards;

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
}

