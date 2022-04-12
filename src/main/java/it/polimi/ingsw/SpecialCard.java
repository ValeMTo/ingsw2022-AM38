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
    public boolean removeTail() throws NotCallableMethodException {
        if (!name.equals(SpecialCardName.HERBALIST)){
            throw new NotCallableMethodException("Not usable method for this card: " + name.toString());
        }
        return false;
    }

    /**
     * Adds a tail when the card is Herbalist, otherwise is useless.
     */
    public boolean returnTail() throws NotCallableMethodException {
        if (!name.equals(SpecialCardName.HERBALIST)){
            throw new NotCallableMethodException("Not usable method for this card: " + name.toString());
        }
        return false;
    }

    /**
     * Check if there are tails when the card is Herbalist, otherwise is useless.
     */
    public boolean isEmptyOfTails() throws NotCallableMethodException {
        if (!name.equals(SpecialCardName.HERBALIST)){
            throw new NotCallableMethodException("Not usable method for this card: " + name.toString());
        }
        return false;
    }

    /**
     * Adds a student in the card room if it is a card with students, otherwise the method is useless.
     */
    public boolean addStudent(Color studentColor) throws NotCallableMethodException {
        if (!name.equals(SpecialCardName.HERBALIST)){
            throw new NotCallableMethodException("Not usable method for this card: " + name.toString());
        }
        return false;
    }

    /**
     * Removes a student from the card room if it is a card with students, otherwise the method is useless.
     */
    public boolean removeStudent(Color studentColor) throws NotCallableMethodException {
        if (!name.equals(SpecialCardName.HERBALIST)){
            throw new NotCallableMethodException("Not usable method for this card: " + name.toString());
        }
        return false;
    }

    /**
     * Count the number of students of a specific color if it is a card with students, otherwise the method is useless.
     */
    public int countStudents(Color studentColor) throws NotCallableMethodException {
        if (!name.equals(SpecialCardName.HERBALIST)){
            throw new NotCallableMethodException("Not usable method for this card: " + name.toString());
        }
        return -1;
    }

    /**
     * Count the number of all students in the room if it is a card with students, otherwise the method is useless.
     */
    public int countStudents() throws NotCallableMethodException {
        if (!name.equals(SpecialCardName.HERBALIST)){
            throw new NotCallableMethodException("Not usable method for this card: " + name.toString());
        }
        return -1;

    }




}