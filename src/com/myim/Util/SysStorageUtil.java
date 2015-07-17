package com.myim.Util;

import android.content.Context;
import android.os.Environment;

/**
 * Created by PC on 2015-07-09.
 */
public class SysStorageUtil {

    public static String getStorageLocation(Context context)
    {
        String location;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            location = context.getExternalFilesDir(null).getAbsolutePath();
        }
        else
            location = context.getCacheDir().getAbsolutePath();

        return location;
    }
}
