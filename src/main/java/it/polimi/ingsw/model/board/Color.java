package it.polimi.ingsw.model.board;

public enum Color {
    BLUE, GREEN, YELLOW, PINK, RED;

    public static Color toColor(String colorString){
        for(Color color:Color.values())
            if(colorString.equalsIgnoreCase(color.name()))
                return color;
        return null;
    }
}
