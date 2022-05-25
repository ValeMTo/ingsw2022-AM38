package it.polimi.ingsw.model.board;

import java.util.Locale;

public enum Tower {
    WHITE,
    BLACK,
    GRAY;

    public static Tower toTower(String towerName){
        towerName = towerName.toLowerCase(Locale.ROOT);
        if (towerName.equals("white")){
            return Tower.WHITE;
        } else if (towerName.equals("black")){
            return Tower.BLACK;
        } else if(towerName.equals("gray")){
            return Tower.GRAY;
        }
        return null;
    }
}
