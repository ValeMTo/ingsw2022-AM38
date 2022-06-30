package it.polimi.ingsw.messages;

public enum ErrorTypeEnum {
    GENERIC_ERROR,                      //0
    CONNECTION_ERROR,                   //1
    INVALID_INPUT,                      //2
    FATAL_ERROR,                        //3
    NICKNAME_ALREADY_TAKEN,             //4
    LOBBY_IS_FULL,                      //5
    NUMBER_OF_PLAYERS_ALREADY_SET,      //6
    GAME_MODE_ALREADY_SET,              //7
    ALREADY_USED_ASSISTANT_CARD,        //8
    ALL_MOVED_USED,                     //9
    NO_SUCH_STUDENT_IN_SCHOOL_ENTRANCE, //10
    DINING_ROOM_COLOR_FULL,             //11
    ISLAND_OUT_OF_BOUND,                //12
    EXCEEDED_STEPS_NUM,                 //13
    CARD_ALREADY_USED,                  //14
    NOT_ENOUGH_COINS,                   //15
    NOT_VALID_ORIGIN,                   //16
    NOT_VALID_DESTINATION,              //17
    ISLAND_ALREADY_BLOCKED,             //18
    ALREADY_USED_SPECIAL_CARD,          //19
    NOT_YOUR_TURN,                      //20
    ALREADY_USED_CLOUD,                 //21
    WRONG_PHASE_ACTION,                 //22
    NO_SUCH_SPECIAL_CARD,               //23
    NO_SUCH_STUDENT_ON_SPECIAL_CARD,    //24
    NO_SUCH_STUDENT_IN_DINING_ROOM,     //25
    ALL_TILES_USED,                     //26
    FUNCTION_NOT_IMPLEMENTED,           //27
    NO_STUDENTS_IN_DINING_ROOM,         //28
    NOT_TERMINABLE                      //29
}
