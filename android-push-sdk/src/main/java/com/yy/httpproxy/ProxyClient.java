package com.yy.httpproxy;


import android.os.Handler;
import android.os.Looper;

import com.yy.httpproxy.requester.HttpCallback;
import com.yy.httpproxy.requester.HttpRequest;
import com.yy.httpproxy.requester.HttpResponse;
import com.yy.httpproxy.requester.RequestInfo;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.httpproxy.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProxyClient implements PushCallback {

    private Config config;
    public static final String TAG = "ProxyClient";
    private long mainThreadId = Looper.getMainLooper().getThread().getId();
    private Handler handler = new Handler(Looper.getMainLooper());
    private Map<String, HttpCallback> replayHandlers = new HashMap<>();

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

    public void request(String path, Object body) {
        final RequestInfo requestInfo = new RequestInfo();
        requestInfo.setBody(config.getRequestSerializer().toBinary(path, body));
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

    @Override
    public void onPush(final String data) {
        if (config.getPushCallback() != null) {
            if (Thread.currentThread().getId() == mainThreadId) {
                config.getPushCallback().onPush(data);
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        config.getPushCallback().onPush(data);
                    }
                });
            }
        }
    }

    private void callSuccessOnMainThread(final HttpCallback replyHandler, final HttpResponse response) {
        if (Thread.currentThread().getId() == mainThreadId) {
            replyHandler.onResult(response);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    replyHandler.onResult(response);
                }
            });
        }
    }

    public String getPushId() {
        return getConfig().getPushId();
    }

    public void onResponse(HttpResponse response) {
        HttpCallback replyHandler = replayHandlers.remove(response.getSequenceId());
        if (replyHandler != null) {
            callSuccessOnMainThread(replyHandler, response);
        }
    }

    public Config getConfig() {
        return config;
    }

    public void http(HttpRequest request, HttpCallback httpCallback) {
        replayHandlers.put(request.getSequenceId(), httpCallback);
        config.getRemoteClient().
                http(request);
    }
}
