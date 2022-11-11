package com.momagic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.momagic.shortcutbadger.ShortcutBadger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import static com.momagic.AppConstant.APPPID;
import static com.momagic.AppConstant.FCM_TOKEN_FROM_JSON;
import static com.momagic.AppConstant.HUAWEI_TOKEN_FROM_JSON;
import static com.momagic.AppConstant.TAG;
import static com.momagic.AppConstant.XIAOMI_TOKEN_FROM_JSON;

public class DATB {
    static Context appContext;
    private static String senderId;
    public static String mAppId;
    public static Builder mBuilder;
    public static int icon;
    private static Payload payload;
    public static boolean mUnsubscribeWhenNotificationsAreDisabled;
    protected static Listener mListener;
    protected static Handler mHandler;
    private static FirebaseAnalyticsTrack firebaseAnalyticsTrack;
    public static String inAppOption;
    @SuppressLint("StaticFieldLeak")
    static Activity curActivity;
    public static String SDKDEF ="momagic-sdk";
    public static String soundID;
    public static int bannerImage;
    private static boolean initCompleted;
    static boolean isInitCompleted() {
        return initCompleted;
    }
    private static OSTaskManager osTaskManager = new OSTaskManager();
    public static void setSenderId(String senderId) {
        DATB.senderId = senderId;
    }
    private static void setActivity(Activity activity){
        curActivity = activity;
    }
    public static DATB.Builder initialize(Context context) {
        return new DATB.Builder(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void init(Builder builder) {
        final Context context = builder.mContext;
        appContext = context.getApplicationContext();
        mBuilder = builder;
        builder.mContext = null;
        final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appInfo.metaData;
            if (bundle != null) {
                if (bundle.containsKey(AppConstant.DATAB_APP_ID)) {
                    mAppId = bundle.getString(AppConstant.DATAB_APP_ID);
                    preferenceUtil.setStringData(AppConstant.ENCRYPTED_PID,mAppId);
                }
                if (mAppId =="") {
                    Lg.e(AppConstant.APP_NAME_TAG, AppConstant.MISSINGID);
                }
                else {
                    Lg.i(AppConstant.APP_NAME_TAG, mAppId + "");


                    RestClient.get(AppConstant.GOOGLE_JSON_URL + mAppId +".dat", new RestClient.ResponseHandler() {
                        @Override
                        void onFailure(int statusCode, String response, Throwable throwable) {
                            super.onFailure(statusCode, response, throwable);
                        }

                        @Override
                        void onSuccess(String response) {
                            super.onSuccess(response);
                            if (!response.isEmpty() && response.length() > 20 && response != null) {
                                try {
                                    final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
                                    JSONObject jsonObject = new JSONObject(Objects.requireNonNull(Util.decrypt(AppConstant.SECRETKEY, response)));
                                    senderId = jsonObject.getString(AppConstant.SENDERID);
                                    String appId = jsonObject.getString(AppConstant.APPID);
                                    String apiKey = jsonObject.getString(AppConstant.APIKEY);
                                    String mKey = jsonObject.optString(AppConstant.MIAPIKEY);
                                    String mId = jsonObject.optString(AppConstant.MIAPPID);
                                    mAppId = jsonObject.getString(AppConstant.APPPID);
                                    preferenceUtil.setDataBID(AppConstant.APPPID, mAppId);
                                    trackAdvertisingId();
                                    if (!mKey.isEmpty() && !mId.isEmpty() && Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                                        XiaomiSDKHandler xiaomiSDKHandler = new XiaomiSDKHandler(DATB.appContext, mId, mKey);
                                        xiaomiSDKHandler.onMIToken();
                                    }

                                    if (senderId != null && !senderId.isEmpty()) {
                                        init(context, apiKey, appId);
                                    } else {
                                        Lg.e(AppConstant.APP_NAME_TAG, appContext.getString(R.string.something_wrong_fcm_sender_id));
                                    }
//
                                    if ( mAppId!= null && preferenceUtil.getBoolean(AppConstant.IS_CONSENT_STORED)) {
                                        preferenceUtil.setIntData(AppConstant.CAN_STORED_QUEUE, 1);
                                    }
                                    

                                } catch (JSONException e) {
                                    if (context != null) {
                                        DebugFileManager.createExternalStoragePublic(context,e.toString(),"[Log.e]-->init");

                                        Util.setException(context, e.toString(), "init", AppConstant.APP_NAME_TAG);
                                    }
                                }
                            }
                            else
                            {
                               DebugFileManager.createExternalStoragePublic(context,"Account id is not sync properly on panel","[Log.e]-->");
                                Log.e(AppConstant.APP_NAME_TAG,"Account id is not sync properly on panel");

                            }
                        }
                    });

                }
            } else {
                Lg.e(AppConstant.APP_NAME_TAG, AppConstant.MESSAGE);
                DebugFileManager.createExternalStoragePublic(context,AppConstant.MESSAGE,"[Log.e]-->");

            }


        } catch (Throwable t) {
            DebugFileManager.createExternalStoragePublic(context,t.toString(),"[Log.e]-->initBuilder");
            Util.setException(appContext, t.toString(), AppConstant.APP_NAME_TAG, "initBuilder");
        }
    }

    private static void init(final Context context, String apiKey, String appId) {
        if(context!=null) {

            try {
                FCMTokenGenerator fcmTokenGenerator = new FCMTokenGenerator();
                fcmTokenGenerator.getToken(context, senderId, apiKey, appId, new TokenGenerator.TokenGenerationHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void complete(String id) {
                        Util util = new Util();
                        final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
                        if (util.isInitializationValid()) {
                            Lg.i(AppConstant.APP_NAME_TAG, AppConstant.DEVICETOKEN + preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                            registerToken();
                            ActivityLifecycleListener.registerActivity((Application) appContext);
                            setCurActivity(context);
                            areNotificationsEnabledForSubscribedState(appContext);
                            if (FirebaseAnalyticsTrack.canFirebaseAnalyticsTrack()) {
                                firebaseAnalyticsTrack = new FirebaseAnalyticsTrack(appContext);
                            }
                            initCompleted = true;
                            osTaskManager.startPendingTasks();
                        }
                    }

                    @Override
                    public void failure(String errorMsg) {
                        Lg.e(AppConstant.APP_NAME_TAG, errorMsg);
                    }
                });
            } catch (Exception ex) {
                Util.setException(appContext, ex.toString(), AppConstant.APP_NAME_TAG, "init");
            }
        }

    }

    private static void trackAdvertisingId(){
        final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
        AdvertisingIdClient.getAdvertisingId(appContext, new AdvertisingIdClient.Listener() {
            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {
                String advertisementID;
                advertisementID = adInfo.getId();
                preferenceUtil.setStringData(AppConstant.ADVERTISING_ID,advertisementID);
                invokeFinish(advertisementID,preferenceUtil.getStringData(AppConstant.ENCRYPTED_PID));

            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {
                invokeFail(new Exception(TAG + " - Error: context null"));
            }
        });
    }

    public static synchronized void idsAvailable(Context context, Listener listener) {
        new DATB().start(context, listener);
    }

    protected void start(final Context context, final Listener listener) {
        if (listener == null) {
            Log.v(AppConstant.APP_NAME_TAG, "getAdvertisingId - Error: null listener, dropping call");
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            mListener = listener;
            if (context == null) {
                invokeFail(new Exception(TAG + " - Error: context null"));
            } else {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
                        invokeFinish(preferenceUtil.getStringData(AppConstant.ADVERTISING_ID),preferenceUtil.getStringData(AppConstant.ENCRYPTED_PID));
                    }
                }).start();
            }
        }
    }


    public interface Listener {

        void idsAvailable(String adverID,String registrationID);

        void onAdvertisingIdClientFail(Exception exception);
    }


    protected static void invokeFinish(final String adverID, final String registrationID) {
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {

                if (mListener != null) {
                    mListener.idsAvailable(adverID,registrationID);
                }
            }
        });
    }

    protected static void invokeFail(final Exception exception) {
        mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onAdvertisingIdClientFail(exception);
                }
            }
        });
    }

 // method for Notification sound
    public static void  setNotificationSound(String soundName)
    {
        soundID = soundName;
    }


    private static void registerToken() {
        if(appContext!=null) {

            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
            if (preferenceUtil.getDataBID(APPPID) != null && !preferenceUtil.getDataBID(APPPID).isEmpty()) {
                if (!preferenceUtil.getBoolean(AppConstant.IS_TOKEN_UPDATED)) {
                    if (!preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                        preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 3);
                    } else if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                        preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 2);
                    } else if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty()) {
                        preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 2);
                    } else {
                        preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 1);
                    }
                    try {
                        if (!preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty())
                            preferenceUtil.setBooleanData(AppConstant.IS_UPDATED_HMS_TOKEN, true);
                        if (!preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty())
                            preferenceUtil.setBooleanData(AppConstant.IS_UPDATED_XIAOMI_TOKEN, true);
                        Map<String, String> mapData = new HashMap<>();
                        mapData.put(AppConstant.ADDURL, "" + AppConstant.STYPE);
                        mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                        mapData.put(AppConstant.BTYPE_, "" + AppConstant.BTYPE);
                        mapData.put(AppConstant.DTYPE_, "" + AppConstant.DTYPE);
                        mapData.put(AppConstant.TIMEZONE, "" + System.currentTimeMillis());
                        mapData.put(AppConstant.APPVERSION, "" + Util.getAppVersion(appContext));
                        mapData.put(AppConstant.OS, "" + AppConstant.SDKOS);
                        mapData.put(AppConstant.ALLOWED_, "" + AppConstant.ALLOWED);
                        mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(appContext));
                        mapData.put(AppConstant.CHECKSDKVERSION, "" + AppConstant.SDK_VERSION);
                        mapData.put(AppConstant.LANGUAGE, "" + Util.getDeviceLanguage());
                        mapData.put(AppConstant.QSDK_VERSION, "" + AppConstant.SDK_VERSION);
                        mapData.put(AppConstant.TOKEN, "" + preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                        mapData.put(AppConstant.ADVERTISEMENTID, "" + preferenceUtil.getStringData(AppConstant.ADVERTISING_ID));
                        mapData.put(AppConstant.XIAOMITOKEN, "" + preferenceUtil.getStringData(AppConstant.XiaomiToken));
                        mapData.put(AppConstant.PACKAGE_NAME, "" + appContext.getPackageName());
                        mapData.put(AppConstant.SDKTYPE, "" + SDKDEF);
                        mapData.put(AppConstant.KEY_HMS, "" + preferenceUtil.getStringData(AppConstant.HMS_TOKEN));
                        mapData.put(AppConstant.ANDROIDVERSION, "" + Build.VERSION.RELEASE);
                        mapData.put(AppConstant.DEVICENAME, "" + Util.getDeviceName());
                        RestClient.postRequest(RestClient.MOMAGIC_SUBSCRIPTION_URL, mapData, null, new RestClient.ResponseHandler() {
                            @Override
                            void onSuccess(final String response) {
                                super.onSuccess(response);
                            }

                            @Override
                            void onFailure(int statusCode, String response, Throwable throwable) {
                                super.onFailure(statusCode, response, throwable);

                            }
                        });


                        RestClient.postRequest(RestClient.BASE_URL, mapData, null, new RestClient.ResponseHandler() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            void onSuccess(final String response) {
                                super.onSuccess(response);
                                lastVisitApi(appContext);

                                if (mBuilder != null && mBuilder.mTokenReceivedListener != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put(FCM_TOKEN_FROM_JSON, preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                                                jsonObject.put(XIAOMI_TOKEN_FROM_JSON, preferenceUtil.getStringData(AppConstant.XiaomiToken));
                                                jsonObject.put(HUAWEI_TOKEN_FROM_JSON, preferenceUtil.getStringData(AppConstant.HMS_TOKEN));
                                                mBuilder.mTokenReceivedListener.onTokenReceived(jsonObject.toString());
                                            } catch (Exception ex) {
                                                DebugFileManager.createExternalStoragePublic(appContext,ex.toString(),"[Log.e]->");
                                            }

                                            // mBuilder.mTokenReceivedListener.onTokenReceived(preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                                        }
                                    });
                                }
                                preferenceUtil.setBooleanData(AppConstant.IS_TOKEN_UPDATED, true);
                                preferenceUtil.setLongData(AppConstant.DEVICE_REGISTRATION_TIMESTAMP, System.currentTimeMillis());
                                areNotificationsEnabledForSubscribedState(appContext);

                                try {
                                    preferenceUtil.setBooleanData(AppConstant.IS_CONSENT_STORED, true);
                                    preferenceUtil.setIntData(AppConstant.CAN_STORED_QUEUE, 1);

                                    if (!preferenceUtil.getStringData(AppConstant.USER_LOCAL_DATA).isEmpty()) {
                                        Util.sleepTime(5000);
                                        JSONObject json = new JSONObject(preferenceUtil.getStringData(AppConstant.USER_LOCAL_DATA));
                                        addUserProperty(Util.toMap(json));
                                    }
                                    if (!preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EN).isEmpty() && !preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EV).isEmpty()) {
                                        JSONObject json = new JSONObject(preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EV));
                                        addEvent(preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EN), Util.toMap(json));
                                    }
                                    if (preferenceUtil.getBoolean(AppConstant.IS_SET_SUBSCRIPTION_METHOD))
                                        setSubscription(preferenceUtil.getBoolean(AppConstant.SET_SUBSCRITION_LOCAL_DATA));


                                    if (!preferenceUtil.getStringData(AppConstant.SUBSCRIBER_ID_DATA).isEmpty()) {
                                        DATB.setSubscriberID(preferenceUtil.getStringData(AppConstant.SUBSCRIBER_ID_DATA));
                                    }
                                    if (!preferenceUtil.getStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE).isEmpty()) {
                                        JSONArray jsonArray  = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE));
                                        topicApi(AppConstant.ADD_TOPIC, (List) Util.toList(jsonArray));
                                    }
                                    if (!preferenceUtil.getStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE).isEmpty()) {
                                        JSONArray jsonArray  = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE));
                                        topicApi(AppConstant.REMOVE_TOPIC, (List) Util.toList(jsonArray));
                                    }

                                } catch (Exception e) {
                                    Util.setException(appContext, e.toString(), "registerToken1", AppConstant.APP_NAME_TAG);
                                }
                            }

                            @Override
                            void onFailure(int statusCode, String response, Throwable throwable) {
                                super.onFailure(statusCode, response, throwable);
                            }
                        });


                    } catch (Exception exception) {
                        Util.setException(appContext, exception.toString(), AppConstant.APP_NAME_TAG, "registerToken");
                    }
                } else {

                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            lastVisitApi(appContext);
                        }
                        areNotificationsEnabledForSubscribedState(appContext);
                        if (mBuilder != null && mBuilder.mTokenReceivedListener != null) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put(FCM_TOKEN_FROM_JSON, preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                                jsonObject.put(XIAOMI_TOKEN_FROM_JSON, preferenceUtil.getStringData(AppConstant.XiaomiToken));
                                jsonObject.put(HUAWEI_TOKEN_FROM_JSON, preferenceUtil.getStringData(AppConstant.HMS_TOKEN));
                                mBuilder.mTokenReceivedListener.onTokenReceived(jsonObject.toString());

                            } catch (Exception ex) {
                                Util.setException(appContext, ex.toString(), AppConstant.APP_NAME_TAG, "registerToken");

                                DebugFileManager.createExternalStoragePublic(appContext,ex.toString(),"[Log.e]->");
                            }
                        }
                        if (!preferenceUtil.getStringData(AppConstant.USER_LOCAL_DATA).isEmpty()) {
                            JSONObject json = new JSONObject(preferenceUtil.getStringData(AppConstant.USER_LOCAL_DATA));
                            addUserProperty(Util.toMap(json));
                        }
                        if (!preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EN).isEmpty() && !preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EV).isEmpty()) {
                            JSONObject json = new JSONObject(preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EV));
                            addEvent(preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EN), Util.toMap(json));
                        }
                        if (preferenceUtil.getBoolean(AppConstant.IS_SET_SUBSCRIPTION_METHOD))
                            setSubscription(preferenceUtil.getBoolean(AppConstant.SET_SUBSCRITION_LOCAL_DATA));
                        if (!preferenceUtil.getStringData(AppConstant.SUBSCRIBER_ID_DATA).isEmpty()) {
                            DATB.setSubscriberID(preferenceUtil.getStringData(AppConstant.SUBSCRIBER_ID_DATA));
                        }
                        if (!preferenceUtil.getStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE).isEmpty()) {
                            JSONArray jsonArray  = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE));
                            topicApi(AppConstant.ADD_TOPIC, (List) Util.toList(jsonArray));
                        }
                        if (!preferenceUtil.getStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE).isEmpty()) {
                            JSONArray jsonArray  = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE));
                            topicApi(AppConstant.REMOVE_TOPIC, (List) Util.toList(jsonArray));
                        }
                        if(!preferenceUtil.getBoolean(AppConstant.FILE_EXIST)) {
                            try{

                                Map<String, String> mapData = new HashMap<>();
                                mapData.put(AppConstant.ADDURL, "" + AppConstant.STYPE);
                                mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                                mapData.put(AppConstant.BTYPE_, "" + AppConstant.BTYPE);
                                mapData.put(AppConstant.DTYPE_, "" + AppConstant.DTYPE);
                                mapData.put(AppConstant.TIMEZONE, "" + System.currentTimeMillis());
                                mapData.put(AppConstant.APPVERSION, "" + Util.getAppVersion(appContext));
                                mapData.put(AppConstant.OS, "" + AppConstant.SDKOS);
                                mapData.put(AppConstant.ALLOWED_, "" + AppConstant.ALLOWED);
                                mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(appContext));
                                mapData.put(AppConstant.CHECKSDKVERSION, "" + AppConstant.SDK_VERSION);
                                mapData.put(AppConstant.LANGUAGE, "" + Util.getDeviceLanguage());
                                mapData.put(AppConstant.QSDK_VERSION, "" + AppConstant.SDK_VERSION);
                                mapData.put(AppConstant.TOKEN, "" + preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                                mapData.put(AppConstant.ADVERTISEMENTID, "" + preferenceUtil.getStringData(AppConstant.ADVERTISING_ID));
                                mapData.put(AppConstant.XIAOMITOKEN, "" + preferenceUtil.getStringData(AppConstant.XiaomiToken));
                                mapData.put(AppConstant.PACKAGE_NAME, "" + appContext.getPackageName());
                                mapData.put(AppConstant.SDKTYPE, "" + SDKDEF);
                                mapData.put(AppConstant.KEY_HMS, "" + preferenceUtil.getStringData(AppConstant.HMS_TOKEN));
                                mapData.put(AppConstant.ANDROIDVERSION, "" + Build.VERSION.RELEASE);
                                mapData.put(AppConstant.DEVICENAME, "" + Util.getDeviceName());
                                DebugFileManager.createExternalStoragePublic(DATB.appContext,mapData.toString(),"RegisterToken");

                            }
                            catch (Exception exception)
                            {
                                DebugFileManager.createExternalStoragePublic(DATB.appContext,"RegisterToken -> "+exception.toString(),"[Log.e]->");
                                Util.setException(appContext, exception.toString(), "registerToken", "iZooto");

                            }
                        }


                    } catch (Exception e) {
                        DebugFileManager.createExternalStoragePublic(DATB.appContext,"RegisterToken -> "+"Error","[Log.e]->");
                        Util.setException(appContext, "Register error", "registerToken", "iZooto");
                    }
                }
            }
            else
            {
                Util.setException(DATB.appContext,"Missing pid",AppConstant.APP_NAME_TAG,"Register Token");
            }
        }

    }
    static void onActivityResumed(Activity activity){
        if(appContext!=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
            setActivity(activity);
            if (!preferenceUtil.getBoolean(AppConstant.IS_NOTIFICATION_ID_UPDATED)) {
                if (firebaseAnalyticsTrack != null && preferenceUtil.getBoolean(AppConstant.FIREBASE_ANALYTICS_TRACK)) {
                    firebaseAnalyticsTrack.influenceOpenTrack();
                    preferenceUtil.setBooleanData(AppConstant.IS_NOTIFICATION_ID_UPDATED, true);
                }
            }

            try {
                ShortcutBadger.applyCountOrThrow(appContext, 0);
            } catch (Exception e) {
                DebugFileManager.createExternalStoragePublic(DATB.appContext,e.toString(),"[Log.v]->");

                Util.setException(appContext, e.toString(), AppConstant.APP_NAME_TAG, "onActivityResumed");

            }
        }

    }



    private static void setCurActivity(Context context) {
        boolean foreground = isContextActivity(context);
        if (foreground) {
            DATB.curActivity = (Activity) context;
        }
    }

    private static boolean isContextActivity(Context context) {
        return context instanceof Activity;
    }
    public static void processNotificationReceived(Context context,Payload payload) {
        if(payload!=null) {
                NotificationEventManager.manageNotification(payload);
        }
//        if(context!=null) {
//            sendOfflineDataToServer(context);
//        }
    }

    public static void notificationView(Payload payload)
    {
        final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
        if(payload!=null)
        {
            if(mBuilder!=null && mBuilder.mNotificationHelper!=null)
            {
                mBuilder.mNotificationHelper.onNotificationReceived(payload);
            }
            if (firebaseAnalyticsTrack != null && preferenceUtil.getBoolean(AppConstant.FIREBASE_ANALYTICS_TRACK))
            {
                firebaseAnalyticsTrack.receivedEventTrack(payload);
            }

            if (payload.getId() != null && !payload.getId().isEmpty()) {
                if (!payload.getId().equals(preferenceUtil.getStringData(AppConstant.TRACK_NOTIFICATION_ID))) {
                    preferenceUtil.setBooleanData(AppConstant.IS_NOTIFICATION_ID_UPDATED, false);
                }
                preferenceUtil.setStringData(AppConstant.TRACK_NOTIFICATION_ID, payload.getId());
            }

        }
    }
    public static void notificationActionHandler(String data)
    {
       if(appContext!=null) {
           final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
           if (mBuilder != null && mBuilder.mNotificationHelper != null) {
               mBuilder.mNotificationHelper.onNotificationOpened(data);
           }
           if (firebaseAnalyticsTrack != null && preferenceUtil.getBoolean(AppConstant.FIREBASE_ANALYTICS_TRACK)) {
               firebaseAnalyticsTrack.openedEventTrack();
           }
           try {
               preferenceUtil.setIntData(AppConstant.NOTIFICATION_COUNT, preferenceUtil.getIntData(AppConstant.NOTIFICATION_COUNT) - 1);
               ShortcutBadger.applyCountOrThrow(appContext, preferenceUtil.getIntData(AppConstant.NOTIFICATION_COUNT));
           } catch (Exception e) {
               Util.setException(appContext, e.toString(), AppConstant.APP_NAME_TAG, "notificationActionHandler");
           }
       }


    }
    public static void notificationInAppAction(String url){
        if (mBuilder!=null && mBuilder.mWebViewListener!=null)
            mBuilder.mWebViewListener.onWebView(url);
    }


    public static class Builder {
        Context mContext;
        private TokenReceivedListener mTokenReceivedListener;
        private NotificationHelperListener mNotificationHelper;
        public NotificationWebViewListener mWebViewListener;
        public PayloadHandler mPayloadHandler;
        private Builder(Context context) {
            mContext = context;
        }

        public Builder setTokenReceivedListener(TokenReceivedListener listener) {
            mTokenReceivedListener = listener;
            return this;
        }
        public Builder setNotificationReceiveListener(NotificationHelperListener notificationHelper) {
            mNotificationHelper = notificationHelper;
            return this;
        }


        public Builder setLandingURLListener(NotificationWebViewListener mNotificationWebViewListener){
            mWebViewListener = mNotificationWebViewListener;
            return this;

        }
        public Builder handlePayloadListener(PayloadHandler payloadHandler)
        {
            mPayloadHandler=payloadHandler;
            return this;
        }



        public Builder unsubscribeWhenNotificationsAreDisabled(boolean set){
            mUnsubscribeWhenNotificationsAreDisabled = set;
            return this;
        }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void build() {
            DATB.init(this);
        }

    }


    private static void areNotificationsEnabledForSubscribedState(Context context){
        if(context!=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            if (context != null) {
                int value = 0;
                if (mUnsubscribeWhenNotificationsAreDisabled) {
                    boolean isChecked = Util.isNotificationEnabled(context);
                    if (!isChecked) {
                        value = 2;
                    }
                }
                if (value == 0 && preferenceUtil.getIntData(AppConstant.GET_NOTIFICATION_ENABLED) == 0) {
                    preferenceUtil.setIntData(AppConstant.GET_NOTIFICATION_ENABLED, 1);
                    preferenceUtil.setIntData(AppConstant.GET_NOTIFICATION_DISABLED, 0);
                    getNotificationAPI(context, value);

                } else if (value == 2 && preferenceUtil.getIntData(AppConstant.GET_NOTIFICATION_DISABLED) == 0) {
                    preferenceUtil.setIntData(AppConstant.GET_NOTIFICATION_DISABLED, 1);
                    preferenceUtil.setIntData(AppConstant.GET_NOTIFICATION_ENABLED, 0);
                    getNotificationAPI(context, value);

                }
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void getNotificationAPI(Context context, int value){

        if(context!=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            try {
                if (!preferenceUtil.getDataBID(AppConstant.APPPID).isEmpty() && Util.isNetworkAvailable(context)) {
                    if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                        Map<String, String> mapData = new HashMap<>();
                        mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                        mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(context));
                        mapData.put(AppConstant.BTYPE_, "" + AppConstant.BTYPE);
                        mapData.put(AppConstant.DTYPE_, "" + AppConstant.DTYPE);
                        mapData.put(AppConstant.APPVERSION, "" + AppConstant.SDK_VERSION);
                        mapData.put(AppConstant.PTE_, "" + AppConstant.PTE);
                        mapData.put(AppConstant.OS, "" + AppConstant.SDKOS);
                        mapData.put(AppConstant.PT_, "" + AppConstant.PT);
                        mapData.put(AppConstant.GE_, "" + AppConstant.GE);
                        mapData.put(AppConstant.ACTION, "" + value);

                        RestClient.postRequest(RestClient.SUBSCRIPTION_API, mapData,null, new RestClient.ResponseHandler() {
                            @Override
                            void onSuccess(final String response) {
                                super.onSuccess(response);
                            }

                            @Override
                            void onFailure(int statusCode, String response, Throwable throwable) {
                                super.onFailure(statusCode, response, throwable);
                            }
                        });
                    }
                }
            } catch (Exception ex) {
                DebugFileManager.createExternalStoragePublic(DATB.appContext,ex.toString(),"[Log.v]->getNotificationAPI->");

                Util.setException(context, ex.toString(), AppConstant.APP_NAME_TAG, "getNotificationAPI");
            }
        }

    }







    // send events  with event name and event data
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void addEvent(String eventName, HashMap<String,Object> data) {

        if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.ADD_EVENT) && appContext == null) {
            String finalEventName = eventName;
            osTaskManager.addTaskToQueue(new Runnable() {
                @Override
                public void run() {
                    Log.d(AppConstant.APP_NAME_TAG, "addEvent(): operation from pending task queue.");
                    addEvent(finalEventName, data);
                }
            });
            return;
        }
        if (data != null && eventName != null&&eventName.length()>0&&data.size()>0) {
            eventName = eventName.substring(0, Math.min(eventName.length(), 32)).replace(" ","_");
            HashMap<String, Object>  newListEvent= new HashMap<String, Object>();
            for (Map.Entry<String,Object> refineEntry : data.entrySet()) {
                if (refineEntry.getKey()!=null&&!refineEntry.getKey().isEmpty()){
                    String newKey = refineEntry.getKey().toLowerCase();
                    newListEvent.put(newKey,refineEntry.getValue());
                }
            }
            if (newListEvent.size()>0) {

                addEventAPI(eventName, newListEvent);
            }
            else
            {
                Util.setException(appContext,"Event Name or Event Data are not available",AppConstant.APP_NAME_TAG,"addEvent");
                DebugFileManager.createExternalStoragePublic(DATB.appContext,eventName+"EventData->Event Name or Event Data are not available","[Log.v]->");

            }
        }
        else
        {
            DebugFileManager.createExternalStoragePublic(DATB.appContext,eventName+"EventData->Event Name or Event Data are not available","[Log.v]->");
            Util.setException(appContext,"Event Name or Event Data are not available",AppConstant.APP_NAME_TAG,"addEvent");
        }
    }
    public static void setSubscriberID(String subscriberID)
    {
        if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.SET_SUBSCRIBER_ID) && appContext == null) {
            osTaskManager.addTaskToQueue(new Runnable() {
                @Override
                public void run() {
                    Log.d(AppConstant.APP_NAME_TAG, "SET_SUBSCRIBER_ID(): operation from pending task queue.");
                    setSubscriberID(subscriberID);
                }
            });
            return;
        }

       if(appContext!=null) {
           final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
           if (!preferenceUtil.getStringData(AppConstant.SAVESUBID).equalsIgnoreCase(subscriberID)) {
               preferenceUtil.setStringData(AppConstant.SAVESUBID, subscriberID);
               if (subscriberID != null && !subscriberID.isEmpty()) {

                   if (!preferenceUtil.getDataBID(AppConstant.APPPID).isEmpty() && preferenceUtil.getIntData(AppConstant.CAN_STORED_QUEUE) > 0) {
                       if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                           try {
                               HashMap<String, String> data = new HashMap<>();
                               data.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                               data.put("operation", "add_property");
                               data.put(AppConstant.BTYPE_, "" + AppConstant.BTYPE);
                               data.put(AppConstant.PT_, "1");
                               data.put(AppConstant.BKEY, Util.getAndroidId(appContext));
                               data.put("name", "subscriber_id");
                               data.put(AppConstant.TOKEN, preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                               data.put("value", subscriberID);
                               RestClient.postRequest(RestClient.MOMAGIC_USER_PROPERTY, data, null, new RestClient.ResponseHandler() {
                                   @Override
                                   void onFailure(int statusCode, String response, Throwable throwable) {
                                       super.onFailure(statusCode, response, throwable);
                                       preferenceUtil.setStringData(AppConstant.SUBSCRIBER_ID_DATA, subscriberID);
                                   }

                                   @Override
                                   void onSuccess(String response) {
                                       super.onSuccess(response);
                                       preferenceUtil.setStringData(AppConstant.SUBSCRIBER_ID_DATA, "");

                                   }
                               });

                               RestClient.postRequest(RestClient.SUBSCRIBER_URL, data, null, new RestClient.ResponseHandler() {
                                   @Override
                                   void onFailure(int statusCode, String response, Throwable throwable) {
                                       super.onFailure(statusCode, response, throwable);
                                       preferenceUtil.setStringData(AppConstant.SUBSCRIBER_ID_DATA, subscriberID);


                                   }

                                   @Override
                                   void onSuccess(String response) {
                                       super.onSuccess(response);
                                       preferenceUtil.setStringData(AppConstant.SUBSCRIBER_ID_DATA, "");
                                   }
                               });


                           } catch (Exception ex) {
                               Util.setException(DATB.appContext, ex.toString(), AppConstant.APP_NAME_TAG, "setSubscriptionID");
                           }

                       } else {
                           preferenceUtil.setStringData(AppConstant.SUBSCRIBER_ID_DATA, subscriberID);

                       }
                   } else {
                       preferenceUtil.setStringData(AppConstant.SUBSCRIBER_ID_DATA, subscriberID);

                   }

               } else {
                   Util.setException(DATB.appContext, "Subscriber ID is not here", AppConstant.APP_NAME_TAG, "SetSubscriberID");
                   DebugFileManager.createExternalStoragePublic(DATB.appContext,"Repeated Subscriber ID "+subscriberID,"[Log.e]->");
               }
           }
           else
           {
               DebugFileManager.createExternalStoragePublic(DATB.appContext,"Repeated Subscriber ID "+subscriberID,"[Log.e]->");
           }
       }
        }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void addEventAPI(String eventName, HashMap<String,Object> data){
        if(appContext!=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
            HashMap<String, Object> filterEventData = checkValidationEvent(data, 1);
            if (filterEventData.size() > 0) {
                try {
                    JSONObject jsonObject = new JSONObject(filterEventData);

                    if (!preferenceUtil.getDataBID(AppConstant.APPPID).isEmpty()  && preferenceUtil.getIntData(AppConstant.CAN_STORED_QUEUE) > 0) {
                        if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                            Map<String, String> mapData = new HashMap<>();
                            mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                            mapData.put(AppConstant.ACT, eventName);
                            mapData.put(AppConstant.ET_, "evt");
                            mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(appContext));
                            mapData.put(AppConstant.VAL, "" + jsonObject.toString());
                            DebugFileManager.createExternalStoragePublic(DATB.appContext,eventName+"EventData->"+mapData.toString(),"[Log.v]->");

                            RestClient.postRequest(RestClient.EVENT_URL, mapData,null, new RestClient.ResponseHandler() {
                                @Override
                                void onSuccess(final String response) {
                                    super.onSuccess(response);
                                    preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EN, null);
                                    preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EV, null);
                                }

                                @Override
                                void onFailure(int statusCode, String response, Throwable throwable) {
                                    super.onFailure(statusCode, response, throwable);
                                    JSONObject jsonObjectLocal = new JSONObject(data);
                                    preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EN, eventName);
                                    preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EV, jsonObjectLocal.toString());
                                }
                            });
                        } else {
                            JSONObject jsonObjectLocal = new JSONObject(data);
                            preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EN, eventName);
                            preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EV, jsonObjectLocal.toString());
                        }
                    } else {
                        JSONObject jsonObjectLocal = new JSONObject(data);
                        preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EN, eventName);
                        preferenceUtil.setStringData(AppConstant.EVENT_LOCAL_DATA_EV, jsonObjectLocal.toString());
                    }
                }
                catch (Exception ex)
                {
                    Util.setException(appContext,ex.toString(),"DATB","add Event");
                }
            }  else {
                Util.setException(appContext,"Event length more than 32",AppConstant.APP_NAME_TAG,"AdEvent");
            }
        }
    }
    private static HashMap<String, Object> checkValidationEvent(HashMap<String, Object> data,int index){
        HashMap<String, Object>  newList= new HashMap<String, Object>();
        for (HashMap.Entry<String,Object> array:data.entrySet()) {
            if (index<=16){
                String newKey = array.getKey().substring(0, Math.min(array.getKey().length(), 32));
                if (array.getValue() instanceof String){
                    if (array.getValue().toString().length()>0) {
                        String newValue = array.getValue().toString().substring(0, Math.min(array.getValue().toString().length(), 64));
                        newList.put(newKey, newValue);
                        index++;
                    }
                } else if (!(array.getValue() instanceof String)&&array.getValue()!=null){
                    newList.put(newKey, ( array.getValue()));
                    index ++;
                }
            }
        }
        return newList;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void addUserProperty(HashMap<String, Object> object) {

            if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.ADD_USERPROPERTY) && appContext == null) {
                osTaskManager.addTaskToQueue(new Runnable() {
                    @Override
                    public void run() {
                        DebugFileManager.createExternalStoragePublic(DATB.appContext,"addUserProperty(): operation from pending task queue.","[Log.d]->addUserProperty->");

                        Log.d(AppConstant.APP_NAME_TAG, "addUserProperty(): operation from pending task queue.");
                        addUserProperty(object);
                    }
                });
                return;
            }

        if (object != null && object.size() > 0) {
            try {
                final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
                HashMap<String, Object> newListUserProfile = new HashMap<String, Object>();
                for (Map.Entry<String, Object> refineEntry : object.entrySet()) {
                    if (refineEntry.getKey() != null && !refineEntry.getKey().isEmpty()) {
                        String newKey = refineEntry.getKey().toLowerCase();
                        newListUserProfile.put(newKey, refineEntry.getValue());
                    }
                }
                if (newListUserProfile.size() > 0) {
                    HashMap<String, Object> filterUserPropertyData = checkValidationUserProfile(newListUserProfile, 1);
                    if (filterUserPropertyData.size() > 0) {
                        JSONObject jsonObject = new JSONObject(filterUserPropertyData);
                        if (!preferenceUtil.getDataBID(AppConstant.APPPID).isEmpty()  && preferenceUtil.getIntData(AppConstant.CAN_STORED_QUEUE) > 0) {
                            if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                                Map<String, String> mapData = new HashMap<>();
                                mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                                mapData.put(AppConstant.ACT, "add");
                                mapData.put(AppConstant.ET_, "" + AppConstant.USERP_);
                                mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(appContext));
                                mapData.put(AppConstant.VAL, "" + jsonObject.toString());
                                RestClient.postRequest(RestClient.PROPERTIES_URL, mapData, null, new RestClient.ResponseHandler() {
                                    @Override
                                    void onSuccess(final String response) {
                                        super.onSuccess(response);
                                        preferenceUtil.setStringData(AppConstant.USER_LOCAL_DATA, null);
                                    }

                                    @Override
                                    void onFailure(int statusCode, String response, Throwable throwable) {
                                        super.onFailure(statusCode, response, throwable);
                                        JSONObject jsonObjectLocal = new JSONObject(object);
                                        preferenceUtil.setStringData(AppConstant.USER_LOCAL_DATA, jsonObjectLocal.toString());

                                    }
                                });
                            } else {
                                JSONObject jsonObjectLocal = new JSONObject(object);
                                preferenceUtil.setStringData(AppConstant.USER_LOCAL_DATA, jsonObjectLocal.toString());
                            }
                        } else {
                            JSONObject jsonObjectLocal = new JSONObject(object);
                            preferenceUtil.setStringData(AppConstant.USER_LOCAL_DATA, jsonObjectLocal.toString());
                        }
                    }
                    else
                    {
                        Util.setException(appContext, "Blank user properties",AppConstant.APP_NAME_TAG, "addUserProperty");
                        DebugFileManager.createExternalStoragePublic(DATB.appContext,"Blank user properties","[Log.d]->addUserProperty->");

                    }

                }
                else {
                    DebugFileManager.createExternalStoragePublic(DATB.appContext,"Blank user properties","[Log.d]->addUserProperty->");

                    Util.setException(appContext, "Blank user properties",AppConstant.APP_NAME_TAG, "addUserProperty");

                }

            } catch (Exception e) {
                DebugFileManager.createExternalStoragePublic(DATB.appContext,"Blank user properties","[Log.d]->addUserProperty->");

                Util.setException(appContext, "Blank user properties",AppConstant.APP_NAME_TAG, "addUserProperty");
            }
        }
        else
        {
            DebugFileManager.createExternalStoragePublic(DATB.appContext,"Blank user properties","[Log.d]->addUserProperty->");

            Util.setException(appContext, "Blank user properties",AppConstant.APP_NAME_TAG, "addUserProperty");

        }

    }
    private static HashMap<String, Object> checkValidationUserProfile(HashMap<String, Object> data,int index){
        HashMap<String, Object>  newList= new HashMap<String, Object>();
        int indexForValue = 1;
        for (HashMap.Entry<String,Object> array:data.entrySet()) {
            if (index<=64){
                String newKey = array.getKey().substring(0, Math.min(array.getKey().length(), 32));
                if (array.getValue() instanceof String){
                    if (array.getValue().toString().length()>0) {
                        String newValue = array.getValue().toString().substring(0, Math.min(array.getValue().toString().length(), 64));
                        newList.put(newKey, newValue);
                        index++;
                    }
                } else if (array.getValue() instanceof List) {
                    List<Object> newvalueListDta = (List<Object>) array.getValue();
                    List<Object> newvalueList = new ArrayList<Object>();
                    for(Object obj: newvalueListDta) {
                        if (indexForValue<=64){
                            if (obj instanceof String){
                                String ListData = obj.toString();
                                if (indexForValue<=64&&ListData.length()>0){
                                    String newListValue = ListData.substring(0, Math.min(ListData.length(), 64));
                                    newvalueList.add(newListValue);
                                    indexForValue ++;
                                }
                            }else if (!(obj instanceof String)&&obj!=null){
                                newvalueList.add(obj);
                                indexForValue ++;
                            }
                        }
                    }
                    newList.put(newKey, newvalueList);
                    index ++;
                }else if (!(array.getValue() instanceof String)&&!(array.getValue() instanceof List)&&array.getValue()!=null){
                    newList.put(newKey, ( array.getValue()));
                    index ++;
                }
            }
        }
        return newList;
    }
    public static void setSubscription(Boolean enable) {
        if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.SET_SUBSCRIPTION) && appContext == null) {
            osTaskManager.addTaskToQueue(new Runnable() {
                @Override
                public void run() {

                    Log.d(AppConstant.APP_NAME_TAG, "setSubscription(): operation from pending task queue.");
                    setSubscription(enable);
                }
            });
            return;
        }

        final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);

        try {
            int value = 2;
            if (enable != null) {
                if (enable) {
                    value = 0;
                }

                if (!preferenceUtil.getDataBID(AppConstant.APPPID).isEmpty()  && preferenceUtil.getIntData(AppConstant.CAN_STORED_QUEUE) > 0) {
                    if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                        Map<String, String> mapData = new HashMap<>();
                        mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                        mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(appContext));
                        mapData.put(AppConstant.BTYPE_, "" + AppConstant.BTYPE);
                        mapData.put(AppConstant.DTYPE_, "" + AppConstant.DTYPE);
                        mapData.put(AppConstant.APPVERSION, "" + AppConstant.SDK_VERSION);
                        mapData.put(AppConstant.PTE_, "" + AppConstant.PTE);
                        mapData.put(AppConstant.OS, "" + AppConstant.SDKOS);
                        mapData.put(AppConstant.PT_, "" + AppConstant.PT);
                        mapData.put(AppConstant.GE_, "" + AppConstant.GE);
                        mapData.put(AppConstant.ACTION, "" + value);
                        DebugFileManager.createExternalStoragePublic(DATB.appContext,"setSubscription"+mapData.toString(),"[Log.d]->setSubscription->");

                        RestClient.postRequest(RestClient.SUBSCRIPTION_API, mapData,null, new RestClient.ResponseHandler() {
                            @Override
                            void onSuccess(final String response) {
                                super.onSuccess(response);
                                preferenceUtil.setBooleanData(AppConstant.IS_SET_SUBSCRIPTION_METHOD, false);
                            }

                            @Override
                            void onFailure(int statusCode, String response, Throwable throwable) {
                                super.onFailure(statusCode, response, throwable);
                                preferenceUtil.setBooleanData(AppConstant.IS_SET_SUBSCRIPTION_METHOD, true);
                                preferenceUtil.setBooleanData(AppConstant.SET_SUBSCRITION_LOCAL_DATA, enable);
                            }
                        });
                    } else {
                        preferenceUtil.setBooleanData(AppConstant.IS_SET_SUBSCRIPTION_METHOD, true);
                        preferenceUtil.setBooleanData(AppConstant.SET_SUBSCRITION_LOCAL_DATA, enable);
                    }
                } else {
                    preferenceUtil.setBooleanData(AppConstant.IS_SET_SUBSCRIPTION_METHOD, true);
                    preferenceUtil.setBooleanData(AppConstant.SET_SUBSCRITION_LOCAL_DATA, enable);
                }

            }
            else
            {
                Util.setException(appContext, "Value should not be null",AppConstant.APP_NAME_TAG, "setSubscription");

            }
        }catch (Exception e) {
            DebugFileManager.createExternalStoragePublic(DATB.appContext,"setSubscription"+e.toString(),"[Log.e]->Exception->");

            Util.setException(appContext, e.toString(),  AppConstant.APP_NAME_TAG,"setSubscription");
        }

    }
    public static void setFirebaseAnalytics(boolean isSet){
        if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.SET_FIREBASE_ANALYTICS) && appContext == null) {
            osTaskManager.addTaskToQueue(new Runnable() {
                @Override
                public void run() {
                    Log.d(AppConstant.APP_NAME_TAG, "setFirebaseAnalytics(): operation from pending task queue.");
                    setFirebaseAnalytics(isSet);
                }
            });
            return;
        }
        if(appContext !=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
            preferenceUtil.setBooleanData(AppConstant.FIREBASE_ANALYTICS_TRACK, isSet);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void handleNotification(Context context, final Map<String,String> data)
    {
        Log.d(AppConstant.APP_NAME_TAG, AppConstant.NOTIFICATIONRECEIVED);
        try {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);

            if(data.get(AppConstant.AD_NETWORK) !=null || data.get(AppConstant.GLOBAL)!=null || data.get(AppConstant.GLOBAL_PUBLIC_KEY)!=null)
            {
                if(data.get(AppConstant.GLOBAL_PUBLIC_KEY)!=null)
                {
                    try
                    {
                        JSONObject jsonObject=new JSONObject(Objects.requireNonNull(data.get(AppConstant.GLOBAL)));
                        String urlData=data.get(AppConstant.GLOBAL_PUBLIC_KEY);
                        if(jsonObject.toString()!=null && urlData!=null && !urlData.isEmpty()) {
                            String cid = jsonObject.optString(ShortpayloadConstant.ID);
                            String rid = jsonObject.optString(ShortpayloadConstant.RID);
                            NotificationEventManager.impressionNotification(RestClient.IMPRESSION_URL,cid,rid,-1,"FCM");
                            AdMediation.getMediationGPL(context, jsonObject, urlData);
                        }
                        else
                        {
                            NotificationEventManager.handleNotificationError("Payload Error",data.toString(),"MessagingSevices","HandleNow");
                        }
                    }
                    catch (Exception ex)
                    {
                        DebugFileManager.createExternalStoragePublic(DATB.appContext,"Payload"+ex.toString()+data.toString(),"[Log.e]->Exception->");

                        Util.setException(context,ex.toString()+"PayloadError"+data.toString(),"DATBMessagingService","handleNow");
                    }

                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(data.get(AppConstant.GLOBAL));
                        String cid = jsonObject.optString(ShortpayloadConstant.ID);
                        String rid = jsonObject.optString(ShortpayloadConstant.RID);
                        NotificationEventManager.impressionNotification(RestClient.IMPRESSION_URL, cid, rid, -1,"FCM");
                        JSONObject jsonObject1=new JSONObject(data.toString());
                        AdMediation.getMediationData(context, jsonObject1,"fcm","");
                        preferenceUtil.setBooleanData(AppConstant.MEDIATION, true);
                    }
                    catch (Exception ex)
                    {
                        Util.setException(context,ex.toString()+"PayloadError"+data.toString(),"DATBMessagingService","handleNow");
                        DebugFileManager.createExternalStoragePublic(DATB.appContext,"Payload Error"+ex.toString()+data.toString(),"[Log.e]->Exception->");

                    }
                }
            }
            else {
                preferenceUtil.setBooleanData(AppConstant.MEDIATION, false);
                JSONObject payloadObj = new JSONObject(data);
                if (payloadObj.optLong(ShortpayloadConstant.CREATEDON) > PreferenceUtil.getInstance(DATB.appContext).getLongValue(AppConstant.DEVICE_REGISTRATION_TIMESTAMP)) {
                    payload = new Payload();
                    payload.setCreated_Time(payloadObj.optString(ShortpayloadConstant.CREATEDON));
                    payload.setFetchURL(payloadObj.optString(ShortpayloadConstant.FETCHURL));
                    payload.setKey(payloadObj.optString(ShortpayloadConstant.KEY));
                    payload.setId(payloadObj.optString(ShortpayloadConstant.ID));
                    payload.setRid(payloadObj.optString(ShortpayloadConstant.RID));
                    payload.setLink(payloadObj.optString(ShortpayloadConstant.LINK));
                    payload.setTitle(payloadObj.optString(ShortpayloadConstant.TITLE));
                    payload.setMessage(payloadObj.optString(ShortpayloadConstant.NMESSAGE));
                    payload.setIcon(payloadObj.optString(ShortpayloadConstant.ICON));
                    payload.setReqInt(payloadObj.optInt(ShortpayloadConstant.REQINT));
                    payload.setTag(payloadObj.optString(ShortpayloadConstant.TAG));
                    payload.setBanner(payloadObj.optString(ShortpayloadConstant.BANNER));
                    payload.setAct_num(payloadObj.optInt(ShortpayloadConstant.ACTNUM));
                    payload.setBadgeicon(payloadObj.optString(ShortpayloadConstant.BADGE_ICON));
                    payload.setBadgecolor(payloadObj.optString(ShortpayloadConstant.BADGE_COLOR));
                    payload.setSubTitle(payloadObj.optString(ShortpayloadConstant.SUBTITLE));
                    payload.setGroup(payloadObj.optInt(ShortpayloadConstant.GROUP));
                    payload.setBadgeCount(payloadObj.optInt(ShortpayloadConstant.BADGE_COUNT));
                    // Button 2
                    payload.setAct1name(payloadObj.optString(ShortpayloadConstant.ACT1NAME));
                    payload.setAct1link(payloadObj.optString(ShortpayloadConstant.ACT1LINK));
                    payload.setAct1icon(payloadObj.optString(ShortpayloadConstant.ACT1ICON));
                    payload.setAct1ID(payloadObj.optString(ShortpayloadConstant.ACT1ID));
                    // Button 2
                    payload.setAct2name(payloadObj.optString(ShortpayloadConstant.ACT2NAME));
                    payload.setAct2link(payloadObj.optString(ShortpayloadConstant.ACT2LINK));
                    payload.setAct2icon(payloadObj.optString(ShortpayloadConstant.ACT2ICON));
                    payload.setAct2ID(payloadObj.optString(ShortpayloadConstant.ACT2ID));

                    payload.setInapp(payloadObj.optInt(ShortpayloadConstant.INAPP));
                    payload.setTrayicon(payloadObj.optString(ShortpayloadConstant.TARYICON));
                    payload.setSmallIconAccentColor(payloadObj.optString(ShortpayloadConstant.ICONCOLOR));
                    payload.setSound(payloadObj.optString(ShortpayloadConstant.SOUND));
                    payload.setLedColor(payloadObj.optString(ShortpayloadConstant.LEDCOLOR));
                    payload.setLockScreenVisibility(payloadObj.optInt(ShortpayloadConstant.VISIBILITY));
                    payload.setGroupKey(payloadObj.optString(ShortpayloadConstant.GKEY));
                    payload.setGroupMessage(payloadObj.optString(ShortpayloadConstant.GMESSAGE));
                    payload.setFromProjectNumber(payloadObj.optString(ShortpayloadConstant.PROJECTNUMBER));
                    payload.setCollapseId(payloadObj.optString(ShortpayloadConstant.COLLAPSEID));
                    payload.setPriority(payloadObj.optInt(ShortpayloadConstant.PRIORITY));
                    payload.setRawPayload(payloadObj.optString(ShortpayloadConstant.RAWDATA));
                    payload.setAp(payloadObj.optString(ShortpayloadConstant.ADDITIONALPARAM));
                    payload.setCfg(payloadObj.optInt(ShortpayloadConstant.CFG));
                    payload.setTime_to_live(payloadObj.optString(ShortpayloadConstant.TIME_TO_LIVE));
                    payload.setPush_type(AppConstant.PUSH_FCM);
                    payload.setSound(payloadObj.optString(ShortpayloadConstant.NOTIFICATION_SOUND));
                    payload.setMaxNotification(payloadObj.optInt(ShortpayloadConstant.MAX_NOTIFICATION));

                } else {
                    String updateDaily=NotificationEventManager.getDailyTime(context);
                    if (!updateDaily.equalsIgnoreCase(Util.getTime())) {
                        preferenceUtil.setStringData(AppConstant.CURRENT_DATE_VIEW_DAILY, Util.getTime());
                        NotificationEventManager.handleNotificationError("Payload Error" + payloadObj.optString("t"), payloadObj.toString(), "iz_db_clientside_handle_servcie", "handleNow()");
                    }
                    return;
                }
                if (DATB.appContext == null)
                    DATB.appContext = context;
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        NotificationEventManager.handleImpressionAPI(payload,AppConstant.PUSH_FCM);
                        DATB.processNotificationReceived(context,payload);
                    } // This is your code
                };
                mainHandler.post(myRunnable);

            }
            DebugFileManager.createExternalStoragePublic(DATB.appContext,data.toString(),"payloadData");

        } catch (Exception e) {
            DebugFileManager.createExternalStoragePublic(DATB.appContext,"Payload Error"+e.toString()+data.toString(),"[Log.e]->Exception->");

            Util.setException(context,e.toString(),"DATB","handleNotification");
        }
    }
    public static void addTag(final List<String> topicName){
            if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.ADD_TAG) && appContext == null) {
                osTaskManager.addTaskToQueue(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(AppConstant.APP_NAME_TAG, "addTag(): operation from pending task queue.");
                        addTag(topicName);
                    }
                });
                return;
            }
        if (topicName != null && !topicName.isEmpty() && FCMTokenGenerator.firebaseApp!=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
            if (preferenceUtil.getStringData(AppConstant.SENDERID) != null) {
//                FirebaseOptions firebaseOptions =
//                        new FirebaseOptions.Builder()
//                                .setGcmSenderId(preferenceUtil.getStringData(AppConstant.SENDERID)) //senderID
//                                .setApplicationId(get_App_ID()) //application ID
//                                .setApiKey(getAPI_KEY()) //Application Key
//                                .setProjectId(get_Project_ID()) //Project ID
//                                .build();
//                try {
//                    FirebaseApp firebaseApp = FirebaseApp.getInstance(AppConstant.FCMDEFAULT);
//                    if (firebaseApp == null) {
//                        FirebaseApp.initializeApp(appContext, firebaseOptions, AppConstant.FCMDEFAULT);
//                    }
//                } catch (IllegalStateException ex) {
//                    FirebaseApp.initializeApp(appContext, firebaseOptions, AppConstant.FCMDEFAULT);
//                }
                List<String> topicList = new ArrayList<String>();
                for (final String filterTopicName : topicName) {
                    if (filterTopicName != null && !filterTopicName.isEmpty()) {
                        if (Util.isMatchedString(filterTopicName)) {
                            try {
                                FirebaseMessaging.getInstance().subscribeToTopic(filterTopicName);
                                preferenceUtil.setStringData(AppConstant.GET_TOPIC_NAME, filterTopicName);
                                topicList.add(filterTopicName);
                            } catch (Exception e) {
                                Util.setException(DATB.appContext, e.toString(), "DATB", "addTag");

                            }
                        }
                    }
                }
                topicApi(AppConstant.ADD_TOPIC, topicList);
            }
        }
        else
        {
            Util.setException(DATB.appContext,"Topic list should not be  blank",AppConstant.APP_NAME_TAG,"AddTag");
        }
    }
    public static void removeTag(final List<String> topicName){

            if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.REMOVE_TAG) && appContext == null) {
                osTaskManager.addTaskToQueue(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(AppConstant.APP_NAME_TAG, "removeTag(): operation from pending task queue.");
                        removeTag(topicName);
                    }
                });
                return;
            }
        if (topicName != null && !topicName.isEmpty() && FCMTokenGenerator.firebaseApp!=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
            if (preferenceUtil.getStringData(AppConstant.SENDERID) != null ) {

//                FirebaseOptions firebaseOptions =
//                        new FirebaseOptions.Builder()
//                                .setGcmSenderId(preferenceUtil.getStringData(AppConstant.SENDERID)) //senderID
//                                .setApplicationId(get_App_ID()) //application ID
//                                .setApiKey(getAPI_KEY()) //Application Key
//                                .setProjectId(get_Project_ID()) //Project ID
//                                .build();
//                try {
//                    FirebaseApp firebaseApp = FirebaseApp.getInstance(AppConstant.FCMDEFAULT);
//                    if (firebaseApp == null) {
//                        FirebaseApp.initializeApp(appContext, firebaseOptions, AppConstant.FCMDEFAULT);
//                    }
//                } catch (IllegalStateException ex) {
//                    FirebaseApp.initializeApp(appContext, firebaseOptions, AppConstant.FCMDEFAULT);
//                }
                List<String> topicList = new ArrayList<String>();
                for (final String filterTopicName : topicName) {
                    if (filterTopicName != null && !filterTopicName.isEmpty()) {
                        if (Util.isMatchedString(filterTopicName)) {
                            try {
                                FirebaseMessaging.getInstance().unsubscribeFromTopic(filterTopicName);
                                preferenceUtil.setStringData(AppConstant.REMOVE_TOPIC_NAME, filterTopicName);
                                topicList.add(filterTopicName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                topicApi(AppConstant.REMOVE_TOPIC, topicList);
            }
        }
        else
        {
            Util.setException(DATB.appContext,"Topic list should not be  blank",AppConstant.APP_NAME_TAG,"RemoveTag");

        }
    }
    private static void topicApi(String action, List<String> topic){
        if (appContext == null)
            return;

        try {
            if (topic.size() > 0){
                final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
                if (!preferenceUtil.getDataBID(AppConstant.APPPID).isEmpty()  && preferenceUtil.getIntData(AppConstant.CAN_STORED_QUEUE) > 0) {
                    if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() || !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                        HashMap<String, List<String>> data = new HashMap<>();
                        data.put(AppConstant.TOPIC, topic);
                        JSONObject jsonObject = new JSONObject(data);
                        Map<String, String> mapData = new HashMap<>();
                        mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                        mapData.put(AppConstant.ACT, action);
                        mapData.put(AppConstant.ET_, "" + AppConstant.USERP_);
                        mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(appContext));
                        mapData.put(AppConstant.VAL, "" + jsonObject.toString());
                        mapData.put(AppConstant.TOKEN, "" + preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                        mapData.put(AppConstant.BTYPE_, "" + AppConstant.BTYPE);
                        RestClient.postRequest(RestClient.PROPERTIES_URL, mapData, null,new RestClient.ResponseHandler() {
                            @Override
                            void onSuccess(final String response) {
                                super.onSuccess(response);
                                if (action.equalsIgnoreCase(AppConstant.ADD_TOPIC))
                                    preferenceUtil.setStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE, null);
                                else if (action.equalsIgnoreCase(AppConstant.REMOVE_TOPIC))
                                    preferenceUtil.setStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE, null);
                            }

                            @Override
                            void onFailure(int statusCode, String response, Throwable throwable) {
                                super.onFailure(statusCode, response, throwable);
                                JSONArray jsonArray = new JSONArray(topic);
                                if (action.equalsIgnoreCase(AppConstant.ADD_TOPIC)) {
                                    preferenceUtil.setStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE, jsonArray.toString());
                                }
                                else if (action.equalsIgnoreCase(AppConstant.REMOVE_TOPIC)) {
                                    preferenceUtil.setStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE, jsonArray.toString());
                                }
                            }
                        });
                    } else {

                        JSONArray jsonArray = new JSONArray(topic);
                        if (action.equalsIgnoreCase(AppConstant.ADD_TOPIC)) {
                            preferenceUtil.setStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE, jsonArray.toString());
                        }
                        else if (action.equalsIgnoreCase(AppConstant.REMOVE_TOPIC)) {
                            preferenceUtil.setStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE, jsonArray.toString());
                        }
                    }
                } else {
                    JSONArray jsonArray = new JSONArray(topic);
                    if (action.equalsIgnoreCase(AppConstant.ADD_TOPIC)) {
                        preferenceUtil.setStringData(AppConstant.IZ_ADD_TOPIC_OFFLINE, jsonArray.toString());
                    }
                    else if (action.equalsIgnoreCase(AppConstant.REMOVE_TOPIC)) {
                        preferenceUtil.setStringData(AppConstant.IZ_REMOVE_TOPIC_OFFLINE, jsonArray.toString());
                    }
                }
            }
        }catch (Exception e) {
            Util.setException(appContext, e.toString(), "topicApi", AppConstant.APP_NAME_TAG);
        }
    }
    private static String  getAPI_KEY()
    {

        try {
            String apiKey = FirebaseOptions.fromResource(DATB.appContext).getApiKey();
            if (apiKey != null)
                return apiKey;
        }
        catch (Exception e)
        {
            Util.setException(DATB.appContext, e.toString(), "DATB", "getAPIkey");

            return "";//new String(Base64.decode(FCM_DEFAULT_API_KEY_BASE64, Base64.DEFAULT));

        }
        return "";


    }
    private  static String get_App_ID() {
        try {
            String application_id = FirebaseOptions.fromResource(DATB.appContext).getApplicationId();
            if (application_id!=null)
                return application_id;
        }
        catch (Exception ex)
        {
            Util.setException(DATB.appContext, ex.toString(), "DATB", "getAppID");
            return "";
        }
        return "";

    }
    private  static String get_Project_ID()
    {
        try {
            String project_id = FirebaseOptions.fromResource(DATB.appContext).getProjectId();
            if(project_id!=null)
                return project_id;
        }
        catch (Exception exception)
        {
            return "";

        }
        return "";

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void lastVisitApi(Context context){
        if(context!=null) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            String time = preferenceUtil.getStringData(AppConstant.CURRENT_DATE);
            if (!time.equalsIgnoreCase(getTime())) {
                preferenceUtil.setStringData(AppConstant.CURRENT_DATE, getTime());
                try {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put(AppConstant.LAST_WEBSITE_VISIT, true);
                    data.put(AppConstant.LANG_, Util.getDeviceLanguageTag());
                    JSONObject jsonObject = new JSONObject(data);
                    Map<String, String> mapData = new HashMap<>();
                    mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                    mapData.put(AppConstant.BKEY, "" + Util.getAndroidId(appContext));
                    mapData.put(AppConstant.VAL, "" + jsonObject.toString());
                    mapData.put(AppConstant.ACT, "add");
                    mapData.put(AppConstant.ISID_, "1");
                    mapData.put(AppConstant.ET_, "" + AppConstant.USERP_);
                    DebugFileManager.createExternalStoragePublic(DATB.appContext,"Last Visit"+mapData.toString(),"[Log.e]->LastVisit->");

                    RestClient.postRequest(RestClient.LASTVISITURL, mapData,null, new RestClient.ResponseHandler() {
                        @Override
                        void onSuccess(final String response) {
                            super.onSuccess(response);
                        }

                        @Override
                        void onFailure(int statusCode, String response, Throwable throwable) {
                            super.onFailure(statusCode, response, throwable);
                        }
                    });
                } catch (Exception ex) {
                    DebugFileManager.createExternalStoragePublic(DATB.appContext,"Last Visit"+ex.toString(),"[Log.e]->LastVisit->");

                    Util.setException(context, ex.toString(), "DATB", "lastVisitAPI");


                }
            }
        }
    }
    private static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }
    public static void setDefaultNotificationBanner(int setBanner){
        bannerImage = setBanner;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setDefaultTemplate(int templateID){
        if (osTaskManager.shouldQueueTaskForInit(OSTaskManager.SET_CUSTOM_TEMPLATE) && appContext == null) {
            osTaskManager.addTaskToQueue(new Runnable() {
                @Override
                public void run() {
                    Log.d(AppConstant.APP_NAME_TAG, "setCustomTemplate(): operation from pending task queue.");
                    setDefaultTemplate(templateID);
                }
            });
            return;
        }
        if(PushTemplate.DEFAULT == templateID || PushTemplate.TEXT_OVERLAY == templateID) {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(appContext);
            preferenceUtil.setIntData(AppConstant.NOTIFICATION_PREVIEW, templateID);
        }
        else
        {
            DebugFileManager.createExternalStoragePublic(DATB.appContext,"setDefaultTemplate"+"Template id is not matched"+templateID,"[Log.V]->setDefaultTemplate->");

            Util.setException(appContext,"Template id is not matched"+templateID,AppConstant.APP_NAME_TAG,"setDefaultTemplate");
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void sendOfflineDataToServer(Context context) {
        if (context == null)
            return;


            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            try {
                if (!preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_CLICK_OFFLINE).isEmpty()) {
                    JSONArray jsonArrayOffline = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_CLICK_OFFLINE));
                    for (int i = 0; i < jsonArrayOffline.length(); i++) {
                        JSONObject c = jsonArrayOffline.getJSONObject(i);
                        NotificationActionReceiver.notificationClickAPI(context, c.optString("apiURL"), c.optString("cid"), c.optString("rid"), c.optInt("click"), i,"fcm");
                    }
                }

                if (!preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_LAST_CLICK_OFFLINE).isEmpty()) {
                    JSONArray lciJsonArrayOffline = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_LAST_CLICK_OFFLINE));
                    for (int i = 0; i < lciJsonArrayOffline.length(); i++) {
                        JSONObject c = lciJsonArrayOffline.getJSONObject(i);
                        NotificationActionReceiver.lastClickAPI(context, c.optString("apiURL"), c.optString("rid"), i);
                    }
                }

                if (!preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_VIEW_OFFLINE).isEmpty()) {
                    JSONArray viewJsonArrayOffline = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_VIEW_OFFLINE));
                    for (int i = 0; i < viewJsonArrayOffline.length(); i++) {
                        JSONObject c = viewJsonArrayOffline.getJSONObject(i);
                        NotificationEventManager.impressionNotification(c.optString("apiURL"), c.optString("cid"), c.optString("rid"), i,"fcm");
                    }
                }

                if (!preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_LAST_VIEW_OFFLINE).isEmpty()) {
                    JSONArray viewJsonArrayOffline = new JSONArray(preferenceUtil.getStringData(AppConstant.IZ_NOTIFICATION_LAST_VIEW_OFFLINE));
                    for (int i = 0; i < viewJsonArrayOffline.length(); i++) {
                        JSONObject c = viewJsonArrayOffline.getJSONObject(i);
                        NotificationEventManager.lastViewNotification(c.optString("apiURL"), c.optString("rid"), c.optString("cid"), i);
                    }
                }
                 if(!preferenceUtil.getStringData(AppConstant.STORE_MEDIATION_RECORDS).isEmpty())
                 {
                     JSONArray mediationRecords = new JSONArray(preferenceUtil.getStringData(AppConstant.STORE_MEDIATION_RECORDS));
                     for(int i=0;i<mediationRecords.length();i++)
                     {
                         JSONObject jsonObject=mediationRecords.getJSONObject(i);
                         if(jsonObject.getString(AppConstant.STORE_MED_API).equals(AppConstant.MED_IMPRESION))
                         {
                            String jsonData= jsonObject.getString(AppConstant.STORE_MED_DATA);
                             AdMediation.mediationImpression(jsonData,i);
                         }
                         if(jsonObject.getString(AppConstant.STORE_MED_API).equals(AppConstant.MED_CLICK))
                         {
                             String jsonData= jsonObject.getString(AppConstant.STORE_MED_DATA);
                             NotificationActionReceiver.callMediationClicks(context,jsonData,i);
                         }
                     }

                }

            } catch (Exception e) {
                DebugFileManager.createExternalStoragePublic(DATB.appContext,"SendOfflineDataToServerException","[Log.V]->SendOfflineDataToServerException->");

                Log.e(AppConstant.APP_NAME_TAG, "Success: SendOfflineDataToServerException -- " + e );
            }


    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static DATB.Builder initialize(Context context, String tokenJson) {
        if (context == null)
            return null;

        try {
            if (tokenJson !=null && !tokenJson.isEmpty()) {
                if (isJSONValid(tokenJson)) {

                    PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
                    JSONObject data = new JSONObject(tokenJson);

                    String fcmToken = data.optString(FCM_TOKEN_FROM_JSON);
                    String xiaomiToken = data.optString(XIAOMI_TOKEN_FROM_JSON);
                    String huaweiToken = data.optString(HUAWEI_TOKEN_FROM_JSON);

                    if (data.has(HUAWEI_TOKEN_FROM_JSON)) {
                        if (!huaweiToken.isEmpty()) {
                            if (Build.MANUFACTURER.equalsIgnoreCase("Huawei")) {
                                if (!huaweiToken.equals(preferenceUtil.getStringData(AppConstant.HMS_TOKEN))) {
                                    preferenceUtil.setBooleanData(AppConstant.CAN_GENERATE_HUAWEI_TOKEN, true);
                                    preferenceUtil.setBooleanData(AppConstant.IS_TOKEN_UPDATED, false);
                                    preferenceUtil.setBooleanData(AppConstant.IS_UPDATED_HMS_TOKEN, true);
                                    preferenceUtil.setStringData(AppConstant.HMS_TOKEN, huaweiToken);
                                }
                            }
                        } else {
                            Util.setException(context, "Please put huawei token...", "initialize", AppConstant.APP_NAME_TAG);
                        }
                    }
                    preferenceUtil.setBooleanData(AppConstant.CAN_GENERATE_FCM_TOKEN, true);
                    if (data.has(FCM_TOKEN_FROM_JSON)) {
                        if (!fcmToken.isEmpty()) {
                            if (!fcmToken.equals(preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN))) {
                                preferenceUtil.setBooleanData(AppConstant.IS_TOKEN_UPDATED, false);
                                preferenceUtil.setStringData(AppConstant.FCM_DEVICE_TOKEN, fcmToken);
                            }
                        } else {
                            Util.setException(context, "Please put fcm token...", "initialize", AppConstant.APP_NAME_TAG);
                        }
                    }

                    if (data.has(XIAOMI_TOKEN_FROM_JSON)) {
                        if (!xiaomiToken.isEmpty()) {
                            if (!xiaomiToken.equals(preferenceUtil.getStringData(AppConstant.XiaomiToken))) {
                                preferenceUtil.setBooleanData(AppConstant.CAN_GENERATE_XIAOMI_TOKEN, true);
                                preferenceUtil.setBooleanData(AppConstant.IS_TOKEN_UPDATED, false);
                                preferenceUtil.setBooleanData(AppConstant.IS_UPDATED_XIAOMI_TOKEN, true);
                                preferenceUtil.setStringData(AppConstant.XiaomiToken, xiaomiToken);
                            }
                        } else {
                            Util.setException(context, "Please put xiaomi token...", "initialize", AppConstant.APP_NAME_TAG);
                        }
                    }
                    return new DATB.Builder(context);

                }
                else
                {
                    Log.e(AppConstant.APP_NAME_TAG,"Given String is Not Valid JSON String");
                    DebugFileManager.createExternalStoragePublic(DATB.appContext,"Given String is Not Valid JSON String"+tokenJson,"[Log.V]->SendOfflineDataToServerException->");


                }

            }

        } catch (Exception e) {
            Util.setException(context, e.toString(), "initialize", AppConstant.APP_NAME_TAG);
            e.printStackTrace();
        }
        return null;

    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean isJSONValid(String targetJson) {
        try {
            new JSONObject(targetJson);
        } catch (JSONException ex) {
            try {
                new JSONArray(targetJson);
            } catch (JSONException ex1) {
                DebugFileManager.createExternalStoragePublic(DATB.appContext,"Given String is Not Valid JSON String"+ex1.toString()+targetJson,"[Log.V]->SendOfflineDataToServerException->");

                return false;
            }
        }
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void  createDirectory(Context context)
    {
        DebugFileManager.createPublicDirectory(context);
    }
    public static void deleteDirectory(Context context)
    {
        DebugFileManager.deletePublicDirectory(context);
    }
    public static void shareFile(Context context,String name,String emailID)
    {

        DebugFileManager.shareDebuginfo(context,name,emailID);
    }
}
