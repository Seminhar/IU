package com.myim.Adapter;


import android.content.Context;

import android.content.res.Resources;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.IU.R;
import com.myim.Beans.ChatMessage;
import com.myim.NetService.Constant;
import com.myim.NetService.HttpFileUpload;
import com.myim.Operation.ImageBig;
import com.myim.Util.BitmapUtil;
import android.os.Handler;
import com.myim.Views.ChatActivity;
import com.myim.model.ContactPeer;


import java.io.File;
import java.util.*;

/**
 * Created by Administrator on 2015/4/4 0004.
 */
public class MyChatAdapter extends BaseAdapter {

    Context context = null;
    ArrayList<ChatMessage> chatList = null;

    public MyChatAdapter(Context context, ArrayList<ChatMessage> chatList) {
        super();
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getCount() {
        return chatList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    class ViewHolder {
        public ImageView imageView = null;
        public TextView textView = null;
        public ImageView imgbtn = null;

    }

    ViewHolder holder = null;

    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessage msg = chatList.get(position);
        holder = new ViewHolder();


        if (msg.getType() == ChatMessage.RECEIVED_MSG) {
            if (msg.getMime().equals(ChatMessage.TEXT_MIME)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_other, null);
            } else if (msg.getMime().equals(ChatMessage.IMG_MIME)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_other, null);
            } else if (msg.getMime().equals(ChatMessage.VOICE_MIME)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_other, null);
            }
        } else {

            if (msg.getMime().equals(ChatMessage.TEXT_MIME)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_me, null);

            } else if (msg.getMime().equals(ChatMessage.IMG_MIME)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_me, null);
            } else if (msg.getMime().equals(ChatMessage.VOICE_MIME)) {
                convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_me, null);
            }
        }

        holder.imageView = (ImageView) convertView.findViewById(R.id.chatList_image);
        Bitmap bit   = ContactPeer.getProfilePic(msg.getFrom());
        if (bit  != null ) {

            holder.imageView.setImageBitmap(bit);
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        if (msg.getMime().equals(ChatMessage.TEXT_MIME)) {
            holder.textView = (TextView) convertView.findViewById(R.id.chatList_content);
            holder.textView.setText(msg.getContent());
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        else if (msg.getMime().equals(ChatMessage.IMG_MIME)) {
            holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
            String subFolder = "thumbnail";
            String fileName = new File(msg.getContent()).getName();
            String location = Constant.LOCATION;
            String pathName = location + "/" + subFolder + "/" + fileName;//文件存储路径

            File file = new File(pathName);
            if (file.exists()) {
                System.out.println("isexits");
                Bitmap bitmap = BitmapUtil.getBitmapFromLocal(pathName);
                if (bitmap != null && holder.imgbtn != null) {

                    holder.imgbtn.setImageBitmap(bitmap);
                }
            } else {
                if(Constant.receivePic) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // String url = HttpFileUpload.getThumbnailUrl(fileName);
                            String filePath = HttpFileUpload.download(HttpFileUpload.getThumbnailUrl(fileName), subFolder, context);
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("filePath", filePath);
                            bundle.putInt("type", 1);                       // 1= IMG
                            message.setData(bundle);
                            handler.sendMessage(message);

                        }
                    });
                    t.start();
                }
            }
            holder.imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subDir = "multimsg";
                    ImageBig imageBig = new ImageBig();
                    imageBig.ImageBig(msg.getContent(), subDir, context);

                }
            });
        }
        else if (msg.getMime().equals(ChatMessage.VOICE_MIME)) {
            holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
          /*  Resources resources = context.getResources();
            Drawable drawable = resources.getDrawable(R.drawable.chatto_voice_playing);
            Bitmap bmp= BitmapFactory.decodeResource(resources, R.drawable.chatto_voice_playing);
*/
            if (msg.getType() == ChatMessage.RECEIVED_MSG) {
                holder.imgbtn.setImageResource(R.drawable.chatfrom_voice_playing);
            } else {
                holder.imgbtn.setImageResource(R.drawable.chatto_voice_playing);
            }
            holder.imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String fileName = new File(msg.getContent()).getName();
                            String filePath = HttpFileUpload.download(msg.getContent(), "multimsg", context);
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("filePath", filePath);
                            bundle.putInt("type", 2);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        }
                    });
                    t.start();

                }
            });
        }

        convertView.setTag(holder);
        return convertView;
    }

    Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            Bundle bundle = msg.getData();

            if ((int) bundle.get("type") == 1) {   // 1 = IMG
                String filePath = (String) bundle.get("filePath");
                if (filePath != null) {

                    Bitmap bitmap = BitmapUtil.getBitmapFromLocal(filePath);
                    if (bitmap != null && holder.imgbtn != null)
                        holder.imgbtn.setImageBitmap(bitmap);
                }
            } else if ((int) bundle.get("type") == 2) {   // 2 = Voice
                String filePath = (String) bundle.get("filePath");
                if (filePath != null) {
                    ((ChatActivity) context).recordPaly(filePath);
                }
            }
            super.handleMessage(msg);
        }
    };
}