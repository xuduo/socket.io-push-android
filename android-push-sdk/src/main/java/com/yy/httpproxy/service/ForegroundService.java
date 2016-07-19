package com.yy.httpproxy.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import com.yy.httpproxy.util.Log;

public class ForegroundService extends Service {

    private final String TAG = "FakeService";

    public static ConnectionService instance;
    @Override
    public void onCreate() {
        super.onCreate();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setPriority(Notification.PRIORITY_MIN);
        startForeground(12345, builder.build());
        beginForeground();
        stopSelf();
        Log.i(TAG, "FakeService onCreate");
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
        Log.i(TAG, "FakeService onDestroy");
        stopForeground(true);
    }

    public void beginForeground() {
        if(instance != null) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(instance);
            builder.setPriority(Notification.PRIORITY_MIN);
            instance.startForeground(12345, builder.build());
        }
    }

}
