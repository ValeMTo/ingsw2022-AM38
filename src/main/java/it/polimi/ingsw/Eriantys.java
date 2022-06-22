package it.polimi.ingsw;

import it.polimi.ingsw.client.CLI.ClientCLI;
import it.polimi.ingsw.client.gui.MainGUI;
import it.polimi.ingsw.server.Server;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Eriantys {

    public static void main(String[] args) {

        System.out.println("Welcome in Eriantys!");
        System.out.println("Options:");
        System.out.println("0 Server");
        System.out.println("1 Client CLI");
        System.out.println("2 Client GUI");
        System.out.println("Which do you want to run?");
        Scanner scanner = new Scanner(System.in);

        int actionCode=-1;
        do {
            try {
                actionCode = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Digit only numbers");
                System.exit(-1);
            }

            switch (actionCode){
                case 0 -> Server.main(null);
                case 1 -> ClientCLI.main(null);
                case 2 -> MainGUI.main(null);
                //case 2-> ClientGUI.main(null);
                default -> System.err.println("Only 0/1/2 numbers admitted");
            }
        } while (actionCode != 0 && actionCode != 1 && actionCode != 2);

    }

}
