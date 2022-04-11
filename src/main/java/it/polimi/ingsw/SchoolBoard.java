package it.polimi.ingsw;
// IMPLEMENTED CLASS



public class SchoolBoard {
    private SchoolEntrance entrance;
    private DiningRoom house;

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
        return house.addStudent(student);
    }

    /**
     * Add a student to entrance
     *
     * @param student : student color
     * @return true if it is allowed to add the student in the entrance
     * false if it cannot add a student in the entrance
     */
    public boolean addStudentEntrance(Color student) {
        return entrance.addStudent(student);
    }

    /**
     * Remove a student to DiningRoom house
     *
     * @param student : student color
     * @return true if it is allowed to remove the student in the DiningRoom house
     * false if it cannot remove students in the DiningRoom house
     */
    public boolean removeStudentDiningRoom(Color student) {
        return house.removeStudent(student);
    }

    /**
     * Remove a student to EntranceRoom
     *
     * @param student : student color
     * @return true if it is allowed to remove the student in the entrance
     * false if it cannot remove students in the entrance
     */
    public boolean removeStudentEntrance(Color student) {
        return entrance.removeStudent(student);
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
