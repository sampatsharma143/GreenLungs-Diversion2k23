package com.shunyank.appwriteproject;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

public class App extends Application {
    private static App mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("app instance","created");
        if (mInstance == null) {
            mInstance = this;
        }
    }
    @NonNull
    public static App getInstance() {
        return mInstance;
    }

}