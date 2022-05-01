package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.FunctionNotImplementedException;

import java.io.*;
import java.util.Scanner;

public class ClientCLI {
    private String hostName;
    private int portNumber;
    private final Scanner in;
    private final PrintStream out;
    private boolean isRunning;
    ConnectionSocket connectionSocket;

    public ClientCLI(){

        this.in = new Scanner(System.in);
        this.out = new PrintStream(System.out);
        this.isRunning = true;

    }

    public static void main(String args[]){

        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert the server IP address:");
        String ip = scanner.nextLine();
        System.out.println("Insert the server port");
        int port = scanner.nextInt();
        ClientCLI cli = new ClientCLI();
        cli.login();

    }

    public void login(){
        String nickname = null;
        boolean confirmation = false;
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
        connectionSocket = new ConnectionSocket(hostName, portNumber);
        try {
            if(!connectionSocket.setup(nickname)) {
                System.err.println("ERROR - The entered IP/port doesn't match any active server or the server is not running.");
                System.err.println("Please try again!");
            }
            System.out.println("Socket Connection setup completed!");
        } catch (FunctionNotImplementedException e) {
            System.err.println("ERROR - You have chosen a nickname that has been already taken.");
            login();
        }

    }

    public boolean isRunning(){
        return isRunning;
    }




}
