package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.messages.MessageTypeEnum;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EasyGameOrchestratorTest {

    GameOrchestrator orchestrator2 = new EasyGameOrchestrator(getNicknames(false), 2, null);
    GameOrchestrator orchestrator3 = new EasyGameOrchestrator(getNicknames(true), 8, null);
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
     * Does a random move student phase
     *
     * @param game : GameOrchestrator that we want to evolve to the phase MoveStudent after the planning
     */
    private void doAMoveStudentsOnlyDining(GameOrchestrator game) {
        Random random = new Random();
        while (game.getCurrentPhase() == PhaseEnum.ACTION_MOVE_STUDENTS) {
            try {
                game.moveStudent(Color.values()[random.nextInt(Color.values().length)]);
            } catch (IncorrectPhaseException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Choose the color until there are errors in the choice
     *
     * @param game : GameOrchestrator that we want to evolve to the phase MoveStudent after the planning
     * @return the message of answer of the GameOrchestrator after the used of the method
     */
    private String chooseColor(GameOrchestrator game) {
        String returnValue = null;
        Gson gson = new Gson();
        JsonObject jsonObject;
        try {
            for (Color color : Color.values()) {
                returnValue = game.chooseColor(color);
                System.out.println("Used color " + color.name() + "received " + returnValue);
                jsonObject = gson.fromJson(returnValue, JsonObject.class);
                if (jsonObject.get("MessageType").getAsInt() != MessageTypeEnum.ERROR.ordinal()) return returnValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Creates the gameOrchestrator until it contains the card and then do a series of rounds adding only students to the diningRooms
     *
     * @param specialCard : special card that we want contained into the game
     * @param numOfRounds : num of rounds to do
     * @return the created ExpertGameOrchestrator containing the card an update after a certain amount of rounds
     */
    private GameOrchestrator doRoundsAndAssureCardContained(SpecialCardName specialCard, int numOfRounds) {
        ExpertGameOrchestrator game = new ExpertGameOrchestrator(getNicknames(true), 0, null);
        while (!game.specialCards.contains(specialCard)) game = new ExpertGameOrchestrator(getNicknames(true), 0, null);
        int counter = 0;
        for (int i = 0; i < 8; i++) {
            GameOrchestratorTest.doAPlanningPhase(game);
            counter = 1;
            try {
                while (game.currentPhase != PhaseEnum.END && game.currentPhase != PhaseEnum.PLANNING) {
                    doAMoveStudentsOnlyDining(game);
                    new GameOrchestratorTest().doAMoveMotherNature(game);
                    game.chooseCloud(counter);
                    counter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return game;
    }

    /**
     * Test of use SpecialCard method in EasyGameOrchestrator.
     * It should launch FunctionNotImplementedException
     */
    @Test
    public void useSpecialCardTest(){
        assertThrows(FunctionNotImplementedException.class, () -> orchestrator2.useSpecialCard(SpecialCardName.JUGGLER.toString()));
        assertThrows(FunctionNotImplementedException.class, () -> orchestrator3.useSpecialCard(SpecialCardName.JUGGLER.toString()));
    }

    /**
     * Test of chooseColor method in EasyGameOrchestrator.
     * It should launch FunctionNotImplementedException
     */
    @Test
    public void chooseColorTest(){
        assertThrows(FunctionNotImplementedException.class, () -> orchestrator2.chooseColor(Color.BLUE));
        assertThrows(FunctionNotImplementedException.class, () -> orchestrator3.chooseColor(Color.RED));
    }

    /**
     * Test of chooseColor method in EasyGameOrchestrator.
     * It should launch FunctionNotImplementedException
     */
    @Test
    public void chooseIslandTest(){
        assertThrows(FunctionNotImplementedException.class, () -> orchestrator2.chooseIsland(8));
        assertThrows(FunctionNotImplementedException.class, () -> orchestrator3.chooseIsland(5));
    }
}
