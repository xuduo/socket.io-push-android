package com.yy.misaka.demo.appmodel;

import android.app.Application;
import android.util.Log;

import com.yy.httpproxy.Config;
import com.yy.httpproxy.ProxyClient;
import com.yy.httpproxy.service.DefaultDnsHandler;
import com.yy.httpproxy.service.DefaultNotificationHandler;
import com.yy.misaka.demo.ChatActivity;
import com.yy.misaka.demo.ConnectActivity;


/**
 * Created by xuduo on 6/13/16.
 */
public class DemoApp extends Application {

    public static DemoApp APP_CONTEXT;
    public ProxyClient proxyClient;
    public HttpApi httpApi;
    private String host = "https://spush.yy.com";

    @Override
    public void onCreate() {
        Log.i("DemoLogger", "DemoApp onCreate");
        super.onCreate();
        APP_CONTEXT = this;
        httpApi = new HttpApi(ChatActivity.API_URL);
        DemoApp.APP_CONTEXT.proxyClient = new ProxyClient(new Config(DemoApp.APP_CONTEXT).setHost(host)
                .setNotificationHandler(DefaultNotificationHandler.class)
                .setDnsHandler(DefaultDnsHandler.class)
                .setLogger(ConnectActivity.DemoLogger.class));
    }

}
