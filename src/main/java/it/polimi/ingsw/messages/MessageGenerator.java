package it.polimi.ingsw.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MessageGenerator {
    private static final Gson gson = new Gson();

    /**
     * Generates a String formatted in json for the nickName set message
     *
     * @param nickName : nickname we want to send
     * @return : json string of the set nickname message
     */
    public static String nickNameMessageGenerate(String nickName) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.SET.ordinal());
        json.addProperty("SetType", SetTypeEnum.SET_NICKNAME.ordinal());
        json.addProperty("nickname", nickName);
        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the message ok
     *
     * @return : json String of the message ok
     */
    public static String okGenerate() {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.OK.ordinal());
        return gson.toJson(json)+"\n";
    }

    /**
     * Generates the message Error with its subfields
     *
     * @param error       : type of error
     * @param errorString : String of the particular error to send
     * @return : json String of the error message
     */
    public static String errorWithStringGenerate(ErrorTypeEnum error, String errorString) {
        JsonObject json = new JsonObject();
        json.addProperty("MessageType", MessageTypeEnum.ERROR.ordinal());
        json.addProperty("ErrorType", error.ordinal());
        json.addProperty("errorString", error.ordinal());
        return gson.toJson(json)+"\n";
    }


}
