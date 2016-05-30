##socket.io-push-android [![Build Status](https://travis-ci.org/xuduo/socket.io-push-android.svg?branch=master)](https://travis-ci.org/xuduo/socket.io-push-android)
demo实现了一个聊天室功能,

#####添加maven/gradle依赖

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.yy/android-push-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.yy/android-push-sdk)


maven
```xml
<dependency>
    <groupId>com.yy</groupId>
    <artifactId>android-push-sdk</artifactId>
    <version>${version}</version>
</dependency>
```


gradle
```groovy
compile 'com.yy:android-push-sdk:${versoion}'
```

####AndroidManifest.xml添加receiver,service,permission
参见Demo的[AndroidManifest.xml](src/main/AndroidManifest.xml)

####Proguard

混淆配置
```
-dontwarn com.yy.httpproxy.thirdparty.**
-dontwarn okio.**

##如接入华为push
-keep class com.huawei.android.pushagent.** {*;}
-keep class com.huawei.android.pushselfshow.** {*;}
-keep class com.huawei.android.microkernel.** {*;}
-keep class com.baidu.mapapi.** {*;}
-dontwarn com.huawei.**
##如接入华为push


```

#####初始化ProxyClient
每次UI进程启动需要初始化,初始化后会自动启动push进程.

```java
Proxy proxyClient = new ProxyClient(new Config(this)
                .setHost("http://spush.yy.com")  //连接的服务器地址
                .setConnectCallback(this)  //socket.io通道,连上,断开回调
                .setPushCallback(this)  //push回调,socket.io长连接通道
                .setLogger(MyLogger.class)); //日志回调,可选,这个类会实例化在push进程
```
注意不要通过其他进程启动,可以用以下代码判断是否ui进程
```java
 private boolean isUiProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
  }
```


#####获取pushId

由客户端自动生成, proxyClient实例化后即可获得

用于服务器对单个设备发push/notification
```java
String pushId = proxyClient.getPushId();
```



####subscribe/unsbuscribe topic

调用不需考虑当时是否连线, 重连也不需要重新sub/unsub,sdk里已经处理
```java
proxyClient.subscribeBroadcast("aTopic"); //对于某个topic的push,需要客户端主动订阅,才能收到.如demo中,需订阅"chatRoom" topic,才能收到聊天消息
proxyClient.subscribeAndReceiveTtlPackets("aTopic"); //同上,这个方法会接收服务器的重传
proxyClient.unsubscribeBroadcast("aTopic");
```



####接收push
```java
public interface PushCallback {

    /**
     *
     * @param data 服务器push下来的字符串
     */
    void onPush(String data);
}
```


####接收notification(使用DefaultNotificationHandler/DelegateToClientNotificationHandler)
sdk默认会弹出系统通知
arrive和click后会调用receiver的方法
```java
public class YYNotificationReceiver extends NotificationReceiver {

    @Override
    public void onNotificationClicked(Context context, PushedNotification notification) {
        Log.d("YYNotificationReceiver", "onNotificationClicked " + notification.id + " values " + notification.values);
        Toast.makeText(context, "YYNotificationReceiver clicked payload: " + notification.values.get("payload"), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, DrawActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     *  如果使用DelegateToClientNotificationHandler ,UI进程存活的时候,会调用此方法,不弹出通知.
     *  UI进程被杀,push进程存活的时候,使用默认的样式弹出
     */
    @Override
    public void onNotificationArrived(Context context, PushedNotification notification) {
        Log.d("YYNotificationReceiver", "onNotificationArrived " + notification.id + " values " + notification.values);
    }

}
```
启动时的配置,配置使用
```java
config.setNotificationHandler(MyHandlerClass.class); //不能混淆这个类
```


####自定义弹出通知(使用自定义NotificationHandler)
可以用代码根据业务服务器下发的notification中的自定义payload字段,展示不同的效果

注意!NotificationHandler的实例,是在push进程中的!

实现接口
```java
public interface NotificationHandler {
    /**
     *
     * @param context context
     * @param binded UI进程 是否存活
     * @param notification  业务服务器下发的notification
     */
    void handlerNotification(Context context, boolean binded, PushedNotification notification);

}
```
启动时的配置,设置NotificationHandler
```java
config.setNotificationHandler("yourFullyQualifiedHandlerClassName"); //不能混淆这个类
```



####绑定UID
绑定UID是业务服务器调用push-server接口进行绑定的(pushId - uid)的关系

由于安全性的问题,客户端无法直接绑定

push-server和业务服务器的绑定关系可能会不一致

每次连接到push-sever后, 客户端要根据回调, 判断回调的uid与当前用户uid是否一致, 进行绑定/解绑
```java
public interface ConnectCallback {

    /**
     *  
     * @param uid 连接push-server后,在服务器绑定的uid,如未绑定,uid = ""
     */
    void onConnect(String uid);

    void onDisconnect();

}
```
解绑Uid,客户端直接调用接口解绑
```java
proxyClient.unbindUid();
```



####集成小米push


[华为push官方接入文档](http://dev.xiaomi.com/doc/?page_id=1670)

本系统透明集成了小米push,开启方法.


1. 添加小米push jar依赖
2. AndroidManifest.xml配置了小米push相关配置,参见demo
3. 当前手机运行MiUi系统

注意项

1. SDK会自动上报小米的regId,并不需要业务代码改动
2. 对于开启的手机,无法使用自定义NotificationHandler控制notification弹出
3. 可以通过push-server配置,应用在前台的时候,不弹出通知(小米push功能)



####集成华为push

[华为push官方接入文档](http://developer.huawei.com/push)

本系统透明集成了华为push,开启方法

1. 添加华为push jar依赖,拷贝一堆资源文件(华为push自带)
2. AndroidManifest.xml配置了华为push相关配置,参见demo
3. 当前手机运行华为系统

注意项

1. SDK会自动上报华为的token,并不需要业务代码改动
2. 对于开启的手机,无法使用自定义NotificationHandler控制notification弹出


####UI进程单独使用push功能

```java
    String pushId = new RandomPushIdGenerator().generatePushId(pushId); //生成随机pushId
    SocketIOProxyClient client = new SocketIOProxyClient(this.getApplicationContext(), host, null);
    client.setPushId(pushId);   //设置pushId
    client.setPushCallback(this); // push回调
    client.setNotificationCallback(this); //notification回调
    client.setConnectCallback(this); //连接回调
```
