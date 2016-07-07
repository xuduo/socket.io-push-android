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
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class HttpActivity extends Activity {

    private static final String TAG = "HttpActivity";
    public static String host;
    private ProxyClient proxyClient;
    private int count = 0;

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
                final long start = System.currentTimeMillis();
                for (int i = 0; i < 1000; i++) {
                    HttpRequest request = new HttpRequest();
                    request.setUrl(url);
                    request.setMethod("get");
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("httpResponseonResult1", "httpResponseonResult");
                    headers.put("httpResponseonResult11", "httpResponseonResult");
                    headers.put("httpResponseonResult12", "httpResponseonResult");
                    headers.put("httpResponseonResult13", "httpResponseonResult");
                    headers.put("httpResponseonResult14", "httpResponseonResult");
                    headers.put("httpResponseonResult15", "httpResponseonResult");
                    headers.put("httpResponseonResult16", "httpResponseonResult");
                    headers.put("httpResponseonResult17", "httpResponseonResult");
                    headers.put("httpResponseonResult18", "httpResponseonResult");
                    headers.put("httpResponseonResult19", "httpResponseonResult");
                    request.setHeaders(headers);
                    Log.d(TAG, "http request url " + url);

                    proxyClient.http(request, new HttpCallback() {
                        @Override
                        public void onResult(HttpResponse httpResponse) {
                            Log.d(TAG, count++ + "httpResponse onResult " + (System.currentTimeMillis() - start) + " " + url + httpResponse);
                            ((TextView) findViewById(R.id.tv_content)).setText(httpResponse.getBody());
                        }

                    });
                }
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
                count = 0;
                for (int i = 0; i < 1000; i++) {
                    HttpUtils.request(url, null, new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "sendMessage onFailure ", e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Log.d(TAG, count++ + "raw httpResponse " + response.protocol().name() + " " + (System.currentTimeMillis() - start) + " " + " " + url + " " + response.body().string());
                            response.close();
                        }
                    });
                }
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