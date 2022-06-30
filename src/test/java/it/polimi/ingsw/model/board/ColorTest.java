package it.polimi.ingsw.model.board;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;


public class ColorTest {

    /**
     * Tests the abbreviations returned by Color for each color
     * @param color the color to test
     */
    @ParameterizedTest
    @EnumSource(Color.class)
    public void testGetAbbreviation(Color color){
        switch (color){
            case BLUE: assertEquals("B",Color.getAbbreviation(color));
                break;
            case YELLOW: assertEquals("Y",Color.getAbbreviation(color));
                break;
            case GREEN: assertEquals("G",Color.getAbbreviation(color));
                break;
            case PINK: assertEquals("P",Color.getAbbreviation(color));
                break;
            case RED: assertEquals("R",Color.getAbbreviation(color));
                break;
            default: assertEquals("", Color.getAbbreviation(color));
        }
    }


    @ParameterizedTest
    @CsvSource({"B","Y","G","P","R"})
    public void testGetColorFromAbbreviation(String abbreviation){
        switch (abbreviation){
            case "B": assertEquals(Color.BLUE,Color.toColor(abbreviation));
                break;
            case "Y": assertEquals(Color.YELLOW,Color.toColor(abbreviation));
                break;
            case "G": assertEquals(Color.GREEN,Color.toColor(abbreviation));
                break;
            case "P" : assertEquals(Color.PINK,Color.toColor(abbreviation));
                break;
            case "R": assertEquals(Color.RED,Color.toColor(abbreviation));
                break;
            default: assertNull(Color.toColor(abbreviation));
        }
    }



}
