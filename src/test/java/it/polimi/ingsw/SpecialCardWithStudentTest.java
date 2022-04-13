package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SpecialCardWithStudentTest {

    SpecialCard priest = new SpecialCardWithStudent(SpecialCardName.PRIEST);
    SpecialCard juggler = new SpecialCardWithStudent(SpecialCardName.JUGGLER);
    SpecialCard princess = new SpecialCardWithStudent(SpecialCardName.PRINCESS);

    /**
     * Test to count students, without considering the color
     */
    @Test
    public void countStudentsTest(){
    }
}
