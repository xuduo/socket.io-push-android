package com.yy.httpproxy.service;

import android.content.Context;

/**
 * Created by huangzhilong on 2016/8/18.
 */
public class DefaultDnsHandler implements DnsHandler {

    @Override
    public void init(Context context) {

    }

    @Override
    public String handlerDns(String host) {
        return host;
    }
}
