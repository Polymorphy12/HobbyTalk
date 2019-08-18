package com.example.sumin.myapplication;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;


/**
 * Created by Sumin on 2017-08-16.
 */

public class SignUpActivity extends AppCompatActivity{

    Button signUpButton;

    EditText userId;
    EditText userNickName;
    EditText userPassword;
    EditText userPasswordCheck;

    TextView passwordChecker;
    TextView passwordChecker2;

    EditText userBirthDate;
    EditText userEmail;

    String userIdString;
    String userNickNameString;
    String userPasswordString;
    String userPasswordCheckString;

    String userBirthDateString;
    String userEmailString;

    AlertDialog.Builder alertBuilder;

    protected InputFilter filter= new InputFilter() {

        public CharSequence filter(CharSequence source, int start, int end,

                                   Spanned dest, int dstart, int dend) {



            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");

            if (!ps.matcher(source).matches()) {

                return "";

            }

            return null;

        }

    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        userId = (EditText) findViewById(R.id.userId);

        userId.setFilters(new InputFilter[] {filter});


        userNickName = (EditText) findViewById(R.id.userNickname);
        userPassword = (EditText) findViewById(R.id.userPassword);
        userPasswordCheck = (EditText) findViewById(R.id.userPasswordCheck);

        final TextView passwordChecker = (TextView) findViewById(R.id.passwordChecker);
        final TextView passwordChecker2 = (TextView) findViewById(R.id.passwordChecker2);

        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() < 8)
                {
                    passwordChecker.setText("8자리 이상 입력해 주세요.");
                }
                else
                {
                    passwordChecker.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userPasswordCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                userPasswordString = userPassword.getText().toString();
                userPasswordCheckString = userPasswordCheck.getText().toString();

                if(!userPasswordString.equals(userPasswordCheckString))
                {
                    passwordChecker2.setText("비밀번호가 일치하지 않습니다.");

                }
                else
                {
                    passwordChecker2.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userBirthDate = (EditText) findViewById(R.id.userBirthDate);
        userEmail = (EditText) findViewById(R.id.userEmail);

        signUpButton = (Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userIdString = userId.getText().toString();
                userNickNameString = userNickName.getText().toString();
                userPasswordString = userPassword.getText().toString();
                userPasswordCheckString = userPasswordCheck.getText().toString();

                userBirthDateString = userBirthDate.getText().toString();
                userEmailString = userEmail.getText().toString();

                if(userIdString.equals(""))
                {
                    alertBuilder = new AlertDialog.Builder(SignUpActivity.this);
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("아이디를 입력해 주세요.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else if(userNickNameString.equals(""))
                {
                    alertBuilder = new AlertDialog.Builder(SignUpActivity.this);
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("대화명을 입력해 주세요.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else if(userPasswordString.equals(""))
                {
                    alertBuilder = new AlertDialog.Builder(SignUpActivity.this);
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("비밀번호를 입력해 주세요.")
                            .setCancelable(true)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else
                {
                    SignUpDB sdb = new SignUpDB();
                    sdb.execute();
                }

//                if(userPasswordString.equals(userPasswordCheckString))
//                {
//                    //패스워드 확인이 정상적으로 됨.
//                    SignUpDB sdb = new SignUpDB();
//                    sdb.execute();
//                }
//                else
//                {
//                    //패스워드확인이 불일치함.
//                    alertBuilder = new AlertDialog.Builder(SignUpActivity.this);
//                    alertBuilder
//                            .setTitle("알림")
//                            .setMessage("비밀번호가 일치하지 않습니다.")
//                            .setCancelable(true)
//                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            });
//                    AlertDialog dialog = alertBuilder.create();
//                    dialog.show();
//                }

            }
        });

    }


    public class SignUpDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            // 인풋 파라메터 값 생성
            String param = "u_id=" + userIdString +"&u_pw="+userPasswordString+"&u_pw_check="+userPasswordCheckString+"&u_nickname="+userNickNameString+"";

            Log.d("result", param);

            try{

                //서버 연결
                URL url = new URL("http://ty79450.vps.phps.kr/signUp.php");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.connect();


                //안드로이드에서 서버로 파라미터값 전달.
                OutputStream outs = connection.getOutputStream();
                outs.write(param.getBytes("UTF-8"));
                outs.flush();

                //서버 -> 안드로이드 파라미터값 전달.
                InputStream is = null ; // is == i(nput)s(tream)
                BufferedReader in = null;
                data = "";

                is = connection.getInputStream();
                in = new BufferedReader(new InputStreamReader(is), 8*1024);
                String line = null;
                StringBuffer buff = new StringBuffer();
                while((line = in.readLine()) != null)
                {
                    buff.append(line + "\n");
                }
                data = buff.toString().trim();
                Log.e("RECV DATA", data);
//
//                if(data.equals("insert success"))
//                {
//                    Log.d("result", "성공적으로 처리 됨.");
//                }
//                else
//                {
//                    Log.d("result", "에러 발생 !! ERRCODE = " +data);
//                }

                if(data.equals("0"))
                {
                    Log.e("result", "성공적으로 처리 됨.");
                }
                else if(data.equals("이미 존재하는 아이디가 있습니다."))
                {

                }
                else
                {
                    Log.d("result", "에러 발생 !! ERRCODE = " +data);
                }

            }catch(MalformedURLException e)
            {
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.e("RECV DATA",data);

            alertBuilder = new AlertDialog.Builder(SignUpActivity.this);

            if(data.equals("이미 존재하는 아이디가 있습니다."))
            {
                alertBuilder
                        .setTitle("알림")
                        .setMessage(data + " 다시 입력해 주세요.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = alertBuilder.create();

                dialog.show();
            }
            else if(data.equals("비밀번호가 일치하지 않습니다."))
            {
                alertBuilder
                        .setTitle("알림")
                        .setMessage(data + " 다시 확인 해주세요.")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog dialog = alertBuilder.create();

                dialog.show();
            }
            else if(data.equals("0"))
            {
                Log.e("RESULT","성공적으로 처리되었습니다!");
                alertBuilder
                        .setTitle("알림")
                        .setMessage("성공적으로 등록되었습니다!")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
            else
            {
                Log.e("RESULT","에러 발생! ERRCODE = " + data);
                alertBuilder
                        .setTitle("알림")
                        .setMessage("등록중 에러가 발생했습니다! errcode : "+ data)
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = alertBuilder.create();

                dialog.show();
            }

        }
    }
}
