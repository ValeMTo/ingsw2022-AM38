package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.board.Color;

public class SchoolEntrance extends Room {

    public SchoolEntrance(int guestLimit) {
        super(guestLimit);
    }

    @Override
    public boolean addStudent(Color studentColor) throws NullPointerException {
        if (studentColor == null) throw new NullPointerException();

        if (this.countStudents() < guestLimit) {
            Integer previousNumStudents = guests.get(studentColor);
            if (previousNumStudents == null) {
                guests.put(studentColor, 1);
            } else {
                if (guestLimit < previousNumStudents) return false;
                guests.put(studentColor, previousNumStudents + 1);
            }
            return true;
        }
        return false;
    }
}
