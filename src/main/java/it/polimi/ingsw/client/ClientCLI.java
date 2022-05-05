package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.exceptions.GameModeAlreadySetException;
import it.polimi.ingsw.exceptions.NumberOfPlayersAlreadySetException;

import java.io.PrintStream;
import java.util.Scanner;

public class ClientCLI {
    private static String hostName;
    private static int portNumber;
    private final Scanner in;
    private final PrintStream out;
    private final boolean isRunning;
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

    }

    public void login() {
        String nickname = null;
        boolean confirmation = false;
        boolean error = false;
        int numOfPlayers = 0;
        boolean isExpert = false;
        connectionSocket = new ConnectionSocket(hostName, portNumber);
        try {
            if (!connectionSocket.setup()) {
                System.err.println("ERROR - The entered IP/port doesn't match any active server or the server is not running.");
                System.err.println("Please try again!");
            }
            System.out.println("Socket Connection setup completed!");
        } catch (FunctionNotImplementedException e) {

        }
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
            login();
        } else System.out.println("Nickname set up correctly");
        confirmation = false;
        boolean ok = false;
        do {
            confirmation = false;
            numOfPlayers = 0;
            while (!confirmation) {
                System.out.println(">Choose the number of player of your game (2 or 3): ");
                numOfPlayers = in.nextInt();
                in.nextLine();
                System.out.println(">You choose " + numOfPlayers + " players is it correct? [Y/N]: ");
                if (in.nextLine().equalsIgnoreCase("Y")) {
                    confirmation = true;
                    try {
                        ok = connectionSocket.sendNumberOfPlayers(numOfPlayers);
                    } catch (NumberOfPlayersAlreadySetException exc) {
                        System.out.println("Number of player already set do you want to continue (Y) or disconnect (N)?");
                        String input = in.nextLine();
                        if (input.equalsIgnoreCase("Y")) {
                            ok = true;
                            connectionSocket.sendOk();
                        } else {
                            connectionSocket.disconnect();
                            System.exit(1);
                        }
                    }
                } else numOfPlayers = 0;
            }
        } while (!ok);

        confirmation = false;
        ok = false;
        do {
            confirmation = false;
            while (!confirmation) {
                System.out.println(">Choose the gameMode Simple or Expert (S or E): ");
                String input = in.nextLine();
                System.out.println(">You choose " + input + " as gameMode is it correct? [Y/N]: ");
                if (input.equalsIgnoreCase("S")) {
                    confirmation = true;
                    isExpert = false;
                } else if (input.equalsIgnoreCase("E")) {
                    confirmation = true;
                    isExpert = true;
                } else {
                    System.out.println("Error in the input");
                    confirmation = false;
                }
            }
            try {
                ok = connectionSocket.setGameMode(isExpert);
            } catch (GameModeAlreadySetException exc) {
                System.out.println("GameMode already set do you want to continue (Y) or disconnect (N)?");
                String input = in.nextLine();
                if (input.equalsIgnoreCase("Y")) {
                    ok = true;
                    connectionSocket.sendOk();
                } else {
                    connectionSocket.disconnect();
                    System.exit(1);
                }
            }

        } while (!ok);
        System.out.println("Enter something to exit");
        in.nextLine();


    }

    public boolean isRunning() {
        return isRunning;
    }


}
