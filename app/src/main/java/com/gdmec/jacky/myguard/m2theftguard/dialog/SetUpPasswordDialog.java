package com.gdmec.jacky.myguard.m2theftguard.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gdmec.jacky.myguard.R;

public class SetUpPasswordDialog extends Dialog implements View.OnClickListener {
    public EditText mFirstPWDET;
    public EditText mAffirmET;
    private TextView mTitleTV;
    private MyCallBack myCallBack;

    public SetUpPasswordDialog(Context context) {
        super(context, R.style.dialog_custom);
    }

    public void setCallBack(MyCallBack myCallBack) {
        this.myCallBack = myCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.setup_password_dialog);
        super.onCreate(savedInstanceState);

        initView();
    }

    public void initView() {
        mTitleTV = (TextView) findViewById(R.id.tv_interpwd_title);
        mFirstPWDET = (EditText) findViewById(R.id.et_firstpwd);
        mAffirmET = (EditText) findViewById(R.id.et_affirm_password);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancle).setOnClickListener(this);
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitleTV.setText(title);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                myCallBack.ok();
                break;
            case R.id.btn_cancle:
                myCallBack.cancle();
                break;
        }
    }

    public interface MyCallBack {
        void ok();

        void cancle();
    }
}
