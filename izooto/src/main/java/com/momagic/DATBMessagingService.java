/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.momagic;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import com.google.android.datatransport.cct.internal.LogEvent;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class DATBMessagingService extends FirebaseMessagingService {
    private  Payload payload = null;
    private final String DATB_TAG_NAME="DATBMessagingService";
    private final String IZ_METHOD_NAME = "handleNow";
    private final String IZ_METHOD_PUSH_NAME ="contentPush";
    private  final String IZ_ERROR_NAME ="Payload Error";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        executeBackgroundTask(remoteMessage);
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            executorService.execute(runnable);
        } catch (Exception ex){
            Util.handleExceptionOnce(this, remoteMessage + ex.toString(), DATB_TAG_NAME, "onMessageReceived");
        }
    }
    private void executeBackgroundTask(RemoteMessage remoteMessage) {
        try {
            if (remoteMessage.getData().size() > 0) {
                Log.v("Push Type", "fcm");
                PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(this);
                if (preferenceUtil.getEnableState(AppConstant.NOTIFICATION_ENABLE_DISABLE)) {
                    Map<String, String> data = remoteMessage.getData();
                    handleNow(data);
                }
            }
            if (remoteMessage.getNotification() != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sendNotification(remoteMessage);
                }
            }
        } catch (Exception ex) {
            Util.handleExceptionOnce(this, remoteMessage + ex.toString(), DATB_TAG_NAME, "executeBackgroundTask");
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(android.R.drawable.ic_popup_reminder)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    AppConstant.CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


   }
    public   void handleNow(final Map<String, String> data) {
        Log.d(AppConstant.APP_NAME_TAG, AppConstant.NOTIFICATION_RECEIVED);
        PreferenceUtil preferenceUtil =PreferenceUtil.getInstance(this);
               try {
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
                             int cfg=jsonObject.optInt(ShortpayloadConstant.CFG);
                             String cfgData=Util.getIntegerToBinary(cfg);
                             if(cfgData!=null && !cfgData.isEmpty()) {
                                 String impIndex = String.valueOf(cfgData.charAt(cfgData.length() - 1));
                                 if(impIndex.equalsIgnoreCase("1"))
                                 {
                                     NotificationEventManager.impressionNotification(RestClient.IMPRESSION_URL, cid, rid, -1,AppConstant.PUSH_FCM);
                                 }
                             }
                             AdMediation.getMediationGPL(this, jsonObject, urlData);
                             preferenceUtil.setBooleanData(AppConstant.MEDIATION, false);
                         }
                         else
                         {
                             NotificationEventManager.handleNotificationError("Payload Error",data.toString(),"MessagingServices","handleNow");
                         }
                      }
                      catch (Exception ex)
                      {
                        if(!preferenceUtil.getBoolean("IsException")) {
                            preferenceUtil.setBooleanData("IsException",true);
                            Util.handleExceptionOnce(this, ex + "PayloadError" + data, "DATBMessagingService", "handleNow");
                        }
                          DebugFileManager.createExternalStoragePublic(this,data.toString(),"[Log.v]->");
                      }
                   }
                   else {
                       try {
                           JSONObject jsonObject = new JSONObject(data.get(AppConstant.GLOBAL));
                           String cid = jsonObject.optString(ShortpayloadConstant.ID);
                           String rid = jsonObject.optString(ShortpayloadConstant.RID);
                             int cfg=jsonObject.optInt(ShortpayloadConstant.CFG);
                             String cfgData=Util.getIntegerToBinary(cfg);
                                if(cfgData!=null && !cfgData.isEmpty()) {
                                   String impIndex = String.valueOf(cfgData.charAt(cfgData.length() - 1));
                                   if(impIndex.equalsIgnoreCase("1"))
                                   {
                                       NotificationEventManager.impressionNotification(RestClient.IMPRESSION_URL, cid, rid, -1,AppConstant.PUSH_FCM);
                                   }
                                }
                           JSONObject jsonObject1=new JSONObject(data.toString());
                           AdMediation.getMediationData(this, jsonObject1,"fcm","");
                           preferenceUtil.setBooleanData(AppConstant.MEDIATION, true);
                       }
                       catch (Exception ex)
                       {
                           DebugFileManager.createExternalStoragePublic(this,data.toString(),"[Log.v]->");

                       }
                   }
                }
                else {
                    preferenceUtil.setBooleanData(AppConstant.MEDIATION, false);
                    JSONObject payloadObj = new JSONObject(data);
                    if (payloadObj.optLong(ShortpayloadConstant.CREATEDON) > PreferenceUtil.getInstance(this).getLongValue(AppConstant.DEVICE_REGISTRATION_TIMESTAMP)) {
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
                        try {
                            if (payload.getRid() != null && !payload.getRid().isEmpty()) {
                                preferenceUtil.setIntData(ShortpayloadConstant.OFFLINE_CAMPAIGN, Util.getValidIdForCampaigns(payload));
                            } else {
                                DebugFileManager.createExternalStoragePublic(DATB.appContext, IZ_METHOD_PUSH_NAME, data.toString());
                            }

                        } catch (Exception e) {
                            DebugFileManager.createExternalStoragePublic(DATB.appContext, IZ_METHOD_PUSH_NAME, e.toString());
                        }
                        if (DATB.appContext == null) {
                            DATB.appContext = this;
                        }

                        final Handler mainHandler = new Handler(Looper.getMainLooper());
                        final Runnable myRunnable = () -> {
                            NotificationEventManager.handleImpressionAPI(payload, AppConstant.PUSH_FCM);
                            DATB.processNotificationReceived(DATB.appContext, payload);
                        };

                        try {
                            NotificationExecutorService notificationExecutorService = new NotificationExecutorService(this);
                            notificationExecutorService.executeNotification(mainHandler, myRunnable, payload);

                        } catch (Exception e) {
                            Util.handleExceptionOnce(DATB.appContext, e.toString(), DATB_TAG_NAME, IZ_METHOD_NAME + "notificationExecutorService");
                        }
                        DebugFileManager.createExternalStoragePublic(DATB.appContext, data.toString(), " Log-> ");


                    } else {
                        String updateDaily = NotificationEventManager.getDailyTime(this);
                        if (!updateDaily.equalsIgnoreCase(Util.getTime())) {
                            preferenceUtil.setStringData(AppConstant.CURRENT_DATE_VIEW_DAILY, Util.getTime());
                            NotificationEventManager.handleNotificationError("Payload Error" + payloadObj.optString("t"), payloadObj.toString(), "DATBMESSAGINSERVEICES", "handleNow()");
                        }
                    }
                }
               } catch (Exception e) {
                   DebugFileManager.createExternalStoragePublic(DATB.appContext,data.toString(),"[Log.v]->");
            }
    }
}