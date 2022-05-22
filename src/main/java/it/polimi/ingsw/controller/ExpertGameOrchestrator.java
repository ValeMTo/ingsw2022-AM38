package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.specialCards.SpecialCardName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GameOrchestrator implementation class in case of the easy game mode.
 * Implements the methods to use the SpecialCards functionalities
 */
public class ExpertGameOrchestrator extends GameOrchestrator {

    private SpecialCardName[] specialCardsArray;
    private Set<SpecialCardName> specialCards;
    private SpecialCardRequiredAction expectingPhase;
    private SpecialCardName activatedSpecialCard;
    private int numberOfUsedInteractions;
    private PhaseEnum oldPhase;

    // Color that we have removed from a card or other place and that we are waiting to locate.
    private Color pendingColor;

    public ExpertGameOrchestrator(List<String> players) {
        super(players, true);
        try {
            this.specialCardsArray = gameBoard.getArrayOfSpecialCardNames();
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

    /**
     * Uses the special card specified with the String of the name of the card to be used
     *
     * @param cardName    : Name of the specialCard to use
     * @param nextRequest : if the interaction with the client continues, contains the response to the client and the successive information requested
     * @return the required action for the card usage or the result of the action, needed to model the interaction with the client
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     */
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
                    case PRIEST, PRINCESS:
                        this.specialCardAlreadyUsed = true;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_CARD, false);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                        return SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                    case JUGGLER:
                        this.specialCardAlreadyUsed = true;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_CARD, true);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                        return SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                    case CHEESEMAKER:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.professorsUpdateTieEffect();
                        nextRequest = MessageGenerator.okMessage();
                        resetPhase();
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
                        resetPhase();
                        return SpecialCardRequiredAction.USED_CORRECTLY;
                    case KNIGHT:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.increaseInfluence();
                        nextRequest = MessageGenerator.okMessage();
                        resetPhase();
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

    /**
     * Choose a color for the SpecialCard usage specified in the given phase.
     * An interaction for the specialCard usage can require more than one color to be selected, in that case the first one is saved into pendingColor.
     * This method is called after the activation of the card and requires the color which has been chosen by the player and that will be removed/added basing to the specific special card effect
     *
     * @param color       : color chosen by the player
     * @param nextRequest : if the interaction with the client continues, contains the response to the client and the successive information requested
     * @return the required action for the card usage or the result of the action, needed to model the interaction with the client
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     */
    @Override
    public SpecialCardRequiredAction chooseColor(Color color, String nextRequest) {
        if (expectingPhase != SpecialCardRequiredAction.CHOOSE_COLOR && expectingPhase != SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE && expectingPhase != SpecialCardRequiredAction.CHOOSE_COLOR_CARD && expectingPhase != SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM)
            nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.WRONG_PHASE_ACTION, "ERROR - It is not needed a choose color");
        Integer position = null;
        for (int i = 0; i < specialCardsArray.length; i++)
            if (specialCardsArray[i].equals(activatedSpecialCard)) position = i;
        try {
            switch (this.activatedSpecialCard) {
                case PRIEST:
                    // Try to remove the student, if not possible error, if possible take the color and update the phase waiting for the Island to put the student
                    if (!gameBoard.removeStudent(StudentCounter.CARD, color, position)) {
                        nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_ON_SPECIAL_CARD, "ERROR - the student color specified is not contained on the card, try another color");
                        return this.expectingPhase;
                    }
                    pendingColor = color;
                    nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_ISLAND, false);
                    this.expectingPhase = SpecialCardRequiredAction.CHOOSE_ISLAND;
                    return SpecialCardRequiredAction.CHOOSE_ISLAND;
                case JUGGLER:
                    // If it is the moment to choose the color to remove from the card tries to remove it and save it in pendingColor
                    if (this.expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD)) {
                        if (!gameBoard.removeStudent(StudentCounter.CARD, color, position)) {
                            nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_ON_SPECIAL_CARD, "ERROR - the student color specified is not contained on the card, try another color");
                            return this.expectingPhase;
                        }
                        pendingColor = color;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE, false);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE;
                        return SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE;
                    } else {
                        // If it is not the choice of the color from the card but with what color substitute it does the swap
                        if (!gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                            nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - the student color specified is not contained on the schoolEntrance, try another color");
                            return this.expectingPhase;
                        }
                        // Add the removed student from the card to the schoolEntrance
                        gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, pendingColor);
                        // Add the removed student from the schoolEntrance to the card
                        gameBoard.addStudent(StudentCounter.CARD, pendingColor, position);
                        numberOfUsedInteractions++;
                        // The maximum interactions for this card have been finished, no more swaps allowed.
                        if (numberOfUsedInteractions >= 3) {
                            resetPhase();
                            nextRequest = MessageGenerator.okMessage();
                            return SpecialCardRequiredAction.USED_CORRECTLY;
                        }
                        // Requires another (optional) swap
                        else {
                            nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_CARD, true);
                            this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                            return SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
                        }
                    }
                case COOKER:
                    // Disables the influence for the given color
                    gameBoard.disableColorInfluence(color);
                    resetPhase();
                    nextRequest = MessageGenerator.okMessage();
                    return SpecialCardRequiredAction.USED_CORRECTLY;
                case BARD:
                    // If the phase requires to choose into the schoolEntrance, if the color is removable it removes it and then save it to pendingColor
                    if (expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE)) {
                        if (!gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                            nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - the student color specified is not contained on the schoolEntrance, try another color");
                            return this.expectingPhase;
                        }
                        pendingColor = color;
                        nextRequest = MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM, false);
                        this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM;
                        return SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM;
                    } else {
                        // If the phase requires to remove the player from the Dining Room and place the selected.
                        if (!gameBoard.removeStudent(StudentCounter.DININGROOM, color)) {
                            nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_DINING_ROOM, "ERROR - the student color specified is not contained on the diningRoom, try another color");
                            return this.expectingPhase;
                        }
                        // If the diningRoom is full, the choice have to be done by the choice of the first student, resets the student re-adding them back
                        if (!gameBoard.addStudent(StudentCounter.DININGROOM, pendingColor)) {
                            nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.DINING_ROOM_COLOR_FULL, "ERROR - the student color chosen cannot be added to the diningRoom since it is full, try another color");
                            gameBoard.addStudent(StudentCounter.DININGROOM, color);
                            gameBoard.addStudent(StudentCounter.CARD, pendingColor, position);
                            this.expectingPhase = SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE;
                            return this.expectingPhase;
                        }
                        // The student is moved correctly
                        resetPhase();
                        nextRequest = MessageGenerator.okMessage();
                        return SpecialCardRequiredAction.USED_CORRECTLY;
                    }
                case PRINCESS:
                    // Tries to remove the student from the card and add it to the diningRoom, if ok set an ok message, if not set the proper error message
                    if (gameBoard.removeStudent(StudentCounter.CARD, color, position)) {
                        if (gameBoard.addStudent(StudentCounter.DININGROOM, color)) {
                            gameBoard.addStudent(StudentCounter.CARD, gameBoard.drawFromBag(), position);
                            resetPhase();
                            nextRequest = MessageGenerator.okMessage();
                            return SpecialCardRequiredAction.USED_CORRECTLY;
                        }
                        nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.DINING_ROOM_COLOR_FULL, "ERROR - the student color chosen cannot be added to the diningRoom since it is full, try another color");
                        return this.expectingPhase;
                    }
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_ON_SPECIAL_CARD, "ERROR - the student color specified is not contained on the card, try another color");
                    return this.expectingPhase;
                case GAMBLER:
                    // For each player, removes maximum three students from the diningRoom of the specified color and put them on the bag
                    for (int i = 0; i < players.size(); i++) {
                        for (int j = 0; j < 3; j++) {
                            if (gameBoard.removeStudent(StudentCounter.DININGROOM, color, i))
                                gameBoard.addStudent(StudentCounter.BAG, color);
                        }
                    }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - error in choose color of the student in special card usage");
        }
        return this.expectingPhase;
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
