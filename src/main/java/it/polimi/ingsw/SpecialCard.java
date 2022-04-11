package it.polimi.ingsw;

public class SpecialCard {
    private final SpecialCardName name;
    private int cost;
    private boolean firstUse;

    public SpecialCard(SpecialCardName name) {
        this.name = name;
        this.firstUse = false;
        if (name == SpecialCardName.PRIEST || name == SpecialCardName.POSTMAN ||
                name == SpecialCardName.JUGGLER || name == SpecialCardName.BARD) {
            this.cost = 1;
        } else if (name == SpecialCardName.HERBALIST || name == SpecialCardName.KNIGHT
                || name == SpecialCardName.PRINCESS || name == SpecialCardName.CHEESEMAKER) {
            this.cost = 2;
        } else {
            this.cost = 3;
        }

    }

    /**
     * Increase the cost of the card of one.
     * It can be used only once: the price will not be increased with two calls of the method.
     */
    public void increasePrice(){
        if (firstUse) {
            this.cost += 1;
            firstUse = true;
        }
    }

    /**
     * Returns the cost of the card
     * @return an int which is the cost of the card
     */
    public int getCostCoin(){
        return this.cost;
    }

    /**
     * Remove a tail when the card is Herbalist, otherwise is useless.
     */
    public boolean removeTail{
        throw new NoSuchMethodError();
        return true;
    }




}
