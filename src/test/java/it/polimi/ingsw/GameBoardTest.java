package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    /**
     * Tests the immediate end of the match due to the end of usable assistant cards (10 rounds have been played)
     */
    @Test
    public void endOfMatchTestEndOfCards() {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
        for (int i = 0; i < 10; i++) {
            assertEquals(EndOfMatchCondition.NoEndOfMatch, gameboard.isEndOfMatch());
            gameboard.increaseRound();
        }
        assertEquals(EndOfMatchCondition.InstantEndOfMatch, gameboard.isEndOfMatch());
    }

    /**
     * Tests the delayed end of the match due to the end of students in the bag
     */
    @Test
    public void endOfMatchTestEndOfStudents() {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));

        assertEquals(EndOfMatchCondition.NoEndOfMatch, gameboard.isEndOfMatch());
        for (Color color : Color.values()) {
            try {
                while (!gameboard.bag.isEmpty()) gameboard.bag.drawStudent();
            } //While there are student to remove from bag of that color
            catch (Exception exc) {
                exc.printStackTrace();
                return;
            }
        }
        assertEquals(EndOfMatchCondition.DelayedEndOfMatch, gameboard.isEndOfMatch());
    }

    /**
     * Tests the instant end of the match due to the end of towers in a particular player
     */
    @Test
    public void endOfMatchTestEndOfTowers() {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
        assertEquals(EndOfMatchCondition.NoEndOfMatch, gameboard.isEndOfMatch());
        int towerNum = gameboard.players[0].getAvailableTowers();
        for (int i = 0; i < towerNum; i++) {
            assertEquals(EndOfMatchCondition.NoEndOfMatch, gameboard.isEndOfMatch());
            gameboard.players[0].removeTower(1);
        }
        assertEquals(EndOfMatchCondition.InstantEndOfMatch, gameboard.isEndOfMatch());
    }

    /**
     * Tests the add student with all the possible locations, with nominal and exceptional conditions
     *
     * @param location : enumeration that say where we want to add the student
     */
    @ParameterizedTest
    @EnumSource(StudentCounter.class)
    public void addStudentTest(StudentCounter location) {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
        for (Color color : Color.values()) {

            try {
                switch (location) {
                    case BAG:
                        assertTrue(gameboard.addStudent(StudentCounter.BAG, color));
                        break;
                    case CARD:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.addStudent(StudentCounter.CARD, color));
                        break;
                    case SCHOOLENTRANCE:
                        if (gameboard.getSchoolEntranceOccupation(Tower.WHITE) < 9)
                            assertTrue(gameboard.addStudent(StudentCounter.SCHOOLENTRANCE, color));
                        break;
                    case DININGROOM:
                        if (gameboard.getDiningRoomOccupation(Tower.WHITE) < 10)
                            assertTrue(gameboard.addStudent(StudentCounter.DININGROOM, color));
                        break;
                    default:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.addStudent(location, Color.BLUE));
                }
            } catch (Exception exc) {
                exc.printStackTrace();

            }
        }
    }

    /**
     * Tests the add student with all the possible locations and using the position, with nominal and exceptional conditions
     *
     * @param location : enumeration that say where we want to add the student
     */
    @ParameterizedTest
    @EnumSource(StudentCounter.class)
    public void addStudentWithPositionTest(StudentCounter location) {
        GameBoard gameboard = new EasyGameBoard(3, getNicknames(3));
        for (Color color : Color.values()) {

            try {
                switch (location) {
                    case BAG:
                        assertTrue(gameboard.addStudent(StudentCounter.BAG, color, 0));
                        break;
                    case CARD:
                        break;
                    case SCHOOLENTRANCE:
                        if (gameboard.getSchoolEntranceOccupation(Tower.WHITE) < 9)
                            assertTrue(gameboard.addStudent(StudentCounter.SCHOOLENTRANCE, color, 0));
                        else assertFalse(gameboard.addStudent(StudentCounter.DININGROOM, color));

                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.addStudent(StudentCounter.SCHOOLENTRANCE, color, 20));
                        break;
                    case CLOUD:
                        for (Color col : Color.values())
                            gameboard.removeStudent(StudentCounter.CLOUD, col, 1);
                        assertTrue(gameboard.addStudent(StudentCounter.CLOUD, color, 1));
                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.addStudent(StudentCounter.CLOUD, color, 20));
                        break;
                    case DININGROOM:
                        if (gameboard.getDiningRoomOccupation(Tower.WHITE) < 10)
                            assertTrue(gameboard.addStudent(StudentCounter.DININGROOM, color));
                        else assertFalse(gameboard.addStudent(StudentCounter.DININGROOM, color));

                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.addStudent(StudentCounter.DININGROOM, color, 20));
                        break;
                    case ISLAND:
                        assertTrue(gameboard.addStudent(location, color, 1));
                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.addStudent(location, color, 0));
                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.addStudent(location, color, 13));

                    default:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.addStudent(location, Color.BLUE));
                }
            } catch (Exception exc) {
                exc.printStackTrace();

            }
        }
    }

    /**
     * Tests the remove student with all the possible locations, with nominal and exceptional conditions
     *
     * @param location : enumeration that say where we want to remove the student
     */

    @ParameterizedTest
    @EnumSource(StudentCounter.class)
    public void removeStudentTest(StudentCounter location) {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
        for (Color color : Color.values()) {

            try {
                switch (location) {
                    case BAG, ISLAND:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.removeStudent(location, color));
                        break;
                    case CARD:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.addStudent(StudentCounter.CARD, color));
                        break;
                    case SCHOOLENTRANCE, DININGROOM:
                        if (gameboard.getDiningRoomOccupation(Tower.WHITE) > 0)
                            assertTrue(gameboard.removeStudent(location, color));
                        else assertFalse(gameboard.removeStudent(location, color));
                        gameboard.addStudent(location, color, gameboard.getPlayerPosition(Tower.WHITE));
                        assertTrue(gameboard.removeStudent(location, color));
                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.removeStudent(location, color, 20));
                        break;
                    default:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.addStudent(location, Color.BLUE));
                }
            } catch (Exception exc) {
                exc.printStackTrace();

            }
        }
    }

    /**
     * Tests the remove student with all the possible locations and using the position, with nominal and exceptional conditions
     *
     * @param location : enumeration that say where we want to remove the student
     */
    @ParameterizedTest
    @EnumSource(StudentCounter.class)
    public void removeStudentWithPositionTest(StudentCounter location) {
        GameBoard gameboard = new EasyGameBoard(3, getNicknames(3));
        for (Color color : Color.values()) {

            try {
                switch (location) {
                    case BAG, ISLAND:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.removeStudent(location, color, 20));

                    case CARD:
                        break;
                    case SCHOOLENTRANCE, DININGROOM:
                        if (gameboard.getDiningRoomOccupation(Tower.WHITE) > 0)
                            assertTrue(gameboard.removeStudent(location, color));
                        else assertFalse(gameboard.removeStudent(location, color));
                        gameboard.addStudent(location, color, gameboard.getPlayerPosition(Tower.WHITE));
                        assertTrue(gameboard.removeStudent(location, color));
                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.removeStudent(location, color, 20));
                        break;
                    case CLOUD:
                        for (Color col : Color.values())
                            gameboard.removeStudent(StudentCounter.CLOUD, col, 1);
                        assertFalse(gameboard.removeStudent(StudentCounter.CLOUD, color, 1));
                        gameboard.addStudent(StudentCounter.CLOUD, color, 1);
                        assertTrue(gameboard.removeStudent(StudentCounter.CLOUD, color, 1));
                        assertThrows(IndexOutOfBoundsException.class, () -> gameboard.addStudent(StudentCounter.CLOUD, color, 20));
                        break;
                    default:
                        assertThrows(LocationNotAllowedException.class, () -> gameboard.removeStudent(location, Color.BLUE, 20));
                }
            } catch (Exception exc) {
                exc.printStackTrace();

            }
        }
    }

    /**
     * Tests that the influence is correctly computed for an Island, doing so tests that the professors are correctly updated
     */
    @Test
    public void simpleInfluence() {
        try {
            GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
            gameboard.addStudent(StudentCounter.DININGROOM, Color.BLUE, 0);
            gameboard.addStudent(StudentCounter.ISLAND, Color.BLUE, 1);
            assertNull(gameboard.computeInfluence(1));
            gameboard.updateProfessorOwnership();
            assertEquals(gameboard.computeInfluence(1), Tower.WHITE);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Test that the initial number of Islands is correctly configured
     */
    @Test
    public void initialIslandNumber() {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
        assertEquals(12, gameboard.getIslandNumber());
    }

    /**
     * Test the merge between two islands after the influence computation
     */
    @Test
    public void islandMerge() {
        try {
            GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
            gameboard.addStudent(StudentCounter.DININGROOM, Color.BLUE, 2);
            gameboard.addStudent(StudentCounter.ISLAND, Color.BLUE, 1);
            gameboard.addStudent(StudentCounter.ISLAND, Color.BLUE, 2);
            gameboard.updateProfessorOwnership();
            assertEquals(Tower.GRAY, gameboard.computeInfluence(1));
            assertEquals(Tower.GRAY, gameboard.computeInfluence(2));
            assertEquals(11, gameboard.getIslandNumber());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Test the merge between two islands after the influence computation but now the island are differents
     */
    @Test
    public void islandMergeReverse() {
        try {
            GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
            gameboard.addStudent(StudentCounter.DININGROOM, Color.BLUE, 1);
            gameboard.addStudent(StudentCounter.ISLAND, Color.BLUE, 1);
            gameboard.addStudent(StudentCounter.ISLAND, Color.BLUE, 12);
            gameboard.updateProfessorOwnership();
            assertEquals(Tower.BLACK, gameboard.computeInfluence(12));
            assertEquals(Tower.BLACK, gameboard.computeInfluence(1));
            assertEquals(11, gameboard.getIslandNumber());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Tests the movement of motherNature if the steps are coherent to the movement we want to do
     */
    @Test
    public void moveMotherNatureCorrectly() {
        GameBoard gameboard = new EasyGameBoard(3, getNicknames(3));
        gameboard.useAssistantCard(Tower.WHITE, 1);
        gameboard.useAssistantCard(Tower.BLACK, 2);
        gameboard.useAssistantCard(Tower.GRAY, 3);
        try {
            assertTrue(gameboard.moveMotherNature(2));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    //TODO: To test with the easyGameBoard since it has the not override method

    /**
     * Tests the movement of motherNature if the steps are incoherent to the movement we want to do
     */
    @Test
    public void moveMotherNatureBeyondLimit() {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
        gameboard.useAssistantCard(Tower.WHITE, 1);
        gameboard.useAssistantCard(Tower.BLACK, 2);
        gameboard.useAssistantCard(Tower.GRAY, 3);
        try {
            assertFalse(gameboard.moveMotherNature(12));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Tests that the diningRoomOccupation is correctly initialized and coherent with add
     *
     * @param student : color of the student we want to add
     */
    @ParameterizedTest
    @EnumSource(Color.class)
    public void getDiningRoomOccupation(Color student) {
        GameBoard gameboard = new ExpertGameBoard(3, getNicknames(3));
        try {
            assertEquals(0, gameboard.getDiningRoomOccupation(Tower.WHITE, student));
            gameboard.addStudent(StudentCounter.DININGROOM, student, 0);
            gameboard.addStudent(StudentCounter.DININGROOM, student, 1);
            gameboard.addStudent(StudentCounter.DININGROOM, student, 2);
            assertEquals(1, gameboard.getDiningRoomOccupation(Tower.WHITE, student));
            for (Color notSameColor : Color.values())
                if (notSameColor != student)
                    assertEquals(0, gameboard.getDiningRoomOccupation(Tower.WHITE, notSameColor));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    /**
     * Verifies that the initial occupation of SchoolEntrance is correctly configured and take into account the number of players instanced
     *
     * @param playerNum : number of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void getInitialEntranceOccupation(int playerNum) {
        GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
        try {
            assertEquals(0, gameboard.getSchoolEntranceOccupation(Tower.WHITE));
            assertEquals(0, gameboard.getSchoolEntranceOccupation(Tower.BLACK));
            if (playerNum != 3)
                assertThrows(NoSuchTowerException.class, () -> gameboard.getSchoolEntranceOccupation(Tower.GRAY));
            else assertEquals(0, gameboard.getSchoolEntranceOccupation(Tower.GRAY));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Verifies that the number of students per color in SchoolEntrance is correct when instanced
     *
     * @param playerNum: number of players
     */

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void getInitialEntranceOccupationForColors(int playerNum) {
        for (Color color : Color.values()) {
            GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
            try {
                assertEquals(0, gameboard.getSchoolEntranceOccupation(Tower.WHITE, color));
                assertEquals(0, gameboard.getSchoolEntranceOccupation(Tower.BLACK, color));
                if (playerNum != 3)
                    assertThrows(NoSuchTowerException.class, () -> gameboard.getSchoolEntranceOccupation(Tower.GRAY, color));
                else assertEquals(0, gameboard.getSchoolEntranceOccupation(Tower.GRAY, color));

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    /**
     * Verifies that the initial occupation of DiningRoom is correctly configured and take into account the number of players instanced
     *
     * @param playerNum : number of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void getInitialDiningRoomOccupation(int playerNum) {
        GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
        try {
            assertEquals(0, gameboard.getDiningRoomOccupation(Tower.WHITE));
            assertEquals(0, gameboard.getDiningRoomOccupation(Tower.BLACK));
            if (playerNum != 3)
                assertThrows(NoSuchTowerException.class, () -> gameboard.getDiningRoomOccupation(Tower.GRAY));
            else assertEquals(0, gameboard.getDiningRoomOccupation(Tower.GRAY));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Verifies that the number of students per color in DiningRoom is correct when instanced
     *
     * @param playerNum: number of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void getInitialDiningRoomOccupationForColors(int playerNum) {
        for (Color color : Color.values()) {
            GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
            try {
                assertEquals(0, gameboard.getDiningRoomOccupation(Tower.WHITE, color));
                assertEquals(0, gameboard.getDiningRoomOccupation(Tower.BLACK, color));
                if (playerNum != 3)
                    assertThrows(NoSuchTowerException.class, () -> gameboard.getDiningRoomOccupation(Tower.GRAY, color));
                else assertEquals(0, gameboard.getDiningRoomOccupation(Tower.GRAY, color));

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    /**
     * Verifies that the player are correctly instanced in order, having a certain tower for a certain position of player
     *
     * @param playerNum : number of players
     */

    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void getPlayerPosition(int playerNum) {
        GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
        try {
            assertEquals(0, gameboard.getPlayerPosition(Tower.WHITE));
            assertEquals(1, gameboard.getPlayerPosition(Tower.BLACK));
            if (playerNum == 3) assertEquals(2, gameboard.getPlayerPosition(Tower.GRAY));
            else assertThrows(NoSuchTowerException.class, () -> gameboard.getPlayerPosition(Tower.GRAY));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Test the use and get of the last assistant card in nominal and exceptional conditions
     *
     * @param priority : priority of the card we use
     */
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 5, 10, 11})
    public void useAndGetLastAssistantCard(int priority) {
        GameBoard gameboard = new ExpertGameBoard(2, getNicknames(2));
        assertThrows(NotLastCardUsedException.class, () -> gameboard.getLastAssistantCard(Tower.WHITE));
        if (priority >= 1 && priority <= 10) {
            assertTrue(gameboard.useAssistantCard(Tower.WHITE, priority));
            try {
                assertEquals(priority, gameboard.getLastAssistantCard(Tower.WHITE));
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else assertThrows(IndexOutOfBoundsException.class, () -> gameboard.useAssistantCard(Tower.WHITE, priority));

    }

    /**
     * Tests that the first player is initialized as the one with White towers and not others
     *
     * @param playerNum : number of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void getCurrentPlayerInitialization(int playerNum) {
        GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
        assertEquals(Tower.WHITE, gameboard.getCurrentPlayer());
    }

    /**
     * Tests the set and get of the currentPlayer
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void setAndGetCurrentPlayer(int playerNum) {
        GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
        try {
            assertTrue(gameboard.setCurrentPlayer(Tower.WHITE));
            assertEquals(Tower.WHITE, gameboard.getCurrentPlayer());
            assertTrue(gameboard.setCurrentPlayer(Tower.BLACK));
            assertEquals(Tower.BLACK, gameboard.getCurrentPlayer());
            if (playerNum == 3) {
                assertTrue(gameboard.setCurrentPlayer(Tower.GRAY));
                assertEquals(Tower.GRAY, gameboard.getCurrentPlayer());
            } else {
                assertThrows(NoSuchTowerException.class, () -> gameboard.setCurrentPlayer(Tower.GRAY));
                assertNotEquals(Tower.GRAY, gameboard.getCurrentPlayer());
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Verifies that the Clouds are initially void
     *
     * @param playerNum : num of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void cloudInitialState(int playerNum) {
        GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
        try {
            for (Color color : Color.values()) {
                assertFalse(gameboard.removeStudent(StudentCounter.CLOUD, color, 1));
                assertFalse(gameboard.removeStudent(StudentCounter.CLOUD, color, 2));
                if (playerNum == 3) assertFalse(gameboard.removeStudent(StudentCounter.CLOUD, color, 3));
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Tests the fill cloud and count that the nr. of students added is correct
     *
     * @param playerNum : num of player
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3})
    public void fillCloud(int playerNum) {
        GameBoard gameboard = new ExpertGameBoard(playerNum, getNicknames(playerNum));
        int counter = 0;
        gameboard.fillClouds();
        try {
            for (Color color : Color.values()) {
                while (gameboard.removeStudent(StudentCounter.CLOUD, color, 1)) counter++;
            }
            if (playerNum == 2) assertEquals(3, counter);
            else if (playerNum == 3) assertEquals(4, counter);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

}
