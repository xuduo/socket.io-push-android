package com.yy.httpproxy.thirdparty;

import android.content.Context;

import com.yy.httpproxy.util.Log;

import com.yy.httpproxy.util.SystemProperty;

public class ProviderFactory {

    private static final String KEY_HUAWEI_VERSION = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION = "ro.miui.ui.version.name";
    private static final String TAG = "ProviderFactory";

    public static NotificationProvider getProvider(Context context) {
        Class provider = checkProvider(context);
        if (HuaweiProvider.class.equals(provider)) {
            return new HuaweiProvider(context);
        } else if (XiaomiProvider.class.equals(provider)){
            return new XiaomiProvider(context);
        } else if (UmengProvider.class.equals(provider)){
            return new UmengProvider(context);
        } else {
            return null;
        }
    }

    public static Class checkProvider(Context context) {
        final SystemProperty prop = new SystemProperty(context);
        boolean isHuaweiSystem = isSystem(prop, KEY_HUAWEI_VERSION);
        boolean isHuaweiAvailable = HuaweiProvider.available(context);
        Log.i(TAG, "isHuaweiSystem " + isHuaweiSystem + ", isHuaweiAvailable " + isHuaweiAvailable);
        if (isHuaweiSystem && isHuaweiAvailable) {
            Log.i(TAG, "HuaweiProvider");
            return HuaweiProvider.class;
        } else {
            boolean isXiaomi = isSystem(prop, KEY_MIUI_VERSION);
            if (isXiaomi && XiaomiProvider.available(context)) {
                Log.i(TAG, "XiaomiProvider");
                return XiaomiProvider.class;
            } else if (UmengProvider.available(context)) {
                return UmengProvider.class;
            } else {
                Log.i(TAG, "No provider");
                return null;
            }
        }
    }

    private static boolean isSystem(SystemProperty prop, String key) {
        String value = prop.get(key);
        boolean b = value != null && !value.isEmpty();
        Log.d(TAG, key + " " + value + " " + b);
        return b;
    }

}
