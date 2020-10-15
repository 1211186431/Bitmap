package com.example.scrollview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Image> list = new ArrayList<>();
    String currentPhotoPath="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPhotoError();  //https://blog.csdn.net/weixin_42105630/article/details/86305354
        Button music = findViewById(R.id.music);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // addlist(new Image(2));
            }
        });
        Button btn1 = findViewById(R.id.btn1);
        Button photo = findViewById(R.id.photo);
        Button video =findViewById(R.id.video);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");//选择图片
                startActivityForResult(intent, 1);
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                try {
                    File cache = createImageFile();
                    Uri imageUri = Uri.fromFile(cache);
                    //先创一个图片获取uri，在把照出来的存那
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 2);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
                startActivityForResult(intent,3);
            }
        });
        final ListView listView = findViewById(R.id.list_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("chick", "123");
                TextView txtId=view.findViewById(R.id.GUID);
                String id=txtId.getText().toString();
                for(int n=0;n<list.size();n++){
                    if(list.get(n).getId().equals(id)){
                        if(list.get(n).getType()==1){
                            Intent intent=new Intent(MainActivity.this,display.class);
                            intent.putExtra("path",list.get(n).getPath()); //忘初始化path了
                            startActivity(intent);
                        }
                        if (list.get(n).getType()==2){

                        }
                        if(list.get(n).getType()==3){
                            Intent intent=new Intent(MainActivity.this,VideoActivity.class);
                            intent.putExtra("path",list.get(n).getPath()); //忘初始化path了
                            startActivity(intent);
                        }
                       break;
                    }
                }


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            switch (requestCode) {
                case 1:
                          //有时会黑屏 试试用线程异步操作
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + ".jpg";
                        String filePath=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+imageFileName;
                        saveBitmap(bitmap,filePath);
                        addlist(new Image(bitmap, filePath, 1));
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }
                    break;
                case 2:
                    FileInputStream fs = null;
                    try {
                        File f = new File(currentPhotoPath);
                        Log.v("path",currentPhotoPath);
                        fs = new FileInputStream(f);
                        Bitmap bitmap = BitmapFactory.decodeStream(fs);
                        addlist(new Image(bitmap,currentPhotoPath, 1));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    String filePath = getAudioFilePathFromUri(uri);
                    Log.v("path",filePath);
                    addlist(new Image(filePath,3));
                    break;
                default:
                    break;
            }

        }

    }

    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    public void addlist(Image image) {
        list.add(image);
        ImageAdapter adapter = new ImageAdapter(list, MainActivity.this);
        ListView l = findViewById(R.id.list_item);
        l.setAdapter(adapter);
    }

    private void initPhotoError() {
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    private File createImageFile() throws IOException {   // 创新文件 老师ppt
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );    // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     */
    public static void saveBitmap(Bitmap bitmap,String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.e("tag", "saveBitmap failure : sdcard not mounted");
            return;
        }
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return;
        }
        Log.i("tag", "saveBitmap success: " + filePic.getAbsolutePath());
    }

}