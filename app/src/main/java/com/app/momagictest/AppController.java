package com.app.momagictest;

import android.app.Application;
import android.util.Log;
import com.momagic.DATB;
import com.momagic.NotificationHelperListener;
import com.momagic.Payload;
import com.momagic.PayloadHandler;
import com.momagic.TokenReceivedListener;


public class AppController extends Application implements TokenReceivedListener,NotificationHelperListener, PayloadHandler

{

    @Override
    public void onCreate() {
        super.onCreate();
        DATB.initialize(this)
                .setTokenReceivedListener(this)
                .setNotificationReceiveListener(this)
                .build();
        DATB.promptForPushNotifications();
    }
    @Override
    public void onTokenReceived(String token) {
        Log.e("Token",token);
    }
    @Override
    public void onNotificationReceived(Payload payload) {
        Log.e("Payload",payload.getMakeStickyNotification());
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