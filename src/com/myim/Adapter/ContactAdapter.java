package com.myim.Adapter;

/**
 * Created by Administrator on 2015/5/6.
 */

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import com.myim.Operation.Contact_AssortPinyinList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.IU.R;
import com.myim.Views.ContactActivity;
import com.myim.Views.PersonalActivity;
import com.myim.model.ContactPeer;
import com.myim.pingyin.LanguageComparator_CN;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ContactAdapter extends BaseExpandableListAdapter {

    private ContactAdapter madpter = null;
    // 字符串
    private List<String> strList;

    private Contact_AssortPinyinList assort = new Contact_AssortPinyinList();

    private Context context;

    private LayoutInflater inflater;
    // 中文排序
    private LanguageComparator_CN cnSort = new LanguageComparator_CN();

    public ContactAdapter(Context context, List<String> strList) {

        super();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.strList = strList;
        if (strList == null) {
            strList = new ArrayList<String>();
        }

        long time = System.currentTimeMillis();
        // 排序
        sort();


    }

    private void sort() {
        // 分类
        for (String str : strList) {
            assort.getHashList().add(str);
        }
        assort.getHashList().sortKeyComparator(cnSort);

        for (int i = 0, length = assort.getHashList().size(); i < length; i++) {
            Collections.sort((assort.getHashList().getValueListIndex(i)), cnSort);
        }

        ContactActivity.nameHashList = assort.getHashList();

    }

    public Object getChild(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueIndex(groupPosition, childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    static class ViewHolder {
        TextView textView;
        ImageView ivProfilePic;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View contentView, ViewGroup parent) {
        ViewHolder holder = null;
        if (contentView == null) {
            holder=new ViewHolder();
            contentView = inflater.inflate(R.layout.contact_adapter, null);
            holder.textView= (TextView) contentView.findViewById(R.id.name);
            holder.ivProfilePic= (ImageView) contentView.findViewById(R.id.head);


            contentView.setTag(holder);
        } else {

            holder = (ViewHolder) contentView.getTag();
        }

        String[] splitUsername = assort.getHashList().getValueIndex(groupPosition, childPosition).split("@");
        holder.textView.setText(splitUsername[0]);
        String username = splitUsername[1];


        Bitmap profilePic = ContactPeer.getProfilePic(username);

        if (profilePic!=null)

            holder.ivProfilePic.setImageBitmap(profilePic);



        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // assort.getHashList().remove(childPosition);
                // madpter.notifyDataSetChanged();
                //String username = adapter.getAssort().getHashList().getValueIndex(groupPosition, childPosition).split("@")[1];
                Intent intent=new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("username", ContactPeer.contactList.get(username));
                intent.putExtras(bundle);
                intent.setClass(context, PersonalActivity.class);
                context.startActivity(intent);
            }
        });


  /*      contentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });*/

       /* contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(OnItemClickListener!=null){
                    OnItemClickListener.OnItemClickListener(v,childPosition);
                }
            }
        });*/

        return contentView;
    }


    /**
     * 联系人条目监听
     */
/*    private OnItemClickListener  OnItemClickListener=null;
    public void setOnItemClickListener(OnItemClickListener listener){
        OnItemClickListener=listener;
    }
    public interface OnItemClickListener{
        void OnItemClickListener(View v,int position);
    }*/


    /**
     * 联系人点击监听事件
     */

    private void showDialog(int groupPosition, int childPosition) {
        new AlertDialog.Builder(context)
                .setTitle("联系人操作")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("编辑", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                }).show();
    }

    public int getChildrenCount(int groupPosition) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueListIndex(groupPosition).size();
    }

    public Object getGroup(int groupPosition) {
        // TODO Auto-generated method stub
        return assort.getHashList().getValueListIndex(groupPosition);
    }

    public int getGroupCount() {
        // TODO Auto-generated method stub
        return assort.getHashList().size();
    }

    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    /**
     * 拼音分类群组
     *
     * @param groupPosition
     * @param arg1
     * @param contentView
     * @param arg3
     * @return
     */
    public View getGroupView(int groupPosition, boolean arg1, View contentView, ViewGroup arg3) {
        ViewHolder holder;
        if (contentView == null) {
            holder=new ViewHolder();
            contentView = inflater.inflate(R.layout.contact_list_group_item, null);
            contentView.setClickable(true);
        }
        TextView textView = (TextView) contentView.findViewById(R.id.name);
        textView.setText(assort.getFirstChar(assort.getHashList().getValueIndex(groupPosition, 0)));

        return contentView;

    }

    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    public Contact_AssortPinyinList getAssort() {
        return assort;
    }

}
