package com.example.forebear.treschattingapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.forebear.treschattingapp.Models.DTO_chat;
import com.example.forebear.treschattingapp.Models.Utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by forebear on 24/2/18.
 */

public class Adapter_Chat extends BaseAdapter {

    Context context;
    List<DTO_chat> dto_chatList;
    private static LayoutInflater inflater = null;
    String clientName;
    String currentUSer;
    String clientID;

    public Adapter_Chat(Context context, List<DTO_chat> dto_chatList,
                        String clientName, String currentUSer,
                        String clientID) {
        this.context = context;
        this.dto_chatList = dto_chatList;
        this.clientName = clientName;
        this.currentUSer = currentUSer;
        this.clientID = clientID;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dto_chatList.size();
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
        View itemView = null;
        DTO_chat chat = dto_chatList.get(position);

        if (chat.getSENDER() == 0) {
            itemView = inflater.inflate(R.layout.view_send, null);

            TextView time_Send = (TextView) itemView.findViewById(R.id.time_Send);
            TextView send_header = (TextView) itemView.findViewById(R.id.sender_header);
            TextView sendMessage = (TextView) itemView.findViewById(R.id.send_messsage);
            ImageView sendImage = (ImageView) itemView.findViewById(R.id.send_image);

            send_header.setText(clientName);
            time_Send.setText(chat.getTIME_STAMP());
            sendMessage.setText(chat.getMSG());

            if (chat.getIMAGE().length() != 0) {
                sendImage.setVisibility(View.VISIBLE);
                sendMessage.setVisibility(View.GONE);
                Log.d("sarvesh---",chat.getIMAGE());
                Glide.with(context).load(chat.getIMAGE()).into(sendImage);
            }
        } else  if (chat.getSENDER() == 1) {
            itemView = inflater.inflate(R.layout.view_recive, null);

            TextView time_recive = (TextView) itemView.findViewById(R.id.time_recive);
            TextView recive_header = (TextView) itemView.findViewById(R.id.reciver_header);
            TextView reciveMessage = (TextView) itemView.findViewById(R.id.recive_message);
            ImageView reciveImage = (ImageView) itemView.findViewById(R.id.recive_image);

            recive_header.setText(currentUSer);
            time_recive.setText(chat.getTIME_STAMP());
            reciveMessage.setText(chat.getMSG());


            if (dto_chatList.get(position).getIMAGE().length() != 0) {
                reciveImage.setVisibility(View.VISIBLE);
                reciveMessage.setVisibility(View.GONE);
                Log.d("sarvesh---",chat.getIMAGE());
                Glide.with(context).load(chat.getIMAGE()).into(reciveImage);
            }
        }

        return itemView;
    }

}
