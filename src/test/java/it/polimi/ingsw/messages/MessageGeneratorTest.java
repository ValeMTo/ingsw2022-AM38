package it.polimi.ingsw.messages;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.controller.SpecialCardRequiredAction;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MessageGeneratorTest {

    Map<String, Tower> playersMappings = new HashMap<String, Tower>();
    Map<Color, Integer> studentsMap = new HashMap<Color, Integer>();

    private void fillStudentsMap(Map<Color, Integer> map) {
        map.put(Color.YELLOW, 0);
        map.put(Color.BLUE, 1);
        map.put(Color.PINK, 2);
        map.put(Color.GREEN, 0);
        map.put(Color.RED, 0);
    }

    private void fillMapping(Map<String, Tower> testMap) {
        testMap.put("Player1", Tower.WHITE);
        testMap.put("Player2", Tower.BLACK);
        testMap.put("Player3", Tower.GRAY);
    }


    /**
     * Test the json schema for okMessage
     */
    @Test
    public void okMessageTest() {
        String message = MessageGenerator.okMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.OK.ordinal(), json.get("MessageType").getAsInt());
    }

    /**
     * Test the json schema for nackMessage
     */
    @Test
    public void nackMessageTest() {
        String message = MessageGenerator.nackMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.NACK.ordinal(), json.get("MessageType").getAsInt());
    }

    /**
     * Test the json schema for errorWithUsableValuesMessage
     */
    @Test
    public void errorWithUsableValuesTest() {
        Set<Integer> list = new HashSet<>();
        list.add(1);
        for (ErrorTypeEnum error : ErrorTypeEnum.values()) {
            String message = MessageGenerator.errorWithUsableValues(error, "errorString", list);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.ERROR.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(error.ordinal(), json.get("ErrorType").getAsInt());
        }
    }

    /**
     * Test the json schema for errorInvalidInputMessage
     */
    @Test
    public void errorInvalidInputMessageTest() {

        String message = MessageGenerator.errorInvalidInputMessage("errorString", 1, 2);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ERROR.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ErrorTypeEnum.INVALID_INPUT.ordinal(), json.get("ErrorType").getAsInt());
        assertEquals(1, json.get("minVal").getAsInt());
        assertEquals(2, json.get("maxVal").getAsInt());

    }

    /**
     * Test the json schema for errorIslandOutOfBoundInputMessage
     */
    @Test
    public void errorIslandOutOfBoundInputTest() {

        String message = MessageGenerator.errorIslandOutOfBoundInputMessage("errorString", 1, 2);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ERROR.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ErrorTypeEnum.ISLAND_OUT_OF_BOUND.ordinal(), json.get("ErrorType").getAsInt());
        assertEquals(1, json.get("minVal").getAsInt());
        assertEquals(2, json.get("maxVal").getAsInt());

    }

    /**
     * Test the json schema for errorWrongPhaseMessage
     */
    @Test
    public void errorWrongPhaseTest() {
        String message = null;
        for (PhaseEnum phase : PhaseEnum.values()) {
            message = MessageGenerator.errorWrongPhase(phase);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.ERROR.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(ErrorTypeEnum.WRONG_PHASE_ACTION.ordinal(), json.get("ErrorType").getAsInt());
            assertEquals(phase.ordinal(), json.get("actualPhase").getAsInt());
        }

    }

    /**
     * Test the json schema for errorNumOfPlayersAlreadySetMessage
     */
    @Test
    public void errorNumOfPlayersAlreadySetTest() {
        String message = null;
        message = MessageGenerator.errorNumOfPlayersAlreadySetMessage("errorString", 3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ERROR.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ErrorTypeEnum.NUMBER_OF_PLAYERS_ALREADY_SET.ordinal(), json.get("ErrorType").getAsInt());
        assertEquals(3, json.get("ActualNumOfPlayers").getAsInt());

    }

    /**
     * Test the jsonschema for pingMessage
     */
    @Test
    public void pingTest() {
        String message = null;
        message = MessageGenerator.pingMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.PING.ordinal(), json.get("MessageType").getAsInt());

    }

    /**
     * Test the jsonschema for pongMessage
     */
    @Test
    public void pongTest() {
        String message = null;
        message = MessageGenerator.pongMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.PONG.ordinal(), json.get("MessageType").getAsInt());

    }

    /**
     * Test the jsonschema for connectionMessage
     */
    @Test
    public void connectionTest() {
        String message = null;
        for (ConnectionTypeEnum connection : ConnectionTypeEnum.values()) {
            message = MessageGenerator.connectionMessage(connection);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.CONNECTION.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(connection.ordinal(), json.get("ConnectionType").getAsInt());
        }
    }

    /**
     * Test the jsonschema for lobbyRequestMessage
     */
    @Test
    public void lobbyRequestTest() {
        String message = null;
        message = MessageGenerator.lobbyRequestMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.REQUEST.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(RequestTypeEnum.LOBBY_REQUEST.ordinal(), json.get("RequestType").getAsInt());
    }

    /**
     * Test the jsonschema for lobbyRequestMessage
     */
    @Test
    public void startGameRequestTest() {
        String message = null;
        message = MessageGenerator.startGameRequestMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.REQUEST.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(RequestTypeEnum.START_GAME.ordinal(), json.get("RequestType").getAsInt());
    }

    /**
     * Test the jsonschema for okRulesGameMessage
     */
    @Test
    public void okRulesGametTest() {
        String message = null;
        message = MessageGenerator.okRulesGameMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ANSWER.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(AnswerTypeEnum.ACCEPT_RULES_ANSWER.ordinal(), json.get("AnswerType").getAsInt());
    }

    /**
     * Test the jsonschema for nackRulesGameMessage
     */
    @Test
    public void nackRulesGametTest() {
        String message = null;
        message = MessageGenerator.nackRulesGameMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ANSWER.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(AnswerTypeEnum.REFUSE_RULES_ANSWER.ordinal(), json.get("AnswerType").getAsInt());
    }

    /**
     * Test the jsonschema for answerLobbyMessage
     */
    @Test
    public void answerLobbyTest() {
        String message = null;
        message = MessageGenerator.answerlobbyMessage("Valeria", "Nicola",  true, 3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ANSWER.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(AnswerTypeEnum.LOBBY_ANSWER.ordinal(), json.get("AnswerType").getAsInt());
        assertEquals(2, json.get("actualPlayers").getAsInt());
        assertEquals(true, json.get("isExpert").getAsBoolean());
        assertEquals(3, json.get("numOfPlayers").getAsInt());
    }

    /**
     * Test the jsonschema for gamemodeRequestMessage
     */
    @Test
    public void gamemodeRequestTest() {
        String message = null;
        message = MessageGenerator.gamemodeRequestMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.REQUEST.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(RequestTypeEnum.GAMEMODE_REQUEST.ordinal(), json.get("RequestType").getAsInt());
    }

    /**
     * Test the jsonschema for gamemodeRequestMessage
     */
    @Test
    public void gamemodeMessageTest() {
        String message = null;
        message = MessageGenerator.gamemodeMessage(true);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ANSWER.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(AnswerTypeEnum.GAMEMODE_ANSWER.ordinal(), json.get("AnswerType").getAsInt());
        assertEquals(true, json.get("gamemode").getAsBoolean());
    }

    /**
     * Test the jsonschema for numberOfPlayerRequestMessage
     */
    @Test
    public void numberOfPlayerRequestTest() {
        String message = null;
        message = MessageGenerator.numberOfPlayerRequestMessage();
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.REQUEST.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(RequestTypeEnum.PLAYERS_NUMBER_REQUEST.ordinal(), json.get("RequestType").getAsInt());
    }

    /**
     * Test the jsonschema for numberOfPlayerMessage
     */
    @Test
    public void numberOfPlayerTest() {
        String message = null;
        message = MessageGenerator.numberOfPlayerMessage(3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ANSWER.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(AnswerTypeEnum.PLAYERS_NUMBER_ANSWER.ordinal(), json.get("AnswerType").getAsInt());
        assertEquals(3, json.get("numOfPlayers").getAsInt());
    }

    /**
     * Test the jsonschema for useAssistantCardMessage
     */
    @Test
    public void useAssistantCardMessageTest() {
        String message = null;
        message = MessageGenerator.useAssistantCardMessage(3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ACTION.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ActionTypeEnum.USE_ASSISTANT_CARD.ordinal(), json.get("ActionType").getAsInt());
        assertEquals(3, json.get("CardPriority").getAsInt());
    }

    /**
     * Test the jsonschema for specialCardAnswerMessage
     */
    @Test
    public void specialCardAnswerTest() {
        String message = null;
        for (SpecialCardRequiredAction cardAction : SpecialCardRequiredAction.values()) {
            message = MessageGenerator.specialCardAnswer(cardAction, true);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.ANSWER.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(AnswerTypeEnum.SPECIAL_CARD_ANSWER.ordinal(), json.get("AnswerType").getAsInt());
            assertEquals(cardAction.ordinal(), json.get("SpecialCardAnswer").getAsInt());
            assertEquals(true, json.get("Optional").getAsBoolean());
        }
    }

    /**
     * Test the jsonschema for moveStudentMessageMessage
     */
    @Test
    public void moveStudentMessageTest() {
        String message = null;
        message = MessageGenerator.moveStudentMessage(Color.BLUE, StudentCounter.SCHOOLENTRANCE, StudentCounter.ISLAND, 3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ACTION.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ActionTypeEnum.MOVE_STUDENT.ordinal(), json.get("ActionType").getAsInt());
        assertEquals(Color.BLUE.ordinal(), json.get("Color").getAsInt());
        assertEquals(StudentCounter.SCHOOLENTRANCE.ordinal(), json.get("From").getAsInt());
        assertEquals(StudentCounter.ISLAND.ordinal(), json.get("To").getAsInt());
        assertEquals(3, json.get("Position").getAsInt());
    }

    /**
     * Test the jsonschema for moveStudentMessageMessage without position input
     */
    @Test
    public void moveStudentMessage2Test() {
        String message = null;
        message = MessageGenerator.moveStudentMessage(Color.BLUE, StudentCounter.SCHOOLENTRANCE, StudentCounter.DININGROOM);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ACTION.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ActionTypeEnum.MOVE_STUDENT.ordinal(), json.get("ActionType").getAsInt());
        assertEquals(Color.BLUE.ordinal(), json.get("Color").getAsInt());
        assertEquals(StudentCounter.SCHOOLENTRANCE.ordinal(), json.get("From").getAsInt());
        assertEquals(StudentCounter.DININGROOM.ordinal(), json.get("To").getAsInt());
    }

    /**
     * Test the jsonschema for moveMotherNatureMessage
     */
    @Test
    public void moveMotherNatureTest() {
        String message = null;
        message = MessageGenerator.moveMotherNatureMessage(3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ACTION.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ActionTypeEnum.MOVE_MOTHER_NATURE.ordinal(), json.get("ActionType").getAsInt());
        assertEquals(3, json.get("IslandPosition").getAsInt());
    }

    /**
     * Test the jsonschema for chooseCloudMessage
     */
    @Test
    public void chooseCloudTest() {
        String message = null;
        message = MessageGenerator.chooseCloudMessage(3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ACTION.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ActionTypeEnum.CHOOSE_CLOUD.ordinal(), json.get("ActionType").getAsInt());
        assertEquals(3, json.get("CloudPosition").getAsInt());
    }

    /**
     * Test the jsonschema for ChooseTilePositionMessage
     */
    @Test
    public void chooseTilePositionTest() {
        String message = null;
        message = MessageGenerator.chooseTilePositionMessage(3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ACTION.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ActionTypeEnum.CHOOSE_TILE_POSITION.ordinal(), json.get("ActionType").getAsInt());
        assertEquals(3, json.get("IslandPosition").getAsInt());
    }

    /**
     * Test the jsonschema for ChooseColorMessage
     */
    @Test
    public void chooseColorTest() {
        String message = null;
        message = MessageGenerator.chooseColorMessage(Color.BLUE);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.ACTION.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(ActionTypeEnum.CHOOSE_COLOR.ordinal(), json.get("ActionType").getAsInt());
        assertEquals(Color.BLUE.ordinal(), json.get("Color").getAsInt());
    }

    /**
     * Test the jsonschema for nickNameMessage
     */
    @Test
    public void nickNameTest() {
        String message = null;
        message = MessageGenerator.nickNameMessage("vale");
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.SET.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(SetTypeEnum.SET_NICKNAME.ordinal(), json.get("SetType").getAsInt());
    }

    /**
     * Test the jsonschema for selectNumberOfPlayersMessage
     */
    @Test
    public void selectNumberOfPlayersTest() {
        String message = null;
        message = MessageGenerator.selectNumberOfPlayersMessage(3);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.SET.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(SetTypeEnum.SELECT_NUMBER_OF_PLAYERS.ordinal(), json.get("SetType").getAsInt());
        assertEquals(3, json.get("SetNumberOfPlayers").getAsInt());
    }

    /**
     * Test the jsonschema for setGameModeMessage
     */
    @Test
    public void setGameModeTest() {
        String message = null;
        message = MessageGenerator.setGameModeMessage(true);
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.SET.ordinal(), json.get("MessageType").getAsInt());
        assertEquals(SetTypeEnum.SET_GAME_MODE.ordinal(), json.get("SetType").getAsInt());
        assertTrue(json.get("SetExpertGameMode").getAsBoolean());
    }

    /**
     * Test the jsonschema for endOfGameMessage
     */
    @Test
    public void endOfGameMessageTest() {
        String message = null;
        message = MessageGenerator.endOfGameMessage("endString");
        JsonObject json = new Gson().fromJson(message, JsonObject.class);

        assertEquals(MessageTypeEnum.END_OF_GAME_MESSAGE.ordinal(), json.get("MessageType").getAsInt());
    }

    /**
     * Test the jsonschema for specialCardUpdateMessage
     */
    @Test
    public void specialCardUpdateTest() {
        String message = null;
        for (SpecialCardName name : SpecialCardName.values()) {
            message = MessageGenerator.specialCardUpdateMessage(name, 2);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.UPDATE.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(UpdateTypeEnum.SPECIAL_CARD_UPDATE.ordinal(), json.get("UpdateType").getAsInt());
            assertEquals(name.ordinal(), json.get("Name").getAsInt());
            assertEquals(2, json.get("coinCost").getAsInt());
        }
    }

    /**
     * Test the jsonschema for specialCardUpdateMessage with num of tiles
     */
    @Test
    public void specialCardUpdate2Test() {
        String message = null;
        for (SpecialCardName name : SpecialCardName.values()) {
            message = MessageGenerator.specialCardUpdateMessage(name, 2, 1);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.UPDATE.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(UpdateTypeEnum.SPECIAL_CARD_UPDATE.ordinal(), json.get("UpdateType").getAsInt());
            assertEquals(name.ordinal(), json.get("Name").getAsInt());
            assertEquals(2, json.get("coinCost").getAsInt());
            assertEquals(1, json.get("NumTiles").getAsInt());
        }
    }

    /**
     * Test the jsonschema for phaseUpdateMessage
     */
    @Test
    public void phaseUpdateTest() {
        String message = null;
        for (PhaseEnum name : PhaseEnum.values()) {
            message = MessageGenerator.phaseUpdateMessage(name);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.UPDATE.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(UpdateTypeEnum.PHASE_UPDATE.ordinal(), json.get("UpdateType").getAsInt());
            assertEquals(name.ordinal(), json.get("CurrentPhase").getAsInt());
        }
    }

    /**
     * Test the jsonschema for specialCardUpdateMessageth studentMap
     */
    @Test
    public void specialCardUpdate3Test() {
        String message = null;
        for (SpecialCardName name : SpecialCardName.values()) {
            fillStudentsMap(studentsMap);
            message = MessageGenerator.specialCardUpdateMessage(name, 2, studentsMap);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.UPDATE.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(UpdateTypeEnum.SPECIAL_CARD_UPDATE.ordinal(), json.get("UpdateType").getAsInt());
            assertEquals(name.ordinal(), json.get("Name").getAsInt());
            assertEquals(2, json.get("coinCost").getAsInt());
        }
    }


    /**
     * Test the jsonschema for lastUsedAssistantCardUpdateMessage
     */
    @Test
    public void lastUsedAssistantCardUpdateTest() {
        String message = null;
        for (Tower name : Tower.values()) {
            message = MessageGenerator.lastUsedAssistantCardUpdateMessage(name, 8);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.UPDATE.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(UpdateTypeEnum.LAST_USED_ASSISTANT_CARD_UPDATE.ordinal(), json.get("UpdateType").getAsInt());
            assertEquals(name.ordinal(), json.get("PlayerTower").getAsInt());
            assertEquals(8, json.get("LastUsed").getAsInt());
        }
    }

    /**
     * Test the jsonschema for CurrentPlayerUpdateMessage
     */
    @Test
    public void currentPlayerUpdateTest() {
        String message = null;
        for (Tower name : Tower.values()) {
            message = MessageGenerator.lastUsedAssistantCardUpdateMessage(name, 8);
            JsonObject json = new Gson().fromJson(message, JsonObject.class);

            assertEquals(MessageTypeEnum.UPDATE.ordinal(), json.get("MessageType").getAsInt());
            assertEquals(UpdateTypeEnum.LAST_USED_ASSISTANT_CARD_UPDATE.ordinal(), json.get("UpdateType").getAsInt());
            assertEquals(name.ordinal(), json.get("PlayerTower").getAsInt());
            assertEquals(8, json.get("LastUsed").getAsInt());
        }
    }


}

