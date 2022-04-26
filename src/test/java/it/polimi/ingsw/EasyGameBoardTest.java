package it.polimi.ingsw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EasyGameBoardTest {

    List<String> nameString = new ArrayList<String>();

    EasyGameBoardTest() {
        nameString.add("Vale");
        nameString.add("Fra");
    }

    /**
     * Tests that the functionNotImplementedException is correctly thrown
     */
    @Test
    public void updateProfessorOwnershipIfTie() {
        EasyGameBoard easyGameBoard = new EasyGameBoard(2, nameString);
        assertThrows(FunctionNotImplementedException.class, () -> easyGameBoard.updateProfessorOwnershipIfTie());
    }

    /**
     * Tests that the functionNotImplementedException is correctly thrown
     */
    @Test
    public void disableInfluence() {
        EasyGameBoard easyGameBoard = new EasyGameBoard(2, nameString);
        assertThrows(FunctionNotImplementedException.class, () -> easyGameBoard.disableInfluence(1));
    }

    /**
     * Tests that the functionNotImplementedException is correctly thrown
     */
    @Test
    public void increaseInfluence() {
        EasyGameBoard easyGameBoard = new EasyGameBoard(2, nameString);
        assertThrows(FunctionNotImplementedException.class, () -> easyGameBoard.increaseInfluence());
    }

    /**
     * Tests that the functionNotImplementedException is correctly thrown
     */
    @Test
    public void disableColorInfluence() {
        EasyGameBoard easyGameBoard = new EasyGameBoard(2, nameString);
        assertThrows(FunctionNotImplementedException.class, () -> easyGameBoard.disableColorInfluence(Color.BLUE));
    }

    /**
     * Tests that the functionNotImplementedException is correctly thrown
     */
    @Test
    public void resetAllTurnFlags() {
        EasyGameBoard easyGameBoard = new EasyGameBoard(2, nameString);
        assertThrows(FunctionNotImplementedException.class, () -> easyGameBoard.resetAllTurnFlags());
    }

    /**
     * Tests that the functionNotImplementedException is correctly thrown
     */
    @Test
    public void increaseMovementMotherNature() {
        EasyGameBoard easyGameBoard = new EasyGameBoard(2, nameString);
        assertThrows(FunctionNotImplementedException.class, () -> easyGameBoard.increaseMovementMotherNature());
    }

    /**
     * Tests that the influence is correctly computed with two players
     */
    @Test
    public void influenceComputation() {
        EasyGameBoard easyGameBoard = new EasyGameBoard(2, nameString);
        try {
            assertNull(easyGameBoard.computeInfluence(1));
            easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.BLUE);
            easyGameBoard.addStudent(StudentCounter.ISLAND, Color.BLUE, 1);
            easyGameBoard.updateProfessorOwnership();
            assertEquals(easyGameBoard.getCurrentPlayer(), easyGameBoard.computeInfluence(1));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
