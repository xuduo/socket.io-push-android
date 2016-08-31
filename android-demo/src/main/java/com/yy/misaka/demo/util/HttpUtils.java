package com.yy.misaka.demo.util;


import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.internal.Util;

/**
 * Created by Administrator on 2016/4/27.
 */
public class HttpUtils {

    private static OkHttpClient okHttpClient = getUnsafeOkHttpClient();

    public static void request(String url, HashMap<String, String> params, Callback callback) {
        StringBuilder okRequestURL = new StringBuilder(url);
        if (params != null) {
            okRequestURL.append("?");
            for (String key : params.keySet()) {
                if (params.get(key) instanceof String) {
                    okRequestURL.append(key).append("=").append(params.get(key).toString()).append("&");
                } else {
                    String encodeStr = JsonHelper.toJson(params.get(key), "UTF-8");
                    okRequestURL.append(key).append("=").append(encodeStr).append("&");
                }
            }
        }
        Request request = new Request.Builder().url(okRequestURL.toString()).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            X509TrustManager manager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    manager
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, manager);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            builder.protocols(Util.immutableList(
                    Protocol.HTTP_2, Protocol.SPDY_3, Protocol.HTTP_1_1));
            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
