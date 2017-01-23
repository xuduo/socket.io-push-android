package com.yy.httpproxy.util;

/**
 * Created by xuduo on 5/23/16.
 */
public class Log {

    public static Logger logger;

    public static void d(String tag, String message) {
        if (logger != null) {
            log(Logger.DEBUG, tag, message, null);
        } else {
            android.util.Log.d(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (logger != null) {
            log(Logger.INFO, tag, message, null);
        } else {
            android.util.Log.i(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (logger != null) {
            log(Logger.ERROR, tag, message, null);
        } else {
            android.util.Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Throwable e) {
        if (logger != null) {
            log(Logger.ERROR, tag, message, e);
        } else {
            android.util.Log.e(tag, message, e);
        }
    }

    private static void log(int level, String tag, String message, Throwable e) {
        logger.log(level, tag + ":" + message, e);
    }

}
