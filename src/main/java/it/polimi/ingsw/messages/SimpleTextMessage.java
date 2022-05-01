package it.polimi.ingsw.messages;

public class SimpleTextMessage implements Message {
    private final String text;

    public SimpleTextMessage(String text){
        this.text = text;
    }

    public String getMessage(){
        return this.text;
    }
}
