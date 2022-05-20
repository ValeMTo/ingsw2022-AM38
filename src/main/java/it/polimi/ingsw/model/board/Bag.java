package it.polimi.ingsw.model.board;


import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;


public class Bag {

    private final int initStudentsPerColor = 24;
    private final HashMap<Color, Integer> counter;
    private int studentNumber;

    /**
     * Constructor that creates the bag and its HashMap and fills it with 10 students, 2 for each color.
     * This is the initial fill at the beginning of the game,
     * before the students are taken from the Bag and placed on 10 islands.
     */

    public Bag() {
        this.counter = new HashMap<Color, Integer>();

        for (Color col : Color.values()) {
            this.counter.put(col, 2);
        }
        int count = 0;
        for (Integer value : counter.values())
            count = count + value;
        this.studentNumber = count;
    }


    /**
     * Initialises the Bag, filling it with the 120 remaining students, 24 for each color, after the first 10
     * have been placed in the islands.
     * The attribute initStudentsPerColor is 24.
     */

    public void initialise() {
        for (Color col : counter.keySet()) {
            this.counter.put(col, initStudentsPerColor);
        }
        int count = 0;
        for (Integer value : counter.values())
            count = count + value;
        this.studentNumber = count;
    }


    /**
     * Draws a student from the bag,  picking a pseudo-random Color from the HashMap.
     * If the last student has been drawn and the bag is left empty, nothing happens until the end of the round,
     * when the Controller will call the isEmpty() method.
     *
     * @return the color of the student that has been drawn.
     * <p>
     * Notes on the algorithm for random picking:
     * It creates a new linked hashmap that associates each color with a range, marked by a threshold.
     * The values of the thresholds must be >= 1, are incremental and increase with the number of students contained
     * in the bag.
     * Example: the bag contains the following students: (BLUE,10), (YELLOW, 15), (PINK, 16), (GREEN, 19), (RED, 22)
     * The thresholds (threshold, COLOR) will be :  (10, BLUE), (25,YELLOW), (41,PINK), (60,GREEN), (82,RED)
     * Since every single student in the bag is associated with a number
     * (ranging from 1 to the current total number of students in the bag), which represents its "position" in the bag,
     * it is possible to determine in which range/color that student falls.
     * Then it is generated a pseudo-random integer ranging from 1 to studentNumber, and it is checked in which
     * color range it is contained.
     * That integer and color represent the random student that is drawn.
     * The linked hashmap is used to maintain the thresholds in order.
     */

    public Color drawStudent() {

        LinkedHashMap<Integer, Color> colorRange = new LinkedHashMap<Integer, Color>();   //  < threshold, COLOR >

        if (this.isEmpty()) return null;

        // the following cycle fills the linked hashmap with the upper threshold for each color range
        int offset = 0;
        for (Color col : Color.values()) {
            if (countStudentByColor(col) != 0) {
                offset += countStudentByColor(col);
                colorRange.put(offset, col);
            }
        }
        Random random = new Random();
        int randPos = random.nextInt(this.studentNumber);   // picks a random position in the bag
        randPos += 1;

        Set<Integer> thresholds = colorRange.keySet();
        // the keySet yields the thresholds in the order they were inserted

        for (Integer t : thresholds) {
            if (t >= randPos) {
                Color foundColor = colorRange.get(t);
                int previousNumStudents = counter.get(foundColor);
                counter.put(foundColor, previousNumStudents - 1);
                this.studentNumber -= 1;

                return foundColor;
            }
        }
        return null;
    }


    /**
     * Adds a student of the given color  in the Bag.
     * Returns false if the limit was reached and the student cannot be added, otherwise returns true.
     *
     * @param studentColor is the color of the student to add in the Bag.
     * @return outcome of the adding attempt.
     */
    public boolean addStudent(Color studentColor) throws NullPointerException {
        if (studentColor == null) throw new NullPointerException();

        Integer previousNumStudents = counter.get(studentColor);
        if (!counter.containsKey(studentColor)) {
            counter.put(studentColor, 1);
        } else {
            if (previousNumStudents >= initStudentsPerColor)
                // case in which the desired color already has the maximum amount allowed, which is 24.
                return false;
            counter.put(studentColor, previousNumStudents + 1);
        }
        studentNumber += 1;
        return true;
    }


    /**
     * Returns true if there are no students left in the Bag, false if there is at least one student, whatever its color.
     * The cycle iterates through the values of the HashMap.
     *
     * @return students shortage status in the bag.
     */

    public boolean isEmpty() {

        for (Integer numStudents : counter.values()) {
            if (numStudents > 0) {
                return false;
            }
        }
        return true;

    }

    /**
     * returns the current total amount of students contained in the Bag.
     *
     * @return the number of students currently in the bag.
     */

    public int getStudentNumber() {
        return this.studentNumber;
    }

    /**
     * returns the current  amount of students of a certain color in the Bag.
     *
     * @param color : the color of the students to be counted
     * @return the number of students currently in the bag.
     */

    public int countStudentByColor(Color color) {
        return this.counter.get(color);
    }


}


