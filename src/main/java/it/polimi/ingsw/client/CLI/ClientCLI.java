package it.polimi.ingsw.client.CLI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.view.IslandView;
import it.polimi.ingsw.client.view.SubPhaseEnum;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.controller.SpecialCardRequiredAction;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.model.board.Cloud;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.StudentCounter;
import it.polimi.ingsw.model.board.Tower;
import it.polimi.ingsw.model.specialCards.SpecialCardName;

import java.io.*;
import java.util.*;

public class ClientCLI {
    private KeyboardInputReader keyboardInputReader;
    private static String hostName;
    private static int portNumber;
    private final PrintStream out;
    static final String CLICyan = "\033[36;1m";
    static final String CLIRed = "\033[31;1m";
    static final String CLIGreen = "\033[32;1m";
    static final String CLIYellow = "\033[93;1m";
    static final String CLIBlue = "\033[94;1m";
    static final String CLIPink = "\033[95;1m";
    static final String CLIEffectReset = "\033[0m";
    static final String CLIBlack = "\033[30;107;1m";
    static final String CLIGray = "\033[90;1m";
    static final String CLIBoldWhite = "\033[1m";
    private final String verticalLine = "│";
    private final String horizontalLine = "─";
    private final String horizontalLinex10 = "──────────";
    private final boolean isRunning;
    static ConnectionSocket connectionSocket;
    private ViewState viewState;
    private static Scanner in = null;
    private String nickname;
    private boolean isConnected;

    public ClientCLI() {

        in = new Scanner(System.in);
        this.out = new PrintStream(System.out);
        this.isRunning = true;
        this.isConnected = false;
        viewState = new ViewState(true);

    }

    public static void main(String[] args) {
        ClientCLI cli = new ClientCLI();
        cli.setAwaitingCLI(); //Give to the viewstate the reference to wake up the cli
        cli.requestConnection();
        cli.login();
        cli.startGame();
    }


    /**
     * Login phase of a new player.
     */
    public synchronized void login() {
        boolean confirmation=false;
        while (!confirmation){
            confirmation = sendNickname();
        }

        //cleaner();
        connectionSocket.isTheFirst();

        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("GameSettings" + viewState.getGameSettings().getNumberActualClients());
        if (viewState.getGameSettings().getNumberActualClients() <= 0) {
            sendGameMode();
            sendNumOfPlayers();
        } else {
            if (acceptSettingsOfTheGame()) {
                System.out.println("You have accepted previous rules");
                //cleaner();
                connectionSocket.startGame();
            } else {
                connectionSocket.disconnect();
                System.out.println("See you next time!!");
            }
        }

    }

    private void requestConnection() {
        boolean isConnected = false;

        while(!isConnected) {
            //Scanner input = new Scanner(System.in);
            System.out.println("Insert the server IP address:");
            hostName = in.nextLine();
            System.out.println("Insert the server port");
            portNumber = Integer.parseInt(in.nextLine());
            System.out.println("Information set: IP " + hostName + " port " + portNumber);
            this.connectionSocket = createConnectionWithServer(hostName, portNumber);
            if(connectionSocket != null)
                isConnected = true;
        }
        return;
    }

    public void printForMoveStudents(){
        System.out.println("CLIENT CLI - student move");
        printArchipelago();
        printPlayerBoard();
        showActionMoveStudentsInstruction();
        showColorChoiceInstruction();
    }

    public synchronized void printForMoveMotherNature(){
        System.out.println("CLIENT CLI - mother nature set");
        printArchipelago();
        System.out.println(CLICyan+"Last card used "+getLastAssistantCardString(viewState.getPlayerTower()));
        showMoveMotherNatureInstruction();
    }


    /**
     * Sets this CLI as the one that waits the ViewState
     */
    public void setAwaitingCLI(){
        this.viewState.setAwaitingCLI(this);
    }

    /**
     * Start Game just start the KeyboardInputReader for acquire the input, then it is viewState that prints everything
     */
    public void startGame() {
        viewState.setAwaitingCLI(this);
        this.keyboardInputReader = new KeyboardInputReader(connectionSocket,viewState,this,in);
        Thread thread = new Thread(this.keyboardInputReader);
        thread.start();
    }


    /**
     * Print method to print special cards if sufficient money and command
     */
    public void printForSpecialCardUsage() {
        if (viewState.playerHasCoinsToUseASpecialCard()) {
            printSpecialCards();
            System.out.println(CLICyan + "It seems that you have enough coins to play a special card, do you want to play one of them (Y/N)" + CLIEffectReset);
        }
    }

    /**
     * Method to use the special cards, says if the player wants to play a special card, called if the coin are sufficient
     * @return true if the special card is accepted to be used, false otherwise
     */
    public void specialCardUsage() {
        // The special card has been already used in this turn
        if (viewState.getSpecialCardUsage())
            return;
        if (!viewState.isExpert()) {
            //System.out.println(CLIRed + "The special card function is not implemented. It is only usable in expert game mode only!" + CLIEffectReset);
            return;
        }
        // If the player has not enough coins to use at least one special card it does not print the special card and let choose one
        if (!viewState.playerHasCoinsToUseASpecialCard())
            return;
        try{
            printSpecialCards();
            System.out.println(CLICyan + "It seems that you have enough coins to play a special card, do you want to play one of them? (PRESS N if not)" + CLIEffectReset);
            System.out.println(CLICyan + "Choose an usable special card or enter 'n' if you do not want to play a card write its name and press enter (choose wisely and be aware that the cost will increase next round)" + CLIEffectReset);
            System.out.println(CLICyan + "Usable special cards list " + viewState.getUpperCaseSpecialCards() + CLIEffectReset);
            for (String usableCardName : viewState.getUpperCaseSpecialCards())
                System.out.println(usableCardName);
            System.out.println(CLIPink + "This special card is not contained into the 3 usable special cards of this game or is misspelled" + CLIEffectReset);
        }
        catch (FunctionNotImplementedException exc) {
            System.out.println(CLIRed + "The special card function is not implemented. It is only usable in expert game mode only!" + CLIEffectReset);
        }
    }

    private void printActiveSpecialCardColors(){
        //TODO
    }

    /**
     * Requires the color for the special card usage
     * @return the color chosen
     */
    private void chooseColorSpecialCardComand() {
        if (viewState.getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_COLOR_CARD)) {
            // TODO : print the special card usable colors
            //printActiveSpecialCard();
            System.out.println(CLICyan + "Choose a color from the card, colors abbreviations: " + getAllColorAbbreviations());
        } else if (viewState.getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_COLOR_SCHOOL_ENTRANCE)) {
            printPlayerBoard();
            System.out.println(CLICyan + "Choose a color from the schoolEntrance, colors abbreviations: " + getAllColorAbbreviations());
        } else if (viewState.getSpecialPhase().equals(SpecialCardRequiredAction.CHOOSE_COLOR_DINING_ROOM)) {
            printPlayerBoard();
            System.out.println(CLICyan + "Choose a color from the diningRoom, colors abbreviations: " + getAllColorAbbreviations());
        }
    }

    /**
     * Used to set an island to use for the special card effect
     * @return the chosen island from the archipelago
     */
    public int chooseIslandSpecialCard(){
        printArchipelago();
        System.out.println(CLICyan+"Choose an island from the archipelago where you want to apply the effect of the chosen special card "+getAllColorAbbreviations());
        String input = in.nextLine();
        try {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException exc){
            System.out.println(CLIPink+"WRONG INPUT! It is required a number, try again"+CLIEffectReset);
            return chooseIslandSpecialCard();
        }
    }

    /**
     * Shows the possible commands and what to do in the planning phase.
     * Then get the number of card from stdin
     */
    public synchronized void showPlanningInstruction() {
        printAssistantCards();
        System.out.println(CLICyan + "CHOICE AN ASSISTANT CARD TO PLAY, SELECT  BETWEEN THE USABLE CARDS (enter the Priority value)" + CLIEffectReset);
        System.out.println("Which card have you chosen?");
    }

    /**
     * Shows the possible commands and what to do in the action phase: move cloud choice subaction
     */
    public synchronized void showCloudChoiceInstruction() {
        printClouds();
        System.out.println(CLICyan + "CHOICE THE CLOUD TO FILL YOUR SCHOOL ENTRANCE WITH (enter the cloud number)" + CLIEffectReset);
    }

    /**
     * Shows the possible commands and what to do in the action phase: move mothernature subaction
     */
    public synchronized void showMoveMotherNatureInstruction() {
        System.out.println(CLICyan + "CHOICE WHERE TO MOVE THE MOTHER NATURE (enter the island destination island)" + CLIEffectReset);
        specialCardDecision();
    }

    /**
     * Shows the possible commands and what to do in the action phase: move students subaction
     */
    public synchronized void showActionMoveStudentsInstruction() {
        System.out.println(CLICyan + "CHOICE A STUDENT TO MOVE (R,Y,B,P,G color) AND THEN CHOICE THE DESTINATION (D :your diningRoom, I: island)" + CLIEffectReset);
        specialCardDecision();
    }

    /**
     * Shows what to do in the not playing state
     */
    public synchronized void showNotYourTurnView() {
        System.out.println(CLICyan + " NOT YOUR TURN - WAIT UNTIL IS YOUR TURN" + CLIEffectReset);
    }

    public synchronized void showColorChoiceInstruction(){
        System.out.println(CLICyan+" Choice a student color: "+CLIBlue+"B"+CLIEffectReset+" for "+CLIBlue+"BLUE"+CLIEffectReset+", "+CLIGreen+"G"+CLIEffectReset+" for "+CLIGreen+"GREEN"+CLIEffectReset+", "+CLIYellow+"Y"+CLIEffectReset+" for "+CLIYellow+"YELLOW"+CLIEffectReset+", "+CLIPink+"P"+CLIEffectReset+" for "+CLIPink+"PINK"+CLIEffectReset+", "+CLIRed+"R"+CLIEffectReset+" for "+CLIRed+"RED"+CLIEffectReset+" ");
    }

    public synchronized void showIslandChoiceInstruction(){
        System.out.println(CLICyan+" Choice an island position to move the student on: "+CLIEffectReset);
    }

    /**
     * Shows the message of choice between place to diningRoom or Island and get the choice
     * @return
     */
    private void showStudentMovementDiningOrIsland(){
            System.out.println(CLICyan+"Choice where to place the Student "+getAnsiStringFromColor(keyboardInputReader.getPendingColor())+keyboardInputReader.getPendingColor()+CLIEffectReset+" from the SchoolEntrance. Enter D for DiningRoom, I for Island");
    }

    public synchronized void showMoveStudentPhase(){
        printArchipelago();
        printPlayerBoard();
        if(viewState.getSubPhaseEnum().equals(SubPhaseEnum.NO_SUB_PHASE)||viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_COLOR))
            showColorChoiceInstruction();
        else if(viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_DINING_OR_ISLAND))
            showStudentMovementDiningOrIsland();
        else if(viewState.getSubPhaseEnum().equals(SubPhaseEnum.CHOOSE_ISLAND))
            showIslandChoiceInstruction();
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
    public synchronized void cleaner() {
        System.out.println("\u001b[2J");
        System.out.flush();
        System.out.println("\033[H");
        System.out.flush();
        System.out.println("\033[2J");
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
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
                System.out.println("ERROR - The entered IP/port doesn't match any active server or the server is not running.");
                System.out.println("Please try again!");
                return null;
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
    private boolean sendNickname() {
        viewState.setNickname(null);
        boolean confirmation = false;
        nickname = null;
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
        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("NICKNAME when awaken: "+ viewState.getNickname());
        if (viewState.getNickname() != null){
            return true;
        }
        return false;


    }

    /**
     * CLI view to send the gameMode to the server
     */
    private void sendGameMode() {
        boolean confirmation = false;
        String mode;
        Boolean isExpert = null;
        while (!confirmation||isExpert==null) {
            System.out.println(">Insert the game mode [Ea/Ex]: ");
            mode = in.nextLine();
            if (mode.equalsIgnoreCase("Ex")) {
                System.out.println("You have chosen the expert mode");
                isExpert = true;
            } else if (mode.equalsIgnoreCase("Ea")) {
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
                if(in.hasNextInt())
                    numOfPlayers = in.nextInt();
                else
                    in.nextLine();
                System.out.println(numOfPlayers);
            } catch (InputMismatchException e) {
                System.out.println("Please, insert a number.");
                in.nextLine();
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
    synchronized void printAssistantCards() {
        System.out.println(CLIBlack+" - Usable assistant cards - \n\t(Less priority = first in action order, Steps = steps that the motherNature can do)\n"+CLIEffectReset);
        String[] rows = new String[5];
        int steps;
        for (int j = 0; j < 5; j++)
            rows[j] = "";
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
     * Prints the usable special cards with their texts, does not print anything if it is easy game mode
     */
    synchronized void printSpecialCards() {
        if(viewState.isExpert()) {
            Gson parser = new Gson();
            try {
                JsonObject json = parser.fromJson(new InputStreamReader(getClass().getResourceAsStream("/json/SpecialCardsEffectsDescription.json")) {
                }, JsonObject.class);
                System.out.println(CLIBlack + "         - Special cards - \n" + CLIEffectReset);
                String[] rows = new String[6];
                for (int i = 0; i < 6; i++)
                    rows[i] = "";
                for (SpecialCardName specialCardName : viewState.getUsableSpecialCards().keySet()) {
                    for (int i = 0; i < 6; i++)
                        rows[i] = "";
                    rows[0] += "  ┌─────────────┐ ";
                    rows[1] += "  │ " + CLIRed + specialCardName.name() + CLIEffectReset;
                    for (int i = specialCardName.name().length(); i < 12; i++)
                        rows[1] += " ";
                    rows[1] += "│ ";

                    rows[3] += "  │ " + CLIGreen + "Cost: " + viewState.getUsableSpecialCards().get(specialCardName)+ CLIEffectReset + "     │ ";
                    for (int j = 2; j < 5; j++) {
                        if (j == 4) {
                            if (SpecialCardName.getSpecialCardsWithStudents().contains(specialCardName)) {
                                String blocks = this.createCubesString(viewState.getSpecialCardStudents(specialCardName), 6);
                                rows[j] += "  │   " + blocks + "    │ ";
                            } else if (specialCardName.equals(SpecialCardName.HERBALIST))
                                rows[j] += "  │ TILES: " + viewState.getHerbalistTiles() + "    │ ";
                            else {
                                rows[j] += "  │             │ ";

                            }
                        } else if(j!=3)
                            rows[j] += "  │             │ ";
                    }

                    rows[5] += "  └─────────────┘ ";
                    if(json.get(specialCardName.name())!=null) {
                        List<String> message = parser.fromJson(json.get(specialCardName.name()),List.class);
                        int maxWordPerRow = 150;
                        // Set the message on the right of the special card
                        if (message != null) {
                            for (int i = 0; i < 5&&i<message.size(); i++) {
                                rows[i+1] += message.get(i);
                            }
                        }
                    }
                    printVector(rows);
                }
            }
            catch (FunctionNotImplementedException exc) {
                System.out.println(CLIPink + "EXCEPTION : The print of the special cards is for expert game mode only!" + CLIEffectReset);
            }
        }
        else{
            System.out.println(CLIPink+"NO SPECIAL CARDS FOR EASY GAME MODE"+CLIEffectReset);
        }
    }

    synchronized void printClouds(){
        System.out.println(CLIBlack + "         - Clouds - \n" + CLIEffectReset);
        String[] rows = new String[8];
        for (int i = 0; i < 8; i++)
            rows[i] = "";
        Map<Cloud,Integer> clouds = viewState.getClouds();
        for(Cloud cloud: clouds.keySet()){
            rows[0] += " " + CLICyan + "    Position : " + clouds.get(cloud) + CLIEffectReset + "     .";
            rows[1] += "      ░░░░░░░░░░      .";
            int counter=0;
            rows[2] += "     ░";
            rows[3] += "   ░░░";
            rows[4] += " ░░░░░";
            rows[5] += "   ░░░";
            rows[6] += "     ░";

            for(Color color:Color.values()) {
                rows[2+counter] += "░ " + getColorAbbreviationWithInitialAnsiiCode(color) + ":"+cloud.countStudent(color)+CLIEffectReset+" "+createCubesString(color,cloud.countStudent(color),4)+CLIEffectReset+"";
                counter++;
            }
            rows[2] += "░     .";
            rows[3] += "░░░   .";
            rows[4] += "░░░░░ .";
            rows[5] += "░░░   .";
            rows[6] += "░     .";
            rows[7] += "      ░░░░░░░░░░      .";
        }
        printVector(rows);
    }

    synchronized void printArchipelago() {
        System.out.println(CLIBlack+" - Archipelago - \n\t TW: tower on the island, M.N. : motherNature\n"+CLIEffectReset);
        String[] rows = new String[12];
        for (int i = 0; i < 12; i++)
            rows[i] = "";
        int maxIslandsPerRow = 12;
        int counter = 0;
        Map<Color,Integer> students;
        List<IslandView> islands = viewState.getIslands();
        for (int i=0;i<islands.size();i++) {
            IslandView island = islands.get(0);
            for(int j=0;j<islands.size();j++)
                if(islands.get(j).getPosition()==i+1)
                    island = islands.get(j);
            students = island.getStudentMap();
            if (island.getTowerNumber() <= 1)
                rows[0] += "                ";
            else
                rows[0] += "" + CLICyan + "GROUP X" + island.getTowerNumber() + " ISLANDS " + CLIEffectReset;
            if (island.getPosition() >= 10)
                rows[1] += " " + CLICyan + " Position : " + island.getPosition() + CLIEffectReset + " ";
            else
                rows[1] += " " + CLICyan + " Position : " + island.getPosition() + CLIEffectReset + "  ";
            rows[2] += "    ┌─────┐    .";
            if (!island.isTaken())
                rows[3] += "   ┌┘     └┐   .";
            else if ( island.getTowerNumber() <= 1)
                rows[3] += "   ┌┘ " + getAnsiStringFromTower(island.getTower()) + getTowerAbbreviation(island.getTower()) + "" + CLIEffectReset + "  └┐   .";
            else
                rows[3] += "   ┌┘" + getAnsiStringFromTower(island.getTower()) + getTowerAbbreviation(island.getTower()) + "(" + island.getTowerNumber() + ")" + CLIEffectReset + "└┐   .";
            if(students.get(Color.BLUE)>0)
            rows[4] += "  ┌┘  " + CLIBlue + "B:" + students.get(Color.BLUE) + CLIEffectReset +     "  └┐  .";
            else
            rows[4] += "  ┌┘       └┐  .";
            if(students.get(Color.GREEN)>0)
            rows[5] += " ┌┘   " + CLIGreen + "G:" + students.get(Color.GREEN) + CLIEffectReset +   "   └┐ .";
            else
            rows[5] += " ┌┘         └┐ .";
            if(students.get(Color.YELLOW)>0)
            rows[6] += " │    " + CLIYellow + "Y:" + students.get(Color.YELLOW) + CLIEffectReset + "    │ .";
            else
            rows[6] += " │           │ .";
            if(students.get(Color.PINK)>0)
            rows[7] += " └┐   " + CLIPink + "P:" + students.get(Color.PINK) + CLIEffectReset +     "   ┌┘ .";
            else
            rows[7] += " └┐         ┌┘ .";
            if(students.get(Color.RED)>0)
            rows[8] += "  └┐  " + CLIRed + "R:" + students.get(Color.RED) + CLIEffectReset +       "  ┌┘  .";
            else
                rows[8] += "  └┐       ┌┘  .";
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
        if(counter!=0)
            printVector(rows);
    }

    private String createCubesString(Color color, int number,int max){
        String cubes ="";
        for(int i =max;i>=1;i--){
            if(i<=number){
                cubes += getAnsiStringFromColor(color)+"¤"+CLIEffectReset;
            }
            else
                cubes += "░";
        }
        return cubes;
    }

    /**
     * Gets consecutive colorful blocks for the students contained in the map
     * @param students
     * @param max
     * @return
     */
    private String createCubesString(Map<Color,Integer> students,int max){
        String cubes ="";
        int i,counter = 0;
        if(students!=null) {
            for (Color color : Color.values()) {
                i = 1;
                while (students.get(color)!=null && i <= students.get(color)) {
                    cubes += getAnsiStringFromColor(color) + Color.getAbbreviation(color) + CLIEffectReset;
                    counter++;
                    i++;
                }
            }
        }
        for(i=counter;i<max;i++)
            cubes+=" ";
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


    /**
     * Represents with cubes the towers on the playerBoard
     * @param tower
     * @param number
     * @return
     */
    private String createTowersCubesString(Tower tower, int number){
        String cubes =CLIEffectReset;
        for(int i=1;i<=9;i++){
            if(i<=number){
                cubes += getAnsiStringFromTower(tower)+"¤"+CLIEffectReset;
            }
            else
                cubes += " ";
        }
        return cubes;
    }

    //TODO
    private String getLastAssistantCardString(Tower playerTower){
        if(viewState.getLastUsedCard(playerTower)!=null)
            return "Priority: "+viewState.getLastUsedCard(playerTower)+" Steps: "+(viewState.getLastUsedCard(playerTower)/2+viewState.getLastUsedCard(playerTower)%2);
        return "";
    }

    /**
     * Prints the playerBoard (SchoolBoard, DiningRoom and Professors owned)
     */
    synchronized void printPlayerBoard(){
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
            String lastcard = "";
            if(viewState.getLastUsedCard(viewState.getPlayerTower())!=null)
                lastcard = viewState.getLastUsedCard(viewState.getPlayerTower()).toString();
            if(playerTower.equals(viewState.getPlayerTower())) {
                if(!viewState.isExpert())
                rows[0] += CLICyan + ""+CLIRed+"- MY PLAYERBOARD (Tw: " + getTowerAbbreviation(playerTower) + ")"+CLICyan+" - LAST CARD USED " + lastcard + "        "+ CLIEffectReset;
                else
                rows[0] += CLICyan + ""+CLIRed+"- MY PLAYERBOARD (Tw: " + getTowerAbbreviation(playerTower) + ")"+CLICyan+" - LAST CARD USED " + lastcard + " COINS "+viewState.getPlayerCoins(playerTower) + CLIEffectReset;
                effectSchoolBoard = CLIRed;
            }
            else {
                effectSchoolBoard = CLIEffectReset;
                if(!viewState.isExpert())
                rows[0] += CLICyan + "   PLAYER BOARD TW: " + getTowerAbbreviation(playerTower) + " LAST CARD USED " + getLastAssistantCardString(playerTower) + "          " + CLIEffectReset;
                else
                rows[0] += CLICyan + "  PLAYER BOARD OF TOWER: " + getTowerAbbreviation(playerTower) + " LAST CARD USED " + getLastAssistantCardString(playerTower) + " COINS: "+viewState.getPlayerCoins(playerTower)+" " + CLIEffectReset;
            }rows[1] += effectSchoolBoard+"  ┌────────────┬────────────────┬─────────────────┐  "+CLIEffectReset;
            rows[2] += "  "+effectSchoolBoard+"│ " + CLIBlack + "PROFESSORS" + CLIEffectReset +effectSchoolBoard+ " │   " + CLIBlack + "DINING ROOM" + CLIEffectReset +effectSchoolBoard+ "  │ " + CLIBlack + "SCHOOL ENTRANCE" + CLIEffectReset +effectSchoolBoard+ " │  ";
            rows[3] += "  "+effectSchoolBoard+"│            │                │                 │  ";
            if (playerTower.equals(viewState.getProfessors().get(Color.BLUE)))
            rows[4] += "  "+effectSchoolBoard+"│    " + CLIBlue + "BLUE " + CLIEffectReset + "   ";
            else
            rows[4] += "  "+effectSchoolBoard+"│            ";
            rows[4] += effectSchoolBoard+"│ " + CLIBlue + "B:" + diningRoomOccupancy.get(Color.BLUE) + CLIEffectReset + " " + createCubesString(Color.BLUE, diningRoomOccupancy.get(Color.BLUE),10) +effectSchoolBoard+ " │  " + CLIBlue + "B:" + schoolEntranceOccupancy.get(Color.BLUE) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.BLUE, schoolEntranceOccupancy.get(Color.BLUE)) + effectSchoolBoard+"  │  ";

            if (playerTower.equals(professors.get(Color.GREEN)))
            rows[5] += effectSchoolBoard+"  │    " + CLIGreen + "GREEN " + CLIEffectReset + "  ";
            else
            rows[5] += effectSchoolBoard+"  │            ";
            rows[5] += effectSchoolBoard+"│ " + CLIGreen + "G:" + diningRoomOccupancy.get(Color.GREEN) + CLIEffectReset + " " + createCubesString(Color.GREEN, diningRoomOccupancy.get(Color.GREEN),10) +effectSchoolBoard+ " │  " + CLIGreen + "G:" + schoolEntranceOccupancy.get(Color.GREEN) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.GREEN, schoolEntranceOccupancy.get(Color.GREEN)) +effectSchoolBoard+ "  │  ";

            if (playerTower.equals(professors.get(Color.YELLOW)))
                rows[6] += effectSchoolBoard+"  │    " + CLIYellow + "YELLOW" + CLIEffectReset + "  ";
            else
                rows[6] +=effectSchoolBoard+ "  │            ";
            rows[6] += effectSchoolBoard+"│ " + CLIYellow + "Y:" + diningRoomOccupancy.get(Color.YELLOW) + CLIEffectReset + " " + createCubesString(Color.YELLOW, diningRoomOccupancy.get(Color.YELLOW),10) +effectSchoolBoard+ " │  " + CLIYellow + "Y:" + schoolEntranceOccupancy.get(Color.YELLOW) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.YELLOW, schoolEntranceOccupancy.get(Color.YELLOW)) +effectSchoolBoard+ "  │  ";

            if (playerTower.equals(professors.get(Color.PINK)))
                rows[7] += effectSchoolBoard+"  │    " + CLIPink + "PINK  " + CLIEffectReset + "  ";
            else
                rows[7] += effectSchoolBoard+"  │            ";
            rows[7] +=effectSchoolBoard+ "│ " + CLIPink + "P:" + diningRoomOccupancy.get(Color.PINK) + CLIEffectReset + " " + createCubesString(Color.PINK, diningRoomOccupancy.get(Color.PINK),10) +effectSchoolBoard+ " │  " + CLIPink + "P:" + schoolEntranceOccupancy.get(Color.PINK) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.PINK, schoolEntranceOccupancy.get(Color.PINK)) +effectSchoolBoard+ "  │  ";

            if (playerTower.equals(professors.get(Color.RED)))
                rows[8] += effectSchoolBoard+"  │    " + CLIRed + "RED   " + CLIEffectReset + "  ";
            else
                rows[8] += effectSchoolBoard+"  │            ";
            rows[8] += effectSchoolBoard+"│ " + CLIRed + "R:" + diningRoomOccupancy.get(Color.RED) + CLIEffectReset + " " + createCubesString(Color.RED, diningRoomOccupancy.get(Color.RED),10) + effectSchoolBoard+" │  " + CLIRed + "R:" + schoolEntranceOccupancy.get(Color.RED) + CLIEffectReset + " " + createSchoolEntranceCubesString(Color.RED, schoolEntranceOccupancy.get(Color.RED)) +effectSchoolBoard+ "  │  ";
            rows[9] += effectSchoolBoard+"  ├────────────┴────────────────┴─────────────────┤  "+CLIEffectReset;
            rows[10] += "  "+effectSchoolBoard+"│     TOWERS LEFT:"+viewState.getTowerLeft(playerTower)+"   "+createTowersCubesString(playerTower,viewState.getTowerLeft(playerTower))+effectSchoolBoard+"                 │  ";
            rows[11] += effectSchoolBoard+"  └───────────────────────────────────────────────┘  "+CLIEffectReset;

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

    static String getColorAbbreviationWithInitialAnsiiCode(Color color) {
        switch (color) {
            case BLUE:
                return CLIBlue+"B";
            case GREEN:
                return CLIGreen+"G";
            case YELLOW:
                return CLIYellow+"Y";
            case PINK:
                return CLIPink+"P";
            case RED:
                return CLIRed+"R";
        }
        return "";
    }

    /**
     *
     * @return all the color abbreviations with their color ansi code
     */
    static String getAllColorAbbreviations(){
        String getAllColor = "";
        for(Color color: Color.values())
            getAllColor+=getColorAbbreviationWithInitialAnsiiCode(color);
        return getAllColor+CLIEffectReset;
    }

    /**
     * Print this message for the special card input usage
     */
    private synchronized void specialCardDecision(){
        if(viewState.isExpert())
            System.out.println(ClientCLI.CLICyan+"(game mode is Expert, use V to visualize special cards, S to use a special card or just continue with the upper instructions)"+ClientCLI.CLIEffectReset);
    }


    private boolean isDraw(Map<String, Integer> standing){
        int firstClassified = 0;
        for(String s: standing.keySet()){
            if(standing.get(s)==1)firstClassified++;
        }
        if(firstClassified>1)
            return true;
        return false;
    }

    private void printStandings(Map<String, Integer> standing){
        System.out.println("\n"+CLIBoldWhite+"Game has ended, caused of end "+viewState.getEndingMotivation()+CLIEffectReset+"\n");
        System.out.println(CLIBoldWhite+"\t - STANDINGS -"+viewState.getEndingMotivation()+CLIEffectReset+"\n");
        try{Thread.sleep(200);}
        catch (InterruptedException exc){}
        int stand = 1;
        Set<String> standingNicknames = standing.keySet();
        while(!standing.isEmpty()){
            for(String s: standingNicknames)
                if(standing.containsKey(s))
                    if(standing.get(s)==stand) {
                        switch (stand) {
                            case 1:
                                System.out.println(CLIBoldWhite + stand + "st: " + s + " with " + viewState.getTowerLeft(viewState.getPlayerTower(s)) + " towers left" + CLIEffectReset);
                                break;
                            case 2:
                                System.out.println(CLIBoldWhite + stand + "nd: " + s + " with " + viewState.getTowerLeft(viewState.getPlayerTower(s)) + " towers left" + CLIEffectReset);
                                break;
                            case 3:
                                System.out.println(CLIBoldWhite + stand + "rd: " + s + " with " + viewState.getTowerLeft(viewState.getPlayerTower(s)) + " towers left" + CLIEffectReset);
                                break;
                        }
                        standing.remove(s);
                    }
            try{Thread.sleep(200);}
            catch (InterruptedException exc){}
            stand++;
        }
    }
    public void printEndOfMatch(){
        Map<String, Integer> standing = viewState.getLeaderBoard();
        if(standing.get(viewState.getNickname())==1){
            if(!isDraw(standing)){
                System.out.println(CLIBoldWhite+"\tY");
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                cleaner();
                System.out.println(CLIBoldWhite+"\tYO");
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                cleaner();
                System.out.println(CLIBoldWhite+"\tYOU");
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                cleaner();
                System.out.println(CLIBoldWhite+"\tYOU ");
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                System.out.println(CLIBoldWhite+"\tYOU W");
                cleaner();
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                System.out.println(CLIBoldWhite+"\tYOU WI");
                cleaner();
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                System.out.println(CLIBoldWhite+"\tYOU WIN");
            }
            else {
                System.out.println(CLIBoldWhite + "\tT");
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                System.out.println(CLIBoldWhite+"\tTI");
                try{Thread.sleep(200);}
                catch (InterruptedException exc){}
                System.out.println(CLIBoldWhite+"\tTIE");
            }
        }
        try{Thread.sleep(200);}
        catch (InterruptedException exc){}
        printStandings(standing);

    }


}
