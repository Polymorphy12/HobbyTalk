package com.example.sumin.myapplication;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sumin on 2017-09-02.
 */

public class UserSearchActivity extends AppCompatActivity {

    AlertDialog.Builder alertBuilder;

    EditText friendSearch;


    String searchId;
    boolean isfriend = false;

    String myUserId;
    String FriendUserId;



    //리스트뷰 세팅에 필요한 것들.
    ListView listView;

    FriendListViewAdapter adapter;
    ArrayList<FriendListViewItem> filteredItemArray = new ArrayList<FriendListViewItem>();
    ArrayList<FriendListViewItem> itemArray = new ArrayList<FriendListViewItem>();

    String jsonObjectData; // 이 안에 ItemArray를 담을 것이다.
    //리스트뷰 세팅 끝.

    boolean flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_search);

        //내 아이디 세팅.
        final SharedPreferences pref = getSharedPreferences("loginPref", MODE_PRIVATE);
        myUserId = pref.getString("loginUserName","");

        //리스트뷰 세팅.
        listView = (ListView) findViewById(R.id.friendSearchListView);
        adapter = new FriendListViewAdapter();

        listView.setAdapter(adapter);

        filteredItemArray = adapter.getFilteredItemList();
        itemArray = adapter.getListViewItemList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                flag = false;

                final FriendListViewItem item = (FriendListViewItem) parent.getItemAtPosition(position);


                if(isfriend)
                {
                    alertBuilder = new AlertDialog.Builder(UserSearchActivity.this);
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("이미 친구사이입니다.")
                            .setCancelable(true)

                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }
                else if(item.getId().equals(myUserId))
                {
                    alertBuilder = new AlertDialog.Builder(UserSearchActivity.this);
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("자기 자신은 친구추가 할 수 없습니다.")
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
                    alertBuilder = new AlertDialog.Builder(UserSearchActivity.this);
                    alertBuilder
                            .setTitle("알림")
                            .setMessage("친구 추가 하시겠습니까?")
                            .setCancelable(true)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    isfriend = true;
                                    FriendUserId = item.getId();
                                    RelationshipUpdateDB reldb = new RelationshipUpdateDB();
                                    reldb.execute();
                                }
                            });
                    AlertDialog dialog = alertBuilder.create();
                    dialog.show();
                }


            }
        });

        //리스트뷰 세팅 끝.

        friendSearch = (EditText) findViewById(R.id.friendSearch);

        friendSearch.setImeOptions(EditorInfo.IME_ACTION_DONE);
        friendSearch.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_DONE)
                {
                    //Toast.makeText(getApplicationContext(),"검색 성공", Toast.LENGTH_SHORT).show();
                    searchId = friendSearch.getText().toString();

                    FriendListUpdateDB FLUDb = new FriendListUpdateDB();
                    FLUDb.execute();
                    return true;
                }

                return false;
            }
        });


    }



    public class FriendListUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "search_id="+searchId + "&my_id="+myUserId;
            Log.e("POST",param);
            try {
            /* 서버연결 */
                Log.e("POST",param);
                URL url = new URL(
                        "http://ty79450.vps.phps.kr/searchUsers.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

            /* 안드로이드 -> 서버 파라메터값 전달 */
                Log.e("POST",param);
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
                Log.e("POST",data);
                Log.e("RECV DATA",data);


//                //이대로 jsonObjectData를 S.P.에 저장한다.
//                SharedPreferences pref = getSharedPreferences("itemArrayJSONPref", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("itemArrayJSON", data);
//                editor.apply();
                //Log.d("아싸", "쓰레드");

                if(data.equals("0"))
                {

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

            try
            {
                JSONArray jsonArray = new JSONArray(data);

                if(jsonArray.getJSONObject(0).getString("user_id").equals(""))
                {

                        alertBuilder = new AlertDialog.Builder(UserSearchActivity.this);
                        alertBuilder
                                .setTitle("알림")
                                .setMessage("검색 결과가 없었습니다..")
                                .setCancelable(true)

                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog dialog = alertBuilder.create();
                        dialog.show();

                }


                else if(jsonArray.length() != 0)
                {
                    //어댑터에 있는 모든 아이템 삭제 해주고.
                    for(int i = adapter.getListViewItemList().size()-1; i >=0; i-- )
                    {
                        adapter.removeItem(i);
                    }

                    for(int i = 0; i <jsonArray.length(); i++)
                    {
                        JSONObject user = jsonArray.getJSONObject(i);
                        FriendListViewItem item = new FriendListViewItem();

                        item.setName(user.getString("nickname"));
                        Log.d("아이템 추가" +i, item.getName());
                        item.setStatusMessage(user.getString("statusMessage"));
                        item.setId(user.getString("user_id"));
                        item.setProfileImageUri(user.getString("profileImage"));
                        if(jsonArray.getJSONObject(0).getBoolean("isFriend"))
                        {
                            isfriend = true;
                        }
                        else
                        {
                            isfriend = false;
                        }

                        adapter.addItem(item);
                        Log.d("아싸", "온크리에이트");
                        adapter.notifyDataSetChanged();
                    }
                }

            }
            catch(JSONException e)
            {

            }
        }
    }


    public class RelationshipUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "my_id="+myUserId +"&f_id="+FriendUserId;
            Log.e("POST",param);
            try {
            /* 서버연결 */
                Log.e("POST",param);
                URL url = new URL(
                        "http://ty79450.vps.phps.kr/relationshipUpdate.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();

            /* 안드로이드 -> 서버 파라메터값 전달 */
                Log.e("POST",param);
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
                Log.e("POST",data);
                Log.e("RECV DATA",data);


//                //이대로 jsonObjectData를 S.P.에 저장한다.
//                SharedPreferences pref = getSharedPreferences("itemArrayJSONPref", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("itemArrayJSON", data);
//                editor.apply();
                //Log.d("아싸", "쓰레드");

                if(data.equals("0"))
                {

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



        }
    }

}
