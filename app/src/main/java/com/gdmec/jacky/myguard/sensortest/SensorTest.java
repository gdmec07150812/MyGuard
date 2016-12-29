package com.gdmec.jacky.myguard.sensortest;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdmec.jacky.myguard.R;

public class SensorTest extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mOrientation;
    private Sensor mLight;
    private TextView tAccelerometer;
    private TextView tOrientation;
    private TextView tLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensortest);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.bright_red));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("传感器测试");
        mLeftImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLeftImgv.setImageResource(R.drawable.back);
        tAccelerometer = (TextView) findViewById(R.id.acceleromerometer);
        tOrientation = (TextView) findViewById(R.id.orientation);
        tLight = (TextView) findViewById(R.id.light);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_NORMAL);

        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[SensorManager.DATA_X];
        float y = event.values[SensorManager.DATA_Y];
        float z = event.values[SensorManager.DATA_Z];
        if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            tOrientation.setText("方位:" + x + "," + y + "," + z);
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            tAccelerometer.setText("加速度" + x + "," + y + "," + z);
        } else {
            tLight.setText("光线" + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}