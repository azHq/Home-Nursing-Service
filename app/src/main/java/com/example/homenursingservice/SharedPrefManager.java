package com.example.homenursingservice;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {


    private static final String SHARED_PREF_NAME = "USER_INFO";
    private static final String USER_ID= "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_TYPE= "USER_TYPE";
    private static final String PHONE_NUMBER= "PHONE_NUMBER";
    private static final String IMAGE_PATH= "IMAGE_PATH";
    private static final String DEVICE_ID="DEVICE_ID";
    private static final String GEOPOINT="GEOPOINT";
    private static final String ONLINE_STATUS="ONLINE_STATUS";
    private static final String ACCOUNT_STATUS="ACCOUNT_STATUS";
    private static final String NOTIFICATION="NOTIFICATION";


    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }


    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, user.getUser_id());
        editor.putString(USER_NAME,user.getUser_name());
        editor.putString(USER_TYPE, user.getUser_type());
        editor.putString(PHONE_NUMBER, user.getPhone_number());
        editor.putString(IMAGE_PATH, user.getImage_path());
        editor.putString(DEVICE_ID, user.getDevice_id());
        editor.putString(ACCOUNT_STATUS, user.account_status);
        editor.putString(ONLINE_STATUS, user.online_status);
        editor.putBoolean(NOTIFICATION, user.notification);
        editor.apply();
    }




    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID, null) != null;
    }


    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(USER_ID, null),
                sharedPreferences.getString(USER_NAME,null),
                sharedPreferences.getString(USER_TYPE, null),
                sharedPreferences.getString(PHONE_NUMBER, null),
                sharedPreferences.getString(IMAGE_PATH,null),
                sharedPreferences.getString(DEVICE_ID,null),
                sharedPreferences.getString(ACCOUNT_STATUS,null),
                sharedPreferences.getString(ONLINE_STATUS,null),
                sharedPreferences.getBoolean(NOTIFICATION, true)
        );
    }

    public void changeDeviceId(String device_id){

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEVICE_ID,device_id);
    }


    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}