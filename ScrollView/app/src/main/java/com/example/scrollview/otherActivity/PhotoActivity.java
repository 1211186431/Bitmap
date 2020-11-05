package com.example.scrollview.otherActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.scrollview.ListActivity;
import com.example.scrollview.MainActivity;
import com.example.scrollview.R;
import com.example.scrollview.db.ListDB;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 照片展示页
 * 显示照片，删除，返回 3个功能
 */
public class PhotoActivity extends AppCompatActivity {
    String n="";
    String path="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {   //ViewPager+fragment实现滚动有时间再做
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent=getIntent();
        path=intent.getStringExtra("path");
        n=intent.getStringExtra("n");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);      //此处箭头为系统的图标资源
        //设置左上角导航键的点击监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fs);
            ImageView imageView = (ImageView) findViewById(R.id.display);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            new android.app.AlertDialog.Builder(this).setTitle("delete").setMessage("是否真的删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent=
                            new Intent(PhotoActivity.this, MainActivity.class);
                    intent.putExtra("delete_n",n);
                    setResult(111,intent);
                    Log.v("delete",n);
                    File file=new File(path);
                    if (file.isFile()) {
                        file.delete();
                    }
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).create().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}