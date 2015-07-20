package com.myim.Util;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import com.example.IU.R;
import com.myim.Beans.ChatMessage;
import com.myim.Beans.NotificationMsg;
import com.myim.Beans.User;
import com.myim.CustomTool.processTask;
import com.myim.NetService.Constant;
import com.myim.Operation.GetTimeFormat;
import com.myim.Operation.notificationRemind;
import com.myim.SQLiteDB.ChatHistoryTblHelper;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.SQLiteDB.NotificationTblHelper;
import com.myim.Views.ChatActivity;
import com.myim.Views.ContactActivity;
import com.myim.model.ContactPeer;
import org.jivesoftware.smack.packet.Message;

import java.util.List;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

/**
 * Created by PC on 2015-07-18.
 */
public class MessageProcessorUtil {

    private Context context;
    private notificationRemind notificationRemind = new notificationRemind();
    private processTask processTask = new processTask();

    public MessageProcessorUtil(Context context) {
        this.context = context;
    }

    public void process(Message msg) {
        if (msg.getSubject() != null && msg.getSubject().equals("img")) {
            processImageMsg(msg);
        } else if (msg.getSubject() != null && msg.getSubject().equals("vo")) {
            processVoiceMsg(msg);
        } else if (msg.getSubject() != null && msg.getSubject().equals("addFri")) {
            processAddFriMsg(msg);
        } else if (msg.getSubject() != null && msg.getSubject().equals("delFri")) {
            processDelFriMsg(msg);
        } else {
            processTextMsg(msg);
        }

    }

    private void processTextMsg(Message msg) {
        User newUser = ContactPeer.getInstance(context).contactList.get(User.getUsernameWithNoAt(msg.getFrom()));
        // 创建消息通知对象
        NotificationMsg noti = new NotificationMsg();
        noti.setId(newUser.getUsername());
        noti.setType(NotificationMsg.CHAT_MSG);
        noti.setStatus(NotificationMsg.UNREAD);
        noti.setTitle(newUser.getNickName());
        noti.setFrom(newUser.getNickName());
        noti.setContent(msg.getBody());
        String time = new GetTimeFormat().getTimeFull();
        noti.setTime(time);
        // 创建聊天信息对象
        ChatMessage chatMsg = new ChatMessage(0, msg.getBody(), newUser.getUsername(), Constant.USER_NAME, time, ChatMessage.RECEIVED_MSG, ChatMessage.TEXT_MIME);
        //保存到本地DB
        saveChatHistory(chatMsg);
        saveNotificationMsg(noti);
        //发生广播
        Intent i = new Intent();
        i.putExtra("roster", noti);
        i.putExtra("chatMsg", chatMsg);
        i.setAction(Constant.CHAT_MSG);
        context.sendBroadcast(i);

        /**
         * 标题栏消息提醒
         */
        if (processTask.isBackgroundRunning(context)) {
            notificationRemind.notificationMSg(context, newUser, chatMsg);
        }
    }

    private void processImageMsg(Message msg) {
        // 创建消息通知对象
        User newUser = ContactPeer.getInstance(context).contactList.get(User.getUsernameWithNoAt(msg.getFrom()));
        NotificationMsg noti = new NotificationMsg();
        //  String from = User.getUsernameWithNoAt(msg.getFrom());
        noti.setId(newUser.getUsername());
        noti.setType(NotificationMsg.CHAT_IMG);
        noti.setStatus(NotificationMsg.UNREAD);
        noti.setTitle(newUser.getNickName());
        noti.setFrom(newUser.getNickName());
        noti.setContent(msg.getBody());
        String time = new GetTimeFormat().getTimeFull();
        noti.setTime(time);
        // 创建聊天信息对象
        ChatMessage chatMsg = new ChatMessage(0, msg.getBody(), newUser.getUsername(), Constant.USER_NAME, time, ChatMessage.RECEIVED_MSG, ChatMessage.IMG_MIME);
        saveNotificationMsg(noti);
        saveChatHistory(chatMsg);

        Intent i = new Intent();
        i.putExtra("roster", noti);
        i.putExtra("chatMsg", chatMsg);
        i.setAction(Constant.CHAT_MSG);
        context.sendBroadcast(i);

        chatMsg.setContent("[图片]");
        if (processTask.isBackgroundRunning(context)) {
            notificationRemind.notificationMSg(context, newUser, chatMsg);
        }
    }

    private void processVoiceMsg(Message msg) {
        //User from = cp.contactList.get(User.getUsernameWithNoAt(msg.getFrom()));
        // 创建消息通知对象
        User newUser = ContactPeer.getInstance(context).contactList.get(User.getUsernameWithNoAt(msg.getFrom()));
        //String from = User.getUsernameWithNoAt(msg.getFrom());
        NotificationMsg noti = new NotificationMsg();

        noti.setId(newUser.getUsername());
        noti.setType(NotificationMsg.CHAT_VO);
        noti.setStatus(NotificationMsg.UNREAD);
        noti.setTitle(newUser.getNickName());
        noti.setFrom(newUser.getNickName());
        noti.setContent(msg.getBody());
        String time = new GetTimeFormat().getTimeFull();
        noti.setTime(time);
        // 创建消息通知对象
        ChatMessage chatMsg = new ChatMessage(0, msg.getBody(), newUser.getUsername(), Constant.USER_NAME, time, ChatMessage.RECEIVED_MSG, ChatMessage.VOICE_MIME);
        // 保存到本地DB
        saveChatHistory(chatMsg);
        saveNotificationMsg(noti);
        // 发出广播
        Intent i = new Intent();
        i.putExtra("roster", noti);
        i.putExtra("chatMsg", chatMsg);
        i.setAction(Constant.CHAT_MSG);
        context.sendBroadcast(i);

        chatMsg.setContent("[语音]");
        if (processTask.isBackgroundRunning(context)) {
            notificationRemind.notificationMSg(context, newUser, chatMsg);
        }
    }

    private void processAddFriMsg(Message msg) {

        String user = User.getUsernameWithNoAt(msg.getFrom());
        User newUser = new User(user);
        newUser.loadVCard();
        NotificationMsg noti = new NotificationMsg();
        noti.setId("sys" + System.currentTimeMillis());
        noti.setType(NotificationMsg.SUBSCRIBE_MSG);
        noti.setStatus(NotificationMsg.UNREAD);
        noti.setTitle("新好友");
        noti.setContent("\"" + newUser.getNickName() + "\"已经成为你好友");
        noti.setFrom(user);
        String time = new GetTimeFormat().getTimeFull();
        noti.setTime(time);

        saveNotificationMsg(noti);

        Intent intent = new Intent();
        intent.setAction(Constant.ROSTER_ACTION);
        intent.putExtra("roster", noti);
        ContactPeer.getInstance(context).contactList.put(user, newUser);
        new ContactTblHelper(context).saveContact(newUser);
        ContactActivity.needRefresh = true;
        context.sendBroadcast(intent);
    }

    private void processDelFriMsg(Message msg) {
        String user = User.getUsernameWithNoAt(msg.getFrom());
        ContactPeer.getInstance(context).contactList.remove(user);
        new ContactTblHelper(context).removeContact(user);
        new NotificationTblHelper(context).removeNotification(user);
        new ChatHistoryTblHelper(context).removeChatHistory(user);
    }

    // 保存通知信息到本地DB
    private void saveNotificationMsg(NotificationMsg noti) {

        new NotificationTblHelper(context).saveNotification(noti);
    }

    // 保存聊天记录到本地DB
    private void saveChatHistory(ChatMessage msg) {

        new ChatHistoryTblHelper(context).saveChatHistory(msg);
    }

}
