package com.yy.misaka.demo.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2016/7/26.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {

    private TestNotificationCallBack notificationCallBack;

    public void setNotificationCallBack(TestNotificationCallBack notificationCallBack) {
        this.notificationCallBack = notificationCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String payload = intent.getStringExtra("payload");
        notificationCallBack.onNotification(title, message, payload);
    }

    public interface TestNotificationCallBack {
        void onNotification(String title, String message, String payload);
    }

}
