package it.polimi.ingsw.model.specialCards;

import it.polimi.ingsw.controller.mvc.Listenable;
import it.polimi.ingsw.controller.mvc.Listener;
import it.polimi.ingsw.exceptions.FunctionNotImplementedException;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;
import it.polimi.ingsw.model.player.PlayerBoard;
import it.polimi.ingsw.server.ClientHandler;

import java.util.ArrayList;
import java.util.List;

public class SpecialCard extends Listenable {
    protected final SpecialCardName name;
    protected int cost;
    protected boolean firstUse;
    protected Listener modelListener = null;
    protected List<ClientHandler> clients = new ArrayList<>();

    public SpecialCard(SpecialCardName name) {
        this.name = name;
        this.firstUse = false;
        if (name == SpecialCardName.PRIEST || name == SpecialCardName.POSTMAN || name == SpecialCardName.JUGGLER || name == SpecialCardName.BARD) {
            this.cost = 1;
        } else if (name == SpecialCardName.HERBALIST || name == SpecialCardName.KNIGHT || name == SpecialCardName.PRINCESS || name == SpecialCardName.CHEESEMAKER) {
            this.cost = 2;
        } else {
            this.cost = 3;
        }
    }

    /**
     * Sets the listener and clients for the update and notify for changes
     * @param modelListener : the modelListener
     * @param clients : the clients to notify
     */
    public void setListenerAndClients(Listener modelListener, List<ClientHandler> clients){
        System.out.println("SPECIAL CARD "+this.name+" I HAVE NOW SET THE LISTENERS");
        this.modelListener = modelListener;
        this.clients = new ArrayList<>();
        if(clients!=null)
            this.clients.addAll(clients);
    }

    /**
     * Getter of the card name
     *
     * @return : card name
     */
    public SpecialCardName getName() {
        return this.name;
    }


    /**
     * Increase the cost of the card of one.
     * It can be used only once: the price will not be increased with two calls of the method.
     */
    public void use() {
        if (!firstUse) {
            this.cost += 1;
            firstUse = true;
        }
        if(modelListener!=null&&clients!=null){
            notify(modelListener,MessageGenerator.specialCardUpdateMessage(this.name, this.cost),clients);
        }
    }

    /**
     * Returns the cost of the card
     *
     * @return an int which is the cost of the card
     */
    public int getCostCoin() {
        return this.cost;
    }

    /**
     * Remove a Tile when the card is Herbalist, otherwise is useless.
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public boolean removeTile() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Adds a Tile when the card is Herbalist, otherwise is useless.
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public boolean returnTile() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Check if there are Tiles when the card is Herbalist, otherwise is useless.
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public boolean isEmptyOfTiles() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Returns the number of tiles available.
     *
     * @return the number of tiles
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public int getNumberOfEntryTiles() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Adds a student in the card room if it is a card with students, otherwise the method is useless.
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public boolean addStudent(Color studentColor) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Removes a student from the card room if it is a card with students, otherwise the method is useless.
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public boolean removeStudent(Color studentColor) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Count the number of students of a specific color if it is a card with students, otherwise the method is useless.
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public int countStudents(Color studentColor) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Count the number of all students on the card if it is a card with students, otherwise the method is useless.
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public int countStudents() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());

    }

    /**
     * Getter guestLimit of the card if it is a card with students, otherwise the method is useless.
     *
     * @return guestLimit
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public int getGuestsLimit() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Getter guestChangeLimit of the card if it is a card with students, otherwise the method is useless.
     *
     * @return guestChangeLimit
     *
     * @throws FunctionNotImplementedException method not implemented, this method cannot be used by any specialCard
     */
    public int getGuestsChangeLimit() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }


}
