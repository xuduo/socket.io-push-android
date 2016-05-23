package com.yy.httpproxy.thirdparty;

import android.content.Context;
import com.yy.httpproxy.util.Log;

import com.huawei.android.pushagent.api.PushManager;
import com.yy.httpproxy.util.ServiceCheckUtil;

/**
 * Created by Administrator on 2016/4/29.
 */
public class HuaweiProvider implements NotificationProvider {

    public final static String TAG = "HuaweiProvider";
    private String token;

    public HuaweiProvider(Context context) {
        Log.i("HuaweiProvider", "init");
        PushManager.requestToken(context);
    }

    public static boolean available(Context context) {
        try {
            return Class.forName("com.huawei.android.pushagent.api.PushManager") != null
                    && Class.forName("com.yy.httpproxy.thirdparty.HuaweiReceiver") != null
                    && ServiceCheckUtil.isServiceAvailable(context, HuaweiReceiver.class);
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
