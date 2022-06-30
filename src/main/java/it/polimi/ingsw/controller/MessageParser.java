package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.AlreadyUsedException;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.IncorrectPhaseException;
import it.polimi.ingsw.exceptions.IslandOutOfBoundException;
import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.server.Server;

import java.util.HashMap;
import java.util.Map;


public class MessageParser{
    private GameOrchestrator gameOrchestrator;
    private final ClientHandler client;
    private String name;

    public MessageParser(ClientHandler client) {
        this.client = client;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setGameOrchestrator(GameOrchestrator gameOrchestrator) {
        this.gameOrchestrator = gameOrchestrator;
    }

    /**
     * Given a json message, reads it and use the proper method of the GameHandler
     *
     * @param message : received json message from the network
     * @return : a String (json) that is the answer message from the server
     */
    public String parseMessageToAction(String message) {
        //System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - parse message to action");
        JsonObject json = new Gson().fromJson(message, JsonObject.class);
        if(json.get("MessageType").getAsInt() == MessageTypeEnum.PING.ordinal())
        {
            //System.out.println("MESSAGE PARSER - PLAYER "+this.name+" received a ping, sending a pong");
            return MessageGenerator.pongMessage();
        }
        if ( gameOrchestrator!= null &&!gameOrchestrator.getActivePlayer().equalsIgnoreCase(client.getNickName())) {
            System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - ERROR - This is not his/her turn");
            System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - ERROR - current player is "+gameOrchestrator.getActivePlayer());
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_YOUR_TURN, "ERROR - This is not your turn");
        }
        System.out.println(client == null);
        System.out.println(message);

        if (json.get("MessageType").getAsInt() == MessageTypeEnum.ACTION.ordinal()) {
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.USE_ASSISTANT_CARD.ordinal())
                return useAssistantCard(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.MOVE_STUDENT.ordinal()) return moveStudent(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.MOVE_MOTHER_NATURE.ordinal())
                return moveMotherNature(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_CLOUD.ordinal()) return chooseCloud(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.USE_SPECIAL_CARD.ordinal() || json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_ISLAND.ordinal() || json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_COLOR.ordinal() || json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_TILE_POSITION.ordinal())
                return useSpecialCard(json);
            if (json.get("ActionType").getAsInt() == ActionTypeEnum.TERMINATE_SPECIAL_EFFECT.ordinal()) {
                try {
                    return gameOrchestrator.terminateSpecialCardUsage();
                } catch (FunctionNotImplementedException exc) {
                    System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - ERROR - Not expert game mode not possible terminate special card usage");
                }
            }
        } else if(json.get("MessageType").getAsInt() == MessageTypeEnum.REQUEST.ordinal()){
            if(json.get("RequestType").getAsInt() == RequestTypeEnum.PLAYERS_NUMBER_REQUEST.ordinal()){
                return MessageGenerator.numberOfPlayerMessage(Server.getNumOfPlayerGame());
            } else if (json.get("RequestType").getAsInt() == RequestTypeEnum.GAMEMODE_REQUEST.ordinal()){
                return MessageGenerator.gamemodeMessage(Server.getGamemode());
            } else if (json.get("RequestType").getAsInt() == RequestTypeEnum.LOBBY_REQUEST.ordinal()){
                System.out.println("Lobby");
                client.lobbyRequestHandler();
                System.out.println("Lobby answer sent");
            }
            return MessageGenerator.okMessage();

        } else if (json.get("MessageType").getAsInt() == MessageTypeEnum.ANSWER.ordinal()){
            if(json.get("AnswerType").getAsInt() == AnswerTypeEnum.ACCEPT_RULES_ANSWER.ordinal()) {
                Server.addPlayerInLobby(client);
                return MessageGenerator.okMessage();
            } else if (json.get("AnswerType").getAsInt() == AnswerTypeEnum.REFUSE_RULES_ANSWER.ordinal()){
                client.disconnectionManager();
            }
            return MessageGenerator.okMessage();
        }else if (json.get("MessageType").getAsInt() == MessageTypeEnum.CONNECTION.ordinal()){
            if (json.get("MessageType").getAsInt() == ConnectionTypeEnum.CLOSE_CONNECTION.ordinal()){
                client.disconnectionManager();
                System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - DISCONNECTING");
            }
            return MessageGenerator.okMessage();

        }else if(json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal()) {
            if (json.get("SetType").getAsInt() == SetTypeEnum.SET_NICKNAME.ordinal()) {
                System.out.println("SET NICKNAME");
                return client.confirmNickname(json.get("nickname").getAsString());


            } else if (json.get("SetType").getAsInt()==SetTypeEnum.SET_GAME_MODE.ordinal()){
                if (Server.getLobbyOfActivePlayers().size() == 0) {
                    Server.setLobbySettings(json.get("SetExpertGameMode").getAsBoolean());
                }
            } else if (json.get("SetType").getAsInt()==SetTypeEnum.SELECT_NUMBER_OF_PLAYERS.ordinal()){
                if (Server.getLobbyOfActivePlayers().size() == 0) {
                    Server.setLobbySettings(json.get("SetNumberOfPlayers").getAsInt());
                    Server.addPlayerInLobby(client); //TODO
                }

            }
            return MessageGenerator.okMessage();
        }
        System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - ERROR - Generic error! ");
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
            System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - useAssistantCard - Try to use card "+json.get("CardPriority").getAsInt());
            if (gameOrchestrator.chooseCard(json.get("CardPriority").getAsInt())){
                System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - useAssistantCard - Using correctly card "+json.get("CardPriority").getAsInt());
                return MessageGenerator.okMessage();
            }
        } catch (IndexOutOfBoundsException exc) {
            System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - useAssistantCard - ERROR - invalid input card "+json.get("CardPriority").getAsInt());
            return MessageGenerator.errorInvalidInputMessage("ERROR - Invalid input, out of bound", 1, 10);
        } catch (AlreadyUsedException exc) {
            System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - useAssistantCard - ERROR - already used card "+json.get("CardPriority").getAsInt());
            return MessageGenerator.errorWithUsableValues(ErrorTypeEnum.ALREADY_USED_ASSISTANT_CARD, "ERROR - The card has been already used", exc.getUsableIndexes());
        } catch (IncorrectPhaseException exc) {
            System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - useAssistantCard - ERROR - incorrect phase: "+exc.getActualPhase());
            return MessageGenerator.errorWrongPhase(exc.getActualPhase());
        }
        System.out.println("MESSAGE PARSER - PLAYER "+this.name+" - useAssistantCard - ERROR - generic error");
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
        try {
            if (json.get("From").getAsInt() == StudentCounter.SCHOOLENTRANCE.ordinal()) {
                if (json.get("To").getAsInt() == StudentCounter.DININGROOM.ordinal()) {
                    if (gameOrchestrator.moveStudent(Color.values()[json.get("Color").getAsInt()])) {
                        return MessageGenerator.okMessage();
                    } else
                        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - No students of the given color in your schoolEntrance");
                } else if (json.get("To").getAsInt() == StudentCounter.ISLAND.ordinal()) {

                    if (gameOrchestrator.moveStudent(Color.values()[json.get("Color").getAsInt()], json.get("Position").getAsInt())) {
                        return MessageGenerator.okMessage();
                    } else
                        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, "ERROR - No students of the given color in your schoolEntrance");

                } else
                    return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_VALID_DESTINATION, "ERROR - Only DiningRoom and Island are allowed in this phase");
            }
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.NOT_VALID_ORIGIN, "ERROR - Only SchoolEntrance is allowed in this phase");
        } catch (IndexOutOfBoundsException exc) {
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.INVALID_INPUT, "ERROR - No island has the given position");
        } catch (IncorrectPhaseException exc) {
            return MessageGenerator.errorWrongPhase(exc.getActualPhase());
        }
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
        } catch (IncorrectPhaseException exc) {
            return MessageGenerator.errorWrongPhase(exc.getActualPhase());
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
        } catch (IncorrectPhaseException exc) {
            return MessageGenerator.errorWrongPhase(exc.getActualPhase());
        }
        catch (IndexOutOfBoundsException exc) {
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.INVALID_INPUT, "ERROR - No cloud has the given position");
        }
    }


    /**
     * Uses the special card with the gameOrchestrator methods
     *
     * @param json :  received message that will be converted in calling the chooseCloud method of gameOrchestrator
     * @return the answer message to the Client in json
     */
    private String useSpecialCard(JsonObject json) {
        try {
            if (json.get("MessageType").getAsInt() == MessageTypeEnum.ACTION.ordinal() && json.get("ActionType").getAsInt() == ActionTypeEnum.USE_SPECIAL_CARD.ordinal()) {
                return gameOrchestrator.useSpecialCard(json.get("SpecialCardName").getAsString());
            }
            if (json.get("MessageType").getAsInt() == MessageTypeEnum.ACTION.ordinal() && (json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_COLOR.ordinal())) {
                return gameOrchestrator.chooseColor(Color.values()[json.get("Color").getAsInt()]);
            }
            if (json.get("MessageType").getAsInt() == MessageTypeEnum.ACTION.ordinal() && (json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_ISLAND.ordinal()||json.get("ActionType").getAsInt() == ActionTypeEnum.CHOOSE_TILE_POSITION.ordinal()))
                return gameOrchestrator.chooseIsland(json.get("IslandPosition").getAsInt());
        } catch (FunctionNotImplementedException exc) {
            return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.FUNCTION_NOT_IMPLEMENTED, "ERROR - this functionality is for expert game mode only");
        } catch (IslandOutOfBoundException exc) {
            return MessageGenerator.errorInvalidInputMessage("ERROR - Island out of bound", exc.getLowerBound(), exc.getHigherBound());
        }
        return MessageGenerator.errorWithStringMessage(ErrorTypeEnum.GENERIC_ERROR, "ERROR - generic error in special card usage");
    }

    /**
     * Method for the get for the SETUP update
     *
     * @return the Map of players string and tower color
     */
    public Map<String, Tower> getPlayersTower() {
        return new HashMap<String, Tower>(this.gameOrchestrator.getPlayersTower());
    }

    public boolean isExpert() {
        return this.gameOrchestrator.isExpert();
    }

    /**
     * Method used to disconnect all clients from a game, due to the lost of a player
     */
    public void disconnectClients(){
        if(gameOrchestrator!=null)
            gameOrchestrator.disconnectClients();
    }

}
