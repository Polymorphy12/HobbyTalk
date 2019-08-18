package com.example.sumin.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Sumin on 2017-08-16.
 */

public class SignInActivity extends AppCompatActivity{


    AlertDialog.Builder alertBuilder;


    private Button btnRegister;
    private Button btnLogin;

    EditText userId;
    EditText userPassword;

    String userIdString;
    String userPasswordString;

    //로그인 유지용, SharedPreferences에 아이디를 저장하기 위한 메서드들.


    public void saveUserNameToPreferences(String userName)
    {
        SharedPreferences pref = this.getSharedPreferences("loginPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        String loginKey = "login";

        editor.putString(loginKey, userName);
        editor.commit();
    }


    //SharedPreferences 끝.



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        //자동로그인 구현.
        SharedPreferences pref = this.getSharedPreferences("loginPref", MODE_PRIVATE);
        if(!pref.getString("login","").equals(""))
        {

        }

        //

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try
                {
                    userIdString = userId.getText().toString();
                    userPasswordString = userPassword.getText().toString();
                }
                catch (NullPointerException e)
                {
                    Log.e("err",e.getMessage());
                }

                loginDB lDB = new loginDB();
                lDB.execute();
            }
        });

    }

    public class loginDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "u_id=" + userIdString + "&u_pw=" + userPasswordString + "";
            Log.e("POST",param);
            try {
            /* 서버연결 */
                URL url = new URL(
                        "http://ty79450.vps.phps.kr/login.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

            /* 안드로이드 -> 서버 파라메터값 전달 */
                OutputStream outs = conn.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();
                outs.close();

            /* 서버 -> 안드로이드 파라메터값 전달 */
                InputStream is = null;
                BufferedReader in = null;
                data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while ( ( line = in.readLine() ) != null )
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();

                /* 서버에서 응답 */
                Log.e("RECV DATA",data);

                if(data.equals("0"))
                {
                    Log.e("RESULT","성공적으로 처리되었습니다!");
                }
                else
                {
                    Log.e("RESULT","에러 발생! ERRCODE = " + data);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(data.equals("아이디가 존재하지 않거나 비밀번호가 일치하지 않습니다."))
            {
                alertBuilder
                        .setTitle("알림")
                        .setMessage("비밀번호가 일치하지 않습니다.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else if(data.equals(userIdString))
            {
                saveUserNameToPreferences(userIdString);

                Log.e("RESULT","성공적으로 처리되었습니다!");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("성공적으로 로그인되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(SignInActivity.this, MainMenu.class);
//                                startActivity(intent);
//                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
//            if(data.equals("1"))
//            {
//                Log.e("RESULT","성공적으로 처리되었습니다!");
//                alertBuilder
//                        .setTitle("알림")
//                        .setMessage("성공적으로 등록되었습니다!")
//                        .setCancelable(true)
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                Intent intent = new Intent(SignInActivity.this, MainMenu.class);
////                                startActivity(intent);
////                                finish();
//                            }
//                        });
//                AlertDialog dialog = alertBuilder.create();
//                dialog.show();
//            }
//            else if(data.equals("0"))
//            {
//                Log.e("RESULT","비밀번호가 일치하지 않습니다.");
//                alertBuilder
//                        .setTitle("알림")
//                        .setMessage("비밀번호가 일치하지 않습니다.")
//                        .setCancelable(true)
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //finish();
//                            }
//                        });
//                AlertDialog dialog = alertBuilder.create();
//                dialog.show();
//            }
//            else
//            {
//                Log.e("RESULT","에러 발생! ERRCODE = " + data);
//                alertBuilder
//                        .setTitle("알림")
//                        .setMessage("등록중 에러가 발생했습니다! errcode : "+ data)
//                        .setCancelable(true)
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //finish();
//                            }
//                        });
//                AlertDialog dialog = alertBuilder.create();
//                dialog.show();
//            }

        }
    }

}
