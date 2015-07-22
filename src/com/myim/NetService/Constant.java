package com.myim.NetService;

import com.myim.Beans.User;

/**
 * Created by PC on 2015-04-26.
 */
public class Constant {
    public static final String XMPP_HOST="101.200.191.34";
    //public static final String XMPP_HOST="101.200.191.34";
    public static final int XMPP_PORT = 5222;
    public  static final String SERVICE_NAME="pc-pc";
    public static final String HTTP_HOST="http://"+XMPP_HOST+":9090/";
    public static String USER_NAME = "null";
    public static String PASS = "null";

    // Message Return
    public  static final int NOT_CONNECT_TO_SERVER = 0;
    public  static final int ACCOUNT_EXISTED = 1;
    public static final  int SUCCESS =2;
    public static final int NO_RESPONSE=3;
    public static  final int NO_NEARBY = 10;
    public static  final int LOCATE_ERR = 11;
    public static  final int LOCATE_SUC = 12;

    // RECEIVER ACTION
    public static final String ROSTER_ACTION = "roster.action";
    //   public static final String ROSTER_REQUEST = "roster.request";
    public static final String CHAT_MSG = "message.new";

    // Service RUnning
    public static  boolean sub = false;
    public static  boolean ch = false;

    public static  String LOCATION ;

    //Setting
    public static  boolean isFirstLoad;
    public static  boolean notification;
    public static  boolean sound;
    public static  boolean vibrate;
    public static boolean isLogin = false ;
    //audioFocuse
    public static boolean getFocus;


    public static User user = null ;
    public static String THUMBNAIL_DIR = "thumbnail" ;
}