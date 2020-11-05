package com.example.scrollview;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scrollview.db.InforDB;
import com.example.scrollview.db.ListDB;
import com.example.scrollview.db.javabean.MyImage;
import com.example.scrollview.db.javabean.MyList;
import com.example.scrollview.otherActivity.DrawActivity;
import com.example.scrollview.otherActivity.MusicActivity;
import com.example.scrollview.otherActivity.VideoActivity;
import com.example.scrollview.otherActivity.PhotoActivity;
import com.example.scrollview.otherActivity.WriteActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ArrayList<MyImage> list = new ArrayList<>();  //多媒体类列表
    String currentPhotoPath = ""; //照片保存路径
    String l_id="";    //列表id
    Boolean needRefresh=false;
    String myText="";   //内容
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
        l_id=intent.getStringExtra("l_id");
        myText=intent.getStringExtra("myText");
        setInfor();
        needRefresh=true;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);      //此处箭头为系统的图标资源
        //设置左上角导航键的点击监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                finish();
            }

        });
        initPhotoError();  //解决拍照问题  https://blog.csdn.net/weixin_42105630/article/details/86305354
        final ListView listView = findViewById(R.id.list_item);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("chick", "123");
                TextView txtId = view.findViewById(R.id.GUID);
                String id = txtId.getText().toString();
                for (int n = 0; n < list.size(); n++) {//遍历列表查询点击的内容
                    if (list.get(n).getId().equals(id)) {
                        switch (list.get(n).getType()) {  //不同种类打开不同的界面
                            case 1:  //照片
                                Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
                                intent.putExtra("path", list.get(n).getPath()); //忘初始化path了
                                intent.putExtra("n",n+"");
                                startActivityForResult(intent, 0);
                                break;
                            case 2:  //音乐
                                int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent2 = new Intent(MainActivity.this,MusicActivity.class);
                                    intent2.putExtra("path2", list.get(n).getPath()); //忘初始化path了
                                    intent2.putExtra("n",n+"");
                                    startActivityForResult(intent2, 0);
                                }else{
                                    //没有权限，向用户请求权限
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                                }

                                break;
                            case 3: //视频
                                int hasWriteStoragePermission2 = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                if (hasWriteStoragePermission2 == PackageManager.PERMISSION_GRANTED) {
                                    Intent intent3 = new Intent(MainActivity.this, VideoActivity.class);
                                    intent3.putExtra("path3", list.get(n).getPath()); //忘初始化path了
                                    intent3.putExtra("n",n+"");
                                    startActivityForResult(intent3, 0);
                                }else{
                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                                }
                                break;
                            case 4:  //画板
                                String[] p2=list.get(n).getPath().split("/");
                                String name2=p2[p2.length-1];
                                Log.v("myTag",name2.charAt(0)+"");
                                if(name2.charAt(0)=='D'){  //手写板，画板种类都是4  读文件名判断
                                    Intent intent4 = new Intent(MainActivity.this, DrawActivity.class);
                                    intent4.putExtra("path4", list.get(n).getPath()); //忘初始化path了
                                    intent4.putExtra("n",n+"");
                                    startActivityForResult(intent4, 0);
                                }
                                else {
                                    Intent intent4 = new Intent(MainActivity.this, WriteActivity.class);
                                    intent4.putExtra("path4", list.get(n).getPath()); //忘初始化path了
                                    intent4.putExtra("n",n+"");
                                    startActivityForResult(intent4, 0);
                                }

                                break;
                        }
                        break;
                    }
                }
            }
        });
    }

    //打开系统相机照相
    public  void photo(){
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

    //打开图库选择图片
    public void photo1(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");//选择图片
        startActivityForResult(intent, 1);
    }

    //录音
    public void record(){
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent,4);
    }

    //画板
    public void myDraw(){
        Intent intent = new Intent(MainActivity.this, DrawActivity.class);
        startActivityForResult(intent, 0);
    }

    //打开录像机
    public void video(){
        Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
        startActivityForResult(intent, 3);
    }

    //手写板
    public  void write(){
        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
        startActivityForResult(intent, 0);
    }

    //打开文件管理器 选择音乐文件
    public void l_music(){
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*"); //选择音频
            startActivityForResult(intent, 4);
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
        }
    }

    //打开文件管理器 选择视频文件
    public void l_video(){
        //使用兼容库就无需判断系统版本
        int hasWriteStoragePermission = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteStoragePermission == PackageManager.PERMISSION_GRANTED) {
            //拥有权限，执行操作
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
            startActivityForResult(intent, 3);
        }else{
            //没有权限，向用户请求权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    //删除全部信息
    public void delete(){
        new android.app.AlertDialog.Builder(this).setTitle("delete").setMessage("是否真的删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListDB listDB=new ListDB(MainActivity.this);
                InforDB inforDB=new InforDB(MainActivity.this);
                for(int t=0;t<list.size();t++){
                    if(list.get(t).getType()==1||list.get(t).getType()==4){
                        String deletePath=list.get(t).getPath();
                        File file=new File(deletePath);  //删除时把文件删了
                        if (file.isFile()) {
                            file.delete();
                        }
                    }
                }
                inforDB.DeleteSql(l_id);  //两个表都删
                listDB.DeleteUseSql(l_id);
                finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).create().show();
    }

    //显示详细信息
    public void detail(){
        ListDB listDB=new ListDB(MainActivity.this);
        ArrayList<MyList> myLists=listDB.getList(l_id);
        MyList myList;
        if(!myLists.isEmpty()){
            myList=myLists.get(0);
            final String myTime=myList.getMytime();
            final String iTime=myList.getiTime();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final View viewDialog = LayoutInflater.from(this).inflate(R.layout.detail, null, false);
            TextView lastTime=viewDialog.findViewById(R.id.lastTime);
            TextView insertTime=viewDialog.findViewById(R.id.insertTime);
            lastTime.setText(myTime);  //修改时间
            insertTime.setText(iTime); //创建时间
            builder.setTitle("详细信息")
                    .setView(viewDialog)
                    .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:   //相册照片
                    //有时会黑屏
                    Uri uri = data.getData();
                    ContentResolver cr = this.getContentResolver();
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + ".jpg";
                        String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + imageFileName;
                        saveBitmap(bitmap, filePath);
                        MyImage myImage1 =new MyImage(filePath, 1);
                        list.add(myImage1);
                    } catch (FileNotFoundException e) {
                        Log.e("Exception", e.getMessage(), e);
                    }
                    break;
                case 2:   //拍照不用获取uri，会有问题
                    MyImage myImage2 =new MyImage(currentPhotoPath, 1);
                    list.add(myImage2);
                    break;
                case 3:   //视频
                    Uri uri2 = data.getData();
                    String filePath = getAudioFilePathFromUri(uri2);
                    MyImage myImage3 =new MyImage(filePath, 3);
                    list.add(myImage3);
                    break;
                case 4:  //音频
                    Uri uri3 = data.getData();
                    String filePath2 = getAudioFilePathFromUri(uri3);
                    MyImage myImage4 =new MyImage(filePath2, 2);
                    list.add(myImage4);
                default:
                    break;
            }
        }
        if (requestCode == 0) {  //画图返回
            switch (resultCode){
                case 11: //新文件返回
                    MyImage myImage =new MyImage(data.getStringExtra("path"), 4);
                    list.add(myImage);
                    break;
                case 12:  //老文件返回
                    int n=Integer.parseInt(data.getStringExtra("change_n"));
                    list.get(n).setPath(data.getStringExtra("path"));
                    break;
                default:
                    break;
            }
        }
        if(resultCode==111){  //所有种类 返回后的删除
           int n=Integer.parseInt(data.getStringExtra("delete_n"));
           Log.v("myTag",list.get(n).getId());
           list.remove(n);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_main, menu);
        return true;
    }

    //uri查找路径
    private String getAudioFilePathFromUri(Uri uri) {
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        return cursor.getString(index);
    }

    //刷新界面
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

    //保存文字
    public void saveText(String l_id){  //存文字
        ListDB listDB=new ListDB(this);
        EditText e1=findViewById(R.id.text);
        String timeStamp = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date());
        String myText=e1.getText().toString();
        listDB.UpdateText(l_id,myText,timeStamp);
    }

    //保存多媒体
    public void saveImage(String l_id){
        InforDB inforDB=new InforDB(this);
        inforDB.DeleteSql(l_id);
        if(!list.isEmpty()){
            for(int i=0;i<list.size();i++){
                inforDB.InsertSql(list.get(i),l_id);
            }
        }
    }

    //加载信息
    public void setInfor(){
        InforDB inforDB=new InforDB(this);
        EditText editText=findViewById(R.id.text);
        editText.setText(myText);
        //查询存储的myImage
        ArrayList<MyImage> i1=inforDB.getInf(l_id);
        if(!i1.isEmpty()){
            list.addAll(i1);
        }
        refresh_list();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (list != null&&needRefresh) {
            refresh_list();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.zhao_pian:
                photo1();break;
            case R.id.photo:
                photo();break;
            case R.id.lu_yin:
                record();break;
            case R.id.lu_xiang:
                video();break;
            case R.id.tu_ya:
                myDraw();break;
            case R.id.delete:
                delete();break;
            case R.id.local_m:
                l_music();break;
            case R.id.local_v:
                l_video();break;
            case R.id.detail:
                detail();break;
            case R.id.shou_xie:
                write();break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    //权限设置时返回   https://www.jianshu.com/p/ccea3c2f9cfa
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //用户同意，执行操作
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*"); //选择视频 （mp4 3gp 是android支持的视频格式）
                startActivityForResult(intent, 3);
            }else{
                //用户不同意，向用户展示该权限作用
                Toast.makeText(MainActivity.this,"没有权限",Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 2){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*"); //选择音频
                startActivityForResult(intent, 4);
            }else{
                //用户不同意，向用户展示该权限作用
                Toast.makeText(MainActivity.this,"没有权限",Toast.LENGTH_LONG).show();
            }
        }
    }
    public void save(){
        Log.v("type",list.size()+"");
        for(int i=0;i<list.size();i++){
            Log.v("type",list.get(i).getType()+"");
        }
        saveText(l_id);
        saveImage(l_id);
    }
    @Override
    protected void onStop() {
        super.onStop();
        save();
        Log.v("stop","stop");
    }
}