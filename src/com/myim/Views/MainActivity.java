package com.myim.Views;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.TabHost;
import com.example.IU.R;

public class MainActivity extends TabActivity implements OnCheckedChangeListener {
    /**
     * Called when the activity is first created.
     */
    private RadioGroup mainTab;
    private static TabHost tabhost;
    private Intent iNear;
    private Intent iMsg;
    private Intent iInfo;
    private Intent iSearch;
    private Intent iMore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tab_button_bottom);
        mainTab = (RadioGroup) findViewById(R.id.main_tab);
        mainTab.setOnCheckedChangeListener(this);
        tabhost = getTabHost();


        iMsg = new Intent(this, MsgListAllActivity.class);

        tabhost.addTab(tabhost.newTabSpec("iMsg")
                .setIndicator(getResources().getString(R.string.main_my_msg), getResources().getDrawable(R.drawable.icon_msg_normal))
                .setContent(iMsg));

        iInfo = new Intent(this, ContactActivity.class);
        tabhost.addTab(tabhost.newTabSpec("iContact")
                .setIndicator(getResources().getString(R.string.main_address), getResources().getDrawable(R.drawable.icon_contacts_normal))
                .setContent(iInfo));

        iNear = new Intent(this, RockActivity.class);
        tabhost.addTab(tabhost.newTabSpec("iNear")
                .setIndicator(getResources().getString(R.string.main_near), getResources().getDrawable(R.drawable.icon_near_normal))
                .setContent(iNear));

        iSearch = new Intent(this, SettingActivity.class);
        tabhost.addTab(tabhost.newTabSpec("iSetting")
                .setIndicator(getResources().getString(R.string.setting), getResources().getDrawable(R.drawable.icon_setting_normal))
                .setContent(iSearch));

        String tab = getIntent().getExtras().getString("tab");
        if (tab != null)
            this.tabhost.setCurrentTabByTag(tab);
    }

    /**
     * 点击事件响应
     *
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.radio_button0:
                this.tabhost.setCurrentTabByTag("iNear");
                break;
            case R.id.radio_button1:
                this.tabhost.setCurrentTabByTag("iMsg");
                break;
            case R.id.radio_button2:
                this.tabhost.setCurrentTabByTag("iContact");
                break;
            case R.id.radio_button3:
                this.tabhost.setCurrentTabByTag("iSetting");
                break;

        }
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
