package com.frontendsourcecode.expenser.util.localstorage;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LocalStorage {

    public static final String KEY_USER = "User";
    private static final String KEY_FIREBASE_TOKEN = "firebaseToken";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_SETTING = "setting";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";


    private static LocalStorage instance = null;
    SharedPreferences sharedPreferences;
    Editor editor;
    int PRIVATE_MODE = 0;
    Context _context;

    public LocalStorage(Context context) {
        sharedPreferences = context.getSharedPreferences("Preferences", 0);
    }

    public static LocalStorage getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalStorage.class) {
                if (instance == null) {
                    instance = new LocalStorage(context);
                }
            }
        }
        return instance;
    }

    public void createUserLoginSession(String user) {
        editor = sharedPreferences.edit();
        editor.putBoolean(IS_USER_LOGIN, true);
        editor.putString(KEY_USER, user);
        editor.commit();
    }

    public String getUserLogin() {
        return sharedPreferences.getString(KEY_USER, "");
    }


    public void logoutUser() {
        editor = sharedPreferences.edit();
        editor.remove(KEY_USER);
        editor.remove(IS_USER_LOGIN);
        editor.commit();
    }

    public boolean checkLogin() {
        // Check login status
        return !this.isUserLoggedIn();
    }


    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(IS_USER_LOGIN, false);
    }



    public String getFirebaseToken() {
        return sharedPreferences.getString(KEY_FIREBASE_TOKEN, null);
    }

    public void setFirebaseToken(String firebaseToken) {
        editor = sharedPreferences.edit();
        editor.putString(KEY_FIREBASE_TOKEN, firebaseToken);
        editor.commit();
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void setToken(String token) {
        editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, token);
        editor.commit();
    }

    public String getSetting() {
        return sharedPreferences.getString(KEY_SETTING, null);
    }

    public void setSetting(String setting) {
        editor = sharedPreferences.edit();
        editor.putString(KEY_SETTING, setting);
        editor.commit();
    }



}
