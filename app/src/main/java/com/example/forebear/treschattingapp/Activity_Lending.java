package com.example.forebear.treschattingapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.forebear.treschattingapp.Asynk.OnlineUser;
import com.example.forebear.treschattingapp.Models.DTO_User;
import com.example.forebear.treschattingapp.Models.Utils;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_Lending extends AppCompatActivity {


    ArrayList<DTO_User> RegisterUser_List = new ArrayList<>();
    ArrayList<DTO_User> OnlineUser_list = new ArrayList<>();

    ListView lv;
    Adapter_Lending adapter_lending;
    int check;
    CountDownTimer timer;
    int delay = 2000;
    Runnable myRunnable;
    public static Handler mHandler = new Handler();
    public static Timer mTimer = null;

    @BindView(R.id.currentUser)
    TextView currentUser;
    @BindView(R.id.registerUser_button)
    TextView registerUserButton;
    @BindView(R.id.onlineUser_button)
    TextView onlineUserButton;
    @BindView(R.id.lending_header)
    RelativeLayout lendingHeader;
    @BindView(R.id.online_user_list)
    ListView onlineUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity__lending);
        ButterKnife.bind(this);

        RegisterUser_List = getUserList("get_register_user?token=");
        OnlineUser_list = getUserList("get_online_user?token=");

        lv = (ListView) findViewById(R.id.online_user_list);
        currentUser.setText(Utils.getCurrentUserName(getApplicationContext()) +
                "\n" + Utils.getCurrentUserNumber(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Utils.getLogout(getApplicationContext()) == 0) {
            timer = new CountDownTimer(15 * 60 * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    Log.d("neha-----", "" + millisUntilFinished);
                }

                public void onFinish() {
                    Utils.setLogout(getApplicationContext(), 0);
                    logout();
                    finish();
                }
            };
            timer.start();
        }
        check = 0;
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = new Timer();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        mTimer.schedule(new TimeDisplayTimerTask(), 0,  5000);
    }

    private void logout() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        String url = Utils.url + "user_logout?token=" + Utils.getuserToken(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        printMessage("Logout Sucessfully..");
                        Utils.setUserToken(getApplicationContext(), "");
                        Utils.setCurrentUserName(getApplicationContext(), "");
                        Utils.setCurrentUserNumber(getApplicationContext(), "");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }

                printMessage(message);
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void printMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.setLogout(getApplicationContext(), 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.setLogout(getApplicationContext(), 0);
        timer.cancel();

        mTimer.cancel();
        mTimer.purge();
    }

    public ArrayList<DTO_User> getUserList(String api) {

        ArrayList<DTO_User> userLsit = new ArrayList<>();
        try {
            userLsit = new OnlineUser(Activity_Lending.this,
                    Utils.url + api + Utils.getuserToken(getApplicationContext())).execute().get();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return userLsit;
    }

    @OnClick({R.id.lending_logout, R.id.registerUser_button, R.id.onlineUser_button})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.lending_logout:

                final Dialog dialog = new Dialog(Activity_Lending.this);
                dialog.setContentView(R.layout.view_logout_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                Button logout = (Button) dialog.findViewById(R.id.logout_dialog);
                Button cancel = (Button) dialog.findViewById(R.id.cancel_dialog);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logout();
                        finish();
                    }
                });
                break;
            case R.id.registerUser_button:

                check = 0;

                // cancel if already existed
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = new Timer();
                } else {
                    // recreate new
                    mTimer = new Timer();
                }
                mTimer.schedule(new TimeDisplayTimerTask(), 0,  5000);

                break;

            case R.id.onlineUser_button:

                check = 1;

                // cancel if already existed
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = new Timer();
                } else {
                    // recreate new
                    mTimer = new Timer();
                }
                mTimer.schedule(new TimeDisplayTimerTask(), 0,  3000);
                break;
        }
    }


    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (adapter_lending != null) {
                        RegisterUser_List.clear();
                        OnlineUser_list.clear();
                        adapter_lending.notifyDataSetChanged();
                    }

                    if(check == 1){
                        onlineUserButton.setEnabled(false);
                        registerUserButton.setEnabled(true);
                        onlineUserButton.setBackgroundResource(R.color.gray);
                        registerUserButton.setBackgroundResource(R.color.recive);

                        OnlineUser_list.clear();
                        OnlineUser_list = getUserList("get_online_user?token=");

                        adapter_lending = new Adapter_Lending(OnlineUser_list, Activity_Lending.this, 0);
                        lv.setAdapter(adapter_lending);
                    }else {
                        registerUserButton.setEnabled(false);
                        onlineUserButton.setEnabled(true);
                        registerUserButton.setBackgroundResource(R.color.gray);
                        onlineUserButton.setBackgroundResource(R.color.recive);

                        RegisterUser_List.clear();
                        RegisterUser_List = getUserList("get_register_user?token=");

                        adapter_lending = new Adapter_Lending(RegisterUser_List, Activity_Lending.this, 1);
                        lv.setAdapter(adapter_lending);
                    }
                }

            });
        }

    }
}
