package com.myim.Views;

/**
 * Created by Administrator on 2015/7/17 0017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.Toast;
import com.example.IU.R;

public class SelectPicPopupWindowActivity extends Activity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_menu);
        ImageButton a1 = (ImageButton) this.findViewById(R.id.button1);
        ImageButton a2 = (ImageButton) this.findViewById(R.id.button2);
        ImageButton a3 = (ImageButton) this.findViewById(R.id.button3);
        ImageButton a4 = (ImageButton) this.findViewById(R.id.button4);
        TableLayout layout = (TableLayout) findViewById(R.id.table1);

        //添加选择窗口范围监听可以优先获取触点，即不再执行onTouchEvent()函数，点击其他地方时执行onTouchEvent()函数销毁Activity
        layout.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
                        Toast.LENGTH_SHORT).show();
            }
        });
        //添加按钮监听
        a1.setOnClickListener(this);
        a2.setOnClickListener(this);
        a3.setOnClickListener(this);
        a4.setOnClickListener(this);
       /* btn_cancel.setOnClickListener(this);
        btn_pick_photo.setOnClickListener(this);
        btn_take_photo.setOnClickListener(this);*/
    }

    //实现onTouchEvent触屏函数但点击屏幕时销毁本Activity
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
            case R.id.button4:
                break;
            default:
                break;
        }
        finish();
    }

}
