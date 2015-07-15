package com.myim.Operation;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.example.IU.R;
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
    AlertDialog dialog;

    public void ImageBig(String fileName, String fileDir, Context context) {
        this.context = context;
        this.fileDir = fileDir;
        this.fileName = fileName;
        imgView = new ImageView(context);
        dialog = new AlertDialog.Builder(context).create();
        getView();


    }

    private void getView() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = HttpFileUpload.download(fileName, fileDir, context);
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("filePath", filePath);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
        t.start();



/*
        InputStream is = getResources().openRawResource(R.drawable.tellcat4);
        Drawable drawable = BitmapDrawable.createFromStream(is, null);
        imgView.setImageDrawable(drawable);*/
        //imgView.setImageResource(R.drawable.tellcat4);


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String filePath = msg.getData().getString("filePath");
            if (filePath != null) {
                File file = new File(filePath);
                imgView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imgView.setImageURI(Uri.fromFile(file));
                dialog.setView(imgView);
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
