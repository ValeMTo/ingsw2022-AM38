package it.polimi.ingsw;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ExpertGameBoardTest {

    List<String> nameString = new ArrayList<String>();

    ExpertGameBoardTest() {
        nameString.add("Vale");
        nameString.add("Fra");
    }

    /*@ParameterizedTest
    @DisplayName("Test to view if the Influence computation works with different combination of flags")
    @CsvSource("{false,false,false,0},{false,false,false,1},{true,false,false,0},{false,true,false,0},{false,false,true,0},{true,true,true,0},{true,false,false,1},{false,true,false,1},{false,false,true,1},{true,true,true,1}")
    public void computeInfluence(boolean noTowerInfluence, boolean moreInfluencePlayer,boolean noInfluenceByColor, int configuration){
        adder(configuration);


    }**/

    /**
     * Compute simply the computation since no towers are present the influence computation changes the Tower of the most influence player
     *
     * @param testConfigurations : used to change the Integer Streams used to add different quantity of students
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4})
    @DisplayName("Test to view if the compute Influence in standard cases works")
    public void computeInfluence(int testConfigurations) {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        // config 0 : P0=4 PTI, P1=0 PTI
        // config 1 : P0=0 PTI, P1=4 PTI
        // config 2 : P0=2 PTI, P1=1 PTI
        // config 3 : P0=0 PTI, P1=1 PTI
        // config 4 : P0=0 PTI, P1=0 PTI
        adder(testConfigurations, expert);
        Tower towerSet = null;
        switch (testConfigurations) {
            case 0, 2:
                towerSet = Tower.WHITE;
                break;
            case 1, 3:
                towerSet = Tower.BLACK;
                break;
        }
        try {
            expert.updateProfessorOwnership();
            assertEquals(expert.computeInfluence(1), towerSet);
        } catch (IslandOutOfBoundException exc) {
            System.out.println("Exception occurred - ");
            exc.printStackTrace();
        }
    }

    /**
     * Tests the boundaries conditions of the IslandOutOfBoundException throw
     *
     * @param islandPosition : position of the island to test the exception (<1, >12)
     */
    @ParameterizedTest
    @DisplayName("Compute influence Island out of bound")
    @ValueSource(ints = {0, 13})
    public void computeInfluenceExceptionTest(int islandPosition) {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        assertThrows(IslandOutOfBoundException.class, () -> expert.computeInfluence(islandPosition));
    }

    /**
     * Tests that the influence computation is correct if the Towers' influence is disable in the computation.
     * Uses a case where we set a tower and then increase the students of a player's diningRoom to have a difference of one point
     * so the result changes with or without the influence score given by the Tower on the Island
     */
    @Test
    @DisplayName("Compute Influence is correctly affected by Towers and TowerInfluence flag")
    public void computeInfluenceWithoutTowers() {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        adder(3, expert); //Set the Tower as BLACK since Player1 wins
        expert.updateProfessorOwnership();
        try {
            assertEquals(Tower.BLACK, expert.computeInfluence(1));
        } catch (IslandOutOfBoundException exc) {
            System.out.println("Exception occurred - ");
            exc.printStackTrace();
        }
        iterativeAdder(StudentCounter.DININGROOM, Color.values()[3], 0, 3, expert); //Now player 0 has more students, with an influence of 1 POINT (0 Player1)
        expert.disableTowerInfluence();
        expert.updateProfessorOwnership();
        try {
            assertEquals(Tower.WHITE, expert.computeInfluence(1));
        } //P0 (WHITE) should win since the BLACK tower is not counted
        catch (IslandOutOfBoundException exc) {
            System.out.println("Exception occurred - ");
            exc.printStackTrace();
        }

    }

    /**
     * Disables a color for the computation of the Influence and give that color to the Island and player
     * Since the unique color in the diningRoom and Island is disabled the tower is not set
     *
     * @param color : color which does not have to been computed in the Influence computation
     */
    @ParameterizedTest
    @DisplayName("Compute Influence disabling color")
    @EnumSource(Color.class)
    public void computeInfluenceWithoutColor(Color color) {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        expert.disableColorInfluence(color);
        try {
            expert.addStudent(StudentCounter.ISLAND, Color.BLUE, 1);
            expert.addStudent(StudentCounter.DININGROOM, Color.BLUE, 0);
            assertNull(expert.computeInfluence(1));
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    /**
     * Assures that the influence is computed taking into account the professors possessed by player0 if they are with tie
     */
    @Test
    @DisplayName("Compute Influence with professor given to player if tie")
    public void computeInfluenceWithTieProfessors() {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        adder(3, expert); //configuration where most of the professors not naturally possessed by a player
        expert.updateProfessorOwnershipIfTie(); //We favour the player 0
        try {
            assertEquals(expert.computeInfluence(1), Tower.WHITE); //Normally Player1 (BLACK) should win but not with tie professors possessed by player0
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    /**
     * Verifies the correct disable of the Island computation
     */
    @Test
    @DisplayName("Influence computation blocked correctly")
    public void noComputeInfluence() {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        try {
            expert.disableInfluence(1);
            adder(1, expert);
            assertNull(expert.computeInfluence(1));
        } catch (IslandOutOfBoundException exc) {
            System.out.println("Exception occurred - ");
            exc.printStackTrace();
        }

    }

    /**
     * Tests if the exception is thrown exactly when the index of the Island that we want to disable the influence is out of bound
     */
    @Test
    @DisplayName("Verify exception IslandOutOfBound in disableInfluence")
    public void noComputeInfluenceException() {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        try {
            expert.disableInfluence(123);
        } catch (IslandOutOfBoundException exc) {
            assertInstanceOf(IslandOutOfBoundException.class, exc);
        }
    }

    /**
     * Verifies that updateProfessorOwnershipIfTie does not favour a player if there is a tie but the player does not possess
     * the maximum amount of students (tie between other 2 players)
     */
    @Test
    @DisplayName("Three player tie professor test")
    public void computeInfluenceWithTieProfessorsThreePlayers() {
        nameString.add("Nick");
        ExpertGameBoard expertThree = new ExpertGameBoard(3, nameString);
        try {
            expertThree.addStudent(StudentCounter.DININGROOM, Color.BLUE, 1);
            expertThree.addStudent(StudentCounter.DININGROOM, Color.BLUE, 2);
            expertThree.addStudent(StudentCounter.ISLAND, Color.BLUE, 1);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        expertThree.updateProfessorOwnershipIfTie();
    }

    /**
     * Verify that the flags are all reset correctly, utilizing a test scenario where all flags would modify the influence computation result
     *
     * @param testConfig : parameter to choose which disable flag instantiate to test then if it is well disabled
     */
    @ParameterizedTest
    @DisplayName("ResetFlags test and influence computation")
    @ValueSource(ints = {0, 1, 2, 3, 4})
    public void resetFlagsTest(int testConfig) {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        try {
            // P0: 1BLUE + 1 TOWER
            expert.addStudent(StudentCounter.DININGROOM, Color.BLUE, 0);
            expert.addStudent(StudentCounter.ISLAND, Color.BLUE, 1);
            expert.updateProfessorOwnership();
            assertEquals(expert.computeInfluence(1), Tower.WHITE); //Player 0 gets the tower (White) on the Island
            // P0: 1BLUE + 1 TOWER (score 2) -- P1: 1YELLOW (score 2)
            expert.addStudent(StudentCounter.DININGROOM, Color.YELLOW, 1);
            expert.addStudent(StudentCounter.ISLAND, Color.YELLOW, 1);
            expert.addStudent(StudentCounter.ISLAND, Color.YELLOW, 1);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        // Now tie in normal influence computation (player0: 1 tower + 1 blue, player1: 2 yellow)
        switch (testConfig) {
            case 0:
                expert.disableColorInfluence(Color.BLUE);
                break;
            case 1:
                expert.disableTowerInfluence();
                break;
            case 2:
                expert.updateProfessorOwnershipIfTie();
                break;
            case 3:
                expert.increaseInfluence();
                break; //If done when the current player is BLACK...
            case 4:
                expert.disableColorInfluence(Color.BLUE);
                expert.disableTowerInfluence();
                expert.updateProfessorOwnershipIfTie();
                expert.increaseInfluence();
                break;
            default:
                System.out.println("Invalid test configuration parameter!");
        }
        expert.resetAllTurnFlags();
        try {
            expert.updateProfessorOwnership();
            assertEquals(expert.computeInfluence(1), Tower.WHITE);
        } catch (Exception exc) {
            exc.printStackTrace();

        }
    }


    /**
     * Verifies that the payment of the special cards works fine (initially the player has no coins but then after adding with configuration
     * the current player(0) can pay one coin since more than 3 students of an identical color are added to its diningRoom)
     */
    @Test
    @DisplayName("Pay special card")
    private void payCard() {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        assertFalse(expert.paySpecialCard(3));
        adder(5, expert);
        assertTrue(expert.paySpecialCard(1));
    }

    /**
     * Tests if the effect of increaseMovement of motherNature has effectively an effect (motherNature is moved taking into account the increased movement)
     */
    @Test
    @DisplayName("Test increased movement of motherNature")
    public void motherNatureMovement() {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        expert.increaseMovementMotherNature();
        if (!expert.useAssistantCard(Tower.WHITE, 2))
            System.out.println("CARD NOT USABLE");
        try {
            System.out.println("Card used, num " + expert.getLastAssistantCard(Tower.WHITE));
            assertTrue(expert.moveMotherNature(3));
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    /**
     * Tests the boundaries conditions of the IslandOutOfBoundException throw in moveMotherNature
     *
     * @param islandPosition : position of the island to test the exception (<1, >12)
     */
    @ParameterizedTest
    @DisplayName("Move motherNature exception IslandOutOfBound")
    @ValueSource(ints = {0, 13})
    public void motherNatureException(int islandPosition) {
        ExpertGameBoard expert = new ExpertGameBoard(2, nameString);
        try {
            expert.useAssistantCard(Tower.WHITE, 1);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        assertThrows(IslandOutOfBoundException.class, () -> expert.moveMotherNature(islandPosition));
    }


    /**
     * To have streams of number for the tests
     *
     * @param source : integer of the stream to use
     * @return a stream of integer with 5 values (corresponding to index of the color to add)
     */
    private int[] streamer(int source) {

        switch (source) {
            case 0:
                return IntStream.of(0, 1, 0, 4, 1).toArray();
            case 1:
            case 6:
                return IntStream.of(0, 0, 0, 0, 1).toArray();
            case 2:
                return IntStream.of(0, 2, 3, 2, 1).toArray();
            case 3:
                return IntStream.of(1, 1, 1, 2, 1).toArray();
            case 4:
                return IntStream.of(1, 1, 1, 4, 1).toArray();
            case 5:
                return IntStream.of(0, 4, 6, 1, 1).toArray();
            default:
                return IntStream.of(0, 4, 6, 2, 1).toArray();
        }
    }

    /**
     * Add a determined amount of students
     *
     * @param studentCounter : where to add the students
     * @param color          : color of the students
     * @param position       : position (Island position or number of the player)
     * @param quantity       : quantity of the students to add
     */
    private void iterativeAdder(StudentCounter studentCounter, Color color, int position, int quantity, ExpertGameBoard expert) {
        try {
            for (int i = 0; i < quantity; i++) {
                if (!expert.addStudent(studentCounter, color, position)) System.out.println("Unsuccessful add\n");
                else if (studentCounter == StudentCounter.ISLAND)
                    System.out.println("NR of students island: " + expert.islands[position - 1].studentNumber(color) + " color " + color);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Adds to player0 and player1 (two players) and on island 1 a determined amount of students
     *
     * @param configuration : integer to choose parameters from the method streamer
     */
    private void adder(int configuration, ExpertGameBoard expert) {

        int[] playerStudents0, playerStudents1, islandStudents; //PTI: 0
        playerStudents0 = streamer(configuration);
        playerStudents1 = streamer(configuration + 1);
        islandStudents = streamer(configuration + 2);
        for (Color color : Color.values()) {
            iterativeAdder(StudentCounter.DININGROOM, color, 0, playerStudents0[color.ordinal()], expert);
            iterativeAdder(StudentCounter.DININGROOM, color, 1, playerStudents1[color.ordinal()], expert);
            iterativeAdder(StudentCounter.ISLAND, color, 1, islandStudents[color.ordinal()], expert);
        }
        System.out.printf("Player0: %d BLUE; %d GREEN; %d YELLOW; %d PINK; %d RED\n", playerStudents0[0], playerStudents0[1], playerStudents0[2], playerStudents0[3], playerStudents0[4]);
        System.out.printf("Player1: %d BLUE; %d GREEN; %d YELLOW; %d PINK; %d RED\n", playerStudents1[0], playerStudents1[1], playerStudents1[2], playerStudents1[3], playerStudents1[4]);
        System.out.printf("Island: %d BLUE; %d GREEN; %d YELLOW; %d PINK; %d RED\n", islandStudents[0], islandStudents[1], islandStudents[2], islandStudents[3], islandStudents[4]);
    }
}
