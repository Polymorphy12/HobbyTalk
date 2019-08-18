package com.example.sumin.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sumin on 2017-09-11.
 */

public class CreateOpenChatRoomActivity extends AppCompatActivity {

    EditText setRoomTitle;
    Button setRoomButton;

    String roomName="";
    String roomId;
    String myUserId;
    String myUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_open_chat_activity);

        setRoomButton = (Button) findViewById(R.id.setRoomButton);
        setRoomTitle = (EditText)findViewById(R.id.setRoomTitle);

//        setRoomTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                roomName = setRoomTitle.getText().toString();
//                Toast.makeText(getApplicationContext(), roomName+"이다", Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });

        setRoomTitle.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                roomName = setRoomTitle.getText().toString();
            }
        });

        setRoomButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                final SharedPreferences pref = getSharedPreferences("loginPref", MODE_PRIVATE);
                myUserId = pref.getString("loginUserName", "");
                myUserName = pref.getString("loginNickname", "");
                roomId = myUserId + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                //Toast.makeText(getApplicationContext(), roomName, Toast.LENGTH_SHORT).show();
                if (!roomName.equals("")) {
                    //Toast.makeText(getApplicationContext(), roomId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateOpenChatRoomActivity.this, ChatRoomActivity.class);
                    intent.putExtra("callNum",0);
                    intent.putExtra("myUserId", myUserId);
                    intent.putExtra("myUserName", myUserName);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("roomName", roomName);
                    // 방을 만들 때에는 forresult 코드가 0.

                    Log.d("인텐트 들어갔니", "응 인텐트 넘기고 있어");
                    startActivity(intent);
                    finish();
                }
                else
                {

                }
            }
        });



    }
}
