package com.gdmec.jacky.myguard.m2theftguard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gdmec.jacky.myguard.R;

public class LostFindActivity extends Activity implements View.OnClickListener {

    private TextView mSafePhoneTV;
    private RelativeLayout mInterSetupRL;
    private SharedPreferences msharePreferences;
    private ToggleButton mToggleButton;
    private TextView mProtectStatusTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lost_find);

        msharePreferences = getSharedPreferences("config", MODE_PRIVATE);
        if (!isSetUp()) {
            startSetUp1Activity();
        }
        initView();
    }

    private boolean isSetUp() {
        return msharePreferences.getBoolean("isSetUp", false);
    }

    private void initView() {
        TextView mTitleTV = (TextView) findViewById(R.id.tv_title);
        mTitleTV.setText("手机防盗");
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.green360));
        mSafePhoneTV = (TextView) findViewById(R.id.tv_safephone);
        mSafePhoneTV.setText(msharePreferences.getString("safephone", ""));
        mToggleButton = (ToggleButton) findViewById(R.id.togglebtn_lostfind);
        mInterSetupRL = (RelativeLayout) findViewById(R.id.rl_inter_setup_wizard);
        mInterSetupRL.setOnClickListener(this);
        mProtectStatusTV = (TextView) findViewById(R.id.tv_lostfind_protectstatus);
        boolean protecting = msharePreferences.getBoolean("protecting", true);
        if (protecting) {
            mProtectStatusTV.setText("防盗保护已经开启");
            mToggleButton.setChecked(true);
        } else {
            mProtectStatusTV.setText("防盗保护没有开启");
            mToggleButton.setChecked(false);
        }
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mProtectStatusTV.setText("防盗保护已经开启");

                } else {
                    mProtectStatusTV.setText("防盗保护没有开启");
                }
                SharedPreferences.Editor editor = msharePreferences.edit();
                editor.putBoolean("protecting", isChecked);
                editor.commit();
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_inter_setup_wizard:
                startSetUp1Activity();
                break;
            case R.id.imgv_leftbtn:
                finish();
                break;
        }
    }

    private void startSetUp1Activity() {
        Intent intent = new Intent(LostFindActivity.this, SetUp1Activity.class);
        startActivity(intent);
        finish();
    }
}
