package it.polimi.ingsw.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * This class is the continuous channel to read jsonObject from Scanner given
 */
public class Reader implements Runnable {
    private BufferedReader inputReader;
    private ViewMessageParser viewHandler;

    public Reader(BufferedReader inputReader, ViewMessageParser viewHandler){
        this.inputReader = inputReader;
        this.viewHandler = viewHandler;
    }

    @Override
    public void run(){
        String input;
        while(true)
        {
            try {
                //System.out.println("READER - waiting for message");
                input = inputReader.readLine();
                //System.out.println("READER - got message "+input);
                viewHandler.parse(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

