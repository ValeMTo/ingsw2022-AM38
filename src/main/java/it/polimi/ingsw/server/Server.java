package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.messages.SimpleTextMessage;

import java.io.*;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static int port;
    private static Gson gson = new Gson();

    public static int getPort(String[] args) {
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        } else {
            JsonParser parser = new JsonParser();
            try {
                JsonObject json = (JsonObject) parser.parse(new FileReader("src/main/resources/json/ConnectionConfiguration.json"));
                port = json.get("port").getAsInt();
                System.out.println("Get from config file port " + port);
            } catch (Exception exc) {
                exc.printStackTrace();
                port = 2345;
            }
        }
        return port;
    }

    public static void main(String[] args) {

        ServerSocket serverSocket = createServerSocket(args);
        Socket clientSocket = establishConnection(serverSocket);

        Scanner inputReader = createScanner(clientSocket);
        PrintWriter writer = createWriter(clientSocket);

        SimpleTextMessage playerName = gson.fromJson(inputReader.nextLine(), SimpleTextMessage.class);
        System.out.println(playerName.getMessage());

        //TODO: Printare la risposta

    }

    private static ServerSocket createServerSocket(String[] args){
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

    private static Socket establishConnection(ServerSocket serverSocket){
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

    private static Scanner createScanner(Socket socket){
        Scanner scanner=null;
        try {
            scanner = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
        }
        return scanner;
    }

    private static PrintWriter createWriter(Socket socket){
        PrintWriter scanner=null;
        try {
            scanner = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Error in socketIn scanner");
        }
        return scanner;
    }


}
