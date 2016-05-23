package com.yy.httpproxy;

import android.content.Context;

import com.yy.httpproxy.serializer.RequestSerializer;
import com.yy.httpproxy.socketio.RemoteClient;
import com.yy.httpproxy.subscribe.CachedSharedPreference;
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.httpproxy.subscribe.PushIdGenerator;
import com.yy.httpproxy.subscribe.RandomPushIdGenerator;
import com.yy.httpproxy.util.Logger;

/**
 * Created by xuduo on 10/19/15.
 */
public class Config {

    private RemoteClient remoteClient;
    private Context context;
    private RequestSerializer requestSerializer;
    private PushCallback pushCallback;
    private ConnectCallback connectCallback;
    private String host;
    private String pushId;
    private String notificationHandler;
    private String logger;

    public Config(Context context) {
        this.context = context;
        this.pushId = new RandomPushIdGenerator().generatePushId(context);
    }

    public RemoteClient getRemoteClient() {
        if (remoteClient == null) {
            remoteClient = new RemoteClient(context, host, pushId, notificationHandler, logger);
        }
        return remoteClient;
    }

    public Config setHost(String host) {
        this.host = host;
        return this;
    }

    public RequestSerializer getRequestSerializer() {
        return requestSerializer;
    }

    public Config setRequestSerializer(RequestSerializer requestSerializer) {
        this.requestSerializer = requestSerializer;
        return this;
    }

    public Config setPushCallback(PushCallback pushCallback) {
        this.pushCallback = pushCallback;
        return this;
    }

    public PushCallback getPushCallback() {
        return pushCallback;
    }

    public Config setLogger(Class<? extends Logger> logger) {
        this.logger = logger.getName();
        return this;
    }

    public Config setNotificationHandler(Class notificationHandler) {
        this.notificationHandler = notificationHandler.getName();
        return this;
    }

    public ConnectCallback getConnectCallback() {
        return connectCallback;
    }

    public Config setConnectCallback(ConnectCallback connectCallback) {
        this.connectCallback = connectCallback;
        return this;
    }

    public String getPushId() {
        return pushId;
    }
}
