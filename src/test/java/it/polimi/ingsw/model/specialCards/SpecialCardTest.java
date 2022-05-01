package it.polimi.ingsw.model.specialCards;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SpecialCardTest {

    private final SpecialCard priest = new SpecialCard(SpecialCardName.PRIEST);
    private final SpecialCard herbalist = new SpecialCard(SpecialCardName.HERBALIST);
    private final SpecialCard gambler = new SpecialCard(SpecialCardName.GAMBLER);

    /**
     * Test the increase of price.
     * The price gets higher only for the first use.
     */
    @Test
    public void increasePriceTest() {
        assertEquals(1, priest.getCostCoin());
        priest.use();
        assertEquals(2, priest.getCostCoin());
        priest.use();
        assertEquals(2, priest.getCostCoin());

        assertEquals(2, herbalist.getCostCoin());
        herbalist.use();
        assertEquals(3, herbalist.getCostCoin());
        herbalist.use();
        assertEquals(3, herbalist.getCostCoin());

        assertEquals(3, gambler.getCostCoin());
        gambler.use();
        assertEquals(4, gambler.getCostCoin());
        gambler.use();
        assertEquals(4, gambler.getCostCoin());
    }

    /**
     * Test for empty Tiles methods for no herbalist cards.
     */
    @Test
    public void herbalistExceptionMethodTest() {
        assertThrows(FunctionNotImplementedException.class, () -> priest.returnTile(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.removeTile(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.getNumberOfEntryTiles(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.isEmptyOfTiles(), "Not usable method for this card: ");
    }

    /**
     * Test empty students methods for no students special cards.
     */
    @Test
    public void studentsExceptionMethodTest() {
        assertThrows(FunctionNotImplementedException.class, () -> priest.addStudent(Color.BLUE), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.removeStudent(Color.BLUE), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.countStudents(Color.BLUE), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.countStudents(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.getGuestsChangeLimit(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> priest.getGuestsLimit(), "Not usable method for this card: ");
    }


}
