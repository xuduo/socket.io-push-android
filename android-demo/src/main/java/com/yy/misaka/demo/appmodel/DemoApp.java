package com.yy.misaka.demo.appmodel;

import android.app.Application;
import android.util.Log;


/**
 * Created by xuduo on 6/13/16.
 */
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        Log.i("DemoLogger", "DemoApp onCreate");
        super.onCreate();
    }
}
