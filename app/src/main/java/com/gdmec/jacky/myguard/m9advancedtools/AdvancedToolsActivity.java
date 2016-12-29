package com.gdmec.jacky.myguard.m9advancedtools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.mycalculator.MyCalculator;
import com.gdmec.jacky.myguard.sensortest.SensorTest;

public class AdvancedToolsActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_advanced_tools);
        initView();
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.bright_red));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("高级工具");
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        findViewById(R.id.advanceview_applock).setOnClickListener(this);
        findViewById(R.id.advanceview_numbelongs).setOnClickListener(this);
        findViewById(R.id.advanceview_smsbackup).setOnClickListener(this);
        findViewById(R.id.advanceview_smsreducition).setOnClickListener(this);
        findViewById(R.id.advanceview_sensortest).setOnClickListener(this);
        findViewById(R.id.advanceview_mycalculator).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.advanceview_applock:
                startActivity(AppLockActivity.class);
                break;
            case R.id.advanceview_smsbackup:
                startActivity(SMSBackupActivity.class);
                break;
            case R.id.advanceview_smsreducition:
                startActivity(SMSReducitionActivity.class);
                break;
            case R.id.advanceview_sensortest:
                startActivity(SensorTest.class);
                break;
            case R.id.advanceview_numbelongs:
                startActivity(NumBelongtoActivity.class);
                break;
            case R.id.advanceview_mycalculator:
                startActivity(MyCalculator.class);
                break;
        }
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
}
