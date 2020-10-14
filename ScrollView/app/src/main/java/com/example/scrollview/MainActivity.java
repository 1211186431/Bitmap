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
import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    ArrayList<Image> list=new  ArrayList<>();
    String photoName="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPhotoError();  //https://blog.csdn.net/weixin_42105630/article/details/86305354
        Button music=findViewById(R.id.music);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addlist(new Image(2));
            }
        });
        Button btn1=findViewById(R.id.btn1);
        Button photo=findViewById(R.id.photo);
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
                photoName=GUID.getGUID()+".jpg";
                File cache=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),"myPhotoPath/");
                if(!cache.exists()){//目录不存在就创一个
                    cache.mkdirs();
                }
                Uri imageUri = Uri.fromFile(new File(cache,photoName));
                //先创一个图片获取uri，在把照出来的存那
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2);
            }
        });
        ListView listView=findViewById(R.id.list_item);
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView txtId=view.findViewById(R.id.GUID);
                String id=txtId.getText().toString();

                for(int n=0;n<list.size();n++){
                    if(list.get(n).getId().equals(id)){
                        Intent intent=new Intent(MainActivity.this,display.class);
                        intent.putExtra("path",list.get(n).getPath()); //忘初始化path了
                        Log.v("Tag",list.get(n).getPath());
                        startActivity(intent);
                    }
                }


            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case 1:
                    Uri uri = data.getData();      //有时会黑屏 试试用线程异步操作
                    String filePath = getAudioFilePathFromUri(uri);
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        addlist(new Image(bitmap,filePath,1));
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(),e);
                    }
                    break;
                case 2:
                    FileInputStream fs = null;
                    try {
                        String pathname=Environment.getExternalStorageDirectory()
                                +"/myPhotoPath/"+photoName;
                        File f=new File(pathname);
                        fs = new FileInputStream(f);
                        Bitmap bitmap = BitmapFactory.decodeStream(fs);
                        addlist(new Image(bitmap,pathname,1));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
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

    public void addlist(Image image){
        list.add(image);
        ImageAdapter adapter=new ImageAdapter(list,MainActivity.this);
        ListView l=findViewById(R.id.list_item);
        l.setAdapter(adapter);
    }
    private void initPhotoError(){
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

}