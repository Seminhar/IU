package com.myim.Game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.example.IU.R;
import com.myim.Game.data.ColorData;
import com.myim.Game.data.RectArea;
import com.myim.Game.material.UIModel;
import com.myim.Game.util.MixedConstant;
import com.myim.Views.PersonalActivity;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DazzlingView extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final String HANDLE_MESSAGE_FINAL_RECORD = "1";

    private Context mContext;
    private Handler mHandler;

    private MixedThread mUIThread;

    private Drawable mTimeTotalImage;
    private Drawable mTimeExpendImage;
    private Bitmap mBgImage;

    private RectArea mPaintArea;

    private boolean mVibratorFlag;

    private boolean mSoundsFlag;

    private Vibrator mVibrator;

    private SoundPool soundPool;

    private HashMap<Integer, Integer> soundPoolMap;

    private Map<Integer, Paint> colorBgMap;

    private Map<Integer, String> colorTextMap;

    private Map<Integer, Integer> textColorMap;

    private Paint mSrcPaint;
    private Paint mTarPaint;
    private Paint mGameMsgRightPaint;
    private Paint mGameMsgLeftPaint;

    private Typeface mDataTypeface;

    public DazzlingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        mHandler = new Handler() {
            /**
             * 处理游戏结束后弹出选择面板并处理用户选择
             */
            public void handleMessage(Message m) {
                SharedPreferences rankingSettings = mContext
                        .getSharedPreferences(
                                MixedConstant.PREFERENCE_Dazzling_RANKING_INFO,
                                0);
                float curRecord = m.getData().getFloat(
                        HANDLE_MESSAGE_FINAL_RECORD);
                // 更新本地记录
                //boolean recordRefreshed = updateLocalRecord(curRecord);
                LayoutInflater factory = LayoutInflater.from(mContext);
                View dialogView = factory.inflate(R.layout.score_post_panel,
                        null);
                dialogView.setFocusableInTouchMode(true);
                dialogView.requestFocus();

                // DecimalFormat formatter = HandleUtils.getDecimalFormatter();

                final AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setView(dialogView).create();
                dialog.show();
                //解锁
                dialogView.findViewById(R.id.post_scores).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("username", ((DazzlingActivity) mContext).user);
                                Intent intent = new Intent(((DazzlingActivity)mContext), PersonalActivity.class);
                                intent.putExtras(bundle);
                                ((DazzlingActivity)mContext).startActivity(intent);
                                ((DazzlingActivity) mContext).finish();

                            }
                        });
                //返回
                dialogView.findViewById(R.id.goback).setOnClickListener(
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                ((DazzlingActivity) mContext).finish();
                            }
                        });

            }
        };
        // 初始化资源
        initRes();
        mUIThread = new MixedThread(holder, context, mHandler);
        setFocusable(true);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mPaintArea = new RectArea(0, 0, width, height);
        mUIThread.initUIModel(mPaintArea);
        mUIThread.setRunning(true);
        mUIThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        mUIThread.setRunning(false);
        while (retry) {
            try {
                mUIThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.d("", "Surface destroy failure:", e);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mUIThread.checkSelection((int) event.getX(), (int) event.getY());
        }
        return true;
    }

    public void restartGame() {
        mUIThread = new MixedThread(this.getHolder(), this.getContext(),
                mHandler);
        mUIThread.initUIModel(mPaintArea);
        mUIThread.setRunning(true);
        mUIThread.start();
    }

    public boolean updateLocalRecord(float record) {
        SharedPreferences rankingSettings = mContext.getSharedPreferences(
                MixedConstant.PREFERENCE_Dazzling_RANKING_INFO, 0);
        if (rankingSettings.getFloat(
                MixedConstant.PREFERENCE_KEY_RANKING_RECORD, 30) > record) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            rankingSettings.edit().putFloat(
                    MixedConstant.PREFERENCE_KEY_RANKING_RECORD, record)
                    .putString(MixedConstant.PREFERENCE_KEY_RANKING_DATE,
                            formatter.format(new Date())).commit();
            return true;
        }
        return false;
    }

    /**
     * 初始化资源
     */
    private void initRes() {
        mBgImage = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.numbergame_bg);
        mTimeTotalImage = mContext.getResources().getDrawable(
                R.drawable.time_total);
        mTimeExpendImage = mContext.getResources().getDrawable(
                R.drawable.time_expend);

        mDataTypeface = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/halver.ttf");

        mSrcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSrcPaint.setColor(Color.parseColor("#AAC1CDC1"));
        mTarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTarPaint.setColor(Color.parseColor("#BBC1CDC1"));

        mGameMsgRightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGameMsgRightPaint.setColor(Color.BLUE);
        mGameMsgRightPaint.setStyle(Style.FILL);
        mGameMsgRightPaint.setTextSize(17f);
        mGameMsgRightPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mGameMsgRightPaint.setTextAlign(Paint.Align.RIGHT);

        mGameMsgLeftPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGameMsgLeftPaint.setColor(Color.BLUE);
        mGameMsgLeftPaint.setStyle(Style.FILL);
        mGameMsgLeftPaint.setTextSize(17f);
        mGameMsgLeftPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mGameMsgLeftPaint.setTextAlign(Paint.Align.LEFT);

        colorBgMap = new HashMap<Integer, Paint>();
        Paint curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#000000"));
        colorBgMap.put(0, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#FFFFFF"));
        colorBgMap.put(1, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#FFFF66"));
        colorBgMap.put(2, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#FF0000"));
        colorBgMap.put(3, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#32CD32"));
        colorBgMap.put(4, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#0033FF"));
        colorBgMap.put(5, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#FFC125"));
        colorBgMap.put(6, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#9A32CD"));
        colorBgMap.put(7, curColor);
        curColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        curColor.setColor(Color.parseColor("#8A8A8A"));
        colorBgMap.put(8, curColor);

        Resources res = mContext.getResources();
        colorTextMap = new HashMap<Integer, String>();
        colorTextMap.put(0, res.getString(R.string.color_black));
        colorTextMap.put(1, res.getString(R.string.color_white));
        colorTextMap.put(2, res.getString(R.string.color_yellow));
        colorTextMap.put(3, res.getString(R.string.color_red));
        colorTextMap.put(4, res.getString(R.string.color_green));
        colorTextMap.put(5, res.getString(R.string.color_blue));
        colorTextMap.put(6, res.getString(R.string.color_orange));
        colorTextMap.put(7, res.getString(R.string.color_purple));
        colorTextMap.put(8, res.getString(R.string.color_gray));

        textColorMap = new HashMap<Integer, Integer>();
        textColorMap.put(0, Color.parseColor("#000000"));
        textColorMap.put(1, Color.parseColor("#FFFFFF"));
        textColorMap.put(2, Color.parseColor("#FFFF66"));
        textColorMap.put(3, Color.parseColor("#FF0000"));
        textColorMap.put(4, Color.parseColor("#32CD32"));
        textColorMap.put(5, Color.parseColor("#0033FF"));
        textColorMap.put(6, Color.parseColor("#FFB90F"));
        textColorMap.put(7, Color.parseColor("#9A32CD"));
        textColorMap.put(8, Color.parseColor("#8A8A8A"));

        SharedPreferences baseSettings = mContext.getSharedPreferences(
                MixedConstant.PREFERENCE_Dazzling_BASE_INFO, 0);
        mSoundsFlag = baseSettings.getBoolean(
                MixedConstant.PREFERENCE_KEY_SOUNDS, true);
        mVibratorFlag = baseSettings.getBoolean(
                MixedConstant.PREFERENCE_KEY_VIBRATE, true);
        soundPool = new SoundPool(10, AudioManager.STREAM_RING, 5);
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put(UIModel.EFFECT_FLAG_MISS, soundPool.load(getContext(),
                R.raw.miss, 1));
        soundPoolMap.put(UIModel.EFFECT_FLAG_PASS, soundPool.load(getContext(),
                R.raw.pass, 1));
        soundPoolMap.put(UIModel.EFFECT_FLAG_TIMEOUT, soundPool.load(
                getContext(), R.raw.timeout, 1));
    }

    private void showToast(int strId) {
        Toast toast = Toast.makeText(mContext, strId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }

    // thread for updating UI
    class MixedThread extends Thread {

        private final SurfaceHolder mSurfaceHolder;

        private Context mContext;

        private Handler mHandler;
        // 运行标志
        private boolean mRun = true;
        // UI模型
        private UIModel mUIModel;

        public MixedThread(SurfaceHolder surfaceHolder, Context context,
                           Handler handler) {
            mSurfaceHolder = surfaceHolder;
            mContext = context;
            mHandler = handler;

        }

        @Override
        public void run() {
            while (mRun) {
                Canvas c = null;
                try {
                    mUIModel.updateUIModel();
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        doDraw(c);
                    }
                    handleEffect(mUIModel.getEffectFlag());
                    Thread.sleep(100);
                } catch (Exception e) {
                    Log.d("", "Error at 'run' method", e);
                } finally {
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
                if (mUIModel.getStatus() == UIModel.GAME_STATUS_GAMEOVER) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putFloat(DazzlingView.HANDLE_MESSAGE_FINAL_RECORD,
                            mUIModel.getFinalRecord());
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                    mRun = false;
                }
            }
        }

        private void doDraw(Canvas canvas) {
            canvas.drawBitmap(mBgImage, 0, 0, null);

            UIModel uiModel = mUIModel;
            canvas.drawRoundRect(uiModel.getSrcPaintArea(), 15, 15, mSrcPaint);
            canvas.drawRoundRect(uiModel.getTarPaintArea(), 15, 15, mTarPaint);

            FontMetrics fmsr = mGameMsgLeftPaint.getFontMetrics();
            canvas.drawText(uiModel.getStageText(), 5, (float) 15
                    - (fmsr.ascent + fmsr.descent), mGameMsgLeftPaint);

            mTimeTotalImage.setBounds(mPaintArea.mMaxX / 2 - 80, 15,
                    mPaintArea.mMaxX / 2 + 80, 25);
            mTimeTotalImage.draw(canvas);

            mTimeExpendImage.setBounds(mPaintArea.mMaxX / 2 - 80, 15,
                    (int) (mPaintArea.mMaxX / 2 - 80 + 160 * uiModel
                            .getTimePercent()), 25);
            mTimeExpendImage.draw(canvas);

            fmsr = mGameMsgRightPaint.getFontMetrics();
            canvas.drawText(uiModel.getTimeText(), mPaintArea.mMaxX - 5,
                    (float) 15 - (fmsr.ascent + fmsr.descent),
                    mGameMsgRightPaint);

            ColorData sourceColor = uiModel.getSourceColor();
            canvas.drawRoundRect(sourceColor.getRectF(), 35, 35, colorBgMap
                    .get(sourceColor.getMBgColor()));

            int width = sourceColor.mMaxX - sourceColor.mMinX;
            int heith = sourceColor.mMaxY - sourceColor.mMinY;

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(textColorMap.get(sourceColor.getMTextColor()));
            textPaint.setTypeface(mDataTypeface);
            textPaint.setStyle(Style.FILL);
            textPaint.setTextSize(0.5f * ((width < heith) ? width : heith));
            textPaint.setTextAlign(Paint.Align.CENTER);
            FontMetrics fmsl = textPaint.getFontMetrics();
            canvas.drawText(colorTextMap.get(sourceColor.getMText()),
                    (float) (sourceColor.mMinX + sourceColor.mMaxX) / 2,
                    (float) (sourceColor.mMinY + sourceColor.mMaxY) / 2
                            - (fmsl.ascent + fmsl.descent) / 2, textPaint);

            List<ColorData> targetColors = uiModel.getTargetColor();
            for (ColorData curColor : targetColors) {
                canvas.drawRoundRect(curColor.getRectF(), 20, 20, colorBgMap
                        .get(curColor.getMBgColor()));
                width = curColor.mMaxX - curColor.mMinX;
                heith = curColor.mMaxY - curColor.mMinY;
                textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(textColorMap.get(curColor.getMTextColor()));
                textPaint.setTypeface(mDataTypeface);
                textPaint.setStyle(Style.FILL);
                textPaint.setTextSize(0.3f * ((width < heith) ? width : heith));
                textPaint.setTextAlign(Paint.Align.CENTER);
                fmsl = textPaint.getFontMetrics();
                canvas.drawText(colorTextMap.get(curColor.getMText()),
                        (float) (curColor.mMinX + curColor.mMaxX) / 2,
                        (float) (curColor.mMinY + curColor.mMaxY) / 2
                                - (fmsl.ascent + fmsl.descent) / 2, textPaint);
            }
        }

        public void initUIModel(RectArea paintArea) {
            if (mUIModel != null) {
                mRun = false;
            }
            mUIModel = new UIModel(paintArea);
            mBgImage = Bitmap.createScaledBitmap(mBgImage, paintArea.mMaxX,
                    paintArea.mMaxY, true);
        }

        public void checkSelection(int x, int y) {
            mUIModel.checkSelection(x, y);
        }

        private void handleEffect(int effectFlag) {
            if (effectFlag == UIModel.EFFECT_FLAG_NO_EFFECT)
                return;
            // 处理音效
            if (mSoundsFlag) {
                playSoundEffect(effectFlag);
            }
            // 处理震动
            if (mVibratorFlag) {
                if (effectFlag == UIModel.EFFECT_FLAG_PASS) {
                    if (mVibrator == null) {
                        mVibrator = (Vibrator) mContext
                                .getSystemService(Context.VIBRATOR_SERVICE);
                    }
                    mVibrator.vibrate(50);
                }
            }
        }

        /**
         * 播放音效
         *
         * @param soundId
         */
        private void playSoundEffect(int soundId) {
            try {
                AudioManager mgr = (AudioManager) getContext()
                        .getSystemService(Context.AUDIO_SERVICE);
                float streamVolumeCurrent = mgr
                        .getStreamVolume(AudioManager.STREAM_RING);
                float streamVolumeMax = mgr
                        .getStreamMaxVolume(AudioManager.STREAM_RING);
                float volume = streamVolumeCurrent / streamVolumeMax;
                soundPool.play(soundPoolMap.get(soundId), volume, volume, 1, 0,
                        1f);
            } catch (Exception e) {
                Log.d("PlaySounds", e.toString());
            }
        }

        public void setRunning(boolean run) {
            mRun = run;
        }
    }// Thread

}
