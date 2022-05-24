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
        while(true)
        {
            try {
                viewHandler.parse(inputReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

