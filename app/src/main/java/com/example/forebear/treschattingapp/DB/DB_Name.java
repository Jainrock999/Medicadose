package com.example.forebear.treschattingapp.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.forebear.treschattingapp.Models.DTO_chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DB_Name extends SQLiteOpenHelper {

    private static volatile DB_Name instance;


    String TABLE_Name = "nameList";
    String SENDER_ID = "sendr_id";
    String RECIVER_ID = "reciver_id";
    String TIME_STAMP = "time_stamp";
    String TIMER = "time";
    String IMAGE = "image";
    String SENDER = "sender";
    String RECIVER = "reciver";
    String ID = "id";
    String MSG = "message";


    public DB_Name(Context context) {
        super(context, "DB_Name", null, 1);
    }

    public static DB_Name getInstance(final Context context) {
        DB_Name localInstance = instance;
        if (localInstance == null) {
            synchronized (DB_Name.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new DB_Name(context);
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE "
                + TABLE_Name + "("
                + ID + " integer PRIMARY KEY AUTOINCREMENT, "
                + SENDER_ID + " text, "
                + RECIVER_ID + " text, "
                + TIME_STAMP + " text, "
                + TIMER + " text, "
                + IMAGE + " text, "
                + SENDER + " integer, "
                + RECIVER + " integer, "
                + MSG + " text " + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addUserDTO(DTO_chat dto_chat) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();

        Log.d("sarvesh----", "senderID-" + dto_chat.getSENDER_ID());
        Log.d("sarvesh----", "reciverID-" + dto_chat.getRECIVER_ID());
        Log.d("sarvesh----", dto_chat.getMSG());

        values.put(SENDER_ID, dto_chat.getSENDER_ID());
        values.put(RECIVER_ID, dto_chat.getRECIVER_ID());
        values.put(TIME_STAMP, dto_chat.getTIME_STAMP());
        values.put(IMAGE, dto_chat.getIMAGE());
        values.put(SENDER, dto_chat.getSENDER());
        values.put(RECIVER, dto_chat.getRECIVER());
        values.put(MSG, dto_chat.getMSG());
        values.put(TIMER, dto_chat.getTimer());

        database.insert(TABLE_Name, null, values);
    }

    public ArrayList<DTO_chat> getUserName(String senderID, String reciverID) {

        SQLiteDatabase db = getWritableDatabase();
        ArrayList<DTO_chat> userList = new ArrayList<>();
        Log.d("sarvesh----", "cam into DB");

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_Name+
                " WHERE " + (SENDER_ID + " = " + senderID + " AND " + RECIVER_ID + " = " + reciverID) + " OR "
                        + (SENDER_ID + " = " + reciverID + " AND " + RECIVER_ID + " = " + senderID), null);

      /*  Cursor c = db.query(TABLE_Name, null, SENDER_ID + "=? AND " + RECIVER_ID + "=? "
                , new String[]{senderID, reciverID}, null, null, null);*/

        if (c.moveToFirst()) {
            do {
                Log.d("sarvesh----", c.getString(c.getColumnIndex(SENDER_ID)) + "---" +
                        c.getString(c.getColumnIndex(RECIVER_ID)) + "---" +
                        c.getString(c.getColumnIndex(TIME_STAMP)) + "---" +
                        c.getString(c.getColumnIndex(IMAGE)) + "---" +
                        c.getInt(c.getColumnIndex(RECIVER)) + "---" +
                        c.getInt(c.getColumnIndex(SENDER)) + "---" +
                        c.getString(c.getColumnIndex(MSG)) + "---" +
                        c.getString(c.getColumnIndex(TIMER)));

                userList.add(new DTO_chat(c.getString(c.getColumnIndex(SENDER_ID)),
                        c.getString(c.getColumnIndex(RECIVER_ID)),
                        c.getString(c.getColumnIndex(TIME_STAMP)),
                        c.getString(c.getColumnIndex(IMAGE)),
                        c.getInt(c.getColumnIndex(SENDER)),
                        c.getInt(c.getColumnIndex(RECIVER)),
                        c.getString(c.getColumnIndex(MSG)),
                        c.getString(c.getColumnIndex(TIMER))));
            } while (c.moveToNext());
        }
        c.close();

        return userList;
    }

    public String GetDate(String senderID, String reciverID) {
        SQLiteDatabase db = getWritableDatabase();
        String date = "";

        Cursor c = db.query(TABLE_Name, null, SENDER_ID + "=? AND " + RECIVER_ID + "=? ", new String[]{senderID, reciverID}, null, null, null);

        if (c.moveToFirst()) {
            do {
                date = c.getString(c.getColumnIndex(TIME_STAMP));
            } while (c.moveToNext());
        }
        c.close();

        return date;

    }

}
