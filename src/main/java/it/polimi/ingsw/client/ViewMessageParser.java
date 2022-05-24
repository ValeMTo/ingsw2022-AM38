package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.view.ViewState;
import it.polimi.ingsw.messages.AnswerTypeEnum;
import it.polimi.ingsw.messages.MessageTypeEnum;

public class ViewMessageParser {
    Gson gson = new Gson();
    ViewState view;

    public ViewMessageParser(ViewState view){
        this.view = view;
    }

    public void parse(String jsonFromServer){

        if (jsonFromServer != null) {
            System.out.println("Got message " + jsonFromServer);
        }
        JsonObject json = gson.fromJson(jsonFromServer, JsonObject.class);
        System.out.println("Sto parsando...........");

        if (json.get("MessageType").equals(MessageTypeEnum.ERROR.ordinal())){
            System.out.println(json.get("ErrorType") + " - " + json.get("errorString"));
        }

    }


}
