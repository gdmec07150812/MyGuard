package com.gdmec.jacky.myguard.m8trafficmonitor.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.IBinder;

import com.gdmec.jacky.myguard.m8trafficmonitor.db.dao.TrafficDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class TrafficMonitoringService extends Service {

    boolean flag = true;
    private long mOldRxBytes;
    private long mOldTxBytes;
    private TrafficDao dao;
    private SharedPreferences msp;
    private long usedFlow;
    private Thread mThread = new Thread() {
        @Override
        public void run() {
            while (flag) {
                try {
                    Thread.sleep(2000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateTodayGPRS();
            }
        }

        private void updateTodayGPRS() {
            usedFlow = msp.getLong("usedflow", 0);
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (Calendar.DAY_OF_MONTH == 1 & Calendar.HOUR_OF_DAY == 0 & Calendar.MINUTE < 1 & Calendar.SECOND < 30) {
                usedFlow = 0;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dataString = sdf.format(date);
            long moblieGPRS = dao.getMoblieGPRS(dataString);
            long moblieRxBytes = TrafficStats.getMobileRxBytes();
            long moblieTxBytes = TrafficStats.getMobileTxBytes();
            long newGprs = (moblieRxBytes + moblieTxBytes) - mOldRxBytes - mOldTxBytes;
            mOldRxBytes = moblieRxBytes;
            mOldTxBytes = moblieTxBytes;
            if (newGprs < 0) {
                newGprs = moblieRxBytes + moblieTxBytes;
            }
            if (moblieGPRS == -1) {
                dao.insertTodayGPRS(newGprs);
            } else {
                if (moblieGPRS < 0) {
                    moblieGPRS = 0;
                }
                dao.UpdateTodayGPRS(moblieGPRS + newGprs);
            }
            usedFlow = usedFlow + newGprs;
            SharedPreferences.Editor editor = msp.edit();
            editor.putLong("userflow", usedFlow);
            editor.commit();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mOldRxBytes = TrafficStats.getMobileRxBytes();
        mOldTxBytes = TrafficStats.getMobileTxBytes();
        dao = new TrafficDao(this);
        msp = getSharedPreferences("config", MODE_PRIVATE);
        mThread.start();
    }

    @Override
    public void onDestroy() {
        if (mThread != null & !Thread.interrupted()) {
            flag = false;
            mThread.interrupt();
            mThread = null;
        }
        super.onDestroy();
    }
}
