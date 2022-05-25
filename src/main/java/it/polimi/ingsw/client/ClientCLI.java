package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;

import java.io.PrintStream;
import java.util.*;

public class ClientCLI {
    private static String hostName;
    private static int portNumber;
    private final PrintStream out;
    private final String CLICyan = "\033[36;1m";
    private final String CLIRed = "\033[31;1m";
    private final String CLIGreen = "\033[32;1m";
    private final String CLIYellow = "\033[93;1m";
    private final String CLIBlue = "\033[94;1m";
    private final String CLIPink = "\033[95;1m";
    private final String CLIEffectReset = "\033[0m";
    private final String CLIBlack = "\033[30;107;1m";
    private final String CLIGray = "\033[90;1m";
    private final String CLIBoldWhite = "\033[1m";
    private final String verticalLine = "│";
    private final String horizontalLine = "─";
    private final String horizontalLinex10 = "──────────";
    private final boolean isRunning;
    ConnectionSocket connectionSocket;
    private ViewState viewState;
    private Scanner in = null;

    public ClientCLI() {

        in = new Scanner(System.in);
        this.out = new PrintStream(System.out);
        this.isRunning = true;
        viewState = new ViewState();


    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the server IP address:");
        hostName = scanner.nextLine();
        System.out.println("Insert the server port");
        portNumber = scanner.nextInt();
        ClientCLI cli = new ClientCLI();
        System.out.println("Information set: IP " + hostName + " port " + portNumber);
        cli.login();
        cli.startGame();
    }


    /**
     * Login phase of a new player.
     */
    public void login() {
        // TODO: remove this things
        this.viewState = new ViewState();
        Map<String,Tower> mapPlayers = new HashMap<>();
        mapPlayers.put("Nick",Tower.WHITE);
        mapPlayers.put("Vale",Tower.BLACK);
        mapPlayers.put("Fra",Tower.GRAY);

        viewState.setViewState(mapPlayers,false);
        viewState.setPlayerTower(Tower.WHITE);
        Map<Color, Integer> mapOccupancy = new HashMap<>();
        mapOccupancy.put(Color.BLUE,2);
        viewState.setDiningRoomOccupancy(Tower.WHITE,mapOccupancy);
        viewState.setDiningRoomOccupancy(Tower.BLACK,mapOccupancy);
        viewState.setDiningRoomOccupancy(Tower.GRAY,mapOccupancy);
        mapOccupancy.put(Color.YELLOW,4);
        viewState.setSchoolEntranceOccupancy(Tower.GRAY,mapOccupancy);
        Map<Color, Integer> dini = viewState.getDiningRoomOccupancy(Tower.WHITE);
        printArchipelago();
        printAssistantCards();
        // Until here
        printPlayerBoard();
        this.connectionSocket = createConnectionWithServer(hostName, portNumber);
        sendNickname();
        cleaner();
        connectionSocket.isTheFirst();

        //TODO: listener that waits the no null value of GameSetting
        while (viewState.getGameSettings() == null){}

        System.out.println("GameSettings" + viewState.getGameSettings().getActualClients());
        if (viewState.getGameSettings().getActualClients() <= 0) {
            sendGameMode();
            sendNumOfPlayers();
        } else {
            if (acceptSettingsOfTheGame()) {
                System.out.println("You have accepted previous rules");
                cleaner();
                connectionSocket.startGame();
            } else {
                connectionSocket.disconnect();
                System.out.println("See you next time!!");
            }
        }

    }

    /**
     * Start Game models the recursive phase of printing coherent menus and interface and waits for correct inputs
     */
    public void startGame() {
        boolean YourTurnShown = false;
        while (!viewState.isEndOfMatch()) {
            if (!viewState.isActiveView()) {
                if (!YourTurnShown){
                    showNotYoutTurnView();
                    YourTurnShown = true;
                }
            } else {
                YourTurnShown= false;
                if (viewState.getCurrentPhase() == PhaseEnum.PLANNING) {
                    printAssistantCards();
                    int card = showPlanningInstructionAndGetCard();
                    connectionSocket.setAssistantCard(card);
                    viewState.setCurrentPhase(PhaseEnum.ACTION_MOVE_STUDENTS); //TODO: maybe change
                    connectionSocket.updateNewPhase(PhaseEnum.ACTION_MOVE_STUDENTS);
                } else if (viewState.getCurrentPhase() == PhaseEnum.ACTION_MOVE_STUDENTS) {
                    showActionMoveStudentsInstruction();
                } else if (viewState.getCurrentPhase() == PhaseEnum.ACTION_MOVE_MOTHER_NATURE) {
                    showMoveMotherNatureInstruction();
                } else if (viewState.getCurrentPhase() == PhaseEnum.ACTION_CHOOSE_CLOUD) {
                    showCloudChoiceInstruction();
                }
            }
            //Not put cleaner here
        }
    }

    /**
     * Shows the possible commands and what to do in the planning phase.
     * Then get the number of card from stdin
     */
    private int showPlanningInstructionAndGetCard() {
        System.out.println(CLICyan + "CHOICE AN ASSISTANT CARD TO PLAY, SELECT  BETWEEN THE USABLE CARDS (enter the Priority value)" + CLIEffectReset);
        System.out.println("Which card have you chosen?");

        System.out.println("Insert a number");
        Integer numOfCard = 0;
        while ((numOfCard <= 0 || numOfCard > 10)) {
            try {
                numOfCard = in.nextInt();
                System.out.println("You have chosen tha card with " + numOfCard + " priority");
            } catch (InputMismatchException e) {
                System.out.println("Please, insert a number.");
            }
        }

        return numOfCard;

    }

    /**
     * Shows the possible commands and what to do in the action phase: move cloud choice subaction
     */
    private void showCloudChoiceInstruction() {
        System.out.println(CLICyan + "CHOICE THE CLOUD TO FILL YOUR SCHOOL ENTRANCE (enter the cloud number)" + CLIEffectReset);
    }

    /**
     * Shows the possible commands and what to do in the action phase: move mothernature subaction
     */
    private void showMoveMotherNatureInstruction() {
        System.out.println(CLICyan + "CHOICE WHERE TO MOVE THE MOTHER NATURE (enter the island destination island)" + CLIEffectReset);
    }

    /**
     * Shows the possible commands and what to do in the action phase: move students subaction
     */
    private void showActionMoveStudentsInstruction() {
        System.out.println(CLICyan + "CHOICE A STUDENT TO MOVE (R,Y,B,P,G color) AND THEN CHOICE THE DESTINATION (D :your diningRoom, I: island)" + CLIEffectReset);
    }

    /**
     * Shows what to do in the not playing state
     */
    private void showNotYoutTurnView() {
        System.out.println(CLICyan + " NOT YOUR TURN - WAIT UNTIL IS YOUR TURN" + CLIEffectReset);
    }

    //TODO : support to sending message in base of the current phase
    public void getInputAndSendMessage() {
        String input = in.nextLine();
    }

    /**
     * Given a color enumeration value, gives the proper command to print the characters into the CLI
     *
     * @param color : color that is given to be converted
     * @return the ANSI command string
     */
    private String getColorCommand(Color color) {
        switch (color) {
            case BLUE:
                return CLIBlue;
            case RED:
                return CLIRed;
            case GREEN:
                return CLIGreen;
            case PINK:
                return CLIPink;
            case YELLOW:
                return CLIYellow;
        }
        return "";
    }

    /**
     * Cleans the CLI
     */
    private void cleaner() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }


    /**
     * Establish connection with the server
     *
     * @param hostName   IP of the server
     * @param portNumber port of the Eriantys server
     * @return connectionSocket
     */
    private ConnectionSocket createConnectionWithServer(String hostName, int portNumber) {
        this.connectionSocket = new ConnectionSocket(hostName, portNumber, viewState);
        try {
            if (!connectionSocket.setup()) {
                System.err.println("ERROR - The entered IP/port doesn't match any active server or the server is not running.");
                System.err.println("Please try again!");
            }
            System.out.println("Socket Connection setup completed!");
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
        return connectionSocket;
    }

    /**
     * CLI view to ask the nickname to the server
     */
    private void sendNickname() {
        boolean confirmation = false;
        String nickname = null;
        while (!confirmation) {
            System.out.println(">Insert your nickname: ");
            nickname = in.nextLine();
            System.out.println(">You chose: " + nickname);
            System.out.println(">Is it ok? [Y/n]:  ");
            if (in.nextLine().equalsIgnoreCase("Y")) {
                confirmation = true;
            } else {
                nickname = null;
            }
        }
        connectionSocket.sendNickname(nickname);
    }

    /**
     * CLI view to send the gameMode to the server
     */
    private void sendGameMode() {
        boolean confirmation = false;
        String mode;
        Boolean isExpert = null;
        while (!confirmation) {
            System.out.println(">Insert the game mode [E/D]: ");
            mode = in.nextLine();
            if (mode.equalsIgnoreCase("E")) {
                System.out.println("You have chosen the expert mode");
                isExpert = true;
            } else if (mode.equalsIgnoreCase("D")) {
                System.out.println("You have chosen the easy mode");
                isExpert = false;
            }
            System.out.println(">Is it ok? [Y/n]:  ");
            if (in.nextLine().equalsIgnoreCase("Y")) {
                confirmation = true;
            }
        }
        connectionSocket.setGameMode(isExpert);
    }

    /**
     * CLI view to send the numberOfPlayers to the server
     */
    private void sendNumOfPlayers() {
        int numOfPlayers = 0;
        while (numOfPlayers != 2 && numOfPlayers != 3) {
            System.out.println(">Insert the number of players [2/3]: ");
            try {
                numOfPlayers = in.nextInt();
                System.out.println(numOfPlayers);
            } catch (InputMismatchException e) {
                System.out.println("Please, insert a number.");
            }
        }
        System.out.println("You have chosen " + numOfPlayers + " players game mode");
        connectionSocket.setNumberOfPlayers(numOfPlayers);
    }

    /**
     * If the player is not the first one, it has to accept settings of the game, set by the first player.
     * If the player does not accept the rules imposed, it will not play.
     *
     * @return decision of the player to play.
     */
    private boolean acceptSettingsOfTheGame() {
        boolean confirmation = false;
        while (!confirmation) {
            System.out.println("The rules are already set...");
            System.out.println("numOfPlayers: " + viewState.getGameSettings().getNumPlayers());
            if (viewState.getGameSettings().getExpert()) {
                System.out.println("GameMode: expert");
            } else {
                System.out.println("GameMode: easy");
            }
            System.out.println("Do you want to play with upper settings?[Y/n]");
            if (in.nextLine().equalsIgnoreCase("Y")) {
                connectionSocket.acceptRules();
                return true;
            }
            System.out.println("If you don't accept the condition, you cannot play.");
            System.out.println("Are you sure?[Y/n]");
            if (in.nextLine().equalsIgnoreCase("Y")) {
                confirmation = true;
            }
        }
        connectionSocket.refuseRules();
        return false;
    }

    private void printVector(String[] stringMatrix) {
        for (String rows : stringMatrix) {
            System.out.println(rows);
        }
    }

    /**
     * Prints the usable assistant card
     */
    private void printAssistantCards() {
        System.out.println(CLIBlack+" - Usable assistant cards - \n\t(Less priority = first in action order, Steps = steps that the motherNature can do)\n"+CLIEffectReset);
        String[] rows = new String[5];
        int steps;
        for (int i = 0; i < 5; i++)
            rows[i] = "";
        for (Integer i : viewState.getUsableCards()) {
            steps = i / 2 + i % 2;
            rows[0] += "  ┌─────────────┐ ";
            if (i >= 10)
                rows[1] += "  │ " + CLIRed + "Priority:" + CLIEffectReset + i + " │ ";
            else
                rows[1] += "  │ " + CLIRed + "Priority:" + CLIEffectReset + i + "  │ ";
            rows[3] += "  │ " + CLIGreen + "Steps:" + CLIEffectReset + steps + "     │ ";
            for (int j = 2; j < 4; j++)
                if (j != 3)
                    rows[j] += "  │             │ ";
            rows[4] += "  └─────────────┘ ";
        }
        printVector(rows);
    }

    /**
     * Prints the usable special cards
     */
    private void printSpecialCards() {
        System.out.println(CLIBlack + " - Special cards - "+CLIEffectReset);
        String[] rows = new String[3];
        for (int i = 0; i < 7; i++)
            rows[i] = "";
        for (Integer i : viewState.getUsableCards()) {

            rows[0] += "  ┌─────────────┐ ";
            if (i >= 10)
                rows[1] += "  │ " + CLIRed + "Priority:" + CLIEffectReset + i + " │ ";
            else
                rows[1] += "  │ " + CLIRed + "Priority:" + CLIEffectReset + i + "  │ ";
            rows[3] += "  │ " + CLIGreen + "Steps:" + CLIEffectReset  + "     │ ";
            for (int j = 2; j < 6; j++)
                if (j != 3)
                    rows[j] += "  │             │ ";
            rows[6] += "  └─────────────┘ ";
        }
        printVector(rows);
    }

    private void printArchipelago() {
        System.out.println(CLIBlack+" - Archipelago - \n\t TW: tower on the island, M.N. : motherNature\n"+CLIEffectReset);
        String[] rows = new String[12];
        for (int i = 0; i < 12; i++)
            rows[i] = "";
        int maxIslandsPerRow = 12;
        int counter = 0;
        Map students;
        for (IslandView island : viewState.getIslands()) {
            students = island.getStudentMap();
            if (island.getTowerNumber() <= 1)
                rows[0] += "                    ";
            else
                rows[0] += " " + CLICyan + "GROUP OF " + island.getTowerNumber() + " ISLANDS  " + CLIEffectReset;
            if (island.getPosition() >= 10)
                rows[1] += " " + CLICyan + " Position : " + island.getPosition() + CLIEffectReset + " ";
            else
                rows[1] += " " + CLICyan + " Position : " + island.getPosition() + CLIEffectReset + "  ";
            rows[2] += "    ┌─────┐    .";
            if (!island.isTaken())
                rows[3] += "   ┌┘     └┐   .";
            else if (island.isTaken() && island.getTowerNumber() <= 1)
                rows[3] += "   ┌┘ " + getAnsiStringFromTower(island.getTower()) + getTowerAbbreviation(island.getTower()) + " TW" + CLIEffectReset + " └┐    .";
            else
                rows[3] += "   ┌┘ " + getAnsiStringFromTower(island.getTower()) + getTowerAbbreviation(island.getTower()) + " TW(" + island.getTowerNumber() + "N)" + CLIEffectReset + " └┐ .";
            rows[4] += "  ┌┘  " + CLIBlue + "B:" + students.get(Color.BLUE) + CLIEffectReset +     "  └┐  .";
            rows[5] += " ┌┘   " + CLIGreen + "G:" + students.get(Color.GREEN) + CLIEffectReset +   "   └┐ .";
            rows[6] += " │    " + CLIYellow + "Y:" + students.get(Color.YELLOW) + CLIEffectReset + "    │ .";
            rows[7] += " └┐   " + CLIPink + "P:" + students.get(Color.PINK) + CLIEffectReset +     "   ┌┘ .";
            rows[8] += "  └┐  " + CLIRed + "R:" + students.get(Color.RED) + CLIEffectReset +       "  ┌┘  .";
            if (viewState.getMotherNature() != island.getPosition())
            rows[9] += "   └┐     ┌┘   .";
            else
            rows[9]  += "   └┐ " + CLIBoldWhite + "M.N." + CLIEffectReset + "┌┘   .";
            rows[10] += "    └─────┘    .";

            counter++;
            if (counter >= maxIslandsPerRow) {
                printVector(rows);
                counter = 0;
                for (int j = 0; j < 11; j++)
                    rows[j] = "";
            }
        }
    }

    private String createCubesString(Color color, int number){
        String cubes ="";
        for(int i =10;i>=1;i--){
            if(i<=number){
                cubes += getAnsiStringFromColor(color)+"¤"+CLIEffectReset;
            }
            else
                cubes += "░";
        }
        return cubes;
    }

    private String createSchoolEntranceCubesString(Color color, int number){
        String cubes ="";
        for(int i =1;i<=9;i++){
            if(i<=number){
                cubes += getAnsiStringFromColor(color)+"¤"+CLIEffectReset;
            }
            else
                cubes += " ";
        }
        return cubes;
    }

    private String getLastAssistantCardString(Tower playerTower){
        return "";
    }

    /**
     * Prints the playerBoard (SchoolBoard, DiningRoom and Professors owned)
     */
    private void printPlayerBoard(){
        System.out.println(CLIBlack+" - PlayerBoard - \n\t PROFESSORS : owned professors, DINING ROOM: student in the dining room, needed to determine the owned professors,\n SCHOOL ENTRANCE: Movable students \n"+CLIEffectReset);
        String[] rows = new String[12];
        for(int i=0;i<12;i++)
            rows [i] = "";
        Map<Color, Integer> schoolEntranceOccupancy;
        Map<Color, Integer> diningRoomOccupancy;
        Map<Color, Tower> professors = viewState.getProfessors();


        for(Tower playerTower: viewState.getTowers()) {
            diningRoomOccupancy = viewState.getDiningRoomOccupancy(playerTower);
            schoolEntranceOccupancy =  viewState.getSchoolEntranceOccupancy(playerTower);
            String effectSchoolBoard;
            if(playerTower.equals(viewState.getPlayerTower())) {
                rows[0] += CLICyan + ""+CLIRed+"- MY PLAYER BOARD (Tower: " + getTowerAbbreviation(playerTower) + ")"+CLICyan+" -  LAST CARD USED     " + CLIEffectReset;
                effectSchoolBoard = CLIRed;
            }
            else {
                effectSchoolBoard = CLIEffectReset;
                rows[0] += CLICyan + "   PLAYER BOARD OF TOWER: " + getTowerAbbreviation(playerTower) + " LAST CARD USED " + getLastAssistantCardString(playerTower) + "          " + CLIEffectReset;
            }rows[1] += effectSchoolBoard+"  ┌────────────┬────────────────┬─────────────────┐  "+CLIEffectReset;
            rows[2] += "  "+effectSchoolBoard+"│ " + CLIBlack + "PROFESSORS" + CLIEffectReset +effectSchoolBoard+ " │   " + CLIBlack + "DINING ROOM" + CLIEffectReset +effectSchoolBoard+ "  │ " + CLIBlack + "SCHOOL ENTRANCE" + CLIEffectReset +effectSchoolBoard+ " │  ";
            rows[3] += "  "+effectSchoolBoard+"│            │                │                 │  ";
            if (playerTower.equals(viewState.getProfessors().get(Color.BLUE)))
            rows[4] += "  "+effectSchoolBoard+"│     " + CLIBlue + "BLUE" + CLIEffectReset + "   ";
            else
            rows[4] += "  "+effectSchoolBoard+"│            ";
            rows[4] += effectSchoolBoard+"│ " + CLIBlue + "B:" + diningRoomOccupancy.get(Color.BLUE) + CLIEffectReset + " " + createCubesString(Color.BLUE, diningRoomOccupancy.get(Color.BLUE)) +effectSchoolBoard+ " │  " + CLIBlue + "B:" + schoolEntranceOccupancy.get(Color.BLUE) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.BLUE, schoolEntranceOccupancy.get(Color.BLUE)) + effectSchoolBoard+"  │  ";

            if (playerTower.equals(professors.get(Color.GREEN)))
            rows[5] += effectSchoolBoard+"  │     " + CLIGreen + "GREEN" + CLIEffectReset + "  ";
            else
            rows[5] += effectSchoolBoard+"  │            ";
            rows[5] += effectSchoolBoard+"│ " + CLIGreen + "G:" + diningRoomOccupancy.get(Color.GREEN) + CLIEffectReset + " " + createCubesString(Color.GREEN, diningRoomOccupancy.get(Color.GREEN)) +effectSchoolBoard+ " │  " + CLIGreen + "G:" + schoolEntranceOccupancy.get(Color.GREEN) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.GREEN, schoolEntranceOccupancy.get(Color.GREEN)) +effectSchoolBoard+ "  │  ";

            if (playerTower.equals(professors.get(Color.GREEN)))
                rows[6] += effectSchoolBoard+"  │    " + CLIYellow + "YELLOW" + CLIEffectReset + "  ";
            else
                rows[6] +=effectSchoolBoard+ "  │            ";
            rows[6] += effectSchoolBoard+"│ " + CLIYellow + "Y:" + diningRoomOccupancy.get(Color.YELLOW) + CLIEffectReset + " " + createCubesString(Color.YELLOW, diningRoomOccupancy.get(Color.YELLOW)) +effectSchoolBoard+ " │  " + CLIYellow + "Y:" + schoolEntranceOccupancy.get(Color.YELLOW) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.YELLOW, schoolEntranceOccupancy.get(Color.YELLOW)) +effectSchoolBoard+ "  │  ";

            if (playerTower.equals(professors.get(Color.GREEN)))
                rows[7] += effectSchoolBoard+"  │    " + CLIPink + "PINK" + CLIEffectReset + "  ";
            else
                rows[7] += effectSchoolBoard+"  │            ";
            rows[7] +=effectSchoolBoard+ "│ " + CLIPink + "P:" + diningRoomOccupancy.get(Color.PINK) + CLIEffectReset + " " + createCubesString(Color.PINK, diningRoomOccupancy.get(Color.PINK)) +effectSchoolBoard+ " │  " + CLIPink + "P:" + schoolEntranceOccupancy.get(Color.PINK) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.PINK, schoolEntranceOccupancy.get(Color.PINK)) +effectSchoolBoard+ "  │  ";

            if (playerTower.equals(professors.get(Color.GREEN)))
                rows[8] += effectSchoolBoard+"  │    " + CLIRed + "Red" + CLIEffectReset + "  ";
            else
                rows[8] += effectSchoolBoard+"  │            ";
            rows[8] += effectSchoolBoard+"│ " + CLIRed + "R:" + diningRoomOccupancy.get(Color.RED) + CLIEffectReset + " " + createCubesString(Color.RED, diningRoomOccupancy.get(Color.RED)) + effectSchoolBoard+" │  " + CLIRed + "R:" + schoolEntranceOccupancy.get(Color.RED) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.RED, schoolEntranceOccupancy.get(Color.RED)) +effectSchoolBoard+ "  │  ";
            rows[9] += effectSchoolBoard+"  └────────────┴────────────────┴─────────────────┘  "+CLIEffectReset;

        }
            printVector(rows);

    }

    /**
     * Returns the color codes given the Tower
     *
     * @param tower : tower we want to print into the island
     * @return the String of the ANSI code for that specific tower
     */
    private String getAnsiStringFromTower(Tower tower) {
        switch (tower) {
            case WHITE:
                return CLIBoldWhite;
            case BLACK:
                return CLIBlack;
            case GRAY:
                return CLIGray;
        }
        return "";
    }

    /**
     * Returns the color codes given the Color
     *
     * @param color : color we want to print into the island
     * @return the String of the ANSI code for that specific tower
     */
    private String getAnsiStringFromColor(Color color) {
        switch (color) {
            case BLUE:
                return CLIBlue;
            case GREEN:
                return CLIGreen;
            case YELLOW:
                return CLIYellow;
            case RED:
                return CLIRed;
            case PINK:
                return CLIPink;
        }
        return "";
    }

    private String getTowerAbbreviation(Tower tower) {
        switch (tower) {
            case WHITE:
                return "WH";
            case BLACK:
                return "BK";
            case GRAY:
                return "GR";
        }
        return "";
    }
}
