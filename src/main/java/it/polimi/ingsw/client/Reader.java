package it.polimi.ingsw.client;

import it.polimi.ingsw.mvc.Listenable;

import java.util.Scanner;

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

