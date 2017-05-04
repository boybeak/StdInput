package com.example.boybe.stdinput;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private WaveformView waveformView = null;
    private Random mRandom;

    private Runnable mRun = new Runnable() {
        @Override
        public void run() {
            int amp = recorder.getMaxAmplitude();
            /*Log.v(TAG, "amp=" + amp);
            if (amp > Byte.MAX_VALUE) {
                amp = Byte.MAX_VALUE;
            }
            byte b = (byte)amp;*/

            waveformView.putInt(amp);
            waveformView.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "" + Byte.MAX_VALUE, Toast.LENGTH_SHORT).show();

        mRandom = new Random();

        waveformView = (WaveformView)findViewById(R.id.wave_form);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
        }
    }

    private MediaRecorder recorder;
    private void startRecord () throws IOException {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(new File(getExternalCacheDir(), System.currentTimeMillis() + "mp3").getAbsolutePath());
        recorder.prepare();
        recorder.start();

        waveformView.postDelayed(mRun, 1000);
        ((WaveformView)waveformView).start();
    }

    private void stopRecord () {
           // Recording is now started
        recorder.stop();
        recorder.reset();   // You can reuse the object by going back to setAudioSource() step
        recorder.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (recorder != null) {
            stopRecord();
        }
    }

    public void start(View view) {
        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        } else {
            try {
                startRecord();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
