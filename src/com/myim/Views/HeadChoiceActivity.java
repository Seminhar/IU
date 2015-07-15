package com.myim.Views;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.example.IU.R;

/**
 * Created by Administrator on 2015/4/28.
 */
public class HeadChoiceActivity extends Activity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.head_choice);
    }
}
