package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;

import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static int port;
    private static Lobby lobby;
    private static ExecutorService executorService;
    private static int numClientConnected;
    private static List<String> allPlayers;
    private List<Game> games;

    public static int getPort(String[] args) {
        if (args != null && args.length > 1) {
            port = Integer.parseInt(args[1]);
        } else {
            try {
                JsonObject json = new Gson().fromJson(new FileReader("src/main/resources/json/ConnectionConfiguration.json"), JsonObject.class);
                if (json != null) {
                    port = json.get("port").getAsInt();
                    System.out.println("Error reading configuration");
                } else port = 1234;
                System.out.println("Get from config file port " + port);
            } catch (Exception exc) {
                exc.printStackTrace();
                port = 1234;
            }
        }
        return port;
    }

    public static void main(String[] args) {
        numClientConnected = 0;
        allPlayers = new ArrayList<>();
        ServerSocket serverSocket = createServerSocket(args);
        executorService = Executors.newCachedThreadPool();
        lobby = new Lobby();

        while (true) {
            executorService.submit(new ClientHandler(establishConnection(serverSocket)));
            numClientConnected += 1;
        }
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

    public static boolean blockPlayerName(String nickname) throws NicknameAlreadyTakenException {
        synchronized (allPlayers) {
            System.out.println("SERVER ADD PLAYER - enter synchronized part");
            if (allPlayers.contains(nickname)) throw new NicknameAlreadyTakenException();
            allPlayers.add(nickname);
            System.out.println("SERVER ADD PLAYER: " + nickname);
            return true;
        }
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

    public static int getLobbyNumberOfActivePlayers() {
        return lobby.getNumOfActiveUsers();
    }

    public static void setLobbySettings(boolean gamemode, int numPlayers) {
        synchronized (lobby) {
            lobby.setNumOfPlayers(numPlayers);
            lobby.setIsExpert(gamemode);
        }
    }

    public static void addPlayerInLobby(ClientHandler client){
        synchronized (lobby){
            lobby.addPlayer(client);
        }
    }
}
