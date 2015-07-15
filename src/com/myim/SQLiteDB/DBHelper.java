package com.myim.SQLiteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by PC on 2015-05-11.
 */
public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.e("dbhelper", "constructor");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("dbhelper", "onCreate");
        String qry = "CREATE TABLE `Notification` (" +
                "`username` TEXT NOT NULL ," +
                "`id`TEXT NOT NULL ," +
                "`title`TEXT NOT NULL," +
                "`content`TEXT," +
                "`from`TEXT NOT NULL," +
                "`to`TEXT," +
                "`time`TEXT NOT NULL," +
                "`type`INTEGER NOT NULL," +
                "`status`INTEGER NOT NULL," +
                "PRIMARY KEY(username,id)" +
                ");";

        String qry1 = "CREATE TABLE `ChatHistory` (" +
                "`ChId` INTEGER NOT NULL  PRIMARY KEY AUTOINCREMENT ," +

                "`ChContent`TEXT," +
                "`ChFrom`TEXT NOT NULL," +
                "`ChTo`TEXT NOT NULL," +
                "`ChTime`TEXT NOT NULL," +
                "`ChType`INTEGER NOT NULL," +
                "`ChMime`TEXT NOT NULL" +

                ");";

        String qry2 = "CREATE TABLE `Contact` (" +
                "`username` TEXT NOT NULL ," +
                "`friWith`TEXT NOT NULL ," +
                "`realName`TEXT NOT NULL," +
                "`nickName`TEXT," +
                "`email`TEXT NOT NULL," +
                "`sex`TEXT," +
                "`address`TEXT NOT NULL," +

                "PRIMARY KEY(username,friWith)" +
                ");";
        // System.out.println(qry);
        //  String qry = "create table t_users(id integer primary key autoincrement,username varchar(200),pass varchar(200) );";
        db.execSQL(qry);
        db.execSQL(qry1);
        db.execSQL(qry2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
