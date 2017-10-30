package com.yy.httpproxy.thirdparty;

import android.content.Context;

import com.meizu.cloud.pushsdk.PushManager;
import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.ServiceCheckUtil;

/**
 * Created by Administrator on 2016/4/29.
 */
public class MeizuProvider implements NotificationProvider {

    public final static String TAG = "HuaweiProvider";
    private String token;

    public MeizuProvider(Context context) {

        String appId = ServiceCheckUtil.getMetaDataValue(context, "MEIZU_APP_ID");
        String appKey = ServiceCheckUtil.getMetaDataValue(context, "MEIZU_APP_KEY");
        PushManager.register(context, appId, appKey);
        Log.i("MeizuProvider", "init");
    }

    public static boolean available(Context context) {
        try {
            return Class.forName("com.meizu.cloud.pushsdk.PushManager") != null
                    && ServiceCheckUtil.isBroadcastReceiverAvailable(context, MyMzPushMessageReceiver.class)
                    && ServiceCheckUtil.getMetaDataValue(context, "MEIZU_APP_ID") != null
                    && ServiceCheckUtil.getMetaDataValue(context, "MEIZU_APP_KEY") != null;
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
        return "huawei";
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }
}
