package com.example.sumin.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sumin on 2017-11-03.
 */

public class CreateClosedChatRoomActivity extends AppCompatActivity
{

    //navigation drawer 세팅에 필요한 것들.

    TextView createChatRoomButton;

    Toolbar myToolbar;

    SearchToInviteAdapter adapter;
    ListView listView;

    String jsonObjectData; // 이 안에 ItemArray를 담
    // 을 것이다.

    //navigation drawer 끝.

    String myUserId;
    String myUserName;
    String roomName="";
    String roomId;




    private void getListViewItemsFromJSON()
    {

        //어댑터에 있는 모든 아이템 삭제 해주고.
        for(int i = adapter.getListViewItemList().size()-1; i >=0; i-- )
        {
            adapter.removeItem(i);
        }

        SharedPreferences pref = getSharedPreferences("itemArrayJSONPref", MODE_PRIVATE);
        jsonObjectData = pref.getString("itemArrayJSON", "");
        Log.d("추가", "됐음, " + jsonObjectData);
        try
        {
            JSONArray jsonArray = new JSONArray(jsonObjectData);
            for(int i = 0; i <jsonArray.length(); i++)
            {
                JSONObject user = jsonArray.getJSONObject(i);
                SearchToInviteItem item = new SearchToInviteItem();

                item.setUserName(user.getString("nickname"));
                item.setUserId(user.getString("user_id"));
                item.setUserProfileURL(user.getString("profileImage"));

                Log.e("RECV", myUserId +" " + item.getUserProfileURL());

                if(item.getUserId().equals(myUserId))
                {

                }
                else
                {
                    adapter.addItem(item);
                    Log.d("RECV", "온크리에이트, 이미지 :" +item.getUserProfileURL());
                    adapter.notifyDataSetChanged();
                }
            }
        }
        catch(JSONException e)
        {

        }
    }

    private String createRoomName()
    {
        String tempName="";

        for(int i = 0; i < adapter.getListViewItemList().size(); i++ )
        {
            SearchToInviteItem tempItem = (SearchToInviteItem) adapter.getItem(i);

            if(tempItem.isChecked)
            {
                if(tempName.equals(""))
                    tempName+= tempItem.getUserName();
                else
                    tempName+= ", "+tempItem.getUserName();
            }
        }


        return tempName;
    }

    private String participantList()
    {
        String tempList="";

        for(int i = 0; i < adapter.getListViewItemList().size(); i++ )
        {
            SearchToInviteItem tempItem = (SearchToInviteItem) adapter.getItem(i);

            if(tempItem.isChecked)
            {
                if(tempList.equals(""))
                    tempList+= tempItem.getUserId();
                else
                    tempList+= ", "+tempItem.getUserId();
            }
        }


        return tempList;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_closed_room_activity);

        Toast.makeText(getApplicationContext(), "초대하자", Toast.LENGTH_SHORT).show();

        myToolbar = (Toolbar) findViewById(R.id.invite_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //리스트뷰 세팅.

        listView = (ListView) findViewById(R.id.search_to_invite_listview);
        adapter = new SearchToInviteAdapter();

        listView.setAdapter(adapter);

        final SharedPreferences pref = getSharedPreferences("loginPref", MODE_PRIVATE);
        myUserId = pref.getString("loginUserName", "");
        myUserName = pref.getString("loginNickname", "");


        //리스트뷰 세팅 끝.

        FriendListUpdateDB fdb = new FriendListUpdateDB();
        fdb.execute();


        ImageButton finishButton = (ImageButton) findViewById(R.id.cc_back_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //textview 클릭시, 인텐트 넘어가도록 만들기.
        createChatRoomButton = (TextView) findViewById(R.id.create_chatroom_button);
        createChatRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                roomId = myUserId + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                //Toast.makeText(getApplicationContext(), roomName, Toast.LENGTH_SHORT).show();

                roomName = createRoomName();

                if (!roomName.equals("")) {

                    //참여자 업데이트 쓰레드를 돌린다.


                    //Toast.makeText(getApplicationContext(), roomId, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateClosedChatRoomActivity.this, ChatRoomActivity.class);
                    intent.putExtra("callNum", 2);
                    intent.putExtra("myUserId", myUserId);
                    intent.putExtra("myUserName", myUserName);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("roomName", roomName);
                    intent.putExtra("participants", participantList());
                    // 방을 만들 때에는 forresult 코드가 0.

                    Log.d("인텐트 들어갔니", "응 인텐트 넘기고 있어");
                    startActivity(intent);
                    finish();
                }

            }
        });
    }



    public class FriendListUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "user_id="+myUserId;
            Log.e("POST",param);
            try {
            /* 서버연결 */
                Log.e("POST",param);
                URL url = new URL(
                        "http://ty79450.vps.phps.kr/updateListView.php");
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


                //이대로 jsonObjectData를 S.P.에 저장한다.
                SharedPreferences pref = getSharedPreferences("itemArrayJSONPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("itemArrayJSON", data);
                editor.apply();
                Log.d("아싸", "쓰레드");

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

            getListViewItemsFromJSON();
        }
    }




}
