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
public class HuaweiCallback implements HuaweiApiClient.ConnectionCallbacks, HuaweiApiClient.OnConnectionFailedListener {

    public final static String TAG = "HuaweiProvider";
    private HuaweiApiClient client;

    public HuaweiCallback(Context context) {
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
