package it.polimi.ingsw.messages;


import java.util.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.model.board.*;
import org.json.JSONObject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;



public class MessageGeneratorTests {

    Map<String, Tower> playersMappings = new HashMap<String, Tower>();
    Map<Color,Integer> studentsMap = new HashMap<Color, Integer>();

    private void fillStudentsMap(Map<Color,Integer> map) {
        map.put(Color.YELLOW,0);
        map.put(Color.BLUE, 1);
        map.put(Color.PINK, 2);
        map.put(Color.GREEN, 0);
        map.put(Color.RED, 0);
    }

    private void fillMapping(Map<String, Tower> testMap) {
        testMap.put("Player1", Tower.WHITE);
        testMap.put("Player2", Tower.BLACK);
        testMap.put("Player3", Tower.GRAY);
    }

    private JsonObject getMessage(String messageReceived) {
        boolean error = false;
        Gson gson = new Gson();
        JsonObject json;
        do {
            System.out.println("waiting for message");

            if (messageReceived == null)
                System.out.println("Got message " + messageReceived);
            json = new Gson().fromJson(messageReceived, JsonObject.class);  //converts message string into JsonObject
            if(json == null || !json.has("MessageType"))
                error = true;
            else
                error = false;
        }while(error);
        return json;
    }



    @Test
    @DisplayName("SetupUpdate message test")
    public void setupUpdateMessageTest() {
        String generatedMessage;
        fillMapping(playersMappings);
        System.out.println("Initial Map: " + playersMappings);

        System.out.println("setupUpdateMessage : " + "\n");
        generatedMessage = MessageGenerator.setupUpdateMessage(playersMappings, 3, true);
        System.out.println(generatedMessage);

        System.out.println("Testing the unpacking...: ");
        JsonObject jsonReceived = getMessage(generatedMessage);
        System.out.print("testing the get method on specific fields..." + "\n");
        System.out.println("Message type is : " + jsonReceived.get("MessageType").getAsInt());
        System.out.println("Players mapping is : " + jsonReceived.get("PlayersMapping").getAsJsonObject());

    }

    @Test
    @DisplayName("CloudViewUpdate message test")
    public void cloudViewUpdateMessageTest() {
        String generatedMessage;
        fillStudentsMap(studentsMap);
        System.out.println("CloudViewUpdateMessage : "+ "\n");
        generatedMessage = MessageGenerator.cloudViewUpdateMessage(2,3, studentsMap);
        System.out.println(generatedMessage);

    }

    @Test
    @DisplayName("AssistantCardUpdate message test")
    public void assistantCardUpdateMessageTest() {
        String generatedMessage;
        ArrayList<Integer> testDeck = new ArrayList<Integer>();
        for(int i=10; i>0; i--) {
            testDeck.add(i);
        }
        System.out.println("Initial array deck: "+"\n");
        for(int c : testDeck)
            System.out.println(c);
        System.out.println("\n");

        System.out.println("AssistantCardUpdate message : "+ "\n");
        generatedMessage = MessageGenerator.assistantCardUpdateMessage(Tower.WHITE, testDeck);
        System.out.println(generatedMessage);

    }



}

