package com.myim.Operation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2015/5/26.
 */
public class getImage extends Activity {
    private Context context=null;
    private Intent intent=null;
    private static final int PHOTO_REQUEST_GALLERY = 1;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 2;
    private static final int PHOTO_CLIP = 3;
    private static String photoName = null;
    private static String file_dir_Name = null;
    private static File fileDir = null;
    private static String tempFile = null;

    public getImage(Context context){
        this.context=context;
        showDialog();
    }

    /**
     * 获取文件名
     */
    private String getPhotoFileName() {
        String dataTime=null;
        GetTimeFormat timeFormat=new GetTimeFormat();
        dataTime=timeFormat.getImgNaTime();

        try {
            File file = Environment.getExternalStorageDirectory();
            if (file == null) {
                Toast.makeText(context, "请插入SD卡", Toast.LENGTH_SHORT).show();
            }
            fileDir = new File(file, "IU");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        file_dir_Name = fileDir + "/" + dataTime+ ".jpg";//
        return file_dir_Name;
    }

    /**
     * 显示对话框
     */
    private void showDialog() {
        new AlertDialog.Builder(context)
                .setTitle("图片选择")
                .setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tempFile = getPhotoFileName();
                        Log.i("pathAll", tempFile.trim());
                        Log.i("path", tempFile.substring(0, 15).trim());
                        Log.i("filename", tempFile.substring(15).trim());
                        try {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempFile.substring(0, 14), tempFile.substring(14).trim())));

                            startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("本地相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                switch (resultCode) {
                    case -1: // -1表示拍照成功
                        File photo = new File(tempFile);
                        if (photo.exists()) {
                            photoClip(Uri.fromFile(photo));
                            Log.i("dsd", Uri.fromFile(photo).toString());
                        }
                        break;
                    default:
                        break;
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    photoClip(data.getData());
                }
                break;

            case PHOTO_CLIP:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                       // headView.setImageBitmap(photo);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void photoClip(Uri uri) {
        // 调用系统中自带的图片剪裁
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CLIP);
    }
}
