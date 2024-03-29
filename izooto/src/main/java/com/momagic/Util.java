package com.momagic;


import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class Util {
    private static String CIPHER_NAME = "AES/CBC/PKCS5PADDING";
    private static int CIPHER_KEY_LEN = 16;

    public static String decrypt(String key, String data) {
        try {
            if (key.length() < CIPHER_KEY_LEN) {
                int numPad = CIPHER_KEY_LEN - key.length();

                StringBuilder keyBuilder = new StringBuilder(key);
                for (int i = 0; i < numPad; i++) {
                    keyBuilder.append("0"); //0 pad to len 16 bytes
                }
                key = keyBuilder.toString();

            } else if (key.length() > CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN); //truncate to 16 bytes
            }

            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("ISO-8859-1"), "AES");

            Cipher cipher = Cipher.getInstance(CIPHER_NAME);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] decodedEncryptedData = android.util.Base64.decode(parts[0], android.util.Base64.DEFAULT);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    private static Bitmap getBitMap(String src) {
        int retry = 0;
        boolean isCheck = false;
        do {
            if (isCheck) {
                sleepTime(2000);

            }
            try {
                return BitmapFactory.decodeStream(new URL(src).openConnection().getInputStream());
            } catch (Throwable t) {
                retry++;
                isCheck = true;
                if (retry >= 4) {
                    DebugFileManager.createExternalStoragePublic(DATB.appContext, t.toString(), "[Log-> e]->getBitmapFromURL");
                    return null;
                }

            }
        } while (retry < 4);

        return null;
    }

    public static Bitmap getBitmapFromURL(String url) {
        if (url == null)
            return null;

        if (url != "" && !url.isEmpty()) {
            String trimmedName = url.trim();
            trimmedName = trimmedName.replace("///", "/");
            trimmedName = trimmedName.replace("//", "/");
            trimmedName = trimmedName.replace("http:/", "https://");
            trimmedName = trimmedName.replace("https:/", "https://");
            if (trimmedName.contains(".jpeg") || trimmedName.contains(".jpg") || trimmedName.contains(".png") || trimmedName.contains(".webp") || trimmedName.contains(".JPG") || trimmedName.contains(".PNG") || trimmedName.contains(".JPEG") || trimmedName.contains(".WEBP")) {
                if (trimmedName.startsWith("http://") || trimmedName.startsWith("https://")) {
                    Bitmap bmp = getBitMap(trimmedName);
                    if (bmp != null) {
                        return bmp;
                    } else {
                        DebugFileManager.createExternalStoragePublic(DATB.appContext, url, "[Log-> e]->getBitmapFromURL");

                    }
                }
            } else {
                DebugFileManager.createExternalStoragePublic(DATB.appContext, url, "[Log-> e]->getBitmapFromURL");
                return null;
            }
        } else {
            DebugFileManager.createExternalStoragePublic(DATB.appContext, "Image URL" + url, "[Log-> e]->getBitmapFromURL");
            return null;
        }
        return null;

    }

    public static String getAndroidId(Context mContext) {
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.v("android_id", android_id);
        return android_id;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static boolean isNotificationEnabled(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    boolean hasFCMLibrary() {
        try {
            return com.google.firebase.messaging.FirebaseMessaging.class != null;
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean isInitializationValid() {
        checkForFcmDependency();
        return true;
    }

    boolean checkForFcmDependency() {
        if (!hasFCMLibrary()) {
            Lg.d(AppConstant.APP_NAME_TAG, AppConstant.CHECKFCMLIBRARY);
            return false;
        }
        return true;

    }

    public static String trimString(String optString) {
        if (optString.length() > 32) {
            int length = optString.length() - 32;
            return optString.substring(0, length);
        }
        return null;
    }

    public static Drawable getApplicationIcon(Context context) {
        ApplicationInfo ai;
        Drawable icon = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
            //e.printStackTrace();
        }

        if (ai != null) {
            icon = context.getPackageManager().getApplicationIcon(ai);
        }


        return icon;
    }


    public static boolean CheckValidationString(String optString) {
        if (optString.length() > 32) {
            return true;
        } else {
            return false;
        }
    }

    public static String getDeviceLanguage() {
        //return Locale.getDefault().getDisplayLanguage();
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = DATB.appContext.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = DATB.appContext.getResources().getConfiguration().locale;
        }
        return locale.getDisplayLanguage();

    }

    public static String getIntegerToBinary(int number) {
        return String.format("%16s", Integer.toBinaryString(number)).replace(' ', '0');

    }

    public static boolean checkNotificationEnable() {
        return NotificationManagerCompat.from(DATB.appContext).areNotificationsEnabled();

    }

    public static String getPackageName(Context context) {
        ApplicationInfo ai;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
            //e.printStackTrace();
        }
        return context.getPackageName();
    }

    public static boolean isMatchedString(String s) {
        try {
            Pattern pattern = Pattern.compile("[a-zA-Z0-9-_.~%]{1,900}");
            Matcher matcher = pattern.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static int convertStringToDecimal(String number) {
        char[] numChar = number.toCharArray();
        int intValue = 0;
        int decimal = 1;
        for (int index = numChar.length; index > 0; index--) {
            if (index == 1) {
                if (numChar[index - 1] == '-') {
                    return intValue * -1;
                } else if (numChar[index - 1] == '+') {
                    return intValue;
                }
            }
            intValue = intValue + (((int) numChar[index - 1] - 48) * (decimal));
            decimal = decimal * 10;
        }
        return intValue;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getDeviceLanguageTag() {
        if (DATB.appContext != null) {
            Locale locale;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                locale = DATB.appContext.getResources().getConfiguration().getLocales().get(0);
                return locale.getDefault().getDisplayLanguage();
            } else {
                return "iz-ln";
            }
        } else {
            return "iz_ln";
        }

    }

    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    public static CharSequence makeBoldString(CharSequence title) {
        if (Build.VERSION.SDK_INT >= 24) {
            title = Html.fromHtml("<font color=\"" + ContextCompat.getColor(DATB.appContext, R.color.iz_black) + "\"><b>" + title + "</b></font>", HtmlCompat.FROM_HTML_MODE_LEGACY);// for 24 api and more
        } else {
            title = Html.fromHtml("<font color=\"" + ContextCompat.getColor(DATB.appContext, R.color.iz_black) + "\"><b>" + title + "</b></font>"); // or for older api
        }
        return title;
    }

    public static CharSequence makeBlackString(CharSequence title) {
        if (Build.VERSION.SDK_INT >= 24) {
            title = Html.fromHtml("<font color=\"" + ContextCompat.getColor(DATB.appContext, R.color.iz_black) + "\">" + title + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY); // for 24 api and more
        } else {
            title = Html.fromHtml("<font color=\"" + ContextCompat.getColor(DATB.appContext, R.color.iz_black) + "\">" + title + "</font>"); // or for older api
        }
        return title;
    }

    public static Bitmap makeCornerRounded(Bitmap image) {
        Bitmap imageRounded = Bitmap.createBitmap(image.getWidth(), image.getHeight(), image.getConfig());
        Canvas canvas = new Canvas(imageRounded);
        Paint mpaint = new Paint();
        mpaint.setAntiAlias(true);
        mpaint.setShader(new BitmapShader(image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0.0f, 0.0f, image.getWidth(), image.getHeight())), 10, 10, mpaint);
        return imageRounded;
    }

    public static void sleepTime(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public static String dayDifference(String currentDate, String previousDate) {
        if (previousDate.isEmpty())
            return "";
        String dayDifference = "";
        try {
            Date date1;
            Date date2;
            SimpleDateFormat dates = new SimpleDateFormat("yyyy.MM.dd");
            date1 = dates.parse(currentDate);
            date2 = dates.parse(previousDate);
            long difference = date1.getTime() - date2.getTime();
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            dayDifference = Long.toString(differenceDates);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return dayDifference;
    }

    public static int getBinaryToDecimal(int cfg) {
        String fourthDg, fifthDg, sixthDg;

        String data = Util.getIntegerToBinary(cfg);
        if (data != null && !data.isEmpty()) {
            fourthDg = String.valueOf(data.charAt(data.length() - 4));
            fifthDg = String.valueOf(data.charAt(data.length() - 5));
            sixthDg = String.valueOf(data.charAt(data.length() - 6));
        } else {
            fourthDg = "0";
            fifthDg = "0";
            sixthDg = "0";
        }
        String dataCFG = sixthDg + fifthDg + fourthDg;
        int decimalData = Integer.parseInt(dataCFG, 2);
        return decimalData;
    }

    static boolean isValidResourceName(String name) {
        return (name != null && !name.matches("^[0-9]"));
    }

    static Uri getSoundUri(Context context, String sound) {
        Resources resources = context.getResources();
        String packageName = context.getPackageName();
        int soundId;
        if (isValidResourceName(sound)) {
            soundId = resources.getIdentifier(sound, "raw", packageName);
            if (soundId != 0)
                return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + soundId);
        }
        soundId = resources.getIdentifier("izooto_default_sound", "raw", packageName);
        if (soundId != 0)
            return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + soundId);
        return null;
    }

    public static void setException(Context context, String exception, String className, String methodName) {
        if (context == null)
            return;
        try {
            final PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            if (!exception.isEmpty()) {
                Map<String, String> mapData = new HashMap<>();
                mapData.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                mapData.put(AppConstant.TOKEN, "" + preferenceUtil.getStringData(AppConstant.FCM_DEVICE_TOKEN));
                mapData.put(AppConstant.HMS_TOKEN, "" + preferenceUtil.getStringData(AppConstant.HMS_TOKEN));
                mapData.put(AppConstant.XiaomiToken, "" + preferenceUtil.getStringData(AppConstant.XiaomiToken));
                mapData.put(AppConstant.ANDROID_ID, "" + Util.getAndroidId(context));
                mapData.put(AppConstant.EXCEPTION_, "" + exception);
                mapData.put(AppConstant.METHOD_NAME, "" + methodName);
                mapData.put(AppConstant.ClASS_NAME, "" + className);
                mapData.put(AppConstant.ANDROID_VERSION, "" + Build.VERSION.RELEASE);
                mapData.put(AppConstant.DEVICE_NAME, "" + Util.getDeviceName());
                mapData.put(AppConstant.SDK, AppConstant.SDK_VERSION);

                RestClient.postRequest(RestClient.APP_EXCEPTION_URL, mapData, null, new RestClient.ResponseHandler() {
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
        } catch (Exception e) {
            Log.e("Exception ex -- ", e.toString());
        }
    }

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static HashMap<String, Object> toMap(JSONObject jsonobj) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keys = jsonobj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonobj.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    static String getAppVersion(Context context) {
        if (context == null)
            return "App Version  is not Found";
        PackageManager pm = context.getPackageManager();
        String pkgName = context.getPackageName();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0);
            return pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "App Version  is not Found";
        }
    }

    public static boolean isAppInForeground(Context context) {
        List<ActivityManager.RunningTaskInfo> task =
                ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                        .getRunningTasks(1);
        if (task.isEmpty()) {
            // app is in background
            return false;
        }
        return task
                .get(0)
                .topActivity
                .getPackageName()
                .equalsIgnoreCase(context.getPackageName());
    }

    static String getTimeWithoutDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String currentData = sdf.format(new Date());
        return currentData;
    }

    static void trackClickOffline(Context context, String apiUrl, String constantValue, String rid, String cid, int click) {
        if (context == null)
            return;

        try {
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            JSONObject payloadJSON = new JSONObject();
            JSONArray jsonArray;
            if (!preferenceUtil.getStringData(constantValue).isEmpty()) {
                jsonArray = new JSONArray(preferenceUtil.getStringData(constantValue));
            } else
                jsonArray = new JSONArray();
            payloadJSON.put(AppConstant.APPPID, preferenceUtil.getDataBID(AppConstant.APPPID));
            payloadJSON.put(AppConstant.SDK, AppConstant.SDK_VERSION);
            payloadJSON.put(AppConstant.BKEY, Util.getAndroidId(context));
            if (!apiUrl.isEmpty())
                payloadJSON.put(AppConstant.STORE_URL, apiUrl);
            if (!rid.isEmpty())
                payloadJSON.put(AppConstant.RID, rid);

            if (constantValue.equalsIgnoreCase(AppConstant.IZ_NOTIFICATION_CLICK_OFFLINE)) {
                payloadJSON.put("notification_op", "click");
                if (!cid.isEmpty())
                    payloadJSON.put(AppConstant.CID_, cid);
                if (click != 0)
                    payloadJSON.put("click", click);
            }

            jsonArray.put(payloadJSON);

            preferenceUtil.setStringData(constantValue, jsonArray.toString());
        } catch (Exception e) {
            Util.setException(context, e.toString(), "trackClickOffline()", "Util");
        }

    }

    static void trackMediation_Impression_Click(Context context, String hitName, String impressionORClickDATA) {
        if (context == null)
            return;

        try {
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            JSONObject payloadJSON = new JSONObject();
            JSONArray jsonArray;
            if (!preferenceUtil.getStringData(AppConstant.STORE_MEDIATION_RECORDS).isEmpty()) {
                jsonArray = new JSONArray(preferenceUtil.getStringData(AppConstant.STORE_MEDIATION_RECORDS));
            } else
                jsonArray = new JSONArray();
            payloadJSON.put(AppConstant.STORE_MED_API, hitName);
            payloadJSON.put(AppConstant.STORE_MED_DATA, impressionORClickDATA.replace("\n", ""));
            jsonArray.put(payloadJSON);
            preferenceUtil.setStringData(AppConstant.STORE_MEDIATION_RECORDS, jsonArray.toString());
        } catch (Exception e) {
            Util.setException(context, e.toString(), "trackMediation_Impression_Click", "Util");
        }
    }

    static boolean ridExists(JSONArray jsonArray, String rid) {
        return jsonArray.toString().contains("\"rid\":\"" + rid + "\"");
    }

    @Nullable
    static BigInteger getAccentColor() {
        try {
            if (DATB.appContext == null)
                return null;
            String defaultColor = getResourceString(DATB.appContext, AppConstant.NOTIFICATION_ACCENT_COLOR, null);
            if (defaultColor.charAt(0) == '#') {
                String default_Color = "";
                default_Color = defaultColor.replace("#", "");
                if (default_Color != null) {
                    return new BigInteger(default_Color, 16);
                }
            } else {
                if (defaultColor != null) {
                    return new BigInteger(defaultColor, 16);
                }
            }
        } catch (Throwable t) {
        }
        return null;
    }

    static String getResourceString(@NonNull Context context, String key, String defaultStr) {
        Resources resources = context.getResources();
        int bodyResId = resources.getIdentifier(key, AppConstant.STRING_RESOURCE_NAME, context.getPackageName());
        if (bodyResId != 0)
            return resources.getString(bodyResId);
        return defaultStr;
    }

    public static String getApplicationName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    static String getChannelName(Context context) {
        if (context != null) {
            String channelName = "";
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            if (preferenceUtil.getStringData(AppConstant.iZ_STORE_CHANNEL_NAME) != "" && !preferenceUtil.getStringData(AppConstant.iZ_STORE_CHANNEL_NAME).isEmpty()) {
                channelName = preferenceUtil.getStringData(AppConstant.iZ_STORE_CHANNEL_NAME);
            } else {
                channelName = AppConstant.CHANNEL_NAME;
            }
            return channelName;
        }
        return AppConstant.CHANNEL_NAME;
    }

    protected static boolean notificationMode() {
        Locale locale = Locale.getDefault();
        return TextUtils.getLayoutDirectionFromLocale(locale) != ViewCompat.LAYOUT_DIRECTION_LTR;
    }


    /* check the expiry time to current time difference in seconds form */
    /* check the expiry time to current time difference in seconds form */
    static String getTimerValue(String createdTime, String expTime) {
        try {
            long timerValue = 0;
            long timerValue_In_Seconds = 0;
            long deliveryTime = System.currentTimeMillis();
            long et_In_Millis = MinutesToMillisecondsConverter(Integer.parseInt(expTime));
            long expiryTime = Long.parseLong(createdTime) + et_In_Millis;

            if (deliveryTime >= Long.parseLong(createdTime) && deliveryTime <= expiryTime) {
                timerValue = expiryTime - deliveryTime;
                timerValue_In_Seconds = (timerValue / 1000);
            }

            if (timerValue_In_Seconds >= 1 && timerValue_In_Seconds < 3600) {
                return String.valueOf(timerValue_In_Seconds);
            } else {
                DebugFileManager.createExternalStoragePublic(DATB.appContext, AppConstant.IZ_TIMER_VALUE_MESSAGE, AppConstant.IZ_TIMER_MESSAGE);
                return "";
            }
        } catch (Exception e) {
            DebugFileManager.createExternalStoragePublic(DATB.appContext, AppConstant.IZ_TIMER_VALUE_MESSAGE, AppConstant.IZ_TIMER_MESSAGE);
            return "";
        }
    }

    // convert minutes into millis
    private static long MinutesToMillisecondsConverter(int minutes) {
        return (minutes * 60 * 1000L);
    }

    // notifications sticky support
    static boolean enableSticky(Payload payload) {
        return payload.getMakeStickyNotification() != null &&
                !payload.getMakeStickyNotification().isEmpty() && payload.getMakeStickyNotification().equals("1");
    }

    // parse vibration pattern
    static long[] parseVibrationPattern(Object patternObj) {
        try {
            JSONArray jsonVibArray;
            if (patternObj instanceof String) {
                jsonVibArray = new JSONArray((String) patternObj);
            } else {
                jsonVibArray = (JSONArray) patternObj;
            }
            long[] longArray = new long[jsonVibArray.length()];
            for (int i = 0; i < jsonVibArray.length(); i++) {
                longArray[i] = jsonVibArray.optLong(i);
            }
            return longArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    // To Handle Exception once
    static void handleExceptionOnce(Context context, String exception, String className, String methodName) {
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
        if (!preferenceUtil.getBoolean(methodName)) {
            setException(context, exception, className, methodName);
            preferenceUtil.setBooleanData(methodName, true);
        }
        DebugFileManager.createExternalStoragePublic(context, exception + " " + methodName, "[Log.e]-> " + className);
    }

    static void parseXml(RssContentCallbackListener callbackListener) {
        ArrayList<Payload> contentList = new ArrayList<>();
        PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(DATB.appContext);

        new Thread(() -> {
            try {
                URL url = new URL(DATB.pUrl);
                XmlPullParser pullParser = XmlPullParserFactory.newInstance().newPullParser();
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    pullParser.setInput(new InputStreamReader(urlConnection.getInputStream()));
                    int eventType = pullParser.getEventType();
                    Payload payload = null;

                    boolean isNull = true;
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = pullParser.getName();
                            if (tagName.equals("title") && isNull) {
                                isNull = false;
                                String name = pullParser.nextText();
                                preferenceUtil.setStringData("pubName", name);
                                pullParser.require(XmlPullParser.END_TAG, null, "title");
                            }
                            if ("item".equals(tagName)) {
                                payload = new Payload();
                            } else if ("title".equals(tagName) && payload != null) {
                                payload.setTitle(pullParser.nextText());
                            } else if ("link".equals(tagName) && payload != null) {
                                payload.setLink(pullParser.nextText());
                            } else if ("description".equals(tagName) && payload != null) {
                                payload.setDescription(pullParser.nextText());
                            } else if ("pubDate".equals(tagName) && payload != null) {
                                payload.setCreated_Time(pullParser.nextText());
                            } else if ("image".equals(tagName) && payload != null) {
                                payload.setBanner(pullParser.nextText());
                            } else if ("media:content".equals(tagName) && payload != null) {
                                String imageUrl = pullParser.getAttributeValue(null, "url");
                                payload.setBanner(imageUrl);
                            } else if ("category".equals(tagName) && payload != null) {
                                String domain = pullParser.getAttributeValue(null, "domain");
                                if ("foxnews.com/metadata/dc.source".equals(domain)) {
                                    eventType = pullParser.next();
                                    if (eventType == XmlPullParser.TEXT) {
                                        String categoryValue = pullParser.getText();
                                        payload.setCategory(categoryValue);
                                    }

                                }
                            }
                        } else if (eventType == XmlPullParser.END_TAG && "item".equals(pullParser.getName()) && payload != null) {
                            contentList.add(payload);
                            callbackListener.onCallback(contentList);
                            contentList.clear();
                            payload = null;
                        }
                        eventType = pullParser.next();
                    }
                } else {
                    Log.e("http connection error", "bad request...");
                }
            } catch (XmlPullParserException | IOException e) {
                Log.e("http connection error", "bad request" + e);
            }
        }).start();
    }

    protected static void pulseClickAPI(Context context, Payload userModal) {
        try {
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            HashMap<String, String> hashMap = new HashMap<>();
            if (preferenceUtil != null) {
                hashMap.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                hashMap.put(AppConstant.ANDROID_ID, Util.getAndroidId(context));
                hashMap.put(AppConstant.VER_, "" + AppConstant.SDK_VERSION);
                hashMap.put("link", userModal.getLink());
                hashMap.put("tt", String.valueOf(DATB.OT_ID)); // 5 means swipe left or right 6 means on backPressed
                hashMap.put("ot", String.valueOf(DATB.OT_ID));
                hashMap.put("cid", DATB.pulseCid);
                hashMap.put("rid", DATB.pulseRid);
            }
            RestClient.postRequest(RestClient.iZ_PULSE_FEATURE_CLICK, hashMap, null, new RestClient.ResponseHandler() {
                @Override
                void onSuccess(String response) {
                    super.onSuccess(response);
                }

                @Override
                void onFailure(int statusCode, String response, Throwable throwable) {
                    super.onFailure(statusCode, response, throwable);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // pulse impression
    protected static void pulseImpression(Context context) {
        try {
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(context);
            HashMap<String, String> hashMap = new HashMap<>();
            if (preferenceUtil != null) {
                hashMap.put(AppConstant.PID, preferenceUtil.getDataBID(AppConstant.APPPID));
                hashMap.put(AppConstant.ANDROID_ID, Util.getAndroidId(context));
                hashMap.put(AppConstant.VER_, "" + AppConstant.SDK_VERSION);
                hashMap.put("tt", String.valueOf(DATB.OT_ID)); // 5 means swipe left or right 6 means on backPressed
                hashMap.put("ot", String.valueOf(DATB.OT_ID));
                hashMap.put("cid", DATB.pulseCid);
                hashMap.put("rid", DATB.pulseRid);
            }
            RestClient.postRequest(RestClient.iZ_PULSE_FEATURE_IMPRESSION, hashMap, null, new RestClient.ResponseHandler() {
                @Override
                void onSuccess(String response) {
                    super.onSuccess(response);
                    Log.e("HashMap", hashMap.toString());
                }

                @Override
                void onFailure(int statusCode, String response, Throwable throwable) {
                    super.onFailure(statusCode, response, throwable);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String getTimeAgo(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date date = dateFormat.parse(timestamp);
            if (date != null) {
                long timeInMillis = date.getTime();
                long now = System.currentTimeMillis();
                CharSequence relativeTimeSpan = DateUtils.getRelativeTimeSpanString(timeInMillis, now, DateUtils.MINUTE_IN_MILLIS);
                return relativeTimeSpan.toString()
                        .replace(" minutes", "m")
                        .replace(" minute", "m")
                        .replace(" hours", "h")
                        .replace(" hour", "h")
                        .replace(" seconds", "s")
                        .replace(" second", "s");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return "";
    }
    protected static int getValidIdForCampaigns(Payload payload){
        int digit = 0;
        try{
            String digits = payload.getRid().trim();
            digit = digits.charAt(0) - '0';
        }catch (Exception e){
            Util.handleExceptionOnce(DATB.appContext,e.toString(),"Util","getValidIdForCampaigns");
            DebugFileManager.createExternalStoragePublic(DATB.appContext, e.toString(), "[Log.e]->RID");
        }
        return digit;
    }
}

