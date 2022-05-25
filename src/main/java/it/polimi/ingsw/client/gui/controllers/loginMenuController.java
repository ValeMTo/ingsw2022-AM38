package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.gui.MainGUI;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.util.Optional;

import static java.lang.Integer.valueOf;

public class loginMenuController implements GUIController  {

    private MainGUI gui;
    ConnectionSocket connectionSocket;
    @FXML private TextField nickname;
    @FXML private TextField address;
    @FXML private TextField port;
    @FXML private Label messageBox;
    @FXML private ToggleButton expertToggle;
    @FXML private ToggleButton numPlayersToggle;

    // If the user doesn't change the settings, the default ones are  Easy mode and 2 players
    private boolean isExpert = false;
    private int numOfPlayers = 2;

    private final String CONNECTION_ERROR = "ERROR - The entered IP/port doesn't match any active server or the server is not running.\n Please try with different address or port";



    @Override
    public void setGuiToController(MainGUI gui) {
        this.gui = gui;
    }

    @Override
    public void showErrorAlert(String errorTitle, String errorString) {
        Alert errorWindow = new Alert(Alert.AlertType.ERROR);
        errorWindow.setTitle("Error");
        errorWindow.setHeaderText(errorTitle);
        //errorWindow.setContentText(errorString);
        errorWindow.getDialogPane().setContent( new Label(errorString));
        errorWindow.showAndWait();
    }

    private Label getMessageBox() {
        return this.messageBox;
    }


    public void setExpertMode() {
        if(expertToggle.isSelected()) {
            expertToggle.setStyle("-fx-background-color: #46b9e7; ");
            isExpert = true;
        }
        else {
            expertToggle.setStyle("-fx-background-color: GRAY; ");
            isExpert = false;
        }

    }

    public void changeNumPlayers() {
        if(numPlayersToggle.isSelected()) {
            numPlayersToggle.setText("3");
            numOfPlayers = 3;
        }
        else {
            numPlayersToggle.setText("2");
            numOfPlayers = 2;
        }

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



    public void startLogin() {

        // controlli sul formato dei text inseriti
        // ....
        messageBox.setText("trying connecting to Server ...");
        this.connectionSocket = createConnectionWithServer(address.getText(), Integer.parseInt(port.getText()), gui.getViewState());


    }




        private ConnectionSocket createConnectionWithServer(String hostName, int portNumber, ViewState viewState) {
        ConnectionSocket connectionSocket = new ConnectionSocket(hostName, portNumber, viewState );
        try {
            if (!connectionSocket.setup()) {
                getMessageBox().setText("Connection error");
                showErrorAlert("Connection Error", CONNECTION_ERROR);

            }
            else if(connectionSocket.setup()) {
                getMessageBox().setText("Socket Connection setup completed!");
                getMessageBox().setText("Connected to Server !");
            }
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
        return connectionSocket;
    }






}
