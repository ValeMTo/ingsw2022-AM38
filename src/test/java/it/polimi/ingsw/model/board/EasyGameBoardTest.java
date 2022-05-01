package it.polimi.ingsw.model.board;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.EasyGameBoard;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.board.Tower;
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


    /**
     * Tests that the influence is correctly computed with three players and more complex scenario
     */
    @Test
    public void influenceComputationThreePlayers() {
        nameString.add("Nick");
        EasyGameBoard easyGameBoard = new EasyGameBoard(3, nameString);
        try {
            assertNull(easyGameBoard.computeInfluence(1));
            //P0 : 1 BLUE
            assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.BLUE, easyGameBoard.getPlayerPosition(Tower.WHITE)));
            //P1 : 1 BLUE + 1 PINK
            assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.BLUE, easyGameBoard.getPlayerPosition(Tower.BLACK)));
            assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.PINK, easyGameBoard.getPlayerPosition(Tower.BLACK)));
            //P2 : 2 PINK
            for (int i = 0; i < 2; i++)
                assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.PINK, easyGameBoard.getPlayerPosition(Tower.GRAY)));

            //Island : 5 BLUE + 2 PINK
            for (int i = 0; i < 5; i++)
                assertTrue(easyGameBoard.addStudent(StudentCounter.ISLAND, Color.BLUE, 1));
            for (int i = 0; i < 2; i++)
                assertTrue(easyGameBoard.addStudent(StudentCounter.ISLAND, Color.PINK, 1));
            assertNull(easyGameBoard.computeInfluence(1));
            easyGameBoard.updateProfessorOwnership();
            assertEquals(Tower.GRAY, easyGameBoard.computeInfluence(1));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Tests that the influence is correctly computed with three players and more complex scenario
     */
    @Test
    public void influenceComputationTower() {
        nameString.add("Nick");
        EasyGameBoard easyGameBoard = new EasyGameBoard(3, nameString);
        try {
            assertNull(easyGameBoard.computeInfluence(1));
            //P0 : 1 BLUE
            assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.BLUE, easyGameBoard.getPlayerPosition(Tower.WHITE)));
            //P1 : 1 BLUE + 1 PINK
            assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.BLUE, easyGameBoard.getPlayerPosition(Tower.BLACK)));
            assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.PINK, easyGameBoard.getPlayerPosition(Tower.BLACK)));
            //P2 : 2 PINK
            for (int i = 0; i < 2; i++)
                assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.PINK, easyGameBoard.getPlayerPosition(Tower.GRAY)));

            //Island : 5 BLUE + 1 PINK
            for (int i = 0; i < 5; i++)
                assertTrue(easyGameBoard.addStudent(StudentCounter.ISLAND, Color.BLUE, 1));
            assertTrue(easyGameBoard.addStudent(StudentCounter.ISLAND, Color.PINK, 1));
            assertNull(easyGameBoard.computeInfluence(1));
            easyGameBoard.updateProfessorOwnership();
            assertEquals(Tower.GRAY, easyGameBoard.computeInfluence(1));
            //P1 now 3 PINK
            assertTrue(easyGameBoard.addStudent(StudentCounter.DININGROOM, Color.PINK, easyGameBoard.getPlayerPosition(Tower.BLACK)));
            easyGameBoard.updateProfessorOwnership();
            assertEquals(Tower.GRAY, easyGameBoard.computeInfluence(1));

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public List<String> getNicknames(int playerNum) {
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= playerNum; i++)
            list.add("Player" + i);
        return list;
    }
}
