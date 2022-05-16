package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.AllMovesUsedException;
import it.polimi.ingsw.exceptions.AlreadyUsedException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.messages.ActionTypeEnum;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.messages.MessageTypeEnum;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;


public class MessageParser {
    private final GameOrchestrator gameOrchestrator;
    private final String nickName;

    public MessageParser(GameOrchestrator gameOrchestrator, String nickName) {
        this.gameOrchestrator = gameOrchestrator;
        this.nickName = nickName;
    }

    /**
     * Given a json message, reads it and use the proper method of the GameHandler
     *
     * @param message : received json message from the network
     * @return : a String (json) that is the answer message from the server
     */
    public String parseMessageToAction(String message) {
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        if (!gameOrchestrator.getActivePlayer().equalsIgnoreCase(nickName))
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_YOUR_TURN, "ERROR - This is not your turn");

        if (json.get("MessageType").getAsInt() == MessageTypeEnum.ACTION.ordinal()) {
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.USE_ASSISTANT_CARD.ordinal())
                return useAssistantCard(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.MOVE_STUDENT.ordinal()) return moveStudent(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.MOVE_MOTHER_NATURE.ordinal())
                return moveMotherNature(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_CLOUD.ordinal()) return chooseCloud(json);
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - generic error, bad request or wrong message");
    }

    /**
     * Uses the assistant card and returns the correct message to submit to the Client
     *
     * @param json : received message that we want to convert to the use of the assistant card
     * @return : the answer message in json
     */
    private String useAssistantCard(JsonObject json) {
        try {
            if (gameOrchestrator.chooseCard(json.get("CardPriority").getAsInt())) return MessageGenerator.okMessage();
        } catch (IndexOutOfBoundsException exc) {
            return MessageGenerator.errorInvalidInputMessage("ERROR - Invalid input, out of bound", 1, 10);
        } catch (AlreadyUsedException exc) {
            return MessageGenerator.errorWithUsableValues(ErrorTypeEnum.ALREADY_USED_ASSISTANT_CARD, "ERROR - The card has been already used", exc.getUsableIndexes());
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - Generic error using assistant card");
    }

    /**
     * Move a student from the SchoolEntrance to the DiningRoom or Island
     *
     * @param json : received message that we want to convert to the use of the student move action
     * @return : the answer message in json
     */
    private String moveStudent(JsonObject json) {
        if (!gameOrchestrator.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_STUDENTS))
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - The given command is not for the phase");
        if (json.get("From").getAsInt() == StudentCounter.SCHOOLENTRANCE.ordinal()) {
            if (json.get("To").getAsInt() == StudentCounter.PLAYER.ordinal()) {
                if (gameOrchestrator.moveStudent(Color.values()[json.get("Color").getAsInt()])) {
                    return MessageGenerator.okMessage();
                } else
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - No students of the given color in your schoolEntrance");
            } else if (json.get("To").getAsInt() == StudentCounter.ISLAND.ordinal()) {
                try {
                    if (gameOrchestrator.moveStudent(Color.values()[json.get("Color").getAsInt()], json.get("Position").getAsInt())) {
                        return MessageGenerator.okMessage();
                    } else
                        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - No students of the given color in your schoolEntrance");
                } catch (IndexOutOfBoundsException exc) {
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.INVALID_INPUT, "ERROR - No island has the given position");
                } catch (AllMovesUsedException exc) {
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.ALL_MOVED_USED, "ERROR - All moves of the students are done, now is time for motherNature movement");
                }
            } else
                return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_VALID_DESTINATION, "ERROR - Only DiningRoom and Island are allowed in this phase");
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_VALID_ORIGIN, "ERROR - Only SchoolEntrance is allowed in this phase");
    }

    /**
     * Moves the motherNature to the island chosen by the player
     *
     * @param json : received message that will be converted in calling the moveMother nature method of gameOrchestrator
     * @return : the answer message to the Client in json
     */
    private String moveMotherNature(JsonObject json) {
        if (!gameOrchestrator.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE))
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - The given command is not for the phase");
        try {
            if (gameOrchestrator.moveMotherNature(json.get("IslandPosition").getAsInt()))
                return MessageGenerator.okMessage();
        } catch (IslandOutOfBoundException exc) {
            return MessageGenerator.errorInvalidInputMessage("ERROR - Island out of bound", exc.getLowerBound(), exc.getHigherBound());
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_VALID_DESTINATION, "ERROR - Not enough steps to reach the island ");
    }

    /**
     * @param json : received message that will be converted in calling the chooseCloud method of gameOrchestrator
     * @return : the answer message to the Client in json
     */
    private String chooseCloud(JsonObject json) {
        if (!gameOrchestrator.getCurrentPhase().equals(PhaseEnum.ACTION_CHOOSE_CLOUD))
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - The given command is not for the phase");
        try {
            if (gameOrchestrator.chooseCloud(json.get("CloudPosition").getAsInt())) return MessageGenerator.okMessage();
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - Error in choice of the cloud");
        } catch (AlreadyUsedException exc) {
            return MessageGenerator.errorWithUsableValues(ErrorTypeEnum.ALREADY_USED_CLOUD, "ERROR - the cloud was already used", exc.getUsableIndexes());
        }
    }

}
