package com.gdmec.jacky.myguard.m3communicationguard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m3communicationguard.adapter.BlackContactAdapter;
import com.gdmec.jacky.myguard.m3communicationguard.db.dao.BlackNumberDao;
import com.gdmec.jacky.myguard.m3communicationguard.entity.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;

public class SecurityPhoneActivity extends Activity implements View.OnClickListener {

    public BlackContactAdapter adapter;
    private FrameLayout mHaveBlackNumber;
    private FrameLayout mNoBlackNumber;
    private BlackNumberDao dao;
    private ListView mListView;
    private int pagenumber = 0;
    private int pagesize = 15;
    private int totalNUmber;
    private List<BlackContactInfo> pageBlackNumber = new ArrayList<BlackContactInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_security_phone);
        initView();
        fillData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (totalNUmber != dao.getTotalNumber()) {
            //数据发生变化
            if (dao.getTotalNumber() > 0) {
                mHaveBlackNumber.setVisibility(View.VISIBLE);
                mNoBlackNumber.setVisibility(View.GONE);
            } else {
                mHaveBlackNumber.setVisibility(View.GONE);
                mNoBlackNumber.setVisibility(View.VISIBLE);
            }
            pagenumber = 0;
            pageBlackNumber.clear();
            pageBlackNumber.addAll(dao.getPageBlackNumber(pagenumber, pagesize));
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void fillData() {

        dao = new BlackNumberDao(SecurityPhoneActivity.this);
        totalNUmber = dao.getTotalNumber();

        if (totalNUmber == 0) {
            //数据库中没有黑名单记录
            mHaveBlackNumber.setVisibility(View.GONE);
            mNoBlackNumber.setVisibility(View.VISIBLE);

        } else if (totalNUmber > 0) {
            //数据库中有黑名单记录
            mHaveBlackNumber.setVisibility(View.VISIBLE);
            mNoBlackNumber.setVisibility(View.GONE);
            pagenumber = 0;
            if (pageBlackNumber.size() > 0) {
                pageBlackNumber.clear();
            }
            pageBlackNumber.addAll(dao.getPageBlackNumber(pagenumber, pagesize));
            if (adapter == null) {
                adapter = new BlackContactAdapter(pageBlackNumber, SecurityPhoneActivity.this);
                adapter.setCallBack(new BlackContactAdapter.BlackConactCallBack() {
                    @Override
                    public void DataSizeChanged() {
                        fillData();
                    }
                });
                mListView.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().
                getColor(R.color.bright_purple));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("通讯卫士");
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);

        mHaveBlackNumber = (FrameLayout) findViewById(R.id.fl_haveblacknumber);
        mNoBlackNumber = (FrameLayout) findViewById(R.id.fl_noblacknumber);
        findViewById(R.id.btn_addblacknumber).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv_blacknumbers);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://没有滑动状态
                        //获取最后一个课件条目
                        int lastVisiblePosition = mListView.getLastVisiblePosition();
                        //如果当前条目是最后一个 增查询更多的数据
                        if (lastVisiblePosition == pageBlackNumber.size() - 1) {
                            pagenumber++;
                            if (pagenumber * pagesize >= totalNUmber) {
                                Toast.makeText(SecurityPhoneActivity.this, "没有更多的数据了"
                                        , Toast.LENGTH_SHORT).show();

                            } else {
                                pageBlackNumber.addAll(dao.getPageBlackNumber(pagenumber, pagesize));
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.btn_addblacknumber:
                //跳转到添加黑名单页面
                //首次添加黑名单没有更新数据
                // startActivity(new Intent(this, AddBlackNumberActivity.class));
                //修正版
                startActivityForResult(new Intent(this, AddBlackNumberActivity.class), 0);
                break;
        }

    }

    //修正版
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (adapter == null) {
            adapter = new BlackContactAdapter(pageBlackNumber, SecurityPhoneActivity.this);
            adapter.setCallBack(new BlackContactAdapter.BlackConactCallBack() {
                @Override
                public void DataSizeChanged() {
                    fillData();
                }
            });
            mListView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
