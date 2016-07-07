package com.yy.httpproxy.thirdparty;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.yy.httpproxy.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.yy.httpproxy.util.ServiceCheckUtil;

public class XiaomiProvider implements NotificationProvider {

    public final static String TAG = "XiaomiProvider";
    private String token;

    public XiaomiProvider(Context context) {

        String appId = getMetaDataValue(context, "XIAOMI_APP_ID");
        String appKey = getMetaDataValue(context, "XIAOMI_APP_KEY");
        Log.i(TAG, appId + " " + appKey);
        MiPushClient.registerPush(context, appId, appKey);

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.e(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(context, newLogger);
        Log.d(TAG, "init");
    }

    public static boolean available(Context context) {
        try {
            return Class.forName("com.xiaomi.mipush.sdk.MiPushClient") != null
                    && Class.forName("com.yy.httpproxy.thirdparty.XiaomiReceiver") != null
                    && ServiceCheckUtil.isServiceAvailable(context, XiaomiReceiver.class) && getMetaDataValue(context, "XIAOMI_APP_ID") != null && getMetaDataValue(context, "XIAOMI_APP_ID") != null;
        } catch (Throwable e) {
            Log.e(TAG, "available ", e);
            return false;
        }
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getType() {
        return "xiaomi";
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }

    private static String getMetaDataValue(Context context, String metaDataName) {
        String metaDataValue = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            metaDataValue = appInfo.metaData.getString(metaDataName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getMetaDataValue error ", e);
        }
        return metaDataValue;

    }

}
