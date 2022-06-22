package it.polimi.ingsw.controller;

public enum SpecialCardRequiredAction {
    NO_SUCH_CARD,
    NOT_ENOUGH_COINS,
    CHOOSE_COLOR,
    CHOOSE_ISLAND,
    CHOOSE_COLOR_CARD,
    CHOOSE_COLOR_DINING_ROOM,
    CHOOSE_COLOR_SCHOOL_ENTRANCE,
    USED_CORRECTLY,
    NO_SUCH_COLOR,
    ALREADY_USED_IN_THIS_TURN,
    NOT_ENOUGH_TILES;

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
