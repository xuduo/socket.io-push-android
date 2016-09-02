package com.yy.httpproxy.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.yy.httpproxy.util.Log;

public class DummyService extends Service {

    private final String TAG = "DummyService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "DummyService onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "DummyService onDestroy");
    }
}
