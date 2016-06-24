package com.yy.httpproxy;


import com.yy.httpproxy.requester.HttpResponse;

public abstract class ReplyHandler {

    public abstract void onReply(HttpResponse response);

}
