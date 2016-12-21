package com.yy.httpproxy;

import android.os.Handler;
import android.os.Looper;

import com.yy.httpproxy.requester.RequestInfo;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.httpproxy.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ProxyClient implements PushCallback {
    private Config config;
    public static final String TAG = "ProxyClient";
    private long mainThreadId = Looper.getMainLooper().getThread().getId();
    private Handler handler = new Handler(Looper.getMainLooper());

    public ProxyClient(Config config) {
        this.config = config;
        if (config.getRemoteClient() != null) {
            config.getRemoteClient().setProxyClient(this);
        }
    }

    public boolean isConnected() {
        return config.getRemoteClient().isConnected();
    }

    public void addTag(String tag) {
        config.getRemoteClient().
                addTag(tag);
    }

    public void removeTag(String tag) {
        config.getRemoteClient().
                removeTag(tag);
    }

    public void request(String path, byte[]data) {
        final RequestInfo requestInfo = new RequestInfo();
        requestInfo.setBody(data);
        requestInfo.setPath(path);

        config.getRemoteClient().
                request(requestInfo);
    }

    public void reportStats(String path, int successCount, int errorCount, int latency) {
        config.getRemoteClient().
                reportStats(path, successCount, errorCount, latency);
    }

    private void subscribeBroadcast(String topic, boolean receiveTtlPackets) {
        config.getRemoteClient().subscribeBroadcast(topic, receiveTtlPackets);
    }

    public void subscribeBroadcast(String topic) {
        config.getRemoteClient().subscribeBroadcast(topic, false);
    }

    public void subscribeAndReceiveTtlPackets(String topic) {
        config.getRemoteClient().subscribeBroadcast(topic, true);
    }

    public void unsubscribeBroadcast(String topic) {
        config.getRemoteClient().unsubscribeBroadcast(topic);
    }

    public void exit() {
        config.getRemoteClient().exit();
    }

    public void unbindUid() {
        config.getRemoteClient().unbindUid();
    }

    public void bindUid(HashMap<String,String> data) {
        config.getRemoteClient().bindUid(data);
    }

    @Override
    public void onPush(final String data) {
        if (config.getPushCallback() != null) {
            if (Thread.currentThread().getId() == mainThreadId) {
                Log.d(TAG, "mainThreadId push data: " + data);
                config.getPushCallback().onPush(data);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "post push data: " + data);
                        config.getPushCallback().onPush(data);
                    }
                });
            }
        }
    }

    public String getPushId() {
        return getConfig().getPushId();
    }

    public Config getConfig() {
        return config;
    }
}
