package com.momagic;

public interface PayloadHandler {
    void onReceivedPayload(String jsonPayload);
}
