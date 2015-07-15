package com.myim.Operation.Record;

import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/5/16.
 */
public class deleteOldFile {
    // É¾³ýÀÏÎÄ¼þ
   public static  void deleteOldFile(String RECORD_FILENAME) {
        File file = new File(Environment.getExternalStorageDirectory(),
                "WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
        if (file.exists()) {
            file.delete();
        }
    }
}
