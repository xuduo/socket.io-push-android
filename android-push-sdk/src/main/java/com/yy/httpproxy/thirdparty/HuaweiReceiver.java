package com.yy.httpproxy.thirdparty;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.service.DefaultNotificationHandler;
import com.yy.httpproxy.service.PushedNotification;
import com.yy.httpproxy.util.Log;

import org.json.JSONArray;

/**
 * Created by Administrator on 2016/4/29.
 */
public class HuaweiReceiver extends PushReceiver {

    private static final String TAG = "HuaweiReceiver";

    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString("belongId");
        Log.i("DemoLogger", TAG + " token = " + token + ",belongId = " + belongId);
        ConnectionService.setToken(token);
    }

    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "onPushMsg： " + new String(msg, "UTF-8");
            Log.d(TAG, content);
        } catch (Exception e) {
        }
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String json = extras.getString(BOUND_KEY.pushMsgKey);
            String content = "huawei clicked： " + extras.getString(BOUND_KEY.pushMsgKey);
            android.util.Log.i("DemoLogger", TAG + " " + content);
            try {

                JSONArray obj = new JSONArray(json);

                PushedNotification pushedNotification = new PushedNotification(obj.getString(0), obj.getJSONObject(1));
                Intent clickIntent = new Intent(DefaultNotificationHandler.getIntentName(context));
                clickIntent.putExtra("cmd", ConnectionService.CMD_NOTIFICATION_CLICKED);
                clickIntent.putExtra("id", pushedNotification.id);
                clickIntent.putExtra("title", pushedNotification.title);
                clickIntent.putExtra("message", pushedNotification.message);
                clickIntent.putExtra("payload", pushedNotification.payload);
                context.sendBroadcast(clickIntent);
                Log.d(TAG, content);

            } catch (Exception e) {
                Log.e("DemoLogger", TAG + " Could not parse malformed JSON: \"" + json + "\"");
            }

        }
    }

}
