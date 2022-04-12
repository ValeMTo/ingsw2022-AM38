
package it.polimi.ingsw;

import java.util.HashMap;


public class Cloud {
    private HashMap<Color,Integer> counter;
    final int studentLimit;


    /**
     * Cloud constructor sets the maximum amount of total students allowed on the cloud, according to the
     * number of players in the game.
     * It initialises the hashmap with zero students for each color. The cloud will be filled further on in the game.
     *
     * @param studentLimit : the maximum number of total students on that cloud.
     */
    public Cloud(int studentLimit) {
        this.studentLimit = studentLimit;
        this.counter = new HashMap<Color, Integer>();
        for(Color col : Color.values()) {
            counter.put(col,0);
        }
    }


    /**
     * Checks whether the selected cloud is full.
     * Returns true if the cloud contains the maximum amount of students (studentLimit); false otherwise
     * @return the fill status of the cloud
     */
    public boolean isFull() {
        int totalStud = 0;
        for (Integer amount : counter.values()) {
            totalStud = totalStud + amount;
        }
        if(totalStud == studentLimit)
            return true;

        return false;
    }

    /**
     * Checks whether the selected cloud is empty.
     * Returns true if the cloud has no students left (zero students); false otherwise
     * @return the emptiness status of the cloud
     */
    public boolean isEmpty() {
        for (Integer amount : counter.values()) {
            if (amount > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Empties the given cloud, removing all the students it contains.
     * @return the outcome of the emptying : false is the cloud is already empty and cannot be emptied; true otherwise.
     */
    public boolean emptyCloud() {
        if(this.isEmpty())
            return false;
        counter.replaceAll((key,value)->0);
        return true;
    }

    /**
     * Adds one student of the given color and places it on the given cloud.
     * Returns false if the limit of students was reached and the student cannot be added, otherwise returns true.
     *
     * @param studentColor is the color of the student to add in the cloud.
     * @return outcome of the adding attempt.
     */
    public boolean addStudent(Color studentColor) throws NullPointerException {
        if (studentColor == null) throw new NullPointerException();

        Integer previousNumStud = counter.get(studentColor);

        if (this.isEmpty()) {
            counter.put(studentColor, 1);
        } else {
            if (this.isFull())
                return false;
            counter.put(studentColor, previousNumStud + 1);
        }
        return true;
    }


    /**
     * Removes a student of the given color from the cloud.
     * Returns true if there was a student of the color to remove, otherwise false.
     *
     * @param studentColor is the color of the student to remove from the cloud.
     * @return outcome of the removal
     */
    public boolean removeStudent(Color studentColor) {
        Integer previousNumStudents = counter.get(studentColor);
        if (previousNumStudents == null || this.countStudentByColor(studentColor) <= 0)
            return false;
        counter.put(studentColor, previousNumStudents - 1);
        return true;
    }


    /**
     * returns the current  amount of students of a certain color in the cloud.
     * @param color : the color of the students to be counted
     * @return the number of students of that given color that are currently contained in the cloud.
     */
    public int countStudentByColor(Color color) {
        return this.counter.get(color);
    }

}