package com.example.forebear.treschattingapp.Asynk;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.forebear.treschattingapp.Models.DTO_User;
import com.example.forebear.treschattingapp.Models.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by forebear on 22/2/18.
 */

public class OnlineUser extends AsyncTask<Void,Void,ArrayList<DTO_User>> {

    Dialog dialog;
    Context context;
    ArrayList<DTO_User> list;
    String link;


    public OnlineUser(Context context ,String link) {
        this.context = context;
        this.link = link;
        Log.d("sarvesh----",link);
    }

    @Override
    protected ArrayList<DTO_User> doInBackground(Void... voids) {

        URL url;
        HttpURLConnection urlConnection = null;
        list = new ArrayList<>();
        try {

            url = new URL(link);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
          //  urlConnection.setRequestProperty("Authorization", Utils.getuserToken(context));
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setUseCaches(false);


                BufferedReader reader = null;
                StringBuffer response = new StringBuffer();
                try {
                    reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            try {
                    Log.d("sarvesh----",response.toString());
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray jsonArray = jsonObject.getJSONArray("online_user_list");

                for (int x = 0; x < jsonArray.length(); x++) {
                    JSONObject object = jsonArray.getJSONObject(x);

                    list.add(new DTO_User(object.getString("reg_id"),
                            object.getString("user_name"),
                            object.getString("mobile_number"),
                            object.getString("user_image"),
                            object.getString("active_msg")));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

                return list;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return list;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        dialog = new ProgressDialog(context);
        dialog.setTitle("Loading...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected void onPostExecute(ArrayList<DTO_User> dto_users) {
        super.onPostExecute(dto_users);
        dialog.dismiss();
    }
}
