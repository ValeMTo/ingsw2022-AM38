package it.polimi.ingsw.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import com.google.gson.Gson;
import java.io.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.*;
public class Server {

    private static int port;
    
    public static void getPort(String[]args){
        if(args.length>1) {
            port =  Integer.parseInt(args[1]);
        }
        else
        {
            JsonParser parser = new JsonParser();
            try {
                JsonObject json = (JsonObject) parser.parse(new FileReader("src/main/resources/json/ConnectionConfiguration.json"));
                port = json.get("port").getAsInt();
                System.out.println("Get from config file port "+port);
            }
            catch(Exception exc)
            {
                exc.printStackTrace();
                port = 2345;
            }
        }
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
