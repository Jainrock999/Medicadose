package com.example.forebear.treschattingapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
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
import com.example.forebear.treschattingapp.DB.DB_Name;
import com.example.forebear.treschattingapp.Models.DTO_chat;
import com.example.forebear.treschattingapp.Models.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_Chat extends AppCompatActivity {


    String clientID, clientUserName, clientUserNumber;
    List<DTO_chat> send;
    List<DTO_chat> dto_chatList;
    Adapter_Chat chat_adapter;
    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 0;
    File destination;
    Bitmap thumbnail;
    ProgressDialog progressDialog;
    public static Handler mHandler = new Handler();
    public static Timer mTimer = null;
    TimeZone timeZone;

    @BindView(R.id.clientName)
    TextView clientName;
    @BindView(R.id.clientNumber)
    TextView clientNumber;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.chat_window)
    LinearLayout chatWindow;
    @BindView(R.id.message)
    EditText message;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        mTimer.purge();
    }


    @Override
    protected void onResume() {
        super.onResume();
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = new Timer();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        mTimer.schedule(new TimeDisplayTimerTask(), 0, 1000);
    }

    @OnClick({R.id.back_chat, R.id.attach_image, R.id.sendButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_chat:
                startActivity(new Intent(Activity_Chat.this, Activity_Lending.class));
                finish();
                break;
            case R.id.attach_image:
                callImageDialog();
                break;
            case R.id.sendButton:
                if (message.getText().toString().trim().isEmpty()) {
                    printMessage("Please Enter Message");
                } else {
                    SendMessage();
                }
                break;
        }
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {


                @Override
                public void run() {
                    String recive_url = Utils.url + "receivemsg?token=" + Utils.getuserToken(getApplicationContext()) +
                            "&from_user_id=" + clientID + "&to_user_id=" + Utils.getUserId(getApplicationContext());

                    RequestQueue ARequestQueue = Volley.newRequestQueue(getApplicationContext());

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, recive_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {
                                        Log.d("sarvesh---", "recive---- " + response);

                                        JSONObject jsonObject = new JSONObject(response);

                                        if (jsonObject.getInt("status") == 1) {

                                            JSONArray jsonArray = jsonObject.getJSONArray("user_message");

                                            if (jsonArray.length() != 0L) {
                                                for (int i = 0; i < jsonArray.length(); i++) {

                                                    JSONObject object = jsonArray.getJSONObject(i);
                                                    if (object.getString("active_msg").equals("1")) {


                                                        DTO_chat dto_chat = new DTO_chat(
                                                                object.getString("from_user_id"),
                                                                object.getString("to_user_id"),
                                                                object.getString("add_date"),
                                                                object.getString("msg_image"),
                                                                0,
                                                                1,
                                                                object.getString("message_data"),
                                                                object.getString("add_date"));

                                                        dto_chatList.add(dto_chat);
                                                        chat_adapter.notifyDataSetChanged();
                                                        DB_Name.getInstance(getApplicationContext()).addUserDTO(dto_chat);
                                                        list.smoothScrollToPosition(dto_chatList.size() - 1);
                                                    }
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

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
                    ARequestQueue.add(stringRequest);
                }

            });
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity__chat);
        ButterKnife.bind(this);

        timeZone = TimeZone.getDefault();


        clientID = getIntent().getStringExtra("regid");
        clientUserName = getIntent().getStringExtra("name");
        clientUserNumber = getIntent().getStringExtra("number");

        clientName.setText(clientUserName);
        clientNumber.setText(clientUserNumber);

        dto_chatList = new ArrayList<>();
        send = new ArrayList<>();

        send = DB_Name.getInstance(getApplicationContext())
                .getUserName(clientID, Utils.getUserId(getApplicationContext()));

        dto_chatList.addAll(send);

        chat_adapter = new Adapter_Chat(Activity_Chat.this, dto_chatList, Utils.getCurrentUserName(getApplicationContext()), clientUserName, clientID);
        list.setAdapter(chat_adapter);
        list.setDivider(null);
        list.smoothScrollToPosition(dto_chatList.size() - 1);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            startActivity(new Intent(Activity_Chat.this, Activity_Lending.class));
            finish();
        }
        return false;
    }

    private void SendMessage() {

        String url = Utils.url + "sendmsg?from_user_id=" + Utils.getUserId(getApplicationContext()) +
                "&to_user_id=" + clientID + "&message_data=" + message.getText().toString().trim();

        Log.d("sarvesh----", url);
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("sarvesh---", "send---- " + response);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject object = jsonArray.getJSONObject(0);

                            if (object.getInt("status") == 1) {
                                message.setText("");
                                DTO_chat dto_chat = new DTO_chat(
                                        object.getString("from_user_id"),
                                        object.getString("to_user_id"),
                                        object.getString("add_date"),
                                        "",
                                        1,
                                        0,
                                        object.getString("message_data"),
                                        object.getString("add_date"));
                                dto_chatList.add(dto_chat);
                                chat_adapter.notifyDataSetChanged();
                                DB_Name.getInstance(getApplicationContext()).addUserDTO(dto_chat);
                                list.smoothScrollToPosition(dto_chatList.size() - 1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
        mRequestQueue.add(stringRequest);

    }

    private void uploadBitmap(final Bitmap bitmap) {

        progressDialog = new ProgressDialog(Activity_Chat.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,

                Utils.url + "sendmsgimg?",

                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();

                        try {
                            Log.e("sarvesh---", "send---- " + new String(response.data));

                            JSONArray jsonArray = new JSONArray(new String(response.data));
                            JSONObject object = jsonArray.getJSONObject(0);

                            if (object.getInt("status") == 1) {
                                Log.e("sarvesh---", "send---- " +
                                                object.getString("from_user_id")+"---"+
                                        object.getString("to_user_id")+"---"+
                                        object.getString("add_date")+"---"+
                                        object.getString("msg_image")+"---"+
                                        1+"---"+
                                        0+"---"+
                                        "---"+
                                        object.getString("add_date"));

                                        DTO_chat dto_chat = new DTO_chat(
                                        object.getString("from_user_id"),
                                        object.getString("to_user_id"),
                                        object.getString("add_date"),
                                        object.getString("msg_image"),
                                        1,
                                        0,
                                       "",
                                        object.getString("add_date"));
                                dto_chatList.add(dto_chat);
                                chat_adapter.notifyDataSetChanged();
                                DB_Name.getInstance(getApplicationContext()).addUserDTO(dto_chat);
                                list.smoothScrollToPosition(dto_chatList.size() - 1);

                                printMessage("Image Send");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();

                    }
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("from_user_id", Utils.getUserId(getApplicationContext()));
                params.put("to_user_id", clientID);
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("msg_image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void printMessage(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void callImageDialog() {

        final Dialog dialog = new Dialog(Activity_Chat.this);
        dialog.setContentView(R.layout.view_image_select_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView camera = (TextView) dialog.findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
                dialog.dismiss();
            }
        });
        TextView gallery = (TextView) dialog.findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                thumbnail = null;
                if (data != null) {
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                uploadBitmap(thumbnail);

            } else if (requestCode == REQUEST_CAMERA) {
                thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                uploadBitmap(thumbnail);
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
