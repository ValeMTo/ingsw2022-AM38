package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.exceptions.NicknameAlreadyTakenException;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static int port;
    private static Lobby lobby;
    private static ExecutorService executorService;
    private static int numClientConnected;
    private static Map<String, ClientHandler> allPlayers;
    protected static ArrayList<ClientHandler> allClients = new ArrayList<>();


    public static int getPort(String[] args) {
        if (args != null && args.length > 1) {
            port = Integer.parseInt(args[1]);
        } else {
           port = 12345;
                System.out.println("Port is "+port);
            }
        return port;
    }

    public static void main(String[] args) {
        numClientConnected = 0;
        allPlayers = new HashMap<>();
        ServerSocket serverSocket = createServerSocket(args);
        executorService = Executors.newCachedThreadPool();
        lobby = new Lobby();


        while (true) {
            ClientHandler client = new ClientHandler(establishConnection(serverSocket));
            allClients.add(client);
            executorService.submit(client);
            numClientConnected += 1;
        }
    }

    /**
     * Creates the ServerSocket asking the port number to the user. Also terminates the app if the user uses END as input
     * @return the originated server socket
     */
    private static ServerSocket createServerSocketFromInput(){
        boolean socketActive = false;
        ServerSocket socket = null;
        String input;
        Scanner scanner = new Scanner(System.in);
        while(!socketActive){
            System.out.println("Enter the port for the Server Socket or digit END to terminate the server");
            input = scanner.nextLine();
            try{
                if(input.equalsIgnoreCase("END"))
                    System.exit(0);
                int port = Integer.parseInt(input);
                socket = new ServerSocket(port);
                socketActive = true;
            }
            catch (IOException exc){
                System.out.println("Server socket error, try with another port or end with END command");
            }
            catch (NumberFormatException exc){
                System.out.println("Only number or END input are allowed.");
            }
        }
        return socket;
    }
    private static ServerSocket createServerSocket(String[] args) {
        port = getPort(args);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server created at port " + port);
            return serverSocket;
        } catch (IOException exc) {
            System.out.println("Server port " + port + " is unable, problem creating the socket, port occupied, shutting down.");
            return createServerSocketFromInput();
        }
    }

    public static boolean blockPlayerName(String nickname) throws NicknameAlreadyTakenException {
        System.out.println("blockPlayerName before sync");
        synchronized (allPlayers) {
            System.out.println("SERVER ADD PLAYER - enter synchronized part");
            if (allPlayers.keySet().contains(nickname)) throw new NicknameAlreadyTakenException();
            allPlayers.put(nickname, null);
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

    public static List<String> getLobbyOfActivePlayers() {
        return lobby.getActiveUsers();
    }

    public static void setLobbySettings(boolean gamemode) {
        synchronized (lobby) {
            lobby.setIsExpert(gamemode);
        }
    }

    public static void setLobbySettings(int numPlayers) {
        synchronized (lobby) {
            lobby.setNumOfPlayers(numPlayers);
        }
    }

    public static void addPlayerInLobby(ClientHandler client) {
        System.out.println("SERVER: ADD PLAYER IN LOBBY");
        synchronized (lobby) {
            lobby.addPlayer(client);
        }
    }

    public static int getNumOfPlayerGame() {
        synchronized (lobby) {
            return lobby.getNumOfPlayers();
        }
    }

    public static boolean getGamemode() {
        synchronized (lobby) {
            return lobby.getGamemode();
        }
    }


    /**
     * Remove a player that has been disconnected from the list of players and from the lobby
     * @param name player to remove
     */
    public static void removePlayer(String name){
        synchronized (lobby) {
            lobby.removePlayer(name);
        }
        System.out.println("SERVER - removePlayer "+name+" from the allPlayersList");
        synchronized (allPlayers) {
            allPlayers.remove(name);
        }
    }
}
