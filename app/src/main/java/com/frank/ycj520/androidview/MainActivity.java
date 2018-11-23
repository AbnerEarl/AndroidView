package com.frank.ycj520.androidview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.frank.ycj520.customview.RenderView;
import com.frank.ycj520.customview.WaveView;
import com.frank.ycj520.customview.WaveViewForDouble;

public class MainActivity extends AppCompatActivity {

    private WaveView waveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(new WaveViewForDouble(this));
        setContentView(new RenderView( this));

        /*setContentView(R.layout.activity_main);
        waveView=(WaveView)findViewById(R.id.waveView);
        waveView.startAnimation();*/
    }
}
