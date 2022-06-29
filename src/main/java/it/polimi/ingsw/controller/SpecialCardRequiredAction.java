package it.polimi.ingsw.controller;

public enum SpecialCardRequiredAction {
    NO_SUCH_CARD,        //0
    NOT_ENOUGH_COINS,    //1
    CHOOSE_COLOR,       // 2
    CHOOSE_ISLAND,      // 3
    CHOOSE_COLOR_CARD,  // 4
    CHOOSE_COLOR_DINING_ROOM,   // 5
    CHOOSE_COLOR_SCHOOL_ENTRANCE,   // 6
    USED_CORRECTLY,                 // 7
    NO_SUCH_COLOR,                  // 8
    ALREADY_USED_IN_THIS_TURN,      // 9
    NOT_ENOUGH_TILES;               // 10

    /**
     * Returns true if the action requires a color
     * @param specialCardRequiredAction : the action to control the requirement of a color
     * @return true if the required action requires a color, otherwise false
     */
    public static boolean isColorChoise(SpecialCardRequiredAction specialCardRequiredAction){
        switch (specialCardRequiredAction){
            case CHOOSE_COLOR_CARD, CHOOSE_COLOR,CHOOSE_COLOR_DINING_ROOM, CHOOSE_COLOR_SCHOOL_ENTRANCE: return true;
            default: return false;
        }
    }
}
