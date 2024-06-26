package com.momagic;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
public class PreferenceUtil {
    public static final String SHARED_PREF_NAME = "DATAB";
    private static PreferenceUtil mContext;
    private final SharedPreferences mSpref;
    private String TAG = PreferenceUtil.class.getSimpleName();
    private PreferenceUtil(Context context) {
        mSpref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceUtil getInstance(Context context) {
        if (mContext == null)
            mContext = new PreferenceUtil(context);
        return mContext;
    }

    public static void logoutUser(Context context) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.clear();
        appInstallInfoEditor.commit();
    }





    public void setFloatData(Context context, String key, float value) {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putFloat(key, value);
        appInstallInfoEditor.commit();
    }

    public void setIntData(String key, int value) {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putInt(key, value);
        appInstallInfoEditor.commit();
    }

    public void setStringData(String key, String value) {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putString(key, value);
        appInstallInfoEditor.apply();
    }

    public boolean getBoolean(String key) {
        return mSpref.getBoolean(key, false);
    }

    public String getStringData(String key) {
        return mSpref.getString(key, "");

    }
    public int getIntData(String key) {
        return mSpref.getInt(key, 0);
    }
    public String getStringDataFilterCount(String key) {
        return mSpref.getString(key, "0");

    }


    public void setBooleanData(String key, boolean value) {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putBoolean(key, value);
        appInstallInfoEditor.apply();
    }

    public long getLongValue(String key) {
        if (mSpref.contains(key))
            return mSpref.getLong(key, 0L);
        else
            Lg.e(TAG, AppConstant.KEY_NOT_FOUND);

        return 0l;
    }

    public void setLongData(String key, long value) {
        SharedPreferences.Editor editor = mSpref.edit();
        editor.putLong(key, value);
        editor.apply();
    }
    public void setDataBID(String key,String id)
    {
        SharedPreferences.Editor appInstallInfoEditor = mSpref.edit();
        appInstallInfoEditor.putString(key, id);
        appInstallInfoEditor.apply();
    }
    public String getDataBID(String key)
    {
        return mSpref.getString(key, "");
    }
    public boolean getEnableState(String key) {
        return mSpref.getBoolean(key, true);
    }


}
