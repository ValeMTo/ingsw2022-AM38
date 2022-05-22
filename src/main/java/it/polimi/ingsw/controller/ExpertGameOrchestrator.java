package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
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
        synchronized (actionBlocker) {
            SpecialCardName convertedName = SpecialCardName.convertFromStringToEnum(cardName);
            if (convertedName == null) {
                nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - wrong name used");
                return SpecialCardRequiredAction.NO_SUCH_CARD;
            }
            if (!specialCards.contains(convertedName)) {
                nextRequest = MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD, "ERROR - no such specialCard");
                return SpecialCardRequiredAction.NO_SUCH_CARD;
            }
            if (cardName.equalsIgnoreCase(SpecialCardName.PRIEST.toString())) {
                if(!gameBoard.)
                return SpecialCardRequiredAction.CHOOSE_COLOR_CARD;
            }
        }
    }

}
