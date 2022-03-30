package it.polimi.ingsw;

public class DiningRoom extends Room {

    public DiningRoom(int guestLimit) {
        super(guestLimit);
    }

    /**
     * DiningRoom constructor imposes a ten student limit of each color.
     */
    public DiningRoom() {
        super(10);
    }
}
