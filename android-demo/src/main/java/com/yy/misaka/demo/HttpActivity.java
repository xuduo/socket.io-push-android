package com.yy.misaka.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yy.httpproxy.Config;
import com.yy.httpproxy.ProxyClient;
import com.yy.httpproxy.requester.HttpCallback;
import com.yy.httpproxy.requester.HttpRequest;
import com.yy.httpproxy.requester.HttpResponse;
import com.yy.httpproxy.service.DefaultNotificationHandler;
import com.yy.misaka.demo.util.HttpUtils;

import java.io.IOException;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;


public class HttpActivity extends Activity {

    private static final String TAG = "HttpActivity";
    public static String host;
    private ProxyClient proxyClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DemoLogger", "HttpActivity onCreate");
        setContentView(R.layout.activity_http);

        proxyClient = new ProxyClient(new Config(this).setHost(host)
                .setNotificationHandler(DefaultNotificationHandler.class));

        findViewById(R.id.btn_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = ((EditText) findViewById(R.id.et_url)).getText().toString();
                HttpRequest request = new HttpRequest();
                request.setUrl(url);
                request.setMethod("get");
                Log.d(TAG, "http request url " + url);
                final long start = System.currentTimeMillis();
                proxyClient.http(request, new HttpCallback() {
                    @Override
                    public void onResult(HttpResponse httpResponse) {
                        Log.d(TAG, "httpResponse onResult " + (System.currentTimeMillis() - start) + " " + url);
                        ((TextView) findViewById(R.id.tv_content)).setText(httpResponse.getBody());
                    }

                });
            }
        });
        findViewById(R.id.btn_raw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = ((EditText) findViewById(R.id.et_url)).getText().toString();
                HttpRequest request = new HttpRequest();
                request.setUrl(url);
                request.setMethod("get");
                Log.d(TAG, "http request url " + url);
                final long start = System.currentTimeMillis();
                HttpUtils.request(url, null, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.d(TAG, "sendMessage onFailure");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        Log.d(TAG, "raw httpResponse" + (System.currentTimeMillis() - start) + " " + " " + url);
                    }
                });
            }
        });

        findViewById(R.id.btn_ping).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpRequest request = new HttpRequest();
                request.setMethod("ping");
                request.setUrl("" + System.currentTimeMillis());
                Log.d(TAG, "http request url " + request.getUrl());
                final long start = System.currentTimeMillis();
                proxyClient.http(request, new HttpCallback() {
                    @Override
                    public void onResult(HttpResponse httpResponse) {
                        Log.d(TAG, "httpResponse onResult " + (System.currentTimeMillis() - start) + " " + httpResponse);
                        ((TextView) findViewById(R.id.tv_content)).setText(httpResponse.getBody());
                    }

                });
            }
        });
    }

    public static void launch(Context context, String host) {
        HttpActivity.host = host;
        Intent intent = new Intent();
        intent.setClass(context, HttpActivity.class);
        context.startActivity(intent);
    }
}