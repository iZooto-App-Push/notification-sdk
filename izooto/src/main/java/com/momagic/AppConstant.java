package com.momagic;


public interface AppConstant {
    String APP_NAME_TAG = "DATB";
    String FCM_DEVICE_TOKEN = "deviceToken";
    String SDK_NAME = "MOMAGIC";
    String DEVICE_TOKEN="DEVICE TOKEN   ->  ";
    String UTF="UTF-8";
    String BROWSER_KEY_ID="{BROWSERKEYID}";
    String PTE="2";
    String ANDROID_VERSION = "osVersion";
    String DEVICE_NAME ="deviceName";
    String TOKEN="at";
    String ADD_URL="s";
    String  PID="pid";
    String BTYPE_="btype";
    String DTYPE_="dtype";
    String TIMEZONE="tz";
    String BKEY="bKey";
    String APP_VERSION="bver";
    String OS="os";
    String ALLOWED_="allowed";
    String CHECK_SDK_VERSION="check";
    String LANGUAGE="ln";
    String CLICKINDEX= "clickIndex";
    String APPPID="pid";
    String ENCRYPTED_PID="encryptedPid";
    String ADVERTISING_ID = "add";
    String GET_NOTIFICATION_ENABLED="enable";
    String GET_NOTIFICATION_DISABLED="disable";
    String FIREBASE_ANALYTICS_TRACK = "isCheck";
    String TRACK_NOTIFICATION_ID = "notificationId";
    String IS_NOTIFICATION_ID_UPDATED = "notificationIdUpdated";
    String NOTIFICATION_COUNT = "datbcount";
    String PAYLOAD_BADGE_COUNT = "payloadBadgeCount";
    String WEB_LANDING_URL = "webLandingUrl";
    String LOG_EVENT = "logEvent";
    String GET_FIREBASE_INSTANCE = "getInstance";
    String UTM_SOURCE = "utm_source";
    String UTM_MEDIUM = "utm_medium";
    String UTM_CAMPAIGN = "utm_campaign";
    String UTM_TERM = "utm_term";
    String UTM_CONTENT = "utm_content";
    String SOURCE = "source";
    String MEDIUM = "medium";
    String FIREBASE_NOTIFICATION_ID = "notification_id";
    String FIREBASE_CAMPAIGN = "campaign";
    String TERM = "term";
    String CONTENT = "content";
    String TIME_OF_CLICK = "time_of_click";
    String FIREBASE_12PM = "12:00:00 PM";
    String FIREBASE_2PM = "02:00:00 PM";
    String FIREBASE_4PM = "04:00:00 PM";
    String FIREBASE_6PM = "06:00:00 PM";
    String FIREBASE_8PM = "08:00:00 PM";
    String FIREBASE_10PM = "10:00:00 PM";
    String FIREBASE_12AM = "12:00:00 AM";
    String FIREBASE_2AM = "02:00:00 AM";
    String FIREBASE_4AM = "04:00:00 AM";
    String FIREBASE_6AM = "06:00:00 AM";
    String FIREBASE_8AM = "08:00:00 AM";
    String FIREBASE_10AM = "10:00:00 AM";
    String FIREBASE_12to2PM = "12-2 PM";
    String FIREBASE_2to4PM = "2-4 PM";
    String FIREBASE_4to6PM = "4-6 PM";
    String FIREBASE_6to8PM = "6-8 PM";
    String FIREBASE_8to10PM = "8-10 PM";
    String FIREBASE_10to12AM = "10-12 AM";
    String FIREBASE_12to2AM = "12-2 AM";
    String FIREBASE_2to4AM = "2-4 AM";
    String FIREBASE_4to6AM = "4-6 AM";
    String FIREBASE_6to8AM = "6-8 AM";
    String FIREBASE_8to10AM = "8-10 AM";
    String FIREBASE_10to12PM = "10-12 PM";
    String API_PID = "?pid=";
    String PTE_="pte";
    String CID_="cid";
    String RID_="rid";
    String NOTIFICATION_OP="op";
    String ACTION="action";
    String PT_="pt";
    int PT = 0;
    String GE_ ="ge";
    String ET_ ="et";
    String VAL ="val";
    String ACT ="act";
    String BUTTON_ID_1 ="button1ID";
    String BUTTON_TITLE_1 ="button1Title";
    String BUTTON_URL_1 ="button1URL";
    String ADDITIONAL_DATA ="additionalData";
    String LANDING_URL ="landingURL";
    String BUTTON_ID_2 ="button2ID";
    String BUTTON_TITLE_2 ="button2Title";
    String BUTTON_URL_2 ="button2URL";
    String ACTION_TYPE ="actionType";
    int GE = 1;

    String DATAB_APP_ID = "momagic_app_id";
    String KEY_WEB_URL = "WEB_URL";
    String KEY_NOTIFICITON_ID = "keyNotificationId";
    String IS_TOKEN_UPDATED = "isTokenUpdated";
    String DEVICE_REGISTRATION_TIMESTAMP = "deviceRegistrationTimeStamp";
    String KEY_IN_APP = "keyInApp";
    String  KEY_IN_CID = "cid";
    String KEY_IN_RID = "rid";
    String KEY_IN_BUTTON="btn";
    String KEY_IN_ADDITIONALDATA="ap";
    String KEY_IN_PHONE ="call";
    String KEY_IN_ACT1ID = "act1ID";
    String KEY_IN_ACT2ID="act2ID";
    int BTYPE = 9;
    int DTYPE = 3;
    int SDKOS = 4;
    int ALLOWED = 1;
    int STYPE = 2;
    String SECRET_KEY = "b07dfa9d56fc64df";
    String MESSAGE = "It seems you forgot to configure momagic_app id or momagic_sender_id property in your app level build.gradle";
    String FCM_ERROR = "Unable to generate FCM token, there may be something wrong with sender id";
    String SUCCESS = " Request Successful: ";
    String FAILURE = " Request Failed: ";

    String ATTACH_REQUEST = "MoMagic RestClient: ResponseHandler is not attached for the Request: ";
    String EXCEPTIONERROR = "Thrown Error";
    String CDN = "https://cdn.izooto.com/app/app_";

    String MISSING_ID="App Id is missing.";
    // Register String

    String SENDERID ="senderId";
    String APPID = "appId";
    String APIKEY="apiKey";

    ///////// JSON Payload Data


    String RID= "rid";
    String TAG= "tag";
    String FIREBASE_EXCEPTION ="exception";
    String FCM_NAME = "FireBase Name";
    String NOTIFICATION_RECEIVED ="Short lived task is done.";
    String FAILED_TOKEN = "Unable to generate FCM token, there may be something wrong with sender id";
    String CHECKFCMLIBRARY="The FCM library is missing! Please make sure to include it in your project.";
    //API
    String APISUCESS ="API SUCCESS";
    String APIFAILURE ="API FAILURE";
    String TELIPHONE = "tel:";
    String NO = "NO";

    //// short payload
    String webViewData = "WebViewClient: shouldOverrideUrlLoading";
    String LANDINGURL="landingURL";
    String ACT1URL = "act1URL";
    String ACT2URL="act2URL";
    String ACT1TITLE="act1title";
    String ACT2TITLE="act2title";


    String CHANNEL_NAME="Updates and Notifications";
    String URL_FWD="&frwd";
    String URL_FWD_="&frwd=";
    String URL_BKEY="&bkey=";
    String URL_ID="id";
    String URL_CLIENT="client";
    String URL_RID="rid";
    String URL_BKEY_="bkey";
    String URL_FRWD___="frwd";
    String FCM_TIME_FORMAT="hh:mm:ss aa";
    String NULL="null";
    String POST="POST";
    String CONTENT_TYPE="Content-Type";
    String FORM_URL_ENCODED="application/x-www-form-urlencoded";
    String FORM_URL_JSON="application/json; charset=UTF-8";
    String DAT=".dat";
    String HTTPS="https:";
    String HTTP="http:";
    String IMPR="impr.izooto.com";
    String KEY_NOT_FOUND="KEY NOT FOUND";
    String GET_TOPIC_NAME = "getTopicName";
    String REMOVE_TOPIC_NAME = "removeTopicName";
    String ADD_TOPIC ="add_topic";
    String REMOVE_TOPIC ="remove_topic";
    String TOPIC ="topic";
    String ANDROID_ID="bKey";
    String ADVERTISEMENTID="adid";
    String LASTCLICKINDEX= "lastclickIndex";
    String LAST_NOTIFICAION_CLICKED= "last_notification_clicked";
    String LAST_NOTIFICAION_VIEWED= "last_notification_viewed";
    String LAST_WEBSITE_VISIT= "last_website_visit";
    String LANG_= "lang";
    String CURRENT_DATE= "currentDate";
    String VER_= "ver";
    String ISID_= "isid";
    String CURRENT_DATE_VIEW = "currentDateView";
    String CURRENT_DATE_CLICK = "currentDateClick";
    String AD_NETWORK="an";
    String GLOBAL="g";
    String AD_TYPE="tp";
    String NOTIFICATION_MESSAGE="Send Notification";
    String YES="yes";
    String SDK_VERSION="2.1.0";
    String QSDK_VERSION="av";
    String SDK="SDKVERSION";
    String PACKAGE_NAME="mpn";
    String SDKTYPE="sn";
    String MEDIATION="Mediation";
    String CHECK_CREATED_ON="CT";
    String CHECK_RID="RID";
    String CHECK_TTL="TTL";
    String NOTIFICATION_DUPLICATE="Add";
    String PUSH_TYPE="&ct=";
    String PUSH="push_type";
    String CHECK_XIAOMI="Check";
    String HMS_TOKEN = "hms_token";
    String KEY_HMS="ht";
    String HMS_APP_ID="hms_app_id";
    String HMS="Huawei";
    String PUSH_HMS="hms";
    String PUSH_FCM="fcm";
    String PUSH_XIAOMI="xiaomi";
    String Check_Notification="checkData";
    String Check_YES="true";
    String Check_NO="false";
    String XIAOMITOKEN = "mt";
    String XiaomiToken = "xiaomi_token";
    String MIAPIKEY = "Mi_api_key";
    String MIAPPID="Mi_app_id";
    String CHECK_PAYLOAD="Title";
    String CLOUD_PUSH="DATBCounter";
    String CHECKDOMAIN="checkDomain";
    String CFGFORDOMAIN= "cfgfordomain";
    String CURRENT_DATE_VIEW_WEEKLY = "currentDateViewWeekly";
    String CURRENT_DATE_VIEW_DAILY = "currentDateViewDaily";
    String CURRENT_DATE_CLICK_WEEKLY = "currentDateClickWeekly";
    String CURRENT_DATE_CLICK_DAILY = "currentDateClickDaily";
    String CHARSET_="charset";
    String UTF_="utf-8";
    String CONTENT_L="Content-Length";
    String USERP_="userp";
    String EXCEPTION_ = "exceptionName";
    String METHOD_NAME = "methodName";
    String ClASS_NAME = "className";
    String APPName_2="NotificationEventManager";
    String APPName_3="NotificationActionReceiver";
    String USER_LOCAL_DATA = "iz_userLocalData";
    String SET_SUBSCRIPTION_DATA="iz_setSubscription";
    String EVENT_LOCAL_DATA_EN = "eventLocalDataEN";
    String EVENT_LOCAL_DATA_EV = "eventLocalDataEV";
    String SET_SUBSCRITION_LOCAL_DATA = "setSubscriptionLocalData";
    String IS_SET_SUBSCRIPTION_METHOD = "isSetSubscriptionMethod";
    String IS_UPDATED_XIAOMI_TOKEN = "iz_xiaomi_token_updated";
    String IS_UPDATED_HMS_TOKEN = "iz_hms_token_updated";
    String SUBSCRIBER_ID_DATA="iz_SUBSCRIBER_ID_DATA";
    String NOTIFICATION_PREVIEW = "iz_notification_preview";
    String DEVICE_ID = "{~UUID~}";
    String ANDROID_TOKEN = "{~TOKEN~}";
    String R_XIAOMI_TOKEN = "{~MITOKEN~}";
    String R_HMS_TOKEN = "{~HMSTOKEN~}";
    String R_FCM_TOKEN="{~FCMTOKEN~}";
    String ACCOUNT_ID="{~PID~}";
    String ADID="{~ADID~}";
    String CAN_GENERATE_HUAWEI_TOKEN = "iz_canGenerateHuaweiToken";
    String CAN_GENERATE_XIAOMI_TOKEN = "iz_canGenerateXiaomiToken";
    String CAN_GENERATE_FCM_TOKEN = "iz_canGenerateFcmToken";
    String FCM_TOKEN_FROM_JSON = "fcmToken";
    String XIAOMI_TOKEN_FROM_JSON = "xiaomiToken";
    String HUAWEI_TOKEN_FROM_JSON = "huaweiToken";
    String GLOBAL_PUBLIC_KEY= "gpl";
    String STORAGE_PAYLOAD_DATA="iz_payload_data";
    String IZ_NOTIFICATION_CLICK_OFFLINE = "iZ_Notification_Click_Offline";
    String IZ_NOTIFICATION_VIEW_OFFLINE = "iZ_Notification_View_Offline";
    String IZ_NOTIFICATION_LAST_CLICK_OFFLINE = "iZ_Notification_Last_Click_Offline";
    String IZ_NOTIFICATION_LAST_VIEW_OFFLINE = "iZ_Notification_Last_View_Offline";
    String IS_CONSENT_STORED= "iz_isConsentStored";
    String CAN_STORED_QUEUE= "iz_cantStoredQueue";
    String STORE_URL = "apiURL";
    String CHECK_SDK_UPDATE="IZ_SDK_UPDATE";
    String STORE_MEDIATION_RECORDS="iz_mediation_records";
    String STORE_MED_API="iz_api_name";
    String STORE_MED_DATA="iz_mediationData";
    String MED_IMPRESION="iz_impression";
    String MED_CLICK="iz_mClick";
    String IZ_ADD_TOPIC_OFFLINE = "iz_add_topic_offline";
    String IZ_REMOVE_TOPIC_OFFLINE = "iz_remove_topic_offline";
    String FILE_EXIST="iz_fileExits";
    String DIRECTORY_NAME="DATB.907135001.debug";
    String SAVESUBID="DATB_SUBID";
    String CHECK_APP_VERSION="iz_app_version";
    String NOTIFICATION_ENABLE_DISABLE = "isEnable";
    String NOTIFICATION_ACCENT_COLOR = "izooto_notification_accent_color";
    String STRING_RESOURCE_NAME = "string";
    String SDC_ ="sdc";
    String NDC_ ="ndc";
    String OPTIN_ ="optin";
    String DENIED_ ="denied";
    int DENIED = 1;
    int SDC = 1;
    int NDC = 1;
    int OPT_IN = 0;
    String NOTIFICATION_PROMPT_DISALLOW = "permissionDisAllow";
    String NOTIFICATION_PROMPT_ALLOW = "permissionAllow";

    String iZ_STORE_CHANNEL_NAME ="ChannelName";

    String IZ_MEDIATION_CLICK_DATA="MEDIATIONCLICKDATA";
    String IZ_GPL_URL="GPLURL";


    String SILENT_CHANNEL="Silent Push Notification";
    String DEFAULT_CHANNEL="Default Channel";
    String NOTIFICATION_SOUND_NAME="IZ_SOUND_NAME";
    String IZ_DEFAULT_WEB_VIEW = "defaultWebView";
    String IZ_NOTIFICATION_DATA = "notificationData";

    String IZ_TITLE_INFO = "title";
    String IZ_MESSAGE_INFO = "message";
    String IZ_BANNER_INFO = "banner_image";
    String IZ_LANDING_URL_INFO = "landing_url";
    String IZ_TIME_STAMP_INFO = "time_stamp";
    String IZ_NO_MORE_DATA ="No more data";
    String IZ_ERROR_MESSAGE = "iZooto is not initialised properly, Please verify again.";
    String IZ_NOTIFICATION_FETCH_EXCEPTION="fetchNotificationData";
    String IZ_PAYLOAD_ERROR ="Payload Error";
    String IZ_AD_MEDIATION_CLASS = "AdMediation";
    String IZ_DEBUG_FILE_NAME ="pid.debug";
    String IZ_DEBUG_EXCEPTION ="Exception";

    String IZ_MISSING_GOOGLE_JSON_SERVICES_FILE ="missing google-service.json file";

    String TP_TYPE="type";
    String TYPE_TP="tp";
    String TYPE_P="p";
    String TYPE_O="o";
    String P_OP="op";
    String DISMISSED="dismiss";

    String FORMAT = "[0-9]";
    String IZ_DATE_TIME_FORMAT = "dd:MM:yyyy hh:mm:ss";
    String IZ_LISTENER_ERROR = "Notification dismiss listener is not working";
    String IZ_LISTENER_KEY = "nDismiss";
    String IZ_TIMER_MESSAGE = "getTimerValue";
    String IZ_TIMER_VALUE_MESSAGE = "Timer values exceed on maximum seconds or minimum seconds";
    String IZ_LANDING_URL = "ln";
    String IZ_DEEPLINK_URL = "ap";
    String IZ_NOTIFICATION_TITLE_KEY_NAME="ti";

    /*...........NotificationChannel............*/
    String NOTIFICATION_CHANNEL_ID = "i";
    String NOTIFICATION_CHANNEL_NAME = "n";
    String NOTIFICATION_CHANNEL_GROUP_ID = "gi";
    String NOTIFICATION_CHANNEL_GROUP_NAME = "gn";
    String NOTIFICATION_CHANNEL_DESCRIPTION = "d";
    String NOTIFICATION_CHANNEL_BYPASSDND = "chnl_bdnd";
    String NOTIFICATION_CHANNEL_DELETE_ID = "delchnl_id";
    String NOTIFICATION_CHANNEL_DELETE_GROUP_ID = "delgrp_id";
    String CUSTOM_CHANNEL_CURRENT_DATE= "customChannelCurrentDate";
    String IZ_LIMIT_EXCEED_MSG = "Limit exceed; cannot create more channels";
    String IZ_NOTIFICATION_CHANNEL = "Notification Channel";
    String pulseRid = "pulseRid";
    String pulseCid = "pulseCid";

    int CAMPAIGN_SI = 6;
    int CAMPAIGN_SE = 7;
    String PulseTemplate = "ot";
    String IZ_SWIPE_GESTURE = "pulseSwipeDirection";
    String IZ_OT = "exitIntentTemplateID";
    String P_URL = "pUrl";
    String PULSE_IMP = "pulseImp";


}








