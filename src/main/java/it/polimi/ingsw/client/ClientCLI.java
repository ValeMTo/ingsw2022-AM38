package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Tower;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

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
    private final String CLIBoldWhite = "\033[1m;";
    private final String verticalLine = "│";
    private final String horizontalLine = "─";
    private final String horizontalLinex10 = "──────────";


    private final boolean isRunning;
    ConnectionSocket connectionSocket;
    private ViewState viewState;
    private Scanner in = null;

    public ClientCLI() {

        this.in = new Scanner(System.in);
        this.out = new PrintStream(System.out);
        this.isRunning = true;

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
        this.connectionSocket = createConnectionWithServer(hostName, portNumber);

        sendNickname();
        cleaner();
        if (connectionSocket.isTheFirst()) {
            sendGameMode();
            sendNumOfPlayers();
        } else {
            if (acceptSettingsOfTheGame()) {
                //TODO: add the player to the lobby
                //TODO: WELCOME IN ERIANTYS
            } else connectionSocket.disconnect();
        }
        cleaner();
        this.viewState = connectionSocket.startGame();
    }

    /**
     * Start Game models the recursive phase of printing coherent menus and interface and waits for correct inputs
     */
    public void startGame() {
        while (!viewState.isEndOfMatch()) {
            showMenu();
            printArchipelago();
            //showBoard();
            printAssistantCards();
            getInputAndSendMessage();
            cleaner();
        }
    }

    /**
     *
     */
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
        this.connectionSocket = new ConnectionSocket(hostName, portNumber);
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

        if (!connectionSocket.sendNickname(nickname)) {
            System.err.println("ERROR - You have chosen a nickname that has been already taken.");
            sendNickname();
        } else System.out.println("Nickname set up correctly");
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
            System.out.println("numOfPlayers: " + connectionSocket.getNumberOfPlayers().toString());
            if (connectionSocket.getGameMode()) {
                System.out.println("GameMode: expert");
            } else {
                System.out.println("GameMode: easy");
            }
            System.out.println("Do you want to play with upper settings?[Y/n]");
            if (in.nextLine().equalsIgnoreCase("Y")) {
                connectionSocket.accept();
                return true;
            }
            System.out.println("If you don't accept the condition, you cannot play.");
            System.out.println("Are you sure?[Y/n]");
            if (in.nextLine().equalsIgnoreCase("Y")) {
                confirmation = true;
            }
        }
        connectionSocket.refuse();
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
        String[] rows = new String[7];
        int steps;
        for (int i = 0; i < 7; i++)
            rows[i] = "";
        for (Integer i : viewState.getUsableCards()) {
            steps = i / 2 + i % 2;
            rows[0] += "  ┌─────────────┐ ";
            if (i >= 10)
                rows[1] += "  │ " + CLIRed + "Priority:" + CLIEffectReset + i + " │ ";
            else
                rows[1] += "  │ " + CLIRed + "Priority:" + CLIEffectReset + i + "  │ ";
            rows[3] += "  │ " + CLIGreen + "Steps:" + CLIEffectReset + steps + "     │ ";
            for (int j = 2; j < 6; j++)
                if (j != 3)
                    rows[j] += "  │             │ ";
            rows[6] += "  └─────────────┘ ";
        }
        printVector(rows);


    }

    /**
     * Shows the possible commands and what to do in the current phase
     */
    private void showMenu() {
        if (!viewState.isActiveView()) {
            System.out.println(CLICyan + " NOT YOUR TURN - WAIT UNTIL IS YOUR TURN" + CLIEffectReset);
        } else {
            if (viewState.getCurrentPhase() == PhaseEnum.PLANNING) {
                System.out.println(CLICyan + "CHOICE AN ASSISTANT CARD TO PLAY, SELECT  BETWEEN THE USABLE CARDS (enter the Priority value)" + CLIEffectReset);
            } else if (viewState.getCurrentPhase() == PhaseEnum.ACTION_MOVE_STUDENTS) {
                System.out.println(CLICyan + "CHOICE A STUDENT TO MOVE (R,Y,B,P,G color) AND THEN CHOICE THE DESTINATION (D :your diningRoom, I: island)" + CLIEffectReset);
            } else if (viewState.getCurrentPhase() == PhaseEnum.ACTION_MOVE_MOTHER_NATURE) {
                System.out.println(CLICyan + "CHOICE WHERE TO MOVE THE MOTHER NATURE (enter the island destination island)" + CLIEffectReset);
            } else if (viewState.getCurrentPhase() == PhaseEnum.ACTION_CHOOSE_CLOUD) {
                System.out.println(CLICyan + "CHOICE THE CLOUD TO FILL YOUR SCHOOLENTRANCE (enter the cloud number)" + CLIEffectReset);
            }
        }
    }

    private void printArchipelago() {
        String[] rows = new String[12];
        for (int i = 0; i < 12; i++)
            rows[i] = "";
        int maxIslandsPerRow = 6;
        int counter = 0;
        Map students;
        for (IslandView island : viewState.getIslands()) {
            students = island.getStudentMap();
            if (island.getTowerNumber() <= 1)
                rows[0] += "                    ";
            else
                rows[0] += " " + CLICyan + "GROUP OF " + island.getTowerNumber() + " ISLANDS  " + CLIEffectReset;
            if (island.getPosition() >= 10)
                rows[1] += "   " + CLICyan + "Position NR. " + island.getPosition() + CLIEffectReset + "   ";
            else
                rows[1] += "   " + CLICyan + "Position NR. " + island.getPosition() + CLIEffectReset + "       ";
            rows[2] += "      " + horizontalLinex10 + "──      ";
            if (!island.isTaken())
                rows[3] += "    /             \\     ";
            else if (island.isTaken() && island.getTowerNumber() <= 1)
                rows[3] += "    │ " + getAnsiStringFromTower(island.getTower()) + getTowerAbbreviation(island.getTower()) + " TOWER" + CLIEffectReset + "     \\     ";
            else
                rows[3] += "    │ " + getAnsiStringFromTower(island.getTower()) + getTowerAbbreviation(island.getTower()) + " TOWER(" + island.getTowerNumber() + "N)" + CLIEffectReset + "\\     ";
            rows[4] += "  ┌─┘   " + CLIBlue + "BLUE:" + students.get(Color.BLUE) + CLIEffectReset + "     \\    ";
            rows[5] += "  │     " + CLIGreen + "GREEN:" + students.get(Color.GREEN) + CLIEffectReset + "     \\   ";
            rows[6] += "  │     " + CLIYellow + "YELLOW:" + students.get(Color.YELLOW) + CLIEffectReset + "     |  ";
            rows[7] += "  │     " + CLIPink + "PINK:" + students.get(Color.PINK) + CLIEffectReset + "       |  ";
            rows[8] += "  └──" +
                    "    " + CLIRed + "RED:" + students.get(Color.RED) + CLIEffectReset + "        /  ";
            rows[9] += "    \\               /   ";
            if (viewState.getMotherNature() != island.getPosition())
                rows[9] += "    \\               /   ";
            else
                rows[9] += "    \\ " + CLIBoldWhite + "MOTHERNATURE" + CLIEffectReset + "  /   ";
            rows[10] += "      │           │    ";

            rows[11] += "      └───────────┘     ";
            counter++;
            if (counter >= maxIslandsPerRow) {
                printVector(rows);
                counter = 0;
                for (int j = 0; j < 12; j++)
                    rows[j] = "";
            }
        }
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
