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
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class XiaomiPushReceiver extends PushMessageReceiver {
    private String TAG="XiaomiPushReceiver  PAYLOAD";
    private Payload payload;
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
        super.onReceivePassThroughMessage(context, miPushMessage);
        String payload = miPushMessage.getContent();
        Log.v("Push Type","Xiaomi");

        if(payload!=null && !payload.isEmpty()) {
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            if (preferenceUtil.getEnableState(AppConstant.NOTIFICATION_ENABLE_DISABLE)) {
                handleNow(context, payload);
            }
        }
    }
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
        super.onNotificationMessageArrived(context, miPushMessage);
        Log.v(TAG, miPushMessage.getContent());
         miPushMessage.getMessageId();
        String payload = miPushMessage.getContent();
        if(payload!=null && !payload.isEmpty())
            handleNow(context,payload);
    }

    private void handleNow(Context context, String data) {
        Log.d(TAG, AppConstant.NOTIFICATION_RECEIVED);
        try {
            PreferenceUtil preferenceUtil =PreferenceUtil.getInstance(context);
            JSONObject payloadObj = new JSONObject(data);
            if(payloadObj.has(AppConstant.AD_NETWORK) || payloadObj.has(AppConstant.GLOBAL) || payloadObj.has(AppConstant.GLOBAL_PUBLIC_KEY))
            {
                if(payloadObj.has(AppConstant.GLOBAL_PUBLIC_KEY))
                {
                    try
                    {
                        JSONObject jsonObject=new JSONObject(Objects.requireNonNull(payloadObj.optString(AppConstant.GLOBAL)));
                        String urlData=payloadObj.optString(AppConstant.GLOBAL_PUBLIC_KEY);
                        if(jsonObject.toString()!=null && urlData!=null && !urlData.isEmpty()) {
                            String cid = jsonObject.optString(ShortpayloadConstant.ID);
                            String rid = jsonObject.optString(ShortpayloadConstant.RID);
                            int cfg=jsonObject.optInt(ShortpayloadConstant.CFG);
                            String cfgData=Util.getIntegerToBinary(cfg);
                            if(cfgData!=null && !cfgData.isEmpty()) {
                                String impIndex = String.valueOf(cfgData.charAt(cfgData.length() - 1));
                                if(impIndex.equalsIgnoreCase("1"))
                                {
                                    NotificationEventManager.impressionNotification(RestClient.IMPRESSION_URL, cid, rid, -1,AppConstant.PUSH_XIAOMI);

                                }

                            }
                            AdMediation.getMediationGPL(context, jsonObject, urlData);
                            preferenceUtil.setBooleanData(AppConstant.MEDIATION, false);

                        }
                        else
                        {
                            NotificationEventManager.handleNotificationError("Payload Error",data.toString(),"MessagingSevices","HandleNow");
                        }
                    }
                    catch (Exception ex)
                    {
                        Util.setException(context,ex.toString()+"PayloadError"+data.toString(),"DATBMessagingService","handleNow");
                    }

                }
                else {
                    try {
                        JSONObject jsonObject = new JSONObject(payloadObj.optString(AppConstant.GLOBAL));
                        String cid = jsonObject.optString(ShortpayloadConstant.ID);
                        String rid = jsonObject.optString(ShortpayloadConstant.RID);
                        int cfg=jsonObject.optInt(ShortpayloadConstant.CFG);
                        String cfgData=Util.getIntegerToBinary(cfg);
                        if(cfgData!=null && !cfgData.isEmpty()) {
                            String impIndex = String.valueOf(cfgData.charAt(cfgData.length() - 1));
                            if(impIndex.equalsIgnoreCase("1"))
                            {
                                NotificationEventManager.impressionNotification(RestClient.IMPRESSION_URL, cid, rid, -1,AppConstant.PUSH_XIAOMI);

                            }

                        }

                       // NotificationEventManager.impressionNotification(RestClient.IMPRESSION_URL, cid, rid, -1,AppConstant.PUSH_XIAOMI);
                        JSONObject jsonObject1=new JSONObject(data.toString());
                        AdMediation.getMediationData(context, jsonObject1,AppConstant.PUSH_XIAOMI,"");
                        // AdMediation.getAdNotificationData(this,jsonObject1,"FCM");
                        preferenceUtil.setBooleanData(AppConstant.MEDIATION, true);
                    }
                    catch (Exception ex)
                    {
                        Util.setException(context,ex+"PayloadError"+data,"DATBMessagingService","handleNow");

                    }
                }
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
                            payload.setGroupKey(payloadObj.optString(ShortpayloadConstant.GKEY));
                            payload.setGroupMessage(payloadObj.optString(ShortpayloadConstant.GMESSAGE));
                            payload.setFromProjectNumber(payloadObj.optString(ShortpayloadConstant.PROJECTNUMBER));
                            payload.setCollapseId(payloadObj.optString(ShortpayloadConstant.COLLAPSEID));
                            payload.setRawPayload(payloadObj.optString(ShortpayloadConstant.RAWDATA));
                            payload.setAp(payloadObj.optString(ShortpayloadConstant.ADDITIONALPARAM));
                            payload.setCfg(payloadObj.optInt(ShortpayloadConstant.CFG));
                            payload.setPush_type(AppConstant.PUSH_FCM);
                            payload.setMaxNotification(payloadObj.optInt(ShortpayloadConstant.MAX_NOTIFICATION));
                            payload.setFallBackDomain(payloadObj.optString(ShortpayloadConstant.FALL_BACK_DOMAIN));
                            payload.setFallBackSubDomain(payloadObj.optString(ShortpayloadConstant.FALLBACK_SUB_DOMAIN));
                            payload.setFallBackPath(payloadObj.optString(ShortpayloadConstant.FAll_BACK_PATH));
                            payload.setDefaultNotificationPreview(payloadObj.optInt(ShortpayloadConstant.TEXTOVERLAY));
                            payload.setNotification_bg_color(payloadObj.optString(ShortpayloadConstant.BGCOLOR));
                            payload.setExpiryTimerValue(payloadObj.optString(ShortpayloadConstant.EXPIRY_TIMER_VALUE));
                            payload.setMakeStickyNotification(payloadObj.optString(ShortpayloadConstant.MAKE_STICKY_NOTIFICATION));


                            // Notification Channel .............
                            payload.setLockScreenVisibility(payloadObj.optInt(ShortpayloadConstant.VISIBILITY));
                            payload.setLedColor(payloadObj.optString(ShortpayloadConstant.LEDCOLOR));
                            payload.setChannel(payloadObj.optString(ShortpayloadConstant.NOTIFICATION_CHANNEL));
                            payload.setVibration(payloadObj.optString(ShortpayloadConstant.VIBRATION));
                            payload.setBadge(payloadObj.optInt(ShortpayloadConstant.BADGE));
                            payload.setOtherChannel(payloadObj.optString(ShortpayloadConstant.OTHER_CHANNEL));
                            payload.setSound(payloadObj.optString(ShortpayloadConstant.SOUND));
                            payload.setPriority(payloadObj.optInt(ShortpayloadConstant.PRIORITY));
                        }
                        else {
                            return;
                        }
                        if (DATB.appContext == null)
                            DATB.appContext = context;
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                NotificationEventManager.handleImpressionAPI(payload,AppConstant.PUSH_XIAOMI);
                                DATB.processNotificationReceived(context,payload);

                            } // This is your code
                        };
                        mainHandler.post(myRunnable);
                    }
            DebugFileManager.createExternalStoragePublic(DATB.appContext,"MIPush",data.toString());

            } catch (Exception e) {
            DebugFileManager.createExternalStoragePublic(DATB.appContext, e.toString(),"[Log.e]->MIPush");
            Util.setException(context, e.toString(), TAG, "handleNow");
            }
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
                    DebugFileManager.createExternalStoragePublic(DATB.appContext,mi_token,"[Log.e]-> MI Token->");

                    registerToken(context,mi_token);
                }
            }
            catch (Exception ex)
            {
                //Log.e("XMPush",ex.toString());
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
                mapData.put(AppConstant.ADD_URL, "" + AppConstant.STYPE);
                mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                mapData.put(AppConstant.BTYPE_,"" + AppConstant.BTYPE);
                mapData.put(AppConstant.DTYPE_,"" + AppConstant.DTYPE);
                mapData.put(AppConstant.TIMEZONE,"" + System.currentTimeMillis());
                mapData.put(AppConstant.APP_VERSION,"" + Util.getAppVersion(context));
                mapData.put(AppConstant.OS,"" + AppConstant.SDKOS);
                mapData.put(AppConstant.ALLOWED_,"" + AppConstant.ALLOWED);
                mapData.put(AppConstant.ANDROID_ID,"" + Util.getAndroidId(context));
                mapData.put(AppConstant.CHECK_SDK_VERSION,"" + AppConstant.SDK_VERSION);
                mapData.put(AppConstant.LANGUAGE,"" + Util.getDeviceLanguage());
                mapData.put(AppConstant.QSDK_VERSION ,"" + AppConstant.SDK_VERSION);
                mapData.put(AppConstant.TOKEN,"" + preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                mapData.put(AppConstant.ADVERTISEMENTID,"" + preferenceUtil.getStringData(AppConstant.ADVERTISING_ID));
                mapData.put(AppConstant.XIAOMITOKEN,miToken);
                mapData.put(AppConstant.PACKAGE_NAME,"" + context.getPackageName());
                mapData.put(AppConstant.SDKTYPE,"" + DATB.SDKDEF);
                mapData.put(AppConstant.KEY_HMS,"" + preferenceUtil.getStringData(AppConstant.HMS_TOKEN));
                mapData.put(AppConstant.ANDROID_VERSION,"" + Build.VERSION.RELEASE);
                mapData.put(AppConstant.DEVICE_NAME,"" + Util.getDeviceName());
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

                RestClient.postRequest(RestClient.BASE_URL, mapData, null,new RestClient.ResponseHandler() {
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
                            Util.setException(context, e.toString(), "xiaomi_registration ", AppConstant.APP_NAME_TAG);
                        }
                        if (preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN).isEmpty() || preferenceUtil.getStringData(AppConstant.HMS_TOKEN).isEmpty())
                            preferenceUtil.setLongData(AppConstant.DEVICE_REGISTRATION_TIMESTAMP, System.currentTimeMillis());

                    }
                    @Override
                    void onFailure(int statusCode, String response, Throwable throwable) {
                        super.onFailure(statusCode, response, throwable);
                    }
                });
                RestClient.postRequest(RestClient.MOMAGIC_SUBSCRIPTION_URL, mapData, null,new RestClient.ResponseHandler() {
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
                Util.setException(context, e.toString(), "MIRegisterToken", AppConstant.APP_NAME_TAG);
            }

        } else {
            DATB.lastVisitApi(context);
        }

    }


}

