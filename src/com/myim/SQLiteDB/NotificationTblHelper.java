package com.myim.SQLiteDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.myim.Beans.NotificationMsg;
import com.myim.Beans.User;
import com.myim.NetService.Constant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PC on 2015-05-11.
 */
public class NotificationTblHelper {
    DBHelper dbHelper;

    public NotificationTblHelper(Context context) {
        dbHelper = new DBHelper(context, "imdb.db", null, 1);
    }

    public void saveNotification(NotificationMsg msg) {
        String from = User.getUsernameWithNoAt(msg.getFrom());
        String to = User.getUsernameWithNoAt(msg.getTo());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String qry = "INSERT OR REPLACE INTO NOTIFICATION VALUES('" + Constant.USER_NAME + "','" + msg.getId() + "','" + msg.getTitle() + "','" + msg.getContent() + "','" + from + "','" + to + "','" + msg.getTime() + "'," + msg.getType() + "," + msg.getStatus() + ");";
        System.out.println(qry);
        db.execSQL(qry);
        db.close();
        Log.e("Sucess", "Save !!");

    }

    public HashMap<String, NotificationMsg> getNotificationMap() {
        HashMap<String, NotificationMsg> map = new HashMap<String, NotificationMsg>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NOTIFICATION WHERE username='" + Constant.USER_NAME + "'", null);
        if (cursor.moveToFirst()) {
            NotificationMsg noti = new NotificationMsg();
            noti.setId(cursor.getString(cursor.getColumnIndex("id")));
            noti.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            noti.setContent(cursor.getString(cursor.getColumnIndex("content")));
            noti.setFrom(cursor.getString(cursor.getColumnIndex("from")));
            noti.setTo(cursor.getString(cursor.getColumnIndex("to")));
            noti.setTime(cursor.getString(cursor.getColumnIndex("time")));
            noti.setType(cursor.getInt(cursor.getColumnIndex("type")));
            noti.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            map.put(noti.getId(), noti);
        }


        db.close();

        return map;
    }

    public void removeNotification(String username) {
        //  String from = User.getNickNameByUserName(msg.getFrom());
        //  String to = User.getNickNameByUserName(msg.getTo());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String qry = "DELETE FROM Notification WHERE username='" + Constant.USER_NAME + "' AND id='" + username + "'";

        db.execSQL(qry);
        db.close();
        Log.e("Remove", "Remove Noti !! " + qry);

    }

    public ArrayList<NotificationMsg> getNotificationList() {
        ArrayList<NotificationMsg> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM NOTIFICATION WHERE username='" + Constant.USER_NAME + "'", null);
        while (cursor.moveToNext()) {
            NotificationMsg noti = new NotificationMsg();
            noti.setId(cursor.getString(cursor.getColumnIndex("id")));
            noti.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            noti.setContent(cursor.getString(cursor.getColumnIndex("content")));
            noti.setFrom(cursor.getString(cursor.getColumnIndex("from")));
            noti.setTo(cursor.getString(cursor.getColumnIndex("to")));
            noti.setTime(cursor.getString(cursor.getColumnIndex("time")));
            noti.setType(cursor.getInt(cursor.getColumnIndex("type")));
            noti.setType(cursor.getInt(cursor.getColumnIndex("type")));
            noti.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
            list.add(noti);
        }


        db.close();
        return list;
    }
}
