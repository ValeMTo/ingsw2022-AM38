package it.polimi.ingsw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;


public class CloudTest {

    /**
     * The following amounts of studentLimit represent the maximum amount of students
     * allowed on the cloud, according to the number of players, as shown in the rules of the game.
     * From here onwards the cloud for 2 or 4 players will be considered the default test case.
     */

    private final int studentLimit2Players = 3;
    private final int studentLimit3Players = 4;

    Cloud testedCloud = new Cloud(studentLimit2Players);

    Cloud testedCloud3Players = new Cloud(studentLimit3Players);

    private void addMultipleStudents(Cloud cloud, Color color, int amount) {
        for (int i = 0; i < amount; i++) {
            cloud.addStudent(color);                // adds multiple students of only one given color
        }
    }

    // converts the string into the enum constant of the corresponding color. If the string is "null", it returns null.
    private Color getEnumParameter(String s) {
        if (Color.BLUE.toString().equals(s)) return Color.BLUE;
        if (Color.YELLOW.toString().equals(s)) return Color.YELLOW;
        if (Color.PINK.toString().equals(s)) return Color.PINK;
        if (Color.GREEN.toString().equals(s)) return Color.GREEN;
        if (Color.RED.toString().equals(s)) return Color.RED;
        return null;
    }

    /**
     * Checks that the constructor correctly creates a cloud, with the correct initial amount of students
     * (initially zero students for each color).
     */
    @Test
    @DisplayName("Cloud construction test")
    public void CloudConstructorTest() {
        Cloud cloud = new Cloud(studentLimit2Players);

        for (Color col : Color.values()) {
            assertEquals(0, cloud.countStudent(col));
        }
        int totalStud = 0;
        for (Color col : Color.values()) {
            totalStud += cloud.countStudent(col);
        }
        assertTrue(totalStud <= studentLimit2Players);

    }


    /**
     * Checks that the method isEmpty correctly returns true if the cloud is empty; false otherwise.
     */
    @Test
    @DisplayName("Check isEmpty method on cloud")
    public void checkCloudIsEmptyTest() {
        assertTrue(testedCloud.isEmpty());
        addMultipleStudents(testedCloud, Color.BLUE, 2);
        assertFalse(testedCloud.isEmpty());
    }


    /**
     * Checks that the method isFull correctly returns true if the cloud for 2 players already contains
     * the maximum amount of allowed students; false otherwise.
     */
    @ParameterizedTest
    @CsvSource({"PINK", "BLUE", "RED", "YELLOW", "GREEN"})
    @DisplayName("Check isFull method on cloud for 2 players")
    public void checkCloud2PlayersIsFullTest(String col) {
        Color color = getEnumParameter(col);
        assertFalse(testedCloud.isFull());
        addMultipleStudents(testedCloud, color, studentLimit2Players);
        assertTrue(testedCloud.isFull());
    }

    /**
     * Checks that the method isFull correctly returns true if the cloud for 3 players already contains
     * the maximum amount of allowed students; false otherwise.
     */
    @ParameterizedTest
    @CsvSource({"PINK", "BLUE", "RED", "YELLOW", "GREEN"})
    @DisplayName("Check isFull method on cloud for 3 players")
    public void checkCloud3PlayersIsFullTest(String col) {
        Color color = getEnumParameter(col);
        assertFalse(testedCloud3Players.isFull());
        addMultipleStudents(testedCloud3Players, color, studentLimit3Players);
        assertTrue(testedCloud3Players.isFull());
    }

    /**
     * Checks that the method isFull correctly returns true if the cloud  already contains
     * the maximum amount of allowed students, but with a "mixed" distribution; false otherwise.
     */
    @ParameterizedTest
    @CsvSource({"PINK", "BLUE", "RED", "YELLOW", "GREEN"})
    @DisplayName("Check isFull method on cloud with mixed initial distribution")
    public void checkMixedCloudIsFullTest(String col) {
        Color color = getEnumParameter(col);
        Cloud mixedCloud = new Cloud(studentLimit2Players);

        assertFalse(mixedCloud.isFull());
        addMultipleStudents(mixedCloud, Color.PINK, 0);
        addMultipleStudents(mixedCloud, Color.BLUE, 1);
        addMultipleStudents(mixedCloud, Color.RED, 2);
        assertTrue(mixedCloud.isFull());
    }


    /**
     * Checks that the method to empty the cloud correctly removes all the students currently in the cloud.
     * The tests are run with different fillings of the cloud, using a cloud for 2 players.
     * Note that a newly-created cloud is always empty at first.
     *
     * @param studentsToAdd : the number of students to add before the emptying attempt
     * @param col           :  the color of the students to add before the emptying attempt
     */
    @ParameterizedTest
    @CsvSource({"0,PINK", "0,BLUE", "1,RED", "2,YELLOW", "3,GREEN"})
    @DisplayName("Check 2-players-cloud emptying method")
    public void emptyTheCloudFor2PlayersTest(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        assertFalse(testedCloud.emptyCloud());    // the cloud is initially empty, so it cannot be emptied

        addMultipleStudents(testedCloud, color, studentsToAdd);
        if (studentsToAdd <= 0) assertFalse(testedCloud.emptyCloud());
        else {
            assertTrue(testedCloud.emptyCloud());
            assertTrue(testedCloud.isEmpty());
            for (Color c : Color.values()) {
                assertEquals(0, testedCloud.countStudent(c));
            }
        }
    }

    /**
     * Checks that the method to empty the cloud correctly removes all the students currently in the cloud.
     * The tests are run with different fillings of the cloud, using a cloud for 3 players.
     *
     * @param studentsToAdd : the number of students to add before the emptying attempt
     * @param col           :  the color of the students to add before the emptying attempt
     */
    @ParameterizedTest
    @CsvSource({"0,PINK", "0,BLUE", "1,RED", "2,YELLOW", "3,GREEN"})
    @DisplayName("Check 3-players-cloud emptying method")
    public void emptyTheCloudFor3PlayersTest(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        assertFalse(testedCloud3Players.emptyCloud());    // the cloud is initially empty, so it cannot be emptied

        addMultipleStudents(testedCloud3Players, color, studentsToAdd);
        if (studentsToAdd <= 0) assertFalse(testedCloud3Players.emptyCloud());
        else {
            assertTrue(testedCloud3Players.emptyCloud());
            assertTrue(testedCloud3Players.isEmpty());
            for (Color c : Color.values()) {
                assertEquals(0, testedCloud3Players.countStudent(c));
            }
        }
    }

    /**
     * Checks that the addStudent returns false if it is attempted to add students to a cloud that is already full.
     */
    @ParameterizedTest
    @CsvSource({"PINK", "BLUE", "RED", "YELLOW", "GREEN"})
    @DisplayName("Check adding beyond student limit")
    public void addingBeyondLimitTest(String col) {
        Color color = getEnumParameter(col);
        addMultipleStudents(testedCloud, Color.BLUE, studentLimit2Players);
        assertTrue(testedCloud.isFull());
        assertFalse(testedCloud.addStudent(color));

        // testing the cloud for 3 players
        addMultipleStudents(testedCloud3Players, Color.BLUE, studentLimit3Players);
        assertTrue(testedCloud3Players.isFull());
        assertFalse(testedCloud3Players.addStudent(color));
    }

    /**
     * Checks that the removeStudent returns false if it is attempted to remove students from a cloud
     * that is already empty.
     */
    @ParameterizedTest
    @CsvSource({"PINK", "BLUE", "RED", "YELLOW", "GREEN"})
    @DisplayName("Check removing on an empty cloud")
    public void removingFromEmptyCloudTest(String col) {
        Color color = getEnumParameter(col);
        testedCloud.emptyCloud();
        assertTrue(testedCloud.isEmpty());
        assertFalse(testedCloud.removeStudent(color));
    }

    /**
     * Tests if the number of students for each color is correctly updated after adding different amounts of student
     * and makes sure the amounts of the colors that are not added remain unchanged.
     *
     * @param studentsToAdd: the number of students to be added
     * @param col            : the color of the students to be added
     */
    @ParameterizedTest
    @CsvSource({"1,PINK", "3,BLUE", "0,RED", "2,YELLOW", "7,GREEN"})
    @DisplayName("Check students per color after addStudent")
    public void checkAmountsPerColorAfterAddingTest(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        Color[] colorSet = Color.values();
        int[] previousAmounts = new int[colorSet.length];

        assertTrue(testedCloud.isEmpty());

        for (int i = 0; i < colorSet.length; i++) {         // counts the amounts of students per color before adding
            previousAmounts[i] = testedCloud.countStudent(colorSet[i]);
        }

        for (int i = 0; i < studentsToAdd; i++) {
            if (i < studentLimit2Players) {
                assertTrue(testedCloud.addStudent(color));
            }
            else {
                assertFalse(testedCloud.addStudent(color));
            }
        }
        if (studentsToAdd < studentLimit2Players) {
            for (int i = 0; i < colorSet.length; i++) {
                if (colorSet[i].equals(color))
                    // checks that only the added color had its amount correctly increased
                    assertEquals(previousAmounts[i] + studentsToAdd, testedCloud.countStudent(color));
                else
                    assertEquals(previousAmounts[i], testedCloud.countStudent(colorSet[i]));
            }
        } else
            assertEquals(studentLimit2Players, testedCloud.countStudent(color));
    }


    /**
     * Tests if the number of students for each color is correctly updated after removing different amounts of student
     * and makes sure the amounts of the colors that are not removed remain unchanged.
     *
     * @param studentsToRemove: the number of students to be removed
     * @param col               : the color of the students to be removed
     */
    @ParameterizedTest
    @CsvSource({"1,PINK", "3,BLUE", "0,RED", "2,YELLOW", "7,GREEN"})
    @DisplayName("Check students per color after removeStudent")
    public void checkAmountsPerColorAfterRemovalTest(int studentsToRemove, String col) {
        Color color = getEnumParameter(col);
        Color[] colorSet = Color.values();
        int[] previousAmounts = new int[colorSet.length];

        assertTrue(testedCloud.isEmpty());
        assertFalse(testedCloud.removeStudent(color));

        addMultipleStudents(testedCloud, color, studentLimit2Players);

        for (int i = 0; i < colorSet.length; i++) {         // counts the amounts of students per color before removing
            previousAmounts[i] = testedCloud.countStudent(colorSet[i]);
        }

        for (int i = 0; i < studentsToRemove; i++) {
            if (i < studentLimit2Players) {
                assertTrue(testedCloud.removeStudent(color));
            }
            else {
                assertFalse(testedCloud.removeStudent(color));
            }
        }
        if (studentsToRemove < studentLimit2Players) {
            for (int i = 0; i < colorSet.length; i++) {
                if (colorSet[i].equals(color)) {
                    // checks that only the removed color had its amount correctly decreased
                    assertEquals(previousAmounts[i] - studentsToRemove, testedCloud.countStudent(color));
                }
                else {
                    assertEquals(previousAmounts[i], testedCloud.countStudent(colorSet[i]));
                }
            }
        } else
            assertEquals(0, testedCloud.countStudent(color));
    }


    /**
     * Checks if the NullPointerException is thrown when addStudent receives a null parameter instead of a Color.
     */
    @Test
    @DisplayName("Check NullPointer exception in addStudent")
    void nullPointerExceptionInAddTest() {
        NullPointerException thrown = assertThrows(NullPointerException.class, () -> testedCloud.addStudent(null), "Expected addStudent() to throw NullPointerException, but it didn't");
    }

    /**
     * Checks if the NullPointerException is thrown when removeStudent receives a null parameter instead of a Color.
     */
    @Test
    @DisplayName("Check NullPointer exception in removeStudent")
    void nullPointerExceptionInRemoveTest() {
        NullPointerException thrown = assertThrows(NullPointerException.class, () -> testedCloud.removeStudent(null), "Expected addStudent() to throw NullPointerException, but it didn't");
    }

}
