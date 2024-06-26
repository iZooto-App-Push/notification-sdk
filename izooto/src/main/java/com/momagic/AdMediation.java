package com.momagic;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdMediation {
    private static Payload payload;
    private static final List<Payload> payloadList = new ArrayList<>();
    private static final List<Payload> adPayload = new ArrayList<>();
    private static final List<Payload> passiveList = new ArrayList<>();
     static final List<JSONObject> failsList = new ArrayList<>();
    public static List<String> clicksData = new ArrayList<>();
     static final List<JSONObject> successList=new ArrayList<>();
    static List<String> storeList=new ArrayList<>();
    static  int counterIndex = 0;

    // handle the mediation payload data
    public static void getMediationData(Context context , JSONObject data, String pushType, String globalPayloadObject)
    {
        if(context!=null) {
            try {
                counterIndex= 0;
                PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
                payloadList.clear();
                passiveList.clear();
                adPayload.clear();
                clicksData.clear();
                successList.clear();
                failsList.clear();
                storeList.clear();
                JSONObject jsonObject = null;
                if(globalPayloadObject!= null && !globalPayloadObject.isEmpty()){
                    jsonObject=new JSONObject(globalPayloadObject);
                }
                else {
                    jsonObject = data.getJSONObject(AppConstant.GLOBAL);
                }
                JSONArray jsonArray = data.getJSONArray(AppConstant.AD_NETWORK);
                long start = System.currentTimeMillis(); //fetch start time
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject payloadObj = jsonArray.getJSONObject(i);
                        if (jsonObject.optLong(ShortPayloadConstant.CREATED_ON) > PreferenceUtil.getInstance(context).getLongValue(AppConstant.DEVICE_REGISTRATION_TIMESTAMP)) {
                            payload = new Payload();
                            payload.setAd_type(jsonObject.optString(ShortPayloadConstant.AD_TYPE));
                            payload.setAdID(payloadObj.optString(ShortPayloadConstant.AD_ID));
                            payload.setReceived_bid(payloadObj.optString(ShortPayloadConstant.RECEIVED_BID).replace("['", "").replace("']", ""));
                            payload.setFetchURL(payloadObj.optString(ShortPayloadConstant.FETCH_URL));
                            payload.setKey(jsonObject.optString(ShortPayloadConstant.KEY));
                            if(jsonObject.has(ShortPayloadConstant.ID))
                                payload.setId(jsonObject.optString(ShortPayloadConstant.ID).replace("['", "").replace("']", ""));
                            else
                                payload.setId(payloadObj.optString(ShortPayloadConstant.ID).replace("['", "").replace("']", ""));

                            payload.setStartTime(start);
                            payload.setRid(jsonObject.optString(ShortPayloadConstant.RID));
                            payload.setLink(payloadObj.optString(ShortPayloadConstant.LINK).replace("['", "").replace("']", ""));
                            payload.setTitle(payloadObj.optString(ShortPayloadConstant.TITLE).replace("['", "").replace("']", ""));
                            payload.setMessage(payloadObj.optString(ShortPayloadConstant.NMESSAGE).replace("['", "").replace("']", ""));
                            payload.setIcon(payloadObj.optString(ShortPayloadConstant.ICON).replace("['", "").replace("']", ""));
                            payload.setReqInt(payloadObj.optInt(ShortPayloadConstant.REQINT));
                            payload.setTag(payloadObj.optString(ShortPayloadConstant.TAG));
                            payload.setBanner(payloadObj.optString(ShortPayloadConstant.BANNER).replace("['", "").replace("']", ""));
                            payload.setBadgeicon(payloadObj.optString(ShortPayloadConstant.BADGE_ICON).replace("['", "").replace("']", ""));
                            payload.setBadgecolor(payloadObj.optString(ShortPayloadConstant.BADGE_COLOR).replace("['", "").replace("']", ""));
                            payload.setSubTitle(payloadObj.optString(ShortPayloadConstant.SUBTITLE).replace("['", "").replace("']", ""));
                            payload.setGroup(payloadObj.optInt(ShortPayloadConstant.GROUP));
                            payload.setBadgeCount(payloadObj.optInt(ShortPayloadConstant.BADGE_COUNT));
                            if (jsonObject.has("b")) {
                                payload.setAct_num(jsonObject.optInt(ShortPayloadConstant.ACTNUM));
                                payload.setAct1name(jsonObject.optString(ShortPayloadConstant.ACT1NAME).replace("['", "").replace("']", ""));
                                payload.setAct2name(jsonObject.optString(ShortPayloadConstant.ACT2NAME).replace("['", "").replace("']", ""));

                            } else {
                                payload.setAct_num(jsonObject.optInt(ShortPayloadConstant.ACTNUM));
                                payload.setAct1name(payloadObj.optString(ShortPayloadConstant.ACT1NAME).replace("['", "").replace("']", ""));
                                payload.setAct2name(payloadObj.optString(ShortPayloadConstant.ACT2NAME).replace("['", "").replace("']", ""));

                            }

                            // Button 1
                            payload.setAct1link(payloadObj.optString(ShortPayloadConstant.ACT1LINK).replace("['", "").replace("']", ""));
                            payload.setAct1icon(payloadObj.optString(ShortPayloadConstant.ACT1ICON).replace("['", "").replace("']", ""));
                            payload.setAct1ID(payloadObj.optString(ShortPayloadConstant.ACT1ID));
                            // Button 2
                            payload.setAct2link(payloadObj.optString(ShortPayloadConstant.ACT2LINK).replace("['", "").replace("']", ""));
                            payload.setAct2icon(payloadObj.optString(ShortPayloadConstant.ACT2ICON));
                            payload.setAct2ID(payloadObj.optString(ShortPayloadConstant.ACT2ID));
                            payload.setInapp(payloadObj.optInt(ShortPayloadConstant.INAPP));
                            payload.setTrayicon(payloadObj.optString(ShortPayloadConstant.TARYICON));
                            payload.setSmallIconAccentColor(payloadObj.optString(ShortPayloadConstant.ICONCOLOR));
                            payload.setSound(payloadObj.optString(ShortPayloadConstant.SOUND));
                            payload.setLedColor(payloadObj.optString(ShortPayloadConstant.LEDCOLOR));
                            payload.setLockScreenVisibility(payloadObj.optInt(ShortPayloadConstant.VISIBILITY));
                            payload.setGroupKey(payloadObj.optString(ShortPayloadConstant.GKEY));
                            payload.setGroupMessage(payloadObj.optString(ShortPayloadConstant.GMESSAGE));
                            payload.setFromProjectNumber(payloadObj.optString(ShortPayloadConstant.PROJECTNUMBER));
                            payload.setCollapseId(payloadObj.optString(ShortPayloadConstant.COLLAPSEID));
                            payload.setPriority(payloadObj.optInt(ShortPayloadConstant.PRIORITY));
                            payload.setRawPayload(payloadObj.optString(ShortPayloadConstant.RAWDATA));
                            payload.setAp(payloadObj.optString(ShortPayloadConstant.ADDITIONALPARAM));
                            payload.setCfg(jsonObject.optInt(ShortPayloadConstant.CFG));
                            payload.setCpc(payloadObj.optString(ShortPayloadConstant.CPC).replace("['", "").replace("']", ""));
                            payload.setRc(payloadObj.optString(ShortPayloadConstant.RC));
                            payload.setRv(payloadObj.optString(ShortPayloadConstant.RV));
                            payload.setPassive_flag(payloadObj.optString(ShortPayloadConstant.Passive_Flag));
                            payload.setCpm(payloadObj.optString(ShortPayloadConstant.CPM).replace("['", "").replace("']", ""));
                            payload.setCtr(payloadObj.optString(ShortPayloadConstant.CTR).replace("['", "").replace("']", ""));
                            payload.setFallBackDomain(jsonObject.optString(ShortPayloadConstant.FALL_BACK_DOMAIN));
                            payload.setFallBackSubDomain(jsonObject.optString(ShortPayloadConstant.FALLBACK_SUB_DOMAIN));
                            payload.setFallBackPath(jsonObject.optString(ShortPayloadConstant.FAll_BACK_PATH));
                            payload.setTime_out(jsonObject.optInt(ShortPayloadConstant.TIME_OUT));
                            payload.setAdTimeOut(payloadObj.optInt(ShortPayloadConstant.AD_TIME_OUT));
                            payload.setCreated_Time(jsonObject.optString(ShortPayloadConstant.CREATED_ON));
                            payload.setPush_type(pushType);
                            payload.setDefaultNotificationPreview(jsonObject.optInt(ShortPayloadConstant.TEXTOVERLAY));
                            payload.setMakeStickyNotification(jsonObject.optString(ShortPayloadConstant.MAKE_STICKY_NOTIFICATION));  // Add sticky value


                            if (payload.getPassive_flag().equalsIgnoreCase("1") && jsonObject.optString(AppConstant.AD_TYPE).equalsIgnoreCase("6")) {
                                passiveList.add(payload);
                            } else {
                                payloadList.add(payload);
                            }
                        } else  {
                            String updateDaily=NotificationEventManager.getDailyTime(context);
                            if (!updateDaily.equalsIgnoreCase(Util.getTime())) {
                                preferenceUtil.setStringData(AppConstant.CURRENT_DATE_VIEW_DAILY, Util.getTime());
                                NotificationEventManager.handleNotificationError("Payload Error" + payloadObj.optString("t"), null, "AdMediation", "getAdJsonData()");
                            }
                            return;
                        }
                    }
                    if (payloadList.size() > 0) {
                        if (jsonObject.optString(AppConstant.AD_TYPE).equalsIgnoreCase("4")) {
                            processPayload(payloadList.get(0), 4, 0);
                        }
                        if (jsonObject.optString(AppConstant.AD_TYPE).equalsIgnoreCase("5")) {
                            preferenceUtil.setBooleanData("Send", true);
                            for (int i = 0; i < payloadList.size(); i++) {
                                if (preferenceUtil.getBoolean("Send")) {
                                    processPayload(payloadList.get(i), 5, i);
                                    Thread.sleep(2000);
                                }
                            }
                        }
                        if (jsonObject.optString(AppConstant.AD_TYPE).equalsIgnoreCase("6")) {
                            int i = 0;
                            do {
                                processPayload(payloadList.get(i), 6, i);
                                Thread.sleep(2000);
                                i++;
                            } while (i < payloadList.size());
                        }
                    }
                }
            } catch (Exception ex) {
                DebugFileManager.createExternalStoragePublic(DATB.appContext,"JSONException"+ex,"[Log.e]->AdMediation");
                Util.handleExceptionOnce(context, ex.toString(), "AdMediation", "getJSONData"); // handle one time
            }
        }
    }


    //handle gpl payload
    static  void getMediationGPL(Context context , JSONObject payloadObj, String url)
    {
        if(context == null)
            return;
        else
        {
            try {
                if (payloadObj != null && url != null && !url.isEmpty()) {
                    if (payloadObj.optLong(ShortPayloadConstant.CREATED_ON) > PreferenceUtil.getInstance(context).getLongValue(AppConstant.DEVICE_REGISTRATION_TIMESTAMP)) {
                        payload = new Payload();
                        globalPayload(url,payload,payloadObj);
                    } else {
                        String updateDaily = NotificationEventManager.getDailyTime(context);
                        if (!updateDaily.equalsIgnoreCase(Util.getTime())) {
                            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
                            preferenceUtil.setStringData(AppConstant.CURRENT_DATE_VIEW_DAILY, Util.getTime());
                            NotificationEventManager.handleNotificationError("Payload Error" + payloadObj.optString("t"), null, "AdMediation", "GPL()");
                        }
                        return;
                    }
                } else {
                    PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(context);
                    String data=preferenceUtil.getStringData("iz_GPL_FIRST_TIME");
                    if (!data.equalsIgnoreCase(Util.getTime())) {
                        preferenceUtil.setStringData("iz_GPL_FIRST_TIME", Util.getTime());
                        NotificationEventManager.handleNotificationError("Payload Error" + payloadObj.optString("t"), null, "AdMediation", "GPL()");
                    }
                }
            }
            catch (Exception ex)
            {
                PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(context);
                String data=preferenceUtil.getStringData("iz_GPL_EXCEPTION");
                if (!data.equalsIgnoreCase(Util.getTime())) {
                    preferenceUtil.setStringData("iz_GPL_EXCEPTION", Util.getTime());
                    Util.setException(context,ex.toString(),"AdMediation","getMediationGPL");
                }
            }
        }
    }
    //handle the global payload
    static  void globalPayload(String url, Payload payload, JSONObject globalPayloadObject) {
        if (url != null && DATB.appContext != null) {
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(DATB.appContext);
            if (preferenceUtil.getStringData(AppConstant.STORAGE_PAYLOAD_DATA) != null && !url.equalsIgnoreCase(checkURL(preferenceUtil.getStringData(AppConstant.STORAGE_PAYLOAD_DATA)))) {
                RestClient.get(url, new RestClient.ResponseHandler() {
                    @Override
                    void onSuccess(String response) {
                        super.onSuccess(response);
                        try {

                            JSONObject jsonObject = new JSONObject(response.replace("\n", ""));
                            if (jsonObject != null) {
                                if(jsonObject.has("an")) {
                                    getMediationData(DATB.appContext,jsonObject,payload.getPush_type(),globalPayloadObject.toString());
                                    return;
                                }
                                if(globalPayloadObject.has(ShortPayloadConstant.CREATED_ON)) {
                                    payload.setCreated_Time(globalPayloadObject.optString(ShortPayloadConstant.CREATED_ON));
                                }else {
                                    payload.setCreated_Time(jsonObject.optString(ShortPayloadConstant.CREATED_ON));
                                }
                                if(globalPayloadObject.has(ShortPayloadConstant.KEY)) {
                                    payload.setKey(globalPayloadObject.optString(ShortPayloadConstant.KEY));
                                }else {
                                    payload.setKey(jsonObject.optString(ShortPayloadConstant.KEY));
                                }
                                if(globalPayloadObject.has(ShortPayloadConstant.ID)) {
                                    payload.setId(globalPayloadObject.optString(ShortPayloadConstant.ID));
                                }else {
                                    payload.setId(jsonObject.optString(ShortPayloadConstant.ID));
                                }
                                if(globalPayloadObject.has(ShortPayloadConstant.RID))
                                {
                                    payload.setRid(globalPayloadObject.optString(ShortPayloadConstant.RID));

                                }else
                                {
                                    payload.setRid(jsonObject.optString(ShortPayloadConstant.RID));
                                }
                                payload.setFetchURL(jsonObject.optString(ShortPayloadConstant.FETCH_URL));
                                payload.setLink(jsonObject.optString(ShortPayloadConstant.LINK));
                                payload.setTitle(jsonObject.optString(ShortPayloadConstant.TITLE));
                                payload.setMessage(jsonObject.optString(ShortPayloadConstant.NMESSAGE));
                                payload.setIcon(jsonObject.optString(ShortPayloadConstant.ICON));
                                if(globalPayloadObject.has(ShortPayloadConstant.REQINT))
                                {
                                    payload.setReqInt(globalPayloadObject.optInt(ShortPayloadConstant.REQINT));
                                }else
                                {
                                    payload.setReqInt(jsonObject.optInt(ShortPayloadConstant.REQINT));
                                }
                                if(globalPayloadObject.has(ShortPayloadConstant.TAG))
                                {
                                    payload.setTag(globalPayloadObject.optString(ShortPayloadConstant.TAG));
                                }else
                                {
                                    payload.setTag(jsonObject.optString(ShortPayloadConstant.TAG));
                                }
                                if(globalPayloadObject.has(ShortPayloadConstant.ACT1NAME))
                                {
                                    payload.setAct1name(globalPayloadObject.optString(ShortPayloadConstant.ACT1NAME));

                                }else
                                {
                                    payload.setAct1name(jsonObject.optString(ShortPayloadConstant.ACT1NAME));
                                }
                                if(globalPayloadObject.has(ShortPayloadConstant.ACT1LINK))
                                {
                                    payload.setAct1link(globalPayloadObject.optString(ShortPayloadConstant.ACT1LINK));
                                }
                                else {
                                    payload.setAct1link(jsonObject.optString(ShortPayloadConstant.ACT1LINK));
                                }
                                payload.setBanner(jsonObject.optString(ShortPayloadConstant.BANNER));
                                payload.setAct_num(jsonObject.optInt(ShortPayloadConstant.ACTNUM));
                                payload.setBadgeicon(jsonObject.optString(ShortPayloadConstant.BADGE_ICON));
                                payload.setBadgecolor(jsonObject.optString(ShortPayloadConstant.BADGE_COLOR));
                                payload.setSubTitle(jsonObject.optString(ShortPayloadConstant.SUBTITLE));
                                payload.setGroup(jsonObject.optInt(ShortPayloadConstant.GROUP));
                                payload.setBadgeCount(jsonObject.optInt(ShortPayloadConstant.BADGE_COUNT));
                                // Button 1
                                payload.setAct1icon(jsonObject.optString(ShortPayloadConstant.ACT1ICON));
                                payload.setAct1ID(jsonObject.optString(ShortPayloadConstant.ACT1ID));
                                // Button 2
                                payload.setAct2name(jsonObject.optString(ShortPayloadConstant.ACT2NAME));
                                payload.setAct2link(jsonObject.optString(ShortPayloadConstant.ACT2LINK));
                                payload.setAct2icon(jsonObject.optString(ShortPayloadConstant.ACT2ICON));
                                payload.setAct2ID(jsonObject.optString(ShortPayloadConstant.ACT2ID));
                                payload.setInapp(jsonObject.optInt(ShortPayloadConstant.INAPP));
                                payload.setTrayicon(jsonObject.optString(ShortPayloadConstant.TARYICON));
                                payload.setSmallIconAccentColor(jsonObject.optString(ShortPayloadConstant.ICONCOLOR));
                                payload.setSound(jsonObject.optString(ShortPayloadConstant.SOUND));
                                payload.setLedColor(jsonObject.optString(ShortPayloadConstant.LEDCOLOR));
                                payload.setLockScreenVisibility(jsonObject.optInt(ShortPayloadConstant.VISIBILITY));
                                payload.setGroupKey(jsonObject.optString(ShortPayloadConstant.GKEY));
                                payload.setGroupMessage(jsonObject.optString(ShortPayloadConstant.GMESSAGE));
                                payload.setFromProjectNumber(jsonObject.optString(ShortPayloadConstant.PROJECTNUMBER));
                                payload.setCollapseId(jsonObject.optString(ShortPayloadConstant.COLLAPSEID));
                                payload.setPriority(jsonObject.optInt(ShortPayloadConstant.PRIORITY));
                                payload.setRawPayload(jsonObject.optString(ShortPayloadConstant.RAWDATA));
                                payload.setAp(jsonObject.optString(ShortPayloadConstant.ADDITIONALPARAM));
                                if(globalPayloadObject.has(ShortPayloadConstant.CFG)) {
                                    payload.setCfg(globalPayloadObject.optInt(ShortPayloadConstant.CFG));
                                }else
                                {
                                    payload.setCfg(jsonObject.optInt(ShortPayloadConstant.CFG));
                                }
                                payload.setPush_type(AppConstant.PUSH_FCM);
                                payload.setPublic_global_key(url);
                                payload.setSound(jsonObject.optString(ShortPayloadConstant.SOUND));
                                payload.setMaxNotification(jsonObject.optInt(ShortPayloadConstant.MAX_NOTIFICATION));
                                payload.setFallBackDomain(jsonObject.optString(ShortPayloadConstant.FALL_BACK_DOMAIN));
                                payload.setFallBackSubDomain(jsonObject.optString(ShortPayloadConstant.FALLBACK_SUB_DOMAIN));
                                payload.setFallBackPath(jsonObject.optString(ShortPayloadConstant.FAll_BACK_PATH));
                                DebugFileManager.createExternalStoragePublic(DATB.appContext,response,"gpl_payload");

                                if (payload.getTitle() != null && !payload.getTitle().isEmpty()) {
                                    DATB.processNotificationReceived(DATB.appContext,payload);
                                    JSONObject storeObject=new JSONObject();
                                    storeObject.put(AppConstant.IZ_GPL_URL,url);
                                    storeObject.put("PayloadData",jsonObject.toString());
                                    preferenceUtil.setStringData(AppConstant.STORAGE_PAYLOAD_DATA,storeObject.toString());

                                } else {
                                    String fallBackURL = callFallbackAPI(payload);
                                    ShowFallBackResponse(fallBackURL, payload);
                                    PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(DATB.appContext);
                                    String cTime = preferenceUtil.getStringData("iz_gplPayload");
                                    if (!cTime.equalsIgnoreCase(Util.getTime())) {
                                        preferenceUtil.setStringData("iz_gplPayload", Util.getTime());
                                        NotificationEventManager.handleNotificationError("Payload title is empty", payload.toString(), "NotificationEventManager", "globalPayload");
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            DebugFileManager.createExternalStoragePublic(DATB.appContext,ex.toString(),"[Log.e]->globalPayload");
                            Util.handleExceptionOnce(DATB.appContext, ex.toString(), "globalPayload", "AdMediation");// handle exception one time
                        }
                    }

                    @Override
                    void onFailure(int statusCode, String response, Throwable throwable) {
                        super.onFailure(statusCode, response, throwable);
                        String fallBackURL = callFallbackAPI(payload);
                        ShowFallBackResponse(fallBackURL, payload);
                    }
                });
            } else {
                String payloadString = preferenceUtil.getStringData(AppConstant.STORAGE_PAYLOAD_DATA);
                try {
                    if (!payloadString.isEmpty() && payloadString != null) {
                        JSONObject getObjectData=new JSONObject(payloadString);
                        String jsonData = getObjectData.optString("PayloadData");
                        JSONObject jsonObject = new JSONObject(jsonData.replace("\n",""));
                        if (jsonObject != null) {
                            payload.setCreated_Time(globalPayloadObject.optString(ShortPayloadConstant.CREATED_ON));
                            if(globalPayloadObject.has(ShortPayloadConstant.CREATED_ON)) {
                                payload.setCreated_Time(globalPayloadObject.optString(ShortPayloadConstant.CREATED_ON));
                            }else {
                                payload.setCreated_Time(jsonObject.optString(ShortPayloadConstant.CREATED_ON));
                            }
                            if(globalPayloadObject.has(ShortPayloadConstant.KEY)) {
                                payload.setKey(globalPayloadObject.optString(ShortPayloadConstant.KEY));
                            }else {
                                payload.setKey(jsonObject.optString(ShortPayloadConstant.KEY));
                            }
                            if(globalPayloadObject.has(ShortPayloadConstant.ID)) {
                                payload.setId(globalPayloadObject.optString(ShortPayloadConstant.ID));
                            }else {
                                payload.setId(jsonObject.optString(ShortPayloadConstant.ID));
                            }
                            if(globalPayloadObject.has(ShortPayloadConstant.RID))
                            {
                                payload.setRid(globalPayloadObject.optString(ShortPayloadConstant.RID));

                            }else
                            {
                                payload.setRid(jsonObject.optString(ShortPayloadConstant.RID));
                            }

                            payload.setFetchURL(jsonObject.optString(ShortPayloadConstant.FETCH_URL));

                            payload.setLink(jsonObject.optString(ShortPayloadConstant.LINK));
                            payload.setTitle(jsonObject.optString(ShortPayloadConstant.TITLE));
                            payload.setMessage(jsonObject.optString(ShortPayloadConstant.NMESSAGE));
                            payload.setIcon(jsonObject.optString(ShortPayloadConstant.ICON));
                            if(globalPayloadObject.has(ShortPayloadConstant.REQINT))
                            {
                                payload.setReqInt(globalPayloadObject.optInt(ShortPayloadConstant.REQINT));
                            }else
                            {
                                payload.setReqInt(jsonObject.optInt(ShortPayloadConstant.REQINT));
                            }
                            if(globalPayloadObject.has(ShortPayloadConstant.TAG))
                            {
                                payload.setTag(globalPayloadObject.optString(ShortPayloadConstant.TAG));
                            }else
                            {
                                payload.setTag(jsonObject.optString(ShortPayloadConstant.TAG));
                            }
                            if(globalPayloadObject.has(ShortPayloadConstant.ACT1NAME))
                            {
                                payload.setAct1name(globalPayloadObject.optString(ShortPayloadConstant.ACT1NAME));

                            }else
                            {
                                payload.setAct1name(jsonObject.optString(ShortPayloadConstant.ACT1NAME));
                            }
                            if(globalPayloadObject.has(ShortPayloadConstant.ACT1LINK))
                            {
                                payload.setAct1link(globalPayloadObject.optString(ShortPayloadConstant.ACT1LINK));

                            }
                            else
                            {
                                payload.setAct1link(jsonObject.optString(ShortPayloadConstant.ACT1LINK));

                            }
                            payload.setBanner(jsonObject.optString(ShortPayloadConstant.BANNER));
                            payload.setAct_num(jsonObject.optInt(ShortPayloadConstant.ACTNUM));
                            payload.setBadgeicon(jsonObject.optString(ShortPayloadConstant.BADGE_ICON));
                            payload.setBadgecolor(jsonObject.optString(ShortPayloadConstant.BADGE_COLOR));
                            payload.setSubTitle(jsonObject.optString(ShortPayloadConstant.SUBTITLE));
                            payload.setGroup(jsonObject.optInt(ShortPayloadConstant.GROUP));
                            payload.setBadgeCount(jsonObject.optInt(ShortPayloadConstant.BADGE_COUNT));
                            // Button 1
                            payload.setAct1icon(jsonObject.optString(ShortPayloadConstant.ACT1ICON));
                            payload.setAct1ID(jsonObject.optString(ShortPayloadConstant.ACT1ID));
                            // Button 2
                            payload.setAct2name(jsonObject.optString(ShortPayloadConstant.ACT2NAME));
                            payload.setAct2link(jsonObject.optString(ShortPayloadConstant.ACT2LINK));
                            payload.setAct2icon(jsonObject.optString(ShortPayloadConstant.ACT2ICON));
                            payload.setAct2ID(jsonObject.optString(ShortPayloadConstant.ACT2ID));

                            payload.setInapp(jsonObject.optInt(ShortPayloadConstant.INAPP));
                            payload.setTrayicon(jsonObject.optString(ShortPayloadConstant.TARYICON));
                            payload.setSmallIconAccentColor(jsonObject.optString(ShortPayloadConstant.ICONCOLOR));
                            payload.setSound(jsonObject.optString(ShortPayloadConstant.SOUND));
                            payload.setLedColor(jsonObject.optString(ShortPayloadConstant.LEDCOLOR));
                            payload.setLockScreenVisibility(jsonObject.optInt(ShortPayloadConstant.VISIBILITY));
                            payload.setGroupKey(jsonObject.optString(ShortPayloadConstant.GKEY));
                            payload.setGroupMessage(jsonObject.optString(ShortPayloadConstant.GMESSAGE));
                            payload.setFromProjectNumber(jsonObject.optString(ShortPayloadConstant.PROJECTNUMBER));
                            payload.setCollapseId(jsonObject.optString(ShortPayloadConstant.COLLAPSEID));
                            payload.setPriority(jsonObject.optInt(ShortPayloadConstant.PRIORITY));
                            payload.setRawPayload(jsonObject.optString(ShortPayloadConstant.RAWDATA));
                            payload.setAp(jsonObject.optString(ShortPayloadConstant.ADDITIONALPARAM));
                            if(globalPayloadObject.has(ShortPayloadConstant.CFG)) {
                                payload.setCfg(globalPayloadObject.optInt(ShortPayloadConstant.CFG));
                            }else
                            {
                                payload.setCfg(jsonObject.optInt(ShortPayloadConstant.CFG));
                            }
                            payload.setPush_type(AppConstant.PUSH_FCM);
                            payload.setPublic_global_key(url);
                            payload.setSound(jsonObject.optString(ShortPayloadConstant.NOTIFICATION_SOUND));
                            payload.setMaxNotification(jsonObject.optInt(ShortPayloadConstant.MAX_NOTIFICATION));
                            payload.setFallBackDomain(jsonObject.optString(ShortPayloadConstant.FALL_BACK_DOMAIN));
                            payload.setFallBackSubDomain(jsonObject.optString(ShortPayloadConstant.FALLBACK_SUB_DOMAIN));
                            payload.setFallBackPath(jsonObject.optString(ShortPayloadConstant.FAll_BACK_PATH));
                            Log.v(AppConstant.NOTIFICATION_MESSAGE, "YES");
                            if (payload.getTitle() != null && !payload.getTitle().isEmpty()) {
                                DATB.processNotificationReceived(DATB.appContext,payload);
                            }
                            else
                            {
                                String fallBackURL = callFallbackAPI(payload);
                                ShowFallBackResponse(fallBackURL, payload);
                            }
                        }
                    }
                    else {
                        return;
                    }

                } catch (Exception ex) {
                    String fallBackURL = callFallbackAPI(payload);
                    ShowFallBackResponse(fallBackURL, payload);
                }
            }

        }
    }
    static  String checkURL(String jsonString)
    {
        String returnString="";
        try
        {
            JSONObject objectData=new JSONObject(jsonString);
            returnString=objectData.optString(AppConstant.IZ_GPL_URL);

            return returnString;
        }
        catch (Exception ex)
        {
            return returnString;
        }
    }

    // handle the ad network payload response data
    private static void processPayload(final Payload payload, final int adIndex,final int indexValue) {
        final long start = System.currentTimeMillis();
        int calculateTime;
        int adTime = payload.getAdTimeOut();
        if(adTime!=0)
            calculateTime=payload.getAdTimeOut();
        else
            calculateTime=payload.getTime_out();
        counterIndex++;
        String fetchURL = NotificationEventManager.fetchURL(payload.getFetchURL());
        RestClient.getRequest(fetchURL,calculateTime * 1000, new RestClient.ResponseHandler(){
            @Override
            void onSuccess(String response) {
                super.onSuccess(response);
                if (response != null) {
                    long end = System.currentTimeMillis(); //fetch end time
                    try {
                        storeList.add(response);
                        Object json = new JSONTokener(response).nextValue();
                        if (json != null) {
                            if (json instanceof JSONObject) {
                                JSONObject jsonObject = new JSONObject(response);
                                payload.setResponseTime((end - start));
                                payload.setIndex(indexValue);
                                parseJson(payload, jsonObject, adIndex, indexValue);
                            } else if (json instanceof JSONArray) {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("", jsonArray);
                                payload.setResponseTime((end - start));
                                payload.setIndex(indexValue);
                                parseJson(payload, jsonObject, adIndex, indexValue);
                            }else {
                                JSONObject data = new JSONObject();
                                data.put("b", "-1");
                                data.put("a", payload.getAdID());
                                data.put("t", end-start);
                                data.put("rb",-1);
                                failsList.add(data);
                                if(adIndex == 4){
                                    String fallBackURL = callFallbackAPI(payload);
                                    ShowFallBackResponse(fallBackURL,payload);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        try {
                            JSONObject data = new JSONObject();
                            data.put("b", "-1");
                            data.put("a", payload.getAdID());
                            data.put("t", end-start);
                            data.put("rb",-1);
                            failsList.add(data);
                            if(adIndex == 4){
                                String fallBackURL = callFallbackAPI(payload);
                                ShowFallBackResponse(fallBackURL,payload);
                            }
                            if(failsList.size()-1 == payloadList.size()-1){
                                if(successList.size() > 0) {
                                    Log.v("Fallback","Data");
                                }
                                else{
                                    String fallBackURL = callFallbackAPI(payload);
                                    ShowFallBackResponse(fallBackURL, payload);
                                }
                            }
                        }
                        catch (Exception ex){
                            DebugFileManager.createExternalStoragePublic(DATB.appContext,ex.toString(),"[Log.e]->Parse JSON");
                            // add one exception
                        }
                    }
                }

            }
            @Override
            void onFailure(int statusCode, String response, Throwable throwable) {
                super.onFailure(statusCode, response, throwable);
                try {
                    JSONObject data = new JSONObject();
                    data.put("b", "-1");
                    data.put("a", payload.getAdID());
                    data.put("rb",-1);
                    if(statusCode ==-1 && payload.getTime_out()!= 0 || payload.getAdTimeOut()!= 0)
                        data.put("t", -2);
                    else
                        data.put("t", -1);
                    failsList.add(data);
                    if(failsList.size() == payloadList.size() && successList.size() == 0) {
                        String fallBackURL = callFallbackAPI(payload);
                        ShowFallBackResponse(fallBackURL, payload);
                    }
                    if (adIndex == 6) {
                        if (successList.size() == payloadList.size() - 1 && failsList.size() == 1) {
                            parseJson(payload, null, adIndex, indexValue);
                        }else if (failsList.size() == payloadList.size() - 1 && successList.size() == 1){
                            parseJson(payload, null, adIndex, indexValue);
                        }
                    }
                    if(adIndex == 4) {
                        String fallBackURL = callFallbackAPI(payload);
                        ShowFallBackResponse(fallBackURL, payload);
                    }
                }
                catch (Exception e) {
                    DebugFileManager.createExternalStoragePublic(DATB.appContext,e.toString(),"[Log.e]->Parse JSON");
                }
            }
        });
    }

    // Fetching the response from payload data
    @SuppressLint("SuspiciousIndentation")
    private static void parseJson(Payload payload, JSONObject jsonObject, int adIndex, int adNetwork) {
        if(DATB.appContext !=null) {
            try {
                if (jsonObject == null){
                    if (adIndex == 6) {
                        if (counterIndex == (payloadList.size())) {
                            showNotification();
                        }
                    }
                }else {
                    PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(DATB.appContext);
                    if (payload.getTitle() != null && !payload.getTitle().isEmpty())
                        payload.setTitle(getParsedValue(jsonObject, payload.getTitle()));
                    if (payload.getReceived_bid().equalsIgnoreCase("-1")) {
                        payload.setReceived_bid(payload.getReceived_bid());
                    }
                    else {
                        payload.setReceived_bid(getParsedValue(jsonObject, payload.getReceived_bid()));
                    }
                    if (payload.getTitle() == "") {
                        payload.setCpc("-1");
                        payload.setReceived_bid("-1");
                    } else {
                        payload.setCpc(getParsedValue(jsonObject, payload.getCpc()));
                        if (payload.getCtr() != "") {
                            payload.setCtr(getParsedValue(jsonObject, payload.getCtr()));
                            payload.setCpm(getParsedValue(jsonObject, payload.getCpm()));
                            if (payload.getCpm() != "") {
                                if (payload.getCtr() != "") {
                                    Float d = Float.parseFloat(payload.getCpm());
                                    Float r = Float.parseFloat(payload.getCtr());
                                    Float dat = 10 * r;
                                    Float dataC = d / dat;
                                    payload.setCpc(String.valueOf(dataC));
                                }
                            }
                        }
                    }
                    if (payload.getLink() != null && !payload.getLink().isEmpty())
                        payload.setLink(getParsedValue(jsonObject, payload.getLink()));

                    if (!payload.getLink().startsWith("http://") && !payload.getLink().startsWith("https://")) {
                        String url = payload.getLink();
                        url = "https://" + url;
                        payload.setLink(url);
                    }
                    if (payload.getBanner() != null && !payload.getBanner().isEmpty())
                        payload.setBanner(getParsedValue(jsonObject, payload.getBanner()));
                    if (payload.getMessage() != null && !payload.getMessage().isEmpty())
                        payload.setMessage(getParsedValue(jsonObject, payload.getMessage()));
                    if (payload.getIcon() != null && !payload.getIcon().isEmpty())
                        payload.setIcon(getParsedValue(jsonObject, payload.getIcon()));

                    payload.setAct1link(getParsedValue(jsonObject, payload.getAct1link()));
                    if (payload.getAct_num() == 1) {
                        if (payload.getAct1link() != null) {
                            payload.setAct1name(payload.getAct1name().replace("~", ""));
                        }
                        if (!payload.getAct1link().startsWith("http://") && !payload.getAct1link().startsWith("https://")) {
                            String url = payload.getAct1link();
                            url = "https://" + url;
                            payload.setAct1link(url);
                        }
                        if(payload.getAct2name()!=null && !payload.getAct2name().isEmpty())
                            payload.setAct2name(payload.getAct2name().replace("~",""));
                        payload.setAct2link(getParsedValue(jsonObject,payload.getAct2link()).replace("~",""));
                        if (!payload.getAct2link().startsWith("http://") && !payload.getAct2link().startsWith("https://")) {
                            String url = payload.getAct2link();
                            url = "https://" + url;
                            payload.setAct2link(url);
                        }
                    }
                    if (payload.getIcon() != null && payload.getIcon() != "") {
                        if (!payload.getIcon().startsWith("http://") && !payload.getIcon().startsWith("https://")) {
                            String url = payload.getIcon();
                            url = "https://" + url;
                            payload.setIcon(url);
                        }
                    }
                    if (payload.getBanner() != null && payload.getBanner() != "") {
                        if (!payload.getBanner().startsWith("http://") && !payload.getBanner().startsWith("https://")) {
                            String url = payload.getBanner();
                            url = "https://" + url;
                            payload.setBanner(url);
                        }
                    }
                    if (payload.getCpc() == "" && payload.getReceived_bid() == "") {
                        payload.setCpc("-1");
                        payload.setReceived_bid("-1");
                    }


                    payload.setAp("");
                    payload.setInapp(0);
                    JSONObject data = new JSONObject();
                    data.put("b", Double.parseDouble(payload.getCpc()));
                    data.put("a", payload.getAdID());
                    if (payload.getResponseTime() == 0)
                        data.put("t", -1);
                    else
                        data.put("t", payload.getResponseTime());
                    if (payload.getReceived_bid() != null && !payload.getReceived_bid().isEmpty())
                        data.put("rb", Double.parseDouble(payload.getReceived_bid()));
                    successList.add(data);

                    if (adIndex == 4) {
                        finalAdPayload(payload);
                    }
                    if (adIndex == 5 && preferenceUtil.getBoolean("Send")) {
                        if (payload.getTitle() != null && !payload.getTitle().equalsIgnoreCase("")) {
                            preferenceUtil.setBooleanData("Send", false);
                            finalAdPayload(payload);
                        } else {
                            if (failsList.size() > 0) {
                                String fallBackURL = callFallbackAPI(payload);
                                ShowFallBackResponse(fallBackURL, payload);
                            }
                        }

                    }
                    if (adIndex == 6) {
                        adPayload.add(payload);
                        if (counterIndex == (payloadList.size())) {
                            showNotification();
                        }
                    }
                }
            } catch (Exception e) {
                PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(DATB.appContext);
                String data2 = preferenceUtil.getStringData("iz_AdMediation_EXCEPTION_AdType_9");
                if (!data2.equalsIgnoreCase(Util.getTime())) {
                    preferenceUtil.setStringData("iz_AdMediation_EXCEPTION_AdType_9", Util.getTime());
                    Util.setException(DATB.appContext, e.toString(), "AdMediation", "getAdNotificationData");
                }
            }
        }

    }

    private static void showNotification() {
        if(AdMediation.adPayload.size() > 0) {
            try {
                int winnerIndex = 0;
                int passiveIndex = 0;
                double passiveNetwork=0.0;
                double winnerNetwork = Float.parseFloat(AdMediation.adPayload.get(0).getCpc());
                for (int index = 0; index < AdMediation.adPayload.size(); index++) {
                    if (AdMediation.adPayload.get(index).getCpc() != null && !AdMediation.adPayload.get(index).getCpc().isEmpty()) {
                        if (Float.parseFloat(AdMediation.adPayload.get(index).getCpc()) > winnerNetwork) {
                            winnerNetwork = Float.parseFloat(AdMediation.adPayload.get(index).getCpc());
                            winnerIndex = index;
                        }
                    }
                }
                if (passiveList.size() > 0) {
                    for (int index = 0; index < passiveList.size(); index++) {
                        if (Float.parseFloat(passiveList.get(index).getCpc()) >= Float.parseFloat(passiveList.get(0).getCpc())) {
                            passiveIndex = index;
                            passiveNetwork = Float.parseFloat(passiveList.get(index).getCpc());
                        }
                    }
                    if (passiveNetwork > winnerNetwork) {
                        fetchPassiveAPI(passiveList.get(passiveIndex));
                    } else {
                        if (AdMediation.adPayload.get(winnerIndex).getTitle() != null && !AdMediation.adPayload.get(winnerIndex).getTitle().equalsIgnoreCase("")) {
                            finalAdPayload(AdMediation.adPayload.get(winnerIndex));
                            if (passiveList.size() > 0) {
                                JSONObject jsonObject1 = new JSONObject();
                                jsonObject1.put("b", -1);
                                jsonObject1.put("rb",Double.parseDouble(passiveList.get(passiveIndex).getReceived_bid()));
                                jsonObject1.put("a", passiveList.get(passiveIndex).getAdID());
                                jsonObject1.put("t", -1);
                                successList.add(jsonObject1);
                                passiveList.clear();
                            }
                        }
                    }
                }
                else {
                    finalAdPayload(AdMediation.adPayload.get(winnerIndex));
                }

            }
            catch (Exception ex){
                Log.v("Exception ex",ex.toString());//
            }
        }

    }
    private static void fetchPassiveAPI(final Payload payload) {
        final long start = System.currentTimeMillis(); //fetch start time
        RestClient.get(payload.getFetchURL(), new RestClient.ResponseHandler() {
            @Override
            void onSuccess(String response) {
                super.onSuccess(response);
                if (response != null) {
                    try {
                        long end = System.currentTimeMillis(); //fetch end time
                        Object json = new JSONTokener(response).nextValue();
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("b", Double.parseDouble(payload.getCpc()));
                        jsonObject1.put("rb",Double.parseDouble(payload.getReceived_bid()));
                        jsonObject1.put("a", payload.getAdID());
                        jsonObject1.put("t", (end-start));
                        successList.add(jsonObject1);
                        if(json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            payload.setResponseTime((end-start));
                            parseAgain1Json(payload,jsonObject);
                        }
                        else if(json instanceof  JSONArray)
                        {
                            JSONArray jsonArray=new JSONArray(response);
                            JSONObject jsonObject=new JSONObject();
                            jsonObject.put("",jsonArray);
                            payload.setResponseTime((end-start));
                            parseAgain1Json(payload,jsonObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }

            }
            @Override
            void onFailure(int statusCode, String response, Throwable throwable) {
                super.onFailure(statusCode, response, throwable);
                String fallBackAPI = callFallbackAPI(payload);
                ShowFallBackResponse(fallBackAPI, payload);
            }
        });
    }

    private static void finalAdPayload(final Payload payloadData)
    {
        if(DATB.appContext!=null) {
            String fetchURL = NotificationEventManager.fetchURL(payloadData.getFetchURL());
            RestClient.get(fetchURL, new RestClient.ResponseHandler() {
                @Override
                void onSuccess(String response) {
                    super.onSuccess(response);
                    if (response != null) {
                        try {
                            Object json = new JSONTokener(response).nextValue();
                            if (json != null) {
                                if (json instanceof JSONObject) {
                                    JSONObject jsonObject = new JSONObject(response);
                                    parseAgainJson(payloadData, jsonObject);

                                } else if (json instanceof JSONArray) {
                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("", jsonArray);
                                    parseAgainJson(payloadData, jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(DATB.appContext);
                            String data2=preferenceUtil.getStringData("iz_AdMediation_EXCEPTION_AdType_11");
                            if (!data2.equalsIgnoreCase(Util.getTime())) {
                                preferenceUtil.setStringData("iz_AdMediation_EXCEPTION_AdType_11", Util.getTime());
                                Util.setException(DATB.appContext, e.toString(), "AdMediation", "finalAd_payload");
                            }
                        }
                    }
                }
                @Override
                void onFailure(int statusCode, String response, Throwable throwable) {
                    super.onFailure(statusCode, response, throwable);
                    String fallBackURL = callFallbackAPI(payloadData);
                    ShowFallBackResponse(fallBackURL, payloadData);
                }
            });
        }

    }
    static String callFallbackAPI(Payload payload) {
        String Message = "FallBAckAPI";
        String domain = "flbk.izooto.com";
        try {
            if(payload.getFallBackSubDomain()!="") {
                domain = payload.getFallBackSubDomain() + ".izooto.com";
            }
            else if(payload.getFallBackDomain()!=""){
                domain=payload.getFallBackDomain();
            }
            String path ="default.json";
            if(payload.getFallBackPath()!="")
                path =payload.getFallBackPath();

            String finalURL="https://"+domain+"/"+path;
            return finalURL;

        } catch (Exception ex) {
            //one time
            Util.setException(DATB.appContext,ex.toString(),"Fallback","callFallbackAPI");
        }
        return "";
    }
    static void parseAgain1Json(Payload payload1, JSONObject jsonObject) {
        if(DATB.appContext!=null) {
            String dataValue;
            try {
                if(payload1.getTitle()!=null && !payload1.getTitle().isEmpty())
                    payload1.setTitle(getParsedValue(jsonObject, payload1.getTitle()));
                if(payload1.getMessage()!=null && !payload1.getMessage().isEmpty())
                    payload1.setMessage(getParsedValue(jsonObject, payload1.getMessage()));

                payload1.setLink(getParsedValue(jsonObject, payload1.getLink()));
                payload1.setCpc(getParsedValue(jsonObject, payload1.getCpc()));
                payload1.setReceived_bid(getParsedValue(jsonObject, payload1.getReceived_bid()));
                if (!payload1.getLink().startsWith("http://") && !payload1.getLink().startsWith("https://")) {
                    String url = payload1.getLink();
                    url = "https://" + url;
                    payload1.setLink(url);

                }
                if(payload1.getBanner()!=null && !payload1.getBanner().isEmpty())
                    payload1.setBanner(getParsedValue(jsonObject, payload1.getBanner()));
                if(payload1.getIcon()!=null && !payload1.getIcon().isEmpty())
                    payload1.setIcon(getParsedValue(jsonObject, payload1.getIcon()));

                payload1.setAct1link(getParsedValue(jsonObject, payload1.getAct1link()));
                payload1.setCtr(getParsedValue(jsonObject, payload1.getCtr()));
                payload1.setCpm(getParsedValue(jsonObject, payload1.getCpm()));
                payload1.setReceived_bid(getParsedValue(jsonObject, payload1.getReceived_bid()));

                if (payload1.getAct_num() == 1) {

                    if (payload1.getAct1link() != null) {
                        payload1.setAct1name(payload1.getAct1name().replace("~",""));
                    }
                    if (!payload1.getAct1link().startsWith("http://") && !payload1.getAct1link().startsWith("https://")) {
                        String url = payload1.getAct1link();
                        url = "https://" + url;
                        payload1.setAct1link(url);

                    }
                    if(payload1.getAct2name()!=null && !payload1.getAct2name().isEmpty())
                        payload1.setAct2name(payload1.getAct2name().replace("~",""));
                    payload1.setAct2link(getParsedValue(jsonObject,payload1.getAct2link()).replace("~",""));
                    if (!payload1.getAct2link().startsWith("http://") && !payload1.getAct2link().startsWith("https://")) {
                        String url = payload1.getAct2link();
                        url = "https://" + url;
                        payload1.setAct2link(url);
                    }
                }
                if (payload1.getIcon() != null && payload1.getIcon() != "") {
                    if (!payload1.getIcon().startsWith("http://") && !payload1.getIcon().startsWith("https://")) {
                        String url = payload1.getIcon();
                        url = "https://" + url;
                        payload1.setIcon(url);

                    }

                }
                if (payload1.getBanner() != null && payload1.getBanner() != "") {
                    if (!payload1.getBanner().startsWith("http://") && !payload1.getBanner().startsWith("https://")) {
                        String url = payload1.getBanner();
                        url = "https://" + url;
                        payload1.setBanner(url);

                    }

                }

                if (payload1.getRv() != null && !payload1.getRv().isEmpty()) {

                    if (payload1.getRv().startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(payload1.getRv());
                        for (int i = 0; i <= jsonArray.length()-1; i++) {
                            String rv = jsonArray.getString(i);
                            payload1.setRv(NotificationEventManager.getRvParseValues(jsonObject, rv));
                            callRandomView(payload1.getRv());
                        }
                    } else {
                        payload1.setRv(NotificationEventManager.getRvParseValues(jsonObject, payload1.getRv()));
                        callRandomView(payload1.getRv());
                    }
                }
                if (payload1.getRc() != null && !payload1.getRc().isEmpty()) {
                    if (payload1.getRc().startsWith("[")) {
                        JSONArray jsonArray = new JSONArray(payload1.getRc());
                        for (int i = 0; i <= jsonArray.length()-1; i++) {
                            String rc = jsonArray.getString(i);
                            payload1.setRc(NotificationEventManager.getRcParseValues(jsonObject, rc));
                            clicksData.add(payload1.getRc());
                        }
                    } else {
                        payload1.setRc(NotificationEventManager.getRcParseValues(jsonObject, payload1.getRc()));
                        clicksData.add(payload1.getRc());
                    }
                }

                if(successList.size()>0) {
                    long end = System.currentTimeMillis();
                    JSONObject finalData = new JSONObject();
                    finalData.put("pid",PreferenceUtil.getInstance(DATB.appContext).getDataBID(AppConstant.APPPID));
                    finalData.put("rid", payload1.getRid());
                    finalData.put("type", payload1.getAd_type());
                    finalData.put("ta", (end - payload1.getStartTime()));
                    finalData.put("av", AppConstant.SDK_VERSION);

                    JSONObject servedObject = new JSONObject();
                    servedObject.put("a", payload1.getAdID());
                    servedObject.put("b", Double.parseDouble(payload1.getCpc()));
                    servedObject.put("t", payload1.getResponseTime());
                    servedObject.put("ln",payload1.getLink());
                    servedObject.put("ti",payload1.getTitle());
                    if (payload1.getReceived_bid() != null && !payload1.getReceived_bid().isEmpty() && payload1.getReceived_bid() != "")
                        servedObject.put("rb", Double.parseDouble(payload1.getReceived_bid()));
                    finalData.put("served", servedObject);
                    successList.addAll(failsList);
                    JSONArray jsonArray = new JSONArray(successList);
                    finalData.put("bids", jsonArray);
                    dataValue = finalData.toString().replaceAll("\\\\", " ");
                    mediationImpression(dataValue, 0);
                    PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(DATB.appContext);
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
                        TargetActivity.medClick = dataValue;
                        preferenceUtil.setStringData(AppConstant.IZ_MEDIATION_CLICK_DATA,dataValue);
                    }
                    else {
                        preferenceUtil.setStringData(AppConstant.IZ_MEDIATION_CLICK_DATA,dataValue);
                        NotificationActionReceiver.medClick = dataValue;
                    }
                    if(payload1.getTitle()!=null && !payload1.getTitle().isEmpty()) {
                        // NotificationEventManager.receiveAds(payload1);
                        NotificationEventManager.notificationPreview(DATB.appContext,payload1); //need to test
                        Log.v(AppConstant.NOTIFICATION_MESSAGE, AppConstant.YES);
                    }
                    else{
                        String fallBackURL = callFallbackAPI(payload);
                        ShowFallBackResponse(fallBackURL, payload);
                        Log.v(AppConstant.NOTIFICATION_MESSAGE, AppConstant.NO);
                    }
                }
                else
                {
                    String fallBackURL = callFallbackAPI(payload);
                    ShowFallBackResponse(fallBackURL, payload);
                    Log.v(AppConstant.NOTIFICATION_MESSAGE, AppConstant.NO);
                }
            } catch (Exception e) {
                PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(DATB.appContext);
                String data2=preferenceUtil.getStringData("iz_AdMediation_EXCEPTION_AdType_13");
                if (!data2.equalsIgnoreCase(Util.getTime())) {
                    preferenceUtil.setStringData("iz_AdMediation_EXCEPTION_AdType_13", Util.getTime());
                    Util.setException(DATB.appContext, "PayloadError"+e.toString(), "AdMediation", "parseAgainJson");
                }
                DebugFileManager.createExternalStoragePublic(DATB.appContext,e.toString(),"[Log.e]->AdMediation 868");
            }
        }
    }
    static void ShowFallBackResponse(String fallBackAPI,  final Payload payload) {
        RestClient.get(fallBackAPI, new RestClient.ResponseHandler() {
            @Override
            void onSuccess(String response) {
                super.onSuccess(response);
                try
                {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject!=null){
                        payload.setTitle(jsonObject.optString(ShortPayloadConstant.TITLE));
                        payload.setMessage(jsonObject.optString(ShortPayloadConstant.NMESSAGE));
                        payload.setLink(jsonObject.optString(ShortPayloadConstant.LINK));
                        payload.setIcon(jsonObject.optString(ShortPayloadConstant.ICON));
                        payload.setBanner(jsonObject.optString(ShortPayloadConstant.BANNER));
                        payload.setAct1link(jsonObject.optString(ShortPayloadConstant.ACT1LINK));
                        payload.setRid(payload.getRid());
                        NotificationEventManager.notificationPreview(DATB.appContext,payload);
                        ShowCLCIKAndImpressionData(payload);
                    }
                }
                catch (Exception ex)
                {
                    Util.setException(DATB.appContext,ex.toString(),"AdMediation","ShowFallBackResponse");// need to one time sends exception
                }
            }

            @Override
            void onFailure(int statusCode, String response, Throwable throwable) {
                super.onFailure(statusCode, response, throwable);
            }
        });
    }
    private static void ShowCLCIKAndImpressionData(Payload payload) {
        if(DATB.appContext!=null) {
            try {
                long end = System.currentTimeMillis();
                JSONObject finalData = new JSONObject();
                finalData.put("pid",PreferenceUtil.getInstance(DATB.appContext).getDataBID(AppConstant.APPPID));
                finalData.put("rid", payload.getRid());
                finalData.put("type", payload.getAd_type());
                finalData.put("ta", (end - payload.getStartTime()));
                finalData.put("av",AppConstant.SDK_VERSION);

                JSONObject servedObject = new JSONObject();
                servedObject.put("a", 0);
                servedObject.put("b", 0);
                servedObject.put("ln",payload.getLink());
                servedObject.put("ti",payload.getTitle());
                // servedObject.put("rb",-1);
                if (payload.getResponseTime() == 0)
                    servedObject.put("t", -1);
                else
                    servedObject.put("t", payload.getResponseTime());

                finalData.put("served", servedObject);

                successList.addAll(failsList);

                JSONArray jsonArray = new JSONArray(successList);
                finalData.put("bids", jsonArray);
                String dataValue = finalData.toString().replaceAll("\\\\", " ");
                mediationImpression(dataValue,0);
                PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(DATB.appContext);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
                    TargetActivity.medClick = dataValue;
                    preferenceUtil.setStringData(AppConstant.IZ_MEDIATION_CLICK_DATA,dataValue);
                }
                else {
                    preferenceUtil.setStringData(AppConstant.IZ_MEDIATION_CLICK_DATA,dataValue);
                    NotificationActionReceiver.medClick = dataValue;

                }
                Log.v(AppConstant.NOTIFICATION_MESSAGE, AppConstant.YES);
            } catch (Exception ex) {
                DebugFileManager.createExternalStoragePublic(DATB.appContext,ex.toString(),"[Log.e]->");
            }
        }
    }
    private static void parseAgainJson(Payload payload1, JSONObject jsonObject) {
        String dataValue;
        try {

            if(payload1.getRv()!=null && !payload1.getRv().isEmpty())
            {
                if(payload1.getRv().startsWith("[")) {

                    JSONArray jsonArray = new JSONArray(payload1.getRv());
                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        String rv = jsonArray.getString(i);
                        payload1.setRv(NotificationEventManager.getRvParseValues(jsonObject,rv));
                        callRandomView(payload1.getRv());
                    }
                }
                else {

                    payload1.setRv(NotificationEventManager.getRvParseValues(jsonObject,payload1.getRv()));

                    callRandomView(payload1.getRv());
                }

            }
            if(payload1.getRc()!=null && !payload1.getRc().isEmpty())
            {
                if(payload1.getRc().startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(payload1.getRc());
                    for (int i = 0; i <= jsonArray.length()-1; i++) {
                        String rc = jsonArray.getString(i);
                        payload1.setRc(NotificationEventManager.getRcParseValues(jsonObject,rc));
                        clicksData.add(payload1.getRc());

                    }
                }
                else {
                    payload1.setRc(NotificationEventManager.getRcParseValues(jsonObject,payload1.getRc()));
                    clicksData.add(payload1.getRc());
                }

            }

            if(payload1.getTitle()!=null && !payload1.getTitle().equalsIgnoreCase("")) {

                long end = System.currentTimeMillis();
                JSONObject finalData=new JSONObject();
                finalData.put("pid",PreferenceUtil.getInstance(DATB.appContext).getDataBID(AppConstant.APPPID));
                finalData.put("rid",payload1.getRid());
                finalData.put("type",payload1.getAd_type());
                finalData.put("ta",(end-payload1.getStartTime()));
                finalData.put("av",AppConstant.SDK_VERSION);

                JSONObject servedObject=new JSONObject();
                servedObject.put("a",payload1.getAdID());
                servedObject.put("b",Double.parseDouble(payload1.getCpc()));
                servedObject.put("t",payload1.getResponseTime());
                servedObject.put("ln",payload1.getLink());
                servedObject.put("ti",payload1.getTitle());
                if(payload1.getReceived_bid()!=null && !payload1.getReceived_bid().isEmpty() && payload1.getReceived_bid()!="")
                    servedObject.put("rb",Double.parseDouble(payload1.getReceived_bid()));
                finalData.put("served",servedObject);

                failsList.addAll(successList);

                JSONArray jsonArray =new JSONArray(failsList);
                finalData.put("bids",jsonArray);
                dataValue=finalData.toString().replaceAll("\\\\", " ");
                mediationImpression(dataValue,0);
                NotificationEventManager.notificationPreview(DATB.appContext,payload1);
                PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(DATB.appContext);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
                    TargetActivity.medClick = dataValue;
                    preferenceUtil.setStringData(AppConstant.IZ_MEDIATION_CLICK_DATA,dataValue);

                }
                else {
                    preferenceUtil.setStringData(AppConstant.IZ_MEDIATION_CLICK_DATA,dataValue);
                    NotificationActionReceiver.medClick = dataValue;

                }
                Log.v(AppConstant.NOTIFICATION_MESSAGE,AppConstant.YES);
            }
            else {
                String fallBackAPI = callFallbackAPI(payload1);
                ShowFallBackResponse(fallBackAPI, payload1);
                Log.v(AppConstant.NOTIFICATION_MESSAGE, AppConstant.NO);
            }



        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    static void ShowClickAndImpressionData(Payload payload) {
        if(DATB.appContext!=null) {
            try {
                PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(DATB.appContext);
                long end = System.currentTimeMillis();
                JSONObject finalData = new JSONObject();
                finalData.put("pid", preferenceUtil.getDataBID(AppConstant.APPPID));
                finalData.put("rid", payload.getRid());
                finalData.put("type", payload.getAd_type());
                finalData.put("ta", (end - payload.getStartTime()));
                finalData.put("av", AppConstant.SDK_VERSION);

                JSONObject servedObject = new JSONObject();
                servedObject.put("a", 0);
                servedObject.put("b", 0);
                servedObject.put("ln",payload.getLink());
                servedObject.put("ti",payload.getTitle());

                if (payload.getResponseTime() == 0)
                    servedObject.put("t", -1);
                else
                    servedObject.put("t", payload.getResponseTime());

                finalData.put("served", servedObject);
                successList.addAll(failsList);
                JSONArray jsonArray = new JSONArray(successList);
                finalData.put("bids", jsonArray);
                String dataValue = finalData.toString().replaceAll("\\\\", " ");
                mediationImpression(dataValue,0);
                NotificationActionReceiver.medClick = dataValue;
                TargetActivity.medClick = dataValue;
            } catch (Exception ex) {
                PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(DATB.appContext);
                String data2=preferenceUtil.getStringData("iz_AdMediation_EXCEPTION_AdType_14");
                if (!data2.equalsIgnoreCase(Util.getTime())) {
                    preferenceUtil.setStringData("iz_AdMediation_EXCEPTION_AdType_14", Util.getTime());
                    Util.handleExceptionOnce(DATB.appContext, "PayloadError"+ex+payload.getRid(), "AdMediation", "ShowClickAndImpressionData");

                }
            }
        }
    }
    static void mediationImpression(String finalData, int impNUmber) {
        if(DATB.appContext!=null) {
            try {
                if(successList.size()>0){
                    DebugFileManager.createExternalStoragePublic(DATB.appContext,storeList.toString(),"successResponseMediation");
                }
                DebugFileManager.createExternalStoragePublic(DATB.appContext,finalData,"mediation_impression");
                JSONObject jsonObject = new JSONObject(finalData);
                RestClient.postRequest(RestClient.MEDIATION_IMPRESSION,null, jsonObject, new RestClient.ResponseHandler() {
                    @SuppressLint("NewApi")
                    @Override
                    void onSuccess(String response) {
                        super.onSuccess(response);
                        PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(DATB.appContext);
                        if (!preferenceUtil.getStringData(AppConstant.STORE_MEDIATION_RECORDS).isEmpty() && impNUmber >= 0) {

                            try {
                                JSONArray jsonArrayOffline = new JSONArray(preferenceUtil.getStringData(AppConstant.STORE_MEDIATION_RECORDS));
                                jsonArrayOffline.remove(impNUmber);
                                preferenceUtil.setStringData(AppConstant.STORE_MEDIATION_RECORDS, null);

                            }
                            catch (Exception ex)
                            {
                                DebugFileManager.createExternalStoragePublic(DATB.appContext,ex.toString(),"[Log.e]-> ");
                            }

                        }

                    }

                    @Override
                    void onFailure(int statusCode, String response, Throwable throwable) {
                        super.onFailure(statusCode, response, throwable);
                        Util.trackMediation_Impression_Click(DATB.appContext,AppConstant.MED_IMPRESION,jsonObject.toString());
                    }
                });
            } catch (Exception ex) {
                PreferenceUtil preferenceUtil=PreferenceUtil.getInstance(DATB.appContext);
                String data2=preferenceUtil.getStringData("iz_AdMediation_EXCEPTION_AdType_15");
                if (!data2.equalsIgnoreCase(Util.getTime())) {
                    preferenceUtil.setStringData("iz_AdMediation_EXCEPTION_AdType_15", Util.getTime());
                    Util.setException(DATB.appContext,ex+finalData, "AdMediation", "mediationImpression");

                }

            }
            finalData="";
        }
    }
    private static void callRandomView(String rv) {
        if(!rv.isEmpty()) {
            RestClient.get(rv, new RestClient.ResponseHandler() {
                @Override
                void onSuccess(String response) {
                    super.onSuccess(response);
                    Log.i(AppConstant.APP_NAME_TAG, "rv"+" "+response);
                }

                @Override
                void onFailure(int statusCode, String response, Throwable throwable) {
                    super.onFailure(statusCode, response, throwable);
                    Log.i(AppConstant.APP_NAME_TAG, "rv"+"Fail");
                }
            });
        }
    }
    private static String getParsedValue(JSONObject jsonObject, String sourceString) {
        try {
            if(sourceString.matches("[0-9]{1,13}(\\.[0-9]*)?"))
            {
                return sourceString;
            }
            if (sourceString.startsWith("~"))
                return sourceString.replace("~", "");
            else {
                if (sourceString.contains(".")) {
                    JSONObject jsonObject1 = null;
                    String[] linkArray = sourceString.split("\\.");
                    if(linkArray.length==2 || linkArray.length==3)
                    {
                        for (int i = 0; i < linkArray.length; i++) {
                            if (linkArray[i].contains("[")) {
                                String[] linkArray1 = linkArray[i].split("\\[");

                                if (jsonObject1 == null)
                                    jsonObject1 = jsonObject.getJSONArray(linkArray1[0]).getJSONObject(Integer.parseInt(linkArray1[1].replace("]", "")));
                                else {
                                    jsonObject1 = jsonObject1.getJSONArray(linkArray1[0]).getJSONObject(Integer.parseInt(linkArray1[1].replace("]", "")));
                                }

                            } else {
                                return Objects.requireNonNull(jsonObject1).optString(linkArray[i]);
                            }

                        }
                    }
                    else if(linkArray.length==4)
                    {
                        if (linkArray[2].contains("[")) {
                            String[] linkArray1 = linkArray[2].split("\\[");
                            if(jsonObject1==null) {
                                jsonObject1 = jsonObject.getJSONObject(linkArray[0]).getJSONObject(linkArray[1]).getJSONArray(linkArray1[0]).getJSONObject(Integer.parseInt(linkArray1[1].replace("]", "")));
                            }
                            else
                                jsonObject1 = jsonObject.getJSONObject(linkArray[0]).getJSONObject(linkArray[1]).getJSONArray(linkArray1[0]).getJSONObject(Integer.parseInt(linkArray1[1].replace("]", "")));

                            return jsonObject1.optString(linkArray[3]);

                        }

                    }
                    else if(linkArray.length==5)
                    {
                        if (linkArray[2].contains("[")) {
                            String[] link1 = linkArray[2].split("\\[");
                            if (jsonObject1 == null)
                                jsonObject1 = jsonObject.getJSONObject(linkArray[0]).getJSONObject(linkArray[1]).getJSONArray(link1[0]).getJSONObject(Integer.parseInt(link1[1].replace("]", ""))).getJSONObject(linkArray[3]);
                            else
                                jsonObject1 = jsonObject1.getJSONObject(linkArray[0]).getJSONObject(linkArray[1]).getJSONArray(link1[0]).getJSONObject(Integer.parseInt(link1[1].replace("]", ""))).getJSONObject(linkArray[3]);


                            return jsonObject1.optString(linkArray[4]);
                        }
                    }
                    else
                    {
                        jsonObject.optString(sourceString);
                    }


                } else {
                    return jsonObject.optString(sourceString);
                }
            }
        } catch (Exception e) {
            DebugFileManager.createExternalStoragePublic(DATB.appContext,"getParsedValue"+e,"[Log.e]->AdMediation");
        }
        return "";
    }
}



