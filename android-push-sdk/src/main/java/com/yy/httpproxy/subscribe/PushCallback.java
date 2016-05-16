package com.yy.httpproxy.subscribe;

/**
 * Created by xuduo on 10/20/15.
 */
public interface PushCallback {

    /**
     * @param data 服务器push下来的json字符串, 可以new String(data,"UTF-8")转换为字符串
     */
    void onPush(String data);

}
