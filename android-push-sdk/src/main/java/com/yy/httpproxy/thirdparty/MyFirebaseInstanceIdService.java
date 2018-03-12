package com.yy.httpproxy.thirdparty;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.util.Log;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIdService.class.getName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "refreshedToken=" + refreshedToken);
        ConnectionService.setToken(refreshedToken);
    }

}