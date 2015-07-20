package com.myim.Util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by PC on 2015-05-08.
 */
public class BitmapUtil {
    public static Bitmap getBitmapFromUrl(String urls )
    {
        Bitmap bitmap  = null ;
        InputStream is ;
        try {
            URL url = new URL(urls);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection)url.openConnection();
            is = new BufferedInputStream(connection.getInputStream());
            bitmap = BitmapFactory.decodeStream(is);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return  bitmap;
    }
    public static Bitmap getBitmapFromLocal(String path) {
        Bitmap bitmap = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(path);
            } else {
                Log.i("BitmapUtil", "Bitmap file not found");
            }
        } catch (Exception e) {

        }
        return bitmap;
    }

    public static Bitmap getBitmapFromLocal(Resources res, int drawable) {

        return BitmapFactory.decodeResource(res, drawable);
    }

    public static byte[] getBitmapByteFromLocal(String path) {

        Bitmap bitmap = getBitmapFromLocal(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();

    }

    public static byte[] getBitmapByteFromBitmap(Bitmap bitmap) {

        //   Bitmap bitmap = getBitmapFromLocal(path);
        if (bitmap == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();

    }

    public static byte[] getBitmapByteFromLocal(Resources res, int drawable) {

        Bitmap bitmap = getBitmapFromLocal(res, drawable);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap getBitmapFromBytes(byte[] b) {
        if (b != null && b.length != 0) {

            return BitmapFactory.decodeByteArray(b, 0, b.length);

        } else {
            return null;
        }
    }

    public static Drawable bitmapToDrawable(Resources res, Bitmap bitmap) {

        BitmapDrawable bd = new BitmapDrawable(res, bitmap);
        return bd;
    }

    public static void saveBitmapToLocal(String path, Bitmap bitmap) {
        Log.e("saveBitmapToLocal", "保存图片");
        File f = new File(path);

        if (f.exists()) {
            f.delete();
        }

        try {
            File parent = f.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out.close();
            Log.i("saveBitmapToLocal", "已经保存");
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

}
