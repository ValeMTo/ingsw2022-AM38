package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.messages.SimpleTextMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionSocket {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private String serverIP;
    private int serverPort;
    private Scanner stdin;
    private Gson gson;

    public ConnectionSocket(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.stdin = new Scanner(System.in);
        this.gson = new Gson();

    }


    /**
     * Method setup initializes a new socket connection and handles the nickname-choice response.
     *
     * @param nickname      of type String - the username chosen by the user.
     */
    //@throws NicknameAlreadyTakenException when the nickname is already in use.
    public boolean setup(String nickname){
        String jsonFromServer = null;
        System.out.println("Trying to connect with the socket...");
        System.out.println("Opening a socket server communication on port " + serverPort);

        Socket socket = establishConnection(serverIP, serverPort);

        Scanner socketIn = createScanner(socket);
        PrintWriter socketOut = createWriter(socket);

        socketOut.print(MessageGenerator.nickNameMessageGenerate(nickname));
        socketOut.flush();

        jsonFromServer = socketIn.nextLine();

        //TODO: leggere la risposta con gson

        return false;
    }

    private Socket establishConnection(String serverIP, int serverPort){
        Socket socket = null;
        try {
            socket = new Socket(serverIP, serverPort);
        } catch (IOException e) {
            System.out.println("ERROR - Connection NOT established");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        System.out.println("Connection established");
        return socket;
    }

    private Scanner createScanner(Socket socket){
        Scanner scanner=null;
        try {
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }

    private PrintWriter createWriter(Socket socket){
        PrintWriter scanner=null;
        try {
            scanner = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }
}