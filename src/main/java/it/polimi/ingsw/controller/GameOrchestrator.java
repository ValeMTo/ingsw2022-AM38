package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.listeners.CloudsListener;
import it.polimi.ingsw.controller.listeners.GameListener;
import it.polimi.ingsw.controller.listeners.IslandsListener;
import it.polimi.ingsw.controller.listeners.PlayerBoardListener;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.*;
import it.polimi.ingsw.controller.mvc.Listenable;
import it.polimi.ingsw.controller.mvc.Listener;

import java.util.*;

public class GameOrchestrator extends Listenable {
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
    protected IslandsListener islandsListener;
    protected CloudsListener cloudsListener;
    protected Map<Tower, PlayerBoardListener> playerBoardListeners = new HashMap<>();
    protected GameListener gameListener;
    protected int id;


    public GameOrchestrator(List<String> players, boolean isExpert, int id) {
        createListeners();

        this.isExpert = isExpert;
        System.out.println("GAMEORCHESTRATOR - SETTING");
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
        for (int i = 0; i < players.size(); i++) {
            System.out.println("GAMEORCHESTRATOR - SETTING - Player " + i);
            planningOrder[i] = players.get(i);
            actionOrder[i] = players.get(i);
            if (gameBoard.getPlayerTower(players.get(i)) != null)
                playersTower.put(players.get(i), gameBoard.getPlayerTower(players.get(i)));
                // In case of null pointer exception
            else {
                switch (i) {
                    case 0:
                        playersTower.put(players.get(i), Tower.WHITE);
                        break;
                    case 1:
                        playersTower.put(players.get(i), Tower.BLACK);
                        break;
                    case 2:
                        playersTower.put(players.get(i), Tower.GRAY);
                        break;
                }
            }
        }

        gameBoard.fillClouds();

        currentPhase = PhaseEnum.PLANNING;
    }

    /**
     * Getter of the active payer, used to know if the player that want to do a move is the actual player or not
     *
     * @return : the String of the nickName of the active player.
     */
    public String getActivePlayer() {
        synchronized (phaseBlocker) {
            return this.players.get(activePlayer);
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
    private void setCurrentPhase(PhaseEnum updatePhase) { //TODO: solve, since you access the gameBoard you should also avoid actions but possible deadlocks!
        synchronized (phaseBlocker) {
            if (gameBoard.isEndOfMatch().equals(EndOfMatchCondition.InstantEndOfMatch))
                this.currentPhase = PhaseEnum.END;
            else this.currentPhase = updatePhase;
        }
        notify(gameListener, MessageGenerator.phaseUpdateMessage(currentPhase));
    }

    /**
     * Moves the student to the diningRoom
     *
     * @param color : Color of the student to move
     * @return : true if the student is correctly moved, otherwise false
     */
    public boolean moveStudent(Color color) {
        synchronized (actionBlocker) {
            if (getCurrentPhase() != PhaseEnum.ACTION_MOVE_STUDENTS) return false;
            if (this.studentMovesLeft == 0) {
                this.setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
                return false;
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
                    notify(playerBoardListeners.get(gameBoard.getCurrentPlayer()), MessageGenerator.moveStudentMessage(color, StudentCounter.SCHOOLENTRANCE, StudentCounter.DININGROOM));
                    if (studentMovesLeft == 0) setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
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
     * Moves te student to the Island
     *
     * @param color  : Color of the student to move from the SchoolEntrance to the Island
     * @param island : Number of the Island we want to move the student into
     * @return true if the student was added correctly
     */
    public boolean moveStudent(Color color, int island) throws IndexOutOfBoundsException, AllMovesUsedException {
        synchronized (actionBlocker) {
            if (getCurrentPhase() != PhaseEnum.ACTION_MOVE_STUDENTS) return false;
            if (studentMovesLeft == 0) {
                setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
                throw new AllMovesUsedException();
            }
            try {
                if (gameBoard.removeStudent(StudentCounter.SCHOOLENTRANCE, color)) {
                    if (!gameBoard.addStudent(StudentCounter.ISLAND, color, island)) {
                        gameBoard.addStudent(StudentCounter.SCHOOLENTRANCE, color);
                        studentMovesLeft--;
                        if (studentMovesLeft == 0) setCurrentPhase(PhaseEnum.ACTION_MOVE_MOTHER_NATURE);
                        notify(playerBoardListeners.get(gameBoard.getCurrentPlayer()), MessageGenerator.moveStudentMessage(color, StudentCounter.SCHOOLENTRANCE, StudentCounter.ISLAND, island));
                        return true;
                    }
                }
                return false;
            } catch (Exception exc) {
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
     */
    public boolean chooseCard(int priority) throws IndexOutOfBoundsException, AlreadyUsedException {
        synchronized (actionBlocker) {
            if (getCurrentPhase() != PhaseEnum.PLANNING) return false;
            if (gameBoard.useAssistantCard(gameBoard.getCurrentPlayer(), priority)){
                nextPlayer();
                try {
                    notify(playerBoardListeners.get(gameBoard.getCurrentPlayer()),
                            MessageGenerator.assistantCardUpdateMessage(gameBoard.getCurrentPlayer(),
                            (ArrayList<Integer>) gameBoard.getUsableAssistantCard(gameBoard.getCurrentPlayer()).keySet()));
                } catch (NoSuchTowerException e) {
                    e.printStackTrace();
                }
            }

            return false;
        }
    }

    /**
     * Moves the motherNature to the designed island
     *
     * @param destinationIsland : island where the player wants to move the motherNature
     * @return : true if the move is allowed, false if the island is unreachable with the steps given by the last card
     * @throws IslandOutOfBoundException : if the island position is out of bound
     */
    public boolean moveMotherNature(int destinationIsland) throws IslandOutOfBoundException {
        synchronized (actionBlocker) {
            if (!this.getCurrentPhase().equals(PhaseEnum.ACTION_MOVE_MOTHER_NATURE)) return false;
            if (gameBoard.moveMotherNature(destinationIsland)) {
                setCurrentPhase(PhaseEnum.ACTION_CHOOSE_CLOUD);
                notify(islandsListener, MessageGenerator.moveMotherNatureMessage(destinationIsland));
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
     */
    public boolean chooseCloud(int cloudPosition) throws IndexOutOfBoundsException, AlreadyUsedException {
        synchronized (actionBlocker) {
            if (getCurrentPhase() != PhaseEnum.ACTION_CHOOSE_CLOUD) return false;
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
            nextPlayer();
            notify(cloudsListener, MessageGenerator.cloudViewUpdateMessage(cloudPosition, gameBoard.getCloudLimit(), null));
            return true;
        }
    }

    /**
     * Updates to the successive player of the planning phase
     *
     * @return : true if now the activePlayer has been updated, false if the phase is incorrect or if we have finished the passive phase
     */
    private boolean updateNextPlanningPlayer() {
        synchronized (phaseBlocker) {
            if (getCurrentPhase() != PhaseEnum.PLANNING) return false;
            if (activePlayer >= players.size() - 1) {
                setCurrentPhase(PhaseEnum.ACTION_MOVE_STUDENTS);
                return false;
            }
            activePlayer++;
            return true;
        }
    }

    /**
     * Updates to the successive player of the action phase
     *
     * @return : true if now the activePlayer has been updated, false if the phase is incorrect or if we have finished the passive phase
     */
    private boolean updateNextActivePlayer() {
        synchronized (phaseBlocker) {
            if (getCurrentPhase() == PhaseEnum.PLANNING) {
                if (activePlayer >= players.size() - 1) return false;
                activePlayer++;
                return true;
            }
            return false;
        }
    }

    /**
     * Set the next player.
     * If the active player is the last player of the planning phase, sets the order of the action Phase and changes phase.
     * If the active player is the last player of the action phase, controls the ending conditions and the nr. of rounds.
     * If the game continues, sets the order of the successive phase and sets the active player.
     */
    private void nextPlayer() {
        synchronized (phaseBlocker) {
            if (getCurrentPhase() == PhaseEnum.PLANNING) {
                //Goes to the next player and set it into the gameBoard
                if (activePlayer < players.size()) {
                    activePlayer++;
                    try {
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(planningOrder[activePlayer]));
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
                //Sets the order of the action phase, set the phase as action and set the player as the first of the array
                else {
                    this.setActionOrder();
                    activePlayer = 0;
                    try {
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(actionOrder[activePlayer]));
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    setCurrentPhase(PhaseEnum.ACTION_MOVE_STUDENTS);
                }
            } else if (getCurrentPhase() == PhaseEnum.ACTION_CHOOSE_CLOUD) {
                //Sets the active player as the next on the action order and sets the action phase as move student
                if (activePlayer < players.size()) {
                    activePlayer++;
                    try {
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(actionOrder[activePlayer]));
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    setCurrentPhase(PhaseEnum.ACTION_MOVE_STUDENTS);
                }
                //If the game is not finished, set everything for the next planning phase
                else if (gameBoard.isEndOfMatch().equals(EndOfMatchCondition.NoEndOfMatch)) {
                    this.setPlanningOrder();
                    activePlayer = 0;
                    try {
                        gameBoard.setCurrentPlayer(gameBoard.getPlayerTower(planningOrder[activePlayer]));
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    setCurrentPhase(PhaseEnum.PLANNING);
                    gameBoard.increaseRound();
                    gameBoard.fillClouds();
                    playedAssistantCard.clear();
                }
                //If the game ends
                else {
                    setCurrentPhase(PhaseEnum.END);
                }
            }
        }
        notify(gameListener, MessageGenerator.currentPlayerUpdateMessage(gameBoard.getCurrentPlayer()));
    }


    /**
     * Sets the array of the planning phase order, using the original List of players for the clockwise order and the last used cards
     */
    private void setPlanningOrder() {
        synchronized (actionOrder) {
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
        synchronized (actionOrder) {
            int index = 0;
            try {
                for (Integer value : playedAssistantCard) {
                    for (String player : planningOrder) {
                        if (value.equals(gameBoard.getLastAssistantCard(gameBoard.getPlayerTower(player)))) {
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
     * Creates all listeners and initialises them
     */
    private void createListeners(){
        islandsListener = new IslandsListener();
        cloudsListener = new CloudsListener();
        gameListener =  new GameListener();

        addListener(islandsListener);
        addListener(cloudsListener);
        addListener(gameListener);
        for(Tower person : playersTower.values()){
            playerBoardListeners.put(person, new PlayerBoardListener());
            addListener(playerBoardListeners.get(person));
        }
    }

    @Override
    public void notify(Listener listener, String message) {
        listener.update(message);

    }
}
