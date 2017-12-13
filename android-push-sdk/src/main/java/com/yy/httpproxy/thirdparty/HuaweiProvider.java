package com.yy.httpproxy.thirdparty;

import android.content.Context;

import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.util.Log;

import com.yy.httpproxy.util.ServiceCheckUtil;

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

    private void getTokenAsyn() {

        if(!client.isConnected()) {

            Log.i(TAG, "获取token失败，原因：HuaweiApiClient未连接");

            client.connect();

            return;

        }



        Log.i(TAG, "异步接口获取push token");

        PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(client);

        tokenResult.setResultCallback(new ResultCallback<TokenResult>() {

            @Override

            public void onResult(TokenResult result) {
                ConnectionService.setToken(result.getTokenRes().getToken());
            }

        });

    }

    public static boolean available(Context context) {
        try {
            return Class.forName("com.huawei.android.pushagent.api.PushManager") != null
                    && Class.forName("com.yy.httpproxy.thirdparty.HuaweiReceiver") != null
                    && ServiceCheckUtil.isBroadcastReceiverAvailable(context, HuaweiReceiver.class);
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

    @Override
    public void onConnected() {
        getTokenAsyn();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
