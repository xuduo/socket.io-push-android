package com.yy.misaka.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.misaka.demo.appmodel.DemoApp;
import com.yy.misaka.demo.appmodel.HttpApi;

import java.util.Random;
import java.util.Set;


public class DrawActivity extends Activity implements ConnectCallback, PushCallback {

    private static final String TAG = "DrawActivity";
    private static String drawTopic = "drawTopic";
    private DrawView drawView;
    private TextView latency;
    private TextView apiLatency;
    private TextView count;
    private TextView connect;
    private long totalTime;
    private long totalCount;
    private long totalApiTime;
    private long totalApiCount;
    public int myColors[] = {Color.BLACK, Color.DKGRAY, Color.CYAN, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA};
    public int myColor;
    private Handler handler = new Handler();
    private HttpApi.CB cb = new HttpApi.CB() {
        @Override
        public void latency(final long latency) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    updateApiLatency(latency);
                }
            });
        }
    };

    private void updateLatency(long timestamp) {
        totalTime += System.currentTimeMillis() - timestamp;
        totalCount++;
        latency.setText("all:" + (totalTime / totalCount) + "ms");
        count.setText(totalCount + "dots");
    }


    private void updateApiLatency(long latency) {
        totalApiTime += latency;
        totalApiCount++;
        apiLatency.setText("api:" + (totalApiTime / totalApiCount) + "ms");
    }

    private void resetLatency() {
        totalCount = 0;
        totalTime = 0;
        totalApiCount = 0;
        totalApiTime = 0;
        latency.setText("0ms");
        apiLatency.setText("0ms");
        count.setText("0dots");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DemoLogger", "DrawActivity onCreate");
        setContentView(R.layout.activity_draw);
        latency = (TextView) findViewById(R.id.tv_latency);
        apiLatency = (TextView) findViewById(R.id.tv_api_latency);
        count = (TextView) findViewById(R.id.tv_count);
        connect = (TextView) findViewById(R.id.tv_connect);
        drawView = (DrawView) findViewById(R.id.draw_view);
        DemoApp.APP_CONTEXT.proxyClient.getConfig().setConnectCallback(this);
        DemoApp.APP_CONTEXT.proxyClient.getConfig().setPushCallback(this);
        DemoApp.APP_CONTEXT.proxyClient.subscribeBroadcast(drawTopic);
        updateConnect();

        Random random = new Random();
        myColor = myColors[random.nextInt(myColors.length)];

        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                DrawView.Dot dot = new DrawView.Dot();
                dot.xPercent = event.getX() / view.getWidth();
                dot.yPercent = event.getY() / view.getHeight();
                dot.setIntColor(myColor);
                Log.d(TAG, "onTouch " + dot);
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    DemoApp.APP_CONTEXT.httpApi.sendMessage(dot, drawTopic, cb);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    dot.endline = true;
                    DemoApp.APP_CONTEXT.httpApi.sendMessage(dot, drawTopic, cb);
                    return true;
                } else {
                    return false;
                }
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoApp.APP_CONTEXT.httpApi.sendMessage(new DrawView.Dot(), drawTopic, null);
                resetLatency();
            }
        });
    }


    private void updateConnect() {
        connect.setText(DemoApp.APP_CONTEXT.proxyClient.isConnected() ? "connected" : "disconnected");
    }

    @Override
    public void onConnect(String uid, Set<String> tags) {
        updateConnect();
    }

    @Override
    public void onDisconnect() {
        updateConnect();
    }

    @Override
    public void onPush(String data) {
        Log.d(TAG, "push " + data);
        DrawView.Dot dot = new Gson().fromJson(data, DrawView.Dot.class);
        if (dot.xPercent == 0 && dot.yPercent == 0) {
            drawView.clear();
            return;
        }
        drawView.addDot(dot);
        updateLatency(dot.timestamp);
    }
}