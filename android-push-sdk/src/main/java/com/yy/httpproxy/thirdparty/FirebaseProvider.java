package com.yy.httpproxy.thirdparty;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.huawei.hms.api.ConnectionResult;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.service.DefaultNotificationHandler;
import com.yy.httpproxy.service.PushedNotification;
import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.ServiceCheckUtil;

import org.json.JSONObject;

/**
 * Created by xuduo on 22/05/2017.
 */

public class FirebaseProvider implements NotificationProvider {

    public final static String TAG = "FirebaseProvider";
    private static String token;

    public FirebaseProvider(Context context) {
        FirebaseApp.initializeApp(context);
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.i(TAG, "FCMProvider init " + token);
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        if (token != null) {
            setToken(token);
        }

    }

    public static boolean available(Context context) {
        try {
            boolean available = Class.forName("com.google.firebase.iid.FirebaseInstanceId") != null
                    && ServiceCheckUtil.isIntentServiceAvailable(context, MyFirebaseInstanceIdService.class) &&
                    ServiceCheckUtil.isIntentServiceAvailable(context, MyFirebaseMessagingService.class) &&
                      isGooglePlayServicesAvailable(context);
            Log.d(TAG, "available " + available);
            return available;
        } catch (Throwable e) {
            Log.e(TAG, "available ", e);
            return false;
        }
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        boolean result = resultCode == ConnectionResult.SUCCESS;
        Log.d(TAG, "isGooglePlayServicesAvailable " + result);
        return true;
    }


    public static boolean handleLauncher(Activity activity) {
        Bundle bundle = activity.getIntent().getExtras();
        if (bundle != null) {
            String payload = bundle.getString("payload");
            if (payload != null) {
                try {

                    JSONObject obj = new JSONObject(payload);
                    PushedNotification pushedNotification = new PushedNotification(obj.getString("id"), obj);

                    Intent clickIntent = new Intent(DefaultNotificationHandler.getIntentName(activity));
                    clickIntent.putExtra("cmd", ConnectionService.CMD_NOTIFICATION_CLICKED);
                    clickIntent.putExtra("id", pushedNotification.id);
                    clickIntent.putExtra("title", pushedNotification.title);
                    clickIntent.putExtra("message", pushedNotification.message);
                    clickIntent.putExtra("payload", pushedNotification.payload);
                    activity.sendBroadcast(clickIntent);

                    Log.d("Message ", obj.getString("id"));
                    return true;

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + payload + "\"");
                }
            }
        }
        return false;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public String getType() {
        return "fcm";
    }

    @Override
    public void setToken(String token) {
        this.token = token;
    }
}
