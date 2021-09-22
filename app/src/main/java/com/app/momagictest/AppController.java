package com.app.momagictest;

import android.app.Application;
import android.util.Log;

import com.momagic.DATB;
import com.momagic.NotificationHelperListener;
import com.momagic.Payload;
import com.momagic.TokenReceivedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppController extends Application implements TokenReceivedListener,NotificationHelperListener

{

    @Override
    public void onCreate() {
        super.onCreate();

        DATB.initialize(getApplicationContext())
                .setNotificationReceiveListener(this)
                .setTokenReceivedListener(this)
                .build();

    HashMap<String,Object> data = new HashMap<>();
    data.put("language","Marathi");
   // DATB.addUserProperty(data);
      //  DATB.setDefaultTemplate(PushTemplate.DEFAULT);



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





}