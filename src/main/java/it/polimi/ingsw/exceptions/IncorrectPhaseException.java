package it.polimi.ingsw.exceptions;

import it.polimi.ingsw.controller.PhaseEnum;

/**
 * Exception thrown when the action is not allowed since the phase is incorrect.
 * With the getActualPhase method allow to know the true current Phase
 */
public class IncorrectPhaseException extends Exception{
    private PhaseEnum actualPhase;
    public IncorrectPhaseException(PhaseEnum actualPhase){
        this.actualPhase = actualPhase;
    }

    /**
     * Returns the current phase
     * @return a PhaseEnum of the actual currentPhase
     */
    public PhaseEnum getActualPhase() {
        return actualPhase;
    }
}
