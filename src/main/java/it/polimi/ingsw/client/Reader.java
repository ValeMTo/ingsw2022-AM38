package it.polimi.ingsw.client;

import java.util.Scanner;

/**
 * This class is the continuous channel to read jsonObject from Scanner given
 */
public class Reader implements Runnable {
    private Scanner inputReader;

    public Reader(Scanner inputReader){
        this.inputReader = inputReader;
    }

    @Override
    public void run(){
        while(true)
        {
            transferMessage(inputReader.nextLine());
        }
    }

    private String transferMessage(String message){
        return message;
    }
}

