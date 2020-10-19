package com.example.scrollview.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.scrollview.GUID;
import com.example.scrollview.Image;
import com.example.scrollview.db.javabean.List;
import com.example.scrollview.db.javabean.Infor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InforDB {
    private static final String TAG = "myTag";
    private static ListDBHelper mDbHelper;   //采用单例模式

    public InforDB(Context context){
            mDbHelper = new ListDBHelper(context);
    }

    public void close() {
        if (mDbHelper != null) mDbHelper.close();
    }


    public ArrayList<Image> getInf(String l_id) {
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
    private ArrayList<Image> ConvertCursor(Cursor cursor) {
        ArrayList<Image> result = new ArrayList<>();
        while (cursor.moveToNext()) {
           Image image=new Image(cursor.getString(cursor.getColumnIndex("mypath")),
                   cursor.getInt(cursor.getColumnIndex("mytype")));
            result.add(image);
        }
        return result;
    }

    //增加
    public  void InsertSql(Image image, String l_id) {
        String sql = "insert into  infor(i_id,mypath,mytype,l_id) values(?,?,?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{image.getId(),image.getPath(),image.getType()+"",l_id});
    }

    //删除
    public void DeleteSql(String strId) {
        String sql = " DELETE FROM " + "infor"+
                "  WHERE l_id= ?";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{strId});
    }

    //更新
    public void UpdateSql(Infor infor) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update infor set mypath=?,mytype=?,l_id=? where i_id=?";
        db.execSQL(sql, new String[]{infor.getMypath(),infor.getMytype()+"",infor.getL_id(),infor.getI_id()});
    }


}