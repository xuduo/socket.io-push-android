package com.yy.misaka.demo.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/7/26.
 */
public class HttpUtil {

    private OkHttpClient okHttpClient = new OkHttpClient();

    public void asyncGet(String requestUrl, Callback callback) {
        final Request request = new Request.Builder().url(requestUrl).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public String getPushUrlByTopic(String host, String pushData, String topic) {
        return host + "/api/push?json=" + pushData + "&topic=" + topic;
    }

    public String getPushUrlByPushId(String host, String pushData, String pushId) {
        return host + "/api/push?json=" + pushData + "&pushId=" + pushId;
    }

    public String getNotificationUrl(String host, String pushId) {
        return host + "/api/notification?pushId=" + pushId
                + "&notification=%7B%20%22android%22%3A%7B%22title%22%3A%22title%22%2C%22message%22%3A%22message%22%7D%2C%22apn%22%3A%7B%22alert%22%3A%22message%22%20%2C%20%22badge%22%3A5%2C%20%22sound%22%3A%22default%22%2C%20%22payload%22%3A1234%7D%7D";
    }
}
