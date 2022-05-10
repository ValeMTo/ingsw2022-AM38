package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.polimi.ingsw.controller.Game;
import it.polimi.ingsw.controller.Lobby;
import it.polimi.ingsw.exceptions.GameModeAlreadySetException;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;
import it.polimi.ingsw.exceptions.NumberOfPlayersAlreadySetException;

import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Server {

    private static int port;
    private List<Game> games;
    private Lobby lobby2Easy;
    private Lobby lobby3Easy;
    private Lobby lobby2Expert;
    private Lobby lobby3Expert;

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
        ServerSocket serverSocket = createServerSocket(args);
        while (true) {
            threadPool.execute(new ClientHandler(establishConnection(serverSocket)));
        }
    }

    public static int getNumOfPlayers() {
        synchronized (blockerPlayer) {
            return Server.numOfPlayers;
        }
    }

    public static boolean isExpert() {
        synchronized (blockerGameMode) {
            return Server.isExpert;
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

    public static boolean addPlayer(String nickname) throws NicknameAlreadyTakenException {
        System.out.println("SERVER ADD PLAYER: " + nickname);
        synchronized (Server.blockerPlayer) {
            System.out.println("SERVER ADD PLAYER - enter synchronized part");
            if (players.contains(nickname)) throw new NicknameAlreadyTakenException();
            if (players.size() >= numOfPlayers && isNumOfPlayersSet) return false;
            players.add(nickname);
            return true;
        }
    }

    public static boolean setNumOfPlayers(int numOfPlayers) throws NumberOfPlayersAlreadySetException {
        synchronized (Server.blockerPlayer) {
            if (Server.numOfPlayers == 0) {
                Server.numOfPlayers = numOfPlayers;
                return true;
            }
            if (numOfPlayers < 2 || numOfPlayers > 3) return false;
        }
        if (numOfPlayers == Server.getNumOfPlayers()) return true;
        throw new NumberOfPlayersAlreadySetException();
    }

    public static int getNumOfPlayer() {
        synchronized (Server.blockerPlayer) {
            return Server.numOfPlayers;
        }
    }

    public static void setGameMode(boolean isExpert) throws GameModeAlreadySetException {
        synchronized (blockerGameMode) {
            if (!gameModeAlreadySet) {
                Server.isExpert = isExpert;
                gameModeAlreadySet = true;
                return;
            }
        }
        throw new GameModeAlreadySetException();
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
}
