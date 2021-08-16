package com.momagic;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class XiaomiPushReceiver extends PushMessageReceiver {
    private String TAG="XiaomiPushReceiver  PAYLOAD";
    private Payload payload;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        super.onReceivePassThroughMessage(context, miPushMessage);
        String payload = miPushMessage.getContent();
        Log.v("Push Type","Xiaomi");
        if(payload!=null && !payload.isEmpty())
         handleNow(context,payload);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
        super.onNotificationMessageArrived(context, miPushMessage);
        Log.v(TAG, miPushMessage.getContent());
         miPushMessage.getMessageId();
        String payload = miPushMessage.getContent();
        if(payload!=null && !payload.isEmpty())
            handleNow(context,payload);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void handleNow(Context context, String data) {
        Log.d(TAG, AppConstant.NOTIFICATIONRECEIVED);
        try {

                   PreferenceUtil preferenceUtil =PreferenceUtil.getInstance(context);
                    JSONObject payloadObj = new JSONObject(data);
                    if(payloadObj.has(AppConstant.AD_NETWORK) && payloadObj.has(AppConstant.GLOBAL))
                    {
                       AdMediation.getAdNotificationData(context,payloadObj,AppConstant.PUSH_XIAOMI);
                        preferenceUtil.setBooleanData(AppConstant.MEDIATION,true);
                    }
                    else {
                        preferenceUtil.setBooleanData(AppConstant.MEDIATION,false);
                        if (payloadObj.optLong(ShortpayloadConstant.CREATEDON) > PreferenceUtil.getInstance(context).getLongValue(AppConstant.DEVICE_REGISTRATION_TIMESTAMP)) {
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
                            payload.setPush_type(AppConstant.PUSH_XIAOMI);
                            payload.setSound(payloadObj.optString(ShortpayloadConstant.NOTIFICATION_SOUND));
                            payload.setMaxNotification(payloadObj.optInt(ShortpayloadConstant.MAX_NOTIFICATION));

                        }
                        else
                            return;
                    }

            } catch (Exception e) {
               Util.setException(context, e.toString(), TAG, "handleNow");
            }


            if (DATB.appContext == null)
                DATB.appContext = context;
            Handler mainHandler = new Handler(Looper.getMainLooper());
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    DATB.processNotificationReceived(payload);

                } // This is your code
            };
            mainHandler.post(myRunnable);



    }
    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        super.onReceiveRegisterResult(context, miPushCommandMessage);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCommandResult(Context context, MiPushCommandMessage miPushCommandMessage) {
        super.onCommandResult(context, miPushCommandMessage);
        if(context!=null) {
            try {
                PreferenceUtil preferenceUtils = PreferenceUtil.getInstance(context);
                preferenceUtils.setStringData(AppConstant.XiaomiToken, miPushCommandMessage.getCommandArguments().toString().replace("[", "").replace("]", ""));
                String mi_token =miPushCommandMessage.getCommandArguments().toString().replace("[", "").replace("]", "");
                Log.i(AppConstant.XiaomiToken, mi_token);
                if(mi_token!=null && !mi_token.isEmpty())
                {
                    registerToken(context,mi_token);
                }
            }
            catch (Exception ex)
            {
                Log.v("XMPush",ex.toString());
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void registerToken(final Context context,String miToken) {
        if (context == null)
            return;
        final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
        if (!preferenceUtil.getBoolean(AppConstant.IS_UPDATED_XIAOMI_TOKEN)) {
            try {
                if (!preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty())
                    preferenceUtil.setBooleanData(AppConstant.IS_UPDATED_HMS_TOKEN, true);
                Map<String,String> mapData= new HashMap<>();
                mapData.put(AppConstant.ADDURL, "" + AppConstant.STYPE);
                mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                mapData.put(AppConstant.BTYPE_,"" + AppConstant.BTYPE);
                mapData.put(AppConstant.DTYPE_,"" + AppConstant.DTYPE);
                mapData.put(AppConstant.TIMEZONE,"" + System.currentTimeMillis());
                mapData.put(AppConstant.APPVERSION,"" + AppConstant.SDKVERSION);
                mapData.put(AppConstant.OS,"" + AppConstant.SDKOS);
                mapData.put(AppConstant.ALLOWED_,"" + AppConstant.ALLOWED);
                mapData.put(AppConstant.ANDROID_ID,"" + Util.getAndroidId(context));
                mapData.put(AppConstant.CHECKSDKVERSION,"" + AppConstant.SDKVERSION);
                mapData.put(AppConstant.LANGUAGE,"" + Util.getDeviceLanguage());
                mapData.put(AppConstant.QSDK_VERSION ,"" + AppConstant.SDKVERSION);
                mapData.put(AppConstant.TOKEN,"" + preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                mapData.put(AppConstant.ADVERTISEMENTID,"" + preferenceUtil.getStringData(AppConstant.ADVERTISING_ID));
                mapData.put(AppConstant.XIAOMITOKEN,miToken);
                mapData.put(AppConstant.PACKAGE_NAME,"" + context.getPackageName());
                mapData.put(AppConstant.SDKTYPE,"" + DATB.SDKDEF);
                mapData.put(AppConstant.KEY_HMS,"" + preferenceUtil.getStringData(AppConstant.HMS_TOKEN));
                String deviceName = URLEncoder.encode(Util.getDeviceName(), AppConstant.UTF);
                String osVersion = URLEncoder.encode(Build.VERSION.RELEASE, AppConstant.UTF);
                mapData.put(AppConstant.ANDROIDVERSION,"" + osVersion);
                mapData.put(AppConstant.DEVICENAME,"" + deviceName);
                if (!preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                    preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 3);
                } else if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.XiaomiToken).isEmpty()) {
                    preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 2);
                } else if (!preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() && !preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty()) {
                    preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 2);
                }
                else {
                    preferenceUtil.setIntData(AppConstant.CLOUD_PUSH, 1);
                }

                RestClient.newPostRequest(RestClient.BASE_URL, mapData, new RestClient.ResponseHandler() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    void onSuccess(final String response) {
                        super.onSuccess(response);
                        preferenceUtil.setBooleanData(AppConstant.IS_UPDATED_XIAOMI_TOKEN, true);
                        DATB.lastVisitApi(context);
                        try {
                            if (!preferenceUtil.getStringData(AppConstant.USER_LOCAL_DATA).isEmpty()) {
                                Util.sleepTime(5000);
                                JSONObject json  = new JSONObject(preferenceUtil.getStringData(AppConstant.USER_LOCAL_DATA));
                                DATB.addUserProperty(Util.toMap(json));
                            }
                            if (!preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EN).isEmpty() && !preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EV).isEmpty()) {
                                JSONObject json  = new JSONObject(preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EV));
                                DATB.addEvent(preferenceUtil.getStringData(AppConstant.EVENT_LOCAL_DATA_EN), Util.toMap(json));
                            }
                            if (preferenceUtil.getBoolean(AppConstant.IS_SET_SUBSCRIPTION_METHOD))
                                DATB.setSubscription(preferenceUtil.getBoolean(AppConstant.SET_SUBSCRITION_LOCAL_DATA));
                            if (!preferenceUtil.getStringData(AppConstant.SUBSCRIBER_ID_DATA).isEmpty())
                                DATB.setSubscriberID(preferenceUtil.getStringData(AppConstant.SUBSCRIBER_ID_DATA));

                        } catch (Exception e) {
                            Util.setException(context, e.toString(), "registerToken", AppConstant.APP_NAME_TAG);
                        }
                        if (preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty())
                            preferenceUtil.setLongData(AppConstant.DEVICE_REGISTRATION_TIMESTAMP, System.currentTimeMillis());

                    }
                    @Override
                    void onFailure(int statusCode, String response, Throwable throwable) {
                        super.onFailure(statusCode, response, throwable);
                    }
                });
                RestClient.newPostRequest(RestClient.MOMAGIC_SUBSCRIPTION_URL, mapData, new RestClient.ResponseHandler() {
                    @Override
                    void onSuccess(final String response) {
                        super.onSuccess(response);
                    }

                    @Override
                    void onFailure(int statusCode, String response, Throwable throwable) {
                        super.onFailure(statusCode, response, throwable);

                    }
                });
            }catch (Exception e){
                Util.setException(context, e.toString(), "registerToken", AppConstant.APP_NAME_TAG);
            }

        } else {
            DATB.lastVisitApi(context);
        }

    }


}

