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

    private SharedPreferences mBaseSettings;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * @param audioMa
     * @param isCheck
     */
    public void volumeSilent(AudioManager audioMa, boolean isCheck) {
        audioMa.setStreamMute(AudioManager.STREAM_MUSIC, isCheck);

    }

    /**
     *
     * @param vibrator
     * @param isChecked
     */
    public void Vibrate(Vibrator vibrator, boolean isChecked) {
        if (isChecked) {
            vibrator.vibrate(1000);
        }
    }

}
