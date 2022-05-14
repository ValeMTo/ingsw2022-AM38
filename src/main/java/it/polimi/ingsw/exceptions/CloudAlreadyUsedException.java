package it.polimi.ingsw.exceptions;

import java.util.ArrayList;
import java.util.List;

public class CloudAlreadyUsedException extends Exception {
    private List<Integer> usableClouds = new ArrayList<>();

    public CloudAlreadyUsedException(List<Integer> usableClouds){
        this.usableClouds.addAll(usableClouds);
    }

    /**
     * Returns the usable clouds' positions
     * @return : the list of available cloud positions
     */
    public List<Integer> getUsableClouds(){
        return this.usableClouds;
    }
}
