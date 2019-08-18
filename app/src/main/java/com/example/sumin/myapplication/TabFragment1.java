package com.example.sumin.myapplication;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

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

import static android.content.Context.MODE_PRIVATE;



/**
 * Created by Sumin on 2017-09-01.
 */

public class TabFragment1 extends Fragment{

    FloatingActionButton fab;

    //Button profileIntentButton;

    String myUserId;

    //리스트뷰 세팅에 필요한 것들.
    ListView listView;

    FriendListViewAdapter adapter;
    ArrayList<FriendListViewItem> filteredItemArray = new ArrayList<FriendListViewItem>();
    ArrayList<FriendListViewItem> itemArray = new ArrayList<FriendListViewItem>();

    String jsonObjectData; // 이 안에 ItemArray를 담
    // 을 것이다.
    //리스트뷰 세팅 끝.

    private void getListViewItemsFromJSON()
    {

        //어댑터에 있는 모든 아이템 삭제 해주고.
        for(int i = adapter.getListViewItemList().size()-1; i >=0; i-- )
        {
            adapter.removeItem(i);
        }

        SharedPreferences pref = getActivity().getSharedPreferences("itemArrayJSONPref", MODE_PRIVATE);
        jsonObjectData = pref.getString("itemArrayJSON", "");
        Log.d("추가", "됐음, " + jsonObjectData);
        try
        {
            JSONArray jsonArray = new JSONArray(jsonObjectData);
            for(int i = 0; i <jsonArray.length(); i++)
            {
                JSONObject user = jsonArray.getJSONObject(i);
                FriendListViewItem item = new FriendListViewItem();

                item.setName(user.getString("nickname"));
                Log.d("아이템 추가" +i, item.getName());
                item.setStatusMessage(user.getString("statusMessage"));
                item.setId(user.getString("user_id"));
                item.setProfileImageUri(user.getString("profileImage"));

                Log.e("RECV", myUserId +" " + item.getProfileImageUri());

                if(item.getId().equals(myUserId))
                {
                    Log.e("RECV", "들어왔눈뎅");
                    item.setIsThatMe(true);
                    Log.e("RECV", item.IsThatMe()+", I'm what I am");
                    SharedPreferences pref2 = getActivity().getSharedPreferences("myProfileImage", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref2.edit();

                    editor.putString("myProfileImage",item.getProfileImageUri());
                    editor.apply();

                    Log.e("RECV", item.getProfileImageUri()+", I'm what I am");
                }

                adapter.addItem(item);
                Log.d("RECV", "온크리에이트, 이미지 :" +item.getProfileImageUri());
                adapter.notifyDataSetChanged();
            }
        }
        catch(JSONException e)
        {

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences pref = getActivity().getSharedPreferences("loginPref", MODE_PRIVATE);
        if(pref.getString("loginUserName","").equals("") && AccessToken.getCurrentAccessToken() == null )
        {
            Log.d("뭐니", pref.getString("loginUserName","") + "aa");
            getActivity().finish();
        }

        FriendListUpdateDB fdb = new FriendListUpdateDB();
        fdb.execute();
    }

    //여기터 실질적인 프래그먼트의 뷰들 관리.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity() , UserSearchActivity.class);
                startActivity(intent);
            }
        });

        final SharedPreferences pref = getActivity().getSharedPreferences("loginPref", MODE_PRIVATE);
        myUserId = pref.getString("loginUserName","");

//        profileIntentButton = (Button) view.findViewById(R.id.profile_intent);
//
//        profileIntentButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent  = new Intent(getActivity() , ProfileSettingsActivity.class);
//                startActivity(intent);
//            }
//        });



        FirebaseMessaging.getInstance().subscribeToTopic("chat");
        Log.d("token", "온크리에이트");
        FirebaseInstanceId.getInstance().getToken();



        //리스트뷰 세팅.
        listView = (ListView) view.findViewById(R.id.friendListView);
        adapter = new FriendListViewAdapter();

        listView.setAdapter(adapter);

        filteredItemArray = adapter.getFilteredItemList();
        itemArray = adapter.getListViewItemList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FriendListViewItem item = (FriendListViewItem) parent.getItemAtPosition(position);

                if(item.isThatMe)
                {
                    Intent intent  = new Intent(getActivity() , ProfileSettingsActivity.class);
                    startActivity(intent);
                }
                else
                {
//                    Intent intent  = new Intent(getActivity() , ChatRoomActivity.class);
//                    startActivityForResult(intent, 2);
                }
            }
        });

        //getListViewItemsFromJSON();

        //친구 목록은 php에서 sql 쿼리를 집어넣은 후 JSONObject형태로 묶어서 안드로이드로 보낸다.
        //안드로이드에서 JSONObject를 받아서 JSONArray를 이용해 리스트뷰에 표현한다.

        return view;
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
                SharedPreferences pref = getActivity().getSharedPreferences("itemArrayJSONPref", MODE_PRIVATE);
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
