package com.myim.Views;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AddressActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView=new TextView(this);
		textView.setText("这是通讯录！");
		setContentView(textView);
	}
}
