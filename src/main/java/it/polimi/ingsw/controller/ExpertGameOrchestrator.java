package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.specialCards.SpecialCardName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GameOrchestrator implementation class in case of the easy game mode.
 * Implements the methods to use the SpecialCards functionalities
 */
public class ExpertGameOrchestrator extends GameOrchestrator {

    private Set<SpecialCardName> specialCards;
    private SpecialCardRequiredAction expectingPhase;
    private SpecialCardName activatedSpecialCard;
    private int numberOfUsedInteractions;
    private PhaseEnum oldPhase;

    public ExpertGameOrchestrator(List<String> players) {
        super(players, true);
        try {
            this.specialCards = new HashSet<>(gameBoard.getSetOfSpecialCardNames());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    /**
     * Sets the current phase as a new one.
     *
     * @param updatePhase : new phase to update
     */ protected void setCurrentPhase(PhaseEnum updatePhase) {
        synchronized (phaseBlocker) {
            if (updatePhase.equals(PhaseEnum.SPECIAL_CARD_USAGE)) {
                this.oldPhase = getCurrentPhase();
                this.currentPhase = PhaseEnum.SPECIAL_CARD_USAGE;
            } else super.setCurrentPhase(updatePhase);
        }
    }

    /**
     * Brings back to the nominal turn phase that was set before the Special card usage
     */
    private void resetPhase() {
        synchronized (phaseBlocker) {
            setCurrentPhase(this.oldPhase);
        }
    }

    @Override
    public SpecialCardRequiredAction useSpecialCard(String cardName, String nextRequest) {
        synchronized (actionBlocker) {
            // The special card has been already used in this turn
            if (specialCardAlreadyUsed) {
                nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.ALREADY_USED_SPECIAL_CARD, "ERROR - a special card has been already used in this turn");
                return SpecialCardRequiredAction.ALREADY_USED_IN_THIS_TURN;
            }
            try {
                SpecialCardName convertedName = SpecialCardName.convertFromStringToEnum(cardName);
                // Incorrect String of the name
                if (convertedName == null) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - wrong name used");
                    return SpecialCardRequiredAction.NO_SUCH_CARD;
                }
                // Card not contained into the usable specialCards
                if (!specialCards.contains(convertedName)) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - no such specialCard");
                    return SpecialCardRequiredAction.NO_SUCH_CARD;
                }
                numberOfUsedInteractions = 0;
                Integer cost = null;
                // Case it is not possible to pay for the card use
                if (!gameBoard.getSpecialCardCost(convertedName, cost)) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - no such specialCard");
                    return SpecialCardRequiredAction.NO_SUCH_CARD;
                }
                if (!gameBoard.paySpecialCard(cost)) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_ENOUGH_COINS, "ERROR - not enought coin to activate this special card");
                    return SpecialCardRequiredAction.NOT_ENOUGH_COINS;
                }
                setCurrentPhase(PhaseEnum.SPECIAL_CARD_USAGE);
                this.activatedSpecialCard = convertedName;
                switch (convertedName) {
                    case PRIEST, JUGGLER, PRINCESS:
                        this.specialCardAlreadyUsed = true;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_CARD, false);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                        return SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                    case CHEESEMAKER:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.professorsUpdateTieEffect();
                        nextRequest = MessageGenerator.okMessage();
                        return SpecialCardRequiredAction.USED_CORRECTLY;
                    case HERALD, ARCHER, HERBALIST:
                        this.specialCardAlreadyUsed = true;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_ISLAND, false);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_ISLAND;
                        return SpecialCardRequiredAction.CHOOSE_ISLAND;
                    case POSTMAN:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.increaseMovementMotherNature();
                        nextRequest = MessageGenerator.okMessage();
                        return SpecialCardRequiredAction.USED_CORRECTLY;
                    case KNIGHT:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.increaseInfluence();
                        nextRequest = MessageGenerator.okMessage();
                        return SpecialCardRequiredAction.USED_CORRECTLY;
                    case COOKER, GAMBLER:
                        this.specialCardAlreadyUsed = true;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR, false);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR;
                        return SpecialCardRequiredAction.CHOOSE_COLOR;
                    case BARD:
                        this.specialCardAlreadyUsed = true;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE, false);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE;
                        return SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE;
                }
            } catch (Exception e) {
                nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - Error during the special card usage");
                return SpecialCardRequiredAction.NOT_ENOUGH_COINS;
            }
        }
        nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - wrong name used");
        return SpecialCardRequiredAction.NO_SUCH_CARD;
    }

    //TODO:Implement
    @Override
    public SpecialCardRequiredAction chooseColor(Color color, String nextRequest) throws FunctionNotImplementedException {
        return null;
    }

    //TODO: Implement
    @Override
    public SpecialCardRequiredAction chooseIsland(int position, String nextRequest) throws FunctionNotImplementedException, IslandOutOfBoundException {
        return null;
    }

    @Override
    public boolean moveMotherNature(int destinationIsland) throws IslandOutOfBoundException, IncorrectPhaseException {
        boolean returnValue = super.moveMotherNature(destinationIsland);
        // If the motherNature is moved correctly, computes the influence
        if (returnValue) {
            gameBoard.computeInfluence(destinationIsland);
        }
        return returnValue;
    }
}
