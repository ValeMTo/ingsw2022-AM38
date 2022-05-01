package it.polimi.ingsw.model.specialCards;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HerbalistTest {

    SpecialCard herbalist = new Herbalist();
    SpecialCard noHerbalist = new SpecialCard(SpecialCardName.GAMBLER);

    /**
     * Test the increase of price.
     * The price gets higher only for the first use.
     */
    @Test
    public void increasePriceTest() {
        assertEquals(2, herbalist.getCostCoin());
        herbalist.use();
        assertEquals(3, herbalist.getCostCoin());
        herbalist.use();
        assertEquals(3, herbalist.getCostCoin());

    }

    /**
     * Test empty students methods for no students special cards.
     */
    @Test
    public void studentsExceptionMethodTest() {
        assertThrows(FunctionNotImplementedException.class, () -> herbalist.addStudent(Color.BLUE), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> herbalist.removeStudent(Color.BLUE), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> herbalist.countStudents(Color.BLUE), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> herbalist.countStudents(), "Not usable method for this card: ");
    }

    /**
     * Test to get the number of tiles, without using any of it
     */
    @Test
    public void getTileTest() {
        try {
            assertEquals(4, herbalist.getNumberOfEntryTiles());
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test the emptyness of herbalist card
     */
    @Test
    public void isEmptyTest() {

        try {

            for (int i = 0; i < 4; i++) {
                assertFalse(herbalist.isEmptyOfTiles());
                assertTrue(herbalist.removeTile());
            }
            assertTrue(herbalist.isEmptyOfTiles());
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to get the number of tiles, when there are not
     */
    @Test
    public void getTileEmptyTest() {

        try {
            for (int i = 0; i < 4; i++) {
                assertTrue(herbalist.removeTile());
            }
            assertEquals(0, herbalist.getNumberOfEntryTiles());
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to remove more ties than there are
     */
    @Test
    public void removeMoreTilesTest() {

        try {
            for (int i = 0; i <4; i++) {
                assertTrue(herbalist.removeTile());
            }
            for (int i=0; i<4; i++){
                assertFalse(herbalist.removeTile());
            };
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to remove more ties than there are
     */
    @Test
    public void returnTileTest() {

        try {
            for (int i = 0; i < 4; i++) {
                assertTrue(herbalist.removeTile());
            }
            for (int i=0; i<4; i++){
                assertTrue(herbalist.returnTile());
            }
            assertEquals(4, herbalist.getNumberOfEntryTiles());
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to remove more ties than there are
     */
    @Test
    public void addTilesOverTheLimitTest() {

        try {
            for (int i = 0; i < 4; i++) {
                assertFalse(herbalist.returnTile());
            }
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test for empty Tiles methods for no herbalist cards.
     */
    @Test
    public void herbalistExceptionMethodTest() {
        assertThrows(FunctionNotImplementedException.class, () -> noHerbalist.returnTile(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> noHerbalist.removeTile(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> noHerbalist.getNumberOfEntryTiles(), "Not usable method for this card: ");
        assertThrows(FunctionNotImplementedException.class, () -> noHerbalist.isEmptyOfTiles(), "Not usable method for this card: ");
    }


}
