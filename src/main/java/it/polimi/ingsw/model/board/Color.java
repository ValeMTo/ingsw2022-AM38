package it.polimi.ingsw.model.board;

public enum Color {
    BLUE, GREEN, YELLOW, PINK, RED;

    /**
     * Returns the color from a string
     * @param colorString : String to parse into a Color enum value
     * @return the Color enum value or null if the String does not correspond to any color name
     */
    public static Color toColor(String colorString){
        for(Color color:Color.values())
            if(colorString.equalsIgnoreCase(color.name())||colorString.equalsIgnoreCase(getAbbreviation(color)))
                return color;
        return null;
    }


    /**
     * Returns the abbreviation given the color
     * @return
     */
    public static String getAbbreviation(Color color){
        switch (color) {
            case YELLOW: return "Y";
            case BLUE: return "B";
            case RED: return "R";
            case GREEN: return "G";
            case PINK: return "P";
            default: return "";
        }
    }

}
