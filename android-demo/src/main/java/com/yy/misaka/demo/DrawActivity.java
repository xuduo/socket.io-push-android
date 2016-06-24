package com.yy.misaka.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.yy.httpproxy.Config;
import com.yy.httpproxy.ProxyClient;
import com.yy.httpproxy.ReplyHandler;
import com.yy.misaka.demo.util.JsonSerializer;
import com.yy.httpproxy.subscribe.ConnectCallback;

import java.util.Random;


public class DrawActivity extends Activity implements ConnectCallback {

    private static final String TAG = "DrawActivity";
    private DrawView drawView;
    private ProxyClient proxyClient;
    private TextView latency;
    private TextView count;
    private TextView connect;
    private long totalTime;
    private long totalCount;
    public int myColors[] = {Color.BLACK, Color.DKGRAY, Color.CYAN, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA};
    public int myColor;

    private void updateLatency(long timestamp) {
        totalTime += System.currentTimeMillis() - timestamp;
        totalCount++;
        latency.setText((totalTime / totalCount) + "ms");
    }

    private void update(long timestamp, int num) {
        totalTime += System.currentTimeMillis() - timestamp;
        latency.setText((totalTime / num) + "ms");
        count.setText("" + num);
    }

    private void resetLatency() {
        totalCount = 0;
        totalTime = 0;
        latency.setText("0ms");
        count.setText("0dots");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DemoLogger", "DrawActivity onCreate");
        setContentView(R.layout.activity_draw);
        latency = (TextView) findViewById(R.id.tv_latency);
        count = (TextView) findViewById(R.id.tv_count);

        updateConnect();
    }

    private void updateConnect() {
        connect.setText(proxyClient.isConnected() ? "connected" : "disconnected");
    }

    @Override
    public void onConnect(String uid) {
        updateConnect();
    }

    @Override
    public void onDisconnect() {
        updateConnect();
    }
}