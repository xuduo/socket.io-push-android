package com.yy.httpproxy.socketio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;

import com.yy.httpproxy.ProxyClient;
import com.yy.httpproxy.requester.RequestInfo;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.service.DummyService;
import com.yy.httpproxy.subscribe.PushSubscriber;
import com.yy.httpproxy.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RemoteClient implements PushSubscriber {

    private static final String TAG = "RemoteClient";
    public static final int CMD_SUBSCRIBE_BROADCAST = 1;
    public static final int CMD_REQUEST = 3;
    public static final int CMD_REGISTER_CLIENT = 4;
    public static final int CMD_UNSUBSCRIBE_BROADCAST = 5;
    public static final int CMD_STATS = 6;
    public static final int CMD_UNBIND_UID = 7;
    public static final int CMD_SET_TOKEN = 8;
    public static final int CMD_BIND_UID = 13;
    public static final int CMD_NOTIFICATION_CLICK = 14;
    public static final int CMD_SET_TAG = 15;
    private Map<String, Boolean> topics = new HashMap<>();
    private ProxyClient proxyClient;
    private Messenger mService;
    private boolean mBound;
    private Messenger messenger = null;
    private Context context;
    private boolean connected = false;
    private static RemoteClient instance;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String host;
    private String pushId;
    private String notificationHandler;
    private String dnsHandler;
    private String logger;

    public RemoteClient(Context context, String host, String pushId, String notificationHandler, String logger, String dnsHandler) {
        this.context = context;
        this.host = host;
        this.pushId = pushId;
        this.notificationHandler = notificationHandler;
        this.logger = logger;
        this.dnsHandler = dnsHandler;
        startServices();
    }

    public void unsubscribeBroadcast(final String topic) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain(null, CMD_UNSUBSCRIBE_BROADCAST, 0, 0);
                Bundle bundle = new Bundle();
                bundle.putSerializable("topic", topic);
                msg.setData(bundle);
                sendMsg(msg);
                topics.remove(topic);
            }
        });
    }

    public boolean isConnected() {
        return connected;
    }

    public void reportStats(String path, int successCount, int errorCount, int latency) {
        Message msg = Message.obtain(null, CMD_STATS, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("path", path);
        bundle.putInt("successCount", successCount);
        bundle.putInt("errorCount", errorCount);
        bundle.putInt("latency", latency);
        msg.setData(bundle);
        sendMsg(msg);
    }

    public void exit() {
        context.stopService(new Intent(context, ConnectionService.class));
        context.unbindService(mConnection);
    }

    public void bindUid(HashMap<String, String> data) {
        Message msg = Message.obtain(null, CMD_BIND_UID, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);
        msg.setData(bundle);
        sendMsg(msg);
    }

    public static void sendNotificationClick(String id) {
        if (instance != null) {
            Message msg = Message.obtain(null, CMD_NOTIFICATION_CLICK, 0, 0);
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            msg.setData(bundle);
            instance.sendMsg(msg);
        }
    }

    public void setTag(Set<String> tags) {
        Message msg = Message.obtain(null, CMD_SET_TAG, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("tags", new ArrayList(tags));
        msg.setData(bundle);
        sendMsg(msg);
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int cmd = msg.what;
            Bundle bundle = msg.getData();
            if (cmd == ConnectionService.CMD_PUSH) {
                String data = bundle.getString("data");
                Log.d(TAG, "push data: " + data);
                proxyClient.onPush(data);
            } else if (cmd == ConnectionService.CMD_CONNECTED && connected == false) {
                connected = true;
                if (proxyClient.getConfig().getConnectCallback() != null) {
                    String uid = null;
                    if (bundle != null) {
                        uid = bundle.getString("uid", "");
                    }
                    proxyClient.getConfig().getConnectCallback().onConnect(uid);
                }
            } else if (cmd == ConnectionService.CMD_DISCONNECT && connected == true) {
                connected = false;
                if (proxyClient.getConfig().getConnectCallback() != null) {
                    proxyClient.getConfig().getConnectCallback().onDisconnect();
                }
            }
        }
    }

    private void resubscribeTopics() {
        for (Map.Entry<String, Boolean> topic : topics.entrySet()) {
            doSubscribe(topic.getKey(), topic.getValue());
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            Log.i(TAG, "onServiceConnected");
            mService = new Messenger(service);
            mBound = true;
            Message msg = Message.obtain(null, CMD_REGISTER_CLIENT, 0, 0);
            msg.replyTo = messenger;
            sendMsg(msg);
            instance = RemoteClient.this;
            resubscribeTopics();
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
            Log.i(TAG, "onServiceDisconnected");
            startServices();
        }
    };

    private void startServices() {
        handler.post(startServiceRunnable);
    }

    private Runnable startServiceRunnable = new Runnable() {
        @Override
        public void run() {
            if (messenger == null) {
                messenger = new Messenger(new IncomingHandler());
            }
            if (!mBound) {
                try {
                    startRemoteService();
                    startDummyService();
                } catch (Exception e) {
                    Log.e(TAG, "start service exception, will try restart", e);
                }
                handler.postDelayed(startServiceRunnable, 5000L);
            }
        }
    };

    private void startDummyService() {
        Intent intent = new Intent(context, DummyService.class);
        context.startService(intent);
    }

    private void startRemoteService() {
        Intent intent = new Intent(context, ConnectionService.class);
        intent.putExtra("host", host);
        intent.putExtra("pushId", pushId);
        intent.putExtra("logger", logger);
        if (notificationHandler != null) {
            intent.putExtra("notificationHandler", notificationHandler);
        }
        if (dnsHandler != null) {
            intent.putExtra("dnsHandler", dnsHandler);
        }
        context.startService(intent);
        Intent bindIntent = new Intent(context, ConnectionService.class);
        context.bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendMsg(Message msg) {
        try {
            if (mBound) {
                mService.send(msg);
            }
        } catch (Exception e) {
            Log.e(TAG, "sendMsg error!", e);
        }
    }

    private void runInMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            handler.post(runnable);
        }
    }

    public void request(RequestInfo requestInfo) {
        Message msg = Message.obtain(null, CMD_REQUEST, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putSerializable("requestInfo", requestInfo);
        msg.setData(bundle);
        sendMsg(msg);
    }

    public void unbindUid() {
        Message msg = Message.obtain(null, CMD_UNBIND_UID, 0, 0);
        sendMsg(msg);
    }


    @Override
    public void subscribeBroadcast(final String topic, final boolean receiveTtlPackets) {
        runInMainThread(new Runnable() {
            @Override
            public void run() {
                topics.put(topic, receiveTtlPackets);
                doSubscribe(topic, receiveTtlPackets);
            }
        });
    }

    private void doSubscribe(String topic, boolean receiveTtlPackets) {
        Message msg = Message.obtain(null, CMD_SUBSCRIBE_BROADCAST, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("topic", topic);
        bundle.putBoolean("receiveTtlPackets", receiveTtlPackets);
        msg.setData(bundle);
        sendMsg(msg);
    }

    public void setProxyClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    public static void setToken(String token) {
        if (instance != null) {
            Message msg = Message.obtain(null, CMD_SET_TOKEN, 0, 0);
            Bundle bundle = new Bundle();
            bundle.putString("token", token);
            msg.setData(bundle);
            instance.sendMsg(msg);
        }
    }

}
