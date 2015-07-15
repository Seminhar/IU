package com.myim.Game;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.example.IU.R;
import com.myim.Beans.User;

/**
 *
 */

public class DazzlingActivity extends Activity {
	public User user= null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		user = (User) bundle.getSerializable("nearByUsername");

		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉手机标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,//屏幕高亮
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,//设置全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dazzling_mixed_color);//显示自定义的view
	}
}
