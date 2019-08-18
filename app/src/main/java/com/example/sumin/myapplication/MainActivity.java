package com.example.sumin.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity{


    //ㅋㅋ arraylist로 친구목록을 만들 수 있겠구나. 내가 어떻게 그것조차 생각못했지??
    // LOL, Maybe I can make a list of friends using "ArrayList". How couldn't I even think about it?

    AlertDialog.Builder alertBuilder;

    private Button btnRegister;
    private Button btnLogin;


    EditText userId;
    EditText userPassword;

    String userIdString;
    String userPasswordString;

    private CallbackManager mCallbackManager;
    private AccessToken mToken = null;



    //로그인 유지용, SharedPreferences에 아이디를 저장하기 위한 메서드들.


    public void saveUserNameToPreferences(String userName, String userNickname, String statusMessage)
    {
        SharedPreferences pref = this.getSharedPreferences("loginPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("loginUserName", userName);
        editor.putString("loginNickname", userNickname);
        editor.putString("loginStatusMessage", statusMessage);

        editor.commit();
    }


    //SharedPreferences 끝.

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.sign_in);

        //자동로그인 구현.
        SharedPreferences pref = this.getSharedPreferences("loginPref", MODE_PRIVATE);
        Log.d("뭐니", pref.getString("loginUserName","") + "aa");
        if(!pref.getString("loginUserName","").equals("") )
        {
            Log.d("뭐니", pref.getString("loginUserName","") + "aa");
            Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
            startActivity(intent);
            finish();
        }



        //

        btnRegister = (Button) findViewById(R.id.btnRegister);

        userId = (EditText) findViewById(R.id.etEmail);
        userPassword = (EditText) findViewById(R.id.etPassword);

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

                MainActivity.loginDB lDB = new MainActivity.loginDB();
                lDB.execute();
            }
        });



        mCallbackManager = CallbackManager.Factory.create();
        mToken = AccessToken.getCurrentAccessToken();

        if(mToken == null){
            LoginButton loginButton = (LoginButton)findViewById(R.id.fbBtnLogin);
            if(loginButton != null) {
                loginButton.setReadPermissions("public_profile", "user_friends", "email");
                loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        GraphRequest request;
                        mToken = loginResult.getAccessToken();
                        request = GraphRequest.newMeRequest(mToken, jsonObjectCallback);
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday,cover");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {}

                    @Override
                    public void onError(FacebookException error) {}
                });
            }
        }else{
            GraphRequest request;
            request = GraphRequest.newMeRequest(mToken, jsonObjectCallback);
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender,birthday,cover");
            request.setParameters(parameters);
            request.executeAsync();
        }




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    GraphRequest.GraphJSONObjectCallback jsonObjectCallback = new GraphRequest.GraphJSONObjectCallback() {
        @Override
        public void onCompleted(final JSONObject user, GraphResponse response) {
            if (response.getError() == null) {
                setResult(RESULT_OK);

                Intent i = new Intent(MainActivity.this, MainPageActivity.class);
                startActivity(i);
                finish();
            }
        }
    };




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
                    Log.e("RESULT","성공적으로 처리되었습니다!!!!"+ data);
                    Log.e("RESULT","성공적으로 처리되었습니다!!!" + data);
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



            Log.e("what", data);
            if(data.equals("존재하지 않는 아이디입니다."))
            {
                Log.e("what", data);
                alertBuilder = new AlertDialog.Builder(MainActivity.this);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("존재하지 않는 아이디입니다.")
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
            else if(data.equals("비밀번호가 일치하지 않습니다."))
            {
                alertBuilder = new AlertDialog.Builder(MainActivity.this);
                Log.e("RESULT","비밀번호가 일치하지 않습니다.");
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
            else
            {
                String[] temp = data.split(":::");
                Log.d("스플릿", temp[0]);


                if(temp.length == 2)
                    saveUserNameToPreferences(userIdString, temp[0], temp[1]);
                else if(temp.length == 1)
                    saveUserNameToPreferences(userIdString, temp[0], "");

                alertBuilder = new AlertDialog.Builder(MainActivity.this);
                Log.e("RESULT","성공적으로 처리되었습니다!!!!!!!!!!" + data);
                Log.e("what 123132", data);


                alertBuilder
                        .setTitle("알림")
                        .setMessage("성공적으로 등록되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, MainPageActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }

        }
    }

}




//        extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//}
