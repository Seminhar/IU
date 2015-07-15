package com.myim.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import com.myim.Beans.User;
import com.myim.NetService.JabberConnection;
import com.myim.SQLiteDB.ContactTblHelper;
import com.myim.Util.BitmapUtil;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by PC on 2015-05-09.
 */
public class ContactPeer {

    public static HashMap<String,User> contactList ;
    private static   HashMap<String,SoftReference<Bitmap>> profilePicCache = new HashMap<String,SoftReference<Bitmap>>();
    public JabberConnection jc;
    private static Context context ;
    private final static String TAG ="ContactPeer";
    public ContactPeer(Context context)
    {
        this.context = context;
        jc=JabberConnection.getInstance();
        contactList = new HashMap<String, User>();

    }
    public void loadDataFromDB ()
    {
        contactList = new ContactTblHelper(context).getContactMap();
    }
    public static Bitmap getProfilePic(String username)
    {
        Bitmap bitmap = null ;
        SoftReference<Bitmap> bitmapSoft = profilePicCache.get(username);
        if(bitmapSoft==null)
        {

            String pathName;
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                pathName = context.getExternalFilesDir(null).getAbsolutePath()+ "/pgim/" + username;

            }
            else
                pathName = context.getCacheDir().getAbsolutePath()+ "/pgim/" + username;


            if(new File(pathName).exists())
            {
                Log.e(TAG,"Get pic from local");
                bitmap = BitmapUtil.getBitmapFromLocal(pathName);
                profilePicCache.put(username,new SoftReference<Bitmap>(bitmap));
            }
            else {
                Log.e(TAG,"Get pic from server");
                VCard vCard = new VCard();
                try {
                    vCard.load(JabberConnection.getInstance().getConnection(), username + "@pc-pc");
                    byte[] bytes = vCard.getAvatar();
                    bitmap = BitmapUtil.getBitmapFromBytes(bytes);
                    if (bitmap != null) {
                        profilePicCache.put(username, new SoftReference<Bitmap>(bitmap));
                        BitmapUtil.saveBitmapToLocal(pathName,bitmap);
                    }
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            if(bitmapSoft.get()==null)
            {
                System.out.println("soft bitmap null");
                String pathName;
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                    pathName = context.getExternalFilesDir(null).getAbsolutePath()+ "/pgim/" + username;
                }
                else
                    pathName = context.getCacheDir().getAbsolutePath()+ "/pgim/" + username;
                if(new File(pathName).exists())
                {
                    Log.e(TAG,"Get pic from local");
                    bitmap = BitmapUtil.getBitmapFromLocal(pathName);
                    profilePicCache.put(username,new SoftReference<Bitmap>(bitmap));
                }
                else
                {
                    Log.e(TAG,"Get pic from server");
                    VCard vCard = new VCard();
                    try {
                        vCard.load(JabberConnection.getInstance().getConnection(),username+"@pc-pc");
                        byte[] bytes = vCard.getAvatar();
                        bitmap = BitmapUtil.getBitmapFromBytes(bytes);
                        if (bitmap!=null)
                        {
                            profilePicCache.put(username,new SoftReference<Bitmap>(bitmap));
                            BitmapUtil.saveBitmapToLocal(pathName,bitmap);
                        }
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

            }
            else
            {
                Log.e(TAG,"Get pic from softreference");
                bitmap = bitmapSoft.get();
            }

        }

        return bitmap;
    }
    public void relad()
    {

//        Collection<RosterEntry> entries = jc.getConnection().getRoster().getEntries() ;
//        for(RosterEntry entry : entries)
//        {
//            contactList.put(entry.getUser(),new User(entry.getUser()));
//        }
        //  new ContactTblHelper(this).loadFromServer();
    }
    public void addFriend(String username,String nickname)
    {
        Roster roster = jc.getConnection().getRoster();
        try {

            roster.createEntry(username,nickname,null);
            Log.i("AddFriend:", username);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
    public void removeFriend(String username)
    {
        Roster roster = jc.getConnection().getRoster();
        RosterEntry entry = roster.getEntry(username);
        try {
            jc.getConnection().getRoster().removeEntry(entry);
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
    public void showLIst()

    {
        for(User user:contactList.values())
        {
            // Log.e(user.getUsername(), user.getNickname());
        }
    }

}
