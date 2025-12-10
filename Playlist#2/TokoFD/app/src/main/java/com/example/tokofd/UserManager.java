package com.example.tokofd;

import android.content.Context;
import android.content.SharedPreferences;

public class UserManager {

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";

    private SharedPreferences sharedPreferences;

    public UserManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Simulasi Login
    public boolean login(String username, String password) {
        String storedUsername = sharedPreferences.getString(KEY_USERNAME, null);
        String storedPassword = sharedPreferences.getString(KEY_PASSWORD, null);
        return username.equals(storedUsername) && password.equals(storedPassword);
    }

    // Simulasi Registrasi
    public boolean register(String username, String password) {
        if (sharedPreferences.contains(KEY_USERNAME)) {
            return false; // Sudah terdaftar
        }
        sharedPreferences.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .apply();
        return true;
    }
}
