package com.myim.Views;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.example.IU.R;


/**
 * Created by Administrator on 2015/7/16.
 */
public class VersionActivity extends Activity {

    private TextView textView;
    private TextView versionBcak;
    private PackageManager packageManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.version);
        textView = (TextView) findViewById(R.id.version_text_versionNum);
        versionBcak=(TextView)findViewById(R.id.version_back_button);
        textView.setText("版本号" + getVersionName());
        versionBcak.setOnClickListener(new Onclick());
    }

    private String getVersionName() {
        packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
    public class Onclick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.version_back_button:
                    VersionActivity.this.finish();
                    break;
            }
        }
    }
}
