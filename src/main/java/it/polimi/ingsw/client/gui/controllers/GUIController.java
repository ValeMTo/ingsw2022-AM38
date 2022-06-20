package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.MainGUI;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

abstract public class GUIController {

    protected MainGUI gui;
    private ViewState viewState;

    /**
     * Method of the gui controller that allows for each gui controller to keep a reference to a running GUI, hence
     * to make changes to it.
     *
     * @param gui : the gui that is assigned to a scene controller, so that it can "change it"
     */

    public void setGuiToController(MainGUI gui) {
        this.gui = gui;
    }

    public void showErrorAlert(String errorTitle, String errorString) {
        Alert errorWindow = new Alert(Alert.AlertType.ERROR);
        errorWindow.setTitle("Error");
        errorWindow.setHeaderText(errorTitle);
        errorWindow.setContentText(errorString);
        errorWindow.showAndWait();
    }

    public Alert showInfoAlert(String infoTitle, String infoString) {
        Alert infoWindow = new Alert(Alert.AlertType.INFORMATION);
        infoWindow.setTitle("Info");
        infoWindow.setHeaderText(infoTitle);
        infoWindow.setContentText(infoString);
        infoWindow.showAndWait();
        return infoWindow;
    }


    /**
     * Method to exit from Eriantys gui
     */
    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit game");
        alert.setHeaderText("Are you sure you want to exit the game ?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Thanks for playing with Eriantys. Bye!");
            if(gui.getConnectionSocket() != null){
                gui.getConnectionSocket().disconnect();
            }
            System.exit(0);
        }
    }

    public void setViewstate(ViewState viewState){
        this.viewState = viewState;
    }

    public void showSettings() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException();
    };

    /**
     * method used by each GUI controller to refresh its parameters. The controller gets each parameter from the gui's
     * viewState and then sets its local JavaFX objects accordingly.
     */
    // public abstract void refreshSceneByController();


}
