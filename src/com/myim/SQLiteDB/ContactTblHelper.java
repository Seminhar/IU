package com.myim.SQLiteDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.myim.Beans.NotificationMsg;
import com.myim.Beans.User;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import org.jivesoftware.smack.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by PC on 2015-05-24.
 */
public class ContactTblHelper {

    DBHelper dbHelper;
    public ContactTblHelper(Context context)
    {
        dbHelper = new DBHelper(context,"imdb.db",null,1);
    }

    public HashMap<String,User> loadFromServer()
    {

        HashMap<String,User> users = new HashMap<String,User>();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Collection<RosterEntry> entries = JabberConnection.getInstance().getConnection().getRoster().getEntries() ;
        for(RosterEntry entry : entries)
        {

            User user = new User(entry.getUser());
            user.loadVCard();
            String qry = "INSERT OR REPLACE INTO Contact VALUES('"+ Constant.USER_NAME+"','"+user.getUsername()+"','"+user.getRealName()+"','"+user.getNickName()+"','"+user.getEmail()+"','"+user.getSex()+"','"+user.getAddress()+"');";
            db.execSQL(qry);
            users.put(user.getUsername(),user);
        }

        // System.out.println(qry);

        db.close();
        Log.e("Sucess", "Save !!");
        return  users;
    }
    public void saveContact(User user)
    {
        //  String from = User.getNickNameByUserName(msg.getFrom());
        //  String to = User.getNickNameByUserName(msg.getTo());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String qry = "INSERT OR REPLACE INTO Contact VALUES('"+ Constant.USER_NAME+"','"+user.getUsername()+"','"+user.getRealName()+"','"+user.getNickName()+"','"+user.getEmail()+"','"+user.getSex()+"','"+user.getAddress()+"');";
        System.out.println(qry);
        db.execSQL(qry);
        db.close();
        Log.e("Sucess", "Save !!");

    }
    public void removeContact(String username)
    {
        //  String from = User.getNickNameByUserName(msg.getFrom());
        //  String to = User.getNickNameByUserName(msg.getTo());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String qry = "DELETE FROM Contact WHERE friWith='"+ username+"' AND username='"+Constant.USER_NAME+"'";

        db.execSQL(qry);
        db.close();
        Log.e("Remove", "Remove !! "+qry);

    }
    public ArrayList<User> getContactList()
    {
        ArrayList<User> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Contact WHERE username='"+ Constant.USER_NAME+"'", null);
        while(cursor.moveToNext())
        {
            User user = new User(cursor.getString((cursor.getColumnIndex("friWith"))));
            user.setRealName(cursor.getString(cursor.getColumnIndex("realName")));
            user.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            user.setAddress(cursor.getString(cursor.getColumnIndex("address")));

            list.add(user);
        }
        db.close();
        return  list;
    }


    public HashMap<String,User> getContactMap()
    {
        HashMap<String,User> list = new HashMap<String,User>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Contact WHERE username='"+ Constant.USER_NAME+"'", null);
        while(cursor.moveToNext())
        {
            com.myim.Beans.User user = new com.myim.Beans.User(cursor.getString((cursor.getColumnIndex("friWith"))));
            user.setRealName(cursor.getString(cursor.getColumnIndex("realName")));
            user.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
            user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
            user.setAddress(cursor.getString(cursor.getColumnIndex("address")));

            list.put(user.getUsername(),user);
        }
        db.close();
        return  list;
    }
}
