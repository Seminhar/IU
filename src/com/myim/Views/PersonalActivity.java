package com.myim.Views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.IU.R;
import com.myim.Beans.User;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import com.myim.SQLiteDB.ChatHistoryTblHelper;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.SQLiteDB.NotificationTblHelper;
import com.myim.model.ContactPeer;

public class PersonalActivity extends Activity {

    private User user;
    private String TAG = "PersonalActivity";
    private TextView tvNickname;
    private TextView tvSex;
    private TextView tvPhone;
    private TextView tvAddress;
    private TextView tvEmail;
    private ImageView ivProfilePic;
    private TextView btnAddFri;
    private TextView btnSendMsg;
    private TextView btnRemoveFri;
    private ImageButton personal_back_img = null;
    private ProgressDialog pdAddFri;
    ContactPeer cp = ContactPeer.getInstance(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Get Username from intent
        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("username");


        // Get widget
        setContentView(R.layout.personal_msg);
        btnAddFri = (TextView) findViewById(R.id.btnAddFriend);
        btnSendMsg = (TextView) findViewById(R.id.btnSendMsg);
        btnRemoveFri = (TextView) findViewById(R.id.btnRemoveFriend);
        personal_back_img = (ImageButton) findViewById(R.id.return1);
        initUI();
        personal_back_img.setOnClickListener(new OnClick());

        pdAddFri = new ProgressDialog(this);


        Log.i(TAG, user.toString());
        addData();


    }

    private class OnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return1:

                    PersonalActivity.this.finish();
                    break;
            }
        }
    }

    public void addData() {
        tvNickname = (TextView) findViewById(R.id.tvNickname);
        tvSex = (TextView) findViewById(R.id.tvSex);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);

        tvNickname.setText(user.getNickName());
        tvAddress.setText(user.getAddress());
        tvPhone.setText(user.getPhone());
        tvSex.setText(user.getSex());
        tvEmail.setText(user.getEmail());
        cp.getProfilePic(user.getUsername(),ivProfilePic);

    }

    private void initUI() {
        String username = user.getUsername();

        if (username.equals(Constant.USER_NAME)) {
            btnSendMsg.setVisibility(View.GONE);
            btnRemoveFri.setVisibility(View.GONE);
            btnAddFri.setVisibility(View.GONE);
        } else if (cp.contactList.containsKey(username)) {
            btnSendMsg.setVisibility(View.VISIBLE);
            btnRemoveFri.setVisibility(View.VISIBLE);
            btnAddFri.setVisibility(View.GONE);
            btnSendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("msgContent", "");
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(PersonalActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
            });

            btnRemoveFri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unFri();
                }
            });
        } else {
            // hide button
            btnSendMsg.setVisibility(View.GONE);
            btnRemoveFri.setVisibility(View.GONE);
            btnAddFri.setVisibility(View.VISIBLE);
            btnAddFri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFri();
                }
            });
        }
    }

    public void addFri() {
        pdAddFri.setTitle("请稍等..");
        pdAddFri.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean r = JabberConnection.getInstance().addFriend(user.getUsername());
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRegSuccess", r);
                bundle.putInt("op", 1);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });
        t.start();

    }

    public void unFri() {
        pdAddFri.setTitle("请稍等..");
        pdAddFri.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean r = JabberConnection.getInstance().unFriend(user.getUsername());
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isUnFriSuccess", r);
                bundle.putInt("op", 2);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        });
        t.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int op = (int) msg.getData().get("op");
            if (op == 1) // addFriend
            {
                boolean isRegSuccess = (boolean) msg.getData().get("isRegSuccess");
                if (isRegSuccess) {
                    org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
                    m.setBody("addFri");
                    m.setSubject("addFri");
                    JabberConnection.getInstance().sendMessage(user.getUsername(), m);
                    ContactActivity.needRefresh = true;
                    new ContactTblHelper(PersonalActivity.this).saveContact(user);
                    cp.contactList.put(user.getUsername(), user);
                    pdAddFri.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putString("username", user.getUsername());
                    bundle.putString("msgContent", "");
                    Intent intent = new Intent(PersonalActivity.this, ChatActivity.class);
                    intent.putExtras(bundle);


                    Toast.makeText(PersonalActivity.this, "加好友成功", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    PersonalActivity.this.finish();

                } else {
                    Toast.makeText(PersonalActivity.this, "网络异常 请稍候重新操作", Toast.LENGTH_LONG).show();
                }
            } else if (op == 2) {
                boolean isUnFriSuccess = (boolean) msg.getData().get("isUnFriSuccess");
                if (isUnFriSuccess) {
                    org.jivesoftware.smack.packet.Message m = new org.jivesoftware.smack.packet.Message();
                    m.setBody("delFri");
                    m.setSubject("delFri");
                    JabberConnection.getInstance().sendMessage(user.getUsername(), m);
                    cp.contactList.remove(user.getUsername());
                    new ContactTblHelper(PersonalActivity.this).removeContact(user.getUsername());
                    new NotificationTblHelper(PersonalActivity.this).removeNotification(user.getUsername());
                    new ChatHistoryTblHelper(PersonalActivity.this).removeChatHistory(user.getUsername());
                    // Refresh needed activity
                    MsgListAllActivity.needRefresh = true;
                    ContactActivity.needRefresh = true;
                    Intent intent = new Intent(PersonalActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tab", "iMsg");
                    intent.putExtras(bundle);
                    startActivity(intent);
                    PersonalActivity.this.finish();
                    pdAddFri.dismiss();

                } else {
                    Toast.makeText(PersonalActivity.this, "网络异常 请稍候重新操作", Toast.LENGTH_LONG).show();
                }
            }
            super.handleMessage(msg);
        }
    };
}
