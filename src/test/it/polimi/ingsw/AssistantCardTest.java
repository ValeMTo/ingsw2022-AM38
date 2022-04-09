package it.polimi.ingsw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


public class AssistantCardTest {

    AssistantCard card = new AssistantCard(8, 4);

    /**
     * Test for the constructor of the Assistant Card with a given priority value
     * Uses algorithm to calculate the steps given the priority, as shown in class PlayerBoard
     */

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    @DisplayName("Test AssistantCard creation")
    public void checkCreatedCard(int priority) {
        int steps = priority / 2;
        if (priority % 2 != 0) {
            steps += 1;
        }
        AssistantCard card = new AssistantCard(priority, steps);
        assertEquals(priority, card.getPriority());
        assertEquals(steps, card.getSteps());
    }


    /**
     * Test to check the initial usage status of a newly created Assistant Card.
     */
    @Test
    @DisplayName("Test initial isUsed status of AssistantCard")

    public void checkInitialUsageTest() {
        assertFalse(card.isUsed());
    }

    /**
     * Test to check if a used card correctly changes its isUsed status
     */
    @Test
    @DisplayName("Test usage status of AssistantCard when used")

    public void checkUsedCardTest() {
        assertFalse(card.isUsed());
        card.use();
        assertTrue(card.isUsed());

    }


}






