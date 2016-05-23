package com.yy.httpproxy.util;

/**
 * Created by xuduo on 5/23/16.
 */
public class Log {

    public static Logger logger;

    public static void d(String tag, String message) {
        log("debug", tag, message, null);
    }

    public static void i(String tag, String message) {
        log("info", tag, message, null);
    }

    public static void e(String tag, String message) {
        log("error", tag, message, null);
    }

    public static void e(String tag, String message, Throwable e) {
        log("error", tag, message, e);
    }

    private static void log(String level, String tag, String message, Throwable e) {
        if (logger != null) {
            logger.log(level, tag + ":" + message, e);
        }
    }

}
