package com.gdmec.jacky.myguard.m3communicationguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import com.gdmec.jacky.myguard.m3communicationguard.db.BlackNumberOpenHelper;
import com.gdmec.jacky.myguard.m3communicationguard.entity.BlackContactInfo;

import java.util.ArrayList;
import java.util.List;


public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;

    public BlackNumberDao(Context context) {
        super();
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }


    public boolean add(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (blackContactInfo.phoneNumber.startsWith("+86")) {
            blackContactInfo.phoneNumber = blackContactInfo.phoneNumber.
                    substring(3, blackContactInfo.phoneNumber.length());
        }
        values.put("number", blackContactInfo.phoneNumber);
        values.put("name", blackContactInfo.contactNmae);
        values.put("mode", blackContactInfo.mode);
        long rowid = db.insert("blacknumber", null, values);
        return rowid != -1;
    }


    public boolean detele(BlackContactInfo blackContactInfo) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        int rownumber = db.delete("blacknumber", "number=?",
                new String[]{blackContactInfo.phoneNumber});
        return rownumber != 0;
    }


    public List<BlackContactInfo> getPageBlackNumber(int pagenumber, int pagesize) {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode,name from blacknumber limit ? offset ? ",
                new String[]{String.valueOf(pagesize), String.valueOf(pagesize * pagenumber)});
        List<BlackContactInfo> mBlackContactInfos = new ArrayList<BlackContactInfo>();
        while (cursor.moveToNext()) {
            BlackContactInfo info = new BlackContactInfo();
            info.phoneNumber = cursor.getString(0);
            info.mode = cursor.getInt(1);
            info.contactNmae = cursor.getString(2);

            mBlackContactInfos.add(info);
        }
        cursor.close();
        db.close();
        SystemClock.sleep(30);
        return mBlackContactInfos;
    }


    public boolean IsNumberExist(String number) {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", null, "number=?", new String[]{number}, null, null, null);
        if (cursor.moveToNext()) {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }


    public int getBlackContactMode(String number) {
        Log.d("incomingg phonenumber", number);
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "number=?", new String[]{number}, null, null, null);
        int mode = 0;
        if (cursor.moveToNext()) {
            mode = cursor.getInt(cursor.getColumnIndex("mode"));
        }
        cursor.close();
        db.close();
        return mode;
    }

    public int getTotalNumber() {
        SQLiteDatabase db = blackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count; // 这句才是截图代码return count;
    }
}
