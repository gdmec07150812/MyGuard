package com.gdmec.jacky.myguard.m3communicationguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsMessage;
import android.util.Log;

import com.gdmec.jacky.myguard.m3communicationguard.db.dao.BlackNumberDao;

public class InterceptSmsReciever extends BroadcastReceiver {
    public InterceptSmsReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("test m3", "InterceptSmsReciever广播开启");
        SharedPreferences mSp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean BlackNumStatus = mSp.getBoolean("BlackNumStatus", true);
        if (!BlackNumStatus) {
            Log.d("test m3", "黑名单拦截关闭");
            //黑名单拦截关闭
            return;
        }
        //如果是黑名单,终止广播
        Log.d("test m3", "是黑名单,终止广播");
        BlackNumberDao dao = new BlackNumberDao(context);
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String sender = smsMessage.getOriginatingAddress();
            String body = smsMessage.getMessageBody();
            if (sender.startsWith("+86")) {
                sender = sender.substring(3, sender.length());
            }
            int mode = dao.getBlackContactMode(sender);
            if (mode == 2 || mode == 3) {
                //需要拦截短信,拦截广播
                Log.d("test m3", "需要拦截短信,拦截广播");
                abortBroadcast();
            }
        }

    }
}
