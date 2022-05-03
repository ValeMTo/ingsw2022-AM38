package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.messages.MessageTypeEnum;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionSocket {
    private static Socket socket = null;
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String serverIP;
    private final int serverPort;
    private final Scanner stdin;

    private final Gson gson;
    private Scanner socketIn;
    private PrintWriter socketOut;

    public ConnectionSocket(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.stdin = new Scanner(System.in);
        this.gson = new Gson();

    }


    /**
     * Method setup initializes a new socket connection and handles the nickname-choice response.
     */
    //@throws NicknameAlreadyTakenException when the nickname is already in use.
    public boolean setup() throws FunctionNotImplementedException {

        System.out.println("Trying to connect with the socket...");
        System.out.println("Opening a socket server communication on port " + serverPort);

        if (socket == null)
            socket = establishConnection(serverIP, serverPort);
        if (socket == null)
            return false;
        socketIn = createScanner(socket);
        socketOut = createWriter(socket);
        return true;
    }

    private JsonObject getMessage() {
        System.out.println("waiting for message");
        String jsonFromServer = socketIn.nextLine();
        System.out.println("Got message " + jsonFromServer);
        return new Gson().fromJson(jsonFromServer, JsonObject.class);
    }

    public boolean sendNickname(String nickname) {
        socketOut.print(MessageGenerator.nickNameMessageGenerate(nickname));
        socketOut.flush();
        System.out.println("Sending: " + MessageGenerator.nickNameMessageGenerate(nickname));
        String jsonFromServer = null;
        JsonObject json = getMessage();
        if (json == null)
            return false;
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }

    public boolean sendNumberOfPlayers(int numOfPlayers) {
        //TODO: use the setNumberOfPlayersGenerate() method or equivalent in MessageGenerate
        socketOut.print(MessageGenerator.okGenerate());
        socketOut.flush();
        //TODO: use the setNumberOfPlayersGenerate() method or equivalent in MessageGenerate
        System.out.println("Sending: " + MessageGenerator.okGenerate());
        String jsonFromServer = null;
        JsonObject json = getMessage();
        return json.get("MessageType").getAsInt() == MessageTypeEnum.OK.ordinal();
    }


    private Socket establishConnection(String serverIP, int serverPort) {
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

    private Scanner createScanner(Socket socket) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }

    private PrintWriter createWriter(Socket socket) {
        PrintWriter scanner = null;
        try {
            scanner = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return scanner;
    }
}