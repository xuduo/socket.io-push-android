package com.yy.httpproxy.service;

/**
 * Created by huangzhilong on 2016/8/18.
 */
public class DefaultDnsHandler implements DnsHandler {

    @Override
    public String handlerDns(String host) {
        return host;
    }
}
