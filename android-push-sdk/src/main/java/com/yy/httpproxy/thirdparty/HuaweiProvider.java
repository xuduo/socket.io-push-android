package com.yy.httpproxy.thirdparty;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.ServiceCheckUtil;
import com.yy.httpproxy.util.Version;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/4/29.
 */
public class HuaweiProvider implements NotificationProvider, HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    public final static String TAG = "HuaweiProvider";
    private String token;
    private HuaweiApiClient client;

    public HuaweiProvider(Context context) {
        Log.i("HuaweiProvider", "init");
        client = new HuaweiApiClient.Builder(context)
                .addApi(HuaweiPush.PUSH_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
    }

    private void getTokenAsync() {

        if (!client.isConnected()) {

            Log.i(TAG, "获取token失败，原因：HuaweiApiClient未连接");

            client.connect();

            return;

        }


        Log.i(TAG, "异步接口获取push token");

        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);

        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

            @Override

            public void onResult(TokenResult result) {
                Log.i(TAG, "TokenResult " + result.getTokenRes().getToken());
            }

        });

    }

    public static boolean available(Context context) {
        try {
            return Class.forName("com.huawei.hms.update.provider.UpdateProvider") != null
                    && ServiceCheckUtil.isBroadcastReceiverAvailable(context, HuaweiReceiver.class) && isHmsAvailable(context) && EMUIValid();
        } catch (Throwable e) {
            Log.e(TAG, "available ", e);
            return false;
        }
    }

    public static boolean EMUIValid() {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
            String buildVersion = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
            buildVersion = buildVersion.replaceAll("EmotionUI_", "");
            Version ver = new Version(buildVersion);
            Log.i(TAG, "EMUI " + buildVersion);
            return ver.compareTo(new Version("5.0")) >= 0; // 5.0以下emui 有各种问题
        } catch (Exception e) {
            Log.e(TAG, "getEMUI ", e);
            return false;
        }
    }

    private static boolean isHmsAvailable(Context context) {

        PackageManager pm = context.getPackageManager();

        try {

            PackageInfo pi = pm.getPackageInfo("com.huawei.hwid", 0);

            if (pi != null) {

                Log.i(TAG, "com.huawei.hwid code " + pi.versionCode + " ,version " + pi.versionName);
                return pi.versionCode >= 241300;
            }

        } catch (Exception e) {

            Log.e(TAG, "isHmsAvailable ", e);

        }
        return false;
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

    @Override
    public void onConnected() {
        getTokenAsync();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed " + connectionResult.getErrorCode());
    }
}
