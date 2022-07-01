package it.polimi.ingsw.model.specialCards;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialCardWithStudentTest {

    SpecialCard priest = new SpecialCardWithStudent(SpecialCardName.PRIEST);
    SpecialCard juggler = new SpecialCardWithStudent(SpecialCardName.JUGGLER);
    SpecialCard princess = new SpecialCardWithStudent(SpecialCardName.PRINCESS);

    /**
     * Test to add student respecting the limit
     */
    @Before
    @Test
    public void addStudentTest() {
        int count = 0;
        for (Color student : Color.values()) {
            try {
                if (count < 4) {
                    assertTrue(priest.addStudent(student));
                    assertTrue(juggler.addStudent(student));
                    assertTrue(princess.addStudent(student));
                }
                count += 1;
            } catch (FunctionNotImplementedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Test to add student over the limit
     */
    @Before
    @Test
    public void addStudentOverLimitTest() {
        try {
            for (int i = 0; i <= 4; i += 1) {
                juggler.addStudent(Color.BLUE);
                priest.addStudent(Color.BLUE);
                princess.addStudent(Color.BLUE);
            }
            juggler.addStudent(Color.PINK);
            juggler.addStudent(Color.PINK);

            assertFalse(princess.addStudent(Color.BLUE));
            assertFalse(priest.addStudent(Color.BLUE));
            assertFalse(juggler.addStudent(Color.BLUE));

        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to add student respecting the limit
     */
    @Test
    public void removeStudentTest() {
        try {
            for (int i=0; i<=4; i++) {
                priest.addStudent(Color.PINK);
                juggler.addStudent(Color.PINK);
                princess.addStudent(Color.PINK);
            }
            assertTrue(priest.removeStudent(Color.PINK));
            assertTrue(juggler.removeStudent(Color.PINK));
            assertTrue(princess.removeStudent(Color.PINK));
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to remove student over the limit
     */
    @Test
    public void removeStudentOverLimitTest() {
        try {
            for (Color student : Color.values()) {
                assertFalse(princess.removeStudent(student));
                assertFalse(priest.removeStudent(student));
                assertFalse(juggler.removeStudent(student));
            }
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test to count students, without considering the color
     */
    @Test
    public void countStudentsTest() {
        for (Color student : Color.values()) {
            try {
                juggler.addStudent(student);
                priest.addStudent(student);
                princess.addStudent(student);
            } catch (FunctionNotImplementedException e) {
                e.printStackTrace();
            }
        }
        try {
            juggler.addStudent(Color.RED);
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(priest.getGuestsLimit(), priest.countStudents());
            assertEquals(princess.getGuestsLimit(), princess.countStudents());
            assertEquals(juggler.getGuestsLimit(), juggler.countStudents());
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Test to count students, considering the color
     */
    @Test
    public void countStudentsWithColorTest() {
        try {
            juggler.addStudent(Color.BLUE);
            priest.addStudent(Color.BLUE);
            princess.addStudent(Color.BLUE);

            juggler.addStudent(Color.RED);
            priest.addStudent(Color.RED);
            princess.addStudent(Color.RED);

            juggler.addStudent(Color.YELLOW);
            priest.addStudent(Color.YELLOW);
            princess.addStudent(Color.YELLOW);

            juggler.addStudent(Color.GREEN);
            priest.addStudent(Color.GREEN);
            princess.addStudent(Color.GREEN);

            juggler.addStudent(Color.PINK);
            juggler.addStudent(Color.PINK);

        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }

        try {
            assertEquals(1, priest.countStudents(Color.BLUE));
            assertEquals(1, juggler.countStudents(Color.BLUE));
            assertEquals(1, princess.countStudents(Color.BLUE));

            assertEquals(1, priest.countStudents(Color.YELLOW));
            assertEquals(1, juggler.countStudents(Color.YELLOW));
            assertEquals(1, princess.countStudents(Color.YELLOW));

            assertEquals(1, priest.countStudents(Color.GREEN));
            assertEquals(1, juggler.countStudents(Color.GREEN));
            assertEquals(1, princess.countStudents(Color.GREEN));

            assertEquals(1, priest.countStudents(Color.RED));
            assertEquals(1, juggler.countStudents(Color.RED));
            assertEquals(1, princess.countStudents(Color.RED));

            assertEquals(2, juggler.countStudents(Color.PINK));
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Test for getter getGuestLimit
     */
    @Test
    @Before
    public void getGuestsLimit() {
        try {
            assertEquals(4, priest.getGuestsLimit());
            assertEquals(4, princess.getGuestsLimit());
            assertEquals(6, juggler.getGuestsLimit());
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test for getter getGuestLimit
     */
    @Test
    @Before
    public void getGuestsChangeLimit() {
        try {
            assertEquals(1, priest.getGuestsChangeLimit());
            assertEquals(1, princess.getGuestsChangeLimit());
            assertEquals(3, juggler.getGuestsChangeLimit());
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Special cards with students method SpecialCardName")
    @ParameterizedTest
    @EnumSource(SpecialCardName.class)
    public void testSpecialCardsWithStudents(SpecialCardName specialCardName){
        switch (specialCardName){
            case PRINCESS, PRIEST, JUGGLER: assertTrue(SpecialCardName.getSpecialCardsWithStudents().contains(specialCardName)); break;
            default: assertFalse(SpecialCardName.getSpecialCardsWithStudents().contains(specialCardName));
        }
    }
}
