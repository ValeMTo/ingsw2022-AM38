package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerBoardTest {

    /**
     * Use only one assistant card.
     * Check the no-usability.
     * Use Easy Constructor.
     *
     * @param numCard : number of assistant card
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void useAssistantCardTest(int numCard) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8);
        assertTrue(player.useAssistantCard(numCard));
        assertFalse(player.useAssistantCard(numCard));

    }

    /**
     * Use all assistant card.
     * Check the no-usability.
     * Use Expert Constructor.
     *
     * @param numCard : number of assistant card
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void useAssistantCardExpertTest(int numCard) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6, 2);
        assertTrue(player.useAssistantCard(numCard));
        assertFalse(player.useAssistantCard(numCard));

    }

    /**
     * Get the last Assistant Card number used, without having using a card
     */
    @Test
    public void getLastCardNotTakenTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        NotLastCardUsedException thrown = assertThrows(NotLastCardUsedException.class, () -> player.getLastCard(), "It is the first round. No card can be take before");
    }

    /**
     * Get the last Assistant Card number used.
     *
     * @param numCard : number of assistant card
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void getLastCardTest(int numCard) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        player.useAssistantCard(numCard);
        try {
            assertEquals(numCard, player.getLastCard());
        } catch (NotLastCardUsedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Show all cards available after using one
     *
     * @param numCard : number of assistant card
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void getUsableCardTest(int numCard) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        ArrayList<Integer> cleanCards = new ArrayList<Integer>();
        player.useAssistantCard(numCard);

        for (int i = 0; i < cleanCards.size(); i++) {
            if (i != numCard) {
                assertEquals(player.showUsableCards().get(i), cleanCards.get(i));
            }
        }
    }

    /**
     * Show all cards available, when they aren't
     */
    @Test
    public void getNoUsableCardTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        for (int i = 1; i <= 10; i++) {
            player.useAssistantCard(i);
        }
        assertEquals(0, player.showUsableCards().size());

    }

    /**
     * Decrease the amount of money of less the concrete amount.
     */
    @Test
    public void payOneTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        assertTrue(player.pay(1));

    }

    /**
     * Decrease the amount of money of more the concrete amount.
     */
    @Test
    public void payMoreTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        assertFalse(player.pay(3));

    }

    /**
     * Decrease the amount of money of the same concrete amount.
     */
    @Test
    public void payEqualTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        assertTrue(player.pay(2));

    }

    /**
     * Increase the amount of money of a certain amount
     *
     * @param coin : amount of coin
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void receiveMoneyTest(int coin) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 8, 2);
        for (int i = 0; i < coin; i++) {
            player.increaseCoinBudget();
        }
        assertEquals(player.getCoin(), 2 + coin);

    }

    /**
     * Check Tower Color
     */
    @Test
    public void checkTowerColorTest() {
        PlayerBoard player;
        for (Tower color : Tower.values()) {
            player = new PlayerBoard("Valeria", color, 6);
            assertEquals(player.getTowerColor(), color);
        }

    }

    /**
     * Test for the superior limit of the number of students to add in the entrance
     *
     * @param studentsToAdd : num of students(>10 to test that from the eleventh are not added)
     * @param color         : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"10,BLUE", "5,BLUE", "12,RED", "9,YELLOW"})
    public void adderStudentEntranceTest(int studentsToAdd, Color color) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertEquals(0, player.countStudentsEntrance(color));
        for (int i = 0; i < studentsToAdd; i++) {
            if (i < 9) {
                assertTrue(player.addStudentEntrance(color));
                assertEquals(i + 1, player.countStudentsEntrance(color));
            } else {
                assertFalse(player.addStudentEntrance(color));
                assertEquals(9, player.countStudentsEntrance(color));
            }
        }
    }

    /**
     * Test for adding in the diningRoom
     *
     * @param studentsToAdd : num of students(>10 to test that from the eleventh are not added)
     * @param color         : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"18,BLUE", "5,BLUE", "12,RED", "9,YELLOW"})
    public void adderStudentDiningRoomTest(int studentsToAdd, Color color) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertEquals(0, player.countStudentsEntrance(color));
        for (int i = 0; i < studentsToAdd; i++) {
            if (i < 10) {
                assertTrue(player.addStudentDiningRoom(color));
                assertEquals(i + 1, player.countStudentsDiningRoom(color));

            } else {
                assertFalse(player.addStudentDiningRoom(color));
                assertEquals(10, player.countStudentsDiningRoom(color));
            }
        }
    }

    /**
     * Test for the superior limit of the number of students to add in the diningRoom
     */
    @Test
    public void diningRoomLimitTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertEquals(0, player.countStudentsEntrance());
        int numStudests = 0;
        for (Color color : Color.values()) {
            for (int i = 0; i < 15; i++) {
                if (i < 10) {
                    assertTrue(player.addStudentDiningRoom(color));
                    numStudests++;
                    assertEquals(i + 1, player.countStudentsDiningRoom(color));
                    assertEquals(numStudests, player.countStudentsDiningRoom());

                } else {
                    assertFalse(player.addStudentDiningRoom(color));
                    assertEquals(10, player.countStudentsDiningRoom(color));
                }
            }
        }
        assertEquals(numStudests, player.countStudentsDiningRoom());
    }

    /**
     * Test for the superior limit of the number of students to add in the entrance
     */
    @Test
    public void entranceLimitTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        int numStudests = 0;
        for (Color color : Color.values()) {
            for (int i = 0; i < 3; i++) {
                if (numStudests < 9) {
                    assertTrue(player.addStudentEntrance(color));
                    numStudests++;
                    assertEquals(i + 1, player.countStudentsEntrance(color));
                    assertEquals(numStudests, player.countStudentsEntrance());

                } else {
                    assertFalse(player.addStudentEntrance(color));
                    assertEquals(0, player.countStudentsEntrance(color));
                }
            }
        }
        assertEquals(9, player.countStudentsEntrance());
    }

    /**
     * Test to remove student from entrance
     */
    @Test
    public void removeStudentEntranceTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        for (Color color : Color.values()) {
            for (int i = 0; i < 3; i++) {
                assertTrue(player.addStudentEntrance(color));
                assertEquals(1, player.countStudentsEntrance(color));
                assertTrue(player.removeStudentEntrance(color));
                assertEquals(0, player.countStudentsEntrance(color));
                assertEquals(0, player.countStudentsEntrance());
            }
        }
    }

    /**
     * Test to remove student from dining room
     */
    @Test
    public void removeStudentDiningRoomTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        for (Color color : Color.values()) {
            for (int i = 0; i < 8; i++) {
                assertTrue(player.addStudentDiningRoom(color));
                assertEquals(1, player.countStudentsDiningRoom(color));
                assertTrue(player.removeStudentDiningRoom(color));
                assertEquals(0, player.countStudentsDiningRoom(color));
                assertEquals(0, player.countStudentsDiningRoom());
            }
        }
    }

    /**
     * Test to remove student from dining room, when there are no students
     */
    @Test
    public void removeZeroStudentDiningRoomTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        for (Color color : Color.values()) {
            assertFalse(player.removeStudentDiningRoom(color));
            assertEquals(0, player.countStudentsDiningRoom(color));
        }
        assertEquals(0, player.countStudentsDiningRoom());
    }

    /**
     * Test to remove student from entrance, when there are no students
     */
    @Test
    public void removeZeroStudentEntranceTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        for (Color color : Color.values()) {
            assertFalse(player.removeStudentEntrance(color));
            assertEquals(0, player.countStudentsEntrance(color));
        }
        assertEquals(0, player.countStudentsEntrance());
    }

    /**
     * Test to not add tower over the limit
     */
    @Test
    public void addTowerOverTheLimitTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertFalse(player.addTower(1));
    }

    /**
     * Test to not add a wrong number of tower
     */
    @Test
    public void addZeroTowerTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertFalse(player.addTower(0));
    }

    /**
     * Test to remove tower
     */
    @Test
    public void removeTowerTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertTrue(player.removeTower(1));
    }

    /**
     * Test to remove too many towers
     */
    @Test
    public void removeTooManyTowerTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertTrue(player.removeTower(6));
        assertFalse(player.removeTower(1));
    }

    /**
     * Test to count towers
     */
    @Test
    public void countTowersTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertEquals(6, player.getAvailableTowers());
        for (int i = 0; i < 6; i++) {
            assertTrue(player.removeTower(1));
            assertEquals(6 - i - 1, player.getAvailableTowers());
        }
        for (int i = 0; i < 6; i++) {
            assertFalse(player.removeTower(1));
            assertEquals(0, player.getAvailableTowers());
        }
    }

    /**
     * Test to not add tower
     */
    @Test
    public void addTowerTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        player.removeTower(2);
        assertTrue(player.addTower(1));
    }

    /**
     * Tests that the available cards at the beginning are with priority between 1 and 10 and are all in the getAvailableCards returned map
     */
    @Test
    public void availableInitialAssistantCardTest() {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        assertEquals(player.getAvailableCards().size(), 10);
        Map<Integer, Integer> availableAssistantCards = player.getAvailableCards();
        for (Integer i : availableAssistantCards.keySet())
            assertTrue(i >= 1 && i <= 10);
    }

    /**
     * Tests that if we use a card the size of the map decrement if the card was usable and the card does not appear in the map of usable cards
     *
     * @param cardToRemove : card to remove from the set
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 3, 6, 19})
    public void availableAssistantCardUseTest(int cardToRemove) {
        PlayerBoard player = new PlayerBoard("Valeria", Tower.WHITE, 6);
        if (cardToRemove < 1 || cardToRemove > 10)
            assertThrows(IndexOutOfBoundsException.class, () -> player.useAssistantCard(cardToRemove));
        else {
            try {
                player.useAssistantCard(cardToRemove);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        if (cardToRemove >= 1 && cardToRemove <= 10) assertEquals(9, player.getAvailableCards().size());
        else assertEquals(10, player.getAvailableCards().size());
        Map<Integer, Integer> availableAssistantCards = player.getAvailableCards();
        for (Integer i : availableAssistantCards.keySet())
            assertTrue(i != cardToRemove);
    }

}

