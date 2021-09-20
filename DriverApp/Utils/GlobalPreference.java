package com.aumento.onlinecabdriver.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GlobalPreference {

    private SharedPreferences prefs;
    private Context context;
    private SharedPreferences.Editor editor;

    public GlobalPreference(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void addIP(String ip) {
        editor.putString(Constants.IP, ip);
        editor.apply();
    }

    public String RetriveIP() {
        return prefs.getString(Constants.IP, "");
    }

    public void addUID(String uid) {
        editor.putString(Constants.UID, uid);
        editor.apply();
    }

    public String RetriveUID() {
        return prefs.getString(Constants.UID, "");
    }

    public void setLoginStatus(Boolean status)     {
        editor.putBoolean(Constants.LOGIN_STATUS, status);
        editor.apply();
    }

    public Boolean getLoginStatus()
    {
        return prefs.getBoolean(Constants.LOGIN_STATUS,false);
    }

    public void addLatitude(String latitude) {
        editor.putString(Constants.LATITUDE, latitude);
        editor.apply();
    }

    public void addLongitude(String longitude) {
        editor.putString(Constants.LONGITUDE, longitude);
        editor.apply();
    }

    public String getLatitude() {
        return prefs.getString(Constants.LATITUDE, "");
    }

    public String getLongitude() {
        return prefs.getString(Constants.LONGITUDE, "");
    }

}