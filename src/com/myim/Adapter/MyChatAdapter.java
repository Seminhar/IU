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
import com.myim.Util.ImageLoader;
import com.myim.Views.ChatActivity;
import com.myim.model.ContactPeer;
import org.jivesoftware.smack.Chat;


import java.io.File;
import java.util.*;

/**
 * Created by Administrator on 2015/4/4 0004.
 */
public class MyChatAdapter extends BaseAdapter implements AbsListView.OnScrollListener {


    Context context = null;
    private ArrayList<ChatMessage> chatList = null;
    private int start, end, visibleItemC;
    private ImageLoader imageLoader;
    public static ArrayList<ChatMessage> allContents;
    private Boolean isFirst;
    private Integer[] otherBubbles, meBubbles;
    ContactPeer cp ;
    public MyChatAdapter(Context context, ArrayList<ChatMessage> chatList, ListView listView) {
        super();
        this.context = context;
        this.chatList = chatList;
        isFirst = true;
        allContents = chatList;
        listView.setOnScrollListener(this);
        imageLoader = new ImageLoader(context, listView);
        cp = ContactPeer.getInstance(context);
        otherBubbles = new Integer[3];
        meBubbles = new Integer[3];
        otherBubbles[0] = new Integer(R.layout.chat_listiterm_other);
        otherBubbles[1] = new Integer(R.layout.chat_listterm_image_other);
        otherBubbles[2] = new Integer(R.layout.chat_voice_other);
        meBubbles[0] = new Integer(R.layout.chat_listiterm_me);
        meBubbles[1] = new Integer(R.layout.chat_listterm_image_me);
        meBubbles[2] = new Integer(R.layout.chat_voice_me);


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

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (scrollState == SCROLL_STATE_IDLE) {
            //加载
            imageLoader.showImageWithRange(start, end, Constant.THUMBNAIL_DIR);
        } else {
            //停止加载
            imageLoader.stopAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        start = firstVisibleItem;
        end = firstVisibleItem + visibleItemCount;
        visibleItemC = visibleItemCount;
        if (isFirst && visibleItemCount > 0) {
            isFirst = false;
            imageLoader.showImageWithRange(start, end, Constant.THUMBNAIL_DIR);
        }
    }

    public void refreshImageMsg() {
        if (visibleItemC > 0) {

            imageLoader.showImageWithRange(start, end, Constant.THUMBNAIL_DIR);
        }
    }

    class ViewHolder {
        public ImageView imageView = null;
        public TextView textView = null;
        public ImageView imgbtn = null;

    }

    ViewHolder holder = null;

    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessage msg = chatList.get(position);
        int msgType = msg.getType();
        String msgMime = msg.getMime();
        if (convertView == null) {
            holder = new ViewHolder();

            // 初始化 convertView
            if (msgType == ChatMessage.RECEIVED_MSG) {
                if (msgMime.equals(ChatMessage.TEXT_MIME)) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_other, null);
                } else if (msgMime.equals(ChatMessage.IMG_MIME)) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_other, null);
                    holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                } else if (msgMime.equals(ChatMessage.VOICE_MIME)) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_other, null);
                    holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                }

            } else if (msgType == ChatMessage.SENT_MSG) {

                if (msgMime.equals(ChatMessage.TEXT_MIME)) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_me, null);

                } else if (msgMime.equals(ChatMessage.IMG_MIME)) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_me, null);
                    holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                } else if (msgMime.equals(ChatMessage.VOICE_MIME)) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_me, null);
                    holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                }
            }
            holder.imageView = (ImageView) convertView.findViewById(R.id.chatList_image);
            holder.textView = (TextView) convertView.findViewById(R.id.chatList_content);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (!msgMime.equals(ChatMessage.TEXT_MIME) && holder.imgbtn == null) {
                holder = new ViewHolder();
                if (msgType == ChatMessage.RECEIVED_MSG) {
                    if (msgMime.equals(ChatMessage.IMG_MIME)) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_other, null);
                        holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                    } else if (msgMime.equals(ChatMessage.VOICE_MIME)) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_other, null);
                        holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                    }

                } else {

                    if (msgMime.equals(ChatMessage.IMG_MIME)) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_me, null);
                        holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                    } else if (msgMime.equals(ChatMessage.VOICE_MIME)) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_me, null);
                        holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                    }
                }
                holder.imageView = (ImageView) convertView.findViewById(R.id.chatList_image);
                // holder.textView = (TextView) convertView.findViewById(R.id.chatList_content);
                convertView.setTag(holder);
            } else if (msgMime.equals(ChatMessage.TEXT_MIME) && holder.textView == null) {
                holder = new ViewHolder();
                if (msg.getType() == ChatMessage.RECEIVED_MSG) {

                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_other, null);

                } else {

                    convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_me, null);
                }
                holder.imageView = (ImageView) convertView.findViewById(R.id.chatList_image);
                holder.textView = (TextView) convertView.findViewById(R.id.chatList_content);
                convertView.setTag(holder);
            }

            else {
                if (msgType == ChatMessage.RECEIVED_MSG) {
                    boolean flag = false;
                    int id = convertView.getId();
                    for (int i = 0; i < otherBubbles.length; i++) {
                        if (otherBubbles[i] == id) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        if (msgMime.equals(ChatMessage.TEXT_MIME)) {
                            convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_other, null);
                            holder.textView = (TextView) convertView.findViewById(R.id.chatList_content);
                        } else if (msgMime.equals(ChatMessage.IMG_MIME)) {
                            convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_other, null);
                            holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);

                        } else if (msgMime.equals(ChatMessage.VOICE_MIME)) {
                            convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_other, null);
                            holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);

                        }
                        holder.imageView = (ImageView) convertView.findViewById(R.id.chatList_image);

                    }
                }
                else if (msgType == ChatMessage.SENT_MSG) {
                    boolean flag = false;
                    int id = convertView.getId();
                    for (int i = 0; i < meBubbles.length; i++) {
                        if (meBubbles[i] == id) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        if (msgMime.equals(ChatMessage.TEXT_MIME)) {
                            convertView = LayoutInflater.from(context).inflate(R.layout.chat_listiterm_me, null);
                            holder.textView = (TextView) convertView.findViewById(R.id.chatList_content);
                        } else if (msgMime.equals(ChatMessage.IMG_MIME)) {
                            convertView = LayoutInflater.from(context).inflate(R.layout.chat_listterm_image_me, null);
                            holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);

                        } else if (msgMime.equals(ChatMessage.VOICE_MIME)) {
                            convertView = LayoutInflater.from(context).inflate(R.layout.chat_voice_me, null);
                            holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
                        }
                        holder.imageView = (ImageView) convertView.findViewById(R.id.chatList_image);

                    }
                }
            }



        }

        // holder.imageView = (ImageView) convertView.findViewById(R.id.chatList_image);
        loadProfilePic(msg.getFrom(),holder.imageView);

        if (msg.getMime().equals(ChatMessage.TEXT_MIME)) {
            // holder.textView = (TextView) convertView.findViewById(R.id.chatList_content);
            holder.textView.setText(msg.getContent());

        } else if (msg.getMime().equals(ChatMessage.IMG_MIME)) {

            String url = msg.getContent();
            //显示缩略图


            holder.imgbtn.setTag(url);
            imageLoader.showImage(holder.imgbtn, url, Constant.THUMBNAIL_DIR);

            holder.imgbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subDir = "multimsg";
                    ImageBig imageBig = new ImageBig();
                    imageBig.ImageBig(msg.getContent(), subDir, context);

                }
            });
        } else if (msg.getMime().equals(ChatMessage.VOICE_MIME)) {
            //  holder.imgbtn = (ImageView) convertView.findViewById(R.id.chatList_ibtn);
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
                            bundle.putInt("type", 2);                          // 1= IMG
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

    void loadProfilePic(String username,ImageView imgV)
    {
        cp.getProfilePic(username,imgV);

    }
}