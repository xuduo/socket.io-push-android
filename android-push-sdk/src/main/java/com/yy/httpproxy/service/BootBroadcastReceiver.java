package com.yy.httpproxy.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yy.httpproxy.util.Log;

/**
 * Created by Administrator on 2015/12/31.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private static String TAG = "MyBootBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "BOOT_COMPLETED start ConnectService");
        Intent service = new Intent(context, ConnectionService.class);
        context.startService(service);
    }
}
