package com.example.scrollview.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.scrollview.GUID;
import com.example.scrollview.db.javabean.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListDB {
    private static final String TAG = "myTag";
    private static ListDBHelper mDbHelper;   //采用单例模式

    public ListDB(Context context){
            mDbHelper = new ListDBHelper(context);
    }

    public void close() {
        if (mDbHelper != null) mDbHelper.close();
    }


    //得到全部单词列表
    public ArrayList<Map<String, String>> getAlllist() {
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
    public  String InsertUserSql(String myText, int star,String mytime) {
        String sql = "insert into  list(l_id,myText,star,mytime) values(?,?,?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String l_id=GUID.getGUID();
        db.execSQL(sql, new String[]{l_id,myText,star+"",mytime});
        return l_id;
    }

    //删除
    public void DeleteUseSql(String strId) {
        String sql = " DELETE FROM " + "list"+
                "  WHERE l_id= ?";
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL(sql, new String[]{strId});
    }

    //更新
    public void UpdateText(String l_id,String myText,String myTime) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update list set mytime=?,myText=? where l_id=?";
        db.execSQL(sql, new String[]{myTime,myText,l_id});
    }

}
