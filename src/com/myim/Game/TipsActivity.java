package com.myim.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import com.example.IU.R;


public class TipsActivity extends Activity implements OnClickListener {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉手机标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,//屏幕高亮
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//设置全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dazzling_tips);
		findViewById(R.id.touch_to_start).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.touch_to_start) {
			Intent i = new Intent(this, DazzlingActivity.class);
			Bundle bundle = getIntent().getExtras();
			i.putExtras(bundle);
			startActivity(i);
			this.finish();
		}
	}
}
