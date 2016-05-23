package com.yy.httpproxy.util;

/**
 * Created by xuduo on 5/23/16.
 */
public class LogcatLogger implements Logger {

    @Override
    public void log(int level, String message, Throwable e) {
        if (level == Logger.DEBUG) {
            android.util.Log.d("push-sdk", message, e);
        } else if (level == Logger.INFO) {
            android.util.Log.i("push-sdk", message, e);
        } else if (level == Logger.ERROR) {
            android.util.Log.e("push-sdk", message, e);
        }
    }
}
