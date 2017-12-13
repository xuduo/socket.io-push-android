package com.yy.misaka.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.util.Log;

import com.yy.httpproxy.thirdparty.ProviderFactory;

public class NickNameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DemoLogger", "NickNameActivity onCreate");
        setContentView(R.layout.activity_nick);
        String provider = "socket.io";
        if (ProviderFactory.checkProvider(this) != null) {
            provider = ProviderFactory.checkProvider(this).getSimpleName();
        }
        setTitle("NotificationProvider :" + provider);
        init();
    }

    private void init() {
        final EditText etNickName = (EditText) findViewById(R.id.et_nick_nickname);

        findViewById(R.id.btn_nick_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = String.valueOf(etNickName.getText()).trim();
                if (nickname.equals("")) {
                    Toast.makeText(NickNameActivity.this, "Nickname can't be null", Toast.LENGTH_SHORT).show();
                } else {
                    ChatActivity.launch(NickNameActivity.this, nickname);
                }
            }
        });

        findViewById(R.id.btn_draw_enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(NickNameActivity.this, DrawActivity.class);
                startActivity(intent);
            }
        });
    }

}
