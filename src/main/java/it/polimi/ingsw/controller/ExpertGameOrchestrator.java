package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
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

    public ExpertGameOrchestrator(List<String> players) {
        super(players, true);
        try {
            this.specialCards = new HashSet<>(gameBoard.getSetOfSpecialCardNames());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public SpecialCardRequiredAction useSpecialCard(String cardName, String nextRequest) {
        //TODO: special card already used in this turn
        synchronized (actionBlocker) {
            try {
                SpecialCardName convertedName = SpecialCardName.convertFromStringToEnum(cardName);
                if (convertedName == null) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - wrong name used");
                    return SpecialCardRequiredAction.NO_SUCH_CARD;
                }
                if (!specialCards.contains(convertedName)) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - no such specialCard");
                    return SpecialCardRequiredAction.NO_SUCH_CARD;
                }
                Integer cost = null;
                if (!gameBoard.getSpecialCardCost(convertedName, cost)) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - no such specialCard");
                    return SpecialCardRequiredAction.NO_SUCH_CARD;
                }
                if (!gameBoard.paySpecialCard(cost)) {
                    nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_ENOUGH_COINS, "ERROR - not enought coin to activate this special card");
                    return SpecialCardRequiredAction.NOT_ENOUGH_COINS;
                }
                switch (convertedName) {
                    case PRIEST:
                        return SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
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
}
