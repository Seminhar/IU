package com.myim.Operation;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/5/28.
 */
public class getPhotoFilenamePath {
    /**
     * 使用当前系统时间生成照片文件名字
     */
    public static String getPhotoFileName(Context context) {
        String dataTime = null;
        String file_dir_Name = null;
        File fileDir = null;
        String tempFile = null;
        GetTimeFormat timeFormat = new GetTimeFormat();
        dataTime = timeFormat.getImgNaTime();
        try {
            String location;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                location = context.getExternalFilesDir(null).getAbsolutePath();
            } else
                location = context.getCacheDir().getAbsolutePath();

           /* File file = Environment.getExternalStorageDirectory();
            if (file == null) {
                Toast.makeText(ChatActivity.this, "请插入SD卡", Toast.LENGTH_SHORT).show();
            }*/
            fileDir = new File(location, "Image");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        file_dir_Name = fileDir + "/" + dataTime + ".jpg";//
        return file_dir_Name;
    }
}
