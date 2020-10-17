package com.example.scrollview.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.scrollview.GUID;
import com.example.scrollview.db.javabean.List;

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


    public ArrayList<Map<String, String>> getAllInf() {
        if (mDbHelper == null) {
            return null;
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                "l_id",
                "myText",
                "star",
                "mytime"
        };

        Cursor c = db.query(
                "list",  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        return ConvertCursor(c);
    }

    //将游标转化
    private ArrayList<Map<String, String>> ConvertCursor(Cursor cursor) {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            Log.v("Tag",cursor.getString(cursor.getColumnIndex("l_id")));
            map.put("l_id", cursor.getString(cursor.getColumnIndex("l_id")));
            map.put("myText", cursor.getString(cursor.getColumnIndex("myText")));
            map.put("star", cursor.getInt(cursor.getColumnIndex("star"))+"");
            map.put("mytime", cursor.getString(cursor.getColumnIndex("mytime")));
            result.add(map);
        }
        return result;
    }

    //增加
    public  void InsertUserSql(String myText, int star,String mytime) {
        String sql = "insert into  list(l_id,myText,star,mytime) values(?,?,?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{GUID.getGUID(),myText,star+"",mytime});
    }

    //删除
    public void DeleteUseSql(String strId) {
        String sql = " DELETE FROM " + "list"+
                "  WHERE l_id= ?";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{strId});
    }

    //更新单词
    public void UpdateUseSql(List list) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update list set mytime=?,myText=?,star=? where _id=?";
        db.execSQL(sql, new String[]{list.getMytime(),list.getMyText(),list.getStar()+"",list.getL_id()});
    }


}
