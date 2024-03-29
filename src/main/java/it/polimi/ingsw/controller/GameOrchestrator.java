package it.polimi.ingsw.controller;

import it.polimi.ingsw.mvc.Listenable;
import it.polimi.ingsw.mvc.Listener;
import it.polimi.ingsw.mvc.ModelListener;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.server.ClientHandler;

import java.util.*;

import static it.polimi.ingsw.controller.PhaseEnum.END;
import static it.polimi.ingsw.controller.PhaseEnum.PLANNING;

public abstract class GameOrchestrator extends Listenable {
    protected final int maxStudentMoves = 3;
    protected final Map<String, Tower> playersTower;
    protected final boolean isExpert;
    protected List<String> players;
    protected SortedSet<Integer> playedAssistantCard;
    protected String[] planningOrder;
    protected String[] actionOrder;
    protected PhaseEnum currentPhase;
    protected int activePlayer;
    protected Object phaseBlocker = new Object();
    protected Object actionBlocker = new Object();
    protected int studentMovesLeft;
    protected GameBoard gameBoard;
    protected Listener modelListener;
    protected int id;
    protected boolean specialCardAlreadyUsed;
    protected List<ClientHandler> clients;
    protected HashMap<Tower, ClientHandler> playerBoardListeners;


    public GameOrchestrator(List<String> players, boolean isExpert, int id, List<ClientHandler> clients) {
        createListeners();
        this.clients = new ArrayList<>();
        if(clients != null)
            this.clients.addAll(clients);
        System.out.println("GAME MODE NOTIFY: SETUP OF THE VIEW STATES");
        Map<String, Tower> sendMap = new HashMap<>();
        sendMap.put(players.get(0), Tower.WHITE);
        sendMap.put(players.get(1), Tower.BLACK);
        if (players.size() >= 3)
            sendMap.put(players.get(2), Tower.GRAY);
        if(clients != null)
            notify(modelListener, MessageGenerator.setupUpdateMessage(sendMap, players.size(),isExpert),clients);
        this.isExpert = isExpert;
        System.out.println("GAME ORCHESTRATOR - SETTING");
        if (isExpert) this.gameBoard = new ExpertGameBoard(players.size(), players);
        else this.gameBoard = new EasyGameBoard(players.size(), players);
        this.players = new ArrayList<String>();
        this.players.addAll(players);
        this.activePlayer = 0;
        this.id = id;
        this.actionOrder = new String[players.size()];
        this.planningOrder = new String[players.size()];
        this.playersTower = new HashMap<String, Tower>();
        this.playedAssistantCard = new TreeSet<Integer>();
        this.specialCardAlreadyUsed = false;
        this.studentMovesLeft = maxStudentMoves;
        for (int i = 0; i < players.size(); i++) {
            System.out.println("GAME ORCHESTRATOR - SETTING - Player " + i+" "+players.get(i));
            planningOrder[i] = players.get(i);
            actionOrder[i] = players.get(i);
            if (gameBoard.getPlayerTower(players.get(i)) != null)
                playersTower.put(players.get(i), gameBoard.getPlayerTower(players.get(i)));
        }
        //Initializes the students on the islands drawing the firsts 10 students
        try {
            for (int j = 2; j <= 6; j++)
                gameBoard.addStudent(StudentCounter.ISLAND, gameBoard.drawFromBag(), j);
            for (int j = 8; j <= 12; j++)
                gameBoard.addStudent(StudentCounter.ISLAND, gameBoard.drawFromBag(), j);

            // Initialize the bag for the normal phase
            gameBoard.initializeBag();
            // Fills with students the SchoolEntrances of the players
            int limit;
            if (players.size() == 3) limit = 7;
            else limit = 9;
            for (int j = 0; j < players.size(); j++) {
                for (int idx = 0; idx < limit; idx++) {
                    gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, gameBoard.drawFromBag(), j);
                }
            }
            gameBoard.setListenerAndClients(modelListener,clients);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        gameBoard.fillClouds();
        if(clients!=null&&modelListener!=null) {
            currentPhase = PLANNING;
            System.out.println("GAME ORCHESTRATOR NOTIFY: ACTIVE PLAYER WITH TOWER " + gameBoard.getPlayerTower(planningOrder[activePlayer]));
            notify(modelListener, MessageGenerator.currentPlayerUpdateMessage(gameBoard.getPlayerTower(planningOrder[activePlayer])), clients);
            System.out.println("GAME ORCHESTRATOR NOTIFY: new phase " + currentPhase.name());
            notify(modelListener, MessageGenerator.phaseUpdateMessage(currentPhase), clients);
        }
        currentPhase = PLANNING;
    }

    /**
     * Getter of the active payer, used to know if the player that want to do a move is the actual player or not
     *
     * @return : the String of the nickName of the active player.
     */
    public String getActivePlayer() {
        synchronized (phaseBlocker) {
            if (getCurrentPhase().equals(PLANNING)) return this.planningOrder[activePlayer];
            return this.actionOrder[activePlayer];
        }
    }

    /**
     * Method for the get for the SETUP update
     *
     * @return the Map of players string and tower color
     */
    public Map<String, Tower> getPlayersTower() {
        return new HashMap<String, Tower>(this.playersTower);
    }

    /**
     * Needed for the SETUP update to give the set gameMode
     *
     * @return true if Expert, false if simple
     */

    public boolean isExpert() {
        return this.isExpert;
    }

    /**
     * Getter of the current phase, to know if the move is allowed in this phase
     *
     * @return : return the actual phase
     */
    public PhaseEnum getCurrentPhase() {
        synchronized (phaseBlocker) {
            return this.currentPhase;
        }
    }

    /**
     * Sets the current phase as a new one.
     *
     * @param updatePhase : new phase to update
     */
    protected void setCurrentPhase(PhaseEnum updatePhase) {
        synchronized (phaseBlocker) {
            if (gameBoard.isEndOfMatch().equals(EndOfMatchCondition.InstantEndOfMatch))
                this.currentPhase = END;
            else this.currentPhase = updatePhase;
        }
    }

    /**
     * Moves the student to the diningRoom
     *
     * @param color : Color of the student to move
     * @return : true if the student is correctly moved, otherwise false
     * @throws IncorrectPhaseException if the phase is not move student phase
     */
    public boolean moveStudent(Color color) throws IncorrectPhaseException {
        synchronized (actionBlocker) {
            if (getCurrentPhase() != PhaseEnum.ACTION_MOVE_STUDENTS)
                throw new IncorrectPhaseException(this.getCurrentPhase());
            if (this.studentMovesLeft == 0) {
                this.setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
                throw new IncorrectPhaseException(getCurrentPhase());
            }
            try {
                // Try to remove the student, if is removed correctly add the student to the DiningR.
                if (gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                    if (!gameBoard.addStudent(StudentCounter.DININGROOM, color)) {
                        // if it's not possible to add the student to the diningRoom then add back the student into the schoolEntrance
                        gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, color);
                        return false;
                    }
                    studentMovesLeft--;
                    if (studentMovesLeft == 0) setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
                    if (clients != null)
                        notifyPhaseAndCurrentPlayer();
                    return true;
                }
                return false;
            } catch (Exception exc) {
                exc.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Moves the student to the Island
     *
     * @param color  : Color of the student to move from the SchoolEntrance to the Island
     * @param island : Number of the Island we want to move the student into
     * @return true if the student was added correctly
     * @throws IncorrectPhaseException if the phase is not move student phase
     */
    public boolean moveStudent(Color color, int island) throws IndexOutOfBoundsException, IncorrectPhaseException {
        synchronized (actionBlocker) {
            if (getCurrentPhase() != PhaseEnum.ACTION_MOVE_STUDENTS)
                throw new IncorrectPhaseException(this.getCurrentPhase());
            if (studentMovesLeft == 0) {
                setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
                throw new IncorrectPhaseException(this.getCurrentPhase());
            }
            try {
                if (gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                    if (!gameBoard.addStudent(StudentCounter.ISLAND, color, island)) {
                        gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, color);
                        return false;
                    }
                    studentMovesLeft--;
                    if (studentMovesLeft == 0) setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
                    if (clients != null)
                        notifyPhaseAndCurrentPlayer();
                    return true;
                }
                return false;
            } catch (FunctionNotImplementedException exc) {
                exc.printStackTrace();
                return false;
            } catch (LocationNotAllowedException exc) {
                exc.printStackTrace();
                return false;
            }
        }
    }

    /**
     * Uses the assistant card with the given priority of the current player.
     *
     * @param priority : priority of the assistant card to use
     * @return : true if the card has been correctly used, otherwise false
     * @throws IndexOutOfBoundsException : if the card priority is not in the correct range
     * @throws IncorrectPhaseException   if the phase is not planning phase
     */
    public boolean chooseCard(int priority) throws IndexOutOfBoundsException, AlreadyUsedException, IncorrectPhaseException {
        System.out.println("GAME ORCHESTRATOR NOTIFY - chooseCard - player "+gameBoard.getCurrentPlayer()+" want to choose card "+priority);
        System.out.flush();
        synchronized (actionBlocker) {
            // If the current phase is incorrect
            if (getCurrentPhase() != PLANNING) throw new IncorrectPhaseException(this.getCurrentPhase());
            try {
                System.out.println("GAME ORCHESTRATOR NOTIFY - chooseCard - player "+gameBoard.getCurrentPlayer()+" choosing card "+priority);
                Set<Integer> usableCards = gameBoard.getUsableAssistantCard(gameBoard.getPlayerTower(getActivePlayer())).keySet();
                usableCards.removeAll(playedAssistantCard);
                //for (Integer i : usableCards)
                //    System.out.println("Usable card of " + getActivePlayer() + " " + i);
                // If the player has other cards to use other than the one played by other have to play another card
                if (usableCards.size() > 0 && !usableCards.contains(priority))
                    throw new AlreadyUsedException(usableCards);
            } catch (NoSuchTowerException exc) {
                exc.printStackTrace();
            }
            // The player plays the chosen card and if it is correctly played update the card used and go to the next phase wit nextStep
            if (gameBoard.useAssistantCard(gameBoard.getCurrentPlayer(), priority)) {
                System.out.println("GAME ORCHESTRATOR NOTIFY - chooseCard - player "+gameBoard.getCurrentPlayer()+" correctly used card"+priority);
                this.playedAssistantCard.add(priority);
                nextStep();
                try {
                    ArrayList<Integer> usableCards = new ArrayList<>();
                    for (Integer card : gameBoard.getUsableAssistantCard(gameBoard.getCurrentPlayer()).keySet()) {
                        usableCards.add(card);
                    }
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
                notifyPhaseAndCurrentPlayer();
                return true;
            }
            notifyPhaseAndCurrentPlayer();
            return false;
        }
    }

    /**
     * Moves the motherNature to the designed island
     *
     * @param destinationIsland : island where the player wants to move the motherNature
     * @return : true if the move is allowed, false if the island is unreachable with the steps given by the last card
     * @throws IslandOutOfBoundException : if the island position is out of bound
     * @throws IncorrectPhaseException   if the phase is not moving motherNature phase
     */
    public boolean moveMotherNature(int destinationIsland) throws IslandOutOfBoundException, IncorrectPhaseException {
        synchronized (actionBlocker) {
            if (!this.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE))
                throw new IncorrectPhaseException(this.getCurrentPhase());
            if (gameBoard.moveMotherNature(destinationIsland)) {
                gameBoard.updateProfessorOwnership();
                gameBoard.computeInfluence(destinationIsland);
                setCurrentPhase(PhaseEnum.ACTION_CHOOSE_CLOUD);
                if (clients != null) {
                    notifyPhaseAndCurrentPlayer();
                }
                return true;
            }
            return false;
        }
    }

    /**
     * Transfer the students from the cloud to the current player schoolEntrance
     * Removes all the student of all colors from the cloud and adds them to the player SchoolEntrance
     *
     * @param cloudPosition : position of the Cloud the player choose to use
     * @return : true if the
     * @throws IndexOutOfBoundsException if the position exceed the usable range [1, numOfPlayers]
     * @throws AlreadyUsedException      if the Cloud is not usable anymore
     * @throws IncorrectPhaseException   if the phase is not choose cloud phase
     */
    public boolean chooseCloud(int cloudPosition) throws IndexOutOfBoundsException, AlreadyUsedException, IncorrectPhaseException {
        synchronized (actionBlocker) {
            if (getCurrentPhase() != PhaseEnum.ACTION_CHOOSE_CLOUD)
                throw new IncorrectPhaseException(getCurrentPhase());
            if (cloudPosition < 1 || cloudPosition > players.size()) throw new IndexOutOfBoundsException();
            if (!gameBoard.getUsableClouds().contains(cloudPosition))
                throw new AlreadyUsedException(gameBoard.getUsableClouds());
            try {
                for (Color color : Color.values()) {
                    while (gameBoard.removeStudent(StudentCounter.CLOUD, color, cloudPosition)) {
                        gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, color);
                    }
                }
            } catch (FunctionNotImplementedException | LocationNotAllowedException exc) {
                exc.printStackTrace();
                return false;
            }
            if (clients != null)
                notify(modelListener, MessageGenerator.cloudViewUpdateMessage(cloudPosition, gameBoard.getCloudLimit(), null), clients);
            nextStep();
            return true;
        }
    }

    protected void notifyPhaseAndCurrentPlayer(){
        if(clients!=null&&modelListener!=null) {
            System.out.println("GAME ORCHESTRATOR NOTIFY - setPhaseAndCurrentPlayer - ActivePlayerWithTower " + gameBoard.getPlayerTower(planningOrder[activePlayer])+" phase "+this.getCurrentPhase());
            if(currentPhase==PLANNING) {
                System.out.println("GAME ORCHESTRATOR NOTIFY - setPhaseAndCurrentPlayer - ActivePlayerWithTower " + gameBoard.getPlayerTower(planningOrder[activePlayer]) + " phase " + this.getCurrentPhase());
                notify(modelListener, MessageGenerator.currentPlayerAndPhaseUpdateMessage(gameBoard.getPlayerTower(planningOrder[activePlayer]), this.getCurrentPhase()), clients);
            }
            else {
                System.out.println("GAME ORCHESTRATOR NOTIFY - setPhaseAndCurrentPlayer - ActivePlayerWithTower " + gameBoard.getPlayerTower(actionOrder[activePlayer]) + " phase " + this.getCurrentPhase());
                notify(modelListener, MessageGenerator.currentPlayerAndPhaseUpdateMessage(gameBoard.getPlayerTower(actionOrder[activePlayer]), this.getCurrentPhase()), clients);
            }
        }
    }


    /**
     * Set the next player.
     * If the active player is the last player of the planning phase, sets the order of the action Phase and changes phase.
     * If the active player is the last player of the action phase, controls the ending conditions and the nr. of rounds.
     * If the game continues, sets the order of the successive phase and sets the active player.
     */
    private void nextStep() {
        synchronized (phaseBlocker) {
            try {
                if (getCurrentPhase() == PLANNING) {
                    //Goes to the next player and set it into the gameBoard
                    if (activePlayer < players.size() - 1) {
                        activePlayer++;
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(planningOrder[activePlayer]));
                    }
                    //Sets the order of the action phase, set the phase as action and set the player as the first of the array
                    else {
                        if(isExpert)
                            gameBoard.resetAllTurnFlags();
                        this.setActionOrder();
                        activePlayer = 0;
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(actionOrder[activePlayer]));
                        System.out.println("GAME ORCHESTRATOR - Setting as current player: " + actionOrder[activePlayer] + " with tower " + gameBoard.getPlayerTower(actionOrder[activePlayer]));
                        this.studentMovesLeft = maxStudentMoves;
                        setCurrentPhase(PhaseEnum.ACTION_MOVE_STUDENTS);
                    }
                } else if (getCurrentPhase() == PhaseEnum.ACTION_CHOOSE_CLOUD) {
                    //Sets the active player as the next on the action order and sets the action phase as move student

                    this.specialCardAlreadyUsed = false;
                    if (activePlayer < players.size()-1) {
                        activePlayer++;
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(actionOrder[activePlayer]));
                        this.studentMovesLeft = maxStudentMoves;
                        setCurrentPhase(PhaseEnum.ACTION_MOVE_STUDENTS);
                        if(isExpert)
                            gameBoard.resetAllTurnFlags();
                    }
                    //If the game is not finished, set everything for the next planning phase
                    else if (gameBoard.isEndOfMatch().equals(EndOfMatchCondition.NoEndOfMatch)) {
                        this.setPlanningOrder();
                        activePlayer = 0;
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(planningOrder[activePlayer]));
                        this.specialCardAlreadyUsed = false;
                        setCurrentPhase(PLANNING);
                        gameBoard.increaseRound();
                        gameBoard.fillClouds();
                        try {
                            if(isExpert)
                                gameBoard.resetAllTurnFlags();
                        }
                        catch (FunctionNotImplementedException exc){
                            System.out.println("SERVER ERROR - IS EXPERT AND GAME BOARD MODE DOES NOT COINCIDE");
                        }
                        playedAssistantCard.clear();
                    }
                    //If the game ends
                    else {
                        System.out.println("GAME ORCHESTRATOR - END OF MATCH - end due to "+gameBoard.isEndOfMatch());
                        gameBoard.notifyEndOfMatchLeaderBoard();
                        setCurrentPhase(END);
                        // Waits some time and then disconnect all players
                        try{Thread.sleep(5000);}
                        catch (InterruptedException exc){}
                        for(ClientHandler client: clients)
                            client.disconnectionManager();
                    }
                }
                if(clients!=null&&modelListener!=null) {
                    if(getCurrentPhase()==PLANNING) {
                        System.out.println("GAME ORCHESTRATOR NOTIFY - nextStep - PLANNING PHASE - ActivePlayerWithTower " + gameBoard.getPlayerTower(planningOrder[activePlayer]) + " phase " + this.getCurrentPhase());
                        notify(modelListener, MessageGenerator.currentPlayerAndPhaseUpdateMessage(gameBoard.getPlayerTower(planningOrder[activePlayer]), this.getCurrentPhase()), clients);
                    }
                    else if(getCurrentPhase()!=END){
                        System.out.println("GAME ORCHESTRATOR NOTIFY - nextStep - ACTION Phase - ActivePlayerWithTower " + gameBoard.getPlayerTower(actionOrder[activePlayer]) + " phase " + this.getCurrentPhase());
                        notify(modelListener, MessageGenerator.currentPlayerAndPhaseUpdateMessage(gameBoard.getPlayerTower(actionOrder[activePlayer]), this.getCurrentPhase()), clients);
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }


    /**
     * Sets the array of the planning phase order, using the original List of players for the clockwise order and the last used cards
     */
    private void setPlanningOrder() {
        synchronized (phaseBlocker) {
            planningOrder[0] = actionOrder[0];
            int firstPlayer = players.indexOf(actionOrder[0]);
            int index = 1;
            //Add players after the first player (clockwise)
            for (int i = firstPlayer + 1; i < players.size(); i++) {
                planningOrder[index] = players.get(i);
                index++;
            }
            //Add the players before the first player (clockwise)
            for (int i = 0; i < firstPlayer; i++) {
                planningOrder[index] = players.get(i);
                index++;
            }
        }
    }

    /**
     * Sets the array of the action phase order, using the original List of players for the clockwise order and the last used cards
     */
    private void setActionOrder() {
        synchronized (phaseBlocker) {
            int index = 0;
            try {
                for (Integer value : playedAssistantCard) {
                    for (String player : planningOrder) {
                        if (value.equals(gameBoard.getLastAssistantCard(gameBoard.getPlayerTower(player)))) {
                            System.out.println("Setting player " + player + " as position " + index + " since has played card " + value);
                            actionOrder[index] = player;
                            index++;
                        }
                    }
                }
            } catch (Exception exc) {
                exc.printStackTrace();
                for (int i = 0; i < players.size(); i++)
                    actionOrder[i] = planningOrder[i];
            }
        }
    }

    /**
     * Uses the special card specified with the String of the name of the card to be used
     *
     * @param cardName : Name of the specialCard to use
     * @return the required action for the card usage or the result of the action, needed to model the interaction with the client
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     */

    public abstract String useSpecialCard(String cardName) throws FunctionNotImplementedException;

    /**
     * Choose a color for the SpecialCard usage
     *
     * @param color : color chosen by the player
     * @return the required action for the card usage or the result of the action, needed to model the interaction with the client
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     */
    public abstract String chooseColor(Color color) throws FunctionNotImplementedException;

    /**
     * Choose an Island for the SpecialCard usage
     *
     * @param position : position of the chosen island
     * @return the required action for the card usage or the result of the action, needed to model the interaction with the client
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     * @throws IslandOutOfBoundException       if the island position is incorrect
     */

    public abstract String chooseIsland(int position) throws FunctionNotImplementedException, IslandOutOfBoundException;

    /**
     * Ends the special card usage
     * @throws FunctionNotImplementedException if the gameHandler was initialized as easy and not expert game mode
     */
    public abstract String terminateSpecialCardUsage() throws FunctionNotImplementedException;


    /**
     * Creates all listeners and initialises them
     */
    private void createListeners() {
        modelListener = new ModelListener();
        addListener(modelListener);
    }

    /**
     * Method used to disconnect all clients from a game, due to the lost of a player
     */
    public void disconnectClients(){
        for(ClientHandler client : clients) {
            System.out.println("GAME ORCHESTRATOR - disconnectClients - connection lost with one client disconnecting all the clients disconnecting: " + client.getNickName());
            client.disconnect();
        }
    }

}
