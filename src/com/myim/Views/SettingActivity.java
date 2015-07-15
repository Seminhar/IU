package com.myim.Views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.IU.R;
import com.myim.CustomTool.SwitchButton;
import com.myim.CustomTool.setAudioController;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import org.jivesoftware.smack.SmackException;

public class SettingActivity extends Activity {
    private RelativeLayout personal_msg = null;
    private Button btn_logout = null;
    private SwitchButton sbNotification;
    private SwitchButton sbSound;
    private SwitchButton sbVibrate;
    private SwitchButton sbPic;
    private AudioManager audioMa;
    private Vibrator vibrator;
    private SharedPreferences mBaseSettings;
    private  int result;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.setting);

        personal_msg = (RelativeLayout) findViewById(R.id.personal_msg);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        sbNotification = (SwitchButton) findViewById(R.id.switch_notification);
        sbSound = (SwitchButton) findViewById(R.id.switch_sound);
        sbVibrate = (SwitchButton) findViewById(R.id.switch_vibrate);
        sbPic = (SwitchButton) findViewById(R.id.switch_pic);
        // 获取系统声音服务类
        audioMa = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        //获取系统震动服务类
        vibrator = (Vibrator) getBaseContext().getSystemService(Service.VIBRATOR_SERVICE);

        result=audioMa.requestAudioFocus(audioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        setSwitchListener();
        initSwitch();

        btn_logout.setOnClickListener(new OnClick());
        personal_msg.setOnClickListener(new OnClick());

    }


    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener=new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }

                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    if (mediaPlayer == null) {

                    } else if (!mediaPlayer.isPlaying()) {

                        mediaPlayer.start();

                    }
                    // Resume playback
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    if (mediaPlayer.isPlaying()) {

                        mediaPlayer.stop();
                    }
                    audioMa.abandonAudioFocus(audioFocusChangeListener);
                    // Stop playback
                } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }

                }
            }

    };

    public void initSwitch() {
        sbNotification.setChecked(Constant.notification);
        sbSound.setChecked(Constant.sound);
        sbVibrate.setChecked(Constant.vibrate);
        sbPic.setChecked(Constant.receivePic);
    }

    public void setSwitchListener() {
        setAudioController controller = new setAudioController();

        sbNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.notification = isChecked;
                SharedPreferences.Editor editor = StartUpActivity.sharedPreferences.edit();
                editor.putBoolean("notification", isChecked);
                editor.commit();

            }
        });
        sbSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.sound = isChecked;
                SharedPreferences.Editor editor = StartUpActivity.sharedPreferences.edit();
                editor.putBoolean("sound", isChecked);
                editor.commit();
//                如果获取焦点成功
                if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    controller.volumeSilent(audioMa, isChecked);
                }
            }
        });
        sbVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.vibrate = isChecked;
                SharedPreferences.Editor editor = StartUpActivity.sharedPreferences.edit();
                editor.putBoolean("vibrate", isChecked);
                editor.commit();
                controller.Vibrate(vibrator, isChecked);
            }
        });
        sbPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constant.receivePic=isChecked;
                SharedPreferences.Editor editor=StartUpActivity.sharedPreferences.edit();
                editor.putBoolean("receivePic", isChecked);
                editor.commit();

            }
        });
    }

    //退出系统
    public void exitAlert() {
        new AlertDialog.Builder(SettingActivity.this).setTitle("您确定要退出吗？")
                .setNeutralButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //stopService();
                        //eimApplication.exit();

                        try {
                            JabberConnection.getInstance().getConnection().disconnect();
                            Constant.isLogin = false;
                            Constant.USER_NAME = "";
                            Constant.PASS = "";
                            SharedPreferences.Editor editor = StartUpActivity.sharedPreferences.edit();
                            editor.putString("username", Constant.USER_NAME);
                            editor.putString("p", Constant.PASS);
                            editor.commit();
                            startActivity(new Intent(SettingActivity.this, LoginActivity.class));
                            SettingActivity.this.finish();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }


    private class OnClick implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.personal_msg:
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("username", Constant.user);
                    intent.putExtras(bundle);
                    intent.setClass(SettingActivity.this, PersonalActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_logout:
                    exitAlert();
                    break;
                default:
                    break;
            }
        }
    }

}
