package com.momagic;

public interface NotificationHelperListener {
    void onNotificationReceived(Payload payload);
    void onNotificationOpened(String data);

}
