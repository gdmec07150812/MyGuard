package com.gdmec.jacky.myguard.m8trafficmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m7processmanager.utils.SystemInfoUtils;
import com.gdmec.jacky.myguard.m8trafficmonitor.db.dao.TrafficDao;
import com.gdmec.jacky.myguard.m8trafficmonitor.service.TrafficMonitoringService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TrafficMonitoringActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences mSP;
    private Button mCorrentFlowBtn;
    private TextView mTotalTV;
    private TextView mUsedTV;
    private TextView mToDayTV;
    private TrafficDao dao;
    private ImageView mRemindIMGV;
    private TextView mRemindTV;
    private CorrectFlowReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_traffic_monitoring);
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        boolean flag = mSP.getBoolean("isset_operator", false);
        if (!flag) {
            startActivity(new Intent(this, OperatorSetActivity.class));
            finish();
        }
        if (!SystemInfoUtils.isServiceRunning(this, "com.gdmec.jacky.myguard.m8trafficmonitor.service.TrafficMonitoringService")) {
            startService(new Intent(this, TrafficMonitoringService.class));
        }
        initView();
        registReceive();
        initData();
    }

    private void initView() {
        findViewById(R.id.rl_titlebar).setBackgroundColor(
                getResources().getColor(R.color.light_green)
        );
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("流量监控");
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        mCorrentFlowBtn = (Button) findViewById(R.id.btn_correction_flow);
        mCorrentFlowBtn.setOnClickListener(this);
        mTotalTV = (TextView) findViewById(R.id.tv_month_totalgprs);
        mUsedTV = (TextView) findViewById(R.id.tv_month_usedgprs);
        mToDayTV = (TextView) findViewById(R.id.tv_today_gprs);
        mRemindIMGV = (ImageView) findViewById(R.id.imgv_traffic_remind);
        mRemindTV = (TextView) findViewById(R.id.tv_traffic_remind);
    }


    private void initData() {
        long totalflow = mSP.getLong("totalflow", 0);
        long usedflow = mSP.getLong("usedflow", 0);
        if (totalflow > 0 & usedflow >= 0) {
            float scale = usedflow / totalflow;
            if (scale > 0.9) {
                mRemindIMGV.setEnabled(false);
                mRemindTV.setText("您的套餐流量即将用完！");
            } else {
                mRemindIMGV.setEnabled(true);
                mRemindTV.setText("本月流量充足请放心使用！");
            }
        }
        mTotalTV.setText("本月流量：" + Formatter.formatFileSize(this, totalflow));
        mUsedTV.setText("本月已用：" + Formatter.formatFileSize(this, usedflow));
        dao = new TrafficDao(this);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataString = sdf.format(date);
        long moblieGPRS = dao.getMoblesGPRS(dataString);
        if (moblieGPRS < 0) {
            moblieGPRS = 0;
        }
        mToDayTV.setText("本日已用：" + Formatter.formatFileSize(this, moblieGPRS));
    }

    private void registReceive() {
        receiver = new CorrectFlowReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.btn_correction_flow:
                int i = mSP.getInt("operator", 0);
                SmsManager smsManager = SmsManager.getDefault();
                switch (i) {
                    case 0:
                        Toast.makeText(this, "您还没有设置运营商信息", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        break;
                    case 2:
                        smsManager.sendTextMessage("10010", null, "CXLL", null, null);
                        break;
                    case 3:
                        break;

                }

        }
    }

    private long getStringTofloat(String str) {
        long flow = 0;
        if (!TextUtils.isEmpty(str)) {
            if (str.contains("KB")) {
                String[] split = str.split("KB");
                float m = Float.parseFloat(split[0]);
                flow = (long) (m * 1024);
            } else if (str.contains("MB")) {
                String[] split = str.split("MB");
                float m = Float.parseFloat(split[0]);
                flow = (long) (m * 1024 * 1024);
            } else if (str.contains("GB")) {
                String[] split = str.split("GB");
                float m = Float.parseFloat(split[0]);
                flow = (long) (m * 1024 * 1024 * 1024);
            }
        }
        return flow;
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    class CorrectFlowReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String body = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                if (!address.equals("10010")) {
                    return;
                }
                String[] split = body.split(",");
                long left = 0;
                long used = 0;
                long beyond = 0;
                for (int i = 0; i < split.length; i++) {
                    if (split[i].contains("本月流量已使用")) {
                        String usedflow = split[i].substring(7, split[i].length());
                        used = getStringTofloat(usedflow);
                    } else if (split[i].contains("剩余流量")) {
                        String leftflow = split[i].substring(4, split[i].length());
                        left = getStringTofloat(leftflow);
                    } else if (split[i].contains("套餐外流量")) {
                        String beyondflow = split[i].substring(5, split[i].length());
                        beyond = getStringTofloat(beyondflow);
                    }
                }
                SharedPreferences.Editor edit = mSP.edit();
                edit.putLong("totalflow", used + left);
                edit.putLong("usedflow", used + beyond);
                edit.commit();
                mTotalTV.setText("本月流量：" + Formatter.formatFileSize(context, (used + left)));
                mUsedTV.setText("本月已用：" + Formatter.formatFileSize(context, (used + beyond)));
            }

        }

    }
}
