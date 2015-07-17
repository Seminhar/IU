package com.myim.Util;


import android.content.Context;
import android.util.Log;
import com.myim.Beans.NearByUser;
import com.myim.Beans.User;
import com.myim.model.ContactPeer;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by PC on 2015/5/3.
 */
public class NearByUserUtil {

    public static ArrayList<NearByUser> getNearByUserListFromJson(JSONObject json,Context context) {
        ArrayList<NearByUser> list = new ArrayList<NearByUser>();
        for (int i = 0; i < json.length(); i++) {
            try {

                String[] attr = json.get(i + "").toString().split(",");
                ContactPeer cp = ContactPeer.getInstance(context);
                if (!cp.contactList.containsKey(attr[0])) {
                    User user = new User(attr[0]);

                    user.loadVCard();

                    list.add(new NearByUser(attr[0], Double.parseDouble(attr[1]), Double.parseDouble(attr[2]), attr[3], Double.parseDouble(attr[4]), user));
                    Log.i("vcard", "add tot nearby");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return list;
    }
}

