package com.gdmec.jacky.myguard.m9advancedtools.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.gdmec.jacky.myguard.m9advancedtools.db.AppLockOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AppLockDao {

    private Context context;
    private AppLockOpenHelper openHelper;
    private Uri uri = Uri.parse("content://com.gdmec.jacky.myguard.applock");

    public AppLockDao(Context context) {
        this.context = context;
        openHelper = new AppLockOpenHelper(context);
    }

    public boolean insert(String packagename) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packagename);
        long rowid = db.insert("applock", null, values);
        if (rowid == -1) {
            return false;
        } else {
            context.getContentResolver().notifyChange(uri, null);
            return true;
        }
    }

    public boolean delete(String packagename) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int rownum = db.delete("applock", "packagename=?", new String[]{packagename});
        if (rownum == 0) {
            return false;
        } else {
            context.getContentResolver().notifyChange(uri, null);
            return true;
        }
    }

    public boolean find(String packagename) {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query("applock", null, "packagename=?", new String[]{packagename}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public List<String> findAll() {
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor cursor = db.query("applock", null, null, null, null, null, null, null);
        List<String> packages = new ArrayList<String>();
        while (cursor.moveToNext()) {
            String string = cursor.getString(cursor.getColumnIndex("packagename"));
            packages.add(string);
        }
        return packages;
    }
}