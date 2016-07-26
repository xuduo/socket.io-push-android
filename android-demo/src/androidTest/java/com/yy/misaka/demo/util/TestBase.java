package com.yy.misaka.demo.util;

import android.content.Intent;
import android.test.InstrumentationTestCase;

import com.yy.misaka.demo.test.TestActivity;

/**
 * Created by Administrator on 2016/7/22.
 */
public class TestBase extends InstrumentationTestCase {

    public TestActivity mActivity;
    public String httpResult = "{\"code\":\"success\"}";
    public HttpUtil httpUtil = new HttpUtil();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent();
        intent.setClassName("com.yy.misaka.demo", TestActivity.class.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity = (TestActivity) getInstrumentation().startActivitySync(intent);
        mySetUp();
    }

    @Override
    protected void tearDown() {
        myTearDowm();
        mActivity.finish();
        try {
            super.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mySetUp() {

    }

    public void myTearDowm() {

    }
}
