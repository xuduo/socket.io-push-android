package com.yy.httpproxy;

import android.content.Context;
import com.yy.httpproxy.socketio.RemoteClient;
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.httpproxy.subscribe.RandomPushIdGenerator;
import com.yy.httpproxy.util.Logger;

/**
 * Created by xuduo on 10/19/15.
 */
public class Config {

    private RemoteClient remoteClient;
    private Context context;
    private PushCallback pushCallback;
    private ConnectCallback connectCallback;
    private String host;
    private String pushId;
    private String notificationHandler;
    private String dnsHandler;
    private String logger;

    public Config(Context context) {
        this.context = context;
        this.pushId = new RandomPushIdGenerator().generatePushId(context);
    }

    public RemoteClient getRemoteClient() {
        if (remoteClient == null) {
            remoteClient = new RemoteClient(context, host, pushId, notificationHandler, logger, dnsHandler);
        }
        return remoteClient;
    }

    public Config setHost(String host) {
        this.host = host;
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

    public Config setDnsHandler(Class dnsHandler) {
        this.dnsHandler = dnsHandler.getName();
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
