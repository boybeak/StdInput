package com.example.boybe.stdinput;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.widget.Toast;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.GLAudioVisualizationView;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private GLAudioVisualizationView mAvView;
    //private AudioVisualization mAv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAvView = (GLAudioVisualizationView)findViewById(R.id.visualizer_view);

        Toast.makeText(this, "" + Byte.MAX_VALUE, Toast.LENGTH_SHORT).show();

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        } else {
            go();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            go();
        }
    }

    private void go () {
        VisualizerDbmHandler vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(this, 0);

        mAvView.linkTo(vizualizerHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAvView.onResume();
    }

    @Override
    protected void onPause() {
        mAvView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mAvView.release();
        super.onDestroy();
    }
}
