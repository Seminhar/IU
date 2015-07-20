package com.myim.Operation;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import com.example.IU.R;
import com.myim.Beans.ChatMessage;
import com.myim.Beans.User;
import com.myim.NetService.Constant;
import com.myim.Views.ChatActivity;

/**
 * Created by Administrator on 2015/7/20.
 */
public class notificationRemind {

    public void notificationMSg(Context context,User newUser,ChatMessage chatMsg){
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
            notifyBuilder.setVibrate(new long[]{300, 500});
        }
        int deleteCode = (int) SystemClock.uptimeMillis();

        PendingIntent deletePendingIntent = PendingIntent.getService(context, deleteCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notifyBuilder.setDeleteIntent(deletePendingIntent);

        Intent notifyIntent = new Intent(context, ChatActivity.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, notifyBuilder.build());
    }
}
