package com.gdmec.jacky.myguard.m3communicationguard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m3communicationguard.db.dao.BlackNumberDao;
import com.gdmec.jacky.myguard.m3communicationguard.entity.BlackContactInfo;

public class AddBlackNumberActivity extends Activity implements View.OnClickListener {

    private CheckBox mSmsCB;
    private CheckBox mTelCB;
    private EditText mNumET;
    private EditText mNameET;
    private BlackNumberDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_black_number);
        dao = new BlackNumberDao(this);
        initView();

    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color
                .bright_purple));
        ((TextView) findViewById(R.id.tv_title)).setText("添加黑名单");
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);

        mSmsCB = (CheckBox) findViewById(R.id.cb_blacknumber_sms);
        mTelCB = (CheckBox) findViewById(R.id.cb_blacknumber_tel);
        mNumET = (EditText) findViewById(R.id.et_blacknumber);
        mNameET = (EditText) findViewById(R.id.et_blackname);
        findViewById(R.id.add_blacknum_btn).setOnClickListener(this);
        findViewById(R.id.add_fromcontact_btn).setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.add_blacknum_btn:
                String number = mNumET.getText().toString().trim();
                String name = mNameET.getText().toString().trim();
                if (TextUtils.isEmpty(number) || TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "电话号码和手机号都不能为空", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    //电话和号码都不为空
                    BlackContactInfo blackContactInfo = new BlackContactInfo();
                    blackContactInfo.phoneNumber = number;
                    blackContactInfo.contactNmae = name;
                    if (mSmsCB.isChecked() & mTelCB.isChecked()) {
                        //两种拦截模式
                        blackContactInfo.mode = 3;

                    } else if (mSmsCB.isChecked() & !mTelCB.isChecked()) {
                        //短信拦截
                        blackContactInfo.mode = 2;
                    } else if (!mSmsCB.isChecked() & mTelCB.isChecked()) {
                        //电话拦截
                        blackContactInfo.mode = 1;
                    } else {
                        Toast.makeText(this, "请选择拦截模式", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!dao.IsNumberExist(blackContactInfo.phoneNumber)) {
                        dao.add(blackContactInfo);
                    } else {
                        Toast.makeText(this, "该号码已经被添加至黑名单", Toast.LENGTH_SHORT).show();
                    }
                    finish();

                }

                break;
            case R.id.add_fromcontact_btn:
                startActivityForResult(new Intent(this, ContactSelectActivity.class), 0);
                break;

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String phone = data.getStringExtra("phone");
            String name = data.getStringExtra("name");
            mNameET.setText(name);
            mNumET.setText(phone);

        }

    }
}
