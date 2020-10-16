package com.example.scrollview.otherActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.scrollview.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PhotoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {   //ViewPager+fragment实现滚动有时间再做
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Intent intent=getIntent();
        String path=intent.getStringExtra("path");
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
}