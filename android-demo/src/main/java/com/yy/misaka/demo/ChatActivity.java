package com.yy.misaka.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.yy.httpproxy.Config;
import com.yy.httpproxy.ProxyClient;
import com.yy.misaka.demo.util.JsonSerializer;
import com.yy.httpproxy.service.DefaultNotificationHandler;
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.httpproxy.util.Logger;
import com.yy.misaka.demo.adapter.ChatMessagesAdapter;
import com.yy.misaka.demo.appmodel.HttpApiModel;
import com.yy.misaka.demo.entity.Message;

public class ChatActivity extends Activity implements PushCallback, ConnectCallback {

    public final static String chatTopic = "chatRoom";
    public final static String TAG = "ChatActivity";
    private RecyclerView recyclerViewMessages;
    private ProxyClient proxyClient;
    private HttpApiModel httpApiModel = new HttpApiModel(API_URL);
    private ChatMessagesAdapter chatMessagesAdapter;
    private static final String API_URL = "http://spush.yy.com/api/push";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DemoLogger", "ChatActivity onCreate");
        setContentView(R.layout.activity_chat);
        init();
    }

    private void init() {
        final String nickName = getIntent().getStringExtra("nickName");
        final String host = getIntent().getStringExtra("host");
        final EditText editTextInput = (EditText) findViewById(R.id.et_input);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                message.setMessage(String.valueOf(editTextInput.getText()));
                message.setNickName(nickName);
                httpApiModel.sendMessage(message);
                editTextInput.setText("");
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        chatMessagesAdapter = new ChatMessagesAdapter();
        recyclerViewMessages = (RecyclerView) findViewById(R.id.rv_messages);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);
        recyclerViewMessages.setAdapter(chatMessagesAdapter);
        recyclerViewMessages.scrollToPosition(chatMessagesAdapter.getItemCount() - 1);
        proxyClient = new ProxyClient(new Config(this).setHost(host).setConnectCallback(this)
                .setPushCallback(this)
                .setNotificationHandler(DefaultNotificationHandler.class)
                .setRequestSerializer(new JsonSerializer())
                .setLogger(DemoLogger.class));
        proxyClient.subscribeAndReceiveTtlPackets(chatTopic);
        updateConnect();

    }

    public static class DemoLogger implements Logger {

        @Override
        public void log(int level, String message, Throwable e) {
            Log.d("DemoLogger", "demo " + message);
        }
    }

    public static void launch(Context context, String nickName, String host) {
        Intent intent = new Intent();
        intent.putExtra("nickName", nickName);
        intent.putExtra("host", host);
        intent.setClass(context, ChatActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void updateConnect() {
        String connectState = proxyClient.isConnected() ? "(connected)" : "(disconnected)";
        setTitle("ChatRoom" + connectState);
    }

    @Override
    public void onConnect(String uid) {
        updateConnect();
    }

    @Override
    public void onDisconnect() {
        updateConnect();
    }

    @Override
    public void onPush(String data) {
        Log.i(TAG, "on push " + data);
        try {
            Message message = new Gson().fromJson(data, Message.class);
            if ("chat_message".equals(message.getType())) {
                chatMessagesAdapter.addData(message);
                recyclerViewMessages.scrollToPosition(chatMessagesAdapter.getItemCount() - 1);
            }
        } catch (Exception e) {

        }

    }

}