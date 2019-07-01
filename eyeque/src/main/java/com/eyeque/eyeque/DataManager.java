package com.amazonaws.androidtest;

import android.content.Context;
import android.content.SharedPreferences;

public class DataManager {

    private static final String PREF_NAME = "com.eyeque.pushdemo.pushkey";

    private String endpoint;
    private static String KEY_ENDPOINT = "endpoint";

    private static DataManager sInstance;
    private final SharedPreferences sharedPreferences;

    private DataManager (Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        load();
    }

    public static synchronized void initializeInstance(Context context){
        if(sInstance == null) {
            sInstance = new DataManager(context);
        }
    }

    public static synchronized DataManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(DataManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    private void load() {
        endpoint = sharedPreferences.getString(KEY_ENDPOINT, null);
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        sharedPreferences.edit().putString(KEY_ENDPOINT, endpoint).commit();
        this.endpoint = endpoint;
    }
}
