package com.myim.Operation;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/5/3.
 */
public class GetTimeFormat {
    Date date = null;
    SimpleDateFormat dateFormat = null;
    String time=null;
    public String getMsgTime() {
        date = new Date(System.currentTimeMillis());
        dateFormat = new SimpleDateFormat("HH:mm");
        time=dateFormat.format(date).toString();
        return time;
    }
    public String getTimeFull()
    {
        date = new Date(System.currentTimeMillis());
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = dateFormat.format(date).toString();
        return time;
    }
    public String getImgNaTime(){
        date = new Date(System.currentTimeMillis());
        dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        time=dateFormat.format(date).toString();
        return time;

    }

}
