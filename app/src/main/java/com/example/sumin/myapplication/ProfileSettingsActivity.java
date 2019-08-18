package com.example.sumin.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Sumin on 2017-08-23.
 */

public class ProfileSettingsActivity extends AppCompatActivity {

    android.support.v7.app.AlertDialog.Builder alertBuilder;

    Button logoutButton;

    TextView userNicknameText;
    TextView statusMessage;
    LinearLayout imageLayout;
    LinearLayout statusMessageLayout;
    LinearLayout userNicknameLayout;
    ImageView profileImage;

    String userNameString;

    String userNicknameString;
    String statusMessageString;



    static String UploadImgPath;


    boolean isAlbum = false;
    boolean isThereAnyPhoto = false;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_ALBUM = 2;
    private static final int CROP_FROM_CAMERA = 3;

    private Uri photoUri;
    private String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int MULTIPLE_PERMISSIONS = 101;

    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.profile_settings);


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog().build());

        checkPermissions();


        logoutButton = (Button) findViewById(R.id.logoutBtn);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = ProfileSettingsActivity.this.getSharedPreferences("loginPref", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                SharedPreferences pref2 = ProfileSettingsActivity.this.getSharedPreferences("myProfileImage", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = pref2.edit();
                editor2.clear();
                editor2.commit();



                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                }

                Intent intent = new Intent(ProfileSettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        statusMessage = (TextView) findViewById(R.id.status_message);
        imageLayout = (LinearLayout) findViewById(R.id.profile_image_layout);
        profileImage = (ImageView) findViewById(R.id.profile_image);
        statusMessageLayout = (LinearLayout) findViewById(R.id.status_message_layout);
        userNicknameLayout = (LinearLayout) findViewById(R.id.userNickname_layout);
        userNicknameText = (TextView) findViewById(R.id.userNicknameText);

        //인텐트로부터 받아오기.
        final SharedPreferences pref = this.getSharedPreferences("loginPref", MODE_PRIVATE);

        userNameString = pref.getString("loginUserName","");

        statusMessage.setText(pref.getString("loginStatusMessage",""));
        userNicknameText.setText(pref.getString("loginNickname",""));

        statusMessageString = pref.getString("loginStatusMessage","");
        userNicknameString = pref.getString("loginNickname","");

        SharedPreferences sp = this.getSharedPreferences("myProfileImage", MODE_PRIVATE);
        if(!sp.getString("myProfileImage","none").equals("none"))
        {
            isThereAnyPhoto = true;
            Glide.with(this).load("http://ty79450.vps.phps.kr/"+userNameString
                    +"/"+sp.getString("myProfileImage","none")).into(profileImage);
        }


        userNicknameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(ProfileSettingsActivity.this);
                ad.setMessage("본인의 이름을 입력해 주세요.");   // 내용 설정

// EditText 삽입하기
                final EditText et1 = new EditText(ProfileSettingsActivity.this);
                et1.setText(userNicknameText.getText());
                ad.setView(et1);

// 확인 버튼 설정
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Text 값 받아서 로그 남기기
                        String value = et1.getText().toString();
                        userNicknameText.setText(value);
                        userNicknameString = value;

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("loginNickname", value);
                        editor.commit();

                        ProfileSettingsActivity.ProfileUpdateDB pDB = new ProfileSettingsActivity.ProfileUpdateDB();
                        pDB.execute();

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
// 취소 버튼 설정
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
// 창 띄우기
                ad.show();
            }
        });


        statusMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(ProfileSettingsActivity.this);


                ad.setMessage("상태 메시지를 변경해 주세요.");   // 내용 설정

// EditText 삽입하기
                final EditText et = new EditText(ProfileSettingsActivity.this);
                et.setText(statusMessage.getText());
                ad.setView(et);

// 확인 버튼 설정
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Text 값 받아서 로그 남기기
                        String value = et.getText().toString();
                        statusMessage.setText(value);
                        statusMessageString = value;

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("loginStatusMessage", value);
                        editor.commit();

                        ProfileSettingsActivity.ProfileUpdateDB pDB = new ProfileSettingsActivity.ProfileUpdateDB();
                        pDB.execute();

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
// 취소 버튼 설정
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
// 창 띄우기
                ad.show();
            }
        });



        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePhoto();
                    }
                };

                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToAlbum();
                    }
                };

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(ProfileSettingsActivity.this)
                        .setTitle("업로드할 이미지 선택")
                        .setPositiveButton("취소", cancelListener)
                        .setNeutralButton("앨범선택", albumListener)
                        .setNegativeButton("사진촬영", cameraListener)
                        .show();
            }
        });

        imageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(!isThereAnyPhoto)
                {
                    return true;
                }

                DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };

                DialogInterface.OnClickListener removeProfileListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isThereAnyPhoto = false;
                        RemoveProfileImageDB rpi = new RemoveProfileImageDB();
                        rpi.execute();
                        profileImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.default_profile_pic, null) );

                        //sharedpreference에 저장되어있는 것 삭제.
                        SharedPreferences pref2 = ProfileSettingsActivity.this.getSharedPreferences("myProfileImage", MODE_PRIVATE);
                        SharedPreferences.Editor editor2 = pref2.edit();
                        editor2.clear();
                        editor2.apply();

                        dialog.dismiss();
                    }
                };

                new AlertDialog.Builder(ProfileSettingsActivity.this)
                        .setTitle("프로필 사진을 내리겠습니까?")
                        .setPositiveButton("아니오", cancelListener)
                        .setNegativeButton("예", removeProfileListener)
                        .show();
                return true;
            }
        });

    }


    private boolean checkPermissions()
    {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try
        {
            photoFile = createImageFile();  // 이미지 파일을  만들고.
        }
        catch (IOException e)
        {
            Toast.makeText(ProfileSettingsActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(ProfileSettingsActivity.this,
                    "com.example.sumin.myapplication.provider", photoFile); // FileProvider에 파일을 넣어 URI에 넣어준 뒤
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);   // 인텐트를 보내준다.
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String imageFileName = userNameString +"_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = /*"file:" +*/ image.getAbsolutePath();
        Log.d("파일 경로 뭐니", mCurrentPhotoPath + " " + imageFileName);
        return image;
    }

    private void goToAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //갤러리 새로고침, ACTION_MEDIA_MOUNTED는 하나의 폴더,
    //ACTION_MEDIA_FILE은 하나의 파일을 새로고침할 때 사용함.
    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f =   new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {

//                      this.permissions[0] :  Manifest.permission.READ_EXTERNAL_STORAGE,
//                      this.permissions[1] :  Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                      this.permissions[2] :  Manifest.permission.CAMERA


                        if (permissions[i].equals(this.permissions[0])) {
                            //권한이 승인되지 않았으면, 토스트를 띄운다. (If permission is not granted, show a Toast.)
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();
                            }
                        } else if (permissions[i].equals(this.permissions[1])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        } else if (permissions[i].equals(this.permissions[2])) {
                            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                                showNoPermissionToastAndFinish();

                            }
                        }
                    }
                } else {
                    showNoPermissionToastAndFinish();
                }
                return;
            }
        }
    }

    private void showNoPermissionToastAndFinish() {
        Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT).show();
        finish();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == PICK_FROM_ALBUM) {
            if (data == null) {
                return;
            }
            photoUri = data.getData();
            isAlbum = true;
            cropImage();
        } else if (requestCode == PICK_FROM_CAMERA) {

            isAlbum = false;

            cropImage();
            // 갤러리에 나타나게
            MediaScannerConnection.scanFile(ProfileSettingsActivity.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {





            profileImage.setImageURI(null);
            profileImage.setImageURI(photoUri);

            galleryAddPic();
            uploadFile(mCurrentPhotoPath);
            //suminFlag

//            try {
//                //Uri에서 이미지 이름을 얻어온다.
//                String name_Str = getImageNameToUri(data.getData());
//                photoUri = data.getData();
//                //절대경로 획득**
//                Cursor c = getContentResolver().query(Uri.parse(photoUri.toString()), null, null, null, null);
//                c.moveToNext();
//                String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
//
//                //이미지 데이터를 비트맵으로 받아옴
//                Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
//
//                ///리사이징
//                int height = image_bitmap.getHeight();
//                int width = image_bitmap.getWidth();
//
//                Bitmap src = BitmapFactory.decodeFile( absolutePath);
//                Bitmap resized = Bitmap.createScaledBitmap( src, width/4, height/4, true );
//
//                saveBitmaptoJpeg(resized, "seatdot", name_Str);
//                ////리사이징
//
//                //배치해놓은 ImageView에 set
//                profileImage.setImageBitmap(resized);
//                profileImage.setTag("exist");
//
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (Exception e)
//            {
//                e.printStackTrace();
//            }
            //suminFlag end
        }
    }

    //Android N crop image
    public void cropImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.grantUriPermission("com.android.camera", photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            grantUriPermission(list.get(0).activityInfo.packageName, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(this, "용량이 큰 사진의 경우 시간이 오래 걸릴 수 있습니다.", Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            intent.putExtra("crop", "true");
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            File croppedFileName = null;
            try {
                croppedFileName = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(ProfileSettingsActivity.this,
                    "com.example.sumin.myapplication.provider", tempFile);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                grantUriPermission(res.activityInfo.packageName, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            startActivityForResult(i, CROP_FROM_CAMERA);
        }
    }

    //사진 올리기.


    public void uploadFile(String filePath)
    {

        String url = "http://ty79450.vps.phps.kr/updatePhoto.php";
        File sourceFile = new File(filePath);
        Log.d("리스폰스", "아 모야..");
        uploadFilePractice(url, sourceFile, userNameString);

//        try {
//            UploadFile uploadFile = new UploadFile(ProfileSettingsActivity.this);
//            uploadFile.setPath(filePath);
//            uploadFile.execute(url);
//        } catch (Exception e){
//
//        }
    }

    //okhttp 연습.




    public static boolean uploadFilePractice(String serverURL, File file, String username) {

        try {

            final File tempFile = file;

            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getName(),
                            RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("uId", username)
                    .build();

            Request request = new Request.Builder()
                    .url(serverURL)
                    .post(requestBody)
                    .build();

            Log.d("리스폰스", "보냈당 "+username);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    //Handle the error
                    Log.d("리스폰스", "실패했당");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        // Handle the error
                        Log.d("리스폰스", "실패했당2");
                    }
                    Log.d("리스폰스", "성공했당");
                    // Upload successful
                    Log.d("리스폰스", response.body().string());

                    //임시경로에 저장된 파일 삭제.
                    if(tempFile.delete())
                    {
                        Log.d("파일삭제", "성공");
                    }
                }
            });



            return true;
        } catch (Exception ex) {
            // Handle the error
        }
        return false;
    }


        void practice()
        {

            String id = "thisIsID";
            String url = "asdf";

            OkHttpClient client = new OkHttpClient();


            //POST 방식을 사용하기 위한 RequestBody.
            RequestBody body = new FormBody.Builder()
                                            .add("Id",id) //안드로이드에서 보내줄 변수.
                                            .build(); //몸통을 만들어 주었다.

            Request request = new Request.Builder()
                                        .url(url)
                                        .post(body)
                                        .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });


        }


    //



    //사진 올리기 끝.


    //프로필 업데이트.

    public class ProfileUpdateDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "statMessage=" + statusMessageString + "&u_id=" + userNameString +"&u_nick=" +userNicknameString +"";
            Log.e("POST",param);
            try {
            /* 서버연결 */
                URL url = new URL(
                        "http://ty79450.vps.phps.kr/updateProfile.php");
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


        }
    }

    public class RemoveProfileImageDB extends AsyncTask<Void, Integer, Void> {

        String data = "";

        @Override
        protected Void doInBackground(Void... unused) {

            /* 인풋 파라메터값 생성 */
            String param = "statMessage=" + statusMessageString + "&u_id=" + userNameString +"&u_nick=" +userNicknameString +"";
            Log.e("POST",param);
            try {
            /* 서버연결 */
                URL url = new URL(
                        "http://ty79450.vps.phps.kr/removeProfileImage.php");
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


        }
    }


}
