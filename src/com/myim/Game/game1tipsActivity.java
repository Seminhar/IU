package com.myim.Game;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.IU.R;

/**
 * Created by Administrator on 2015/5/31 0031.
 */
public class game1tipsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_star);
        Button btn = (Button) findViewById(R.id.star_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getIntent().getExtras();
                Intent intent = new Intent(game1tipsActivity.this,GameNumberActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
