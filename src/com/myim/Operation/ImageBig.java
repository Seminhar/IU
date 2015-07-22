package com.myim.Operation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import com.example.IU.R;
import com.myim.NetService.Constant;
import com.myim.NetService.HttpFileUpload;

import java.io.File;


/**
 * Created by Administrator on 2015/5/25.
 */
public class ImageBig {
    Context context = null;
    String fileDir = null;
    String fileName = null;
    ImageView imgView;
    Dialog dialog;

    public void ImageBig(String fileName, String fileDir, Context context) {
        this.context = context;
        this.fileDir = fileDir;
        this.fileName = fileName;
        String f = new File(fileName).getName();
        this.fileName = Constant.HTTP_HOST+fileDir+"/"+f ;
        //imgView = new ImageView(context);
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.imagebig, null);
        imgView = (ImageView) view.findViewById(R.id.imgV);
        dialog =new Dialog(context,R.style.imageBigStyle);
        dialog.setContentView(view);

        //dialog = new AlertDialog.Builder(context).create();

        //dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//����ȫ��
        Window window = dialog.getWindow();
        WindowManager.LayoutParams  wl = window.getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.onWindowAttributesChanged(wl);
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int height = displaymetrics.heightPixels;
//        int Width = displaymetrics.widthPixels;
        getView();


    }

    private void getView() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("imagebig", fileDir);
                Log.i("imagebig", fileName);
                String filePath = HttpFileUpload.download(fileName, fileDir, context);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("filePath", filePath);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
        t.start();

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String filePath = msg.getData().getString("filePath");
            if (filePath != null) {
                File file = new File(filePath);
                //imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imgView.setImageURI(Uri.fromFile(file));
                //dialog.setView(imgView);
                dialog.show();
                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        }
    };
}
