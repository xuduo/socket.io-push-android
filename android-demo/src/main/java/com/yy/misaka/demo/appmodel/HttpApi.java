package com.yy.misaka.demo.appmodel;

import android.util.Log;

import com.yy.httpproxy.ProxyClient;
import com.yy.misaka.demo.ChatActivity;
import com.yy.misaka.demo.entity.Message;
import com.yy.misaka.demo.util.HttpUtils;
import com.yy.misaka.demo.util.JsonHelper;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HttpApi {
    private String url;
    private static final String TAG = "HttpApi";

    public interface CB {
        void latency(long latency);
    }

    public HttpApi(String url) {
        this.url = url;
    }

    public void sendMessage(Object msg, String topic,final CB cb) {
        HashMap<String, String> params = new HashMap<>();
        String msgStr = JsonHelper.toJson(msg, "UTF-8");
        Log.d(TAG, "msgStr " + msgStr);
        params.put("json", msgStr);
        params.put("topic", topic);
        final long start = System.currentTimeMillis();
        HttpUtils.request(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "sendMessage onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "sendMessage onResponse " + " p: " + response.protocol() + " " + response.body().string());
                if (cb != null) {
                    cb.latency(System.currentTimeMillis() - start);
                }
            }
        });
    }
}
