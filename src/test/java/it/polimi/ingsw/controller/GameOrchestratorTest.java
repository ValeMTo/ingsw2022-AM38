package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.AlreadyUsedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

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
                List<Integer> a = new ArrayList<>(exc.getUsableIndexes());
                try {
                    game.chooseCard(a.get(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Does a random move student phase
     *
     * @param game : GameOrchestrator that we want to evolve to the phase MoveStudent after the planning
     */
    private void doAMoveStudents(GameOrchestrator game) {
        Random random = new Random();
        while (game.getCurrentPhase() == PhaseEnum.ACTION_MOVE_STUDENTS) {
            try {
                if (random.nextBoolean()) game.moveStudent(Color.values()[random.nextInt(Color.values().length)]);
                else game.moveStudent(Color.values()[random.nextInt(Color.values().length)], 1);
            } catch (IncorrectPhaseException exception) {
                exception.printStackTrace();
            }
        }
    }


    /**
     * Does a random movement of motherNature
     *
     * @param game : GameOrchestrator that we want to evolve to the phase MoveStudent after the planning
     */
    private void doAMoveMotherNature(GameOrchestrator game) {
        Random random = new Random();
        int max = 12;
        while (game.getCurrentPhase() == PhaseEnum.ACTION_MOVE_MOTHER_NATURE) {
            try {
                game.moveMotherNature(random.nextInt(max));
            } catch (IslandOutOfBoundException exc) {
                System.out.println("GOT ISLAND OUT OF BOUND");
                max = exc.getHigherBound();
            } catch (IncorrectPhaseException exc) {
                exc.printStackTrace();
            }
        }
    }

    /**
     * Does an entire round using the support method of this class
     *
     * @param game : GameOrchestrator that we want to evolve to the phase MoveStudent after the planning
     */
    private void doARound(GameOrchestrator game) {
        doAPlanningPhase(game);
        int counter = 0;
        while (PhaseEnum.PLANNING != game.getCurrentPhase() && PhaseEnum.END != game.getCurrentPhase()) {
            doAMoveStudents(game);
            doAMoveMotherNature(game);
            final int idx = counter;
            assertDoesNotThrow(() -> game.chooseCloud(idx + 1));
            counter++;
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
        //Checks the GameMode
        assertEquals(expert, game.isExpert());
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
     * Tests that it is not allowed to use the same card while there are other card usables
     */
    @Test
    @DisplayName("SameCard used by two players exception")
    public void sameCardUsed() {
        GameOrchestrator game = setup(false, false);
        assertDoesNotThrow(() -> game.chooseCard(1));
        assertThrows(AlreadyUsedException.class, () -> game.chooseCard(1));
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

    /**
     * Tests that motherNature moves correctly in nominal situations
     */
    @Test
    @DisplayName("Simple motherNature movement")
    public void simpleMotherNatureMovement() {
        GameOrchestrator game = setup(false, false);
        try {
            game.chooseCard(6);
            game.chooseCard(8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.doAMoveStudents(game);
        assertDoesNotThrow(() -> game.moveMotherNature(3));
        assertEquals(PhaseEnum.ACTION_CHOOSE_CLOUD, game.getCurrentPhase());
    }

    /**
     * Tests that if the steps of the played card are not sufficient to reach an island, the method return false and does not change phase
     */
    @Test
    @DisplayName("Not enough step movement exception of mother nature")
    public void notEnoughStepMovement() {
        GameOrchestrator game = setup(false, false);
        try {
            game.chooseCard(2);
            game.chooseCard(5);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.doAMoveStudents(game);
        try {
            assertFalse(game.moveMotherNature(10));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertEquals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE, game.getCurrentPhase());
    }

    /**
     * Tests the choice of a cloud by a player and the phase change
     */
    @Test
    @DisplayName("Choose cloud test - nominal")
    public void normalChooseCloud() {
        GameOrchestrator game = setup(false, false);
        doAPlanningPhase(game);
        doAMoveStudents(game);
        doAMoveMotherNature(game);
        assertDoesNotThrow(() -> game.chooseCloud(1));
        assertEquals(PhaseEnum.ACTION_MOVE_STUDENTS, game.getCurrentPhase());
    }

    /**
     * Tests if two player can correctly do their respective cloud choice.
     */
    @Test
    @DisplayName("Choose cloud test - two players nominal")
    public void nominalChooseCloudTwoPlayers() {
        GameOrchestrator game = setup(false, false);
        doAPlanningPhase(game);
        doAMoveStudents(game);
        doAMoveMotherNature(game);
        assertDoesNotThrow(() -> game.chooseCloud(1));
        doAMoveStudents(game);
        doAMoveMotherNature(game);
        assertDoesNotThrow(() -> game.chooseCloud(2));
        assertEquals(PhaseEnum.PLANNING, game.getCurrentPhase());
    }

    /**
     * Tests that the AlreadyUsedException is thrown if the cloud chosen has been already used
     */
    @Test
    @DisplayName("Choose cloud test - already used cloud exception")
    public void alreadyChosenCloudTwoPlayers() {
        GameOrchestrator game = setup(false, false);
        doAPlanningPhase(game);
        doAMoveStudents(game);
        doAMoveMotherNature(game);
        assertDoesNotThrow(() -> game.chooseCloud(1));
        doAMoveStudents(game);
        doAMoveMotherNature(game);
        assertThrows(AlreadyUsedException.class, () -> game.chooseCloud(1));
        assertEquals(PhaseEnum.ACTION_CHOOSE_CLOUD, game.getCurrentPhase());
    }

    /**
     * Does two rounds
     *
     * @param threePlayers : indicates if there are 2 or 3 players
     */
    @ParameterizedTest
    @DisplayName("Two round test")
    @ValueSource(booleans = {false, true})
    public void twoRoundTest(boolean threePlayers) {
        GameOrchestrator game = setup(threePlayers, false);
        Set<PhaseEnum> conditionEndRound = new HashSet<>();
        conditionEndRound.add(PhaseEnum.PLANNING);
        conditionEndRound.add(PhaseEnum.END);
        doARound(game);
        assertTrue(conditionEndRound.contains(game.getCurrentPhase()));
    }

    /**
     * Tests an entire play, expecting into 10 rounds the end signaling.
     */
    /*@ParameterizedTest
    @DisplayName("End of the game in 10 rounds")
    @CsvSource("{true,true},{true,false},{false,true},{false,false}")
    public void endInTenRounds(boolean threePlayers, boolean expert) {
        GameOrchestrator game = setup(threePlayers, expert);
        int numOfRound = 0;
        while (game.getCurrentPhase() != PhaseEnum.END) {
            assertTrue(numOfRound < 10);
            numOfRound++;
            doARound(game);
        }
        assertEquals(PhaseEnum.END, game.getCurrentPhase());
        assertThrows(IncorrectPhaseException.class, () -> game.moveStudent(Color.BLUE));
    }*/
}
