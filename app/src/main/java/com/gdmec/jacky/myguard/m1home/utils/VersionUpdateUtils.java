package com.gdmec.jacky.myguard.m1home.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m1home.HomeActivity;
import com.gdmec.jacky.myguard.m1home.entity.VersionEntity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class VersionUpdateUtils {

    private static final int MESSAGE_NET_EEOR = 101;
    private static final int MESSAGE_IO_EEOR = 102;
    private static final int MESSAGE_JSON_EEOR = 103;
    private static final int MESSAGE_SHOW_DIALOG = 104;
    private static final int MESSAGE_ENTERHOME = 105;
    private String mVersion;
    private Activity context;
    private ProgressDialog mProgressDialog;
    private VersionEntity versionEntity;
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_IO_EEOR:
                    Toast.makeText(context, "IO异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_EEOR:
                    Toast.makeText(context, "JSON解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_NET_EEOR:
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_SHOW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    Intent intent = new Intent(context, HomeActivity.class);
                    context.startActivity(intent);
                    context.finish();
                    break;

            }
        }

    };

    public VersionUpdateUtils(String Version, Activity activity) {
        mVersion = Version;
        context = activity;
    }

    public void getCloudVersion() {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 5000);

            HttpGet httpGet = new HttpGet("http://192.168.2.1/updateinfo.html");
            HttpResponse execute = client.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200) {

                HttpEntity entity = execute.getEntity();
                String result = EntityUtils.toString(entity, "utf-8");
                Log.d("update", result + "");
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                String code = jsonObject.getString("code");
                versionEntity.versioncode = code;
                String des = jsonObject.getString("des");
                versionEntity.description = des;
                String apkurl = jsonObject.getString("apkurl");
                versionEntity.apkurl = apkurl;
                if (!mVersion.equals(versionEntity.versioncode)) {
                    handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
                }
            }

        } catch (ClientProtocolException e) {
            handler.sendEmptyMessage(MESSAGE_NET_EEOR);
            e.printStackTrace();
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }

    }


    private void showUpdateDialog(final VersionEntity versionEntity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到新版本:" + versionEntity.versioncode);
        builder.setMessage(versionEntity.description);
        builder.setCancelable(false);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                initProgressDialog();
                downloadNewApk(versionEntity.apkurl);
            }
        });

        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();

    }

    private void initProgressDialog() {
        Log.d("VersionUpdateUtils", "下载提示");
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("准备下载....");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    protected void downloadNewApk(String apkurl) {
        Log.d("VersionUpdateUtils", "下载中...");
        DownloadUtils downloadUtils = new DownloadUtils();
        downloadUtils.downapk(apkurl, "/mnt/sdcard/mobilesafe2.0.apk", new MyCallBack() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                Log.d("VersionUpdateUtils", "下载成功");
                //mProgressDialog.dismiss();
                MyUtils.installApk(context);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Log.d("VersionUpdateUtils", "下载失败");
                mProgressDialog.setMessage("下载失败");
                mProgressDialog.dismiss();
                enterHome();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                mProgressDialog.setMax((int) total);
                mProgressDialog.setMessage("正在下载...");
                mProgressDialog.setProgress((int) current);
            }
        });
    }


    private void enterHome() {
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME, 2000);
    }

}
