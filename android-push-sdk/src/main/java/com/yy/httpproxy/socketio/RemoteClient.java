package com.yy.httpproxy.socketio;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import com.yy.httpproxy.ProxyClient;
import com.yy.httpproxy.requester.HttpRequest;
import com.yy.httpproxy.requester.HttpRequester;
import com.yy.httpproxy.requester.HttpResponse;
import com.yy.httpproxy.requester.RequestInfo;
import com.yy.httpproxy.service.BindService;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.service.DummyService;
import com.yy.httpproxy.service.PushedNotification;
import com.yy.httpproxy.subscribe.PushSubscriber;
import com.yy.httpproxy.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class RemoteClient implements PushSubscriber, HttpRequester {

    private static final String TAG = "RemoteClient";
    public static final int CMD_SUBSCRIBE_BROADCAST = 1;
    public static final int CMD_SET_PUSH_ID = 2;
    public static final int CMD_REQUEST = 3;
    public static final int CMD_REGISTER_CLIENT = 4;
    public static final int CMD_UNSUBSCRIBE_BROADCAST = 5;
    public static final int CMD_STATS = 6;
    public static final int CMD_UNBIND_UID = 7;
    public static final int CMD_SET_TOKEN = 8;
    public static final int CMD_THIRD_PARTY_ON_NOTIFICATION = 9;
    public static final int CMD_HTTP = 10;
    public static final int CMD_ADD_TAG = 11;
    public static final int CMD_REMOVE_TAG = 12;
    private Map<String, Boolean> topics = new HashMap<>();
    private ProxyClient proxyClient;
    private Messenger mService;
    private boolean mBound;
    private final Messenger messenger = new Messenger(new IncomingHandler());
    private Context context;
    private boolean connected = false;
    private static RemoteClient instance;
    private Map<String, HttpRequest> replyCallbacks = new ConcurrentHashMap<>();
    private long timeout = 10000;
    private Handler handler;

    private Runnable timeoutTask = new Runnable() {
        @Override
        public void run() {
            Iterator<Map.Entry<String, HttpRequest>> it = replyCallbacks.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, HttpRequest> pair = it.next();
                HttpRequest request = pair.getValue();
                if (request.timeoutForRequest(timeout)) {
                    HttpResponse response = new HttpResponse();
                    response.setSequenceId(request.getSequenceId());
                    response.setHeaders(new HashMap<String, String>());
                    response.setBody("request timeout after " + timeout);
                    Log.i(TAG, "request timeout " + request.getUrl());
                    proxyClient.onResponse(response);
                    it.remove();
                    continue;
                }
            }
            postTimeout();
        }
    };

    private void postTimeout() {
        handler.removeCallbacks(timeoutTask);
        if (replyCallbacks.size() > 0) {
            handler.postDelayed(timeoutTask, 1000);
        }
    }

    public void unsubscribeBroadcast(String topic) {
        Message msg = Message.obtain(null, CMD_UNSUBSCRIBE_BROADCAST, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putSerializable("topic", topic);
        msg.setData(bundle);
        sendMsg(msg);
        topics.remove(topic);
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
        context.stopService(new Intent(context, BindService.class));
    }


    private void reSendFailedRequest() {
        if (!replyCallbacks.isEmpty()) {
            List<HttpRequest> values = new ArrayList<>(replyCallbacks.values());
            replyCallbacks.clear();
            for (HttpRequest request : values) {
                Log.i(TAG, "onConnected repost request " + request.getUrl());
                http(request);
            }
        }
    }

    public void http(HttpRequest request) {
        request.setTimestamp();
        replyCallbacks.put(request.getSequenceId(), request);
        postTimeout();
        Message msg = Message.obtain(null, CMD_HTTP, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putSerializable("request", request);
        msg.setData(bundle);
        sendMsg(msg);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void addTag(String tag) {
        Message msg = Message.obtain(null, CMD_ADD_TAG, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        msg.setData(bundle);
        sendMsg(msg);
    }

    public void removeTag(String tag) {
        Message msg = Message.obtain(null, CMD_REMOVE_TAG, 0, 0);
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        msg.setData(bundle);
        sendMsg(msg);
    }

    private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int cmd = msg.what;
            Bundle bundle = msg.getData();
            if (cmd == BindService.CMD_PUSH) {
                String data = bundle.getString("data");
                proxyClient.onPush(data);
            } else if (cmd == BindService.CMD_CONNECTED && connected == false) {
                connected = true;
                reSendFailedRequest();
                if (proxyClient.getConfig().getConnectCallback() != null) {
                    String uid = null;
                    Set<String> tags = new HashSet<>();
                    if (bundle != null) {
                        uid = bundle.getString("uid", "");
                        String[] array = bundle.getStringArray("tags");
                        if (array != null) {
                            for (int i = 0; i < array.length; i++) {
                                tags.add(array[i]);
                            }
                        }
                    }
                    proxyClient.getConfig().getConnectCallback().onConnect(uid, tags);
                }
            } else if (cmd == BindService.CMD_DISCONNECT && connected == true) {
                connected = false;
                if (proxyClient.getConfig().getConnectCallback() != null) {
                    proxyClient.getConfig().getConnectCallback().onDisconnect();
                }
            } else if (cmd == BindService.CMD_HTTP_RESPONSE) {
                HttpResponse response = HttpResponse.fromBundle(bundle);
                replyCallbacks.remove(response.getSequenceId());
                proxyClient.onResponse(response);
            }
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

            for (Map.Entry<String, Boolean> topic : topics.entrySet()) {
                doSubscribe(topic.getKey(), topic.getValue());
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mService = null;
            mBound = false;
            Log.i(TAG, "onServiceDisconnected");
            context.unbindService(this);
        }
    };

    public RemoteClient(Context context, String host, String pushId, String notificationHandler, String logger) {
        this.context = context;
        startRemoteService(context, host, pushId, notificationHandler, logger);
        startDummyService(context);
    }

    private void startDummyService(Context context) {
        Intent intent = new Intent(context, DummyService.class);
        context.startService(intent);
    }

    private void startRemoteService(Context context, String host, String pushId, String notificationHandler, String logger) {
        Intent intent = new Intent(context, ConnectionService.class);
        intent.putExtra("host", host);
        intent.putExtra("pushId", pushId);
        intent.putExtra("logger", logger);
        if (notificationHandler != null) {
            intent.putExtra("notificationHandler", notificationHandler);
        }
        context.startService(intent);
        Intent bindIntent = new Intent(context, BindService.class);
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
    public void subscribeBroadcast(String topic, boolean receiveTtlPackets) {
        topics.put(topic, receiveTtlPackets);
        doSubscribe(topic, receiveTtlPackets);
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

    public static void publishNotification(PushedNotification pushedNotification) {
        if (instance != null) {
            Message msg = Message.obtain(null, CMD_THIRD_PARTY_ON_NOTIFICATION, 0, 0);
            Bundle bundle = new Bundle();
            bundle.putString("id", pushedNotification.id);
            bundle.putString("title", pushedNotification.title);
            bundle.putString("message", pushedNotification.message);
            bundle.putString("payload", pushedNotification.payload);
            msg.setData(bundle);
            instance.sendMsg(msg);
        }
    }

}
