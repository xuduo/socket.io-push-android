package com.yy.httpproxy.requester;

import android.os.Bundle;

import java.util.Map;

/**
 * Created by xuduo on 6/17/16.
 */
public class HttpResponse {

    private int statusCode;
    private String sequenceId;
    private String body;
    private Map<String, String> headers;

    public static HttpResponse fromBundle(Bundle bundle){
        HttpResponse response = new HttpResponse();
        response.body = bundle.getString("body");
        response.sequenceId = bundle.getString("sequenceId");
        response.statusCode = bundle.getInt("code", 200);
        response.headers = (Map<String, String>) bundle.getSerializable("headers");
        return response;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", sequenceId='" + sequenceId + '\'' +
                ", body='" + body + '\'' +
                ", headers=" + headers +
                '}';
    }
}
