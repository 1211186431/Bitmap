package com.example.scrollview.otherActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.scrollview.MainActivity;
import com.example.scrollview.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PhotoActivity extends AppCompatActivity {
    String n="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {   //ViewPager+fragment实现滚动有时间再做
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Button back=findViewById(R.id.back);
        Button delete=findViewById(R.id.delete);
        Intent intent=getIntent();
        String path=intent.getStringExtra("path");
        n=intent.getStringExtra("n");
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(path);
            Bitmap bitmap = BitmapFactory.decodeStream(fs);
            ImageView imageView = (ImageView) findViewById(R.id.display);
            imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=
                        new Intent(PhotoActivity.this, MainActivity.class);
                intent.putExtra("delete_n",n);
                setResult(111,intent);
                Log.v("delete",n);
                finish();
            }
        });
    }
}