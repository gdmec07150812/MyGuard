package com.gdmec.jacky.myguard.m3communicationguard.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.gdmec.jacky.myguard.m3communicationguard.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class InterceptCallReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("test", "InterceptCallReciever广播启动");
        SharedPreferences mSP = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        boolean BlackNumStatus = mSP.getBoolean("BlackNumStatus", true);

        if (!BlackNumStatus) {
            return;
        }
        BlackNumberDao dao = new BlackNumberDao(context);

        if (!intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            String mIncomingNumber = "";

            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            switch (tManager.getCallState()) {

                case TelephonyManager.CALL_STATE_RINGING:
                    mIncomingNumber = intent.getStringExtra("incoming_number");
                    if (mIncomingNumber == null) {
                        return;
                    }
                    int blackContactMode = dao.getBlackContactMode(mIncomingNumber);
                    if (blackContactMode == 1 || blackContactMode == 3) {
                        Uri uri = Uri.parse("content://call_log/calls");
                        context.getContentResolver().registerContentObserver(
                                uri, true, new CallLogObserver(new Handler(), mIncomingNumber, context));
                        endCall(context);
                    }
                    break;
            }
        }
    }

    //挂断电话
    public void endCall(Context context) {
        try {
            Class clazz = context.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony itelephony = ITelephony.Stub.asInterface(iBinder);
            itelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class CallLogObserver extends ContentObserver {
        private String incomingNumber;
        private Context content;

        public CallLogObserver(Handler handler, String incomingNumber, Context context) {
            super(handler);
            this.incomingNumber = incomingNumber;
            this.content = context;
        }


        //数据库内容变化是调用
        @Override
        public void onChange(boolean selfChange) {
            Log.i("CallLogObserver", "呼叫记录数据库内容变化了");
            content.getContentResolver().unregisterContentObserver(this);
            deleteCallLog(incomingNumber, content);
            super.onChange(selfChange);
        }

        //清楚呼叫记录
        private void deleteCallLog(String incomingNumber, Context content) {
            ContentResolver resolver = content.getContentResolver();
            Uri uri = Uri.parse("content://call_log/calls");
            Cursor cursor = resolver.query(uri, new String[]{"_id"}, "number=?",
                    new String[]{incomingNumber}, "_id desc limit 1");
            if (cursor.moveToNext()) {
                String id = cursor.getString(0);
                resolver.delete(uri, "_id=?", new String[]{id});
            }
        }


    }
}
