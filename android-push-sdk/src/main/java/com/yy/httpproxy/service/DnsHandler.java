package com.yy.httpproxy.service;


import android.content.Context;

/**
 * Created by huangzhilong on 2016/8/18.
 */
public interface DnsHandler {

    void init(Context context);

    String handlerDns(String host);
}
