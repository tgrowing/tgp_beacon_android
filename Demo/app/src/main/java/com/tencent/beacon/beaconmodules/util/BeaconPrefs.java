package com.tencent.beacon.beaconmodules.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BeaconPrefs {
    // 策略域名
    public static final String PREFS_KEY_CONFIG_HOST = "prefs_key_config_host";
    // 上报域名
    public static final String PREFS_KEY_UPLOAD_HOST = "prefs_key_upload_host";
    // appkey
    public static final String PREFS_KEY_APPKEY = "prefs_key_appkey";


    private Context mContext;

    public BeaconPrefs(Context context) {
        mContext = context;
    }

    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        SharedPreferences prefs = getSharedPreferences();
        return prefs.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setInt(String key, int value) {
        SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key, int defaultValue) {
        SharedPreferences prefs = getSharedPreferences();
        return prefs.getInt(key, defaultValue);
    }

    public void setFloat(String key, float value) {
        SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public float getFloat(String key, float defaultValue) {
        SharedPreferences prefs = getSharedPreferences();
        return prefs.getFloat(key, defaultValue);
    }

    public void setString(String key, String value) {
        SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key, String defaultValue) {
        SharedPreferences prefs = getSharedPreferences();
        return prefs.getString(key, defaultValue);
    }

    public void setLong(String key, long value) {
        SharedPreferences prefs = getSharedPreferences();
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key, long defaultValue) {
        SharedPreferences prefs = getSharedPreferences();
        return prefs.getLong(key, defaultValue);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }


}
