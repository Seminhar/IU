package com.myim.Beans;


import android.util.Log;
import com.myim.NetService.JabberConnection;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.Serializable;
import java.util.Arrays;


public class User implements Serializable {
    private String username="";
    private String realName="";
    private String nickName="";
    private String email="";
    private String sex="";
    private String address="";
    private String dob="";
    private String phone="";
    private byte[] profilePic;

    public User(String username)
    {
        this.username = getUsernameWithNoAt(username);

    }
    public void loadVCard()
    {
        JabberConnection jc  = JabberConnection.getInstance() ;
        VCard vCard = new VCard();
        try {
            vCard.load(jc.getConnection(),username+"@pc-pc");
            this.realName = vCard.getField("realName");
            this.nickName = vCard.getField("nickName");
            this.email = vCard.getField("email");
            this.sex = vCard.getField("sex");
            this.address = vCard.getField("address");
            this.dob = vCard.getField("dob");
            this.phone = vCard.getField("phone");

            this.profilePic = null;

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        Log.i("vcard:", "load success");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    public static String getUsernameWithNoAt(String username)
    {
        if(username == null)
            return "";
        if (!username.contains("@")) {
            return username;
        }
        return username.split("@")[0];
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", realName='" + realName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", email='" + email + '\'' +
                ", sex='" + sex + '\'' +
                ", address='" + address + '\'' +
                ", profilePic=" + Arrays.toString(profilePic) +
                '}';
    }
}
