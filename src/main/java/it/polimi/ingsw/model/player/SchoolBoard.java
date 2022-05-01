package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.DiningRoom;

public class SchoolBoard {
    private final SchoolEntrance entrance;
    private final DiningRoom house;

    /**
     * SchoolBoard constructor
     *
     * @param entranceLimit   : Student limit of the entrance
     * @param diningRoomLimit : Student limit of the DiningRoom house
     */
    public SchoolBoard(int entranceLimit, int diningRoomLimit) {
        entrance = new SchoolEntrance(entranceLimit);
        house = new DiningRoom(diningRoomLimit);
    }

    /**
     * Add a student to DiningRoom house
     *
     * @param student : student color
     * @return true if it is allowed to add the student in the DiningRoom house
     * false if it cannot add a student in the DiningRoom house
     */
    public boolean addStudentDiningRoom(Color student) {
        try {
            return house.addStudent(student);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Add a student to entrance
     *
     * @param student : student color
     * @return true if it is allowed to add the student in the entrance
     * false if it cannot add a student in the entrance
     */
    public boolean addStudentEntrance(Color student) {
        try {
            return entrance.addStudent(student);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a student to DiningRoom house
     *
     * @param student : student color
     * @return true if it is allowed to remove the student in the DiningRoom house
     * false if it cannot remove students in the DiningRoom house
     */
    public boolean removeStudentDiningRoom(Color student) {
        try {
            return house.removeStudent(student);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a student to EntranceRoom
     *
     * @param student : student color
     * @return true if it is allowed to remove the student in the entrance
     * false if it cannot remove students in the entrance
     */
    public boolean removeStudentEntrance(Color student) {
        try {
            return entrance.removeStudent(student);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Count the total number of students in DiningRoom house
     *
     * @return the number of students in DiningRoom house
     */
    public int countStudentsDiningRoom() {
        return house.countStudents();
    }

    /**
     * Count all the students in entrance
     *
     * @return the number of students in entrance
     */
    public int countStudentsEntrance() {
        return entrance.countStudents();
    }

    /**
     * Count the number of students of a particular color in DiningRoom house
     *
     * @param color : select a color for the count
     * @return the number of student of the selected color returned by DiningRoom house
     */
    public int countStudentsDiningRoom(Color color) {
        return house.countStudents(color);
    }

    /**
     * Count the number of students of a particular color in entrance
     *
     * @param color : select a color for the count
     * @return the number of student of the selected color returned by entrance
     */
    public int countStudentsEntrance(Color color) {
        return entrance.countStudents(color);
    }
}
