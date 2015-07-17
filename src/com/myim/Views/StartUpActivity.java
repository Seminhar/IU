package com.myim.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;
import com.example.IU.R;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.Services.ChatService;
import com.myim.Services.SubscribeService;
import com.myim.model.ContactPeer;

/**
 * Created by PC on 2015-05-29.
 */
public class StartUpActivity extends Activity {
    public static SharedPreferences sharedPreferences ;
    private SharedPreferences.Editor editor ;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.startup);


        sharedPreferences = getSharedPreferences("imset", this.MODE_PRIVATE);
        if(sharedPreferences.getAll().size()==0) // First time
        {
            //initdata
            editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLoad",true);
            editor.putBoolean("notification",true);
            editor.putBoolean("sound",true);
            editor.putBoolean("vibrate",true);
            //editor.putBoolean("isLogin",true);
            editor.putString("username", "");
            editor.putString("p", "");
            editor.commit();
        }


        Constant.isFirstLoad = sharedPreferences.getBoolean("isFirstLoad",false);
        Constant.notification = sharedPreferences.getBoolean("notification",true);
        Constant.sound = sharedPreferences.getBoolean("sound", true);
        Constant.vibrate = sharedPreferences.getBoolean("vibrate", true);
        Constant.USER_NAME = sharedPreferences.getString("username", "");
        Constant.PASS = sharedPreferences.getString("p", "");


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


            }
        },1500);
        if(Constant.isFirstLoad)
        {
            //标记不再是第一次运行了
            Constant.isFirstLoad = false;
            editor = sharedPreferences.edit();
            editor.putBoolean("isFirstLoad",false);
            editor.commit();
            //调到导航页
            Intent intent=new Intent();
            intent.setClass(StartUpActivity.this,AndyViewPagerActivity.class);
            StartUpActivity.this.startActivity(intent);
            StartUpActivity.this.finish();
        }
        // If have username, automatically login
        else if(!Constant.PASS.equals("") && !Constant.USER_NAME.equals(""))
        {
            // Login
            new Thread(new Runnable() {
                public void run() {

                    JabberConnection jc = JabberConnection.getInstance();
                    jc = JabberConnection.getInstance();

                    Log.i("xmppCon", "connted = false");
                    jc.connectToXmppServer();
                    // Start Services
                    if (jc.getConnection().isConnected()) {
                        // Contact Listener Service
                        Intent subscribeService = new Intent(StartUpActivity.this, SubscribeService.class);
                        StartUpActivity.this.startService(subscribeService);

                        // Chat Listener Service
                        Intent chatService = new Intent(StartUpActivity.this, ChatService.class);
                        StartUpActivity.this.startService(chatService);

                        while (Constant.sub == false || Constant.ch == false) {
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        Boolean isLogin = jc.login(Constant.USER_NAME + "@" + Constant.SERVICE_NAME, Constant.PASS);
                        Log.i("SubscribeS", "login sucess");
                        android.os.Message msg = new android.os.Message();
                        if (isLogin) {
                            msg.what = 1;
                            ///Constant.isLogin = true;
                        } else {
                            msg.what = 0;
                        }

                        handler.sendMessage(msg);
                    } else {
                        Message msg = new Message() ;
                        msg.what =2 ;
                        handler.sendMessage(msg);
                    }


                }
            }).start();
        }
        else if( (Constant.PASS.equals("") || Constant.USER_NAME.equals("")))
        {
            startActivity(new Intent(StartUpActivity.this,LoginActivity.class));
            StartUpActivity.this.finish();
        }
        super.onCreate(savedInstanceState);
    }
    Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0) {
                Toast.makeText(StartUpActivity.this, "登陆失败!请检查网络或账号", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 2) {
                Toast.makeText(StartUpActivity.this, "网络连接有误，请重新连接网络", Toast.LENGTH_SHORT).show();
                StartUpActivity.this.finish();
            }
            if (msg.what == 1) {
                ContactPeer contactPeer = ContactPeer.getInstance(StartUpActivity.this);
                contactPeer.loadDataFromDB();
                contactPeer.contactList= new ContactTblHelper(StartUpActivity.this).loadFromServer();
//                // Contact Listener Service
//                Intent subscribeService = new Intent(LoginActivity.this,SubscribeService.class);
//                LoginActivity.this.startService(subscribeService);
                //Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setClass(StartUpActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tab", "iMsg");
                intent.putExtras(bundle);
                startActivity(intent);
                StartUpActivity.this.finish();
            }
            super.handleMessage(msg);
        }
    };
}