package com.example.forebear.treschattingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.forebear.treschattingapp.Models.DTO_User;

import java.util.ArrayList;

/**
 * Created by forebear on 20/2/18.
 */

public class Adapter_Lending extends BaseAdapter {

    ArrayList<DTO_User> usersList;
    TextView no_of_msg;
    TextView user_name;
    TextView user_number;
    ImageView user_image;
    Context mContext;
    int ActiveToken;
    private static LayoutInflater inflater=null;

    public Adapter_Lending(ArrayList<DTO_User> usersList, Context context,int token ) {
        this.usersList = usersList;
        this.mContext = context;
        this.ActiveToken = token;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {


        View itemView = inflater.inflate(R.layout.view_listview,  null);

        no_of_msg = (TextView) itemView.findViewById(R.id.no_of_msg);
        user_name = (TextView) itemView.findViewById(R.id.user_name);
        user_number = (TextView) itemView.findViewById(R.id.user_number);
        user_image = (ImageView) itemView.findViewById(R.id.user_image);

        if (ActiveToken == 0){
            no_of_msg.setBackgroundResource(R.drawable.active_user);
        }else if (ActiveToken == 1){
            no_of_msg.setBackgroundResource(R.drawable.in_active_user);
        }

        if (!usersList.get(position).getActive_mage().equals("null") ){
            no_of_msg.setText(usersList.get(position).getActive_mage());
        }

        user_name.setTag(position);
        user_number.setTag(position);

        user_name.setText(usersList.get(position).getName());
        user_number.setText(usersList.get(position).getNumber());

        if (usersList.get(position).getImage().length() == 0L){
            user_image.setImageResource(R.drawable.usericon);
        }else {
            Glide.with(mContext)
                    .load(usersList.get(position).getImage()).asBitmap().centerCrop().skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT).
                    into(new BitmapImageViewTarget(user_image) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            user_image.setImageDrawable(circularBitmapDrawable);
                        }
                    });
        }

        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callcommonMthod(Integer.parseInt(v.getTag().toString()));
            }
        });

        user_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callcommonMthod(Integer.parseInt(v.getTag().toString()));
            }
        });

        return itemView;
    }

    private void callcommonMthod(int i) {

        Intent intent = new Intent(mContext.getApplicationContext(),Activity_Chat.class);
        intent.putExtra("regid",usersList.get(i).getId());
        intent.putExtra("name",usersList.get(i).getName());
        intent.putExtra("number",usersList.get(i).getNumber());
        intent.putExtra("image",usersList.get(i).getImage());
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }


}
