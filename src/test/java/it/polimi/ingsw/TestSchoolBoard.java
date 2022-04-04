package it.polimi.ingsw;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Assertions.*;

import static java.lang.Math.min;
import static org.junit.jupiter.api.Assertions.*;

public class TestSchoolBoard {
    private final int maxStudentDiningRoomColor = 10;
    private final int maxStudentEntranceColor = 7;

    SchoolBoard schoolBoard = new SchoolBoard(maxStudentEntranceColor,maxStudentDiningRoomColor);

    private Color getEnumParameter (String s){
        if(s == Color.BLUE.toString()) return Color.BLUE;
        if(s == Color.YELLOW.toString()) return Color.YELLOW;
        if(s == Color.PINK.toString()) return Color.PINK;
        if(s == Color.GREEN.toString()) return Color.GREEN;
        if(s == Color.RED.toString()) return Color.RED;
        return null;
    }

    /**
     * Test for the superior limit of the number of students to add in the diningRoom
     * @param num : num of students(>10 to test that from the eleventh are not added)
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,BLUE","11,BLUE","11,RED","12,YELLOW"})
    @DisplayName("Test superior limit addStudent to Dining Room")
    public void adderTestSuperiorLimitDining(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num;i++)
            {
                if(i<maxStudentDiningRoomColor) {
                    assertTrue(schoolBoard.addStudentDiningRoom(color));
                }
                else
                    assertFalse(schoolBoard.addStudentDiningRoom(color));
            }
    }

    /**
     * Test for the superior limit of the number of students to add in the entrance
     * @param num : num of students(>7/9 to test that from beyond the limit are not added)
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,BLUE","11,BLUE","11,RED","12,YELLOW"})
    @DisplayName("Test superior limit addStudent to entrance")
    public void adderTestSuperiorLimitEntrance(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num;i++)
        {
            if(i<maxStudentEntranceColor) {
                assertTrue(schoolBoard.addStudentEntrance(color));
            }
            else
                assertFalse(schoolBoard.addStudentEntrance(color));
        }
    }

    /**
     * ParameterizedTest to test if the number of students added by Color correspond with the count (or 10 if they exceed the limit)
     * @param num : number of students to add
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK","4,BLUE","0,RED","12,YELLOW","8,GREEN"})
    @DisplayName("Test add students DiningRoom and count")
    public void adderAndCountDiningRoomTest(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num;i++)
    {
        if(i<maxStudentDiningRoomColor){
            assertTrue(schoolBoard.addStudentDiningRoom(color));
        }
        else
            assertFalse(schoolBoard.addStudentDiningRoom(color));
    }
        if(num<maxStudentDiningRoomColor)
            assertEquals(num,schoolBoard.countStudentsDiningRoom(color));
        else
            assertEquals(maxStudentDiningRoomColor,schoolBoard.countStudentsDiningRoom(color));
    }

    /**
     * ParameterizedTest to test if the number of students added by Color to entrance correspond with the count (or 7/10 if they exceed the limit)
     * @param num : number of students to add
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"4,PINK","4,BLUE","0,RED","12,YELLOW","8,GREEN"})
    @DisplayName("Test add students entrance and count")
    public void adderAndCountEntranceTest(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num;i++)
        {
            if(i<maxStudentEntranceColor){
                assertTrue(schoolBoard.addStudentEntrance(color));
            }
            else
                assertFalse(schoolBoard.addStudentEntrance(color));
        }
        if(num<maxStudentEntranceColor)
            assertEquals(num,schoolBoard.countStudentsEntrance(color));
        else
            assertEquals(maxStudentEntranceColor,schoolBoard.countStudentsEntrance(color));
    }

    /**
     * Parameterized test for the remove, to be sure it removes students only the right number of times
     * and do not remove below zero
     * @param num : number of students added (if beyond the limit they will not be added...)
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK","4,BLUE","0,RED","12,YELLOW","8,GREEN"})
    @DisplayName("Add and remove students by color in DiningRoom")
    public void removeDiningRoom(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num&&i<maxStudentDiningRoomColor;i++){
            schoolBoard.addStudentDiningRoom(color);
        }
        for(int i=0;i<num&&i<maxStudentDiningRoomColor;i++){
            assertTrue(schoolBoard.removeStudentDiningRoom(color));
        }
        assertFalse(schoolBoard.removeStudentDiningRoom(color));
        assertFalse(schoolBoard.removeStudentDiningRoom(color));
    }

    /**
     * Parameterized test for the remove in entrance, to be sure it removes students only the right number of times
     * and do not remove below zero
     * @param num : number of students added (if beyond the limit they will not be added...)
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK","4,BLUE","0,RED","12,YELLOW","8,GREEN"})
    @DisplayName("Add and remove students by color in entrance")
    public void removeEntrance(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num&&i<maxStudentEntranceColor;i++){
            schoolBoard.addStudentEntrance(color);
        }
        for(int i=0;i<num&&i<maxStudentEntranceColor;i++){
            assertTrue(schoolBoard.removeStudentEntrance(color));
        }
        assertFalse(schoolBoard.removeStudentEntrance(color));
        assertFalse(schoolBoard.removeStudentEntrance(color));
    }


    // CHECK FROM HERE -- DONE FAST...

    /**
     * Parameterized test for the remove and count to be sure the number is consistent
     * @param num : number of students added (if beyond the limit they will not be added...)
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"8,PINK","4,BLUE","2,RED","10,YELLOW","8,GREEN"})
    @DisplayName("Add, remove and count students by color in DiningRoom")
    public void removeAndCountDiningRoom(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num;i++){
            schoolBoard.addStudentDiningRoom(color);
        }
        for(int i=0;i<num&&i<maxStudentDiningRoomColor;i++){
            assertTrue(schoolBoard.removeStudentDiningRoom(color));
            assertEquals(schoolBoard.countStudentsDiningRoom(color), min(num,maxStudentDiningRoomColor)-(i+1));
        }

    }

    /**
     * Parameterized test for the remove in entrance, to be sure it removes students only the right number of times
     * and do not remove below zero
     * @param num : number of students added (if beyond the limit they will not be added...)
     * @param color : color of the students to add
     */
    @ParameterizedTest
    @CsvSource({"12,PINK","4,BLUE","0,RED","12,YELLOW","8,GREEN"})
    @DisplayName("Add and remove students by color in entrance")
    public void removeAndCountEntrance(int num, String col){
        Color color = getEnumParameter(col);
        if(color==null){
            System.out.println("Null exception test");
            return;}
        for(int i=0;i<num;i++){
            schoolBoard.addStudentEntrance(color);
        }
        for(int i=0;i<num&&i<maxStudentEntranceColor;i++){
            assertTrue(schoolBoard.removeStudentEntrance(color));
            assertEquals(schoolBoard.countStudentsEntrance(color), min(num,maxStudentEntranceColor)-(i+1));
        }
    }

}
