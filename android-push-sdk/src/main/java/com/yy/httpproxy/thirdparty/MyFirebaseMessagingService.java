package com.yy.httpproxy.thirdparty;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yy.httpproxy.socketio.RemoteClient;
import com.yy.httpproxy.util.Log;

/**
 * Created by xuduo on 08/03/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "fcm to remote client" + remoteMessage.getData());
            RemoteClient.sendNotificationReceive(remoteMessage.getData().get("payload"));

        }

    }


}
