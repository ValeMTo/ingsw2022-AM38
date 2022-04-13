package it.polimi.ingsw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SchoolEntranceTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    public void countOneColorStudentTest(int value) {
        SchoolEntrance room = new SchoolEntrance(9);

        for (int i = 0; i < value; i++) {
            room.addStudent(Color.RED);
        }
        assertEquals(room.countStudents(), value);
        assertEquals(room.countStudents(Color.RED), value);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    public void countMoreColorStudentTest(int value) {
        SchoolEntrance room = new SchoolEntrance(7);

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
        SchoolEntrance room = new SchoolEntrance(7);
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
        SchoolEntrance room = new SchoolEntrance(7);
        Color newStudent = Color.BLUE;

        for (int i = 0; i < 10; i++) {
            room.addStudent(newStudent);
        }

        assertFalse(room.addStudent(newStudent));
        assertEquals(room.countStudents(), 7);
        assertEquals(room.countStudents(Color.BLUE), 7);
        assertFalse(room.addStudent(Color.RED));
    }

    @Test
    public void removeStudentTest() {
        SchoolEntrance room = new SchoolEntrance(7);

        for (int i = 0; i < 5; i++) {
            assertTrue(room.addStudent(Color.BLUE));
            assertTrue(room.removeStudent(Color.BLUE));
        }

        assertEquals(room.countStudents(Color.BLUE), 0);
        assertEquals(room.countStudents(), 0);
    }

    @Test
    public void removeStudentUnderLimitTest() {
        SchoolEntrance room = new SchoolEntrance(7);

        assertFalse(room.removeStudent(Color.BLUE));
    }

    @Test
    public void addNullPointerTest() {
        Room room = new SchoolEntrance(7);

        assertThrows(NullPointerException.class, () -> room.addStudent(null));
    }

    @Test
    public void removeNullPointerTest() {
        Room room = new SchoolEntrance(7);

        assertThrows(NullPointerException.class, () -> room.removeStudent(null));
    }
}