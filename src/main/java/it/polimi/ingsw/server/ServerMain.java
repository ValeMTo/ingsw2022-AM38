package it.polimi.ingsw.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
public class ServerMain {

    private static int port;
    
    public static void getPort(String[]args){
        if(args.length>1) {
            port =  Integer.parseInt(args[1]);
        }
        else
            port = 1234;
    }

    public static void main(String[]args){
        getPort(args);
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server created at port "+port);
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
            return;
        }
        Socket clientSocket = null;
        System.out.println("Server accepting connection...");
        try{
            clientSocket = serverSocket.accept();
            System.out.println("Connection accepted from IP: "+clientSocket.getInetAddress()+" port: "+clientSocket.getPort());
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        BufferedReader input = null;
        try{
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }

        String s = "";
        try {
            while ((s = input.readLine()) != null) {
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
