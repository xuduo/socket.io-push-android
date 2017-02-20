package com.yy.httpproxy.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by xuduo on 07/02/2017.
 */

public class HttpUtil {

    public interface HttpCallback {

        void onSuccess(String result);

        void onError(String message);

    }

    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public HttpUtil(){
        client.dispatcher().setMaxRequestsPerHost(1);
    }

    public void postJson(String url, String json, final HttpCallback callback) {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                callback.onSuccess(response.body().string());
            }
        });
    }

    public static void main(String[] args) {
        new HttpUtil().postJson("http://localhost:11001/api/push", "{\"topic\":\"chatRoom\" , \"json\":{ \"message\": \"test_message\", \"nickName\": \"HttpUtil\", \"type\": \"chat_message\"}}", new HttpCallback() {
            @Override
            public void onSuccess(String result) {
                System.out.println("onSuccess " + result);
            }

            @Override
            public void onError(String message) {
                System.out.println("onError " + message);
            }
        });
    }
}
