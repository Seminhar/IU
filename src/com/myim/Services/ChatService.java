package com.myim.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.example.IU.R;
import com.myim.Beans.ChatMessage;
import com.myim.Beans.NotificationMsg;
import com.myim.Beans.User;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import com.myim.Operation.GetTimeFormat;

import com.myim.SQLiteDB.ChatHistoryTblHelper;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.SQLiteDB.NotificationTblHelper;
import com.myim.Util.MessageProcessorUtil;
import com.myim.Views.ChatActivity;
import com.myim.Views.ContactActivity;
import com.myim.Views.PersonalActivity;
import com.myim.model.ContactPeer;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * Created by PC on 2015-05-11.
 */
public class ChatService extends Service {
    private String TAG = "ChatService";
    private final int NOTIFY_ID = 1;
    Context context = null;
    JabberConnection jc;
    ContactPeer cp = ContactPeer.getInstance(this);

    @Override
    public void onCreate() {
        jc = JabberConnection.getInstance();
        initChatListener();
        Constant.ch = true;
        context = getApplicationContext();
        Log.i(TAG, "ChatService onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "ChatService started");
        Constant.ch = true;
        return super.onStartCommand(intent, flags, startId);
    }

    public void initChatListener() {
        jc.getConnection().addPacketListener(chatPacketListener, new MessageTypeFilter(Message.Type.chat));

    }

    public PacketListener chatPacketListener;

    {
        chatPacketListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) throws SmackException.NotConnectedException {
                Message msg = (Message) packet;
                Log.e("packet messsage", packet.toXML().toString());
                if (msg != null && msg.getBody() != null && !msg.getBody().equals("")) {

                    MessageProcessorUtil mpu = new MessageProcessorUtil(context);
                    mpu.process(msg);
                }
            }

        };
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
