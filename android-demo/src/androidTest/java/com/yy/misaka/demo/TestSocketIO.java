package com.yy.misaka.demo;

import android.content.IntentFilter;
import android.util.Log;

import com.yy.misaka.demo.test.TestActivity;
import com.yy.misaka.demo.util.MyBroadcastReceiver;
import com.yy.misaka.demo.util.TestBase;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/7/26.
 */
public class TestSocketIO extends TestBase {
    public final static String TAG = "TestSocketIO";
    private String pushData = "Hello World";
    private int timeOut = 10;
    private MyBroadcastReceiver myBroadcastReceiver;

    @Override
    public void mySetUp() {
        super.mySetUp();
        myBroadcastReceiver = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.yy.misaka.demo.YY_NOTIFICATION");
        mActivity.registerReceiver(myBroadcastReceiver, filter);
    }

    public void testInit() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        assertNotNull(mActivity);
        mActivity.setConnectCallBack(new TestActivity.TestConnectCallBack() {
            @Override
            public void isConnect(boolean state) {
                assertEquals(state, true);
                signal.countDown();
            }
        });
        boolean resutl = signal.await(timeOut, TimeUnit.SECONDS);
        assertEquals(resutl, true);//timeout return false
    }

    public void testPushByTopic() throws InterruptedException {
        pushTest(true);
    }

    public void testPushByPushId() throws InterruptedException {
        pushTest(false);
    }

    public void testNotification() throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(2);
        httpUtil.asyncGet(httpUtil.getNotificationUrl(mActivity.host, mActivity.pushId), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assertEquals(response.body().string(), httpResult);
                signal.countDown();
            }
        });
        myBroadcastReceiver.setNotificationCallBack(new MyBroadcastReceiver.TestNotificationCallBack() {
            @Override
            public void onNotification(String title, String message, String payload) {
                assertEquals(title, "title");
                assertEquals(message, "message");
                signal.countDown();
            }
        });
        boolean resutl = signal.await(timeOut, TimeUnit.SECONDS);
        assertEquals(resutl, true);
    }

    @Override
    public void myTearDowm() {
        super.myTearDowm();
        mActivity.unregisterReceiver(myBroadcastReceiver);
    }

    private void pushTest(boolean isTopic) throws InterruptedException{
        String url = httpUtil.getPushUrlByPushId(mActivity.host, pushData, mActivity.pushId);
        if (isTopic) {
            url = httpUtil.getPushUrlByTopic(mActivity.host, pushData, mActivity.topic);
        }
        final CountDownLatch signal = new CountDownLatch(2);
        httpUtil.asyncGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assertEquals(response.body().string(), httpResult);
                signal.countDown();
            }
        });

        mActivity.setPushCallBack(new TestActivity.TestPushCallBack() {
            @Override
            public void onPush(String data) {
                Log.i(TAG, "onPush: " + data);
                assertEquals(data, pushData);
                signal.countDown();
            }
        });
        boolean resutl = signal.await(timeOut, TimeUnit.SECONDS);
        assertEquals(resutl, true);
    }


}
