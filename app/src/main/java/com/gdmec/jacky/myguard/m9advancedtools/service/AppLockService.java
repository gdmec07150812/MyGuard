package com.gdmec.jacky.myguard.m9advancedtools.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.gdmec.jacky.myguard.m9advancedtools.EnterPswActivity;
import com.gdmec.jacky.myguard.m9advancedtools.db.dao.AppLockDao;

import java.util.List;

/**
 * Created by dell-pc on 2016/12/19.
 */
public class AppLockService extends Service {
    private boolean flag = false;
    private AppLockDao dao;
    private Uri uri = Uri.parse("content://com.gdmec.jacky.myguard.applock");
    private List<String> packagenames;
    private Intent intent;
    private ActivityManager am;
    private List<ActivityManager.RunningTaskInfo> taskInfos;
    private ActivityManager.RunningTaskInfo taskInfo;
    private String packagename;
    private String tempStopProtectPackname;
    private AppLockReceiver receiver;
    private MyObserver observer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //创建APPLockDao实例
        dao = new AppLockDao(this);
        observer = new MyObserver(new Handler());
        getContentResolver().registerContentObserver(uri, true, observer);
        //获取数据库中的所有包名
        packagenames = dao.findAll();
        receiver = new AppLockReceiver();
        IntentFilter filter = new IntentFilter("com.gdmec.jacky.myguard.applock");
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
        //创建Intent实例,用来打开输入密码界面
        intent = new Intent(AppLockService.this, EnterPswActivity.class);
        //获取ActivityManager对象
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        startAppLockService();
        super.onCreate();
    }

    private void startAppLockService() {
        new Thread() {
            public void run() {
                flag = true;
                while (flag) {
                    //监视任务栈的情况，最近使用的打开的任务栈在集合的最前面
                    taskInfos = am.getRunningTasks(1);
                    //最近使用的任务栈
                    taskInfo = taskInfos.get(0);
                    packagename = taskInfo.topActivity.getPackageName();
                    //判断这个包名是否需要被保护
                    if (packagenames.contains(packagename)) {
                        //判断当前应用程序是否需要临时停止保护（输入了正确密码）
                        if (!packagename.equals(tempStopProtectPackname)) {
                            //需要保护，弹出一个输入密码的界面
                            intent.putExtra("packagename", packagename);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        flag = false;
        unregisterReceiver(receiver);
        receiver = null;
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        super.onDestroy();
    }

    //广播接收者
    class AppLockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("cn.itcast.mobilesafe.applock".equals(intent.getAction())) {
                tempStopProtectPackname = intent.getStringExtra("packagename");
            } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                tempStopProtectPackname = null;
                //停止监控程序
                flag = false;
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                //开启监控程序
                if (flag == false) {
                    startAppLockService();
                }
            }
        }
    }

    //内容观察者
    class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            packagenames = dao.findAll();
            super.onChange(selfChange);
        }
    }
}
