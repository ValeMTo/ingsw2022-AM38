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

    private static int port;
    private static final Gson gson = new Gson();

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
        Scanner inputReader = createScanner(clientSocket);
        PrintWriter writer = createWriter(clientSocket);

        String jsonInput = null;
        do {
            error = true;
            System.out.println("Waiting for a message ");
            jsonInput = inputReader.nextLine();
            System.out.println("I got a message " + jsonInput);
            JsonObject json = new Gson().fromJson(jsonInput, JsonObject.class);
            if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal() && json.get("SetType").getAsInt() == SetTypeEnum.SET_NICKNAME.ordinal()) {
                player = json.get("SetNickName").getAsString();
                if (players.contains(player)) {
                    error = true;
                    writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.NICKNAME_ALREADY_TAKEN, "The nickname has been already taken"));
                } else {
                    error = false;
                    players.add(player);
                }
            } else {
                writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
            }
        } while (error);
        System.out.println("Player name" + player + "has been set");
        writer.println(MessageGenerator.okGenerate());
        writer.flush();
        System.out.println("Sending message "+MessageGenerator.okGenerate());
        do {
            error = true;
            System.out.println("Waiting for a message ");
            jsonInput = inputReader.nextLine();
            System.out.println("I got a message " + jsonInput);
            JsonObject json = new Gson().fromJson(jsonInput, JsonObject.class);
            if (json.get("MessageType").getAsInt() == MessageTypeEnum.SET.ordinal() && json.get("SetType").getAsInt() == SetTypeEnum.SET_GAME_MODE.ordinal()) {
                player = json.get("SetNickName").getAsString();
                if (players.contains(player)) {
                    error = true;
                    writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.NICKNAME_ALREADY_TAKEN, "The nickname has been already taken"));
                } else {
                    error = false;
                    players.add(player);
                }
            } else {
                writer.println(MessageGenerator.errorWithStringGenerate(ErrorTypeEnum.GENERIC_ERROR, "Invalid move or bad formatted message"));
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
