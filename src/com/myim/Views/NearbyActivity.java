package com.myim.Views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class NearbyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent=new Intent();
		intent.setClass(NearbyActivity.this,RockActivity.class);
		startActivity(intent);
	}
	
}
