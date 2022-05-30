package it.polimi.ingsw.controller;

import it.polimi.ingsw.server.ClientHandler;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyTest {
    Lobby lobby =  new Lobby();

    /**
     * Test getNumOfActiveUsers(
     * If the lobby is empty the number of active players is zero
     */
    @Test
    public void getNumberOfActiveUsers(){
        assertEquals(0, lobby.getNumOfActiveUsers());

    }

    /**
     * Test set method setIsExpert
     */
    @Test
    public void setIsExpertTest(){
        lobby.setIsExpert(true);
        assertTrue(lobby.getGamemode());
        lobby.setIsExpert(false);
        assertFalse(lobby.getGamemode());
    }

    /**
     * Test set method setNumOfPlayers
     */
    @Test
    public void setNumOfPlayersTest(){
        lobby.setNumOfPlayers(3);
        assertEquals(3, lobby.getNumOfPlayers());
        lobby.setNumOfPlayers(2);
        assertEquals(2, lobby.getNumOfPlayers());
    }

    /**
     * Test addPlayer method
     */
    @Test
    public void addPlayerTest(){
        lobby.setNumOfPlayers(2);

        Socket clientSocket = new Socket();
        ClientHandler client = new ClientHandler(clientSocket);
        client.setNickname("Vale");

        Socket clientSocket2 = new Socket();
        ClientHandler client2 = new ClientHandler(clientSocket2);
        client2.setNickname("Nick");

        lobby.addPlayer(client);
        lobby.addPlayer(client2);


    }



}
