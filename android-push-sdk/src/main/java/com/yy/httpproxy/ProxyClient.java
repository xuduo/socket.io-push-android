package com.yy.httpproxy;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.yy.httpproxy.requester.RequestInfo;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.httpproxy.thirdparty.ProviderFactory;
import com.yy.httpproxy.thirdparty.UmengProvider;
import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.ServiceCheckUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

public class ProxyClient implements PushCallback {

    private Config config;
    public static final String TAG = "ProxyClient";
    private long mainThreadId;
    private Handler handler;
    public static long uptime;

    public ProxyClient(final Config config) {
        uptime = SystemClock.elapsedRealtime();
        String packageName = config.getContext().getPackageName();
        String processName = getProcessName(config.getContext());
        if (!packageName.equals(processName)) {
            String pushProcess = ServiceCheckUtil.getPushProcessName(config.getContext());
            Log.i(TAG, "not in main process, skip init , start service ");
            if (pushProcess.equals(processName)) {
                Log.i(TAG, "in push process, registerUmeng");
                registerUmeng(config.getContext());
            }
            return;
        }
        Log.i(TAG, "init " + config);
        this.config = config;
        handler = new Handler(Looper.getMainLooper());
        mainThreadId = Looper.getMainLooper().getThread().getId();

        if (config.getRemoteClient() != null) {
            config.getRemoteClient().setProxyClient(this);
        }

        registerUmeng(config.getContext());

    }

    private void registerUmeng(Context context) {
        if (UmengProvider.class.equals(ProviderFactory.checkProvider(context))) {
            UmengProvider.register(context);
        }
    }

    public boolean isConnected() {
        return config.getRemoteClient().isConnected();
    }

    public void setTags(Set<String> tags) {
        config.getRemoteClient().
                setTag(tags);
    }

    public void request(String path, byte[] data) {
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

    public void bindUid(HashMap<String, String> data) {
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

    public String getProcessName(Context context) {
        BufferedReader cmdlineReader = null;
        try {
            cmdlineReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(
                            "/proc/" + android.os.Process.myPid() + "/cmdline"),
                    "iso-8859-1"));
            int c;
            StringBuilder processName = new StringBuilder();
            while ((c = cmdlineReader.read()) > 0) {
                processName.append((char) c);
            }
            Log.d(TAG, "/proc/ file name " + processName.toString());
            return processName.toString();
        } catch (Exception e) {
            Log.e(TAG, "read /proc/ error ", e);
            int pid = android.os.Process.myPid();
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
                if (processInfo.pid == pid) {
                    Log.d(TAG, "processInfo.processName file name " + processInfo.processName);
                    return processInfo.processName;
                }
            }
        } finally {
            if (cmdlineReader != null) {
                try {
                    cmdlineReader.close();
                } catch (IOException e) {

                }
            }
        }
        return context.getPackageName();
    }

    public String getPushId() {
        return getConfig().getPushId();
    }

    public Config getConfig() {
        return config;
    }
}
