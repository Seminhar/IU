package com.myim.Beans;

import java.io.Serializable;

/**
 * Created by PC on 2015-05-16.
 */
public class ChatMessage implements Serializable {

    //type
    public static final int RECEIVED_MSG =0 ;
    public static final int SENT_MSG = 1;

    //
    public static final String TEXT_MIME ="0";
    public static final String IMG_MIME ="1";
    public static final String VOICE_MIME ="2";
    public static final String ADD_FRI_REQUEST ="3";
    private int id;
    private String content;
    private String from;
    private String to;
    private String time;
    private int type;
    private String mime;

    public ChatMessage() {

    }

    public ChatMessage(int id, String content, String from, String to, String time, int type, String mime) {
        this.id = id;
        this.content = content;
        this.from = from;
        this.to = to;
        this.time = time;
        this.type = type;
        this.mime = mime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }
}
