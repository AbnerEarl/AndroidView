package com.frank.ycj520.androidview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.frank.ycj520.customview.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveView waveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        waveView=(WaveView)findViewById(R.id.waveView);
        waveView.startAnimation();
    }
}
