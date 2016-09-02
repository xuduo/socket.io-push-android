package com.yy.misaka.demo.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.yy.httpproxy.Config;
import com.yy.httpproxy.ProxyClient;
import com.yy.httpproxy.service.DefaultNotificationHandler;
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.misaka.demo.R;
import com.yy.misaka.demo.util.JsonSerializer;

import java.util.Set;

/**
 * Created by Administrator on 2016/7/19.
 */
public class TestActivity extends Activity implements ConnectCallback, PushCallback{

    private static final String TAG = "TestActivity";
    public static String host = "https://spush.yy.com";
    public static final String topic = "testTopic";
    public ProxyClient proxyClient;
    private TextView tv_socketState,tv_pushId;
    public String pushId;

    private TestConnectCallBack connectCallBack;
    private TestPushCallBack pushCallBack;

    public void setPushCallBack(TestPushCallBack pushCallBack) {
        this.pushCallBack = pushCallBack;
    }

    public void setConnectCallBack(TestConnectCallBack connectCallBack) {
        this.connectCallBack = connectCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        proxyClient = new ProxyClient(new Config(this).setHost(host)
                .setConnectCallback(this)
                .setPushCallback(this)
                .setNotificationHandler(DefaultNotificationHandler.class)
                .setRequestSerializer(new JsonSerializer()));
        proxyClient.subscribeBroadcast(topic);
        pushId = proxyClient.getPushId();
        tv_pushId.setText("pushId: " + pushId);
        proxyClient.subscribeBroadcast(topic);
    }

    public void initView() {
        tv_socketState = (TextView)findViewById(R.id.tv_state);
        tv_pushId = (TextView)findViewById(R.id.tv_pushId);
    }

    @Override
    public void onConnect(String uid, Set<String> topics) {
        Log.i(TAG, "socket Connect and uid is " + uid + topics.toString());
        tv_socketState.setText("connected");
        tv_socketState.setTextColor(Color.GREEN);
        if(connectCallBack != null) {
            connectCallBack.isConnect(true);
        }
    }

    @Override
    public void onDisconnect() {
        Log.i(TAG, "socket disConnect");
        tv_socketState.setText("disconnected");
        tv_socketState.setTextColor(Color.RED);
        if(connectCallBack != null) {
            connectCallBack.isConnect(false);
        }
    }

    @Override
    public void onPush(String data) {
        Log.i(TAG, data);
        pushCallBack.onPush(data);
    }


    public interface TestConnectCallBack {
        void isConnect(boolean state);
    }

    public interface TestPushCallBack {
        void onPush(String data);
    }

}
