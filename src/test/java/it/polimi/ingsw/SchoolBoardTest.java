package it.polimi.ingsw;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.*;

public class SchoolBoardTest {
    private final int maxStudentDiningRoomColor = 10;
    private final int maxStudentEntranceColor = 7;

    SchoolBoard schoolBoard = new SchoolBoard(maxStudentEntranceColor, maxStudentDiningRoomColor);

    private Color getEnumParameter(String s) {
        if (Color.BLUE.toString().equals(s)) return Color.BLUE;
        if (Color.YELLOW.toString().equals(s)) return Color.YELLOW;
        if (Color.PINK.toString().equals(s)) return Color.PINK;
        if (Color.GREEN.toString().equals(s)) return Color.GREEN;
        if (Color.RED.toString().equals(s)) return Color.RED;
        return null;
    }

    /**
     * Test for the superior limit of the number of students to add in the diningRoom
     *
     * @param studentsToAdd : num of students(>10 to test that from the eleventh are not added)
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,BLUE", "11,BLUE", "11,RED", "12,YELLOW"})
    @DisplayName("Test superior limit addStudent to Dining Room")
    public void adderTestSuperiorLimitDining(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd; i++) {
            if (i < maxStudentDiningRoomColor) {
                assertTrue(schoolBoard.addStudentDiningRoom(color));
            } else assertFalse(schoolBoard.addStudentDiningRoom(color));
        }
    }

    /**
     * Test for the superior limit of the number of students to add in the entrance
     *
     * @param studentsToAdd : num of students(>7/9 to test that from beyond the limit are not added)
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,BLUE", "11,BLUE", "11,RED", "12,YELLOW"})
    @DisplayName("Test superior limit addStudent to entrance")
    public void adderTestSuperiorLimitEntrance(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd; i++) {
            if (i < maxStudentEntranceColor) {
                assertTrue(schoolBoard.addStudentEntrance(color));
            } else assertFalse(schoolBoard.addStudentEntrance(color));
        }
    }

    /**
     * ParameterizedTest to test if the number of students added by Color correspond with the count (or 10 if they exceed the limit)
     *
     * @param studentsToAdd : number of students to add
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK", "4,BLUE", "0,RED", "12,YELLOW", "8,GREEN"})
    @DisplayName("Test add students DiningRoom and count")
    public void adderAndCountDiningRoomTest(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd; i++) {
            if (i < maxStudentDiningRoomColor) {
                assertTrue(schoolBoard.addStudentDiningRoom(color));
            } else assertFalse(schoolBoard.addStudentDiningRoom(color));
        }
        if (studentsToAdd < maxStudentDiningRoomColor)
            assertEquals(studentsToAdd, schoolBoard.countStudentsDiningRoom(color));
        else assertEquals(maxStudentDiningRoomColor, schoolBoard.countStudentsDiningRoom(color));
    }

    /**
     * ParameterizedTest to test if the number of students added by Color to entrance correspond with the count (or 7/10 if they exceed the limit)
     *
     * @param studentsToAdd : number of students to add
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"4,PINK", "4,BLUE", "0,RED", "12,YELLOW", "8,GREEN"})
    @DisplayName("Test add students entrance and count")
    public void adderAndCountEntranceTest(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd; i++) {
            if (i < maxStudentEntranceColor) {
                assertTrue(schoolBoard.addStudentEntrance(color));
            } else assertFalse(schoolBoard.addStudentEntrance(color));
        }
        if (studentsToAdd < maxStudentEntranceColor)
            assertEquals(studentsToAdd, schoolBoard.countStudentsEntrance(color));
        else assertEquals(maxStudentEntranceColor, schoolBoard.countStudentsEntrance(color));
    }

    /**
     * Parameterized test for the remove, to be sure it removes students only the right number of times
     * and do not remove below zero
     *
     * @param studentsToAdd : number of students added (if beyond the limit they will not be added...)
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK", "4,BLUE", "0,RED", "12,YELLOW", "8,GREEN"})
    @DisplayName("Add and remove students by color in DiningRoom")
    public void removeDiningRoom(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd && i < maxStudentDiningRoomColor; i++) {
            schoolBoard.addStudentDiningRoom(color);
        }
        for (int i = 0; i < studentsToAdd && i < maxStudentDiningRoomColor; i++) {
            assertTrue(schoolBoard.removeStudentDiningRoom(color));
        }
        assertFalse(schoolBoard.removeStudentDiningRoom(color));
        assertFalse(schoolBoard.removeStudentDiningRoom(color));
    }

    /**
     * Parameterized test for the remove in entrance, to be sure it removes students only the right number of times
     * and do not remove below zero
     *
     * @param studentsToAdd : number of students added (if beyond the limit they will not be added...)
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK", "4,BLUE", "0,RED", "12,YELLOW", "8,GREEN"})
    @DisplayName("Add and remove students by color in entrance")
    public void removeEntrance(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd && i < maxStudentEntranceColor; i++) {
            schoolBoard.addStudentEntrance(color);
        }
        for (int i = 0; i < studentsToAdd && i < maxStudentEntranceColor; i++) {
            assertTrue(schoolBoard.removeStudentEntrance(color));
        }
        assertFalse(schoolBoard.removeStudentEntrance(color));
        assertFalse(schoolBoard.removeStudentEntrance(color));
    }

    /**
     * Parameterized test for the remove and count to be sure the number is consistent
     *
     * @param studentsToAdd : number of students added (if beyond the limit they will not be added...)
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"8,PINK", "4,BLUE", "2,RED", "10,YELLOW", "8,GREEN"})
    @DisplayName("Add, remove and count students by color in DiningRoom")
    public void removeAndCountDiningRoom(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd; i++) {
            schoolBoard.addStudentDiningRoom(color);
        }
        for (int i = 0; i < studentsToAdd && i < maxStudentDiningRoomColor; i++) {
            assertTrue(schoolBoard.removeStudentDiningRoom(color));
            assertEquals(schoolBoard.countStudentsDiningRoom(color), min(studentsToAdd, maxStudentDiningRoomColor) - (i + 1));
            assertEquals(schoolBoard.countStudentsDiningRoom(), min(studentsToAdd, maxStudentDiningRoomColor) - (i + 1));
        }

    }

    /**
     * Parameterized test for the remove in entrance, to be sure it removes students only the right number of times
     * and do not remove below zero
     *
     * @param studentsToAdd : number of students added (if beyond the limit they will not be added...)
     * @param col           : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK", "4,BLUE", "0,RED", "12,YELLOW", "8,GREEN"})
    @DisplayName("Add and remove students by color in entrance")
    public void removeAndCountEntrance(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        for (int i = 0; i < studentsToAdd; i++) {
            schoolBoard.addStudentEntrance(color);
        }
        for (int i = 0; i < studentsToAdd && i < maxStudentEntranceColor; i++) {
            assertTrue(schoolBoard.removeStudentEntrance(color));
            assertEquals(schoolBoard.countStudentsEntrance(color), min(studentsToAdd, maxStudentEntranceColor) - (i + 1));
            assertEquals(schoolBoard.countStudentsEntrance(), min(studentsToAdd, maxStudentEntranceColor) - (i + 1));

        }
    }

    @ParameterizedTest
    @DisplayName("Test count adding students of all colour diningRoom")
    @ValueSource(booleans = {true, false})
    public void addStudentsAndCountDining(boolean diningRoom) {
        Color[] color = Color.values();
        int addCounter = 0;
        if (diningRoom) assertEquals(0, schoolBoard.countStudentsDiningRoom());
        else assertEquals(0, schoolBoard.countStudentsEntrance());
        for (int i = 0; i < color.length; i++) {
            if (diningRoom) {
                schoolBoard.addStudentDiningRoom(color[i]);
                addCounter++;
                assertEquals(addCounter, schoolBoard.countStudentsDiningRoom());
            } else {
                schoolBoard.addStudentEntrance(color[i]);
                addCounter++;
                assertEquals(addCounter, schoolBoard.countStudentsEntrance());
            }
        }
    }

    /**
     * Tests the catch of the NullPointerException by schoolEntrance and return false
     */
    @Test
    public void addDiningException() {
        assertFalse(schoolBoard.addStudentDiningRoom(null));
    }

    /**
     * Tests the catch of the NullPointerException by schoolEntrance and return false
     */
    @Test
    public void addEntranceException() {
        assertFalse(schoolBoard.addStudentEntrance(null));
    }

    /**
     * Tests the catch of the NullPointerException by schoolEntrance and return false
     */
    @Test
    public void removeDiningException() {
        assertFalse(schoolBoard.removeStudentDiningRoom(null));
    }

    /**
     * Tests the catch of the NullPointerException by schoolEntrance and return false
     */
    @Test
    public void removeEntranceException() {
        assertFalse(schoolBoard.removeStudentEntrance(null));
    }

}
