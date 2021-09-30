package com.momagic.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.momagic.AppConstant;
import com.momagic.Payload;
import com.momagic.PreferenceUtil;

import java.lang.ref.PhantomReference;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "datb";
    private static final String TABLE_NAME = "pushnotifications";
    private static final String ID = "id";
    private static final String NOTIFICATION_TITLE = "title";
    private static final String NOTIFICATION_MESSAGE = "message";
    private static final String NOTIFICATION_ICON = "icon";
    private static final String NOTIFICATION_BANNER_IMAGE = "bImage";
    private static final String NOTIFICATION_CID= "cid";
    private static final String NOTIFICATION_RID = "rid";
    private static final  String NOTIFICATION_ID="notificationID";
    private static final String APP_ID="appId";
    private static final String DEVICE_ID="deviceID";
    private static final String DEVICE_TOKEN="deviceToken";
    private static final String NOTIFICATION_BADGE_ICON="bi";
    private static final String NOTIFICATION_BADGE_COLOR="bg";
    private static final String FETCH_URL="adURL";
    private static final String LANDING_URL="url";
    private static final String BUTTON1_NAME="BName";
    private static final String BUTTON1_URL="b1Link";
    /*
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
                        payload.setPush_type(AppConstant.PUSH_FCM);
                        payload.setSound(payloadObj.optString(ShortpayloadConstant.NOTIFICATION_SOUND));
                        payload.setMaxNotification(payloadObj.optInt(ShortpayloadConstant.MAX_NOTIFICATION));
                        payload.setFallBackDomain(payloadObj.optString(ShortpayloadConstant.FALL_BACK_DOMAIN));
                        payload.setFallBackSubDomain(payloadObj.optString(ShortpayloadConstant.FALLBACK_SUB_DOMAIN));
                        payload.setFallBackPath(payloadObj.optString(ShortpayloadConstant.FAll_BACK_PATH));
                        payload.setDefaultNotificationPreview(payloadObj.optInt(ShortpayloadConstant.TEXTOVERLAY));
                        payload.setNotification_bg_color(payloadObj.optString(ShortpayloadConstant.BGCOLOR));

     */
    private static Context context;


    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }
// create a table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY," + NOTIFICATION_TITLE + " TEXT,"
                + NOTIFICATION_MESSAGE + " TEXT,"
                + NOTIFICATION_ICON + " TEXT,"
                + NOTIFICATION_BANNER_IMAGE + " TEXT,"
                + NOTIFICATION_CID + " TEXT,"
                + NOTIFICATION_RID + " TEXT,"
                + NOTIFICATION_ID + " TEXT,"
                + APP_ID + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(sqLiteDatabase);
    }
   public void addNotificationInDB(Payload payload) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTIFICATION_TITLE, payload.getTitle());
        values.put(NOTIFICATION_MESSAGE, payload.getMessage());
        values.put(NOTIFICATION_ICON,payload.getIcon());
        values.put(NOTIFICATION_BANNER_IMAGE,payload.getBanner());
        values.put(NOTIFICATION_CID,payload.getId());
        values.put(NOTIFICATION_RID,payload.getRid());
        values.put(APP_ID, PreferenceUtil.getInstance(context).getStringData(AppConstant.APPPID));
        values.put(NOTIFICATION_ID,payload.getId());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
   public Payload getNotificationFromDB(String notificationID) {
        SQLiteDatabase db = this.getReadableDatabase();

        @SuppressLint("Recycle") Cursor cursor = db.query(TABLE_NAME, new String[] { NOTIFICATION_ID,
                        NOTIFICATION_TITLE, NOTIFICATION_MESSAGE,NOTIFICATION_ICON,NOTIFICATION_BANNER_IMAGE,NOTIFICATION_RID,NOTIFICATION_CID }, NOTIFICATION_ID + "=?",
                new String[] { notificationID}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Payload payload = new Payload();
        payload.setTitle(cursor.getString(1));
        payload.setMessage(cursor.getString(2));


        return payload;
    }
    public void deleteNotificationFromDB(Payload payload) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, NOTIFICATION_RID + " = ?",
                new String[] { String.valueOf(payload.getRid()) });
        db.close();
    }
}
