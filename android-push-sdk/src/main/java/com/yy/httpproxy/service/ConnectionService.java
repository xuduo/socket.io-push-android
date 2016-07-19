package com.yy.httpproxy.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;

import com.yy.httpproxy.requester.HttpRequest;
import com.yy.httpproxy.requester.RequestInfo;
import com.yy.httpproxy.socketio.RemoteClient;
import com.yy.httpproxy.socketio.SocketIOProxyClient;
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.httpproxy.thirdparty.NotificationProvider;
import com.yy.httpproxy.thirdparty.ProviderFactory;
import com.yy.httpproxy.util.CrashHandler;
import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.LogcatLogger;
import com.yy.httpproxy.util.Logger;

import java.io.Serializable;
import java.util.Map;

public class ConnectionService extends Service implements PushCallback, SocketIOProxyClient.Callback {

    private static final String TAG = "ConnectionService";
    public static SocketIOProxyClient client;
    private NotificationHandler notificationHandler;
    private static NotificationProvider notificationProvider;

    public static final int CMD_PUSH = 2;
    public static final int CMD_NOTIFICATION_CLICKED = 3;
    public static final int CMD_NOTIFICATION_ARRIVED = 5;
    public static final int CMD_RESPONSE = 4;
    public static final int CMD_CONNECTED = 5;
    public static final int CMD_DISCONNECT = 6;
    public static final int CMD_HTTP_RESPONSE = 7;
    private final Messenger messenger = new Messenger(new IncomingHandler());
    private Messenger remoteClient;
    private boolean bound = false;

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int cmd = msg.what;
            Log.d(TAG, "receive msg " + cmd);
            Bundle bundle = msg.getData();
            if (cmd == RemoteClient.CMD_SUBSCRIBE_BROADCAST) {
                String topic = bundle.getString("topic");
                boolean receiveTtlPackets = bundle.getBoolean("receiveTtlPackets", false);
               client.subscribeBroadcast(topic, receiveTtlPackets);
            } else if (cmd == RemoteClient.CMD_SET_PUSH_ID) {
                client.setPushId(bundle.getString("pushId"));
            } else if (cmd == RemoteClient.CMD_REQUEST) {
                RequestInfo info = (RequestInfo) bundle.getSerializable("requestInfo");
               client.request(info);
            } else if (cmd == RemoteClient.CMD_REGISTER_CLIENT) {
                remoteClient = msg.replyTo;
                bound = true;
                sendConnect();
            } else if (cmd == RemoteClient.CMD_UNSUBSCRIBE_BROADCAST) {
                String topic = bundle.getString("topic");
                client.unsubscribeBroadcast(topic);
            } else if (cmd == RemoteClient.CMD_STATS) {
                String path = bundle.getString("path");
                int successCount = bundle.getInt("successCount");
                int errorCount = bundle.getInt("errorCount");
                int latency = bundle.getInt("latency");
                client.reportStats(path, successCount, errorCount, latency);
            } else if (cmd == RemoteClient.CMD_UNBIND_UID) {
                client.unbindUid();
            } else if (cmd == RemoteClient.CMD_SET_TOKEN) {
                String token = bundle.getString("token");
                setToken(token);
            } else if (cmd == RemoteClient.CMD_HTTP) {
                HttpRequest request = (HttpRequest) bundle.getSerializable("request");
                client.http(request);
            } else if (cmd == RemoteClient.CMD_ADD_TAG) {
                client.addTag(bundle.getString("tag"));
            } else if (cmd == RemoteClient.CMD_REMOVE_TAG) {
                client.removeTag(bundle.getString("tag"));
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "ConnectionService onCreate");
    }

    private void startForegroundService() {
        Intent intent = new Intent(this, ForegroundService.class);
        startService(intent);
    }

    private String getFromIntentOrPref(Intent intent, String name) {
        String value = null;
        if (intent != null) {
            value = intent.getStringExtra(name);
        }
        SharedPreferences pref = getSharedPreferences("RemoteService", MODE_PRIVATE);
        if (value == null) {
            value = pref.getString(name, null);
        } else {
            pref.edit().putString(name, value).commit();
        }
        return value;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String logger = getFromIntentOrPref(intent, "logger");
        initLogger(logger);
        initCrashHandler();
        String host = "null";
        if (intent != null) {
            host = intent.getStringExtra("host");
        }
        Log.d(TAG, "onStartCommand " + host);

        ForegroundService.instance = this;
        startForegroundService();

        initClient(intent);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        if (client != null) {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setPath("/androidBind");
           client.request(requestInfo);
        }
        return messenger.getBinder();
    }

    private void initCrashHandler() {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
        }
    }

    private void initLogger(String loggerClass) {
        android.util.Log.i(TAG, "initLogger " + loggerClass);
        if (Log.logger == null) {
            if (loggerClass == null) {
                Log.logger = new LogcatLogger();
            } else {
                try {
                    Log.logger = (Logger) Class.forName(loggerClass).newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void initClient(Intent intent) {
        String host = getFromIntentOrPref(intent, "host");
        if (client != null && !host.equals(client.getHost())) {
            Log.i(TAG, "host changed re create client");
            client.disconnect();
            client = null;
        }
        if (client == null) {
            String pushId = getFromIntentOrPref(intent, "pushId");
            String handlerClassName = getFromIntentOrPref(intent, "notificationHandler");
            Class handlerClass;

            if (host == null) {
                Log.e(TAG, "host is null , exit");
                stopSelf();
                return;
            }

            if (handlerClassName == null) {
                notificationHandler = new DefaultNotificationHandler();
            } else {
                try {
                    handlerClass = Class.forName(handlerClassName);
                    notificationHandler = (NotificationHandler) handlerClass.newInstance();
                } catch (Exception e) {
                    Log.e(TAG, "handlerClass error", e);
                    notificationHandler = new DefaultNotificationHandler();
                }
            }
            notificationProvider = ProviderFactory.getProvider(this.getApplicationContext());
            client = new SocketIOProxyClient(this.getApplicationContext(), host, notificationProvider);
            client.setPushId(pushId);
            client.setPushCallback(this);
            client.setSocketCallback(this);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        client.disconnect();
        client = null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        if (client != null) {
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setPath("/androidUnbind");
            client.request(requestInfo);
        }
        bound = false;
        return true;
    }

    @Override
    public void onPush(String data) {
        Log.d(TAG, "on push data:" + data);
        Message msg = Message.obtain(null, ConnectionService.CMD_PUSH, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("data", data);
        msg.setData(bundle);
        sendMsg(msg);
    }

    @Override
    public void onNotification(PushedNotification notification) {
        notificationHandler.handlerNotification(this, bound, notification);
    }

    @Override
    public void onHttp(String sequenceId, int code, Map<String, String> headers, String body) {
        Log.d(TAG, "onHttp  " + code);
        Message msg = Message.obtain(null, ConnectionService.CMD_HTTP_RESPONSE, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("sequenceId", sequenceId);
        bundle.putInt("code", code);
        bundle.putString("body", body);
        bundle.putSerializable("headers", (Serializable) headers);
        msg.setData(bundle);
        sendMsg(msg);
    }

    public void sendConnect() {
        if (client == null) {
            return;
        }
        int id;
        if (client.isConnected()) {
            id = ConnectionService.CMD_CONNECTED;
        } else {
            id = ConnectionService.CMD_DISCONNECT;
        }
        Message msg = Message.obtain(null, id, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("uid", client.getUid());
        bundle.putStringArray("tags", client.getTags());
        msg.setData(bundle);
        sendMsg(msg);
    }

    @Override
    public void onConnect() {
        sendConnect();
    }

    @Override
    public void onDisconnect() {
        sendConnect();
    }

    public static void setToken(String token) {
        if (notificationProvider != null && client != null) {
            Log.i(TAG, "setToken " + token);
            notificationProvider.setToken(token);
            client.sendTokenToServer();
        } else {
            Log.i(TAG, "setToken from main process");
            RemoteClient.setToken(token);
        }
    }

    public void sendMsg(Message msg) {
        if (bound) {
            try {
                remoteClient.send(msg);
            } catch (Exception e) {
                Log.e(TAG, "sendMsg error!", e);
            }
        } else {
            Log.d(TAG, "sendMsg not bound");
        }
    }
}
