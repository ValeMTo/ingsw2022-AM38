package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.messages.ErrorTypeEnum;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.messages.MessageTypeEnum;
import it.polimi.ingsw.messages.SetTypeEnum;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server {

    private static final Gson gson = new Gson();
    private static int port;
    private static Scanner inputReader;
    private static PrintWriter writer;
    private static int numOfPlayers = 0;

    public static int getPort(String[] args) {
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        } else {
            JsonParser parser = new JsonParser();
            try {
                JsonObject json = new Gson().fromJson(new FileReader("src/main/resources/json/ConnectionConfiguration.json"), JsonObject.class);
                if (json != null) {
                    port = json.get("port").getAsInt();
                    System.out.println("Error reading configuration");
                } else
                    port = 1234;
                System.out.println("Get from config file port " + port);
            } catch (Exception exc) {
                exc.printStackTrace();
                port = 1234;
            }
        }
        return port;
    }

    public static void main(String[] args) {

        boolean error = false;

        ServerSocket serverSocket = createServerSocket(args);

        Socket clientSocket = establishConnection(serverSocket);
        List<String> players = new ArrayList<String>();
        String player = null;
        inputReader = createScanner(clientSocket);
        writer = createWriter(clientSocket);

        String jsonInput = null;
        do {
            error = addPlayer(players);
        } while (error);

        do {
            try {
                error = !choosePlayerNum();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } while (error);
    }

    private static ServerSocket createServerSocket(String[] args) {
        port = getPort(args);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server created at port " + port);
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return serverSocket;
    }

    public static boolean addPlayer(List<String> players) {
        boolean error = true;
        System.out.println("Waiting for a message ");
        String jsonInput = inputReader.nextLine();
        System.out.println("I got a message " + jsonInput);
        JsonObject json = new Gson().fromJson(jsonInput, JsonObject.class);
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal() && json.get("SetType").getAsInt() == SetTypeEnum.SET_NICKNAME.ordinal()) {
            String player = json.get("nickname").getAsString();
            if (players.contains(player)) {
                error = true;
                writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.NICKNAME_ALREADY_TAKEN, "The nickname has been already taken"));
            } else {
                error = false;
                players.add(player);
                System.out.println("Player name" + player + "has been set");
                writer.println(MessageGenerator.okGenerate());
                writer.flush();
                System.out.println("Sending message " + MessageGenerator.okGenerate());
            }
        } else {
            writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
            error = true;
            writer.flush();
            System.out.println("Sending message " + MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
        }
        return !error;
    }

    public static boolean choosePlayerNum() throws Exception {
        boolean ok = false;
        int numOfPlayers = 0;
        System.out.println("Waiting for a message ");
        String jsonInput = inputReader.nextLine();
        System.out.println("I got a message " + jsonInput);
        JsonObject json = new Gson().fromJson(jsonInput, JsonObject.class);
        if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal() && json.get("SetType").getAsInt() == SetTypeEnum.SELECT_NUMBER_OF_PLAYERS.ordinal()) {
            numOfPlayers = json.get("numOfPlayers").getAsInt();
            if ((numOfPlayers == 2 || numOfPlayers == 3) && Server.numOfPlayers == 0) {
                ok = true;
                Server.numOfPlayers = numOfPlayers;
                writer.print(MessageGenerator.okGenerate());
            } else {
                ok = false;
                writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.INVALID_INPUT, "The input is invalid or out of bound 2 or 3 players allowed"));
            }
        } else {
            writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
        }
        writer.flush();
        return ok;
    }

    private static Socket establishConnection(ServerSocket serverSocket) {
        Socket clientSocket = null;
        System.out.println("Server accepting connection...");
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Connection accepted from IP: " + clientSocket.getInetAddress() + " port: " + clientSocket.getPort());
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return clientSocket;
    }

    private static Scanner createScanner(Socket socket) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
        }
        return scanner;
    }

    private static PrintWriter createWriter(Socket socket) {
        PrintWriter scanner = null;
        try {
            scanner = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
        }
        return scanner;
    }


}
