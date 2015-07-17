package com.myim.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.example.IU.R;
import com.myim.Adapter.ViewPagerAdapter;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.Services.ChatService;
import com.myim.Services.SubscribeService;
import com.myim.model.ContactPeer;

import java.util.ArrayList;
import java.util.List;

public class AndyViewPagerActivity extends Activity implements OnClickListener, OnPageChangeListener {

    private ViewPager vp;
    private ViewPagerAdapter vpAdapter;
    private List<View> views;
    private ImageView button;
    ContactPeer cp = ContactPeer.getInstance(this);
    //引导图片资源
    private static final int[] pics = {R.drawable.guide1,
            R.drawable.guide2, R.drawable.guide3,
            R.drawable.guide4};

    //底部小点图片
    private ImageView[] dots;

    //记录当前选中位置
    private int currentIndex;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏

        setContentView(R.layout.guide_activity);
        button = (ImageView) findViewById(R.id.button);
        views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        //初始化引导图片列表
        for (int pic : pics) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pic);
            views.add(iv);
        }
        vp = (ViewPager) findViewById(R.id.viewpager);
        //初始化Adapter
        vpAdapter = new ViewPagerAdapter(views);
        vp.setAdapter(vpAdapter);
        //绑定回调
        vp.setOnPageChangeListener(this);
        //初始化底部小点
        initDots();
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!Constant.isLogin && !Constant.PASS.equals("") && !Constant.USER_NAME.equals("")) {
                    // Login
                    new Thread(new Runnable() {
                        public void run() {
                            JabberConnection jc = JabberConnection.getInstance();
                            jc = JabberConnection.getInstance();
                            jc.connectToXmppServer();
                            // Start Services

                            // Contact Listener Service
                            Intent subscribeService = new Intent(AndyViewPagerActivity.this, SubscribeService.class);
                            AndyViewPagerActivity.this.startService(subscribeService);

                            // Chat Listener Service
                            Intent chatService = new Intent(AndyViewPagerActivity.this, ChatService.class);
                            AndyViewPagerActivity.this.startService(chatService);

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
                            System.out.println("返回码----》" + msg.what);
                        }
                    }).start();
                } else if (!Constant.isLogin && (Constant.PASS.equals("") || Constant.USER_NAME.equals(""))) {
                    startActivity(new Intent(AndyViewPagerActivity.this, LoginActivity.class));
                    AndyViewPagerActivity.this.finish();
                }
                finish();
            }
        });

    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

        dots = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(true);//都设为灰色
            dots[i].setOnClickListener(this);
            dots[i].setTag(i);//设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(false);//设置为白色
    }

    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }

        vp.setCurrentItem(position);
    }

    /**
     * 这只当前引导小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }

        dots[positon].setEnabled(false);
        dots[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    //当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    //当前页面被滑动时调用
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    //当新的页面被选中时调用
    @Override
    public void onPageSelected(int arg0) {
        //设置底部小点选中状态
        setCurDot(arg0);
        if (arg0 == 3) {
            button.setVisibility(View.VISIBLE);

        } else {
            button.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == 0) {
                Toast.makeText(AndyViewPagerActivity.this, "登陆失败!请检查网络或账号", Toast.LENGTH_SHORT).show();
            }
            if (msg.what == 1) {
                //ContactPeer contactPeer = new ContactPeer(AndyViewPagerActivity.this);
                cp.loadDataFromDB();
                cp.contactList = new ContactTblHelper(AndyViewPagerActivity.this).loadFromServer();
                Intent intent = new Intent();
                intent.setClass(AndyViewPagerActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tab", "iMsg");
                intent.putExtras(bundle);
                startActivity(intent);
                AndyViewPagerActivity.this.finish();
            }
            super.handleMessage(msg);
        }
    };
}