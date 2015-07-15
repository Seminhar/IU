package com.myim.Game;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.example.IU.R;
import com.myim.Game.util.MixedConstant;


public class DazzlingMenuActivity extends Activity implements OnClickListener {

	private SharedPreferences mBaseSettings;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉手机标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,//屏幕高亮
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//设置全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dazzling_main);

		Button startButton = (Button) findViewById(R.id.start_game);
		startButton.setOnClickListener(this);

        Button optionButton = (Button) findViewById(R.id.options);
		optionButton.setVisibility(View.GONE);
        optionButton.setOnClickListener(this);

		Button exitButton = (Button) findViewById(R.id.exit);
		exitButton.setOnClickListener(this);

		mBaseSettings = getSharedPreferences(
				MixedConstant.PREFERENCE_Dazzling_BASE_INFO, 0);

	}

	@Override
	public void finish() {
		//this.unbindService(mConnection);
		super.finish();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
            //开始游戏
		case R.id.start_game:
			if (mBaseSettings.getBoolean(MixedConstant.PREFERENCE_KEY_SHORTIES,
					true)) {
                Intent intent=new Intent(this, TipsActivity.class);
				Bundle bundle  ;
				bundle =  getIntent().getExtras();
				intent.putExtras(bundle);
				startActivity(intent);
				this.finish();
			} else {
                Intent intent=new Intent(this, DazzlingActivity.class);
				Bundle bundle  ;
				bundle =  getIntent().getExtras();
				intent.putExtras(bundle);
                startActivity(intent);
				this.finish();
			}
			break;
		case R.id.options:
          //  Intent intent=new Intent(DazzlingMenuActivity.this,Prefs.class);
          //  startActivity(intent);

			break;
		case R.id.exit:
			DazzlingMenuActivity.this.finish();

            break;
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
		}

		public void onServiceDisconnected(ComponentName className) {
		}
	};
}