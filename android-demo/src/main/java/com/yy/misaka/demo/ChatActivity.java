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
import com.yy.httpproxy.subscribe.ConnectCallback;
import com.yy.httpproxy.subscribe.PushCallback;
import com.yy.misaka.demo.appmodel.DemoApp;
import com.yy.misaka.demo.adapter.ChatMessagesAdapter;
import com.yy.misaka.demo.entity.Message;

import java.util.Set;

public class ChatActivity extends Activity implements ConnectCallback, PushCallback {
    public final static String chatTopic = "chatRoom";
    public final static String TAG = "ChatActivity";
    private RecyclerView recyclerViewMessages;
    private ChatMessagesAdapter chatMessagesAdapter;
    public static final String API_URL = "https://spush.yy.com/api/push";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DemoLogger", "ChatActivity onCreate");
        setContentView(R.layout.activity_chat);
        init();

        DemoApp.APP_CONTEXT.proxyClient.subscribeAndReceiveTtlPackets(chatTopic);
        DemoApp.APP_CONTEXT.proxyClient.getConfig().setPushCallback(this).setConnectCallback(this);
    }

    private void init() {
        final String nickName = getIntent().getStringExtra("nickName");
        final EditText editTextInput = (EditText) findViewById(R.id.et_input);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(editTextInput.getText()).length() == 0) {
                    return;
                }
                Message message = new Message();
                message.setMessage(String.valueOf(editTextInput.getText()));
                message.setNickName(nickName);
                DemoApp.APP_CONTEXT.httpApi.sendMessage(message, chatTopic, null);
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

        updateConnect();
    }

    public static void launch(Context context, String nickName) {
        Intent intent = new Intent();
        intent.putExtra("nickName", nickName);
        intent.setClass(context, ChatActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void updateConnect() {
        String connectState = DemoApp.APP_CONTEXT.proxyClient.isConnected() ? "(connected)" : "(disconnected)";
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