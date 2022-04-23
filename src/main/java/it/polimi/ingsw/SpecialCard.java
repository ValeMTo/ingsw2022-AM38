package it.polimi.ingsw;

public class SpecialCard {
    private final SpecialCardName name;
    private int cost;
    private boolean firstUse;

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
     */
    public boolean removeTile() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Adds a Tile when the card is Herbalist, otherwise is useless.
     */
    public boolean returnTile() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Check if there are Tiles when the card is Herbalist, otherwise is useless.
     */
    public boolean isEmptyOfTiles() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Returns the number of tiles available.
     *
     * @return the number of tiles
     */
    public int getTile() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Adds a student in the card room if it is a card with students, otherwise the method is useless.
     */
    public boolean addStudent(Color studentColor) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Removes a student from the card room if it is a card with students, otherwise the method is useless.
     */
    public boolean removeStudent(Color studentColor) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Count the number of students of a specific color if it is a card with students, otherwise the method is useless.
     */
    public int countStudents(Color studentColor) throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Count the number of all students on the card if it is a card with students, otherwise the method is useless.
     */
    public int countStudents() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());

    }

    /**
     * Getter guestLimit of the card if it is a card with students, otherwise the method is useless.
     *
     * @return guestLimit
     */
    public int getGuestsLimit() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }

    /**
     * Getter guestChangeLimit of the card if it is a card with students, otherwise the method is useless.
     *
     * @return guestChangeLimit
     */
    public int getGuestsChangeLimit() throws FunctionNotImplementedException {
        throw new FunctionNotImplementedException("Not usable method for this card: " + name.toString());
    }


}
