package com.momagic;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

/* Developed By Amit Gupta */
public class FCMTokenGenerator implements TokenGenerator {

    static FirebaseApp firebaseApp;

    @Override
    public void getToken(final Context context, final String senderId, final TokenGenerationHandler callback) {
        if (context == null)
            return;
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
        if (preferenceUtil.getBoolean(AppConstant.CAN_GENERATE_FCM_TOKEN)) {
            if (callback != null)
                callback.complete(preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initFireBaseApp(senderId);
                    FirebaseMessaging messageApp = firebaseApp.get(FirebaseMessaging.class);
                    messageApp.getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {

                                    try {
                                        if (!task.isSuccessful()) {
                                            return;
                                        }
                                        String token = task.getResult();
                                        if (token != null && !token.isEmpty()) {
                                            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
                                            if (!token.equals(preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN)) || !AppConstant.SDK_VERSION.equals(preferenceUtil.getStringData(AppConstant.CHECK_SDK_UPDATE))
                                                    || !preferenceUtil.getStringData(AppConstant.CHECK_APP_VERSION).equalsIgnoreCase(Util.getAppVersion(context))) {
                                                preferenceUtil.setBooleanData(AppConstant.IS_TOKEN_UPDATED, false);
                                                preferenceUtil.setStringData(AppConstant.CHECK_APP_VERSION, Util.getAppVersion(context));
                                                preferenceUtil.setStringData(AppConstant.CHECK_SDK_UPDATE, AppConstant.SDK_VERSION);
                                            }
                                            preferenceUtil.setStringData(AppConstant.FCM_DEVICE_TOKEN, token);
                                            if (callback != null)
                                                callback.complete(token);
                                        } else {
                                            callback.failure(AppConstant.FCM_ERROR);
                                        }

                                    } catch (Exception e) {
                                        Util.handleExceptionOnce(context, e.toString(), "FCMTokenGenerator", "getToken");
                                        if (callback != null)
                                            callback.failure(e.getMessage());
                                    }
                                }
                            });

                } catch (Exception e) {
                    Util.handleExceptionOnce(context, e.toString(), "FCMTokenGenerator", "getToken");
                    if (callback != null)
                        callback.failure(e.getMessage());
                }
            }
        }).start();

    }

    void removeDeviceAddress(Context context, String senderId) {
        try {
            initFireBaseApp(senderId);
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            FirebaseMessaging messageApp = firebaseApp.get(FirebaseMessaging.class);
            messageApp.deleteToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN) != null && !preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty()) {
                                preferenceUtil.setStringData(AppConstant.FCM_DEVICE_TOKEN, null);
                                firebaseApp.delete();
                            }
                        } else {
                            Log.v("token", "delete failed!");
                        }
                    });
        } catch (Exception e) {
            Util.handleExceptionOnce(context, e.toString(), "FCMTokenGenerator", "removeDeviceAddress");
        }
    }

    void initFireBaseApp(final String senderId) {
        if (firebaseApp != null)
            return;

        if (get_Project_ID() != "" && getAPI_KEY() != "" && senderId != "") {
            FirebaseOptions firebaseOptions =
                    new FirebaseOptions.Builder()
                            .setGcmSenderId(senderId) //senderID
                            .setApplicationId(get_App_ID()) //application ID
                            .setApiKey(getAPI_KEY()) //Application Key
                            .setProjectId(get_Project_ID()) //Project ID
                            .build();
            firebaseApp = FirebaseApp.initializeApp(DATB.appContext, firebaseOptions, AppConstant.SDK_NAME);
            Lg.d(AppConstant.FCM_NAME, firebaseApp.getName());
        } else {
            Log.w(AppConstant.APP_NAME_TAG, AppConstant.IZ_MISSING_GOOGLE_JSON_SERVICES_FILE);
        }
    }

    private static String getAPI_KEY() {
        if (DATB.appContext != null) {
            try {
                String apiKey = FirebaseOptions.fromResource(DATB.appContext).getApiKey();
                if (apiKey != null)
                    return apiKey;
            } catch (Exception e) {
                Util.handleExceptionOnce(DATB.appContext, e.toString(), "FCMTokenGenerator", "getAPiKey");
                return "";

            }
        }
        return "";
    }

    private static String get_App_ID() {
        if (DATB.appContext != null) {
            try {
                String application_id = FirebaseOptions.fromResource(DATB.appContext).getApplicationId();
                if (application_id != null)
                    return application_id;
            } catch (Exception ex) {
                Util.handleExceptionOnce(DATB.appContext, ex.toString(), "FCMTokenGeneration", "getAppID");
                return "";  //FCM_DEFAULT_APP_ID;
            }
        }
        return "";

    }

    private static String get_Project_ID() {
        if (DATB.appContext != null) {
            try {
                String project_id = FirebaseOptions.fromResource(DATB.appContext).getProjectId();
                if (project_id != null)
                    return project_id;
            } catch (Exception exception) {
                Util.handleExceptionOnce(DATB.appContext, exception.toString(), "FCMTokenGenerator", "getProjectID");
                return "";

            }
        }
        return "";

    }
}
