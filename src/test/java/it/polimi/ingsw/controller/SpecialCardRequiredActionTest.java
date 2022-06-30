package it.polimi.ingsw.controller;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;

public class SpecialCardRequiredActionTest {

    @ParameterizedTest
    @EnumSource(SpecialCardRequiredAction.class)
    public void testIsColor(SpecialCardRequiredAction specialCard){
        switch (specialCard)
        {
            case CHOOSE_COLOR_CARD, CHOOSE_COLOR, CHOOSE_COLOR_DINING_ROOM, CHOOSE_COLOR_SCHOOL_ENTRANCE: assertTrue(SpecialCardRequiredAction.isColorChoise(specialCard)); break;
            default: assertFalse(SpecialCardRequiredAction.isColorChoise(specialCard));
        }
    }
}
