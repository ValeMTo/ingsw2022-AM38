package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.MainGUI;

public interface GUIController {


    /**
     *  Method of the gui controller that allows for each gui controller to keep a reference to a running GUI, hence
     *  to make changes to it.
     * @param gui : the gui that is assigned to a scene controller, so that it can "change it"
     */

    public void setGuiToController(MainGUI gui);

    public void showErrorAlert(String errorTitle, String errorString);


}
