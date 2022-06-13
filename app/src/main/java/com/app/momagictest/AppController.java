package com.app.momagictest;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.momagic.DATB;
import com.momagic.NotificationHelperListener;
import com.momagic.Payload;
import com.momagic.PayloadHandler;
import com.momagic.PushTemplate;
import com.momagic.TokenReceivedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppController extends Application implements TokenReceivedListener,NotificationHelperListener, PayloadHandler

{

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate() {
        super.onCreate();
        DATB.initialize(this)
                .setNotificationReceiveListener(this)
                .build();
        DATB.setDefaultTemplate(PushTemplate.DEFAULT);


    }

    @Override
    public void onTokenReceived(String token) {
        Log.e("Token",token);

    }

    @Override
    public void onNotificationReceived(Payload payload) {
        Log.e("Payload",payload.getTitle());

    }

    @Override
    public void onNotificationOpened(String data) {
     Log.e("Data",data);
    }


    @Override
    public void onReceivedPayload(String jsonPayload) {
        Log.e("PayloadData",jsonPayload);
    }
}