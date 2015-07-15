package com.myim.Adapter;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import com.myim.Beans.NotificationMsg;
import com.myim.Beans.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.IU.R;
import com.myim.Views.ChatActivity;
import com.myim.model.ContactPeer;


public class MsgListAdapter extends BaseAdapter {
    /**
     * 上下文对象
     */
    private Context mContext = null;
    private List<NotificationMsg> data;

    /**
     * @param
     */
    public MsgListAdapter(Context ctx, List<NotificationMsg> data) {
        mContext = ctx;
        this.data = data;
    }

    static class ViewHolder {
        RelativeLayout item_left;
        RelativeLayout item_right;
        TextView tv_title;
        TextView tv_msg;
        TextView tv_time;
        ImageView iv_icon;
        TextView item_right_txt;
        int type ;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.msg_all, parent, false);
            holder = new ViewHolder();

            holder.item_right = (RelativeLayout) convertView.findViewById(R.id.item_right);
            holder.item_left = (RelativeLayout) convertView.findViewById(R.id.msg_item_left);

            holder.iv_icon = (ImageView) convertView.findViewById(R.id.msg_iv_icon);
            holder.tv_title = (TextView) convertView.findViewById(R.id.msg_tv_title);
            holder.tv_msg = (TextView) convertView.findViewById(R.id.msg_tv_msg);
            holder.tv_time = (TextView) convertView.findViewById(R.id.msg_tv_time);

            holder.item_right_txt = (TextView) convertView.findViewById(R.id.item_right_txt);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        NotificationMsg noti = data.get(position);
        if(noti.getType()==NotificationMsg.SUBSCRIBE_MSG) {
            holder.tv_title.setText(noti.getTitle());
            holder.tv_msg.setText(noti.getContent());
            holder.tv_time.setText(noti.getTime());
            holder.iv_icon.setImageResource(R.drawable.new_fri_icon);
        }
        else if(noti.getType() == NotificationMsg.CHAT_MSG)
        {
            holder.tv_title.setText(noti.getTitle());
            holder.tv_msg.setText(noti.getContent());
            holder.tv_time.setText(noti.getTime());

            Bitmap profilePic = ContactPeer.getProfilePic(noti.getId());
            if (profilePic!=null)
                holder.iv_icon.setImageBitmap(profilePic);
            else
                holder.iv_icon.setImageResource(R.drawable.profile);
        }
        else if(noti.getType() == NotificationMsg.CHAT_IMG)
        {
            holder.tv_title.setText(noti.getTitle());
            holder.tv_msg.setText("[图片]");
            holder.tv_time.setText(noti.getTime());
            Bitmap profilePic = ContactPeer.getProfilePic(noti.getId());
            if (profilePic!=null)
                holder.iv_icon.setImageBitmap(profilePic);
            else
                holder.iv_icon.setImageResource(R.drawable.profile);
        }
        else if(noti.getType() == NotificationMsg.CHAT_VO)
        {
            holder.tv_title.setText(noti.getTitle());
            holder.tv_msg.setText("[语音]");
            holder.tv_time.setText(noti.getTime());
            Bitmap profilePic = ContactPeer.getProfilePic(noti.getId());
            if (profilePic!=null)
                holder.iv_icon.setImageBitmap(profilePic);
            else
                holder.iv_icon.setImageResource(R.drawable.profile);
        }


        holder.item_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRightItemClick(v, position);
                }
            }
        });
        holder.item_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OnItemClickListener!=null){
                    OnItemClickListener.OnItemClickListener(v,position);
                }

            }
        });
        return convertView;
    }

    /**
     * 单击事件监听器
     */
    private onRightItemClickListener mListener = null;
    public void setOnRightItemClickListener(onRightItemClickListener listener) {
        mListener = listener;
    }

    public interface onRightItemClickListener {
        void onRightItemClick(View v, int position);
    }


    private OnItemClickListener  OnItemClickListener=null;
    public void setOnItemClickListener(OnItemClickListener listener){
        OnItemClickListener=listener;
    }
    public interface OnItemClickListener{
        void OnItemClickListener(View v,int position);
    }
}
