package com.example.sumin.myapplication;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * Created by Sumin on 2017-11-18.
 */

public class ImageFilterActivity extends AppCompatActivity {


    String myUserId;
    String myNickname;

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


    private ImageView imgMain ;
    private static final int SELECT_PHOTO = 100;
    private Bitmap src;

    private ImageView[] filteredImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagefilter_activity);

        imgMain = (ImageView) findViewById(R.id.effect_main);
        src = BitmapFactory.decodeResource(getResources(), R.drawable.default_profile_pic);

        filteredImages = new ImageView[11];

        filteredImages[0] = (ImageView) findViewById(R.id.effect_black);
        filteredImages[1] = (ImageView) findViewById(R.id.effect_boost_1);
        filteredImages[2] = (ImageView) findViewById(R.id.effect_boost_2);
        filteredImages[3] = (ImageView) findViewById(R.id.effect_boost_3);
        filteredImages[4] = (ImageView) findViewById(R.id.effect_brightness);
        filteredImages[5] = (ImageView) findViewById(R.id.effect_color_red);
        filteredImages[6] = (ImageView) findViewById(R.id.effect_color_blue);
        filteredImages[7] = (ImageView) findViewById(R.id.effect_color_green);
        filteredImages[8] = (ImageView) findViewById(R.id.effect_color_depth_32);
        filteredImages[9] = (ImageView) findViewById(R.id.effect_color_depth_64);
        filteredImages[10] = (ImageView) findViewById(R.id.effect_contrast);

        //내 아이디 세팅.
        final SharedPreferences pref = getSharedPreferences("loginPref", MODE_PRIVATE);
        myNickname = pref.getString("loginNickname","");
        myUserId = pref.getString("loginUserName", "");


        DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                takePhoto();
            }
        };

        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                goToAlbum();
            }
        };

        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ImageFilterActivity.this.finish();
            }
        };

        new AlertDialog.Builder(ImageFilterActivity.this)
                .setTitle("업로드할 이미지 선택")
                .setPositiveButton("취소", cancelListener)
                .setNeutralButton("앨범선택", albumListener)
                .setNegativeButton("사진촬영", cameraListener)
                .show();
    }

    public void buttonClicked(View v){

        Toast.makeText(this,"Processing...",Toast.LENGTH_SHORT).show();
        ImageFilters imgFilter = new ImageFilters();
        if(v.getId() == R.id.btn_pick_img){

            try{

                Bitmap bmp = ((BitmapDrawable)imgMain.getDrawable()).getBitmap();

                File f = new File(mCurrentPhotoPath);

                if(f !=null)
                {
                    Log.d("만들어짐", "1, 보낼거임" + f.toString() + "  이름 :" + fileName);
                }

                FileOutputStream fos = new FileOutputStream(f);
                bmp.compress(Bitmap.CompressFormat.PNG,90,fos);


                uploadFile(mCurrentPhotoPath);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }

            Intent intent = new Intent();
            intent.putExtra("mCurrentPhotoPath", mCurrentPhotoPath);
            intent.putExtra("fileName", fileName);

            setResult(RESULT_OK, intent);
            finish();
        }
//        else if(v.getId() == R.id.effect_highlight)
//            saveBitmap(imgFilter.applyHighlightEffect(src), "effect_highlight");
        else if(v.getId() == R.id.effect_black)
            saveBitmap(imgFilter.applyBlackFilter(src),"effect_black");
        else if(v.getId() == R.id.effect_boost_1)
            saveBitmap(imgFilter.applyBoostEffect(src, 1, 40),"effect_boost_1");
        else if(v.getId() == R.id.effect_boost_2)
            saveBitmap(imgFilter.applyBoostEffect(src, 2, 30),"effect_boost_2");
        else if(v.getId() == R.id.effect_boost_3)
            saveBitmap(imgFilter.applyBoostEffect(src, 3, 67),"effect_boost_3");
        else if(v.getId() == R.id.effect_brightness)
            saveBitmap(imgFilter.applyBrightnessEffect(src, 80),"effect_brightness");
        else if(v.getId() == R.id.effect_color_red)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 255, 0, 0),"effect_color_red");
        else if(v.getId() == R.id.effect_color_green)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 0, 255, 0),"effect_color_green");
        else if(v.getId() == R.id.effect_color_blue)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 0, 0, 255),"effect_color_blue");
        else if(v.getId() == R.id.effect_color_depth_64)
            saveBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 64),"effect_color_depth_64");
        else if(v.getId() == R.id.effect_color_depth_32)
            saveBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 32),"effect_color_depth_32");
        else if(v.getId() == R.id.effect_contrast)
            saveBitmap(imgFilter.applyContrastEffect(src, 70),"effect_contrast");
    }

    private void saveBitmap(Bitmap bmp,String myFileName)
    {
        try
        {
            imgMain.setImageBitmap(bmp);

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            String imageFileName = myUserId +"_" + timeStamp;

            mCurrentPhotoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NOSTest/" + imageFileName+".png";
            fileName= imageFileName+".png";
            Log.d("만들어짐2", mCurrentPhotoPath);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private Bitmap decodeUri(Uri selectedImage)  {
        try {
            // Decode image size
            //Image Width, Height 정보 가져오기.
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            //
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 400;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //사진 찍기.
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try
        {
            photoFile = createImageFile();  // 이미지 파일을  만들고.
        }
        catch (IOException e)
        {
            Toast.makeText(ImageFilterActivity.this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }
        if (photoFile != null) {
            photoUri = FileProvider.getUriForFile(ImageFilterActivity.this,
                    "com.example.sumin.myapplication.provider", photoFile); // FileProvider에 파일을 넣어 URI에 넣어준 뒤
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);   // 인텐트를 보내준다.
        }
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String imageFileName = myUserId +"_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
        if (!storageDir.exists())
        {
            storageDir.mkdirs();
        }
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = /*"file:" +*/ image.getAbsolutePath();
        fileName =image.getName();
        Log.d("파일 경로 뭐니", mCurrentPhotoPath + " " + imageFileName);
        return image;
    }

    private void goToAlbum()
    {

        // 앨범에서 사진을 가져오는 인텐트이다.
        Intent intent = new Intent(Intent.ACTION_PICK);
        //MIME 타입이란 클라이언트에게 전송된 문서의 다양성을 알려주기 위한 메커니즘입니다: 웹에서 파일의 확장자는 별  의미가 없습니다.
        //MIME 는 전자 우편을 위한(전송을 위한?) 인터넷 표준 포맷이다.
        //전자우편은 7비트 ASCII 문자를 사용하여 전송되기 때문에,
        //8비트 이상의 코드를 사용하는 문자나 이진 파일들은 MIME 포맷으로 변환되어 SMTP로 전송된다.
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

        //이 함수를 쓰지 않으면 크롭된 사진을 저장해도 앨범에 보이지 않으며, 직접 파일 관리자 앱을 통해 폴더를 들어가야만 사진을 볼 수 있다.
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
            MediaScannerConnection.scanFile(ImageFilterActivity.this,
                    new String[]{photoUri.getPath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        } else if (requestCode == CROP_FROM_CAMERA) {

            galleryAddPic();

            ImageFilters imgFilter = new ImageFilters();

            uploadFile(mCurrentPhotoPath);
            sendedPhoto = true;

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f =   new File(mCurrentPhotoPath);
            Uri selectedImage = Uri.fromFile(f);

            Bitmap bmp = decodeUri(selectedImage);
            if(bmp !=null){
                src = bmp;
                imgMain.setImageBitmap(src);
                filteredImages[0].setImageBitmap(imgFilter.applyBlackFilter(src));
                filteredImages[1].setImageBitmap(imgFilter.applyBoostEffect(src, 1, 40));
                filteredImages[2].setImageBitmap(imgFilter.applyBoostEffect(src, 2, 30));
                filteredImages[3].setImageBitmap(imgFilter.applyBoostEffect(src, 3, 67));
                filteredImages[4].setImageBitmap(imgFilter.applyBrightnessEffect(src, 80));
                filteredImages[5].setImageBitmap(imgFilter.applyColorFilterEffect(src, 255, 0, 0));
                filteredImages[6].setImageBitmap(imgFilter.applyColorFilterEffect(src, 0, 0, 255));
                filteredImages[7].setImageBitmap(imgFilter.applyColorFilterEffect(src, 0, 255, 0));
                filteredImages[8].setImageBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 32));
                filteredImages[9].setImageBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 64));
                filteredImages[10].setImageBitmap(imgFilter.applyContrastEffect(src, 70));
            }



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
        if (size == 0)
        {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
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
            try
            {
                croppedFileName = createImageFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            File folder = new File(Environment.getExternalStorageDirectory() + "/NOSTest/");
            File tempFile = new File(folder.toString(), croppedFileName.getName());

            photoUri = FileProvider.getUriForFile(ImageFilterActivity.this,
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

    //사진 찍기 끝.


    //사진 올리기.


    public void uploadFile(String filePath)
    {

        String url = "http://ty79450.vps.phps.kr/sendImages.php";
        File sourceFile = new File(filePath);
        Log.d("리스폰스", "아 모야..");
        uploadFilePractice(url, sourceFile, myUserId);

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

    //사진 올리기 끝.



}
