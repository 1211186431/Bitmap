package com.example.scrollview.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.scrollview.db.javabean.*;

import java.util.ArrayList;

public class ListDB {
    private static final String TAG = "myTag";
    private static ListDBHelper mDbHelper;   //采用单例模式

    public ListDB(Context context){
            mDbHelper = new ListDBHelper(context);
    }

    public void close() {
        if (mDbHelper != null) mDbHelper.close();
    }


    //得到
    public ArrayList<MyList> getAllList() {
        if (mDbHelper == null) {
            return null;
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                "l_id",
                "myText",
                "star",
                "mytime",
                "iTime"
        };

        Cursor c = db.query(
                "list",  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                "star DESC,mytime DESC"                                // The sort order
        );

        return ConvertCursor(c);
    }
    //得到
    public ArrayList<MyList> getList(String l_id) {
        if (mDbHelper == null) {
            return null;
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                "l_id",
                "myText",
                "star",
                "mytime",
                "iTime"
        };
        String where = "l_id = ?";
        String[] params = {l_id};
        Cursor c = db.query(
                "list",  // The table to query
                projection,                               // The columns to return
                where,                                // The columns for the WHERE clause
                params,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        return ConvertCursor(c);
    }
    //将游标转化
    private ArrayList<MyList> ConvertCursor(Cursor cursor) {
        ArrayList<MyList> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            MyList list=new MyList( cursor.getString(cursor.getColumnIndex("l_id")),
                    cursor.getString(cursor.getColumnIndex("myText")),
                    cursor.getInt(cursor.getColumnIndex("star")),
                    cursor.getString(cursor.getColumnIndex("mytime")),
                    cursor.getString(cursor.getColumnIndex("iTime")));
            result.add(list);
        }
        return result;
    }

    //增加
    public  String InsertUserSql(String myText, int star,String mytime) {
        String sql = "insert into  list(l_id,myText,star,mytime,iTime) values(?,?,?,?,?)";
        //Gets the data repository in write mode*/
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String l_id=GUID.getGUID();
        db.execSQL(sql, new String[]{l_id,myText,star+"",mytime,mytime});
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
    public void insertStar(String l_id){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update list set star=? where l_id=?";
        db.execSQL(sql, new String[]{1+"",l_id});
    }
    public void deleteStar(String l_id){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "update list set star=? where l_id=?";
        db.execSQL(sql, new String[]{-1+"",l_id});
    }
    //查找
    public ArrayList<MyList> SearchUseSql(String strSearch) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String sql = "select * from list where myText like ? order by myText desc";
        Cursor c = db.rawQuery(sql, new String[]{"%" + strSearch + "%"});
        return ConvertCursor(c);
    }

}
