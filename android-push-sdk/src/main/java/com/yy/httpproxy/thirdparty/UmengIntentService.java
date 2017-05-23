package com.yy.httpproxy.thirdparty;

import android.content.Context;
import android.content.Intent;

import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.service.DefaultNotificationHandler;
import com.yy.httpproxy.service.ForegroundService;
import com.yy.httpproxy.service.PushedNotification;
import com.yy.httpproxy.util.Log;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

public class UmengIntentService extends UmengMessageService {

    private static final String TAG = UmengIntentService.class.getName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (ForegroundService.instance == null) {
            Log.i(TAG, "start ConnectionService from umeng");
            Context context = getApplicationContext();
            Intent intent = new Intent(context, ConnectionService.class);
            context.startService(intent);
        }
    }

    @Override
    public void onMessage(Context context, Intent intent) {
        Log.d(TAG, "message=" + intent.getStringExtra(AgooConstants.MESSAGE_BODY));
        try {
            //可以通过MESSAGE_BODY取得消息体
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage msg = new UMessage(new JSONObject(message));
            Log.d(TAG, "message=" + message);      //消息体
            Log.d(TAG, "custom=" + msg.custom);    //自定义消息的内容
            // 通知内容
            // code  to handle message here
            // ...

            try {
                JSONObject obj = new JSONObject(msg.custom);
                PushedNotification pushedNotification = new PushedNotification(obj.getString("id"), obj);
                if (ForegroundService.instance != null) {
                    ForegroundService.instance.onNotification(pushedNotification);
                }

                Log.d(TAG, "umeng on arrive " + msg.custom);
            } catch (Exception e) {
                Log.e(TAG, "umeng Could not parse malformed JSON: \"" + msg.custom + "\"", e);
            }

        } catch (Exception e) {
            UmLog.e(TAG, e.getMessage());
        }
    }
}