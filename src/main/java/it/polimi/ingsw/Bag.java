package it.polimi.ingsw;

import it.polimi.*;

import java.util.HashMap;

import java.util.Random;


public class Bag {

    private HashMap<Color,Integer> counter;
    private int studentNumber;
    private int colorLimit;


    /**
     * Constructor that creates the bag and its HashMap without specifying yet the initial amount of students.
     *
     */

    public Bag(){
        this.counter = new HashMap<Color,Integer>();
    }


    /**
     *   Initializes the initial number of students in the bag, at the beginning of the game.
     *   Note that the initial number varies according to the number of players in the game
     *   The initial amount of students for each color will be set by the Controller using the addStudent() method
     *
     * @param colorLimit  the maximum amount of Students allowed for that color as set by the Controller
     */

    public void initialise(int numStudent, int colorLimit) {
        this.studentNumber = numStudent;
        this.colorLimit = colorLimit;
    }


    /**
     *  Draws a student from the bag,  picking a pseudo-random Color from the HashMap.
     *
     * @return Color of the student that has been drawn
     *
     */

    public Color drawStudent() {

        if(this.isEmpty())
            throw new EmptyBagException();

        HashMap<Integer, Color> randIntToColor = new HashMap<Integer,Color>(); ;
        boolean foundColor = false;

        // initializes the hashmap mapping each color to an int index ranging from 0 to 4
        int i = 0;
        for(Color c: Color.values()){
            randIntToColor.put(i,c);
            i+=1 ;
        }

        // start of the random Color generator

        Random random = new Random();
        int x = random.nextInt(5);

        for (int j=0; j<5; j++) {

            Color color = randIntToColor.get(x);

            if(counter.get(color)  > 0) {           // found a non-empty color "c" from which to draw a student
                Integer previousNumStudents = counter.get(c);
                counter.put(color, previousNumStudents-1);

                return color;

            }
            else {                        // tries drawing from the next color in the hashmap
                x+=1 ;
                if (x>4)
                    x = 0;
            }

        }
        return null;
    }


    /**
     * Adds a student of the given color  in the Bag.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the Bag.
     * @return outcome of the adding attempt.
     */
    public boolean addStudent(Color studentColor) throws NullPointerException {
        if (studentColor == null) throw new NullPointerException();

        Integer previousNumStudents = counter.get(studentColor);
        if (previousNumStudents == null) {
            counter.put(studentColor, 1);
        } else {
            if (previousNumStudents >= colorLimit) return false;
            counter.put(studentColor, previousNumStudents + 1);
        }
        return true;
    }


    /**
     * Returns true if there are no students left in the Bag, false if there is one student or more, whatever their color.
     *
     * @return students shortage status in the bag.
     */

    public boolean isEmpty() {

        for (Integer numStudents : counter.values()) {
            if(numStudents > 0)  { return false; }
        }
        return true;

    }


}


