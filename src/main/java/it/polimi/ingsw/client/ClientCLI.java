package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.controller.PhaseEnum;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

public class ClientCLI {
    private ViewState viewState;
    private static String hostName;
    private static int portNumber;
    private Scanner in = null;
    private final PrintStream out;
    private boolean isRunning;
    private final String CLICyan = "\033[36";
    ConnectionSocket connectionSocket;

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
        this.connectionSocket= createConnectionWithServer(hostName, portNumber);

        sendNickname();
        cleaner();
        if(connectionSocket.isTheFirst()){
            sendGameMode();
            sendNumOfPlayers();
        } else {
            if(acceptSettingsOfTheGame()){
                //TODO: add the player to the lobby
                //TODO: WELCOME IN ERIANTYS
            }
            else
                connectionSocket.disconnect();
        }
        this.viewState = connectionSocket.startGame();
    }

    public void startGame(){
        while(!viewState.isEndOfMatch()){
            cleaner();
            showMenu();
            //showBoard();
            getInputAndSendMessage();
        }
    }

    public void getInputAndSendMessage(){
        String input = in.nextLine();
    }

    private void cleaner(){
        System.out.println("\033[H\033[2J");
        ProcessBuilder processBuilder;
        try{
            if(System.getProperty("os.name").contains("Windows")){
                processBuilder = new ProcessBuilder("cmd", "/c", "cls").inheritIO();

            }
            else processBuilder = new ProcessBuilder("clear").inheritIO();
            processBuilder.start();
        }
        catch(IOException exc){
            exc.printStackTrace();
        }
    }


    /**
     * Establish connection with the server
     *
     * @param hostName IP of the server
     * @param portNumber port of the Eriantys server
     * @return connectionSocket
     */
    private ConnectionSocket createConnectionWithServer(String hostName, int portNumber){
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
    private void sendNickname(){
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
    private void sendGameMode(){
        boolean confirmation = false;
        String mode;
        Boolean isExpert = null;
        while (!confirmation) {
            System.out.println(">Insert the game mode [E/D]: ");
            mode = in.nextLine();
            if(mode.equalsIgnoreCase("E")){
                System.out.println("You have chosen the expert mode");
                isExpert =  true;
            } else if (mode.equalsIgnoreCase("D")){
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
    private void sendNumOfPlayers(){
        int numOfPlayers = 0;
        while (numOfPlayers != 2 && numOfPlayers != 3){
            System.out.println(">Insert the number of players [2/3]: ");
            try {
                numOfPlayers = in.nextInt();
                System.out.println(numOfPlayers);
            } catch (InputMismatchException e){
                System.out.println("Please, insert a number.");
            }
        }
        System.out.println("You have chosen " + numOfPlayers+ " players game mode");
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
    private void showMenu(){
        if(!viewState.isActiveView()){
            System.out.println(CLICyan+" NOT YOUR TURN - WAIT UNTIL IS YOUR TURN");
        }
        else
        {
            if(viewState.getCurrentPhase()== PhaseEnum.PLANNING){
                System.out.println(CLICyan+" CHOICE AN ASSISTANT CARD TO PLAY, SELECT  BETWEEN THE USABLE CARDS");
            }
            else if(viewState.getCurrentPhase()== PhaseEnum.ACTION_MOVE_STUDENTS){
                System.out.println(CLICyan+" CHOICE A STUDENT TO MOVE (R,Y,B,P,G color) AND THEN CHOICE THE DESTINATION (D :your diningRoom, I: island)");
            }
        }
    }
}
