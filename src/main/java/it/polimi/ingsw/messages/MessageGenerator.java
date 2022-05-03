package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.board.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class MessageGenerator {
    private static final Gson gson = new Gson();


    /**
     * Generates the message OK
     *
     * @return : json String of the message OK
     */
    public static String okMessage() {    //renamed
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.OK.ordinal());
        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the message NACK
     *
     * @return : json String of the message NACK
     */
    public static String nackMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.NACK.ordinal());
        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the generic message Error with only the errorString field as content of the error
     *
     * @param error       : type of error
     * @param errorString : String of the particular error to send
     * @return : json String of the error message
     */
    public static String errorWithStringMessage(ErrorTypeEnum error, String errorString) {   // renamed
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", error.ordinal());
        json.addProperty("errorString", errorString);

        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the error message for InvalidInput with the additional fields that informs on the allowed range
     * of values for the input.
     * @param errorString : String of the particular error to send
     * @param minVal : the minimum allowed value for the input
     * @param maxVal : the maximum allowed value for the input
     * @return : json String of the error message
     */
    public static String errorInvalidInputMessage(String errorString, int minVal, int maxVal) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", ErrorTypeEnum.INVALID_INPUT.ordinal());
        json.addProperty("errorString", errorString);
        json.addProperty("minVal", minVal);
        json.addProperty("maxVal", maxVal);

        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the error message for InvalidInput with the additional fields that shows the already set NumberOfPlayers
     * @param errorString : String of the particular error to send
     * @param actualNumOfPlayers : the current number of player that is already set
     * @return : json String of the error message
     */
    public static String errorNumOfPlayersAlreadySetMessage(String errorString, int actualNumOfPlayers) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", ErrorTypeEnum.NUMBER_OF_PLAYERS_ALREADY_SET.ordinal());
        json.addProperty("errorString", errorString);
        json.addProperty("ActualNumOfPlayers", actualNumOfPlayers);

        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the message Ping
     *
     * @return : json String of the Ping message
     */
    public static String pingMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.PING.ordinal());
        return gson.toJson(json)+"\n";
    }


    /**
     * Generates the message Pong
     *
     * @return : json String of the Pong message
     */
    public static String pongMessage() {
        JsonObject json = new JsonObject();
        json.put("key", value);
        json.addProperty("MessageType", MessageTypeEnum.PONG.ordinal());
        return gson.toJson(json)+"\n";
    }

    /**
     * Generates all the subtypes of Connection messages
     * @param connectionType : the subtype of the Connection message
     * @return : json String of the Connection message
     */
    public static String connectionMessage(ConnectionTypeEnum connectionType) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.CONNECTION.ordinal());
        json.addProperty("ConnectionType", connectionType.ordinal());

        return gson.toJson(json)+"\n";
    }

    // UseAssistantCardMessageGenerator()

    //UseSpecialCardMessageGenerator()


    /**
     * Generates the MoveStudent Action message, used to move a student of a given color from one given location
     * to another.
     * @param action : the type of Action performed by the message
     * @param color : the color of the student to move
     * @param fromLoc : the location from which the student is moved
     * @param toLoc : the location to which the student is moved (its destination)
     * @param position: the index representing the specific destination (e.g.: the specific island of destination)
     * @return : json String of the Action MoveStudent message
     */
    public static String moveStudentMessage(ActionTypeEnum action, Color color, StudentCounter fromLoc, StudentCounter toLoc, int position) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", action.ordinal());

        json.addProperty("Color", color.ordinal());
        json.addProperty("From", fromLoc.ordinal());
        json.addProperty("To", toLoc.ordinal());
        json.addProperty("Position", position);

        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the MoveMotherNature Action message, used to move MotherNature to a given destination Island.
     * @param islandPosition: the index representing the specific destination Island among the existing Islands.
     *
     * @return : json String of the Action message MoveMotherNature
     */
    public static String moveMotherNatureMessage(int islandPosition) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.MOVE_MOTHER_NATURE.ordinal());
        json.addProperty("IslandPosition", islandPosition);

        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the ChooseCloud Action message, used by the players to choose a specific Cloud.
     * @param cloudPosition : the index representing the specific Cloud of choice
     *
     * @return : json String of the Action message ChooseCloud
     */
    public static String chooseCloudMessage(int cloudPosition) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.CHOOSE_CLOUD.ordinal());
        json.addProperty("CloudPosition", cloudPosition);

        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the ChooseTilePosition Action message, used to choose the Island on which to place the NoEntryTile.
     * @param islandPosition : the index representing the specific Island where to place a NoEntry tile.
     *
     * @see it.polimi.ingsw.model.specialCards.Herbalist
     * @return : json String of the Action message ChooseCloud
     */
    public static String chooseTilePositionMessage(int islandPosition) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.CHOOSE_TILE_POSITION.ordinal());
        json.addProperty("IslandPosition", islandPosition);

        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the ChooseColor Action message, used to choose a specific Color within an interaction with the Server.
     * @param color : the chosen Color
     *
     * @return : json String of the Action message ChooseColor
     */
    public static String chooseColorMessage(Color color) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.CHOOSE_COLOR.ordinal());
        json.addProperty("Color", color.ordinal());

        return gson.toJson(json)+"\n";
    }


    /**
     * Generates a String formatted in json for the nickName set message
     *
     * @param nickName : nickname we want to send
     * @return : json string of the set nickname message
     */
    public static String nickNameMessage(String nickName) {    //renamed
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.SET.ordinal());
        json.addProperty("SetType", SetTypeEnum.SET_NICKNAME.ordinal());
        json.addProperty("SetNickName", nickName);
        return gson.toJson(json)+"\n";
    }

    /**
     * Generates a String formatted in json for the SelectNumberOfPlayers set message
     *
     * @param numOfPlayers : the number of players we want to set
     * @return : json string of the SelectNumberOfPlayers message
     */
    public static String selectNumberOfPlayersMessage(int numOfPlayers) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.SET.ordinal());
        json.addProperty("SetType", SetTypeEnum.SELECT_NUMBER_OF_PLAYERS.ordinal());
        json.addProperty("SetNumberOfPlayers", numOfPlayers);
        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the Set message of SetGameMode, used to select a GameMode (Easy or Expert)
     *
     * @param isExpertMode : the boolean indicating the preferred GameMode : "False" -> "Easy" ;  "True"-> "Expert"
     * @return : json string of the SetGameMode message
     */
    public static String setGameModeMessage(boolean isExpertMode) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.SET.ordinal());
        json.addProperty("SetType", SetTypeEnum.SET_GAME_MODE.ordinal());
        json.addProperty("SetExpertGameMode", isExpertMode);
        return gson.toJson(json)+"\n";
    }












}
