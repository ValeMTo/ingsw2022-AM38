package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;

import java.io.PrintStream;
import java.util.Scanner;

public class ClientCLI {
    private static String hostName;
    private static int portNumber;
    private final Scanner in;
    private final PrintStream out;
    ConnectionSocket connectionSocket;
    private final boolean isRunning;

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
        } else
            System.out.println("Nickname set up correctly");
    }

    public boolean isRunning() {
        return isRunning;
    }


}
