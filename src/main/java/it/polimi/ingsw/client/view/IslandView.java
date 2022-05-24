package it.polimi.ingsw.client.view;

import it.polimi.ingsw.model.board.Color;
import it.polimi.ingsw.model.board.Island;

import java.util.HashMap;
import java.util.Map;

public class IslandView extends Island {

    /**
     * Constructor that initializes the HashMap and position
     *
     * @param position : is the initial position of the Island in the gameBoard
     */
    public IslandView(int position) {
        super(position);
        for(Color color : Color.values()){
            if(!this.influence.containsKey(color))
                this.influence.put(color,0);
        }
    }

    /**
     * Returns the students on the Island
     *
     * @return the Map of students contained on the Island
     */
    public Map<Color, Integer> getStudentMap() {
        return new HashMap<>(this.influence);
    }

    /**
     * Changes in one time all the students in the Map
     *
     * @param studentMap : the given Mao with the students colors and numbers
     */
    public void setStudentMap(Map<Color, Integer> studentMap) {
        this.influence.clear();
        this.influence.putAll(studentMap);
        for(Color color : Color.values()){
            if(!this.influence.containsKey(color))
                this.influence.put(color,0);
        }
    }
}
