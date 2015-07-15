package com.myim.NetService;

import android.content.SharedPreferences;
import android.util.Log;
import com.myim.Beans.User;
import com.myim.Views.StartUpActivity;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by PC on 2015-04-26.
 */
public class JabberConnection {

    private XMPPConnection xmppConnection;

    private static JabberConnection jabberConnection;
    private MessageListener ml;

    private JabberConnection() {

    }

    public static JabberConnection getInstance() {
        if (jabberConnection == null) {

            jabberConnection = new JabberConnection();
            jabberConnection.init();

        }
        return jabberConnection;


    }
    public XMPPConnection getConnection()
    {
        return  xmppConnection;
    }
    public void init() {
        if (xmppConnection == null) {
            ConnectionConfiguration config = new ConnectionConfiguration(Constant.XMPP_HOST, Constant.XMPP_PORT);
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
            xmppConnection = new XMPPTCPConnection(config);

        }
    }

    public boolean connectToXmppServer() {
        boolean b = true;
        if (xmppConnection == null) {
            Log.i("xmppConnection ", "Error xmppConnection is null");
            b= false;
        }
        //  else if (xmppConnection!=null && xmppConnection.isConnected()==false) {
        else if(xmppConnection!=null && xmppConnection.isConnected()==false) {
            try {

                xmppConnection.connect();

                Log.i("xmppConnection", "Connected to XMPP Server:" + Constant.XMPP_HOST);
                b = true;
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
        return b;
    }
    public boolean login(String user, String pass)
    {
        boolean b = false;
        if (xmppConnection == null) {
            Log.i("xmppConnection ", "Error xmppConnection is null");

        }
        else
        {
            try {
                xmppConnection.login(user, pass);
                b=true;
                Constant.USER_NAME = user.split("@")[0];
                Constant.PASS=pass;
                Constant.isLogin = true;
                SharedPreferences.Editor editor = StartUpActivity.sharedPreferences.edit();
                editor.putString("username",Constant.USER_NAME);
                editor.putString("p",Constant.PASS);
                editor.commit();

                Constant.user = new User(Constant.USER_NAME);
                Constant.user.loadVCard();

                Log.i("xmppConnection", "Login Sucess");

            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (SmackException e) {
                if(e.getClass().toString().equals("class org.jivesoftware.smack.SmackException$AlreadyLoggedInException"))
                {
                    b=true;
                    Constant.USER_NAME = user.split("@")[0];
                    Constant.PASS=pass;
                    Constant.isLogin = true;
                    SharedPreferences.Editor editor = StartUpActivity.sharedPreferences.edit();
                    editor.putString("username",Constant.USER_NAME);
                    editor.putString("p",Constant.PASS);
                    editor.commit();

                    Constant.user = new User(Constant.USER_NAME);
                    Constant.user.loadVCard();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return  b;
    }
    public int register (String userName,String pass,String nickName,String email,String sex,byte[] profilePic)
    {
        int rtn = Constant.NOT_CONNECT_TO_SERVER;
        HashMap<String,String> attr = new HashMap<String, String>();
        attr.put("username",userName);
        attr.put("password",pass);
        // attr.put("name",nickName);   // name == nickname
        attr.put("email", email);
        attr.put("android", "geolo_createUser_android");
        if(xmppConnection != null)
        {
            Registration reg = new Registration();
            reg.setType(IQ.Type.SET);
            reg.setTo(Constant.SERVICE_NAME);
            reg.setAttributes(attr);
            PacketFilter filter = new AndFilter(new PacketIDFilter(
                    reg.getPacketID()), new PacketTypeFilter(IQ.class));
            PacketCollector collector = xmppConnection .createPacketCollector(filter);
            try {
                xmppConnection.sendPacket(reg);
                IQ result = (IQ) collector.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout());
                collector.cancel();

                if (result == null) {
                    Log.e("Register", "No response from server.");
                    rtn = Constant.NO_RESPONSE;

                }
                else {

                    if (result.getType() == IQ.Type.RESULT) {
                        Log.e("Register", "Register Success");
                        try {
                            xmppConnection.login(userName+"@"+Constant.SERVICE_NAME,pass);
                            try {
                                VCard vCard = new VCard();
                                vCard.load(xmppConnection);

                                Log.e("vCard", "loaded");
                                vCard.setField("sex", sex);
                                vCard.setField("nickName", nickName);
                                vCard.setField("email", email);
                                vCard.setAvatar(profilePic);
                                vCard.save(xmppConnection);

                                Log.e("vCard", "saved");
                                xmppConnection.disconnect();
                                rtn = Constant.SUCCESS;

                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                                return Constant.NO_RESPONSE;
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                                return Constant.NOT_CONNECT_TO_SERVER;
                            }
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        } catch (SmackException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }







                    }
                    else { // if (result.getType() == IQ.Type.ERROR)

                        if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
                            Log.e("RegistActivity", "IQ.Type.ERROR: "
                                    + result.getError().toString());
                            rtn = Constant.ACCOUNT_EXISTED;

                        } else {
                            Log.e("RegistActivity", "IQ.Type.ERROR: "  + result.getError().toString());
                            rtn = Constant.ACCOUNT_EXISTED;
                        }
                    }
                }
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
                rtn = Constant.NOT_CONNECT_TO_SERVER;

            }

        }
        Log.v("regis",rtn+"");
        return rtn;
    }
    public boolean addFriend(String username)
    {
        boolean rtn = false;

        Roster roster = xmppConnection.getRoster();
        try {

            roster.createEntry(username+"@"+Constant.SERVICE_NAME,"",null);
            rtn=true;
            Log.i("AddFriend","success");
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return rtn;
    }
    public boolean unFriend(String username)
    {
        boolean rtn = false;

        Roster roster = xmppConnection.getRoster();
        try {
            RosterEntry entry = roster.getEntry(username+"@"+Constant.SERVICE_NAME);
            roster.removeEntry(entry);
            rtn=true;
            Log.i("Unfriend","success");
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        return rtn;
    }
    public  void initChatListener(final MessageListener ml)

    {
        this.ml = ml;
        ChatManager chatManager = ChatManager.getInstanceFor(xmppConnection);

        chatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean b) {
                if(!b)
                {
                    chat.addMessageListener(ml);
                }
            }
        });
        Log.i("initChatListener:","Sucess");
    }

    public void sendMessage(String to , Message msg )
    {
        Chat newChart = ChatManager.getInstanceFor(xmppConnection).createChat(to+"@"+Constant.SERVICE_NAME, null);
        try {
            newChart.sendMessage(msg);
            Log.i("","Send to:"+ newChart.getParticipant()+" "+msg);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }




    public void initpacketall( )
    {
        PacketFilter filter = new PacketFilter() {
            @Override
            public boolean accept(Packet packet) {
                Log.i("sdf",packet.toXML().toString());

                // Presence p = (Presence)packet ;



                return true;
            }
        };
        xmppConnection.addPacketListener(packetallListener,filter);
        Log.i("InitPacketall:","Success");
    }
    private  PacketListener packetallListener = new PacketListener() {
        @Override
        public void processPacket(Packet packet) throws SmackException.NotConnectedException {
            Log.i("Packet al",packet.toXML().toString() );
        }
    };

}





