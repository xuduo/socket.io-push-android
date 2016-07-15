package com.yy.httpproxy.service;

import android.content.Context;

/**
 * Created by xuduo on 11/6/15.
 */

public class DelegateToClientNotificationHandler extends DefaultNotificationHandler {

    @Override
    public void handlerNotification(Context context, boolean binded, PushedNotification pushedNotification) {
        if (!binded) {
            showNotification(context, pushedNotification);
        } else {
            sendArrived(context, pushedNotification);
        }
    }

}
