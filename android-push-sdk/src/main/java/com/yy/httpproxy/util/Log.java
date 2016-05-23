package com.yy.httpproxy.util;

/**
 * Created by xuduo on 5/23/16.
 */
public class Log {

    public static Logger logger;

    public static void d(String tag, String message) {
        log(Logger.DEBUG, tag, message, null);
    }

    public static void i(String tag, String message) {
        log(Logger.INFO, tag, message, null);
    }

    public static void e(String tag, String message) {
        log(Logger.ERROR, tag, message, null);
    }

    public static void e(String tag, String message, Throwable e) {
        log(Logger.ERROR, tag, message, e);
    }

    private static void log(int level, String tag, String message, Throwable e) {
        if (logger != null) {
            logger.log(level, tag + ":" + message, e);
        }
    }

}
