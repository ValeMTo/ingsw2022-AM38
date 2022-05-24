package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.MainGUI;
import javafx.application.Application;
import javafx.stage.Stage;

public class loginMenuController implements GUIController  {

    private MainGUI gui;



    /** Method startPlay changes the stage scene to the login scene when the button "Play" is pressed.
     *
     **/
    @Override
    public void initGUI(MainGUI gui) {
        this.gui = gui;
    }




}
