package com.myim.CustomTool;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.IU.R;
import com.myim.Views.ChatActivity;

/**
 * Created by Administrator on 2015/5/18.
 */
public class setDialogImage {
    private double voiceValue = 0.0; //录音的音量值
    private ImageView mIvRecVolume;
    private Dialog mRecordDialog;
    private TextView mTvRecordDialogTxt;
    private Context context=null;

    public setDialogImage(Context context){
        super();
        this.context=context;
    }


    public  void  dialogImage(){
        // 录音Dialog图片随声音大小切换
            if (voiceValue < 600.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_01);
            } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_02);
            } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_03);
            } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_04);
            } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_05);
            } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_06);
            } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_07);
            } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_08);
            } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_09);
            } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_10);
            } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_11);
            } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_12);
            } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_13);
            } else if (voiceValue > 12000.0) {
                mIvRecVolume.setImageResource(R.drawable.record_animate_14);
            }
        }

    // 录音时显示Dialog
   public void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(context, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
            case 1:
                mIvRecVolume.setImageResource(R.drawable.record_cancel);
                mTvRecordDialogTxt.setText("松开手指可取消录音");
                break;

            default:
                mIvRecVolume.setImageResource(R.drawable.record_animate_01);
                mTvRecordDialogTxt.setText("向下滑动可取消录音");
                break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }

}
