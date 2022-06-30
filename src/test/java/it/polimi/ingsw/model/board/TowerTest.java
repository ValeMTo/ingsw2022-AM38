package it.polimi.ingsw.model.board;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;


public class TowerTest {

    /**
     * Tests that the toTower method for tower return the right Tower
     * @param towerColor : the string of the tower colo
     */
    @ParameterizedTest
    @CsvSource({"white","BLACK","GraY"})
    public void testToTower(String towerColor){
        switch (towerColor){
            case "white": assertEquals(Tower.WHITE, Tower.toTower(towerColor)); break;
            case "BLACK": assertEquals(Tower.BLACK, Tower.toTower(towerColor)); break;
            case "GraY": assertEquals(Tower.GRAY, Tower.toTower(towerColor)); break;
        }
    }

}
