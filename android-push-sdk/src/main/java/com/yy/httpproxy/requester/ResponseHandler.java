package com.yy.httpproxy.requester;


import java.util.Map;

public interface ResponseHandler {

    void onHttp(int sequenceId, int code, Map<String, String> headers, String body);

}
