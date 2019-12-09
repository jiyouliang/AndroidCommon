package com.jyl.test.loggerlib;

import android.app.Application;

import com.jiyouliang.log.Logger;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        Logger.setDebug(true);
    }
}
