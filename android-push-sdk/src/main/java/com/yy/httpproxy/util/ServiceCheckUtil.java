package com.yy.httpproxy.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.util.Log;

import java.util.List;

public class ServiceCheckUtil {

    public static boolean isBroadcastReceiverAvailable(Context context, Class className) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            final Intent intent = new Intent(context, className);
            List resolveInfo =
                    packageManager.queryBroadcastReceivers(intent,
                            0);
            Log.d("ServiceCheckUtil", "isBroadcastReceiverAvailable " + className + " " + resolveInfo.size());
            return resolveInfo.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isIntentServiceAvailable(Context context, Class className) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            final Intent intent = new Intent(context, className);
            List resolveInfo =
                    packageManager.queryIntentServices(intent,
                            0);
            Log.d("ServiceCheckUtil", "isIntentServiceAvailable " + className + " " + resolveInfo.size());
            return resolveInfo.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPushProcessName(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        try {
            final Intent intent = new Intent(context, ConnectionService.class);
            List<ResolveInfo> resolveInfo =
                    packageManager.queryIntentServices(intent,
                            0);
            if (resolveInfo.size() > 0) {
                String processName = resolveInfo.get(0).serviceInfo.processName;
                Log.d("ServiceCheckUtil", "ConnectionService process " + processName);
                return processName;
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }
}
