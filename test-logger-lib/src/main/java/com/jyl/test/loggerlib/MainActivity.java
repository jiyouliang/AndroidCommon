package com.jyl.test.loggerlib;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.jiyouliang.log.Logger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "LogDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.d(TAG, "debug信息");
        Logger.v(TAG, "v信息");
        Logger.w(TAG, "w信息");
        Logger.e(TAG, "e信息");

//        Logger.setDebug(true);
//        Log.d(TAG, "开启log日志");

        Logger.d(TAG, "debug信息");
        Logger.v(TAG, "v信息");
        Logger.w(TAG, "w信息");
        Logger.e(TAG, "e信息");

        Log.d(TAG, "设置log级别");
        Logger.setLogLevel(Logger.LogLevel.ERROR);
        Logger.d(TAG, "debug信息");
        Logger.v(TAG, "v信息");
//        Logger.setDebug(false);
        Logger.w(TAG, "w信息");
        Logger.e(TAG, "e信息");


//        testReadContact();

    }


}
