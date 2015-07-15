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

    @Override
    public void onCreate() {
        jc = JabberConnection.getInstance();
        // ContactPeer contactPeer = new ContactPeer(ChatService.this);
        // contactPeer.contactList= new ContactTblHelper(ChatService.this).loadFromServer();
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


                    if (msg.getSubject() != null && msg.getSubject().equals("img")) {
                        Log.e(TAG, "Receive Image");
                        User newUser = ContactPeer.contactList.get(User.getUsernameWithNoAt(msg.getFrom()));

                        NotificationMsg noti = new NotificationMsg();
                        String from = User.getUsernameWithNoAt(msg.getFrom());
                        noti.setId(from);
                        noti.setType(NotificationMsg.CHAT_IMG);
                        noti.setStatus(NotificationMsg.UNREAD);
                        noti.setTitle(from);
                        noti.setFrom(from);
                        noti.setContent(msg.getBody());
                        String time = new GetTimeFormat().getTimeFull();
                        noti.setTime(time);

                        ChatMessage chatMsg = new ChatMessage(0, msg.getBody(), from, Constant.USER_NAME, time, ChatMessage.RECEIVED_MSG, ChatMessage.IMG_MIME);
                        new ChatHistoryTblHelper(ChatService.this).saveChatHistory(chatMsg);
                        NotificationTblHelper notificationTblHelper = new NotificationTblHelper(ChatService.this);
                        notificationTblHelper.saveNotification(noti);

                        Intent i = new Intent();
                        i.putExtra("roster", noti);
                        i.putExtra("chatMsg", chatMsg);
                        i.setAction(Constant.CHAT_MSG);
                        sendBroadcast(i);
                    } else if (msg.getSubject() != null && msg.getSubject().equals("vo")) {
                        Log.e(TAG, "Receive Voice");
                        User newUser = ContactPeer.contactList.get(User.getUsernameWithNoAt(msg.getFrom()));
                        NotificationMsg noti = new NotificationMsg();
                        // String from = User.getUsernameWithNoAt(msg.getFrom());
                        noti.setId(newUser.getUsername());
                        noti.setType(NotificationMsg.CHAT_VO);
                        noti.setStatus(NotificationMsg.UNREAD);
                        noti.setTitle(newUser.getNickName());
                        noti.setFrom(newUser.getNickName());
                        noti.setContent(msg.getBody());
                        String time = new GetTimeFormat().getTimeFull();
                        noti.setTime(time);

                        ChatMessage chatMsg = new ChatMessage(0, msg.getBody(), User.getUsernameWithNoAt(msg.getFrom()), Constant.USER_NAME, time, ChatMessage.RECEIVED_MSG, ChatMessage.VOICE_MIME);
                        new ChatHistoryTblHelper(ChatService.this).saveChatHistory(chatMsg);
                        NotificationTblHelper notificationTblHelper = new NotificationTblHelper(ChatService.this);
                        notificationTblHelper.saveNotification(noti);

                        Intent i = new Intent();
                        i.putExtra("roster", noti);
                        i.putExtra("chatMsg", chatMsg);
                        i.setAction(Constant.CHAT_MSG);
                        sendBroadcast(i);
                    } else {
                        if (msg.getSubject() != null && msg.getSubject().equals("addFri")) {
                            String user = User.getUsernameWithNoAt(msg.getFrom());
                            User newUser = new User(user);
                            newUser.loadVCard();


                            NotificationMsg noti = new NotificationMsg();
                            //  String id = "sys"+System.currentTimeMillis();
                            noti.setId("sys" + System.currentTimeMillis());
                            noti.setType(NotificationMsg.SUBSCRIBE_MSG);
                            noti.setStatus(NotificationMsg.UNREAD);
                            noti.setTitle("新好友");
                            noti.setContent("\"" + newUser.getNickName() + "\"已经成为你好友");
                            noti.setFrom(user);
                            String time = new GetTimeFormat().getTimeFull();
                            noti.setTime(time);


                            Intent intent = new Intent();
                            // Save to database
                            NotificationTblHelper notificationTblHelper = new NotificationTblHelper(ChatService.this);
                            notificationTblHelper.saveNotification(noti);

                            intent.setAction(Constant.ROSTER_ACTION);
                            intent.putExtra("roster", noti);

                            ContactPeer.contactList.put(user, newUser);
                            new ContactTblHelper(ChatService.this).saveContact(newUser);
                            ContactActivity.needRefresh = true;
                            sendBroadcast(intent);
                        } else {
                            Log.e("sad", "Receive Text msg");
                            User newUser = ContactPeer.contactList.get(User.getUsernameWithNoAt(msg.getFrom()));

                            NotificationMsg noti = new NotificationMsg();
                            //String from = User.getUsernameWithNoAt(msg.getFrom());
                            noti.setId(newUser.getUsername());
                            noti.setType(NotificationMsg.CHAT_MSG);
                            noti.setStatus(NotificationMsg.UNREAD);
                            noti.setTitle(newUser.getNickName());
                            noti.setFrom(newUser.getNickName());
                            noti.setContent(msg.getBody());
                            String time = new GetTimeFormat().getTimeFull();
                            noti.setTime(time);

                            ChatMessage chatMsg = new ChatMessage(0, msg.getBody(), newUser.getUsername(), Constant.USER_NAME, time, ChatMessage.RECEIVED_MSG, ChatMessage.TEXT_MIME);
                            new ChatHistoryTblHelper(ChatService.this).saveChatHistory(chatMsg);

                            NotificationTblHelper notificationTblHelper = new NotificationTblHelper(ChatService.this);
                            notificationTblHelper.saveNotification(noti);
                            Intent i = new Intent();
                            i.putExtra("roster", noti);
                            i.putExtra("chatMsg", chatMsg);
                            i.setAction(Constant.CHAT_MSG);
                            sendBroadcast(i);


                            /**
                             * 标题栏消息提醒
                             */
                            Bundle bundle = new Bundle();
                            bundle.putString("username", chatMsg.getFrom());
                            bundle.putString("msgContent", chatMsg.getContent());
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtras(bundle);

                            NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context);
                            // 下面三个参数是必须要设定的
                            notifyBuilder.setSmallIcon(R.drawable.icon);
                            notifyBuilder.setContentTitle(newUser.getNickName());
                            notifyBuilder.setContentText(chatMsg.getContent());
                            if (!Constant.sound) {
                                Uri url = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                notifyBuilder.setSound(url);
                                //notifyBuilder.build().defaults = Notification.DEFAULT_SOUND;
                           }
                            if (!Constant.vibrate) {
                               notifyBuilder.setVibrate(new long[]{300,500});
                            }
                            int deleteCode = (int) SystemClock.uptimeMillis();

                            PendingIntent deletePendingIntent = PendingIntent.getService(context, deleteCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notifyBuilder.setDeleteIntent(deletePendingIntent);

                            Intent notifyIntent = new Intent(context, ChatActivity.class);
                            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            notifyIntent.putExtras(bundle);
                            // Creates the PendingIntent
                            // 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，你需要给notification设置一个独一无二的requestCode
                            int requestCode = (int) SystemClock.uptimeMillis();

                            PendingIntent pendIntent = PendingIntent.getActivity(context, requestCode, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            notifyBuilder.setContentIntent(pendIntent);
                            // 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失
                            notifyBuilder.setAutoCancel(true);
                            // 将Ongoing设为true 那么notification将不能滑动删除
                            notifyBuilder.setOngoing(false);
                            // 从Android4.1开始，可以通过以下方法，设置notification的优先级，优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
                            notifyBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                            // notifyBuilder.setPriority(NotificationCompat.PRIORITY_MIN);
                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(NOTIFY_ID, notifyBuilder.build());
                        }
                    }
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
