package com.example.scrollview.otherActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scrollview.MainActivity;
import com.example.scrollview.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DrawActivity extends AppCompatActivity {
    LinkedList<Path> r=new LinkedList<Path>();
    LinkedList<Integer> rcolor=new LinkedList<Integer>();
    LinkedList<Integer> rsize=new LinkedList<Integer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Intent intent=getIntent();
        CustomView c=findViewById(R.id.Custom);
        final String path=intent.getStringExtra("path4");
        if(path==null){

        }
        else {
            c.setPath(path);
        }

        Button save=findViewById(R.id.saveCustom);
        Button blue=findViewById(R.id.blue);
        Button up=findViewById(R.id.size_up);
        Button back=findViewById(R.id.back);
        Button red=findViewById(R.id.red);
        Button down=findViewById(R.id.size_down);
        Button go=findViewById(R.id.go);
        Button eraser=findViewById(R.id.Eraser);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomView c=findViewById(R.id.Custom);
                Bitmap bitmap=c.getMemBMP();
                if(path==null){
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String imageFileName = "Draw_" + timeStamp + ".jpg";
                    String filePath=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+imageFileName;
                    saveBitmap(bitmap,filePath);
                    Intent intent=
                            new Intent(DrawActivity.this, MainActivity.class);
                    intent.putExtra("path",filePath);
                    setResult(11,intent);
                    finish();
                }
                else {
                    String filePath=path;
                    saveBitmap(bitmap,filePath);
                    Intent intent=
                            new Intent(DrawActivity.this, MainActivity.class);
                    intent.putExtra("path",filePath);
                    setResult(11,intent);
                    finish();
                }

            }
        });
        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomView c=findViewById(R.id.Custom);
                c.setColor(Color.BLUE);
            }
        });
        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomView c=findViewById(R.id.Custom);
                c.setColor(Color.RED);
            }
        });
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomView c=findViewById(R.id.Custom);
                c.setSize(10);
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomView c=findViewById(R.id.Custom);
                c.setSize(5);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomView c=findViewById(R.id.Custom);
                List<Path> l=c.getListStrokes();
                if(l.size()-1>=0){
                    Log.v("Tag",l.size()+" "+c.getColors().size()+" "+c.getSizes().size());
                    r.push(l.get(l.size()-1));
                    rcolor.push(c.getColors().get(l.size()-1));
                    rsize.push(c.getSizes().get(l.size()-1));
                    l.remove(l.size()-1);   //颜色删除写那边了
                    c.backListStrokes(l);
                    c.drawStrokes();
                }
                else
                    Toast.makeText(DrawActivity.this,"没有上一步",Toast.LENGTH_LONG).show();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomView c=findViewById(R.id.Custom);
                List<Path> l=c.getListStrokes();
                if(!r.isEmpty()){
                    l.add(r.pop());
                    c.goListStrokes(rcolor.pop(),rsize.pop(),l);
                    c.drawStrokes();
                }
                else
                    Toast.makeText(DrawActivity.this,"没有下一步",Toast.LENGTH_LONG).show();
            }
        });
        eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomView c=findViewById(R.id.Custom);
                c.setColor(Color.WHITE);
            }
        });
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