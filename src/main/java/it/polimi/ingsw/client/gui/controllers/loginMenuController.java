package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.gui.MainGUI;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Optional;

public class loginMenuController implements GUIController  {

    private MainGUI gui;

    @FXML private TextField nickname;
    @FXML private TextField address;
    @FXML private TextField port;
    @FXML private Label messageBox;

    /** Method startPlay changes the stage scene to the login scene when the button "Play" is pressed.
     *
     **/
    @Override
    public void initGUI(MainGUI gui) {
        this.gui = gui;
    }


    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit game");
        alert.setHeaderText("Are you sure you want to exit the game ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Thanks for playing with Eriantys. Bye!");
            System.exit(0);
        }
    }


    public void startConnection() {
        // controlli sul formato dei text inseriti
        try {
            messageBox.setText("trying connection");
            ConnectionSocket connectionSocket = new ConnectionSocket()
        }
        catch(FunctionNotImplementedException exc) {
            exc.printStackTrace();
        }
    }



}
