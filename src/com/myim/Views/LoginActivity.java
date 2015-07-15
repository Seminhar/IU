package com.myim.Views;

import android.os.Environment;
import android.util.Log;
import com.myim.Beans.ChatMessage;
import com.myim.Beans.NotificationMsg;
import com.myim.Beans.User;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.example.IU.R;
import com.myim.Operation.GetTimeFormat;
import com.myim.SQLiteDB.ChatHistoryTblHelper;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.SQLiteDB.NotificationTblHelper;
import com.myim.Services.ChatService;
import com.myim.Services.SubscribeService;
import com.myim.model.ContactPeer;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.*;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.offline.OfflineMessageManager;

import java.util.ArrayList;


/**
 * Created by Administrator on 2015/4/18.
 */
public class LoginActivity extends Activity {
    Button btn_login;
    TextView btn_register;
    EditText etusername = null;
    EditText etpassword = null;
    String username;
    String password;
    boolean isLogin = false;
    JabberConnection jc = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        Context context = getApplicationContext();
        SmackAndroid.init(context);
        etusername = (EditText) findViewById(R.id.etusername);
        etpassword = (EditText) findViewById(R.id.etpassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (TextView) findViewById(R.id.register_link);

        btn_login.setOnClickListener(new click());
        btn_register.setOnClickListener(new click());
        String location;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            location = context.getExternalFilesDir(null).getAbsolutePath();
        } else
            location = context.getCacheDir().getAbsolutePath();
        Constant.LOCATION = location;
    }

    /**
     * 点击响应事件监听
     */
    class click implements OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    clickLogin();
                    break;
                case R.id.register_link:
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private void clickLogin() {
        username = etusername.getText().toString().trim();

        if (username.equals("") || username == null || username.length() <= 0) {
            etusername.requestFocus();
            Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        } else {
            username = etusername.getText().toString().trim();
        }
        System.out.println("用户名" + username);
        password = etpassword.getText().toString().trim();
        if (password == null || username.equals("") || password.length() <= 0) {
            etpassword.requestFocus();
            Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        } else {
            password = etpassword.getText().toString().trim();
        }
        System.out.println("密码" + password);

        /**
         * 网络连接线程启动
         */

        new Thread(new Runnable() {
            public void run() {
                jc = JabberConnection.getInstance();
                jc.connectToXmppServer();
                // Start Services


                // Contact Listener Service
                Intent subscribeService = new Intent(LoginActivity.this, SubscribeService.class);
                LoginActivity.this.startService(subscribeService);

                // Chat Listener Service
                Intent chatService = new Intent(LoginActivity.this, ChatService.class);
                LoginActivity.this.startService(chatService);

                while (Constant.sub == false || Constant.ch == false) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isLogin = jc.login(username + "@" + Constant.SERVICE_NAME, password);
                Log.i("SubscribeS", "login sucess");
                android.os.Message msg = new android.os.Message();
                if (isLogin) {
                    msg.what = 1;

                    //Constant.USER_NAME=username;
                    // Constant.PASS=password;


                } else {
                    msg.what = 0;
                }

                handler.sendMessage(msg);
                System.out.println("返回码----》" + msg.what);
            }
        }).start();

    }

    Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0) {
                Toast.makeText(LoginActivity.this, "登陆失败!请检查网络或账号", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 1) {
                ContactPeer contactPeer = new ContactPeer(LoginActivity.this);
                contactPeer.loadDataFromDB();
                contactPeer.contactList = new ContactTblHelper(LoginActivity.this).loadFromServer();
//                // Contact Listener Service
//                Intent subscribeService = new Intent(LoginActivity.this,SubscribeService.class);
//                LoginActivity.this.startService(subscribeService);
                //Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tab", "iMsg");
                intent.putExtras(bundle);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            super.handleMessage(msg);
        }
    };

}
