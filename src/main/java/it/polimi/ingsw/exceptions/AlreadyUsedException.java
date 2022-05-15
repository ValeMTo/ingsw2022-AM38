package it.polimi.ingsw.exceptions;

import java.util.ArrayList;
import java.util.List;

public class AlreadyUsedException extends Exception {
    private List<Integer> usableIndexes = new ArrayList<>();

    public AlreadyUsedException(List<Integer> usableIndexes){
        this.usableIndexes.addAll(usableIndexes);
    }

    /**
     * Returns the usable positions
     * @return : the list of available positions
     */
    public List<Integer> getUsableIndexes(){
        return this.usableIndexes;
    }
}
