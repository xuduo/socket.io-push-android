package com.yy.misaka.demo.appmodel;

import android.app.Application;
import android.util.Log;

import com.yy.httpproxy.ProxyClient;
import com.yy.misaka.demo.ChatActivity;


/**
 * Created by xuduo on 6/13/16.
 */
public class DemoApp extends Application {

    public static DemoApp APP_CONTEXT;
    public ProxyClient proxyClient;
    public HttpApi httpApi;

    @Override
    public void onCreate() {
        Log.i("DemoLogger", "DemoApp onCreate");
        super.onCreate();
        APP_CONTEXT = this;
        httpApi = new HttpApi(ChatActivity.API_URL);
    }

}
