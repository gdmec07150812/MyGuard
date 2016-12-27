package com.gdmec.jacky.myguard.m1home.utils;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

interface MyCallBack {
    void onSuccess(ResponseInfo<File> arg0);

    void onFailure(HttpException arg0, String arg1);

    void onLoading(long total, long current, boolean isUploading);
}

public class DownloadUtils {


    public void downapk(String url, String tragerFile, final MyCallBack myCallBack) {
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.download(url, tragerFile, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                myCallBack.onSuccess(arg0);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                myCallBack.onFailure(arg0, arg1);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                myCallBack.onLoading(total, current, isUploading);
            }
        });
    }
}