package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.gui.MainGUI;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class loginMenuController extends GUIController {

    private final String CONNECTION_ERROR = "ERROR - The entered IP/port doesn't match any active server or the server is not running.\n Please try with different address or port";
    //private MainGUI gui;
    @FXML
    private TextField nickname;
    @FXML
    private TextField address;
    @FXML
    private TextField port;
    @FXML
    private Label messageBox;
    @FXML private AnchorPane ipAndPort;

    @FXML private AnchorPane nicknameBox ;
    @FXML private Button enterButton;

    private Label getMessageBox() {
        return this.messageBox;
    }


    @FXML
    public void initialize() {
        ipAndPort.setVisible(true);
        nicknameBox.setVisible(false);
    }

    /**
     * Firstly the login controller tries to establish a connection with the server by creating a connectionSocket in the MainGUI class.
     * If the connection is established, the controller then asks to set a nickname.
     * Then the controller checks the viewState the nickname is valid and not already taken, then
     */
    public void startLogin() {

        // controlli sul formato dei text inseriti
        // ....
        messageBox.setText("");
        messageBox.setText("trying connecting to Server ...");
        gui.setConnectionSocket(createConnectionWithServer(address.getText(), Integer.parseInt(port.getText()), gui.getViewState()));
        if (gui.getConnectionSocket() != null) {
            enterButton.setVisible(false);
            nicknameBox.setVisible(true);
           // if(!setNickname());


        }



    }


    private ConnectionSocket createConnectionWithServer(String hostName, int portNumber, ViewState viewState) {
        ConnectionSocket connectionSocket = new ConnectionSocket(hostName, portNumber, viewState);
        try {
            if (!connectionSocket.setup()) {
                getMessageBox().setText("Connection error");
                showErrorAlert("Connection Error", CONNECTION_ERROR);
                connectionSocket = null;

            } else if (connectionSocket.setup()) {
                getMessageBox().setText("Socket Connection setup completed!");
                getMessageBox().setText("Connected to Server !");
            }
        } catch (FunctionNotImplementedException e) {
            e.printStackTrace();
        }
        return connectionSocket;
    }


    public boolean setNickname() {
        gui.getConnectionSocket().sendNickname(nickname.getText());
        return true;   // just a test
    }



}

