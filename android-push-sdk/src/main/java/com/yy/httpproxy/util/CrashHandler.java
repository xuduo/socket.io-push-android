package com.yy.httpproxy.util;

/**
 * Created by xuduo on 5/23/16.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler defaultUEH;

    public CrashHandler() {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("CrashHandler", "push-sdk crashed", e);
        defaultUEH.uncaughtException(t, e);
    }
}
