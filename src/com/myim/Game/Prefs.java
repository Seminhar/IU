package com.myim.Game;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import com.example.IU.R;
import com.myim.Game.util.MixedConstant;


public class Prefs extends Activity {

    private SharedPreferences mBaseSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉手机标题栏

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,//屏幕高亮
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//设置全屏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.dazzling_options);

        mBaseSettings = getSharedPreferences(MixedConstant.PREFERENCE_Dazzling_BASE_INFO, 0);
        CheckBox vibrateCheckbox = (CheckBox) findViewById(R.id.options_vibrate_checkbox);
        //开启震动
        vibrateCheckbox.setChecked(mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_VIBRATE, true));
        vibrateCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBaseSettings.edit().putBoolean(MixedConstant.PREFERENCE_KEY_VIBRATE, true).apply();
                } else {
                    mBaseSettings.edit().putBoolean(MixedConstant.PREFERENCE_KEY_VIBRATE, false).apply();
                }
            }
        });


        CheckBox soundsCheckbox = (CheckBox) findViewById(R.id.options_sounds_checkbox);
        //开启音效
        soundsCheckbox.setChecked(mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_SOUNDS, true));
        soundsCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBaseSettings.edit().putBoolean(MixedConstant.PREFERENCE_KEY_SOUNDS, true).apply();
                } else {
                    mBaseSettings.edit().putBoolean(MixedConstant.PREFERENCE_KEY_SOUNDS, false).apply();
                }
            }
        });

        CheckBox showTipsCheckbox = (CheckBox) findViewById(R.id.options_showtips_checkbox);
        //游戏提示
        showTipsCheckbox.setChecked(mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_SHORTIES, true));
        showTipsCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mBaseSettings.edit().putBoolean(MixedConstant.PREFERENCE_KEY_SHORTIES, true).apply();
                } else {
                    mBaseSettings.edit().putBoolean(MixedConstant.PREFERENCE_KEY_SHORTIES, false).apply();
                }
            }
        });
    }
}