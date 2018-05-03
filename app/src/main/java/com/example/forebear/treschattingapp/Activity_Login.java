package com.example.forebear.treschattingapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.forebear.treschattingapp.Models.Utils;
import com.example.forebear.treschattingapp.Notification.Config;
import com.example.forebear.treschattingapp.Notification.MyFirebaseInstanceIDService;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Activity_Login extends Activity {

    @BindView(R.id.user_image)
    ImageView userImage;
    @BindView(R.id.login_username)
    EditText loginUsername;
    @BindView(R.id.login_number)
    EditText loginNumber;

    Dialog dialog;
    int REQUEST_CAMERA = 1;
    int SELECT_FILE = 0;
    File destination;
    Bitmap thumbnail;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity__login);
        ButterKnife.bind(this);

        if (Config.getFirstTimeID(getApplicationContext()) == 0) {
            Config.setFirstTimeID(getApplicationContext(), 1);
            Intent intent = new Intent(this, MyFirebaseInstanceIDService.class);
            startService(intent);
        } else if (Config.getFirstTimeID(getApplicationContext()) == 1) {
            Utils.setFCMToken(getApplicationContext(), FirebaseInstanceId.getInstance().getToken());
        }
    }

    @OnClick({R.id.user_image, R.id.login_genrate_OTP, R.id.allready_registered})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_image:
                callImageDialog();
                break;
            case R.id.login_genrate_OTP:
                if (InternetCheck()) {
                    if (loginUsername.getText().toString().trim().isEmpty() && loginNumber.getText().toString().trim().isEmpty()) {
                        printMessage("Please Enter Name & Mobile Number");
                    } else if (loginUsername.getText().toString().trim().isEmpty()) {
                        printMessage("Please Enter Name");
                    } else if (loginNumber.getText().toString().isEmpty()) {
                        printMessage("Please Enter Mobile Number");
                    } else if (loginNumber.getText().toString().trim().length() != 10) {
                        printMessage("Mobile Number Invalid");
                    } else if (thumbnail == null) {
                        printMessage("Please Upload Image");
                    } else {
                        callDialog();
                    }
                } else {
                    printMessage("No Internet Connection");
                }
                break;
            case R.id.allready_registered:
                callRegistedDialog();
                break;
        }
    }


    private void callRegistedDialog() {

        final Dialog dialog = new Dialog(Activity_Login.this);
        dialog.setContentView(R.layout.view_already_registered);
        ButterKnife.bind(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final TimeZone tz = TimeZone.getDefault();

        final EditText mobileNumber = (EditText) dialog.findViewById(R.id.loginMobileNumber);
        final EditText loginotp = (EditText) dialog.findViewById(R.id.loginotp);
        Button loginOTP = (Button) dialog.findViewById(R.id.loginSendOTP);
        final Button ok_button = (Button) dialog.findViewById(R.id.ok_button);
        final RelativeLayout mobile_layout = (RelativeLayout) dialog.findViewById(R.id.mobile_layout);
        final RelativeLayout otp_layout = (RelativeLayout) dialog.findViewById(R.id.otp_layout);


        loginOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mobileNumber.getText().toString().trim().isEmpty()) {
                    printMessage("Please Enter Mobile Number");
                } else if (mobileNumber.getText().toString().trim().length() != 10) {
                    printMessage("Please Enter Correct Mobile Number");
                } else {
                    mobile_layout.setVisibility(View.GONE);
                    otp_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginotp.getText().toString().isEmpty()) {
                    printMessage("Please Enter OTP");
                } else if (loginotp.getText().toString().trim().equals("1234")) {
                    if (InternetCheck()) {
                        printMessage("OTP Matched");
                        callurl(mobileNumber.getText().toString().trim());

                        Log.d("sarvesh---",tz.getDisplayName(false, TimeZone.SHORT));


                    } else {
                        printMessage("No Internet Connection");
                    }
                } else {
                    printMessage("Please Enter Correct OTP");
                }
            }
        });


    }

    private void callDialog() {

        final Dialog dialog = new Dialog(Activity_Login.this);
        dialog.setContentView(R.layout.view_otp_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        final EditText otp = (EditText) dialog.findViewById(R.id.login_OTP);
        Button submit = (Button) dialog.findViewById(R.id.login_submit);
        Button reset = (Button) dialog.findViewById(R.id.login_reset);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUsername.setText("");
                loginNumber.setText("");
                thumbnail = null;
                userImage.setImageResource(R.drawable.usericon);
                dialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (otp.getText().toString().isEmpty()) {
                    printMessage("Please Enter OTP");
                } else if (otp.getText().toString().trim().equals("1234")) {
                    printMessage("OTP Matched");
                    dialog.dismiss();
                    uploadBitmap(thumbnail);
                } else {
                    printMessage("Please Enter Correct OTP");
                }
            }
        });
    }

    public void callurl(final String mobile) {
        progressDialog = new ProgressDialog(Activity_Login.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();

        final TimeZone tz = TimeZone.getDefault();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                Utils.url + "checklogin?", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {

                try {
                    JSONObject jsonObject = new JSONObject(new String(response.data));

                    if (jsonObject.getString("status").equals("1")) {

                        printMessage("Login Sucessfully");
                        progressDialog.dismiss();
                        Utils.setUserToken(getApplicationContext(), jsonObject.getString("token"));
                        Utils.setCurrentUserName(getApplicationContext(), jsonObject.getString("user_name"));
                        Utils.setCurrentUserNumber(getApplicationContext(), jsonObject.getString("mobile_number"));
                        Utils.setUserId(getApplicationContext(), jsonObject.getString("user_id"));
                        startActivity(new Intent(Activity_Login.this, Activity_Lending.class));
                        finish();
                    } else if (jsonObject.getString("status").equals("0")) {
                        progressDialog.dismiss();
                        printMessage("Login Unsucessfull");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_number", mobile);
                params.put("fcm_token", Utils.getFCMToken(getApplicationContext()));
                params.put("time_zone", tz.getDisplayName(false, TimeZone.SHORT));

                Log.d("sarvesh---",tz.getDisplayName(false, TimeZone.SHORT));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    // Image Processing....

    private void callImageDialog() {

        dialog = new Dialog(Activity_Login.this);
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
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        destination = new File(Environment.getExternalStorageDirectory(),
                "Img_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Glide.with(getApplicationContext())
                .load(bytes.toByteArray()).asBitmap().centerCrop().skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).
                into(new BitmapImageViewTarget(userImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userImage.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

    private void onSelectFromGalleryResult(Intent data) {
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
        destination = new File(Environment.getExternalStorageDirectory(),
                "Img_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Glide.with(getApplicationContext())
                .load(bytes.toByteArray()).asBitmap().centerCrop().skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESULT).
                into(new BitmapImageViewTarget(userImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userImage.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

    private void uploadBitmap(final Bitmap bitmap) {

        progressDialog = new ProgressDialog(Activity_Login.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait...");
        progressDialog.show();

        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final TimeZone tz1 = TimeZone.getDefault();

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,
                Utils.url + "user_registration?",

                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progressDialog.dismiss();

                        Log.d("sarvesh----", new String(response.data));
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));

                            if (obj.getInt("status") == 1) {
                                Utils.setUserId(getApplicationContext(), obj.getString("user_id"));
                                Utils.setCurrentUserName(getApplicationContext(), obj.getString("user_name"));
                                Utils.setCurrentUserNumber(getApplicationContext(), obj.getString("mobile_number"));
                                Utils.setUserToken(getApplicationContext(), obj.getString("token"));
                                printMessage("Login Sucessfully");
                                startActivity(new Intent(Activity_Login.this, Activity_Lending.class));
                                finish();
                            } else if (obj.getInt("status") == 0) {
                                printMessage("Mobile Number Already Registered");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        progressDialog.dismiss();
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
                }) {


            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", loginUsername.getText().toString().trim());
                params.put("mobile_number", loginNumber.getText().toString().trim());
                params.put("device_id", android_id);
                params.put("device_type", "Android");
                params.put("fcm_token", Utils.getFCMToken(getApplicationContext()));
                params.put("time_zone", tz1.getDisplayName(false, TimeZone.SHORT));
                Log.d("sarvesh---",tz1.getDisplayName(false, TimeZone.SHORT));
                return params;
            }


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("user_image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //  Print Toast Message....
    private void printMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public boolean InternetCheck() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;
        return connected;
    }

}



