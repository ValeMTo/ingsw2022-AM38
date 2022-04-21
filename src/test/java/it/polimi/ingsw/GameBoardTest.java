package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class GameBoardTest {
    public List<String> getNicknames(int playerNum) {
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= playerNum; i++)
            list.add("Player" + i);
        return list;
    }

    /**
     * Adds a specified amount of students of a given color to the players in their dining rooms
     *
     * @param student         : color of the student
     * @param playersStudents : array of integers that specifies the number of students to add in the position of the player
     * @param gameBoard       : gameBoard in which we want to add the students
     * @return : true if every add was successful, otherwise returns false
     */
    public boolean studentAdderToPlayers(Color student, Integer[] playersStudents, GameBoard gameBoard) {
        for (int i = 0; i < playersStudents.length; i++)
            for (int j = 0; j < playersStudents[i]; j++) {
                try {
                    if (!gameBoard.addStudent(StudentCounter.PLAYER, student, i)) return false;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    return false;
                }
            }
        return true;
    }

    /**
     * Adds a specified amount of students of a given color to the players in their dining rooms
     *
     * @param student        : color of the student
     * @param islandStudents : students to add to the island
     * @param gameBoard      : gameBoard in which we want to add the students
     * @return : true if every add was successful, otherwise returns false
     */
    public boolean studentAdderToIsland(Color student, Integer islandStudents, Integer islandPosition, GameBoard gameBoard) {
        for (int i = 0; i < islandStudents; i++) {
            try {
                if (!gameBoard.addStudent(StudentCounter.ISLAND, student, islandPosition)) return false;
            } catch (Exception exc) {
                exc.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Tests that the roundCounter is increased fine in both 2 or 3 players mode
     *
     * @param playersNum : number of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void roundCounterTest(int playersNum) {
        GameBoard gameboard = new ExpertGameBoard(playersNum, getNicknames(playersNum));
        for (int i = 1; i < 12; i++) {
            assertEquals(i, gameboard.getNumRound());
            gameboard.increaseRound();
        }
    }

    /**
     * Tests that an AssistantCard cannot be used twice
     */
    @Test
    public void noMultipleUsesAssistantCard() {
        GameBoard gameboard = new ExpertGameBoard(2, getNicknames(2));
        assertTrue(gameboard.useAssistantCard(Tower.WHITE, 1));
        assertFalse(gameboard.useAssistantCard(Tower.WHITE, 1));
    }

    /**
     * Tests that all the AssistantCards returned by the getUsableAssistantCard method can be used
     */
    @Test
    public void usableAssistantCard() {
        GameBoard gameboard = new ExpertGameBoard(2, getNicknames(2));
        try {
            Map<Integer, Integer> usableCardsPriority = gameboard.getUsableAssistantCard(Tower.WHITE);
            for (Integer i : usableCardsPriority.keySet())
                assertTrue(gameboard.useAssistantCard(Tower.WHITE, i));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Tests that the values of card priority are only the integer between 1 and 10, no more, no less
     */
    @Test
    public void usableAssistantCardValues() {
        GameBoard gameboard = new ExpertGameBoard(2, getNicknames(2));
        Map<Integer, Integer> controlMap = new HashMap<Integer, Integer>();
        for (int i = 1; i <= 10; i++)
            controlMap.put(i, i / 2 + i % 2);
        try {
            for (Integer priority : gameboard.getUsableAssistantCard(Tower.WHITE).keySet())
                assertTrue(controlMap.containsKey(priority));

            for (Integer priority : controlMap.keySet())
                assertTrue(gameboard.getUsableAssistantCard(Tower.WHITE).containsKey(priority));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Tests that the boundary numbers (0 and 11) of the ones allowed to be use for assistantCard returns false
     *
     * @param cardNum : priority of the AssistantCard
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 11})
    public void boundaryAssistantCardIndexes(int cardNum) {
        GameBoard gameboard = new ExpertGameBoard(2, getNicknames(2));
        try {
            Map<Integer, Integer> usableCardsPriority = gameboard.getUsableAssistantCard(Tower.BLACK);
            assertThrows(IndexOutOfBoundsException.class, () -> gameboard.useAssistantCard(Tower.BLACK, cardNum));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


}
