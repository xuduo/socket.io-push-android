package com.yy.httpproxy.subscribe;

import java.util.Set;

/**
 * Created by xuduo on 10/20/15.
 */
public interface ConnectCallback {

    /**
     * @param uid 连接push-server后,在服务器绑定的uid
     */
    void onConnect(String uid, Set<String> tags);

    void onDisconnect();

}
