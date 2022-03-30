package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DiningRoomTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void countOneColorStudentTest(int value) {
        DiningRoom room = new DiningRoom();

        for (int i = 0; i < value; i++) {
            room.addStudent(Color.RED);
        }
        assertEquals(room.countStudents(), value);
        assertEquals(room.countStudents(Color.RED), value);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    public void countMoreColorStudentTest(int value) {
        DiningRoom room = new DiningRoom();

        for (int i = 0; i < value; i++) {
            room.addStudent(Color.RED);
            room.addStudent(Color.BLUE);
            room.addStudent(Color.YELLOW);
        }
        assertEquals(room.countStudents(), value * 3);
        assertEquals(room.countStudents(Color.RED), value);
        assertEquals(room.countStudents(Color.BLUE), value);
        assertEquals(room.countStudents(Color.YELLOW), value);
    }

    @Test
    public void addOneStudentTest() {
        DiningRoom room = new DiningRoom();
        Color newStudent = Color.BLUE;

        room.addStudent(newStudent);
        assertEquals(room.countStudents(), 1);
        assertEquals(room.countStudents(Color.BLUE), 1);
        assertEquals(room.countStudents(Color.YELLOW), 0);
        assertEquals(room.countStudents(Color.GREEN), 0);
        assertEquals(room.countStudents(Color.PINK), 0);
        assertEquals(room.countStudents(Color.RED), 0);
    }

    @Test
    public void addStudentOverTheLimitTest() {
        DiningRoom room = new DiningRoom();
        Color newStudent = Color.BLUE;

        for (int i = 0; i < 20; i++) {
            room.addStudent(newStudent);
        }

        assertEquals(room.countStudents(), 10);
        assertFalse(room.addStudent(newStudent));
        assertEquals(room.countStudents(), 10);
        assertEquals(room.countStudents(Color.BLUE), 10);
        assertTrue(room.addStudent(Color.RED));

    }

    @Test
    public void removeStudentTest() {
        DiningRoom room = new DiningRoom();

        for (int i = 0; i < 5; i++) {
            assertTrue(room.addStudent(Color.BLUE));
            assertTrue(room.removeStudent(Color.BLUE));
        }

        assertEquals(room.countStudents(Color.BLUE), 0);
        assertEquals(room.countStudents(), 0);

    }

    @Test
    public void removeStudentUnderLimitTest() {
        DiningRoom room = new DiningRoom();

        assertFalse(room.removeStudent(Color.BLUE));

    }

}
