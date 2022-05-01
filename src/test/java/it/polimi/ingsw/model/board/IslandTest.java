package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.board.Tower;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class IslandTest {
    Island island = new Island(1);

    /**
     * Support class for have the Stream of possibles Tower's colors
     *
     * @return a stream of all the Tower's colors
     */
    public static Stream<Tower> supplyTowerColors() {
        return Stream.of(Tower.GRAY, Tower.BLACK, Tower.WHITE);
    }

    /**
     * Test for assure the initialization of island is correct
     * and that isTaken() changes collocating a Tower
     */
    @Test
    @DisplayName("isTaken test initialize and then set")
    public void testIsTaken() {
        assertFalse(island.isTaken());
        island.setTower(Tower.GRAY);
        assertTrue(island.isTaken());
    }

    /**
     * Test for assure that the initialization of island is correct
     * and for testing isInfluenceEnabled() after trying to disable and then re-enable the influence
     */
    @Test
    @DisplayName("InfluenceEnable initial value and then disable and enable")
    public void testInfluenceEnable() {
        assertTrue(island.isInfluenceEnabled());
        island.disableInfluence();
        assertFalse(island.isInfluenceEnabled());
        island.enableInfluence();
        assertTrue(island.isInfluenceEnabled());
    }

    /**
     * Second test for InfluenceEnabled, test first enabling the influence
     * then test enable another time and then it disable the influence two times
     */
    @DisplayName("InfluenceEnable with two enables and then disable")
    @Test
    public void testInfluenceEnable2() {
        island.enableInfluence();
        assertTrue(island.isInfluenceEnabled());
        island.enableInfluence();
        assertTrue(island.isInfluenceEnabled());
        island.disableInfluence();
        assertFalse(island.isInfluenceEnabled());
        island.disableInfluence();
        assertFalse(island.isInfluenceEnabled());
    }

    /**
     * Adds the students using their parameters
     *
     * @param b for blue students
     * @param y for yellow students
     * @param r for red students
     * @param p for pink students
     * @param g for green students
     */
    public void adder(int b, int y, int r, int p, int g) {
        for (int i = 0; i < b; i++)
            island.addStudent(Color.BLUE);
        for (int i = 0; i < y; i++)
            island.addStudent(Color.YELLOW);
        for (int i = 0; i < r; i++)
            island.addStudent(Color.RED);
        for (int i = 0; i < p; i++)
            island.addStudent(Color.PINK);
        for (int i = 0; i < g; i++)
            island.addStudent(Color.GREEN);
    }

    /**
     * ParameterizedTest for the control of the addition of Student and verifying that their number is correct
     *
     * @param b : BLUE students to add
     * @param y : YELLOW students to add
     * @param r : RED students to add
     * @param p : PINK students to add
     * @param g : GREEN students to add
     */
    @DisplayName("Add students and student number control")
    @ParameterizedTest
    @CsvSource({"0,1,2,3,4,5", "1,0,0,0,0", "0,0,0,0,0", "15,14,13,13,12"})
    public void adderAndGet(int b, int y, int r, int p, int g) {
        adder(b, y, r, p, g);
        assertEquals(b, island.studentNumber(Color.BLUE));
        assertEquals(y, island.studentNumber(Color.YELLOW));
        assertEquals(r, island.studentNumber(Color.RED));
        assertEquals(p, island.studentNumber(Color.PINK));
        assertEquals(g, island.studentNumber(Color.GREEN));
    }

    /**
     * Test for the initial value of the towers in the Island
     */
    @Test
    @DisplayName("Tower number test without set towers")
    public void towerNumber() {
        assertEquals(0, island.getTowerNumber());
    }

    /**
     * Parameterized test for the set method of the towers number and verifying it is correctly set
     *
     * @param towerNum : new tower number to set
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5})
    @DisplayName("Tower Number after set test")
    public void towerNumber(int towerNum) {
        island.setTowerNumber(towerNum);
        assertEquals(towerNum, island.getTowerNumber());
    }

    @Test
    public void towerSetting() {
        Tower[] towers = Tower.values();
        for (int i = 0; i < towers.length; i++) {
            island.setTower(towers[i]);
            assertEquals(towers[i], island.getTower());
        }
    }

    /**
     * Test for the initial null value of the Tower in the Island
     */
    @Test
    @DisplayName("Empty Tower Color Test")
    public void tower() {
        assertNull(island.getTower());
    }

    /**
     * ParameterizedTest for the set and get of the Towers color
     *
     * @param towerColor
     */
    @DisplayName("All tower colors test")
    @ParameterizedTest
    @MethodSource("supplyTowerColors")
    public void tower(Tower towerColor) {
        island.setTower(towerColor);
        assertEquals(island.getTower(), towerColor);
    }

    /**
     * Test the various condition of the influence enable / disable and initialization
     * of the flag to enable the influence computation
     */
    @Test
    @DisplayName("Test disable and enable influence")
    public void enableAndDisableInfluence() {
        assertTrue(island.isInfluenceEnabled());
        island.disableInfluence();
        assertFalse(island.isInfluenceEnabled());
        island.enableInfluence();
        assertTrue(island.isInfluenceEnabled());
    }

    /**
     * Verify that the position is initializated well and that the set and get work fine
     *
     * @param position
     */
    @ParameterizedTest
    @DisplayName("Test the Island positioning set and get")
    @ValueSource(ints = {0, 1, 2, 3, 12})
    public void setAndGetPositioning(int position) {
        assertEquals(island.getPosition(), 1); //initialization
        island.setPosition(position);
        assertEquals(island.getPosition(), position);
    }


}
