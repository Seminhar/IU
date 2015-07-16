package com.myim.Views;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import com.example.IU.R;


/**
 * Created by Administrator on 2015/7/16.
 */
public class VersionActivity extends Activity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.version);

    }
}
