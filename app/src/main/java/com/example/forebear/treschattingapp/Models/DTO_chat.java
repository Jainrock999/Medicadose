package com.example.forebear.treschattingapp.Models;

/**
 * Created by forebear on 24/2/18.
 */

public class DTO_chat {

    String SENDER_ID ;
    String RECIVER_ID ;
    String TIME_STAMP ;
    String IMAGE ;
    int SENDER ;
    int RECIVER ;
    String MSG ;
    String Timer;

    public DTO_chat(String SENDER_ID, String RECIVER_ID, String TIME_STAMP,
                    String IMAGE, int SENDER, int RECIVER, String MSG, String Timer) {
        this.SENDER_ID = SENDER_ID;
        this.RECIVER_ID = RECIVER_ID;
        this.TIME_STAMP = TIME_STAMP;
        this.Timer = Timer;
        this.IMAGE = IMAGE;
        this.SENDER = SENDER;
        this.RECIVER = RECIVER;
        this.MSG = MSG;
    }

    public String getSENDER_ID() {
        return SENDER_ID;
    }

    public String getRECIVER_ID() {
        return RECIVER_ID;
    }

    public String getTIME_STAMP() {
        return TIME_STAMP;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public int getSENDER() {
        return SENDER;
    }

    public int getRECIVER() {
        return RECIVER;
    }

    public String getMSG() {
        return MSG;
    }

    public String getTimer() {
        return Timer;
    }
}
