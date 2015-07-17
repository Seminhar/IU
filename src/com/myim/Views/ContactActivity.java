package com.myim.Views;

import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.myim.Adapter.ContactAdapter;
import com.myim.Beans.User;
import com.myim.Operation.Contact_AssortView;
import android.app.Activity;
import android.os.Bundle;
import com.example.IU.R;
import com.myim.model.ContactPeer;
import com.myim.sort.HashList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/6.
 */
public class ContactActivity extends Activity {
    private ContactAdapter adapter;
    private static ExpandableListView eListView;
    private Contact_AssortView contactAssortView;
    public static List<String> names;
    public static HashList<String, String> nameHashList;
    public static boolean needRefresh = false;
    ContactPeer cp = ContactPeer.getInstance(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.contact_main);
        eListView = (ExpandableListView) findViewById(R.id.contact_list);
        contactAssortView = (Contact_AssortView) findViewById(R.id.assort);

        reloadList();
        adapter = new ContactAdapter(this, names);
        eListView.setAdapter(adapter);
        /**
         * 展开联系人列表
         */
        for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
            eListView.expandGroup(i);
        }


        /**
         *  右边字母视图字母按键回调显示
         */

        contactAssortView.setOnTouchAssortListener(new Contact_AssortView.OnTouchAssortListener() {
            PopupWindow popupWindow;

            View layoutView = LayoutInflater.from(ContactActivity.this).inflate(R.layout.contact_dialog_menu, null);
            TextView text = (TextView) layoutView.findViewById(R.id.content);

            public void onTouchAssortListener(String str) {
                int index = adapter.getAssort().getHashList().indexOfKey(str);
                if (index != -1) {
                    eListView.setSelectedGroup(index);
                }
                if (popupWindow != null) {
                    text.setText(str);
                } else {
                    //指定窗口大小,以内容大小为约束
                    popupWindow = new PopupWindow(layoutView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
                    // 显示在Activity的根视图中心
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                }
                text.setText(str);
            }

            public void onTouchAssortUP() {
                if (popupWindow != null)
                    popupWindow.dismiss();
                popupWindow = null;
            }
        });

    }


    @Override
    protected void onPostResume() {


        reloadList();
        adapter = new ContactAdapter(this, names);
        eListView.setAdapter(adapter);
        /**
         * 展开联系人列表
         */
        for (int i = 0, length = adapter.getGroupCount(); i < length; i++) {
            eListView.expandGroup(i);
        }


        /**
         *  右边字母视图字母按键回调显示
         */

        contactAssortView.setOnTouchAssortListener(new Contact_AssortView.OnTouchAssortListener() {
            PopupWindow popupWindow;

            View layoutView = LayoutInflater.from(ContactActivity.this).inflate(R.layout.contact_dialog_menu, null);
            TextView text = (TextView) layoutView.findViewById(R.id.content);

            public void onTouchAssortListener(String str) {
                int index = adapter.getAssort().getHashList().indexOfKey(str);
                if (index != -1) {
                    eListView.setSelectedGroup(index);
                }
                if (popupWindow != null) {
                    text.setText(str);
                } else {
                    //指定窗口大小,以内容大小为约束
                    popupWindow = new PopupWindow(layoutView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
                    // 显示在Activity的根视图中心
                    popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                }
                text.setText(str);
            }

            public void onTouchAssortUP() {
                if (popupWindow != null)
                    popupWindow.dismiss();
                popupWindow = null;
            }
        });


        needRefresh = false;


        super.onPostResume();
    }

    public void reloadList() {
        names = new ArrayList<String>();
        if (cp.contactList != null) {
            // Collection<User> users =   ContactPeer.contactList.values();

            for (User user : cp.contactList.values()) {
                names.add(user.getNickName() + "@" + user.getUsername());
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }


}
