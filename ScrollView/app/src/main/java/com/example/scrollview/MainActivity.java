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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scrollview.otherActivity.DrawActivity;
import com.example.scrollview.otherActivity.MusicActivity;
import com.example.scrollview.otherActivity.VideoActivity;
import com.example.scrollview.otherActivity.PhotoActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ArrayList<Image> list = new ArrayList<>();
    String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPhotoError();  //https://blog.csdn.net/weixin_42105630/article/details/86305354
        Button record = findViewById(R.id.record);
        Button btn1 = findViewById(R.id.btn1);
        Button photo = findViewById(R.id.photo);
        Button video = findViewById(R.id.video);
        Button draw = findViewById(R.id.draw);
        Button local_m=findViewById(R.id.local_m);
        Button local_v=findViewById(R.id.local_v);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                startActivityForResult(intent,4);
            }
        });
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
                startActivityForResult(intent, 3);
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrawActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        local_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                startActivityForResult(intent, 3);
            }

        });
        local_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*"); //选择音频
                startActivityForResult(intent, 4);
            }
        });
        final ListView listView = findViewById(R.id.list_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("chick", "123");
                TextView txtId = view.findViewById(R.id.GUID);
                String id = txtId.getText().toString();
                for (int n = 0; n < list.size(); n++) {
                    if (list.get(n).getId().equals(id)) {
                        switch (list.get(n).getType()) {
                            case 1:
                                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                                intent.putExtra("path", list.get(n).getPath()); //忘初始化path了
                                intent.putExtra("n",n+"");
                                startActivityForResult(intent, 0);
                                break;
                            case 2:
                                Intent intent2 = new Intent(MainActivity.this, MusicActivity.class);
                                intent2.putExtra("path2", list.get(n).getPath()); //忘初始化path了
                                intent2.putExtra("n",n+"");
                                startActivityForResult(intent2, 0);
                                break;
                            case 3:
                                Intent intent3 = new Intent(MainActivity.this, VideoActivity.class);
                                intent3.putExtra("path3", list.get(n).getPath()); //忘初始化path了
                                intent3.putExtra("n",n+"");
                                startActivityForResult(intent3, 0);
                                break;
                            case 4:
                                Intent intent4 = new Intent(MainActivity.this, DrawActivity.class);
                                intent4.putExtra("path4", list.get(n).getPath()); //忘初始化path了
                                intent4.putExtra("n",n+"");
                                startActivityForResult(intent4, 0);
                                break;
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

            switch (requestCode) {
                case 1:   //相册照片
                    //有时会黑屏 试试用线程异步操作
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + ".jpg";
                        String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + imageFileName;
                        saveBitmap(bitmap, filePath);
                        list.add(new Image(filePath, 1));
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }
                    break;
                case 2:   //拍照不用获取uri，会有问题
                    FileInputStream fs = null;
                    try {
                        File f = new File(currentPhotoPath);
                        fs = new FileInputStream(f);
                        Bitmap bitmap = BitmapFactory.decodeStream(fs);
                        list.add(new Image(currentPhotoPath, 1));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:   //视频
                    Uri uri2 = data.getData();
                    String filePath = getAudioFilePathFromUri(uri2);
                    Log.v("path", filePath);
                    list.add(new Image(filePath, 3));
                    break;
                case 4:  //音频
                    Uri uri3 = data.getData();
                    String filePath2 = getAudioFilePathFromUri(uri3);
                    Log.v("path", filePath2);
                    list.add(new Image(filePath2, 2));
                default:
                    break;
            }
        }
        if (requestCode == 0) {
            switch (resultCode){
                case 11:
                    FileInputStream fs = null;
                    File f = new File(data.getStringExtra("path"));
                    //      Log.v("path",data.getStringExtra("path"));
                    try {
                        fs = new FileInputStream(f);
                        Bitmap bitmap = BitmapFactory.decodeStream(fs);
                        list.add(new Image(data.getStringExtra("path"), 4));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case 12:
                    int n=Integer.parseInt(data.getStringExtra("change_n"));
                    list.get(n).setPath(data.getStringExtra("path"));
                    break;
                default:
                    break;
            }

        }
        if(resultCode==111){  //所有返回后删除
           int n=Integer.parseInt(data.getStringExtra("delete_n"));
            list.remove(n);
        }

    }

    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    public void refresh_list() {
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
    public static void saveBitmap(Bitmap bitmap, String path) {
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

    @Override
    protected void onResume() {
        super.onResume();
        if (list == null) {

        } else {
            refresh_list();
        }
    }
}