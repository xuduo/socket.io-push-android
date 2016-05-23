package com.yy.httpproxy.util;

/**
 * Created by xuduo on 5/23/16.
 */
public class LogcatLogger implements Logger {

    @Override
    public void log(String level, String message, Throwable e) {
        if (level.equals("debug")) {
            android.util.Log.d("push-sdk", message, e);
        } else if (level.equals("info")) {
            android.util.Log.i("push-sdk", message, e);
        } else if (level.equals("error")) {
            android.util.Log.e("push-sdk", message, e);
        }
    }
}
