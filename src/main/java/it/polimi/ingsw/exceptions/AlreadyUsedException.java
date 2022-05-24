package it.polimi.ingsw.exceptions;

import java.util.HashSet;
import java.util.Set;

public class AlreadyUsedException extends Exception {
    private final Set<Integer> usableIndexes = new HashSet<>();

    public AlreadyUsedException(Set<Integer> usableIndexes) {
        this.usableIndexes.addAll(usableIndexes);
    }

    /**
     * Returns the usable positions
     *
     * @return : the list of available positions
     */
    public Set<Integer> getUsableIndexes() {
        return this.usableIndexes;
    }
}
