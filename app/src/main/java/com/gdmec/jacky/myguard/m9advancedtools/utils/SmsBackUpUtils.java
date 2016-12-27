package com.gdmec.jacky.myguard.m9advancedtools.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SmsBackUpUtils {

    private boolean flag = true;

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean backUpSms(Context context, BackupStatusCallback callback) throws IllegalStateException, IOException {
        XmlSerializer serializer = Xml.newSerializer();
        File sdDir = Environment.getExternalStorageDirectory();
        long freesize = sdDir.getFreeSpace();
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) && freesize > 1024l * 1024l) {
            File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
            FileOutputStream os = new FileOutputStream(file);
            serializer.setOutput(os, "utf-8");
            serializer.startDocument("utf-8", true);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            Cursor cursor = resolver.query(uri, new String[]{
                    "address", "body", "type", "date"
            }, null, null, null);
            int size = cursor.getCount();
            //设置进度的总大小
            callback.beforeSmsBackup(size);
            serializer.startTag(null, "smss");
            serializer.attribute(null, "size", String.valueOf(size));
            int process = 0;
            while (cursor.moveToNext() & flag) {
                serializer.startTag(null, "sms");
                serializer.startTag(null, "body");
                //可能会有乱码问题需处理，如果出现乱码会导致备份失败
                try {
                    String bodyencpyt = Crypto.encrypt("123", cursor.getString(1));
                    serializer.text(bodyencpyt);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    serializer.text("短信读取失败");
                }
                serializer.endTag(null, "body");
                serializer.startTag(null, "address");
                serializer.text(cursor.getString(0));
                serializer.endTag(null, "address");
                serializer.startTag(null, "type");
                serializer.text(cursor.getString(2));
                serializer.endTag(null, "type");
                serializer.startTag(null, "date");
                serializer.text(cursor.getString(3));
                serializer.endTag(null, "date");
                serializer.endTag(null, "sms");
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //设置进度条对话框的进度
                process++;
                callback.onSmsBackup(process);
            }
            cursor.close();
            serializer.endTag(null, "smss");
            serializer.endDocument();
            os.flush();
            os.close();
            return flag;
        } else {
            throw new IllegalStateException("sd卡不存在或空间不足");
        }
    }

    public interface BackupStatusCallback {
        void beforeSmsBackup(int size);

        void onSmsBackup(int process);
    }
}
