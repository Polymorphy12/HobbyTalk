package com.example.sumin.myapplication;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Sumin on 2017-10-21.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    String myUserId;

    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        Log.d("token", "onTokenRefresh");

        final SharedPreferences pref = getSharedPreferences("loginPref", MODE_PRIVATE);
        myUserId = pref.getString("loginUserName","");

        // 생성등록된 토큰을 개인 앱서버에 보내 저장해 두었다가 추가 뭔가를 하고 싶으면 할 수 있도록 한다.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        //post로 FCM 토큰과 현재 앱의 유저 아이디를 보낸다. (db에 등록 용도)
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .add("user_id", myUserId)
                .build();

        Log.d("token", "리퀘스트 보냈당");
        Log.d("token", token);
        //request
        Request request = new Request.Builder()
                .url("http://ty79450.vps.phps.kr/fcm/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
