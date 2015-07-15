package com.myim.Beans;

import java.io.Serializable;

/**
 * Created by PC on 2015-05-10.
 */
public class NotificationMsg implements Serializable {
    private String id;
    private String title;
    private String content;
    private String from;
    private String to;
    private String time;
    private int type;
    private int status;
    // type
    public static final int SYS_MSG = 0;
    public static final int CHAT_MSG = 1;
    public static final int SUBSCRIBE_MSG= 2;
    public static final int CHAT_IMG =3;
    public static final int CHAT_VO =4;
    // status
    public static final int UNREAD = 0 ;
    public static final int READ = 1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
