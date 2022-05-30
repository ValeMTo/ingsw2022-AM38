package it.polimi.ingsw.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.controller.SpecialCardRequiredAction;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.model.specialCards.SpecialCardName;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// See the implementation below for the exact names of fields in the generated json (overrides the names in the GoogleDoc).

public class MessageGenerator {
    private static final Gson gson = new Gson();


    /**
     * Generates the message OK
     *
     * @return : json String of the message OK
     */
    public static String okMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.OK.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the message NACK
     *
     * @return : json String of the message NACK
     */
    public static String nackMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.NACK.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates all the Error messages that only carry the errorString field and the error subtype.
     *
     * @param errorType   : type of error
     * @param errorString : String of the particular error to send
     * @return : json String of the error message
     */
    public static String errorWithStringMessage(ErrorTypeEnum errorType, String errorString) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", errorType.ordinal());
        json.addProperty("errorString", errorString);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the Error messages caused by an already used exception.
     * This message contains also the List of usable integers
     *
     * @param errorType     : type of error
     * @param errorString   : String of the particular error to send
     * @param usableIndexes : List of the usable integer indexes
     * @return : json String of the error message
     */
    public static String errorWithUsableValues(ErrorTypeEnum errorType, String errorString, Set<Integer> usableIndexes) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.put("ErrorType", errorType.ordinal());
        json.put("errorString", errorString);
        json.put("usableIndexes", usableIndexes);

        return json + "\n";
    }

    /**
     * Generates the error message for InvalidInput with the additional fields that informs on the allowed range
     * of values for the input.
     *
     * @param errorString : String of the particular error to send
     * @param minVal      : the minimum allowed value for the input
     * @param maxVal      : the maximum allowed value for the input
     * @return : json String of the error message
     */
    public static String errorInvalidInputMessage(String errorString, int minVal, int maxVal) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", ErrorTypeEnum.INVALID_INPUT.ordinal());
        json.addProperty("errorString", errorString);
        json.addProperty("minVal", minVal);
        json.addProperty("maxVal", maxVal);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the error message for IslandOutOfBound with the additional fields that informs on the allowed range
     * of values for the input.
     *
     * @param errorString : String of the particular error to send
     * @param minVal      : the minimum allowed value for the input
     * @param maxVal      : the maximum allowed value for the input
     * @return : json String of the error message
     */
    public static String errorIslandOutOfBoundInputMessage(String errorString, int minVal, int maxVal) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", ErrorTypeEnum.ISLAND_OUT_OF_BOUND.ordinal());
        json.addProperty("errorString", errorString);
        json.addProperty("minVal", minVal);
        json.addProperty("maxVal", maxVal);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates an error message caused by using an action that cannot be used in the current phase of the turn
     *
     * @param actualPhase actual phase that is now set
     * @return the json String of the error message
     */
    public static String errorWrongPhase(PhaseEnum actualPhase) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", ErrorTypeEnum.WRONG_PHASE_ACTION.ordinal());
        json.addProperty("actualPhase", actualPhase.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the error message for InvalidInput with the additional fields that shows the already set NumberOfPlayers
     *
     * @param errorString        : String of the particular error to send
     * @param actualNumOfPlayers : the current number of player that is already set
     * @return : json String of the error message
     */
    public static String errorNumOfPlayersAlreadySetMessage(String errorString, int actualNumOfPlayers) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", ErrorTypeEnum.NUMBER_OF_PLAYERS_ALREADY_SET.ordinal());
        json.addProperty("errorString", errorString);
        json.addProperty("ActualNumOfPlayers", actualNumOfPlayers);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the message Ping
     *
     * @return : json String of the Ping message
     */
    public static String pingMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.PING.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the message Pong
     *
     * @return : json String of the Pong message
     */
    public static String pongMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.PONG.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates all the subtypes of Connection messages
     *
     * @param connectionType : the subtype of the Connection message
     * @return : json String of the Connection message
     */
    public static String connectionMessage(ConnectionTypeEnum connectionType) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.CONNECTION.ordinal());
        json.addProperty("ConnectionType", connectionType.ordinal());

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json for request the actual condition of the lobby
     *
     * @return : json String for request lobby number of players
     */
    public static String lobbyRequestMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.REQUEST.ordinal());
        json.addProperty("RequestType", RequestTypeEnum.LOBBY_REQUEST.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json for request the start of the game
     *
     * @return : json String for request lobby number of players
     */
    public static String startGameRequestMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.REQUEST.ordinal());
        json.addProperty("RequestType", RequestTypeEnum.START_GAME.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json to accept rules game
     *
     * @return : json String with an ok
     */
    public static String okRulesGameMessage(){
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ANSWER.ordinal());
        json.addProperty("AnswerType", AnswerTypeEnum.ACCEPT_RULES_ANSWER.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json to refuse rules game
     *
     * @return : json String with an ok
     */
    public static String nackRulesGameMessage(){
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ANSWER.ordinal());
        json.addProperty("AnswerType", AnswerTypeEnum.REFUSE_RULES_ANSWER.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json for answer the actual condition of the lobby
     *
     * @return : json String with number of players in the lobby
     */
    public static String answerlobbyMessage(int actualPlayer, boolean isExpert,int numOfPlayers ) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ANSWER.ordinal());
        json.addProperty("AnswerType", AnswerTypeEnum.LOBBY_ANSWER.ordinal());
        json.addProperty("actualPlayers", actualPlayer);
        json.addProperty("isExpert", isExpert);
        json.addProperty("numOfPlayers", numOfPlayers);
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json for request the gamemode
     *
     * @return : json String for request gamemode
     */
    public static String gamemodeRequestMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.REQUEST.ordinal());
        json.addProperty("RequestType", RequestTypeEnum.GAMEMODE_REQUEST.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json for answer the actual gamemode
     *
     * @return : json String with gamemode of the lobby
     */
    public static String gamemodeMessage(boolean isExpert) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ANSWER.ordinal());
        json.addProperty("AnswerType", AnswerTypeEnum.GAMEMODE_ANSWER.ordinal());
        json.addProperty("gamemode", isExpert);
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json for request the number of player for this match
     *
     * @return : json String for request number of player
     */
    public static String numberOfPlayerRequestMessage() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.REQUEST.ordinal());
        json.addProperty("RequestType", RequestTypeEnum.PLAYERS_NUMBER_REQUEST.ordinal());
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates a String formatted in json for answer the actual number of players
     *
     * @return : json String with number of players in this match
     */
    public static String numberOfPlayerMessage(int numOfPlayers) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ANSWER.ordinal());
        json.addProperty("AnswerType", AnswerTypeEnum.PLAYERS_NUMBER_ANSWER.ordinal());
        json.addProperty("numOfPlayers", numOfPlayers);
        return gson.toJson(json) + "\n";
    }


    /**
     * Generates the UseAssistantCard Action message
     *
     * @param priority :  the priority that identifies tha AssistantCard to be played
     * @return : json String of the Action UseAssistantCard message
     */
    public static String useAssistantCardMessage(int priority) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.USE_ASSISTANT_CARD.ordinal());
        json.addProperty("CardPriority", priority);
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the ok message that includes the next step needed to use the special card
     *
     * @return : json String of the Action UseSpecialCard message
     */
    public static String specialCardAnswer(SpecialCardRequiredAction specialCardRequiredAction, boolean optionalAction) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ANSWER.ordinal());
        json.addProperty("AnswerType", AnswerTypeEnum.SPECIAL_CARD_ANSWER.ordinal());
        json.addProperty("SpecialCardAnswer", specialCardRequiredAction.ordinal());
        json.addProperty("Optional", optionalAction);
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the UseSpecialCard Action message
     *
     * @param specialCardName :  the name of the SpecialCard that is used
     * @return : json String of the Action UseSpecialCard message
     */
    public static String useSpecialCard(String specialCardName) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.USE_SPECIAL_CARD.ordinal());
        json.addProperty("SpecialCardName", specialCardName);
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the MoveStudent Action message, used to move a student of a given color from one given location
     * to another.
     *
     * @param color     : the color of the student to move
     * @param fromLoc   : the location from which the student is moved
     * @param toLoc     : the location to which the student is moved (its destination)
     * @param position: the index representing the specific destination (e.g.: the specific island of destination)
     * @return : json String of the Action MoveStudent message
     */
    public static String moveStudentMessage(Color color, StudentCounter fromLoc, StudentCounter toLoc, int position) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.MOVE_STUDENT.ordinal());

        json.addProperty("Color", color.ordinal());
        json.addProperty("From", fromLoc.ordinal());
        json.addProperty("To", toLoc.ordinal());
        json.addProperty("Position", position);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the MoveStudent Action message, used to move a student of a given color from one given location
     * to another.
     *
     * @param color     : the color of the student to move
     * @param fromLoc   : the location from which the student is moved
     * @param toLoc     : the location to which the student is moved (its destination)
     * @return : json String of the Action MoveStudent message
     */
    public static String moveStudentMessage(Color color, StudentCounter fromLoc, StudentCounter toLoc) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.MOVE_STUDENT.ordinal());

        json.addProperty("Color", color.ordinal());
        json.addProperty("From", fromLoc.ordinal());
        json.addProperty("To", toLoc.ordinal());

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the MoveMotherNature Action message, used to move MotherNature to a given destination Island.
     *
     * @param islandPosition: the index representing the specific destination Island among the existing Islands.
     * @return : json String of the Action message MoveMotherNature
     */
    public static String moveMotherNatureMessage(int islandPosition) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.MOVE_MOTHER_NATURE.ordinal());
        json.addProperty("IslandPosition", islandPosition);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the ChooseCloud Action message, used by the players to choose a specific Cloud.
     *
     * @param cloudPosition : the index representing the specific Cloud of choice
     * @return : json String of the Action message ChooseCloud
     */
    public static String chooseCloudMessage(int cloudPosition) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.CHOOSE_CLOUD.ordinal());
        json.addProperty("CloudPosition", cloudPosition);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the ChooseTilePosition Action message, used to choose the Island on which to place the NoEntryTile.
     *
     * @param islandPosition : the index representing the specific Island where to place a NoEntry tile.
     * @return : json String of the Action message ChooseCloud
     * @see it.polimi.ingsw.model.specialCards.Herbalist
     */
    public static String chooseTilePositionMessage(int islandPosition) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.CHOOSE_TILE_POSITION.ordinal());
        json.addProperty("IslandPosition", islandPosition);

        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the ChooseColor Action message, used to choose a specific Color within an interaction with the Server.
     *
     * @param color : the chosen Color
     * @return : json String of the Action message ChooseColor
     */
    public static String chooseColorMessage(Color color) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ACTION.ordinal());
        json.addProperty("ActionType", ActionTypeEnum.CHOOSE_COLOR.ordinal());
        json.addProperty("Color", color.ordinal());

        return gson.toJson(json) + "\n";
    }


    /**
     * Generates a String formatted in json for the nickName set message
     *
     * @param nickName : nickname we want to send
     * @return : json string of the set nickname message
     */
    public static String nickNameMessage(String nickName) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.SET.ordinal());
        json.addProperty("SetType", SetTypeEnum.SET_NICKNAME.ordinal());
        json.addProperty("nickname", nickName);
        return gson.toJson(json) + "\n";
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
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the Set message of SetGameMode, used to select a GameMode (Easy or Expert)
     *
     * @param isExpertMode : the boolean indicating the preferred GameMode : false->"Easy" ; true->"Expert"
     * @return : json string of the SetGameMode message
     */
    public static String setGameModeMessage(boolean isExpertMode) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.SET.ordinal());
        json.addProperty("SetType", SetTypeEnum.SET_GAME_MODE.ordinal());
        json.addProperty("SetExpertGameMode", isExpertMode);
        return gson.toJson(json) + "\n";
    }

    /**
     * Generates the EndOfGameMessage
     *
     * @return : json string of the EndOfGameMessage message
     */
    // It might have to be updated with additional content and fields.
    public static String endOfGameMessage(String endString) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.END_OF_GAME_MESSAGE.ordinal());
        json.addProperty("EndOfGameString", endString);
        return gson.toJson(json) + "\n";
    }

    //   UPDATE-type MESSAGES :  used JSONObject and "put()" methods instead of JsonObject and "addProperty()"

    /**
     * Generates the SetupUpdate message, used to notify the initial settings of the match, which include the
     * mapping of each player (their NickName) to a color of Tower, hence to a specific SchoolBoard.
     *
     * @param playersMapping : the Map with the association NickName -> Tower. Note that the Tower enum values are
     *                       converted to their corresponding ordinal integer.
     * @param numPlayers     : the initial number of players of the game
     * @param isExpertMode   : same semantics of {@link #setGameModeMessage(boolean isExpertMode)}.
     * @return : json string of the SetupUpdate message
     */
    public static String setupUpdateMessage(Map<String, Tower> playersMapping, int numPlayers, boolean isExpertMode) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.SETUP_UPDATE.ordinal());
        json.put("NumberOfPlayers", numPlayers);
        json.put("isExpertMode", isExpertMode);
        Map<String, String> playerStringMap = new HashMap<>();
        for(String s:playersMapping.keySet())
            playerStringMap.put(s,playersMapping.get(s).name());
        json.put("PlayersMapping", playersMapping);

        return json + "\n";    // using the toString() on a JSONObject instead of using toJson
    }

    /**
     * Generates the CloudViewUpdate message, used to notify the updated status of a given cloud.
     *
     * @param position:   the index of the specific cloud where the change has occurred.
     * @param studentsMap : the hashmap with the updated amounts of students for each color
     * @param limit       : the maximum limit of students on the cloud
     * @return : json string of the CloudViewUpdate message
     */
    public static String cloudViewUpdateMessage(int position, int limit, Map<Color, Integer> studentsMap) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.CLOUD_VIEW_UPDATE.ordinal());
        json.put("position", position);
        json.put("StudentsLimit", limit);
        json.put("StudentsMap", studentsMap);

        return json + "\n";
    }

    /**
     * Generates the IslandViewUpdate message, used to notify the updated status of a given Island.
     *
     * @param position:   the index of the specific island where the change has occurred.
     * @param studentsMap : the hashmap with the updated amounts of students for each color
     * @param towerColor  : the tower color of the player who has dominance on the given island
     * @param numTower    : the amount of towers on the island
     * @param isDisabled: states whether the dominance computation on that island is disabled (true) or enabled (false)
     * @return : json string of the IslandViewUpdate message
     */
    public static String islandViewUpdateMessage(int position, Map<Color, Integer> studentsMap, Tower towerColor, int numTower, boolean isDisabled) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.ISLAND_VIEW_UPDATE.ordinal());
        json.put("position", position);
        json.put("StudentsMap", studentsMap);
        if(towerColor!=null)
            json.put("TowerColor", towerColor.ordinal());
        else
            json.put("TowerColor","null");
        json.put("NumOfTowers", numTower);
        json.put("IsDisabled", isDisabled);
        return json + "\n";
    }

    /**
     * Generates the ArchipelagoViewUpdate message, used to notify the updated status of an Archipelago of Islands.
     *
     * @param numIslands:          the index of the specific island where the change has occurred.
     * @param motherNaturePosition : the index of the island where the MotherNature is placed.
     * @return : json string of the ArchipelagoViewUpdate message
     */
    public static String archipelagoViewUpdateMessage(int numIslands, int motherNaturePosition) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.ARCHIPELAGO_VIEW_UPDATE.ordinal());
        json.put("NumOfIslands", numIslands);
        json.put("MotherNaturePosition", motherNaturePosition);

        return json + "\n";
    }

    /**
     * Generates the SchoolBoardUpdate message, used to notify the updated status of a given Schoolboard.
     *
     * @param schoolEntranceMap: the hashmap with the updated amounts of students for each color in the SchoolEntrance
     * @param diningRoomMap:     the hashmap with the updated amounts of students for each color in the DiningRoom
     * @param towerColor:        the tower color of the player who owns that SchoolBoard
     * @param numTower           : the amount of towers left on the SchoolBoard.
     * @param coins              : the amount of coins left on the SchoolBoard.
     * @return : json string of the SchoolBoardUpdate message
     */
    public static String schoolBoardViewUpdate(Map<Color, Integer> diningRoomMap, Map<Color, Integer> schoolEntranceMap, Tower towerColor, int numTower, int coins) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.SCHOOL_BOARD_UPDATE.ordinal());
        json.put("SchoolEntranceMap", schoolEntranceMap);
        json.put("DiningRoomMap", diningRoomMap);
        json.put("TowerColor", towerColor.ordinal());
        json.put("NumOfTowers", numTower);
        json.put("coins", coins);
        return json + "\n";
    }

    /**
     * Generates the ProfessorsUpdate message, used to notify the updated mapping of a professor to
     * the player who owns it.  The professor is represented by its color, while the player is represented by
     * their tower color.
     *
     * @param professors: the hashmap with the updated professor ownership
     * @return : json string of the ProfessorsUpdate message
     */
    public static String professorsUpdateMessage(Map<Color, Tower> professors) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.PROFESSORS_UPDATE.ordinal());
        json.put("ProfessorsMap", professors);
        return json + "\n";
    }


    /**
     * Generates the SpecialCardUpdate message, used to notify the updated status of a given SpecialCard.
     *
     * @param name:     the name of the SpecialCard, represented in the json by its ordinal value in the enum.
     * @param coinCost: the current cost of that specialCard
     * @return : json string of the SpecialCardUpdate message
     */
    private static JSONObject specialCardStandardMessage(SpecialCardName name, int coinCost) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.SPECIAL_CARD_UPDATE.ordinal());
        json.put("Name", name.ordinal());
        json.put("coinCost", coinCost);
        return json;
    }


    public static String specialCardUpdateMessage(SpecialCardName name, int coinCost) {
        JSONObject json = new JSONObject();
        json = specialCardStandardMessage(name, coinCost);
        return json + "\n";
    }

    /**
     * Generates the SpecialCardUpdate message, used to notify the updated status of the Herbalist specialCard.
     * Overloads the method {@link #specialCardUpdateMessage(SpecialCardName, int)}, adding the NoEntryTiles parameter
     *
     * @param numTiles : the updated amount of NoEntryTiles placed on the Herbalist specialCard.
     * @return : json string of the SpecialCardUpdate message
     * @see it.polimi.ingsw.model.specialCards.Herbalist
     */
    public static String specialCardUpdateMessage(SpecialCardName name, int coinCost, int numTiles) {
        JSONObject json = new JSONObject();
        json = specialCardStandardMessage(name, coinCost);
        json.put("NumTiles", numTiles);
        return json + "\n";
    }

    /**
     * Generates the SpecialCardUpdate message, in the case of a specialCard with students.
     * Overloads the method {@link #specialCardUpdateMessage(SpecialCardName, int)}, adding the hashmap of the students.
     *
     * @param studentsMap: the updated amounts of students on the specialCard.
     * @return : json string of the SpecialCardUpdate message
     * @see it.polimi.ingsw.model.specialCards.SpecialCardWithStudent
     */
    public static String specialCardUpdateMessage(SpecialCardName name, int coinCost, Map<Color, Integer> studentsMap) {
        JSONObject json = new JSONObject();
        json = specialCardStandardMessage(name, coinCost);
        json.put("StudentsMap", studentsMap);
        return json + "\n";
    }

    /**
     * Generates the PhaseUpdate message, used to notify the change of phase in the game.
     *
     * @param phase : the name of the new current phase. In the json it is represented by its ordinal value in the enum.
     * @return : json string of the phaseUpdate message
     */
    public static String phaseUpdateMessage(PhaseEnum phase) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.PHASE_UPDATE.ordinal());
        json.put("CurrentPhase",phase.ordinal());
        return json + "\n";
    }

    /**
     * Generates the AssistantCardUpdate message, used to notify the player with the available Assistant Cards
     * in their deck.
     *
     * @param playerTower    : identifies the player whose deck availability has been updated
     * @param availableCards : the list of available AssistantCards in the player's deck.
     * @return : json string of the AssistantCardUpdate message
     */
    public static String assistantCardUpdateMessage(Tower playerTower, ArrayList<Integer> availableCards) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.ASSISTANT_CARD_UPDATE.ordinal());
        json.put("PlayerTower", playerTower.ordinal());
        json.put("AvailableCards", availableCards);
        return json + "\n";
    }

    /**
     * Generates the LastUsedAssistantCardUpdate message, used to update a player's view with their
     * last used AssistantCard.
     *
     * @param playerTower : identifies the player recipient of the update
     * @param lastUsed    : the list of available AssistantCards in the player's deck.
     * @return : json string of the LastUsedAssistantCardUpdate message
     */
    public static String lastUsedAssistantCardUpdateMessage(Tower playerTower, int lastUsed) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.LAST_USED_ASSISTANT_CARD_UPDATE.ordinal());
        json.put("PlayerTower", playerTower.ordinal());
        json.put("LastUsed", lastUsed);
        return json + "\n";
    }


    /**
     * Generates the CurrentPlayerUpdate message, used to notify the new current player.
     *
     * @param nickname : the nickname of the current active player.
     * @return : json string of the CurrentPlayerUpdate message
     */
    public static String currentPlayerUpdateMessage(Tower nickname) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.CURRENT_PLAYER_UPDATE.ordinal());
        json.put("CurrentPlayer", nickname);
        return json + "\n";
    }

    /**
     * Generates the CurrentPlayerAndPhaseUpdate message, used to notify the new current player and the phase at the same time.
     *
     * @param nickname : the nickname of the current active player.
     * @return : json string of the CurrentPlayerUpdate message
     */
    public static String currentPlayerAndPhaseUpdateMessage(Tower nickname, PhaseEnum phase) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.PHASE_AND_CURRENT_PLAYER_UPDATE.ordinal());
        json.put("CurrentPlayer", nickname);
        json.put("CurrentPhase",phase.ordinal());
        return json + "\n";
    }


    /**
     * Generates the LeaderboardUpdate message, used to notify the current leaderboard status,
     * It is also used at the end of the game with the final results.
     *
     * @param isEndOfGame : states if the game is ended.
     * @param leaderboard : the current updated leaderboard (results and scores).
     * @return : json string of the LeaderboardUpdate message
     */
    public static String leaderboardUpdateMessage(boolean isEndOfGame, Map<String, Integer> leaderboard) {
        JSONObject json = new JSONObject();
        json.put("MessageType", MessageTypeEnum.UPDATE.ordinal());
        json.put("UpdateType", UpdateTypeEnum.LEADERBOARD_UPDATE.ordinal());
        json.put("IsEndOfGame", isEndOfGame);
        json.put("Leaderboard", leaderboard);
        return json + "\n";
    }



}
