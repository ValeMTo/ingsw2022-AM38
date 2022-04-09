package it.polimi.ingsw;

public class AssistantCard {
    final int priority;
    final int motherNatureSteps;
    private boolean used;

    /**
     * AssistantCard constructor builds the card given its turn priority and motherNatureSteps parameter
     *
     * @param motherNatureSteps states the maximum amount of steps MotherNature can make if that card is played
     * @param priority  is the number used to set the order/priority of the players in a turn
     */

    public AssistantCard(int priority, int motherNatureSteps) {
        this.priority = priority;
        this.motherNatureSteps = motherNatureSteps;
        this.used = false;
    }

    /**
     * returns true if the card has already been used (or if it is being used in the current round)
     * returns false if it is still in the deck, and it hasn't been used
     */

    public boolean isUsed() {
        return this.used;
    }

    /**
     * changes the status of an unused card to used
     */

    public void use() {
        if (!used)
            this.used = true;
    /*    else
    *       throw AlreadyUsedCardException;
    */
    }


    /**
     * returns the priority parameter of an assistant card
     * @return value "priority" of the given Assistant card
     */

    public int getPriority() {
        return this.priority;
    }


    /**
     * returns the "motherNatureSteps" parameter of an assistant card
     * @return the value of "motherNatureSteps" of the given Assistant card
     */

    public int getSteps() {
        return this.motherNatureSteps;
    }


}



