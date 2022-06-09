package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.ConnectionSocket;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginMenuController extends GUIController {

    private final String CONNECTION_ERROR = "ERROR - The entered IP/port doesn't match any active server or the server is not running.\n Please try with different address or port";
    @FXML
    private TextField nickname;
    @FXML
    private TextField address;
    @FXML
    private TextField port;
    @FXML
    private Label messageBox;
    @FXML
    private AnchorPane ipAndPort;
    @FXML
    private AnchorPane nicknameBox;
    @FXML
    private Button enterButton;

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
        getMessageBox().setText("");
        getMessageBox().setText("trying connecting to Server ...");
        gui.setConnectionSocket(createConnectionWithServer(address.getText(), Integer.parseInt(port.getText()), gui.getViewState()));
        if (gui.getConnectionSocket() != null) {
            enterButton.setVisible(false);
            nicknameBox.setVisible(true);
            ipAndPort.setVisible(false);
        }


    }

    public void confirmNickname(){
        getMessageBox().setText("");
        getMessageBox().setText("Checking the uniqueness of the name...");
        gui.getConnectionSocket().sendNickname(nickname.getText());
        synchronized (gui) {
            try {
                gui.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (gui.getViewState().getNickname() != null){
            getMessageBox().setText("Your name is unique");
            isTheFirst();
        } else {
            getMessageBox().setText("Your name has been already chosen.\nInsert another one.");
        }

    }

    private void isTheFirst(){
        gui.getConnectionSocket().isTheFirst();
        synchronized (gui) {
            try {
                gui.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (gui.getViewState().getGameSettings().getActualClients() <= 0) {
            gui.setNextStage("setupMenu.fxml");
        }else{
            gui.setNextStage("acceptConditionsMenu.fxml");
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
        gui.getViewState().setAwaitingGUI(gui);
        return connectionSocket;
    }

}


