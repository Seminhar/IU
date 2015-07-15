package com.myim.Listener;

import android.util.Log;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

/**
 * Created by Administrator on 2015/4/28.
 */
public class MsgListener {
    private MessageListener messageListener;
    private  String from=null;
    private  String content=null;

    public MsgListener() {
        messageListener = new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {

                Log.i("From:", message.getFrom() + ": " + message.getBody());
                from = message.getFrom();
                content = message.getBody();
            }
        };
    }


    public String getContent() {
        return content;
    }

    public String getFrom() {

        return from;
    }



    public MessageListener getMessageListener() {

        return this.messageListener;
    }
}

