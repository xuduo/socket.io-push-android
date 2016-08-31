package com.yy.misaka.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.misaka.demo.appmodel.DemoApp;

import java.util.Random;
import java.util.Set;


public class DrawActivity extends Activity implements ConnectCallback, PushCallback {

    private static final String TAG = "DrawActivity";
    private static String drawTopic = "drawTopic";
    private static String endlineTopic = "endlineTopic";
    private DrawView drawView;
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
        count.setText(totalCount + "dots");

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
        connect = (TextView) findViewById(R.id.tv_connect);
        drawView = (DrawView) findViewById(R.id.draw_view);
        DemoApp.APP_CONTEXT.proxyClient.getConfig().setConnectCallback(this);
        DemoApp.APP_CONTEXT.proxyClient.getConfig().setPushCallback(this);
        DemoApp.APP_CONTEXT.proxyClient.subscribeBroadcast(drawTopic);
        DemoApp.APP_CONTEXT.proxyClient.subscribeBroadcast(endlineTopic);
        updateConnect();

        Random random = new Random();
        myColor = myColors[random.nextInt(myColors.length)];

        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                DrawView.Dot dot = new DrawView.Dot();
                dot.xPercent = event.getX() / view.getWidth();
                dot.yPercent = event.getY() / view.getHeight();
                dot.myColor = myColor;
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    DemoApp.APP_CONTEXT.httpApi.sendMessage(dot, drawTopic);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    dot.endline = true;
                    DemoApp.APP_CONTEXT.httpApi.sendMessage(dot, drawTopic);
                    return true;
                }else {
                    return false;
                }
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clear();
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
        DrawView.Dot dot = new Gson().fromJson(data, DrawView.Dot.class);
        drawView.addDot(dot);
        updateLatency(dot.timestamp);
        if(dot.endline){
            drawView.endLine();
        }
    }
}