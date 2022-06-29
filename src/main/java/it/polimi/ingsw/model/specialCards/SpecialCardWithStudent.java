package it.polimi.ingsw.model.specialCards;

import it.polimi.ingsw.controller.mvc.Listener;
import it.polimi.ingsw.messages.MessageGenerator;
import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.server.ClientHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialCardWithStudent extends SpecialCard{
    private HashMap<Color, Integer> guests;
    private int guestsLimit;
    private int guestsChangeLimit;


    /**
     * Imposes the limit of students that the card can contain and its change limit.
     *
     * @param name name of the special Card
     */
    public SpecialCardWithStudent(SpecialCardName name) {
        super(name);
        this.guests = new HashMap<Color, Integer>();
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
    }

    /**
     * Overrides setListenerAndClients to notify the guests into the special card
     * @param modelListener : the modelListener
     * @param clients : the clients to notify
     */
    @Override
    public void setListenerAndClients(Listener modelListener, List<ClientHandler> clients){
        super.setListenerAndClients(modelListener,clients);
        System.out.println("SPECIAL CARD "+this.name+" I HAVE NOW SET THE LISTENERS and I am with students!");
        notifyGuestsChange();
    }

    /**
     * Notifies the changes to the guests
     */
    private void notifyGuestsChange(){
        System.out.println("SPECIAL CARD "+name+" I have changed the counter, now I notify");
        if(modelListener!=null&&clients!=null){
            Map<Color,Integer> studentToSend = new HashMap<>();
            if(guests!=null)
                studentToSend.putAll(guests);
            notify(modelListener, MessageGenerator.specialCardUpdateMessage(this.name,this.cost,studentToSend),clients);
        }
    }

    /**
     * Adds a student in the card room.
     * Returns false if the limit was reached and adding the student was not added, otherwise returns true.
     *
     * @param studentColor is the student to add in the room.
     * @return outcome of the addition.
     */

    @Override
    public boolean addStudent(Color studentColor) {
        if (this.countStudents() < guestsLimit) {
            if (!guests.containsKey(studentColor)) {
                guests.put(studentColor, 1);
            } else {
                guests.put(studentColor, this.countStudents(studentColor) + 1);
            }
            notifyGuestsChange();
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
    @Override
    public boolean removeStudent(Color studentColor) {
        Integer previousNumStudents = guests.get(studentColor);
        if (previousNumStudents == null || this.countStudents(studentColor) <= 0) return false;
        guests.put(studentColor, previousNumStudents - 1);
        notifyGuestsChange();
        return true;
    }

    /**
     * Count the number of students of a specific color.
     *
     * @param studentColor the color of students to count.
     * @return the number of student of that color.
     */
    @Override
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
    @Override
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
     * Getter guestLimit
     *
     * @return guestLimit
     */
    @Override
    public int getGuestsLimit(){
        return guestsLimit;
    }

    /**
     * Getter guestChangeLimit
     *
     * @return guestChangeLimit
     */
    @Override
    public int getGuestsChangeLimit(){
        return guestsChangeLimit;
    }
}
