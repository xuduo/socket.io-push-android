package com.yy.httpproxy.thirdparty;

import android.content.Context;

import com.yy.httpproxy.util.Log;

import com.yy.httpproxy.util.SystemProperty;

public class ProviderFactory {

    private static final String KEY_HUAWEI_VERSION = "ro.build.version.emui";
    private static final String KEY_MIUI_VERSION = "ro.miui.ui.version.name";
    private static final String TAG = "ProviderFactory";
    private static final String HUAWEI_BUG_NAME = "NXT-AL10";
    private static final String HUAWEI_BUG_VERSION = "EmotionUI_4.1";

    public static NotificationProvider getProvider(Context context) {
        final SystemProperty prop = new SystemProperty(context);
        boolean isHuaweiSystem = isSystem(prop, KEY_HUAWEI_VERSION);
        boolean isHuaweiAvailable = HuaweiProvider.available(context);
        boolean huaweiBug = huaweiBug(prop);
        Log.i(TAG, "isHuaweiSystem " + isHuaweiSystem + ", isHuaweiAvailable " + isHuaweiAvailable + ", huaweiBug " + huaweiBug);
        if (isHuaweiSystem && isHuaweiAvailable && !huaweiBug) {
            Log.i(TAG, "HuaweiProvider");
            return new HuaweiProvider(context);
        } else {
            boolean isXiaomi = isSystem(prop, KEY_MIUI_VERSION);
            if (isXiaomi && XiaomiProvider.available(context)) {
                Log.i(TAG, "XiaomiProvider");
                return new XiaomiProvider(context);
            } else if (UmengProvider.available(context)) {
                return new UmengProvider(context);
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

    private static boolean huaweiBug(SystemProperty prop) {
        String productName = prop.get("ro.product.name");
        String emuiVersion = prop.get(KEY_HUAWEI_VERSION);
        Log.d(TAG, "huawei productName " + productName + " emuiVersion " + emuiVersion);
        return HUAWEI_BUG_VERSION.equals(emuiVersion) && HUAWEI_BUG_NAME.equals(productName);
    }

}
