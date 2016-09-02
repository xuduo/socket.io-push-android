package com.yy.misaka.demo.util;

import android.content.Intent;
import android.content.IntentFilter;
import android.test.InstrumentationTestCase;

import com.yy.misaka.demo.test.TestActivity;

/**
 * Created by Administrator on 2016/7/22.
 */
public class TestBase extends InstrumentationTestCase {

    public TestActivity mActivity;
    public String httpResult = "{\"code\":\"success\"}";
    public HttpUtil httpUtil = new HttpUtil();
    public MyBroadcastReceiver myBroadcastReceiver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("com.yy.misaka.demo", TestActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity = (TestActivity) getInstrumentation().startActivitySync(intent);

        if (getName().equals("testNotification")) {
            myBroadcastReceiver = new MyBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.yy.misaka.demo.YY_NOTIFICATION");
            mActivity.registerReceiver(myBroadcastReceiver, filter);
        }
    }

    @Override
    protected void tearDown() {
        if (getName().equals("testNotification")) {
            mActivity.unregisterReceiver(myBroadcastReceiver);
        }
        mActivity.finish();
        try {
            super.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
