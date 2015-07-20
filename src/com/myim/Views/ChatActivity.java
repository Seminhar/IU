package com.myim.Views;

/**
 * Created by Administrator on 2015/4/4 0004.
 */

import android.app.*;
import android.content.*;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import com.myim.Adapter.MsgListAdapter;
import com.myim.Beans.ChatMessage;
import com.myim.Beans.NotificationMsg;
import com.myim.CustomTool.setAudioController;
import com.myim.Listener.MsgListener;
import com.myim.NetService.Constant;
import com.myim.NetService.HttpFileUpload;
import com.myim.NetService.JabberConnection;
import android.os.Bundle;
import android.widget.*;

import java.io.*;
import java.util.*;

import com.myim.Adapter.MyChatAdapter;
import com.example.IU.R;
import com.myim.Operation.GetTimeFormat;
import com.myim.Operation.Record.AudioRecorder;
import com.myim.Operation.Record.CustomImageButton;
import com.myim.Operation.Record.deleteOldFile;
import com.myim.Operation.getPhotoFilenamePath;
import com.myim.SQLiteDB.ChatHistoryTblHelper;
import com.myim.SQLiteDB.NotificationTblHelper;
import com.myim.Services.ChatService;
import com.myim.Util.BitmapUtil;
import com.myim.Util.SysStorageUtil;
import com.myim.model.ContactPeer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class ChatActivity extends Activity {

    private Chat chat = null;
    private BroadReceiver receiver;

    ArrayList<ChatMessage> chatList = null;

    /**
     * 录音部分变量
     */
    protected Button record_btn = null;
    protected LinearLayout recordLayout = null;     //录音按钮
    private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
    private static final int RECORD_OFF = 0; // 不在录音
    private static final int RECORD_ON = 1; // 正在录音
    // private static final String RECORD_FILENAME = "record0033"; // 录音文件名
    private String RECORD_FILENAME = UUID.randomUUID().toString(); // 随机生成字符串
    private int recordState = 0; // 录音状态
    private float recodeTime = 0.0f; // 录音时长
    private double voiceValue = 0.0; //录音的音量值
    private boolean playState = false; // 录音的播放状态
    private boolean moveState = false; // 手指是否移动
    private float downY;
    private Button mBtnStartRecord;
    private TextView mTvRecordTxt;
    private TextView mTvRecordPath;
    private CustomImageButton mBtnPlayRecord;
    private TextView mTvRecordDialogTxt;
    private ImageView mIvRecVolume;
    private Dialog mRecordDialog;
    private AudioRecorder mAudioRecorder;
    private MediaPlayer mMediaPlayer;
    private Thread mRecordThread;
    public Context context;

    /**
     *
     */
    public final static int OTHER = 1;
    public final static int ME = 0;
    protected ListView chatListView = null;
    protected TextView chatSendButton = null;
    protected TextView chat_msg_button = null;
    protected ImageButton chat_bottom_add = null;
    protected EditText editText = null;
    protected MyChatAdapter adapter = null;
    protected TextView chat_contact_name = null;
    JabberConnection jc = null;
    private MsgListener msgListener = new MsgListener();
    private String username = null;
    private String msgContent = null;
    private Button chat_msg_enjoyBtn = null;
    private ImageButton chat_menu = null;
    ContactPeer cp = ContactPeer.getInstance(this);
    private static final int PHOTO_REQUEST_GALLERY = 1;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 2;
    private static final int PHOTO_CLIP = 3;
    private static String photoName = null;
    private static String file_dir_Name = null;
    private static File fileDir = null;
    private static String tempFile = null;
    private static boolean cancelRecord;
    private static boolean shortTime;
    Intent intent = new Intent();
    Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置启动不弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.chat_msg);
        jc = JabberConnection.getInstance();
        this.context = ChatActivity.this;

        /**
         *发送按钮,编辑框，列表视图
         *
         */
        chatSendButton = (TextView) findViewById(R.id.chat_bottom_sendbutton);
        editText = (EditText) findViewById(R.id.chat_bottom_edittext);
        chatListView = (ListView) findViewById(R.id.chat_list);
        chat_contact_name = (TextView) findViewById(R.id.chat_contact_name);
        chat_msg_button = (TextView) findViewById(R.id.chat_msg_button);
        chat_bottom_add = (ImageButton) findViewById(R.id.chat_bottom_add);
        chat_menu = (ImageButton) findViewById(R.id.chat_bottom_menu);
        chat_msg_enjoyBtn = (Button) findViewById(R.id.chat_msg_enjoyBtn);
        /**
         *录音按钮及相关布局
         */
        record_btn = (Button) findViewById(R.id.record_btn);
        recordLayout = (LinearLayout) findViewById(R.id.record_btn_liner);

        /**
         * 监听注册
         */
        chat_bottom_add.setOnClickListener(new OnClick());
        chat_msg_button.setOnClickListener(new OnClick());
        chatSendButton.setOnClickListener(new OnClick());
        record_btn.setOnTouchListener(new OnTouch());
        chat_msg_enjoyBtn.setOnClickListener(new OnClick());
        chat_menu.setOnClickListener(new OnClick());


        bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        msgContent = bundle.getString("msgContent");
        chat_contact_name.setText(cp.contactList.get(username).getNickName());

        chat = ChatManager.getInstanceFor(jc.getConnection()).createChat(username + "@" + Constant.SERVICE_NAME, null);
        initChatList();
        adapter = new MyChatAdapter(this, chatList, chatListView);
        chatListView.setAdapter(adapter);
        initReceiver();
    }

    //三星手机拍照旋转问题
    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }


    private void initChatList() {
        chatList = new ChatHistoryTblHelper(this).getChatHistoryList(username);
    }

    private void initReceiver() {
        receiver = new BroadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CHAT_MSG);

        registerReceiver(receiver, filter);
    }

    /**
     * 信息发送
     */
    private void sendMsg() {
        String myWord = null;
        String otherWord = null;
        String time = new GetTimeFormat().getTimeFull();
        myWord = (editText.getText() + "").toString();
        if (myWord.length() == 0)
            return;
        try {
            chat.sendMessage(myWord);
            ChatMessage msg = new ChatMessage(0, myWord, Constant.USER_NAME, username, time, ChatMessage.SENT_MSG, ChatMessage.TEXT_MIME);
            chatList.add(msg);
            new ChatHistoryTblHelper(this).saveChatHistory(msg);
            editText.setText("");
            // Add to notification list
            NotificationMsg noti = new NotificationMsg();
            //String from = User.getUsernameWithNoAt(msg.getFrom());
            String nickName = cp.contactList.get(username).getNickName();
            noti.setId(username);
            noti.setType(NotificationMsg.CHAT_MSG);
            noti.setStatus(NotificationMsg.UNREAD);
            noti.setTitle(nickName);
            noti.setFrom(Constant.USER_NAME);
            noti.setContent(myWord);
            noti.setTo(username);
            noti.setTime(new GetTimeFormat().getTimeFull());
            NotificationTblHelper notificationTblHelper = new NotificationTblHelper(ChatActivity.this);
            notificationTblHelper.saveNotification(noti);
            MsgListAllActivity.needRefresh = true;
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            Toast.makeText(ChatActivity.this, "未连接服务器", Toast.LENGTH_LONG);
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
        chatListView.setSelection(chatList.size() - 1);
    }

    /**
     * 点击头像选择弹出对话框
     */
    private void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("图片分享")
                .setPositiveButton("相机", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tempFile = getPhotoFilenamePath.getPhotoFileName(ChatActivity.this);
                        try {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(tempFile.substring(0, tempFile.length() - 23), tempFile.substring(tempFile.length() - 23).trim())));
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

    /**
     * 图片分享
     */
    void enjoyImage() {
        showDialog();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO:
                switch (resultCode) {
                    case -1: // -1表示拍照成功
                        File photo = new File(tempFile);
                        if (photo.exists()) {
                            new AlertDialog.Builder(this)
                                    .setTitle("裁剪")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            photoClip(Uri.fromFile(photo));
                                        }
                                    })
                                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            File file = new File(tempFile);
                                            sendImage(file);
                                        }
                                    }).show();
                            Log.i("dsd", Uri.fromFile(photo).toString());
                        }
                        break;
                    default:
                        break;
                }
                break;
            case PHOTO_REQUEST_GALLERY:
                if (data != null) {
                    new AlertDialog.Builder(this)
                            .setTitle("裁剪")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    photoClip(data.getData());
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    Uri originalUri = data.getData();        //获得图片的uri
                                    String[] proj = {MediaStore.Images.Media.DATA};
                                    //android多媒体数据库的封装接口
                                    Cursor cursor = managedQuery(originalUri, proj, null, null, null);
                                    //获得用户选择的图片的索引值
                                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                                    //将光标移至开头
                                    cursor.moveToFirst();
                                    //根据索引值获取图片路径
                                    String path = cursor.getString(column_index);
                                    File file = new File(path);
                                    sendImage(file);
                                }
                            }).show();

                    //photoClip(data.getData());
                }
                break;

            case PHOTO_CLIP:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");
                        File file = new File(getPhotoFilenamePath.getPhotoFileName(ChatActivity.this));
                        try {
                            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            bos.flush();
                            bos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        sendImage(file);
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
        intent.putExtra("outputX", 350);
        intent.putExtra("outputY", 350);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_CLIP);
    }

    /**
     * 事件监听实现
     */
    private class OnClick implements View.OnClickListener {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.chat_msg_button:
                    ChatActivity.this.finish();
                    break;
                case R.id.chat_bottom_sendbutton:
                    sendMsg();
                    break;
                case R.id.chat_bottom_add:
                    recordLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.chat_msg_enjoyBtn:
                    enjoyImage();
                    break;
                case R.id.chat_bottom_menu:
                    openMenu();
                    break;
                default:
                    break;
            }
        }
    }

    //弹出底部隐藏的菜单
    private void openMenu() {

        startActivity(new Intent(ChatActivity.this, SelectPicPopupWindowActivity.class));

    }

    //录音  手指相关动作的响应
    private class OnTouch implements View.OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 按下按钮
                    if (recordState != RECORD_ON) {
                        downY = event.getY();
                        //deleteOldFile.deleteOldFile(RECORD_FILENAME);
                        mAudioRecorder = new AudioRecorder(RECORD_FILENAME, context);
                        recordState = RECORD_ON;
                        try {
                            mAudioRecorder.start();
                            recordTimethread();             //录音线程开始，即正式开始录音
                            showVoiceDialog(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:                // 滑动手指
                    float moveY = event.getY();

                    if (moveY - downY > 50) {               //downY表示按下时的坐标  move表示移动时的坐标
                        //两者之差代表移动的距离   往上坐标变小
                        moveState = true;                   //判断手指是否移动
                        showVoiceDialog(1);                 //1代表滑动后显示的对话框，即松开可取消录音
                        cancelRecord = true;
                    } else {
                        cancelRecord = false;
                    }

                    if (moveY - downY < 20) {
                        moveState = false;
                        showVoiceDialog(0);
                    }
                    break;


                case MotionEvent.ACTION_CANCEL:

                case MotionEvent.ACTION_UP:        // 松开手指
                    recordLayout.setVisibility(View.INVISIBLE);

                    Log.i("----------》》", "录音结束");
                    if (recordState == RECORD_ON) {
                        recordState = RECORD_OFF;
                        if (mRecordDialog.isShowing()) {
                            mRecordDialog.dismiss();
                        }
                        try {
                            mAudioRecorder.stop();
                            mRecordThread.interrupt();
                            voiceValue = 0.0;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        File filename = getAmrPath();
                        if (cancelRecord) {
                            deleteFile(filename.getName());            //取消后删除录音文件
                            Log.i("--------------->", "删除已有语音" + filename.getName());
                        }
                        if (!moveState) {
                            if (recodeTime < MIN_RECORD_TIME) {
                                showWarnToast("时间太短,录音失败");
                                shortTime = false;
                            } else {
                                shortTime = true;
                            }
                            if (filename != null && !cancelRecord && shortTime) {
                                sendVoice(filename);                         //调用发送方法
                                Log.i("--------------->", "发送语音");

                            }

                        }
                        moveState = false;
                    }
                    break;
            }
            return false;
        }
    }


    private void sendImage(File file) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = null;
                url = HttpFileUpload.uploadFile(file, "img");
                if (url.equals("false")) {
                    return;
                } else {
                    //从服务器加载thumbnail
                    String fileName = new File(url).getName();
                    url = Constant.HTTP_HOST + "thumbnail/" + fileName;
                    String path = HttpFileUpload.download(url, Constant.THUMBNAIL_DIR, context);
                    Bitmap bitmap = BitmapUtil.getBitmapFromLocal(path);

                    org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message();
                    msg.setBody(url);
                    msg.setSubject("img");
                    try {
                        chat.sendMessage(msg);
                        String time = new GetTimeFormat().getTimeFull();
                        ChatMessage chatMessage = new ChatMessage(0, url, Constant.USER_NAME, username, time, ChatMessage.SENT_MSG, ChatMessage.IMG_MIME);
                        chatList.add(chatMessage);
                        new ChatHistoryTblHelper(ChatActivity.this).saveChatHistory(chatMessage);
                        // Add to notificatioin list
                        NotificationMsg noti = new NotificationMsg();
                        String nickName = cp.contactList.get(username).getNickName();
                        noti.setId(username);
                        noti.setType(NotificationMsg.CHAT_IMG);
                        noti.setStatus(NotificationMsg.UNREAD);
                        noti.setTitle(nickName);
                        noti.setFrom(Constant.USER_NAME);
                        noti.setContent("");
                        noti.setTo(username);

                        noti.setTime(new GetTimeFormat().getTimeFull());
                        NotificationTblHelper notificationTblHelper = new NotificationTblHelper(ChatActivity.this);
                        notificationTblHelper.saveNotification(noti);
                        MsgListAllActivity.needRefresh = true;
                        refreshHandler.sendMessage(new Message());
                    } catch (SmackException.NotConnectedException e) {
                        Toast.makeText(ChatActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            }
        });
        t.start();

    }

    private void sendVoice(File file) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = null;
                url = HttpFileUpload.uploadFile(file, "voice");
                if (url.equals("false")) {
                    //Toast.makeText(ChatActivity.this, "语音发送失败，请稍候重试", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    org.jivesoftware.smack.packet.Message msg = new org.jivesoftware.smack.packet.Message();
                    msg.setBody(url);
                    msg.setSubject("vo");
                    try {
                        chat.sendMessage(msg);
                        String time = new GetTimeFormat().getTimeFull();
                        ChatMessage chatMessage = new ChatMessage(0, url, Constant.USER_NAME, username, time, ChatMessage.SENT_MSG, ChatMessage.VOICE_MIME);
                        chatList.add(chatMessage);
                        new ChatHistoryTblHelper(ChatActivity.this).saveChatHistory(chatMessage);
                        NotificationMsg noti = new NotificationMsg();
                        //String from = User.getUsernameWithNoAt(msg.getFrom());
                        String nickName = cp.contactList.get(username).getNickName();
                        noti.setId(username);
                        noti.setType(NotificationMsg.CHAT_VO);
                        noti.setStatus(NotificationMsg.UNREAD);
                        noti.setTitle(nickName);
                        noti.setFrom(Constant.USER_NAME);
                        noti.setContent("");
                        noti.setTo(username);

                        noti.setTime(new GetTimeFormat().getTimeFull());
                        NotificationTblHelper notificationTblHelper = new NotificationTblHelper(ChatActivity.this);
                        notificationTblHelper.saveNotification(noti);
                        MsgListAllActivity.needRefresh = true;
                        refreshHandler.sendMessage(new Message());
                    } catch (SmackException.NotConnectedException e) {
                        Toast.makeText(ChatActivity.this, "网络连接失败", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    /**
     *
     */
    private class BroadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("ChatActivity:", "onReceive");
            ChatMessage chatMessage = (ChatMessage) intent.getSerializableExtra("chatMsg");
            if (!chatMessage.getTo().equals(username) && !chatMessage.getFrom().equals(username))
                return;

            chatList.add(chatMessage);
            adapter.notifyDataSetChanged();
            chatListView.setSelection(chatList.size() - 1);
        }
    }

    /**
     * 录音部分
     *
     * @return
     */
    // 播放
    public void recordPaly(String filepath) {
        if (!playState) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(filepath);    //获取播放文件路径
                mMediaPlayer.prepare();
                // mBtnPlayRecord.setText("正在播放");
                playState = true;
                mMediaPlayer.start();
                // 设置播放结束时监听
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (playState) {
                            playState = false;
                        }
                    }
                });

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                playState = false;
            } else {
                playState = false;
            }
            // mBtnPlayRecord.setText("播放录音");
        }
    }


    // 录音线程
    private Runnable recordThread = new Runnable() {
        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                {
                    try {
                        Thread.sleep(150);
                        recodeTime += 0.15;
                        // 获取音量，更新dialog
                        if (!moveState) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            recordHandler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            adapter.notifyDataSetChanged();
            chatListView.setSelection(chatList.size() - 1);
        }
    };


    public Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setDialogImage();
        }
    };


    // 获取文件手机路径
    private File getAmrPath() {
        String path;
        String mdir = "RECORD";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = context.getExternalFilesDir(null).getAbsolutePath();
            System.out.println("文件手机路径" + path + "文件名" + RECORD_FILENAME);
        } else {
            path = context.getCacheDir().getAbsolutePath();
        }
        //String SDCard = Environment.getExternalStorageDirectory() + "";
        //String SDCard = context.getFilesDir().getAbsolutePath();
        String pathName = path + "/" + mdir + "/" + RECORD_FILENAME; //文件存储路径
        File file = new File(pathName);
        return file;
    }

    // 录音计时线程
    void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
        if (voiceValue < 600.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_01);
        } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_02);
        } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_03);
        } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_04);
        } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_05);
        } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_06);
        } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_07);
        } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_08);
        } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_09);
        } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_10);
        } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_11);
        } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_12);
        } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_13);
        } else if (voiceValue > 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.record_animate_14);
        }
    }

    // 录音时显示Dialog
    void showVoiceDialog(int flag) {

        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(ChatActivity.this, R.style.DialogStyle);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            mRecordDialog.setContentView(R.layout.record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
        }
        switch (flag) {
            case 1:
                mIvRecVolume.setImageResource(R.drawable.record_cancel);
                mTvRecordDialogTxt.setText("松开手指可取消录音");
                break;

            default:
                mIvRecVolume.setImageResource(R.drawable.record_animate_01);
                mTvRecordDialogTxt.setText("向下滑动可取消录音");
                break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }

    // 录音时间太短时Toast显示
    void showWarnToast(String toastText) {
        Toast toast = new Toast(ChatActivity.this);
        LinearLayout linearLayout = new LinearLayout(ChatActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 20, 20, 20);

        // 定义一个ImageView
        ImageView imageView = new ImageView(ChatActivity.this);
        imageView.setImageResource(R.drawable.voice_to_short); // 图标

        TextView mTv = new TextView(ChatActivity.this);
        mTv.setText(toastText);
        mTv.setTextSize(14);
        mTv.setTextColor(Color.WHITE);// 字体颜色

        // 将ImageView和ToastView合并到Layout中
        linearLayout.addView(imageView);
        linearLayout.addView(mTv);
        linearLayout.setGravity(Gravity.CENTER);// 内容居中
        linearLayout.setBackgroundResource(R.drawable.record_bg);// 设置自定义toast的背景

        toast.setView(linearLayout);
        toast.setGravity(Gravity.CENTER, 0, 0);// 起点位置为中间
        toast.show();
    }

}