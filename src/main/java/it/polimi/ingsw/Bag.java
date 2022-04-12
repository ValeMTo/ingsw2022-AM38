package it.polimi.ingsw;


import java.util.HashMap;
import java.util.Random;


public class Bag {

    private final int maxInitStudents = 120;
    private final int initStudentsPerColor = maxInitStudents / 5;
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
        this.studentNumber = 10;
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
        studentNumber = maxInitStudents;
    }


    /**
     * Draws a student from the bag,  picking a pseudo-random Color from the HashMap.
     * If the last student has been drawn and the bag is left empty, nothing happens until the end of the round,
     * when the Controller will call the isEmpty() method.
     *
     * @return Color of the student that has been drawn.
     */

    public Color drawStudent() {

        HashMap<Integer, Color> randIntToColor = new HashMap<Integer, Color>();

        // initializes the hashmap mapping each color to an int index ranging from 0 to 4
        int i = 0;
        for (Color c : Color.values()) {
            randIntToColor.put(i, c);
            i += 1;
        }

        /* The following random color generator picks a random index and checks if there are any students
         *  of the corresponding color.
         */

        Random random = new Random();
        int x = random.nextInt(5);

        for (int j = 0; j < 5; j++) {

            Color randColor = randIntToColor.get(x);

            if (counter.get(randColor) > 0) {  // found a non-empty color "randColor" from which to draw a student
                int previousNumStudents = counter.get(randColor);
                counter.put(randColor, previousNumStudents - 1);
                this.studentNumber -= 1;
                return randColor;
            } else {                        // tries drawing from the next color in the hashmap "randIntToColor"
                x += 1;
                if (x > 4)
                    x = 0;
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


