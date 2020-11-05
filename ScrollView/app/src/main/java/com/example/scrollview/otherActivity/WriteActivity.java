package com.example.scrollview.otherActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.scrollview.MainActivity;
import com.example.scrollview.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * 手写功能界面
 */
public class WriteActivity extends AppCompatActivity {
    String n="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        final Button save=findViewById(R.id.saveCustom);
        Button back=findViewById(R.id.back);
        Button delete=findViewById(R.id.delete);
        Intent intent=getIntent();
        WriteView c=findViewById(R.id.Custom);
        final String path=intent.getStringExtra("path4");

        n=intent.getStringExtra("n");
        if(path!=null){  //判断老文件新文件
            c.setPath(path);
            String[] a=path.split("_");   //把x，y存图片名称里
            c.setMy_x(Integer.parseInt(a[3]));
            c.setMy_y(Integer.parseInt(a[4]));
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(path);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteView c=findViewById(R.id.Custom);
                c.back();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (path != null) {
                    new android.app.AlertDialog.Builder(WriteActivity.this).setTitle("delete").setMessage("是否真的删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=
                                    new Intent(WriteActivity.this, MainActivity.class);
                            intent.putExtra("delete_n",n);
                            setResult(111,intent);
                            File file=new File(path);
                            if (file.isFile()) {
                                file.delete();
                            }
                            Log.v("delete",n);
                            finish();
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create().show();
                }
                else
                    finish();
            }
        });
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
                Log.v("file","createFile");
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
    public void save(String path){
        WriteView c=findViewById(R.id.Custom);
        Bitmap bitmap=c.getSaveBMP();
        if(path==null){
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "Write_" + timeStamp+"_"+c.getMy_x()+"_"+c.getMy_y()+ "_.jpg";
            String filePath=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+imageFileName;
            saveBitmap(bitmap,filePath);
            Intent intent=
                    new Intent(WriteActivity.this, MainActivity.class);
            intent.putExtra("path",filePath);
            setResult(11,intent);
        }
        else {
            File file=new File(path);  //删了重创一个，解决用glide的方法问题
            if (file.isFile()) {
                file.delete();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "Write_" + timeStamp+"_"+c.getMy_x()+"_"+c.getMy_y()+ "_.jpg";
            String filePath=getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+imageFileName;
            saveBitmap(bitmap,filePath);
            Intent intent=
                    new Intent(WriteActivity.this, MainActivity.class);
            intent.putExtra("path",filePath);
            intent.putExtra("change_n",n);
            setResult(12,intent);
        }
        c.setStopThread(true);
        finish();
    }
}