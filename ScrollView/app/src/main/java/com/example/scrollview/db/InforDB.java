package com.example.scrollview.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.scrollview.db.javabean.MyImage;

import java.util.ArrayList;

/**
 * 存储信息操作类
 * 用来对存的图片等的数据库操作
 */
public class InforDB {
    private static final String TAG = "myTag";
    private static ListDBHelper mDbHelper;   //采用单例模式

    public InforDB(Context context){
            mDbHelper = new ListDBHelper(context);
    }

    public void close() {
        if (mDbHelper != null) mDbHelper.close();
    }

    /**
     * l_id 列表id
     * 获取目录中存储的多媒体类信息
     */
    public ArrayList<MyImage> getInf(String l_id) {
        if (mDbHelper == null) {
            return null;
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                "i_id",
                "mypath",
                "mytype",
                "l_id"
        };
        String where = "l_id= ?";
        String[] params = {l_id};
        Cursor c = db.query(
                "infor",  // The table to query
                projection,                               // The columns to return
                where,                                // The columns for the WHERE clause
                params,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        return ConvertCursor(c);
    }

    //将游标转化
    private ArrayList<MyImage> ConvertCursor(Cursor cursor) {
        ArrayList<MyImage> result = new ArrayList<>();
        while (cursor.moveToNext()) {
           MyImage myImage =new MyImage(cursor.getString(cursor.getColumnIndex("mypath")),
                   cursor.getInt(cursor.getColumnIndex("mytype")));
            result.add(myImage);
        }
        return result;
    }

    //增加
    public  void InsertSql(MyImage myImage, String l_id) {
        String sql = "insert into  infor(i_id,mypath,mytype,l_id) values(?,?,?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Log.v("myTag", myImage.getId());
        db.execSQL(sql, new String[]{myImage.getId(), myImage.getPath(), myImage.getType()+"",l_id});
    }

    //删除 按照列表id删除所有内容
    public void DeleteSql(String strId) {
        String sql = " DELETE FROM " + "infor"+
                "  WHERE l_id= ?";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{strId});
    }

    //按照id指定删除 用于指定删除
    public void DeleteOne(String i_id){
        String sql = " DELETE FROM " + "infor"+
                "  WHERE i_id= ?";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{i_id});
    }


}
