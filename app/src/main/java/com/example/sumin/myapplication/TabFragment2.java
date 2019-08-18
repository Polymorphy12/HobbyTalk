package com.example.sumin.myapplication;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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

public class TabFragment2 extends Fragment{

    Button openChatButton;

    //리스트뷰 세팅에 필요한 것들.
    ListView listView;
    ArrayList<ChatRoomListViewItem> filteredItemArray = new ArrayList<ChatRoomListViewItem>();
    ArrayList<ChatRoomListViewItem> itemArray = new ArrayList<ChatRoomListViewItem>();
    ChatRoomListViewAdapter adapter;

    String jsonObjectData; // 이 안에 ItemArray를 담
    // 을 것이다.
    //리스트뷰 세팅 끝.

    String roomName="";
    String roomId;
    String myUserId;
    String myUserName;

    FloatingActionButton createOpenChatBtn;



    private void getListViewItemsFromJSON()
    {

        //어댑터에 있는 모든 아이템 삭제 해주고.
        for(int i = adapter.getListViewItemList().size()-1; i >=0; i-- )
        {
            adapter.removeItem(i);
        }

        SharedPreferences pref = getActivity().getSharedPreferences("chatRoomItemArrayJSONPref", MODE_PRIVATE);
        jsonObjectData = pref.getString("itemArrayJSON", "");
        Log.d("추가", "됐음, " + jsonObjectData);
        try
        {
            JSONArray jsonArray = new JSONArray(jsonObjectData);
            for(int i = jsonArray.length()-1; i >=0; i--)
            {
                JSONObject user = jsonArray.getJSONObject(i);
                ChatRoomListViewItem item = new ChatRoomListViewItem();

                item.setChatRoomId(user.getString("room_id"));
                Log.d("아이템 추가" +i, item.getChatRoomId());
                item.setChatRoomTitle(user.getString("room_name"));
                //item.setProfileImageUri(user.getString("profileImage"));

                //Log.e("RECV", myUserId +" " + item.getProfileImageUri());


                adapter.addItem(item);
                Log.d("RECV", "온크리에이트, 이미지 :");
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

        OpenChatListUpdateDB odb = new OpenChatListUpdateDB();
        odb.execute();
    }

    //여기터 실질적인 프래그먼트의 뷰들 관리.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        //리스트뷰 세팅.
        listView = (ListView) view.findViewById(R.id.chatRoomListView);
        adapter = new ChatRoomListViewAdapter();

        listView.setAdapter(adapter);

        filteredItemArray = adapter.getFilteredItemList();
        itemArray = adapter.getListViewItemList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoomListViewItem item = (ChatRoomListViewItem) parent.getItemAtPosition(position);

                final SharedPreferences pref = getActivity().getSharedPreferences("loginPref", MODE_PRIVATE);
                myUserId = pref.getString("loginUserName", "");
                myUserName = pref.getString("loginNickname", "");
                roomId = item.getChatRoomId();
                roomName = item.getChatRoomTitle();

                Intent intent  = new Intent(getActivity() , ChatRoomActivity.class);
                intent.putExtra("callNum",1);
                intent.putExtra("myUserId", myUserId);
                intent.putExtra("myUserName", myUserName);
                intent.putExtra("roomId", roomId);
                intent.putExtra("roomName", roomName);
                startActivity(intent);
            }
        });

        //리스트 뷰 세팅 끝.

//        openChatButton = (Button) view.findViewById(R.id.openChatButton);
//        openChatButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent  = new Intent(getActivity() , ChatRoomActivity.class);
//                startActivity(intent);
//            }
//        });

        createOpenChatBtn = (FloatingActionButton) view.findViewById(R.id.fab2);
        createOpenChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(getActivity() , CreateOpenChatRoomActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


    public class OpenChatListUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "";
            Log.e("POST",param);
            try {

            /* 서버연결 */
                Log.e("POST",param);
                URL url = new URL(
                        "http://ty79450.vps.phps.kr/updateOpenChatRooms.php");
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
                SharedPreferences pref = getActivity().getSharedPreferences("chatRoomItemArrayJSONPref", MODE_PRIVATE);
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
