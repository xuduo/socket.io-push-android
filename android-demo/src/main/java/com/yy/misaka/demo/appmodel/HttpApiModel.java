package com.yy.misaka.demo.appmodel;

import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.yy.httpproxy.ProxyClient;
import com.yy.httpproxy.requester.HttpCallback;
import com.yy.httpproxy.requester.HttpRequest;
import com.yy.httpproxy.requester.HttpResponse;
import com.yy.misaka.demo.ChatActivity;
import com.yy.misaka.demo.entity.Message;
import com.yy.misaka.demo.util.HttpUtils;
import com.yy.misaka.demo.util.JsonHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpApiModel {

    private String url;
    private static final String TAG = "HttpApiModel";
    private ProxyClient proxyClient;

    public HttpApiModel(String url, ProxyClient proxyClient) {
        this.url = url;
        this.proxyClient = proxyClient;
    }

    public void sendMessage(Message msg) {
        HashMap<String, String> params = new HashMap<>();
        String msgStr = JsonHelper.toJson(msg, "UTF-8");
        Log.d(TAG, "msgStr " + msgStr);
        params.put("json", msgStr);
        params.put("topic", ChatActivity.chatTopic);
//        HttpUtils.request(url, params, new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Log.d(TAG, "sendMessage onFailure");
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                Log.d(TAG, "sendMessage onResponse" + response.body());
//            }
//        });

        HttpRequest request = new HttpRequest();
        request.setUrl(url);
        request.setParams(params);

        proxyClient.http(request, new HttpCallback() {
            @Override
            public void onResult(HttpResponse httpResponse) {

            }

        });
    }

}
