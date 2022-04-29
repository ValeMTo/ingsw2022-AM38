package it.polimi.ingsw;

public class Herbalist extends SpecialCard{
    private int noEntryTiles;
    private int noEntryTilesLimit;

    public Herbalist(){
        super(SpecialCardName.HERBALIST);
        this.noEntryTiles = 4;
        this.noEntryTilesLimit = 4;
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
     * Decreases of one the number of tails available.
     * Returns false if the action is not possible, otherwise true.
     *
     * @return the outcome of the action
     */
    public boolean removeTile(){
        if (noEntryTiles > 0){
            noEntryTiles -= 1;
            return true;
        }
        return false;
    }

    /**
     * Increases of one the number of tails available.
     * Returns false if the action is not possible, otherwise true.
     *
     * @return the outcome of the action
     */
    public boolean returnTile(){
        if (noEntryTiles < noEntryTilesLimit){
            noEntryTiles += 1;
            return true;
        }
        return false;
    }

    /**
     * Returns true if there are no tails available, otherwise false.
     *
     * @return true if there are no tails available, otherwise false
     */
    public boolean isEmptyOfTiles(){
        if(noEntryTiles == 0){
            return true;
        }
        return false;
    }


}
