package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.exceptions.LocationNotAllowedException;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.specialCards.SpecialCard;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import it.polimi.ingsw.server.ClientHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GameOrchestrator implementation class in case of the easy game mode.
 * Implements the methods to use the SpecialCards functionalities
 */
public class ExpertGameOrchestrator extends GameOrchestrator {

    protected SpecialCard[] specialCardsArray;
    protected Set<SpecialCardName> specialCards;
    protected SpecialCardRequiredAction expectingPhase;
    protected SpecialCardName activatedSpecialCard;
    protected int numberOfUsedInteractions;
    protected PhaseEnum oldPhase;

    // Color that we have removed from a card or other place and that we are waiting to locate.
    protected Color pendingColor;

    public ExpertGameOrchestrator(List<String> players, int id, List<ClientHandler> clients) {
        super(players, true, id, clients);
        System.out.println("EXPERT GAME ORCHESTRATOR - created simpler orchestrator");
        try {
            this.specialCardsArray = gameBoard.getArrayOfSpecialCard();
            this.specialCards = new HashSet<>(gameBoard.getSetOfSpecialCardNames());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("EXPERT GAME ORCHESTRATOR - Filling cards with students - "+specialCardsArray.length+" cards");
        // Fills the special cards with students if they have any
        for(int i=0;i<specialCardsArray.length;i++) {
            boolean flag = false;
            Color drawnColor = null;
                try {
                    specialCardsArray[i].countStudents();
                    // Adds all the students to the card
                    do {
                        drawnColor = gameBoard.drawFromBag();
                        flag = gameBoard.addStudent(StudentCounter.CARD, drawnColor, i);
                        System.out.println("EXPERT GAME ORCHESTRATOR - Filling card "+i+" "+specialCardsArray[i].getName()+" with student - "+drawnColor+" and is added correctly or full card: "+flag);
                        // If the student cannot be added to the bag is added back to it
                        if (!flag)
                            gameBoard.addStudent(StudentCounter.BAG, drawnColor);
                    }while(flag);
                }
                // If the count is not implemented is because it cannot contain students
                catch (FunctionNotImplementedException e)
                {
                    flag = true;
                }
                catch (LocationNotAllowedException e){
                    e.printStackTrace();
                }

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
                if(getCurrentPhase()!=PhaseEnum.SPECIAL_CARD_USAGE)
                    this.oldPhase = getCurrentPhase();
                this.currentPhase = PhaseEnum.SPECIAL_CARD_USAGE;
                notifyPhaseAndCurrentPlayer();
            } else super.setCurrentPhase(updatePhase);
        }
    }

    private void setSpecialCardPhase(SpecialCardRequiredAction specialCardRequiredAction){
         synchronized (phaseBlocker){
             this.expectingPhase = specialCardRequiredAction;
             if(clients!=null && modelListener!=null)
                 notify(modelListener,MessageGenerator.specialCardUpdatePhaseMessage(PhaseEnum.SPECIAL_CARD_USAGE,specialCardRequiredAction),clients);
         }
    }

    /**
     * Brings back to the nominal turn phase that was set before the Special card usage
     */
    private void resetPhase() {
        System.out.println("EXPERT GAME ORCHESTRATOR - END OF THE SPECIAL CARD USAGE PHASE RESET PHASE TO "+oldPhase);
        synchronized (phaseBlocker) {
            super.setCurrentPhase(this.oldPhase);
            notifyPhaseAndCurrentPlayer();
        }
    }

    /**
     * Uses the special card specified with the String of the name of the card to be used
     *
     * @param cardName : Name of the specialCard to use
     * @return contains the response to the client and the successive information requested or the ok message to end the interaction
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     */
    @Override
    public String useSpecialCard(String cardName) {
        synchronized (actionBlocker) {
            // The special card has been already used in this turn
            if (specialCardAlreadyUsed) {
                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.ALREADY_USED_SPECIAL_CARD, "ERROR - a special card has been already used in this turn");
            }
            try {
                SpecialCardName convertedName = SpecialCardName.convertFromStringToEnum(cardName);
                // Incorrect String of the name
                if (convertedName == null) {
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - wrong name used");
                }
                // Card not contained into the usable specialCards
                if (!specialCards.contains(convertedName)) {
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - no such specialCard");
                }
                numberOfUsedInteractions = 0;
                // Case it is not possible to pay for the card use
                if (gameBoard.getSpecialCardCost(convertedName)==null) {
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - no such specialCard");
                }
                Integer cost = gameBoard.getSpecialCardCost(convertedName);
                if (!gameBoard.paySpecialCard(cost)) {
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_ENOUGH_COINS, "ERROR - not enought coin to activate this special card");
                }
                setCurrentPhase(PhaseEnum.SPECIAL_CARD_USAGE);
                this.activatedSpecialCard = convertedName;
                switch (convertedName) {
                    case PRIEST, PRINCESS:
                        this.specialCardAlreadyUsed = true;
                        setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR_CARD);
                        return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_CARD, false);
                    case JUGGLER:
                        this.specialCardAlreadyUsed = true;
                        setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR_CARD);
                        return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_CARD, true);
                    case CHEESEMAKER:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.professorsUpdateTieEffect();
                        resetPhase();
                        return MessageGenerator.okMessage();
                    case ARCHER:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.disableTowerInfluence();
                        resetPhase();
                        return MessageGenerator.okMessage();
                    case HERALD:
                        this.specialCardAlreadyUsed = true;
                        setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_ISLAND);
                        return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_ISLAND, false);
                    case HERBALIST:
                        if (specialCardsArray[getPositionSpecialCard()].isEmptyOfTiles()) {
                            resetPhase();
                            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.ALL_TILES_USED, "ERROR - The herbalist special card has no usable tiles");
                        }
                        this.specialCardAlreadyUsed = true;
                        setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_ISLAND);
                        return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_ISLAND, false);
                    case POSTMAN:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.increaseMovementMotherNature();
                        resetPhase();
                        return MessageGenerator.okMessage();
                    case KNIGHT:
                        this.specialCardAlreadyUsed = true;
                        this.gameBoard.increaseInfluence();
                        resetPhase();
                        return MessageGenerator.okMessage();
                    case COOKER, GAMBLER:
                        this.specialCardAlreadyUsed = true;
                        setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR);
                        return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR, false);
                    case BARD:
                        this.specialCardAlreadyUsed = true;
                        setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE);
                        return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - Error during the special card usage "+e.toString());
            }
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - wrong name used ");
    }

    /**
     * @return the position of the activated SpecialCard
     */
    private Integer getPositionSpecialCard() {
        for (int i = 0; i < specialCardsArray.length; i++)
            if (specialCardsArray[i].getName().equals(activatedSpecialCard)) return i;
        return null;
    }


    /**
     * Choose a color for the SpecialCard usage specified in the given phase.
     * An interaction for the specialCard usage can require more than one color to be selected, in that case the first one is saved into pendingColor.
     * This method is called after the activation of the card and requires the color which has been chosen by the player and that will be removed/added basing to the specific special card effect
     *
     * @param color : color chosen by the player
     * @return contains the response to the client and the successive information requested or the ok message to end the interaction
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     */
    @Override
    public String chooseColor(Color color) {
        synchronized (actionBlocker) {
            if (!expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR) && !expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE) && !expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD) && !expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM)) {
                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.WRONG_PHASE_ACTION, "ERROR - It is not needed a choose color");
            }
            Integer position = getPositionSpecialCard();

            try {
                switch (this.activatedSpecialCard) {
                    case PRIEST:
                        // Try to remove the student, if not possible error, if possible take the color and update the phase waiting for the Island to put the student
                        if (!gameBoard.removeStudent(StudentCounter.CARD, color, position)) {
                            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_ON_SPECIAL_CARD, "ERROR - the student color specified is not contained on the card, try another color");
                        }
                        pendingColor = color;
                        setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_ISLAND);
                        return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_ISLAND, false);
                    case JUGGLER:
                        // If it is the moment to choose the color to remove from the card tries to remove it and save it in pendingColor
                        if (this.expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD)) {
                            if (!gameBoard.removeStudent(StudentCounter.CARD, color, position)) {
                                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_ON_SPECIAL_CARD, "ERROR - the student color specified is not contained on the card, try another color");
                            }
                            pendingColor = color;
                            setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE);
                            return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE, false);
                        } else {
                            // If it is not the choice of the color from the card but with what color substitute it does the swap
                            if (!gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - the student color specified is not contained on the schoolEntrance, try another color");
                            }
                            // Add the removed student from the card to the schoolEntrance
                            gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, pendingColor);
                            // Add the removed student from the schoolEntrance to the card
                            gameBoard.addStudent(StudentCounter.CARD, pendingColor, position);
                            numberOfUsedInteractions++;
                            // The maximum interactions for this card have been finished, no more swaps allowed.
                            if (numberOfUsedInteractions >= 3) {
                                resetPhase();
                                return MessageGenerator.okMessage();
                            }
                            // Requires another (optional) swap
                            else {
                                setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR_CARD);
                                return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_CARD, true);
                            }
                        }
                    case COOKER:
                        // Disables the influence for the given color
                        gameBoard.disableColorInfluence(color);
                        resetPhase();
                        return MessageGenerator.okMessage();
                    case BARD:
                        // If the phase requires to choose into the schoolEntrance, if the color is removable it removes it and then save it to pendingColor
                        if (expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE)) {
                            if (!gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - the student color specified is not contained on the schoolEntrance, try another color");
                            }
                            pendingColor = color;
                            setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM);
                            return MessageGenerator.specialCardAnswer(SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM, true);
                        } else {
                            // If the phase requires to remove the player from the Dining Room and place the selected.
                            if (!gameBoard.removeStudent(StudentCounter.DININGROOM, color)) {
                                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_DINING_ROOM, "ERROR - the student color specified is not contained on the diningRoom, try another color");
                            }
                            // If the diningRoom is full, the choice have to be done by the choice of the first student, resets the student re-adding them back
                            if (!gameBoard.addStudent(StudentCounter.DININGROOM, pendingColor)) {
                                gameBoard.addStudent(StudentCounter.DININGROOM, color);
                                gameBoard.addStudent(StudentCounter.CARD, pendingColor, position);
                                setSpecialCardPhase(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE);
                                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.DINING_ROOM_COLOR_FULL, "ERROR - the student color chosen cannot be added to the diningRoom since it is full, try another color");
                            }
                            // The student is moved correctly
                            resetPhase();
                            return MessageGenerator.okMessage();
                        }
                    case PRINCESS:
                        // Tries to remove the student from the card and add it to the diningRoom, if ok set an ok message, if not set the proper error message
                        if (gameBoard.removeStudent(StudentCounter.CARD, color, position)) {
                            if (gameBoard.addStudent(StudentCounter.DININGROOM, color)) {
                                gameBoard.addStudent(StudentCounter.CARD, gameBoard.drawFromBag(), position);
                                resetPhase();
                                return MessageGenerator.okMessage();
                            }
                            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.DINING_ROOM_COLOR_FULL, "ERROR - the student color chosen cannot be added to the diningRoom since it is full, try another color");
                        }
                        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_ON_SPECIAL_CARD, "ERROR - the student color specified is not contained on the card, try another color");
                    case GAMBLER:
                        // For each player, removes maximum three students from the diningRoom of the specified color and put them on the bag
                        for (int i = 0; i < players.size(); i++) {
                            for (int j = 0; j < 3; j++) {
                                if (gameBoard.removeStudent(StudentCounter.DININGROOM, color, i))
                                    gameBoard.addStudent(StudentCounter.BAG, color);
                            }
                        }
                        resetPhase();
                        return MessageGenerator.okMessage();
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - error in choose color of the student in special card usage");
            }
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - error in choose color of the student in special card usage");
    }


    /**
     * Choose an Island for the SpecialCard usage
     *
     * @param position : position of the chosen island
     * @return the required action for the card usage or the result of the action, needed to model the interaction with the client
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     * @throws IslandOutOfBoundException       if the island position is incorrect
     */
    @Override
    public String chooseIsland(int position) throws FunctionNotImplementedException, IslandOutOfBoundException {
        synchronized (actionBlocker) {
            if (!expectingPhase.equals(SpecialCardRequiredAction.CHOOSE_ISLAND)) {
                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.WRONG_PHASE_ACTION, "ERROR - It is not needed a choose color");
            }
            Integer cardPosition = getPositionSpecialCard();
            try {
                switch (activatedSpecialCard) {
                    case PRIEST:
                        // Add the student chosen first and add it to the island, then draw a new student for the card
                        gameBoard.addStudent(StudentCounter.ISLAND, pendingColor, position);
                        gameBoard.addStudent(StudentCounter.CARD, gameBoard.drawFromBag(), cardPosition);
                        resetPhase();
                        return MessageGenerator.okMessage();
                    case HERALD:
                        // Computes the influence on the island
                        gameBoard.computeInfluence(position);
                        resetPhase();
                        return MessageGenerator.okMessage();
                    case HERBALIST:
                        // Disables an Island influence computation and remove the tile from the special card
                        if (gameBoard.disableInfluence(position)) {
                            for(SpecialCard specialCard: specialCardsArray)
                                if(specialCard.getName().equals(SpecialCardName.HERBALIST))
                                    specialCard.removeTile();
                        }
                        resetPhase();
                        return MessageGenerator.okMessage();
                }
            }
            catch (IslandOutOfBoundException exc){
                return MessageGenerator.errorIslandOutOfBoundInputMessage("ERROR - Island out of bound",exc.getLowerBound(),exc.getHigherBound());
            }
            catch (LocationNotAllowedException exc){
                exc.printStackTrace();
            }
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - error in choose color of the student in special card usage");
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
