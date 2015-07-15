package com.myim.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.myim.Beans.NotificationMsg;
import com.myim.Beans.User;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import com.myim.Operation.GetTimeFormat;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.SQLiteDB.NotificationTblHelper;
import com.myim.model.ContactPeer;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

/**
 * Created by PC on 2015-05-09.
 */

public class SubscribeService extends Service {
    JabberConnection jc;
    Roster roster;
    String TAG = "SubscribeService";

    @Override
    public void onCreate() {
        Log.e("SubscribeService::", "on create");
        jc = JabberConnection.getInstance();
        initSubscriptionListener();
        super.onCreate();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("SubscribeService::", "onStartCommand");
        initRosterListener();
        Constant.sub = true;
        return super.onStartCommand(intent, flags, startId);
    }

    public void initRosterListener() {
        roster = jc.getConnection().getRoster();
        roster.removeRosterListener(rosterListener);
        roster.addRosterListener(rosterListener);
    }

    public void initSubscriptionListener() {
        PacketFilter filter = new PacketFilter() {
            @Override
            public boolean accept(Packet packet) {

                if (packet instanceof Presence) {
                    Log.i(TAG, packet.toXML().toString());
                    Presence p = (Presence) packet;
                    if (p.getType().equals(Presence.Type.subscribe)) {
                        return true;
                    }

                }
                return false;
            }
        };
        jc.getConnection().addPacketListener(subPacketListener, filter);
        Log.i("InitSubscriptListener:", "Success");
    }

    private PacketListener subPacketListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) throws SmackException.NotConnectedException {


            Log.i("Packet SUbscript", packet.toXML().toString());
            Presence p = new Presence(Presence.Type.subscribed);
            p.setTo(packet.getFrom());
            jc.getConnection().sendPacket(p);
        }
    };

    private RosterListener rosterListener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> collection) {

            Log.e(TAG, "entry add");
            for (String user : collection) {
                RosterEntry entry = roster.getEntry(user);
//                User newUser = new User(entry.getUser());
//                newUser.loadVCard();
//
//
//                NotificationMsg noti = new NotificationMsg();
//              //  String id = "sys"+System.currentTimeMillis();
//                noti.setId("sys"+System.currentTimeMillis());
//                noti.setType(NotificationMsg.SUBSCRIBE_MSG);
//                noti.setStatus(NotificationMsg.UNREAD);
//                noti.setTitle("新好友");
//                noti.setContent("\""+newUser.getNickName()+"\"已经成为你好友");
//                noti.setFrom(user);
//                String time =new GetTimeFormat().getTimeFull();
//                noti.setTime(time);
//
//
//                Intent intent = new Intent();
//                // Save to database
//                NotificationTblHelper notificationTblHelper = new NotificationTblHelper(SubscribeService.this);
//                notificationTblHelper.saveNotification(noti);
//
//                intent.setAction(Constant.ROSTER_ACTION);
//                intent.putExtra("roster",noti);
//
//                ContactPeer.contactList.put(user,newUser);
//                new ContactTblHelper(SubscribeService.this).saveContact(newUser);
//                sendBroadcast(intent);
            }


        }

        @Override
        public void entriesUpdated(Collection<String> collection) {
            Log.e(TAG, "entries update");


        }

        @Override
        public void entriesDeleted(Collection<String> collection) {
            Log.e(TAG, "entries delete");
        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    };
}
