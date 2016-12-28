package com.gdmec.jacky.myguard.m2theftguard;

import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gdmec.jacky.myguard.R;

public class SetUp1Activity extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set_up1);
        initView();
    }

    private void initView() {
        TextView mTitleTV = (TextView) findViewById(R.id.tv_title);
        mTitleTV.setText("手机防盗");
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.green360));
        ((RadioButton) findViewById(R.id.rb_first)).setChecked(true);
    }

    @Override
    public void showNext() {
        startActivityAndFinishSelf(SetUp2Activity.class);
    }

    @Override
    public void showPre() {
        // Toast.makeText(this,"当前页面已经是第一页",0).show();
    }
}
