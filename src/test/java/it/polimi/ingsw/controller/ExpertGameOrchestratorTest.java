package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.messages.AnswerTypeEnum;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageTypeEnum;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class ExpertGameOrchestratorTest {
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
                    System.out.println("Round "+game.gameBoard.getNumRound()+" active player "+game.gameBoard.getCurrentPlayer()+" CHOOSING CLOUD "+counter);
                    System.out.println("Round "+game.gameBoard.getNumRound()+" active player "+game.gameBoard.getCurrentPlayer()+"From usable clouds "+game.gameBoard.getUsableClouds());
                    game.chooseCloud(counter);
                    counter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return game;
    }

    @Test
    @DisplayName("Special card - No such card between the usable special cards")
    public void noSuchCardTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.HERALD, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            for (SpecialCardName specialCardName : SpecialCardName.values()) {
                if (!game.gameBoard.getSetOfSpecialCardNames().contains(specialCardName)) {
                    json = game.useSpecialCard(specialCardName.name());
                    jsonObject = gson.fromJson(json, JsonObject.class);
                    assertEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
                    assertEquals(ErrorTypeEnum.NO_SUCH_SPECIAL_CARD.ordinal(), jsonObject.get("ErrorType").getAsInt());
                }
            }
        } catch (FunctionNotImplementedException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Uses the special cards in the initial state, so the chosen card that costs 1 coin are used, the others not and the one
     * that were not initialized give error since they are not the three chosen cards
     *
     * @param specialCard : the special card name that we want to use
     */
    @ParameterizedTest
    @EnumSource(SpecialCardName.class)
    @DisplayName("Use of the special card with the starting one coin")
    public void testUseSpecialCardOneCoin(SpecialCardName specialCard) {
        ExpertGameOrchestrator game = new ExpertGameOrchestrator(getNicknames(true), 0, null);
        GameOrchestratorTest.doAPlanningPhase(game);
        String json;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(specialCard.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            if (game.specialCards.contains(specialCard)) {
                switch (specialCard) {
                    case PRIEST, POSTMAN, JUGGLER:
                        assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
                        break;
                        case  BARD:
                            assertNotEquals(ErrorTypeEnum.NOT_ENOUGH_COINS.ordinal(), jsonObject.get("ErrorType").getAsInt());
                            assertEquals(ErrorTypeEnum.NO_STUDENTS_IN_DINING_ROOM.ordinal(), jsonObject.get("ErrorType").getAsInt());
                            break;
                    default:
                        assertEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
                }
            } else assertEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    /**
     * Try to use the card when it is surely contained into the gameBoard and when the player has sufficient coins (the round passed are sufficient)
     *
     * @param specialCard : the special card name that we want to use
     */
    @ParameterizedTest
    @EnumSource(SpecialCardName.class)
    @DisplayName("Special card - sure contained card - all coin usable")
    public void testUseSpecialCard(SpecialCardName specialCard) {
        GameOrchestrator game = doRoundsAndAssureCardContained(specialCard, 8);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(specialCard.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
        jsonObject = gson.fromJson(json, JsonObject.class);
        if (jsonObject.get("errorString") != null) System.out.println(jsonObject.get("errorString").getAsString());
        assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
    }


    /**
     * Uses the priest card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - PRIEST - normal")
    public void priestTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.PRIEST, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.PRIEST.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(MessageTypeEnum.ANSWER.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(AnswerTypeEnum.SPECIAL_CARD_ANSWER.ordinal(), jsonObject.get("AnswerType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
            assertEquals(SpecialCardRequiredAction.CHOOSE_ISLAND.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(game.chooseIsland(1), JsonObject.class);
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the cheeseMaker card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - CHEESEMAKER - normal")
    public void cheeseMakerTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.CHEESEMAKER, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.CHEESEMAKER.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the herald card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - HERALD - normal")
    public void heraldTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.HERALD, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.HERALD.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_ISLAND.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(game.chooseIsland(1), JsonObject.class);
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the postman card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - POSTMAN - normal")
    public void postmanTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.POSTMAN, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.POSTMAN.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO: some times an unexpected card launch an exception, investigate!

    /**
     * Uses the herbalist card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - HERBALIST - normal")
    public void herbalistTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.HERBALIST, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.HERBALIST.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_ISLAND.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(game.chooseIsland(1), JsonObject.class);
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the archer card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - ARCHER - normal")
    public void archerTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.ARCHER, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.ARCHER.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the juggler card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - JUGGLER - normal")
    public void jugglerTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.JUGGLER, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.JUGGLER.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(MessageTypeEnum.ANSWER.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(AnswerTypeEnum.SPECIAL_CARD_ANSWER.ordinal(), jsonObject.get("AnswerType").getAsInt());
            //TODO : also model the interaction where I want to stop using the swap effect
            for (int i = 0; i < 3; i++) {
                assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
                jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
                assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
                jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
            }
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the knight card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - KNIGHT - normal")
    public void knightTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.KNIGHT, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.KNIGHT.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the cooker card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - COOKER - normal")
    public void cookerTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.COOKER, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.COOKER.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the bard card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - BARD - normal")
    public void bardTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.BARD, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.BARD.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
            assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the princess card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - PRINCESS - normal")
    public void princessTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.PRINCESS, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.PRINCESS.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Uses the gambler card and waits for the right interactions
     */
    @Test
    @DisplayName("SpecialCard - GAMBLER - normal")
    public void gamblerTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.GAMBLER, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.GAMBLER.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_COLOR.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(chooseColor(game), JsonObject.class);
            assertEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Island out of bound error message test
     */
    @Test
    @DisplayName("SpecialCard - Island out of bound")
    public void islandOutOfBoundTest() {
        GameOrchestrator game = doRoundsAndAssureCardContained(SpecialCardName.HERALD, 7);
        String json = null;
        JsonObject jsonObject;
        Gson gson = new Gson();
        try {
            json = game.useSpecialCard(SpecialCardName.HERALD.name());
            jsonObject = gson.fromJson(json, JsonObject.class);
            assertNotEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(SpecialCardRequiredAction.CHOOSE_ISLAND.ordinal(), jsonObject.get("SpecialCardAnswer").getAsInt());
            jsonObject = gson.fromJson(game.chooseIsland(13), JsonObject.class);
            assertNotEquals(MessageTypeEnum.OK.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(MessageTypeEnum.ERROR.ordinal(), jsonObject.get("MessageType").getAsInt());
            assertEquals(ErrorTypeEnum.ISLAND_OUT_OF_BOUND.ordinal(), jsonObject.get("ErrorType").getAsInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

