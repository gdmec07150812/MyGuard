package com.gdmec.jacky.myguard.m9advancedtools.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by dell-pc on 2016/12/19.
 */
public class UiUtils {

    public static void showToast(final Activity context, final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run()

                {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
