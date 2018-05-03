package com.example.forebear.treschattingapp.Models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by forebear on 21/2/18.
 */

public class Utils {
    public static String url = "http://dev1.sbsgroupsolutions.co.in/chatsystem/index.php/webservice/";

    public static String ClientName = "client_name";
    public static String ClientMobile = "client_number";
    public static String UserName = "current_user";
    public static String UserNumber = "user_number";
    public static String Token = "token";
    public static String TIME = "time";
    public static String ID = "id";




    public static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        return editor;
    }

    public static SharedPreferences getPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        return pref;
    }

    public static void setFCMToken(Context context, String str) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString("fcm", str);
        edit.commit();
    }
    public static String getFCMToken(Context context) {
        String deviceID;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        deviceID = pref.getString("fcm","");
        return deviceID;
    }

    public static void setLogout(Context context, int str) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();
        edit.putInt("logout", str);
        edit.commit();
    }
    public static int getLogout(Context context) {
        int deviceID;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        deviceID = pref.getInt("logout",0);
        return deviceID;
    }


    public static void setTime(Context context, String TIME) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(TIME, TIME);
        editor.commit();
    }

    public static String getTime(Context context) {
        String tim = getPref(context).getString(TIME, "");
        return tim;
    }

    public static void setUserId(Context context, String id) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(ID, id);
        editor.commit();
    }

    public static String getUserId(Context context) {
        String name = getPref(context).getString(ID, "");
        return name;
    }

    public static void setUserToken(Context context, String token) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(Token, token);
        editor.commit();
    }

    public static String getuserToken(Context context) {
        String name = getPref(context).getString(Token, "");
        return name;
    }
 public static void setCurrentUserName(Context context, String name) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(UserName, name);
        editor.commit();
    }

    public static String getCurrentUserName(Context context) {
        String name = getPref(context).getString(UserName, "sarvesh");
        return name;
    }


    public static void setCurrentUserNumber(Context context, String mobile) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(UserNumber, mobile);
        editor.commit();
    }

    public static String getCurrentUserNumber(Context context) {
        String name = getPref(context).getString(UserNumber, "8109140353");
        return name;
    }

    public static void setClientName(Context context, String name) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(ClientName, name);
        editor.commit();
    }

    public static String getClientName(Context context) {
        String name = getPref(context).getString(ClientName, "client");
        return name;
    }


    public static void setClientNumber(Context context, String mobile) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(ClientMobile, mobile);
        editor.commit();
    }

    public static String getClientNumber(Context context) {
        String name = getPref(context).getString(ClientMobile, "9131474643");
        return name;
    }

}
