package com.yy.httpproxy.util;

/**
 * Created by xuduo on 5/23/16.
 */
public interface Logger {

    int DEBUG = 3;

    int INFO = 4;

    int ERROR = 6;

    void log(int level, String message, Throwable e);

}
