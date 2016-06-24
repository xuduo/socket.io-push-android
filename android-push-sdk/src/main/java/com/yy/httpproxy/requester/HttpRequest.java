package com.yy.httpproxy.requester;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Created by xuduo on 10/16/15.
 */
public class HttpRequest implements Serializable {

    private String sequenceId = new BigInteger(130, new SecureRandom()).toString(32);
    private String method = "get";
    private String url;
    private String body;
    private HashMap<String, String> params;
    private HashMap<String, String> headers;

    private long timestamp;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public void setParams(Object... paramArray) {
        params = new HashMap<>();
        for (int i = 0; i + 1 < paramArray.length; i = i + 2) {
            params.put(paramArray[i].toString(), paramArray[i + 1].toString());
        }
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    public boolean timeoutForRequest(long timeout) {
        return System.currentTimeMillis() - timestamp > timeout;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
