package com.app.vekadelivery;

import androidx.appcompat.app.AppCompatDelegate;

public class Application  extends android.app.Application {

    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
