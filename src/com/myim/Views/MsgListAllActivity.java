package com.myim.Views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;
import com.myim.Adapter.MsgListAdapter;
import com.myim.Beans.NotificationMsg;
import com.myim.NetService.Constant;
import com.myim.Operation.DeleteMsgListView;
import com.myim.Operation.GetTimeFormat;
import com.myim.Beans.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import com.example.IU.R;
import com.myim.Operation.MsgListView;
import com.myim.SQLiteDB.NotificationTblHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/5/3.
 */
public class MsgListAllActivity extends Activity {


    private List<NotificationMsg> data = new ArrayList<NotificationMsg>();
    HashMap<String, NotificationMsg> dataMap ;
    private DeleteMsgListView mListView;
    private MsgListAdapter mAdapter;
    private BroadReceiver receiver;
    public static boolean needRefresh = false;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataMap = new HashMap<String, NotificationMsg>();
        data = new NotificationTblHelper(this).getNotificationList();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.msg_all_main);

        findView();
        initReceiver();

    }

    @Override
    protected void onPostResume() {



        if(needRefresh) {
           refreshList();
            needRefresh = false;

        }
        super.onPostResume();
    }

    private void initReceiver()
    {
        receiver = new BroadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CHAT_MSG);
        filter.addAction(Constant.ROSTER_ACTION);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void findView() {
        mListView = (DeleteMsgListView) findViewById(R.id.mListView);
        mAdapter = new MsgListAdapter(MsgListAllActivity.this, data);
        mListView.setAdapter(mAdapter);

        /**
         * 删除消息项
         */
        mAdapter.setOnItemClickListener(new MsgListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClickListener(View v, int position) {

                String username = data.get(position).getId();
                String msgContent = data.get(position).getContent();
                int type = data.get(position).getType();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("msgContent", msgContent);
                if(type==NotificationMsg.CHAT_MSG || type==NotificationMsg.CHAT_IMG || type==NotificationMsg.CHAT_VO) {
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    intent.setClass(MsgListAllActivity.this, ChatActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MsgListAllActivity.this,"no op",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAdapter.setOnRightItemClickListener(new MsgListAdapter.onRightItemClickListener() {
            public void onRightItemClick(View v, int position) {
                data.remove(position);

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {

        // mAdapter.notifyDataSetChanged();
        super.onResume();
    }
    public void refreshList()
    {
        data.clear();
        data.addAll(new NotificationTblHelper(MsgListAllActivity.this).getNotificationList());
        mAdapter.notifyDataSetChanged();
    }
    private class BroadReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("Onreceive:","Notif");
            refreshList();
//
}
    }
}
