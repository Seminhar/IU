package com.myim.Views;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import com.myim.NetService.Constant;
import com.myim.NetService.JabberConnection;
import com.myim.Operation.GetTimeFormat;
import com.myim.Beans.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.example.IU.R;
import com.myim.Services.ChatService;
import com.myim.Services.SubscribeService;
import com.myim.Util.BitmapUtil;

import java.io.File;

/**
 * Created by Administrator on 2015/4/21.
 */
public class RegisterActivity extends Activity {
    private Button btn_sure, btn_cancel;
    private EditText inputUsername, inputEmail, inputPassword, inputSpassword, inputNickname;
    private ImageView headView;
    // private ProgressDialog mpDialog;
    private RadioGroup radioSex;
    private RadioButton male;
    private RadioButton female;
    private User user;
    private String sex = null;
    private static final int PHOTO_REQUEST_GALLERY = 1;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 2;
    private static final int PHOTO_CLIP = 3;
    private static String photoName = null;
    private static String file_dir_Name = null;
    private static File fileDir = null;
    private static String tempFile = null;
    private boolean isRegister = false;
    private Bitmap photo = null;
    private ProgressDialog registerProgressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register);
        registerProgressDialog = new ProgressDialog(this);

        /**
         *
         mpDialog = new ProgressDialog(this);
         mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
         mpDialog.setTitle(R.string.loading_data);
         mpDialog.setIcon(android.R.drawable.ic_dialog_info);
         mpDialog.setMessage(getString(R.string.registing));
         mpDialog.setIndeterminate(false);
         mpDialog.setCancelable(true);btn_back=(Button)findViewById(R.id.back);
         */


        btn_sure = (Button) findViewById(R.id.ok);
        btn_cancel = (Button) findViewById(R.id.cancel);

        inputUsername = (EditText) findViewById(R.id.input_userid);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_pwd);
        inputSpassword = (EditText) findViewById(R.id.input_repwd);
        inputNickname = (EditText) findViewById(R.id.input_nickname);
        headView = (ImageView) findViewById(R.id.hendView);

        radioSex = (RadioGroup) findViewById(R.id.radioSex);
        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);

        btn_sure.setOnClickListener(new OnClick());
        btn_cancel.setOnClickListener(new OnClick());
        headView.setOnClickListener(new OnClick());


        radioSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == male.getId()) {
                    sex = "male";
                    Log.i("sex", sex);
                    System.out.println(male.getId());
                }
                if (checkedId == female.getId()) {
                    sex = "female";
                    Log.i("female", sex);
                    System.out.println(female.getId());
                }
            }
        });
    }


    /**
     * 使用当前系统时间生成照片文件名字
     */
    private String getPhotoFileName() {
        String dataTime=null;
        GetTimeFormat timeFormat=new GetTimeFormat();
        dataTime=timeFormat.getImgNaTime();

        try {
            File file = Environment.getExternalStorageDirectory();
            if (file == null) {
                Toast.makeText(RegisterActivity.this, "请插入SD卡", Toast.LENGTH_SHORT).show();
            }
            fileDir = new File(file, "IU");
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        file_dir_Name = fileDir + "/" + dataTime+ ".jpg";//
        return file_dir_Name;
    }

    /**
     * 点击响应事件
     */
    private class OnClick implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.hendView:
                    showDialog();
                    break;
                case R.id.cancel:
                    RegisterActivity.this.finish();
                    break;
                case R.id.ok:
                    RegisterCheck();
            }
        }
    }

    /**
     * 点击头像选择弹出对话框
     */
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("头像设置")
                .setPositiveButton("相机", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tempFile = getPhotoFileName();
                        Log.i("pathAll", tempFile.trim());
                        Log.i("path", tempFile.substring(0, 15).trim());
                        Log.i("filename", tempFile.substring(15).trim());
                        try {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempFile.substring(0, 14), tempFile.substring(14).trim())));
                            startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("本地相册", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        Intent intent = new Intent(Intent.ACTION_PICK, null);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
                    }
                }).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                switch (resultCode) {
                    case -1: // -1表示拍照成功
                        File photo = new File(tempFile);
                        if (photo.exists()) {
                            photoClip(Uri.fromFile(photo));
                            Log.i("dsd", Uri.fromFile(photo).toString());
                        }
                        break;
                    default:
                        break;
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    photoClip(data.getData());
                }
                break;

            case PHOTO_CLIP:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        photo = extras.getParcelable("data");
                        headView.setImageBitmap(photo);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void photoClip(Uri uri) {
        // 调用系统中自带的图片剪裁
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CLIP);
    }

    /**
     * 将裁剪好的图片放到ImageView中
     *
     * @param data
     */
    Drawable drawable = null;


    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            photo = extras.getParcelable("data");
            drawable = new BitmapDrawable(photo);
            headView.setImageDrawable(drawable);
        }
    }

    /**
     * check of register
     */
    String userId;
    private void RegisterCheck() {

        userId = inputUsername.getText().toString().trim();
        final String pwd = inputPassword.getText().toString().trim();
        final String repwd = inputSpassword.getText().toString().trim();
        final String nickname = inputNickname.getText().toString().trim();
        final String email = inputEmail.getText().toString().trim();


        if (userId == "") {
            Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
            inputUsername.requestFocus();
            return;
        }
        if (email == "") {
            Toast.makeText(this, "请输入邮箱地址", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd == "") {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pwd.equals(repwd)) {
            Toast.makeText(this, "两次输入的密码不相符", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.length() < 6) {
            Toast.makeText(this, "密码长度不能少于6", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pwd.length() > 18) {
            Toast.makeText(this, "密码长度不要超过18", Toast.LENGTH_LONG).show();
            return;
        }
        if (nickname == "") {
            Toast.makeText(this, "请输入昵称", Toast.LENGTH_LONG).show();
            return;
        }
        if (sex == null) {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
            return;
        }

        registerProgressDialog.setTitle("请等待");
        registerProgressDialog.setMessage("提交注册请求...");
        registerProgressDialog.show();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                JabberConnection jc = JabberConnection.getInstance();
                jc.connectToXmppServer();
                int rtn;
                rtn = jc.register(userId, pwd, nickname, email, sex, BitmapUtil.getBitmapByteFromBitmap(photo));
                Message msg = new Message();
                msg.what=rtn;
                registerHandler.sendMessage(msg);
            }
        });
        t.start();


    }
    Handler registerHandler = new android.os.Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == Constant.NOT_CONNECT_TO_SERVER) {
                registerProgressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "注册失败，请检查网络", Toast.LENGTH_LONG).show();
            }
            else if(msg.what ==Constant.ACCOUNT_EXISTED)
            {
                registerProgressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "注册失败, 该用户名已存在", Toast.LENGTH_LONG).show();
            }
            else if (msg.what == Constant.SUCCESS) {
//                // Contact Listener Service
//                Intent subscribeService = new Intent(RegisterActivity.this,SubscribeService.class);
//                RegisterActivity.this.startService(subscribeService);
//
//                // Chat Listener
//                Intent chatService = new Intent(RegisterActivity.this, ChatService.class);
//                RegisterActivity.this.startService(chatService);

                registerProgressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
               // Constant.USER_NAME=userId;
//                Intent intent = new Intent();
//                intent.setClass(RegisterActivity.this, MainActivity.class);
//                startActivity(intent);
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                RegisterActivity.this.finish();
            }
            super.handleMessage(msg);
        }
    };

}