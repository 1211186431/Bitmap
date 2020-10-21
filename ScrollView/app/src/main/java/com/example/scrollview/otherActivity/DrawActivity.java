package com.example.scrollview.otherActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.scrollview.Image;
import com.example.scrollview.MainActivity;
import com.example.scrollview.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DrawActivity extends AppCompatActivity {
    LinkedList<Path> r=new LinkedList<Path>();
    LinkedList<Integer> rcolor=new LinkedList<Integer>();
    LinkedList<Integer> rsize=new LinkedList<Integer>();
    String n="";
    int PColor=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        Intent intent=getIntent();
        CustomView c=findViewById(R.id.Custom);
        n=intent.getStringExtra("n");
        final String path=intent.getStringExtra("path4");
        if(path==null){

        }
        else {
            c.setPath(path);
        }

        ArrayList<Map<String,Object>> list=setColor();
        SimpleAdapter simpleAdapter=new SimpleAdapter(DrawActivity.this,list,R.layout.item_color,new String[]{"color","pic"},new int[]{R.id.color_name,R.id.color_image});
        Spinner spinner=findViewById(R.id.color);
        spinner.setAdapter(simpleAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                TextView t=view.findViewById(R.id.color_name);
                setPColor(t.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callbac
            }
        });
        ArrayList<Map<String,Object>> list_size=setSize();
        SimpleAdapter simpleAdapter2=new SimpleAdapter(DrawActivity.this,list_size,R.layout.item_size,new String[]{"color","pic"},new int[]{R.id.color_name,R.id.color_image});
        Spinner spinner_size=findViewById(R.id.size);
        spinner_size.setAdapter(simpleAdapter2);
        spinner_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                TextView t=view.findViewById(R.id.color_name);
                setPSize(t.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callbac
            }
        });
        ImageButton back=findViewById(R.id.back);
        ImageButton go=findViewById(R.id.go);
        final Button eraser=findViewById(R.id.Eraser);
        ImageButton delete=findViewById(R.id.delete);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_save);      //此处箭头为系统的图标资源
        //设置左上角导航键的点击监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save(path);
                finish();
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
                String e_text=eraser.getText().toString();
                switch (e_text){
                    case "画笔":
                        c.setColor(PColor);
                        eraser.setText("橡皮");
                        break;
                    case "橡皮":
                        c.setColor(Color.WHITE);
                        eraser.setText("画笔");
                        break;
                    default:break;
                }

            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(DrawActivity.this).setTitle("delete").setMessage("是否真的删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=
                                new Intent(DrawActivity.this, MainActivity.class);
                        intent.putExtra("delete_n",n);
                        setResult(111,intent);
                        Log.v("delete",n);
                        finish();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();

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
    public void save(String path){
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
            intent.putExtra("change_n",n);
            setResult(12,intent);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_draw, menu);
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,

                                    ContextMenu.ContextMenuInfo menuInfo) {

        // Log.v(TAG, "WordItemFragment::onCreateContextMenu()");
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.contextmenu1, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.back_toolbar) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //初始化颜色下拉列表
    public ArrayList<Map<String,Object>> setColor(){
        ArrayList<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        Map<String,Object> a=new HashMap<>();
        a.put("pic",R.drawable.shape_black);
        a.put("color","black");
        list.add(a);
        Map<String,Object> a1=new HashMap<>();
        a1.put("pic",R.drawable.shape_blue);
        a1.put("color","blue");
        list.add(a1);
        Map<String,Object> a11=new HashMap<>();
        a11.put("pic",R.drawable.shape_gree);
        a11.put("color","gree");
        list.add(a11);
        Map<String,Object> a111=new HashMap<>();
        a111.put("pic",R.drawable.shape_red);
        a111.put("color","red");
        list.add(a111);
        Map<String,Object> a1111=new HashMap<>();
        a1111.put("pic",R.drawable.shape_yellow);
        a1111.put("color","yellow");
        list.add(a1111);
        return list;
    }

    //初始化大小下拉列表
    public ArrayList<Map<String,Object>> setSize(){
        ArrayList<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
        Map<String,Object> a=new HashMap<>();
        a.put("pic",R.drawable.size_5);
        a.put("color","black");
        list.add(a);
        Map<String,Object> a1=new HashMap<>();
        a1.put("pic",R.drawable.size_10);
        a1.put("color","blue");
        list.add(a1);
        Map<String,Object> a11=new HashMap<>();
        a11.put("pic",R.drawable.size_15);
        a11.put("color","red");
        list.add(a11);
        return list;
    }
    public void setPSize(String color){
        CustomView c=findViewById(R.id.Custom);
        switch (color){
            case "red":
                c.setSize(15);break;
            case "black":
                c.setSize(5);break;
            case "blue":
                c.setSize(10);break;
            default:
                break;
        }
    }
    public void setPColor(String color){
        CustomView c=findViewById(R.id.Custom);
        switch (color){
            case "red":
                PColor=Color.RED;
                c.setColor(Color.RED);break;
            case "black":
                PColor=Color.BLACK;
                c.setColor(Color.BLACK);break;
            case "gree":
                PColor=Color.GREEN;
                c.setColor(Color.GREEN);break;
            case "yellow":
                PColor=Color.YELLOW;
                c.setColor(Color.YELLOW);break;
            case "blue":
                PColor=Color.BLUE;
                c.setColor(Color.BLUE);break;
            default:
                break;
        }
    }
}