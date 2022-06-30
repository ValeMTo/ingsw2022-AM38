package it.polimi.ingsw.model.specialCards;

import it.polimi.ingsw.mvc.Listener;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.server.ClientHandler;
import org.json.JSONObject;

import java.util.List;

public class Herbalist extends SpecialCard{
    private int noEntryTiles;
    private int noEntryTilesLimit;

    public Herbalist(){
        super(SpecialCardName.HERBALIST);
        this.noEntryTiles = 4;
        this.noEntryTilesLimit = 4;
    }

    /**
     * Notify that the tiles have changed
     */
    private void notifyTilesChange(){
        if(modelListener!=null&&clients!=null) {
            notify(modelListener, MessageGenerator.specialCardUpdateMessage(this.name,this.cost,this.noEntryTiles),clients);
        }
    }

    @Override
    public void setListenerAndClients(Listener modelListener, List<ClientHandler> clients) {
        super.setListenerAndClients(modelListener, clients);
        notifyTilesChange();
    }

        /**
         * Returns the number of tiles available.
         *
         * @return the number of tiles
         */
    public int getNumberOfEntryTiles(){
        return noEntryTiles;
    }

    /**
     * Decreases of one the number of tiles available.
     * Returns false if the action is not possible, otherwise true.
     *
     * @return the outcome of the action
     */
    public boolean removeTile(){
        if (noEntryTiles > 0){
            noEntryTiles -= 1;
            notifyTilesChange();
            return true;
        }
        return false;
    }

    /**
     * Increases of one the number of tiles available.
     * Returns false if the action is not possible, otherwise true.
     *
     * @return the outcome of the action
     */
    public boolean returnTile(){
        if (noEntryTiles < noEntryTilesLimit){
            noEntryTiles += 1;
            notifyTilesChange();
            return true;
        }
        return false;
    }

    /**
     * Returns true if there are no tiles available, otherwise false.
     *
     * @return true if there are no tiles available, otherwise false
     */
    public boolean isEmptyOfTiles(){
        if(noEntryTiles == 0){
            return true;
        }
        return false;
    }

    @Override
    public JSONObject save(){
        JSONObject json = super.save();
        json.put("NoEntryTiles", noEntryTiles);
        json.put("NoEntryTilesLimit", noEntryTilesLimit);
        return json;
    }

}
