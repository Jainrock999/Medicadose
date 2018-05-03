package com.example.forebear.treschattingapp.Notification;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by forebear on 1/3/18.
 */

public class Config {
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";


    public static void setFirstTimeID(Context ctx, int id) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor edt = pref.edit();
        edt.putInt("ID", id);
        edt.commit();
    }

    public static int getFirstTimeID(Context ctx) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        int is = pref.getInt("ID", 0);
        return is;
    }
}
