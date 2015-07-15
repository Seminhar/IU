package com.myim.Game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.IU.R;
import com.myim.Views.PersonalActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2015/5/12.
 */
public class GameNumberActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    ImageView a1, a2, a3, a4, a5, a6, a7, a8, a9;
    ArrayList<Integer> imgArray;
    ArrayList<Integer> map;
    ArrayList<ImageView> imageViewsArray;
    int empty;
    ImageButton start, end;
    private Serializable tempUser;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.context = this;
        setContentView(R.layout.game_number);
        a1 = (ImageView) findViewById(R.id.a1);
        a2 = (ImageView) findViewById(R.id.a2);
        a3 = (ImageView) findViewById(R.id.a3);
        a4 = (ImageView) findViewById(R.id.a4);
        a5 = (ImageView) findViewById(R.id.a5);
        a6 = (ImageView) findViewById(R.id.a6);
        a7 = (ImageView) findViewById(R.id.a7);
        a8 = (ImageView) findViewById(R.id.a8);
        a9 = (ImageView) findViewById(R.id.a9);

        start = (ImageButton) findViewById(R.id.button_star);
        end = (ImageButton) findViewById(R.id.button_back);

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameNumberActivity.this.finish();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                randomButton();
                showBtn();
            }
        });
        randomButton();
        showBtn();
        Bundle bundle = getIntent().getExtras();
        tempUser = bundle.getSerializable("nearByUsername");

    }


    public void randomButton() {
        imageViewsArray = new ArrayList<ImageView>();
        imageViewsArray.add(a1);
        imageViewsArray.add(a2);
        imageViewsArray.add(a3);
        imageViewsArray.add(a4);
        imageViewsArray.add(a5);
        imageViewsArray.add(a6);
        imageViewsArray.add(a7);
        imageViewsArray.add(a8);
        imageViewsArray.add(a9);


        for (ImageView i : imageViewsArray)
            i.setOnClickListener(new ImgClick());
        map = new ArrayList<Integer>();

        imgArray = new ArrayList<Integer>();
        imgArray.add(R.drawable.a1);
        imgArray.add(R.drawable.a2);
        imgArray.add(R.drawable.a3);
        imgArray.add(R.drawable.a4);
        imgArray.add(R.drawable.a5);
        imgArray.add(R.drawable.a6);
        imgArray.add(R.drawable.a7);
        imgArray.add(R.drawable.a8);
        imgArray.add(R.drawable.a9);
        int sum;
        int count;
        while (true) {
            sum = 0;
            map = new ArrayList<Integer>();
            Random random = new Random();
            ArrayList<Integer> t = new ArrayList<Integer>();
            int i = 0;
            while (i < 9) {
                int r = random.nextInt(9);

                if (!t.contains(r)) {
                    t.add(r);


                    map.add(r);

                    if (r == 8) {
                        empty = i;
                    }
                    i++;
                }
            }
            count = 0;
            for (i = 1; i <= 8; i++)
                for (int j = 0; j < i; j++) {
                    if (map.get(j) > map.get(i)) {
                        count++;

                    }
                }

            sum = count + (empty / 3 + 1) + (empty % 3 + 1);


            if (sum % 2 == 0) {
                break;
            }
        }
    }

    public void showBtn() {

        for (int i = 0; i < 9; i++) {
            imageViewsArray.get(i).setImageDrawable(getResources().getDrawable(imgArray.get(map.get(i))));
        }


    }

    class ImgClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // System.out.println(v);
            int emptyID = getBtnId(empty);

            if (v.getId() == emptyID)
                return;

            //����O��Button
            if (v.getId() == getBtnId(0)) {
                if (getBtnId(1) == emptyID) {
                    empty = 0;
                    int a, b;
                    a = map.get(0);
                    b = map.get(1);
                    map.set(0, b);
                    map.set(1, a);
                    showBtn();
                } else if (getBtnId(3) == emptyID) {
                    empty = 0;
                    int a, b;
                    a = map.get(0);
                    b = map.get(3);
                    map.set(0, b);
                    map.set(3, a);
                    showBtn();
                }
            }
            //����1��Button
            if (v.getId() == getBtnId(1)) {
                if (getBtnId(0) == emptyID) {
                    empty = 1;
                    int a, b;
                    a = map.get(1);
                    b = map.get(0);
                    map.set(1, b);
                    map.set(0, a);
                    showBtn();
                } else if (getBtnId(2) == emptyID) {
                    empty = 1;
                    int a, b;
                    a = map.get(1);
                    b = map.get(2);
                    map.set(1, b);
                    map.set(2, a);
                    showBtn();
                } else if (getBtnId(4) == emptyID) {
                    empty = 1;
                    int a, b;
                    a = map.get(1);
                    b = map.get(4);
                    map.set(1, b);
                    map.set(4, a);
                    showBtn();
                }
            }
            //����2��Button
            if (v.getId() == getBtnId(2)) {
                if (getBtnId(1) == emptyID) {
                    empty = 2;
                    int a, b;
                    a = map.get(1);
                    b = map.get(2);
                    map.set(1, b);
                    map.set(2, a);
                    showBtn();
                } else if (getBtnId(5) == emptyID) {
                    empty = 2;
                    int a, b;
                    a = map.get(5);
                    b = map.get(2);
                    map.set(5, b);
                    map.set(2, a);
                    showBtn();
                }
            }
            //����3��Button
            if (v.getId() == getBtnId(3)) {
                if (getBtnId(0) == emptyID) {
                    empty = 3;
                    int a, b;
                    a = map.get(0);
                    b = map.get(3);
                    map.set(0, b);
                    map.set(3, a);
                    showBtn();
                } else if (getBtnId(4) == emptyID) {
                    empty = 3;
                    int a, b;
                    a = map.get(4);
                    b = map.get(3);
                    map.set(4, b);
                    map.set(3, a);
                    showBtn();
                } else if (getBtnId(6) == emptyID) {
                    empty = 3;
                    int a, b;
                    a = map.get(6);
                    b = map.get(3);
                    map.set(6, b);
                    map.set(3, a);
                    showBtn();
                }
            }
            //����4��Button
            if (v.getId() == getBtnId(4)) {
                if (getBtnId(1) == emptyID) {
                    empty = 4;
                    int a, b;
                    a = map.get(4);
                    b = map.get(1);
                    map.set(4, b);
                    map.set(1, a);
                    showBtn();
                } else if (getBtnId(3) == emptyID) {
                    empty = 4;
                    int a, b;
                    a = map.get(3);
                    b = map.get(4);
                    map.set(3, b);
                    map.set(4, a);
                    showBtn();
                } else if (getBtnId(5) == emptyID) {
                    empty = 4;
                    int a, b;
                    a = map.get(5);
                    b = map.get(4);
                    map.set(5, b);
                    map.set(4, a);
                    showBtn();
                } else if (getBtnId(7) == emptyID) {
                    empty = 4;
                    int a, b;
                    a = map.get(7);
                    b = map.get(4);
                    map.set(7, b);
                    map.set(4, a);
                    showBtn();
                }
            }
            //����5��Button
            if (v.getId() == getBtnId(5)) {
                if (getBtnId(2) == emptyID) {
                    empty = 5;
                    int a, b;
                    a = map.get(2);
                    b = map.get(5);
                    map.set(2, b);
                    map.set(5, a);
                    showBtn();
                } else if (getBtnId(4) == emptyID) {
                    empty = 5;
                    int a, b;
                    a = map.get(4);
                    b = map.get(5);
                    map.set(4, b);
                    map.set(5, a);
                    showBtn();
                } else if (getBtnId(8) == emptyID) {
                    empty = 5;
                    int a, b;
                    a = map.get(8);
                    b = map.get(5);
                    map.set(8, b);
                    map.set(5, a);
                    showBtn();
                }
            }
            //����6��Button
            if (v.getId() == getBtnId(6)) {
                if (getBtnId(3) == emptyID) {
                    empty = 6;
                    int a, b;
                    a = map.get(6);
                    b = map.get(3);
                    map.set(6, b);
                    map.set(3, a);
                    showBtn();
                } else if (getBtnId(7) == emptyID) {
                    empty = 6;
                    int a, b;
                    a = map.get(7);
                    b = map.get(6);
                    map.set(7, b);
                    map.set(6, a);
                    showBtn();
                }
            }
            //����7��Button
            if (v.getId() == getBtnId(7)) {
                if (getBtnId(4) == emptyID) {
                    empty = 7;
                    int a, b;
                    a = map.get(7);
                    b = map.get(4);
                    map.set(7, b);
                    map.set(4, a);
                    showBtn();
                } else if (getBtnId(6) == emptyID) {
                    empty = 7;
                    int a, b;
                    a = map.get(6);
                    b = map.get(7);
                    map.set(6, b);
                    map.set(7, a);
                    showBtn();
                } else if (getBtnId(8) == emptyID) {
                    empty = 7;
                    int a, b;
                    a = map.get(8);
                    b = map.get(7);
                    map.set(8, b);
                    map.set(7, a);
                    showBtn();
                }
            }
            //����8�Ű�ť
            if (v.getId() == getBtnId(8)) {
                if (getBtnId(5) == emptyID) {
                    empty = 8;
                    int a, b;
                    a = map.get(8);
                    b = map.get(5);
                    map.set(8, b);
                    map.set(5, a);
                    showBtn();
                } else if (getBtnId(7) == emptyID) {
                    empty = 8;
                    int a, b;
                    a = map.get(7);
                    b = map.get(8);
                    map.set(7, b);
                    map.set(8, a);
                    showBtn();
                }
            }
            if (empty == 8) {
                {
                    if (map.get(0) == 0 && map.get(1) == 1 && map.get(2) == 2 && map.get(3) == 3 &&
                            map.get(4) == 4 && map.get(5) == 5 && map.get(6) == 6 && map.get(7) == 7 &&
                            map.get(8) == 8)

                    {
                        //String self;

                        new AlertDialog.Builder(GameNumberActivity.this)
                                .setTitle("哇偶")
                                .setMessage("恭喜您解密成功！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("username", tempUser);
                                        intent.putExtras(bundle);
                                        intent.setClass(GameNumberActivity.this, PersonalActivity.class);
                                        startActivity(intent);
                                        GameNumberActivity.this.finish();
                                    }
                                })
                                .show();

                        // Toast.makeText(GameNumberActivity.this,"lasdf",Toast.LENGTH_LONG);
                    }
                }
            }

        }

        int getBtnId(int i) {
            return imageViewsArray.get(i).getId();
        }
    }
}
