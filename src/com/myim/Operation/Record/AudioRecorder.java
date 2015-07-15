package com.myim.Operation.Record;

import android.content.Context;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.widget.Toast;

public class AudioRecorder {
    private static int SAMPLE_RATE_IN_HZ = 8000; // 采样率

    private MediaRecorder mMediaRecorder;
    private String mPath;
    private Context context;
    public AudioRecorder(String RECORD_FILENAME,Context context) {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        this.context=context;
        this.mPath = sanitizePath(RECORD_FILENAME);

    }

    private String sanitizePath(String RECORD_FILENAME) {

        String path;
        String mdir="RECORD";
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = context.getExternalFilesDir(null).getAbsolutePath();
            // path=Environment.getExternalStorageDirectory().getAbsolutePath();
            System.out.println(path);
        }
        else {
            path = context.getCacheDir().getAbsolutePath();
        }
        //String SDCard = Environment.getExternalStorageDirectory() + "";
        //   String SDCard = context.getFilesDir().getAbsolutePath();
        String pathName = path + "/" + mdir + "/" + RECORD_FILENAME;     //文件存储路径

        return pathName;
    }

    public void start() throws IOException {
        String state = android.os.Environment.getExternalStorageState();
        if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
            throw new IOException("SD Card is not mounted,It is  " + state
                    + ".");
        }
        File directory = new File(mPath).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("无法创建文件路径");
        }
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
        mMediaRecorder.setOutputFile(mPath);
        mMediaRecorder.prepare();
        mMediaRecorder.setOnInfoListener(null);
        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.start();
    }

    public void stop() throws IOException {

        mMediaRecorder.stop();
        mMediaRecorder.release();
    }

    public double getAmplitude() {
        if (mMediaRecorder != null) {
            return (mMediaRecorder.getMaxAmplitude());
        } else
            return 0;
    }
}