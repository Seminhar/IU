package com.myim.SQLiteDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.myim.Beans.ChatMessage;
import com.myim.Beans.User;
import com.myim.NetService.Constant;

import java.util.ArrayList;

/**
 * Created by PC on 2015-05-15.
 */
public class ChatHistoryTblHelper {
    DBHelper dbHelper;

    public ChatHistoryTblHelper(Context context) {
        dbHelper = new DBHelper(context, "imdb.db", null, 1);
    }

    public void saveChatHistory(ChatMessage msg) {
        String from = User.getUsernameWithNoAt(msg.getFrom());
        String to = User.getUsernameWithNoAt(msg.getTo());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String qry = "INSERT INTO CHATHISTORY VALUES(null,'" + msg.getContent() + "','" + from + "','" + to + "','" + msg.getTime() + "'," + msg.getType() + ",'" + msg.getMime() + "');";
        System.out.println(qry);
        db.execSQL(qry);
        db.close();
        Log.e("Sucess", "Save !!");

    }

    public void removeChatHistory(String username) {
        //  String from = User.getNickNameByUserName(msg.getFrom());
        //  String to = User.getNickNameByUserName(msg.getTo());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String qry = "DELETE FROM ChatHistory WHERE (ChFrom='" + username + "' AND ChTo='" + Constant.USER_NAME + "') OR (ChFrom='" + Constant.USER_NAME + "' AND ChTo='" + username + "')";

        db.execSQL(qry);
        db.close();
        Log.e("Remove", "Remove ChatHistory !! " + qry);

    }

    public ArrayList<ChatMessage> getChatHistoryList(String other) {
        ArrayList<ChatMessage> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM CHATHISTORY WHERE (ChFrom='" + other + "' AND ChTo='" + Constant.USER_NAME + "') OR (ChFrom='" + Constant.USER_NAME + "' AND ChTo='" + other + "')", null);
        while (cursor.moveToNext()) {
            ChatMessage msg = new ChatMessage();
            msg.setId(cursor.getInt(cursor.getColumnIndex("ChId")));

            msg.setContent(cursor.getString(cursor.getColumnIndex("ChContent")));
            msg.setFrom(cursor.getString(cursor.getColumnIndex("ChFrom")));
            msg.setTo(cursor.getString(cursor.getColumnIndex("ChTo")));
            msg.setTime(cursor.getString(cursor.getColumnIndex("ChTime")));
            msg.setType(cursor.getInt(cursor.getColumnIndex("ChType")));

            msg.setMime(cursor.getString(cursor.getColumnIndex("ChMime")));
            list.add(msg);
        }


        db.close();
        return list;
    }

}
