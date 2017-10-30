package com.yy.httpproxy.thirdparty;

import android.content.Context;

import com.meizu.cloud.pushsdk.MzPushMessageReceiver;
import com.meizu.cloud.pushsdk.PushManager;
import com.meizu.cloud.pushsdk.platform.message.PushSwitchStatus;
import com.meizu.cloud.pushsdk.platform.message.RegisterStatus;
import com.meizu.cloud.pushsdk.platform.message.SubAliasStatus;
import com.meizu.cloud.pushsdk.platform.message.SubTagsStatus;
import com.meizu.cloud.pushsdk.platform.message.UnRegisterStatus;
import com.yy.httpproxy.service.ConnectionService;
import com.yy.httpproxy.util.Log;
import com.yy.httpproxy.util.ServiceCheckUtil;

public class MyMzPushMessageReceiver extends MzPushMessageReceiver {

    public final static String TAG = "MyMzPushMessageReceiver";

    @Override
    @Deprecated
    public void onRegister(Context context, String pushid) {
        //调用PushManager.register(context）方法后，会在此回调注册状态
        //应用在接受返回的pushid
        Log.d(TAG, "MyMzPushMessageReceiver onRegister " + pushid);

    }

    @Override
    public void onMessage(Context context, String s) {
        //接收服务器推送的透传消息
    }

    @Override
    @Deprecated
    public void onUnRegister(Context context, boolean b) {
        //调用PushManager.unRegister(context）方法后，会在此回调反注册状态
    }

    @Override
    public void onPushStatus(Context context,PushSwitchStatus pushSwitchStatus) {
        //检查通知栏和透传消息开关状态回调
    }

    @Override
    public void onRegisterStatus(Context context,RegisterStatus registerStatus) {
        //调用新版订阅PushManager.register(context,appId,appKey)回调
        ConnectionService.setToken(registerStatus.getPushId());
        PushManager.switchPush(context, ServiceCheckUtil.getMetaDataValue(context, "MEIZU_APP_ID"), ServiceCheckUtil.getMetaDataValue(context, "MEIZU_APP_KEY"), registerStatus.getPushId(), 0, true);
        Log.d(TAG, "MyMzPushMessageReceiver onRegisterStatus " + registerStatus.getPushId());
    }

    @Override
    public void onUnRegisterStatus(Context context,UnRegisterStatus unRegisterStatus) {
        //新版反订阅回调
    }

    @Override
    public void onSubTagsStatus(Context context,SubTagsStatus subTagsStatus) {
        //标签回调
    }

    @Override
    public void onSubAliasStatus(Context context,SubAliasStatus subAliasStatus) {
        //别名回调
    }
    @Override
    public void onNotificationArrived(Context context, String title, String content, String selfDefineContentString) {
        //通知栏消息到达回调，flyme6基于android6.0以上不再回调
        Log.d(TAG, "MyMzPushMessageReceiver onNotificationArrived " + title);
    }

    @Override
    public void onNotificationClicked(Context context, String title, String content, String selfDefineContentString) {
        //通知栏消息点击回调
        Log.d(TAG, "MyMzPushMessageReceiver onNotificationClicked " + title);
    }

    @Override
    public void onNotificationDeleted(Context context, String title, String content, String selfDefineContentString) {
        //通知栏消息删除回调；flyme6基于android6.0以上不再回调
    }
}
