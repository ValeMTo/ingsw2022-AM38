
package it.polimi.ingsw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;


public class BagTest {


    /**
     * The following first two amounts refer to the total students in game "physical" components, whereas
     * the last two amounts refer to the number of students placed in the bag right before the first round of the game,
     * after the 10 students have been placed on the islands.
     */

    private final int maxTotalStudents = 130;
    private final int TotalStudentsPerColor = 26;
    private final int maxInitStudents = 120;
    private final int initStudentsPerColor = 24;

    Bag testedBag = new Bag();


    private Color getEnumParameter(String s) {
        if (Color.BLUE.toString().equals(s)) return Color.BLUE;
        if (Color.YELLOW.toString().equals(s)) return Color.YELLOW;
        if (Color.PINK.toString().equals(s)) return Color.PINK;
        if (Color.GREEN.toString().equals(s)) return Color.GREEN;
        if (Color.RED.toString().equals(s)) return Color.RED;
        return null;
    }

    // some handy methods to quickly fill and empty the bag for the various tests

    private void emptyBag(Bag b) {
       int previousNum = b.getStudentNumber();
       for(int i=0; i<previousNum; i++) {
           b.drawStudent();
       }

    }

    private void addMultipleStudents(Bag b, Color color, int amount) {
        for(int i=0; i<amount; i++ ){
           b.addStudent(color);                // adds multiple students of only one given color
        }
    }


    /**
     * Test to make sure the constructor method creates the bag with the correct number of students.
     * Also tests the getter of bag's studentNumber and the amount of student for each color.
     */
    @Test
    @DisplayName("Test Bag constructor")
    public void bagConstructorTest() {
        Bag bag = new Bag();
        assertEquals(10, bag.getStudentNumber());

        for(Color c : Color.values()) {
            assertEquals(2, bag.countStudentByColor(c));
        }
    }


    /**
     * Test to make sure the initialisation method of the Bag sets the correct number of students.
     * Also tests the getter of bag's studentNumber.
     */
    @Test
    @DisplayName("Test Bag initialisation")
    public void bagInitialisationTest() {
        testedBag.initialise();
        assertEquals(maxInitStudents, testedBag.getStudentNumber());

        for(Color c : Color.values()) {
            assertEquals(initStudentsPerColor, testedBag.countStudentByColor(c));
        }
    }

    /**
     * Tests the special case in which all the students are drawn from the bag, including the last student left,
     * and then the bag is empty.
     * The test is executed for different initial amounts of students

     * @param amount: the previous amount of student before the bag is emptied.
     */

    @Test
    @DisplayName("Draw all students and isEmpty")
    public void drawAndEmptyBagTest() {
        assertFalse(testedBag.isEmpty());       // initially the constructor has filled the testedBag with 10 students
        emptyBag(testedBag);
        assertEquals(0, testedBag.getStudentNumber());
        assertTrue(testedBag.isEmpty());
    }


    /**
     * Tests the color of the student drawn by the drawStudent() method, considering the scenario in which the bag
     * contains only one student of a given color.
     * The test is repeated for each color and checks if the returned color is correct.

     * @param col: the color of the only student drawn from the bag, which is expected to be returned.
     */

    @ParameterizedTest
    @CsvSource({"PINK", "BLUE", "RED", "YELLOW", "GREEN"})
    @DisplayName("Test drawStudent() returned color")
    public void checkDrawnColorTest(String col) {
        Color color = getEnumParameter(col);
        assertFalse(testedBag.isEmpty());
        emptyBag(testedBag);
        testedBag.addStudent(color);                     // Adding only one student of color "color"
        Color returnedColor = testedBag.drawStudent();
        assertEquals(color, returnedColor);

    }



    /** Tests if the current total number of students in the bag is correctly updated after adding multiple students
     * @param studentsToAdd: the number of students to be added
     * @param col : the color of the students to be added
     */
    @ParameterizedTest
    @CsvSource({"12,PINK", "4,BLUE", "0,RED", "12,YELLOW", "8,GREEN"})
    @DisplayName("Check current studentNumber after addStudent")
    public void updateStudentNumberAfterAdd(int studentsToAdd, String col) {
        Color color = getEnumParameter(col);
        int previousStudNumber = testedBag.getStudentNumber();
        if (color == null) {
            System.out.println("Null exception test");
            return;
        }
        addMultipleStudents(testedBag, color, studentsToAdd);
        assertEquals(previousStudNumber + studentsToAdd, testedBag.getStudentNumber());
    }



    /** Tests if the current total number of students in the bag is correctly updated after drawing multiple students
     * @param studentsToDraw: the number of students to be drawn
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 10, 34})
    @DisplayName("Check current studentNumber after multiple drawStudents")
    public void updateStudentNumberAfterDraw(int studentsToDraw) {

        testedBag.initialise();    // initialises the bag with 120 students, 24 per color
        int previousStudNumber = testedBag.getStudentNumber();
        for(int i = 0; i < studentsToDraw; i++) {
            testedBag.drawStudent();
        }
        assertEquals(previousStudNumber - studentsToDraw, testedBag.getStudentNumber());
    }




}