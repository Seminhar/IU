package com.myim.CustomTool;


import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;



/**
 * Created by Administrator on 2015/7/10.
 */
public class setAudioController extends Activity {
//     private AudioManager audioMa;
//     private Vibrator vibrator;
    //��������
    private int volume = 0;
    //����ģʽ
    private int mode;
    private SharedPreferences mBaseSettings;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ��ȡϵͳ����������
//        audioMa = (AudioManager)getBaseContext(). getSystemService(Context.AUDIO_SERVICE);
//        //��ȡϵͳ�𶯷�����
//        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

    }
    public void receivePic(boolean isChecked) {


    }

    /**
     * ����
     * @param audioMa
     * @param isCheck
     */
    public void volumeSilent(AudioManager audioMa, boolean isCheck) {
        audioMa.setStreamMute(AudioManager.STREAM_MUSIC, isCheck);

    }

    /**
     * ��
     * @param vibrator
     * @param isChecked
     */
    public void Vibrate(Vibrator vibrator, boolean isChecked) {
        if (isChecked) {
            vibrator.vibrate(1000);
        }
    }

}
