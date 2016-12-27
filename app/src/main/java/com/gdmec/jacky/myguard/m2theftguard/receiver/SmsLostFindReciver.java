package com.gdmec.jacky.myguard.m2theftguard.receiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m2theftguard.service.GPSLocationService;

public class SmsLostFindReciver extends BroadcastReceiver {

    private static final String TAG = SmsLostFindReciver.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private ComponentName componentName;
    private MediaPlayer player = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("test", "短信广播开启");
        sharedPreferences = context.getSharedPreferences("config", Activity.MODE_PRIVATE);


        boolean protecting = sharedPreferences.getBoolean("protecting", true);
        Log.d("test", "开启安全号码");
        if (protecting) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context
                    .DEVICE_POLICY_SERVICE);
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String sender = smsMessage.getOriginatingAddress();
                if (sender.startsWith("+86")) {
                    sender = sender.substring(3, sender.length());
                }
                String body = smsMessage.getMessageBody();
                Log.d("test", body + "");
                String safephone = sharedPreferences.getString("safephone", null);
                Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
                //如果短信是安全号码发送的
                if (!TextUtils.isEmpty(safephone) & sender.equals(safephone)) {

                    Log.d("test", "安全号码发送短信");
                    if ("#*location*#".equals(body)) {
                        Log.i(TAG, "返回位置信息");
                        Intent service = new Intent(context, GPSLocationService.class);
                        context.startService(service);
                        abortBroadcast();
                    } else if ("#*alarm*#".equals(body)) {
                        Log.i(TAG, "播放报警音乐");
                        //MediaPlayer start方法调用后必须reset后才能再次调用start方法播放音乐
                        if (player == null) {
                            player = MediaPlayer.create(context, R.raw.ylzs);
                            player.setVolume(1.0f, 1.0f);
                            player.start();
                        } else {
                            player.reset();
                            player.start();
                        }
                        abortBroadcast();
                    } else if ("#*wipedata*#".equals(body)) {
                        Log.i(TAG, "远程清除数据");
                        dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                        abortBroadcast();
                    } else if ("#*lockScreen*#".equals(body)) {
                        Log.i(TAG, "远程锁屏");
                        dpm.resetPassword("1234", 0);
                        dpm.lockNow();
                        abortBroadcast();
                    }
                }

            }
        }
    }
}
