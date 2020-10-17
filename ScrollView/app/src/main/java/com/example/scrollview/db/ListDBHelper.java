package com.example.scrollview.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ListDBHelper extends SQLiteOpenHelper  {
    private final static String DATABASE_NAME = "mydb";//数据库名字
    private final static int DATABASE_VERSION = 1;  //等级 注意

    private final static String SQL_CREATE_DATABASE = "CREATE TABLE list (" +
            "  l_id  varchar(50) PRIMARY KEY, " +
            "  myText  varchar(300)," +
            "  star  int CHECK( star = 1 OR star = -1), " +
            "  mytime  varchar(50) not null" +
            ");";
    private final static String SQL_CREATE_DATABASE2 = "CREATE Table infor(" +
            "  i_id   varchar(50) PRIMARY Key," +
            "  mypath  varchar(300) not null," +
            "  mytype  int CHECK(mytype=1 or mytype=2 or mytype=3 or mytype=4)," +
            "  l_id   varchar(50)," +
            "  FOREIGN KEY(l_id) REFERENCES list(l_id) " +
            ");";

    private final static String SQL_DELETE_DATABASE1 = "DROP TABLE IF EXISTS list";
    private final static String SQL_DELETE_DATABASE2 = "DROP TABLE IF EXISTS infor";

        public ListDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override

        public void onCreate(SQLiteDatabase sqLiteDatabase) {        //创建数据库
            sqLiteDatabase.execSQL(SQL_CREATE_DATABASE2);
            sqLiteDatabase.execSQL(SQL_CREATE_DATABASE);


        }


        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {        //当数据库升级时被调用，首先删除旧表，然后调用OnCreate()创建新表
            sqLiteDatabase.execSQL(SQL_DELETE_DATABASE1);
            sqLiteDatabase.execSQL(SQL_DELETE_DATABASE2);
            onCreate(sqLiteDatabase);
        }
    }


