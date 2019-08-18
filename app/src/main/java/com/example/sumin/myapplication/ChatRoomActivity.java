package com.example.sumin.myapplication;

import android.*;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Sumin on 2017-09-02.
 */

public class ChatRoomActivity extends AppCompatActivity {

    String myUserId;
    String myNickname;
    String roomId;
    String roomName;
    int callNum;


    //사진 찍을 때 필요한 요소들

    boolean isAlbum = false;
    boolean sendedPhoto = false;

    private static final int PICK_FROM_CAMERA = 6;
    private static final int PICK_FROM_ALBUM = 7;
    private static final int CROP_FROM_CAMERA = 8;

    //필터 사진 액티비티로 startActivityForResult 이동하기 위한 private static final int 변수.
    private static final int FILTERED_PHOTO = 100;


    private Uri photoUri;
    private String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;

    private String mCurrentPhotoPath;
    private String fileName;

    //사진찍을 때 필요한 요소들 끝.


    //내가 만든 방인지 확인하는 boolean.
    boolean justCreated = false;

    //true 일시, 나 나간다
    boolean exit = false;

    String sendmsg;

    // private static int port = 5001;
    // private static final String ipText = "192.168.0.7"; // IP지정으로 사용시에 쓸 코드
    String streammsg = "";
    TextView showText;
    //Button connectBtn;
    Button Button_send;

    EditText editText_massage;
    Handler msghandler;

    SocketClient client;
    ReceiveThread receive;
    SendThread send;
    Socket socket;

    PipedInputStream sendstream = null;
    PipedOutputStream receivestream = null;

    LinkedList<SocketClient> threadList;


    //리스트뷰 세팅에 필요한 것들.
    ListView listView;

    ChatListViewAdapter2 adapter;
    ArrayList<ListContents> itemArray = new ArrayList<ListContents>();

    String jsonObjectData; // 이 안에 ItemArray를 담
    // 을 것이다.
    //리스트뷰 세팅 끝.


    //navigation drawer 세팅에 필요한 것들.

    Toolbar myToolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ImageButton drawerRightButton;

    DrawerListViewAdapter drawerListViewAdapter;
    ListView participatingUsers;

    //일반채팅일 때 자동 업데이트하기.
    String toUpdateParticipant;

    //navigation drawer 끝.

    private void getListViewItemsFromJSON()
    {
        Log.d("세팅까지 왔니", "겟 리스트뷰 아이템즈 프롬 제이슨");

        SharedPreferences pref = getSharedPreferences("chatArrayJSONPref", MODE_PRIVATE);
        jsonObjectData = pref.getString("chatArrayJSON", "");
        Log.d("챗 리스트 추가", "됐음, " + jsonObjectData);
        try
        {
            JSONArray jsonArray = new JSONArray(jsonObjectData);
            for(int i = 0; i <jsonArray.length(); i++)
            {
                JSONObject user = jsonArray.getJSONObject(i);

                String uId = user.getString("user_id");
                Log.d("챗 리스트 추가", "ID 됐음, " + uId);
                String nName = user.getString("nickname");
                Log.d("챗 리스트 추가", "닉네임 됐음, " + nName);
                String msg = user.getString("chat_content");
                Log.d("챗 리스트 추가", "메세지 됐음, " + msg);
                String profImageURL = user.getString("profile_image_url");
                Log.d("챗 리스트 추가", "프로필이미지 됐음, " + profImageURL);
                String time = user.getString("time");
                Log.d("챗 리스트 추가", "시간 됐음, " + time);
                String type = user.getString("type");
                Log.d("챗 리스트 추가", "타입 됐음, " + type);

                if(type.equals("text"))
                {
                    if(uId.equals(myUserId))
                    {
                        adapter.add(uId,nName, msg, profImageURL, time, 1);
                    }
                    else
                    {
                        adapter.add(uId,nName, msg, profImageURL, time, 0);
                    }
                }
                else if(type.equals("photo"))
                {
                    if(uId.equals(myUserId))
                    {
                        adapter.add(uId,nName, msg, profImageURL, time, 4);
                    }
                    else
                    {
                        adapter.add(uId,nName, msg, profImageURL, time, 3);
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
        catch(JSONException e)
        {

        }
    }


    /*
    onCreate 에서는
    1. 닉네임과 아이디를 S.P.에서 가져온다.
    2. 인텐트를 통해 방 ID와 방 이름을 가져온다.
    3. 채팅 내용 리스트뷰를 세팅한다.
    4. 핸들러의 기능을 세팅한다.
    (구분자 쪼개기를 이용해 닉네임, 대화내용, 유저 아이디, 프로필 이미지 주소를 가져온다.
    그리고 listview 업데이트를 한다.

    5. 전송버튼 클릭 시 이벤트를 설정한다. (send Thread를 시작한다. )
    6. ChatListUpdateDB 쓰레드를 쓴다. (이것은 액티비티에 그대로 둬야 할 것 같다.)
    7. SocketClient 쓰레드를 쓴다. (이 때, SocketClient 안에서 receive 쓰레드가 시작된다.)
    */


    /*

    //navigation drawer 세팅에 필요한 것들.

    Toolbar myToolbar;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    ImageButton drawerRightButton;

    //navigation drawer 끝.

    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room_practice);

        //툴바 및 navigation drawer 세팅

        myToolbar = (Toolbar) findViewById(R.id.chat_room_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.chat_room_drawer);

        participatingUsers = (ListView) findViewById(R.id.participating_users);
        drawerListViewAdapter = new DrawerListViewAdapter();
        participatingUsers.setAdapter(drawerListViewAdapter);

        drawerRightButton = (ImageButton) findViewById(R.id.chatroom_drawer_button);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        drawerRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(mDrawerLayout.isDrawerOpen(navigationView))
                {
                    mDrawerLayout.closeDrawer(navigationView);
                }
                else if(!mDrawerLayout.isDrawerOpen(navigationView))
                {
                    //채팅방 참여자 업데이트.
                    ParticipantsUpdateDB pdb = new ParticipantsUpdateDB();
                    pdb.execute();

                    mDrawerLayout.openDrawer(navigationView);
                }
            }
        });

        ImageButton finishButton = (ImageButton) findViewById(R.id.chatroom_back_button);
        finishButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        ImageButton exitButton = (ImageButton) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                exit = true;

                //flag
                send = new SendThread(socket);
                send.start();

                finish();
            }
        });

        //툴바 및 navigation drawer 세팅 끝.


        //내 닉네임 세팅.

        //내 아이디 세팅.
        final SharedPreferences pref = getSharedPreferences("loginPref", MODE_PRIVATE);
        myNickname = pref.getString("loginNickname","");
        myUserId = pref.getString("loginUserName", "");

        //내 닉네임 세팅 끝.

        editText_massage = (EditText) findViewById(R.id.editText_massage);
        Button_send = (Button) findViewById(R.id.Button_send);

        Intent intent = getIntent();
        callNum = intent.getIntExtra("callNum",-1);
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");

        TextView roomNameTextView = (TextView) findViewById(R.id.room_name);
        roomNameTextView.setText(roomName);

        if(callNum == 0)
        {
            //callNum이 0일 때는 open, callNum이 2일 때는 closed.
            justCreated = true;
        }
        else if(callNum == 2)
        {
            //callNum이 0일 때는 open, callNum이 2일 때는 closed.
            justCreated = true;
            toUpdateParticipant = intent.getStringExtra("participants");
        }
        else
        {
            justCreated= false;
        }

        //리스트뷰 세팅.

        Log.d("세팅까지 왔니", "응");

        listView = (ListView) findViewById(R.id.chatListView);
        Log.d("세팅까지 왔니", "리스트뷰 findviewbyid");
        adapter = new ChatListViewAdapter2();
        Log.d("세팅까지 왔니", "어댑터 세팅");

        listView.setAdapter(adapter);
        Log.d("세팅까지 왔니", "셋 어댑터");
        itemArray = adapter.getListViewItemList();
        Log.d("세팅까지 왔니", "어레이 리스트 받고.");


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListContents item =  (ListContents) adapter.getItem(position);

                Toast.makeText(getApplicationContext(), item.msg , Toast.LENGTH_SHORT).show();
            }
        });


//        //서비스 세팅.
//
//
//        //서비스 세팅 끝.


        //채팅 내용 업데이트..
        ChatListUpdateDB fdb = new ChatListUpdateDB();
        fdb.execute();


        //쓰레드 추가하고
        threadList = new LinkedList<ChatRoomActivity.SocketClient>();

        //Client 연결부
        client = new SocketClient("115.71.232.80", "5001");
        threadList.add(client);
        client.start();

        //Toast.makeText(getApplicationContext(), "채팅을 시작한다", Toast.LENGTH_SHORT).show();
        //클라이언트 시작.

        // ReceiveThread를통해서 받은 메세지를 Handler로 MainThread에서 처리(외부Thread에서는 UI변경이불가)
        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) {
                if (hdmsg.what == 1111) {
                    //showText.append(hdmsg.obj.toString() + "\n");
                    String msg = hdmsg.obj.toString();

                    Log.d("챗 받은거 추가", "@@@@@@@@@" + msg);

                    String[] array;
                    array = msg.split(":::");

                    if(array.length == 5)
                    {
                        String tempNickName = array[0];
                        msg = array[1];
                        String tempUserId = array[2];
                        String profileImage = array[3];
                        //timeFlag
                        String time = array[4];

                        if(tempUserId.equals(myUserId))
                        {
                            adapter.add(tempUserId,tempNickName,msg,profileImage,time,1);
                            Log.d("챗 받은거 추가", "내가 보낸거 @@@@@@@@@" + msg);
                        }
                        else
                        {
                            adapter.add(tempUserId,tempNickName,msg,profileImage,time,0);
                            Log.d("챗 받은거 추가", "남이 보낸거 @@@@@@@@@" + msg);
                        }
                    }
                    else if(array.length == 6)
                    {
                        String tempNickName = array[0];
                        //Log.d("챗 받은거 추가", "@@@@@@@@@" + tempNickName);
                        msg = array[1];
                        //Log.d("챗 받은거 추가", "@@@@@@@@@" + msg);
                        String tempUserId = array[2];
                        //Log.d("챗 받은거 추가", "@@@@@@@@@" + tempUserId);
                        String profileImage = array[3];
                        //Log.d("챗 받은거 추가", "@@@@@@@@@" + profileImage);
                        //timeFlag
                        String time = array[4];

                        if(tempUserId.equals(myUserId))
                        {
                            adapter.add(tempUserId,tempNickName,msg,profileImage,time,4);
                            Log.d("챗 받은거 추가", "내가 보낸거 @@@@@@@@@" + msg);
                        }
                        else
                        {
                            adapter.add(tempUserId,tempNickName,msg,profileImage,time,3);
                            Log.d("챗 받은거 추가", "내가 보낸거 @@@@@@@@@" + msg);
                        }
                    }
                    else
                    {
                        adapter.add("","",msg,"","",2);
                    }

                    adapter.notifyDataSetChanged();
                }
            }
        };

        // 이미지 전송 클릭 이벤트
        ImageButton sendImageButton = (ImageButton) findViewById(R.id.sendImageButton);
        sendImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent imageFilterIntent = new Intent(ChatRoomActivity.this, ImageFilterActivity.class);
                startActivityForResult(imageFilterIntent, FILTERED_PHOTO);

//                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        takePhoto();
//                    }
//                };
//
//                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        goToAlbum();
//                    }
//                };
//
//                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                };
//
//                new AlertDialog.Builder(ChatRoomActivity.this)
//                        .setTitle("업로드할 이미지 선택")
//                        .setPositiveButton("취소", cancelListener)
//                        .setNeutralButton("앨범선택", albumListener)
//                        .setNegativeButton("사진촬영", cameraListener)
//                        .show();


            }
        });


        //전송 버튼 클릭 이벤트
        Button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //여기서 서비스를 시작하거나 불러와야 할 것 같다.

                //SendThread 시작
                if (!editText_massage.getText().toString().replaceAll(" ","").equals(""))
                {
                    send = new SendThread(socket);
                    send.start();

                    //시작후 edittext 초기화
                    editText_massage.setText("");
                }
            }
        });
    }


    //사진 찍기.

//
//    private void takePhoto() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File photoFile = null;
//        try
//        {
//            photoFile = createImageFile();  // 이미지 파일을  만들고.
//        }
//        catch (IOException e)
//        {
//            Toast.makeText(ChatRoomActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//            finish();
//            e.printStackTrace();
//        }
//        if (photoFile != null) {
//            photoUri = FileProvider.getUriForFile(ChatRoomActivity.this,
//                    "com.example.sumin.myapplication.provider", photoFile); // FileProvider에 파일을 넣어 URI에 넣어준 뒤
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            startActivityForResult(intent, PICK_FROM_CAMERA);   // 인텐트를 보내준다.
//        }
//    }
//
//    private File createImageFile() throws IOException
//    {
//        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
//        String imageFileName = myUserId +"_" + timeStamp + "_";
//        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
//        if (!storageDir.exists())
//        {
//            storageDir.mkdirs();
//        }
//        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//        mCurrentPhotoPath = /*"file:" +*/ image.getAbsolutePath();
//        fileName =image.getName();
//        Log.d("파일 경로 뭐니", mCurrentPhotoPath + " " + imageFileName);
//        return image;
//    }
//
//    private void goToAlbum()
//    {
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, PICK_FROM_ALBUM);
//    }
//
//    //갤러리 새로고침, ACTION_MEDIA_MOUNTED는 하나의 폴더,
//    //ACTION_MEDIA_FILE은 하나의 파일을 새로고침할 때 사용함.
//    private void galleryAddPic()
//    {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f =   new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
//    {
//        switch (requestCode)
//        {
//            case MULTIPLE_PERMISSIONS: {
//                if (grantResults.length > 0) {
//                    for (int i = 0; i < permissions.length; i++) {
//
////                      this.permissions[0] :  Manifest.permission.READ_EXTERNAL_STORAGE,
////                      this.permissions[1] :  Manifest.permission.WRITE_EXTERNAL_STORAGE,
////                      this.permissions[2] :  Manifest.permission.CAMERA
//
//
//
//
//                        if (permissions[i].equals(this.permissions[0])) {
//                            //권한이 승인되지 않았으면, 토스트를 띄운다. (If permission is not granted, show a Toast.)
//                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                                showNoPermissionToastAndFinish();
//                            }
//                        } else if (permissions[i].equals(this.permissions[1])) {
//                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                                showNoPermissionToastAndFinish();
//                            }
//                        } else if (permissions[i].equals(this.permissions[2])) {
//                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                                showNoPermissionToastAndFinish();
//                            }
//                        }
//                    }
//                } else {
//                    showNoPermissionToastAndFinish();
//                }
//                return;
//            }
//        }
//    }
//
//    private void showNoPermissionToastAndFinish() {
//        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
//        finish();
//    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다. + " + resultCode, Toast.LENGTH_SHORT).show();
            return;
        }

        //오픈채팅으로 만들었을 때.
        if(requestCode == 0)
        {
            Log.d("인텐트 들어갔니", "응 인텐트 들어갔어");
            //Toast.makeText(getApplicationContext(), "채팅을 시작한다", Toast.LENGTH_SHORT).show();
            roomId = data.getStringExtra("roomId");
            roomName = data.getStringExtra("roomName");
            justCreated = true;
        }
        //일반 채팅으로 만들었을 때.
        else if(requestCode == 1)
        {

        }
        //친구 목록 눌려서 초대했을 때.
        else if(requestCode == 2)
        {

        }
        else if(requestCode == FILTERED_PHOTO)
        {
            Toast.makeText(this, "필터 되었습니다.", Toast.LENGTH_SHORT).show();
            mCurrentPhotoPath = data.getStringExtra("mCurrentPhotoPath");
            fileName = data.getStringExtra("fileName");

            Log.d("만들어짐", "필터된"+mCurrentPhotoPath);

            sendedPhoto = true;

            send = new SendThread(socket);
            send.start();
        }

//        else if (requestCode == PICK_FROM_ALBUM) {
//            if (data == null) {
//                return;
//            }
//            photoUri = data.getData();
//            isAlbum = true;
//            cropImage();
//        } else if (requestCode == PICK_FROM_CAMERA) {
//
//            isAlbum = false;
//
//            cropImage();
//            // 갤러리에 나타나게
//            MediaScannerConnection.scanFile(ChatRoomActivity.this,
//                    new String[]{photoUri.getPath()}, null,
//                    new MediaScannerConnection.OnScanCompletedListener() {
//                        public void onScanCompleted(String path, Uri uri) {
//                        }
//                    });
//        } else if (requestCode == CROP_FROM_CAMERA) {
//
//
//
//            galleryAddPic();
//
//            uploadFile(mCurrentPhotoPath);
//            sendedPhoto = true;
//
//            send = new SendThread(socket);
//            send.start();
//
//        }


    }
//
//    //Android N crop image
//    public void cropImage() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            this.grantUriPermission("com.android.camera", photoUri,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(photoUri, "image/*");
//
//        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//        int size = list.size();
//        if (size == 0) {
//            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            }
//            intent.putExtra("crop", "true");
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//            intent.putExtra("scale", true);
//            File croppedFileName = null;
//            try {
//                croppedFileName = createImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
//            File tempFile = new File(folder.toString(), croppedFileName.getName());
//
//            photoUri = FileProvider.getUriForFile(ChatRoomActivity.this,
//                    "com.example.sumin.myapplication.provider", tempFile);
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            }
//
//            intent.putExtra("return-data", false);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//
//            Intent i = new Intent(intent);
//            ResolveInfo res = list.get(0);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//
//                grantUriPermission(res.activityInfo.packageName, photoUri,
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            }
//            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            startActivityForResult(i, CROP_FROM_CAMERA);
//        }
//    }
//
//    //사진 찍기 끝.
//
//
//    //사진 올리기.
//
//
//    public void uploadFile(String filePath)
//    {
//
//        String url = "http://ty79450.vps.phps.kr/sendImages.php";
//        File sourceFile = new File(filePath);
//        Log.d("리스폰스", "아 모야..");
//        uploadFilePractice(url, sourceFile, myUserId);
//
////        try {
////            UploadFile uploadFile = new UploadFile(ProfileSettingsActivity.this);
////            uploadFile.setPath(filePath);
////            uploadFile.execute(url);
////        } catch (Exception e){
////
////        }
//    }
//
//    //okhttp 연습.
//    public static boolean uploadFilePractice(String serverURL, File file, String username) {
//        try {
//            final File tempFile = file;
//            OkHttpClient client = new OkHttpClient();
//
//            RequestBody requestBody = new MultipartBody.Builder()
//                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("file", file.getName(),
//                            RequestBody.create(MediaType.parse("text/csv"), file))
//                    .addFormDataPart("uId", username)
//                    .build();
//
//            Request request = new Request.Builder()
//                    .url(serverURL)
//                    .post(requestBody)
//                    .build();
//
//            Log.d("리스폰스", "보냈당 "+username);
//
//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Call call, IOException e) {
//                    //Handle the error
//                    Log.d("리스폰스", "실패했당");
//                }
//
//                @Override
//                public void onResponse(Call call, Response response) throws IOException {
//                    if (!response.isSuccessful()) {
//                        // Handle the error
//                        Log.d("리스폰스", "실패했당2");
//                    }
//                    Log.d("리스폰스", "성공했당");
//
//                    // Upload successful
//                    Log.d("리스폰스", response.body().string());
//
//                    //임시경로에 저장된 파일 삭제.
//                    if(tempFile.delete())
//                    {
//                        Log.d("파일삭제", "성공");
//                    }
//                }
//            });
//
//
//
//            return true;
//        } catch (Exception ex) {
//            // Handle the error
//        }
//        return false;
//    }

    //사진 올리기 끝.


    //쓰레드 시작.
    class SocketClient extends Thread {
        boolean threadAlive;
        String ip;
        String port;
        String mac;

        //InputStream inputStream = null;
        OutputStream outputStream = null;
        BufferedReader br = null;

        private DataOutputStream output = null;

        public SocketClient(String ip, String port) {
            threadAlive = true;
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run()
        {
            try {
                // 연결후 바로 ReceiveThread 시작
                Log.d("연결", "SocketClient 쓰레드 시작. " + ip + ", 그리고 포트는 " +port);
                socket = new Socket(ip, Integer.parseInt(port));
                //inputStream = socket.getInputStream();
                output = new DataOutputStream(socket.getOutputStream());
                receive = new ReceiveThread(socket);
                receive.start();

                //mac주소를 받아오기위해 설정
                //★★★★★★★ API 24 이상부터는 getApplicationContext()를 통해서 getSystemService를 써야한다.
                WifiManager mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo info = mng.getConnectionInfo();
                Log.d("연결", "SocketClient 쓰레드 시작. 알아");

                //mac = info.getMacAddress();
                if(justCreated && callNum == 2)
                    mac = myNickname+":::"+roomId+":::"+myUserId+":::"+roomName+":::"+toUpdateParticipant;
                else if(justCreated)
                    mac = myNickname+":::"+roomId+":::"+myUserId+":::"+roomName;
                else
                    mac = myNickname+":::"+roomId+":::"+myUserId;

                Log.d("연결", "SocketClient 쓰레드 시작. 맞춰");
                if(mac != null)
                {
                    Log.d("연결", "SocketClient 쓰레드 시작. 봅시다");
                    Log.d("연결", "널 아닌데?");
                }
                else
                {
                    Log.d("연결", "널인데?");
                }
                Log.d("연결", "SocketClient 쓰레드 시작. - mac : " + mac);

                //mac 전송
                output.writeUTF(mac);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread extends Thread
    {
        private Socket socket = null;
        DataInputStream input;

        public ReceiveThread(Socket socket)
        {
            this.socket = socket;
            try
            {
                input = new DataInputStream(socket.getInputStream());
            }
            catch(Exception e)
            {

            }
        }
        // 메세지 수신후 Handler로 전달
        public void run()
        {
            try
            {
                while (input != null)
                {
                    String msg = input.readUTF();
                    Log.d("연결", "ReceiveThread - 핸들러로 전달 if문 이전.");

                    if (msg != null)
                    {
                        Log.d("연결", "ReceiveThread - 핸들러로 전달");

                        //액티비티를 제어할 핸들러. 이 앱에서는 리스트뷰에 메시지를 띄울거당
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 1111;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                        Log.d("연결",hdmsg.obj.toString());
                    }
                }
                Log.d("리시버", "ReceiveThread - 종료");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    class SendThread extends Thread {
        private Socket socket;

        DataOutputStream output;

        public SendThread(Socket socket) {
            this.socket = socket;
            try
            {
                output = new DataOutputStream(socket.getOutputStream());
            }
            catch (Exception e)
            {
            }
        }

        public void run() {
            try {
                sendmsg = editText_massage.getText().toString();

                // 메세지 전송부 (누군지 식별하기위한 방법으로 mac를 사용)
                Log.d("연결", "SendThread");
                String mac = null;
                WifiManager mng = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                WifiInfo info = mng.getConnectionInfo();
                //mac = info.getMacAddress();
                mac = myNickname;

                if(output != null)
                {
                    if(exit)
                    {
                        output.writeUTF("exit");
                        Log.d("방나감", "방을 나갑니다.");
//                        Toast.makeText(getApplicationContext(), "방을 나갑니다.", Toast.LENGTH_SHORT).show();
                    }
                    else if(sendedPhoto)
                    {
                        output.writeUTF(mac + ":::" +fileName + ":::" +myUserId+":::photo");
                        sendedPhoto = false;
                    }
                    else if (sendmsg != null) {
                        output.writeUTF(mac + ":::" +sendmsg + ":::" +myUserId);
                        Log.d("udb", sendmsg);

                        FCMUpdateDB udb = new FCMUpdateDB();
                        udb.execute();
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
    }


    public class ChatListUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "room_id="+roomId;
            Log.e("POST",param);
            try {
            /* 서버연결 */
                Log.e("POST",param);
                URL url = new URL("http://ty79450.vps.phps.kr/updateChatListView.php");
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
                SharedPreferences pref = getSharedPreferences("chatArrayJSONPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("chatArrayJSON", data);
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
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            getListViewItemsFromJSON();
        }
    }


    // Navigation drawer에 표시될 채팅방 참여자 목록이다.
    //onCreate에서 실행한다.
    public class ParticipantsUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "room_id="+roomId;
            Log.e("POST",param);
            try {
            /* 서버연결 */
                Log.e("POST",param);
                URL url = new URL("http://ty79450.vps.phps.kr/updateParticipants.php");
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
//                SharedPreferences pref = getSharedPreferences("chatArrayJSONPref", MODE_PRIVATE);
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("chatArrayJSON", data);
//                editor.apply();
//                Log.d("아싸", "쓰레드");

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

            //getListViewItemsFromJSON();
            //Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();

            for(int i =  drawerListViewAdapter.getCount()-1; i >= 0 ; i--)
            {
                drawerListViewAdapter.removeItem(i);
            }


            try
            {
                JSONArray jsonArray = new JSONArray(data);
                for(int i = 0; i <jsonArray.length(); i++)
                {
                    JSONObject user = jsonArray.getJSONObject(i);

                    String uId = user.getString("user_id");
                    String userNickname = user.getString("user_nickname");
                    String profImageURL = user.getString("profileImage");

                    DrawerListViewItem item = new DrawerListViewItem();
                    item.setUserId(uId);
                    item.setUserName(userNickname);
                    item.setUserProfileURL(profImageURL);

                    drawerListViewAdapter.addItem(item);

                    drawerListViewAdapter.notifyDataSetChanged();
                }
            }
            catch(JSONException e)
            {

            }
        }
    }


    public class FCMUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "room_id="+roomId+"&message="+sendmsg+"&room_name="+roomName +"&user_id="+myUserId;
            Log.e("POST",param);
            try {
            /* 서버연결 */
                Log.e("POST",param);
                URL url = new URL("http://ty79450.vps.phps.kr/fcm/push_notification.php");
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
            //
            // Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
        }
    }



}
