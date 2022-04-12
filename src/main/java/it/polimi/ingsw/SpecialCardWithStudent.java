package it.polimi.ingsw;

import java.util.HashMap;

public class SpecialCardWithStudent extends SpecialCard{
    private HashMap<Color, Integer> guests;
    private int guestsLimit;
    private int guestsChangeLimit;


    public SpecialCardWithStudent(SpecialCardName name) {
        super(name);
        this.guestsChangeLimit = 0;
        this.guestsLimit = 0;
        if (name == SpecialCardName.PRIEST ) {
            this.guestsLimit = 4;
            this.guestsChangeLimit = 1;
        } else if ( name == SpecialCardName.JUGGLER ) {
            this.guestsLimit = 6;
            this.guestsChangeLimit = 3;
        } else if (name == SpecialCardName.PRINCESS) {
            this.guestsLimit = 4;
            this.guestsChangeLimit = 1;
        }

        /*ADD STUDENTS*/
    }

    /**
     * Adds a student in the card room.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */
    public boolean addStudent(Color studentColor) {
        if (this.countStudents() < guestsLimit) {
            Integer previousNumStudents = guests.get(studentColor);
            if (previousNumStudents == null) {
                guests.put(studentColor, 1);
            } else {
                if (guestsLimit < previousNumStudents) return false;
                guests.put(studentColor, previousNumStudents + 1);
            }
            return true;
        }
        return false;
    }

    /**
     * Removes a student from the card room.
     * Returns true if the student was present, otherwise false.
     *
     * @param studentColor is the student to remove from the room.
     * @return outcome of the removal
     */
    public boolean removeStudent(Color studentColor) {
        Integer previousNumStudents = guests.get(studentColor);
        if (previousNumStudents == null || this.countStudents(studentColor) <= 0) return false;
        guests.put(studentColor, previousNumStudents - 1);
        return true;
    }

    /**
     * Count the number of students of a specific color.
     *
     * @param studentColor the color of students to count.
     * @return the number of student of that color.
     */
    public int countStudents(Color studentColor) {
        if (guests.containsKey(studentColor)) {
            return guests.get(studentColor);
        }
        return 0;
    }

    /**
     * Count the number of all students in the room, uncaring of the color.
     *
     * @return number of all students in the room
     */
    public int countStudents() {
        int numStudents = 0;
        for (Color color : Color.values()) {
            if (guests.containsKey(color)) {
                numStudents += guests.get(color);
            }
        }
        return numStudents;

    }
}
