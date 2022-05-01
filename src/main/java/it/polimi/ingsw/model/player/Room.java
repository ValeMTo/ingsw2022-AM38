package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.board.Color;

import java.util.HashMap;

abstract class Room {
    final int guestLimit;
    HashMap<Color, Integer> guests;

    /**
     * Room costructor set the maximum amount of students inside the room.
     *
     * @param guestLimit limit number of student of each color in a room.
     */
    public Room(int guestLimit) {
        this.guestLimit = guestLimit;
        this.guests = new HashMap<Color, Integer>();
    }

    /**
     * Adds a student in the room.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */
    public boolean addStudent(Color studentColor) throws NullPointerException {
        if (studentColor == null) throw new NullPointerException();

        Integer previousNumStudents = guests.get(studentColor);
        if (previousNumStudents == null) {
            guests.put(studentColor, 1);
        } else {
            if (previousNumStudents >= guestLimit) return false;
            guests.put(studentColor, previousNumStudents + 1);
        }
        return true;
    }

    /**
     * Removes a student from the room.
     * Returns true if the student was present, otherwise false.
     *
     * @param studentColor is the student to remove from the room.
     * @return outcome of the removal
     */
    public boolean removeStudent(Color studentColor) {
        if (studentColor == null) throw new NullPointerException();

        Integer previousNumStudents = guests.get(studentColor);
        if (previousNumStudents == null || this.countStudents(studentColor) <= 0) return false;
        guests.put(studentColor, previousNumStudents - 1);
        return true;
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
}
