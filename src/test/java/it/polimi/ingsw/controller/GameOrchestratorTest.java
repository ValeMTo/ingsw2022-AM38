package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.AlreadyUsedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameOrchestratorTest {
    /**
     * Needed to have a list of nicknames for the GameOrchestrator constructor
     *
     * @param threePlayers indicates if three or two players are needed into the list
     * @return the List containing the nicknames of the players
     */
    private List<String> getNicknames(boolean threePlayers) {
        List<String> nicknames = new ArrayList<>();
        nicknames.add("Fra");
        nicknames.add("Vale");
        if (threePlayers) nicknames.add("Nick");
        return nicknames;
    }

    /**
     * Creates the GameOrchestrator with given setup parameters
     *
     * @param threePlayers : if three players or if false 2 players
     * @param expert       : if expert gameMode or if false easy
     * @return a new GameOrchestrator created with given parameters
     */
    private GameOrchestrator setup(boolean threePlayers, boolean expert) {
        return new GameOrchestrator(getNicknames(threePlayers), expert);
    }

    /**
     * Do a planning turn, plays some assistant cards
     *
     * @param game : GameOrchestrator that we want to evolve to the phase MoveStudent after the planning
     */
    private void doAPlanningPhase(GameOrchestrator game) {
        Random random = new Random();
        int card;
        while (game.getCurrentPhase() == PhaseEnum.PLANNING) {
            try {
                card = random.nextInt(9) + 1;
                System.out.println("Player " + game.getActivePlayer() + " tries to play card " + card);
                game.chooseCard(card);
            } catch (IncorrectPhaseException exc) {
                exc.printStackTrace();
            } catch (AlreadyUsedException exc) {
                System.out.println("Already used card");
            }
        }
    }

    /**
     * Controls that the setup and constructor of GameOrchestrator work well
     *
     * @param threePlayers : indicates if there are 2 or 3 players
     * @param expert       : set true if the gameMode is expert
     */
    @ParameterizedTest
    @DisplayName("Initial Setup test")
    @CsvSource("{true,true},{true,false},{false,true},{false,false}")
    public void setupTest(boolean threePlayers, boolean expert) {
        GameOrchestrator game = new GameOrchestrator(getNicknames(threePlayers), expert);
        //Checks that active phase is correct and active player is not null and contained into the List of nicknames
        assertEquals(PhaseEnum.PLANNING, game.getCurrentPhase());
        assertNotNull(game.getActivePlayer());
        assertTrue(getNicknames(threePlayers).contains(game.getActivePlayer()));
        //Checks that the players are all contained into the getPlayersTower returned map
        assertTrue(game.getPlayersTower().containsKey(getNicknames(threePlayers).get(0)));
        assertTrue(game.getPlayersTower().containsKey(getNicknames(threePlayers).get(1)));
        if (threePlayers) assertTrue(game.getPlayersTower().containsKey(getNicknames(threePlayers).get(2)));
        //Checks that all the TowerColors (GRAY only if three players) are contained into the map
        assertTrue(game.getPlayersTower().containsValue(Tower.WHITE));
        assertTrue(game.getPlayersTower().containsValue(Tower.BLACK));
        if (threePlayers) assertTrue(game.getPlayersTower().containsKey(Tower.GRAY));
    }

    /**
     * Tests that the method only for other phases than planning throws IncorrectPhaseException
     *
     * @param threePlayers : indicates if there are 2 or 3 players
     * @param expert       : set true if the gameMode is expert
     */
    @ParameterizedTest
    @DisplayName("Not allowed actions in planning phase")
    @CsvSource("{true,true},{true,false},{false,true},{false,false}")
    public void notAllowedActionsPlanning(boolean threePlayers, boolean expert) {
        GameOrchestrator game = setup(threePlayers, expert);
        assertThrows(IncorrectPhaseException.class, () -> game.moveStudent(Color.BLUE));
        assertThrows(IncorrectPhaseException.class, () -> game.moveMotherNature(2));
        assertThrows(IncorrectPhaseException.class, () -> game.chooseCloud(1));
    }

    /**
     * Tests a normal planning phase, where the players use their cards and the gameOrchestrator evolve to the next phase (moveStudent)
     *
     * @param threePlayers : indicates if there are 2 or 3 players
     * @param expert       : set true if the gameMode is expert
     */
    @ParameterizedTest
    @DisplayName("Normal planning turn")
    @CsvSource("{true,true},{true,false},{false,true},{false,false}")
    public void normalPlanningTurn(boolean threePlayers, boolean expert) {
        GameOrchestrator game = setup(threePlayers, expert);
        try {
            assertTrue(game.chooseCard(1));
            assertTrue(game.chooseCard(2));
            if (threePlayers) assertTrue(game.chooseCard(3));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals(PhaseEnum.ACTION_MOVE_STUDENTS, game.getCurrentPhase());
    }

    /**
     * Sees that the exceptions are not thrown in a normal planning phase interaction
     *
     * @param threePlayers : indicates if there are 2 or 3 players
     * @param expert       : set true if the gameMode is expert
     */
    @ParameterizedTest
    @DisplayName("Normal planning turn - No throw test")
    @CsvSource("{true,true},{true,false},{false,true},{false,false}")
    public void normalPlanningTurnNotThrow(boolean threePlayers, boolean expert) {
        GameOrchestrator game = setup(threePlayers, expert);
        assertDoesNotThrow(() -> game.chooseCard(1));
        assertDoesNotThrow(() -> game.chooseCard(2));
        if (threePlayers) assertDoesNotThrow(() -> game.chooseCard(3));
        assertEquals(PhaseEnum.ACTION_MOVE_STUDENTS, game.getCurrentPhase());
    }

    /**
     * Tests that the player order of the action phase is correctly set
     *
     * @param firstPlayerFirst : true if the first player uses the lowest priority card, otherwise false
     */
    @ParameterizedTest
    @DisplayName("Simple test for the assistant card priority order")
    @ValueSource(booleans = {true, false})
    public void assistantCardPlayTwoPlayers(boolean firstPlayerFirst) {
        GameOrchestrator game = setup(false, false);
        try {
            if (firstPlayerFirst) {
                if (game.getActivePlayer().equalsIgnoreCase(getNicknames(false).get(0))) {
                    game.chooseCard(1);
                    assertEquals(getNicknames(false).get(1), game.getActivePlayer());
                    game.chooseCard(2);
                } else {
                    game.chooseCard(2);
                    assertEquals(getNicknames(false).get(0), game.getActivePlayer());
                    game.chooseCard(1);
                }

            } else {
                if (game.getActivePlayer().equalsIgnoreCase(getNicknames(false).get(0))) {
                    game.chooseCard(2);
                    assertEquals(getNicknames(false).get(1), game.getActivePlayer());
                    game.chooseCard(1);
                } else {

                    game.chooseCard(1);
                    assertEquals(getNicknames(false).get(0), game.getActivePlayer());
                    game.chooseCard(2);
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals(PhaseEnum.ACTION_MOVE_STUDENTS, game.getCurrentPhase());
        if (firstPlayerFirst) assertEquals(getNicknames(false).get(0), game.getActivePlayer());
        else assertEquals(getNicknames(false).get(1), game.getActivePlayer());

    }

    /**
     * Sees that the player can move correctly three times the students from the SchoolEntrance to the DiningRoom
     *
     * @param threePlayers : indicates if there are 2 or 3 players
     * @param expert       : set true if the gameMode is expert
     */
    @ParameterizedTest
    @DisplayName("Normal move student turn of a player")
    @CsvSource("{true,true},{true,false},{false,true},{false,false}")
    public void normalMoveStudentPhaseOnlyDiningRoom(boolean threePlayers, boolean expert) {
        GameOrchestrator game = setup(threePlayers, expert);
        doAPlanningPhase(game);
        String activePlayer = game.getActivePlayer();
        int counter = 0;
        try {
            do {
                for (Color color : Color.values()) {
                    if (counter < 3 && game.moveStudent(color)) counter++;
                    System.out.println("Test color " + color);
                }
            } while (counter < 3);
        } catch (IncorrectPhaseException exc) {
            exc.printStackTrace();
            System.out.println("ERROR");
        }
        assertEquals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE, game.getCurrentPhase());
        assertEquals(activePlayer, game.getActivePlayer());
        assertThrows(IncorrectPhaseException.class, () -> game.moveStudent(Color.BLUE));
    }


    //TODO: active player problem...
    //TODO: in first rounds not allowed to use same card!

    /**
     * Sees that the player can move correctly three times the students from the SchoolEntrance to one Island
     *
     * @param threePlayers : indicates if there are 2 or 3 players
     * @param expert       : set true if the gameMode is expert
     */
    @ParameterizedTest
    @DisplayName("Normal move student turn of a player")
    @CsvSource("{true,true},{true,false},{false,true},{false,false}")
    public void normalMoveStudentPhaseOnlyIsland(boolean threePlayers, boolean expert) {
        GameOrchestrator game = setup(threePlayers, expert);
        doAPlanningPhase(game);
        String activePlayer = game.getActivePlayer();
        int counter = 0;
        try {
            do {
                for (Color color : Color.values()) {
                    System.out.println("Player " + activePlayer + " checking color " + color);
                    if (counter < 3 && game.moveStudent(color, 3)) {
                        System.out.println("Ok counter updated to " + counter);
                        counter++;
                    }
                }
            } while (counter < 3);
        } catch (IncorrectPhaseException exc) {
            exc.printStackTrace();
            System.out.println("ERROR");
        }
        assertEquals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE, game.getCurrentPhase());
        assertEquals(activePlayer, game.getActivePlayer());
        assertThrows(IncorrectPhaseException.class, () -> game.moveStudent(Color.BLUE));
    }
}
