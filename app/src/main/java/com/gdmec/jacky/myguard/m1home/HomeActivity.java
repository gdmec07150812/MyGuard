package com.gdmec.jacky.myguard.m1home;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.gdmec.jacky.myguard.R;
import com.gdmec.jacky.myguard.m10settings.SettingsActivity;
import com.gdmec.jacky.myguard.m1home.adapter.HomeAdapter;
import com.gdmec.jacky.myguard.m2theftguard.LostFindActivity;
import com.gdmec.jacky.myguard.m2theftguard.dialog.InterPasswordDialog;
import com.gdmec.jacky.myguard.m2theftguard.dialog.SetUpPasswordDialog;
import com.gdmec.jacky.myguard.m2theftguard.receiver.MyDeviceAdminReciever;
import com.gdmec.jacky.myguard.m2theftguard.utils.MD5Utils;
import com.gdmec.jacky.myguard.m3communicationguard.SecurityPhoneActivity;
import com.gdmec.jacky.myguard.m4appmanager.AppManagerActivity;
import com.gdmec.jacky.myguard.m5virusscan.VirusScanActivity;
import com.gdmec.jacky.myguard.m6cleancache.CacheClearListActivity;
import com.gdmec.jacky.myguard.m7processmanager.ProcessManagerActivity;
import com.gdmec.jacky.myguard.m8trafficmonitor.TrafficMonitoringActivity;
import com.gdmec.jacky.myguard.m9advancedtools.AdvancedToolsActivity;
import com.gdmec.jacky.myguard.myweather.Weather;
import com.gdmec.jacky.mymap.MyMap;

public class HomeActivity extends Activity {

    private GridView gv_home;

    private SharedPreferences msharedPreferences;

    private DevicePolicyManager policyManager;

    private ComponentName componentName;

    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);

        msharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        TextView mTitleTV = (TextView) findViewById(R.id.tv_title);
        mTitleTV.setText("GDMEC手机卫士");
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.green360));
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapter(HomeActivity.this));
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (isSetUpPassword()) {
                            showInterPswdDialog();
                        } else {
                            showSetUpPswdDialog();
                        }
                        break;
                    case 1:
                        startActivity(SecurityPhoneActivity.class);
                        break;
                    case 2:
                        startActivity(AppManagerActivity.class);
                        break;
                    case 3:
                        startActivity(VirusScanActivity.class);

                        break;
                    case 4:
                        startActivity(CacheClearListActivity.class);
                        break;
                    case 5:
                        startActivity(ProcessManagerActivity.class);
                        break;
                    case 6:
                        startActivity(TrafficMonitoringActivity.class);
                        break;
                    case 7:
                        startActivity(AdvancedToolsActivity.class);
                        break;
                    case 8:
                        startActivity(SettingsActivity.class);
                        break;
                    case 9:
                        startActivity(Weather.class);
                        break;
                    case 10:
                        startActivity(MyMap.class);
                }
            }
        });
        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, MyDeviceAdminReciever.class);
        boolean active = policyManager.isAdminActive(componentName);
        if (!active) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "获取超级管理员权限,用于远程锁屏和清除数据");
            startActivity(intent);
        }

    }

    private void showSetUpPswdDialog() {
        final SetUpPasswordDialog setUpPasswordDialog = new SetUpPasswordDialog(HomeActivity.this);
        setUpPasswordDialog.setCallBack(new SetUpPasswordDialog.MyCallBack() {
            @Override
            public void ok() {
                String firstPwsd = setUpPasswordDialog.mFirstPWDET.getText().toString().trim();
                String affirmPwsd = setUpPasswordDialog.mAffirmET.getText().toString().trim();


                if (!TextUtils.isEmpty(firstPwsd) && !TextUtils.isEmpty(affirmPwsd)) {

                    if (firstPwsd.equals(affirmPwsd)) {

                        savePswd(affirmPwsd);
                        setUpPasswordDialog.dismiss();
                        showInterPswdDialog();
                    } else {
                        Toast.makeText(HomeActivity.this, "两次密码不一致!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancle() {
                setUpPasswordDialog.dismiss();
            }
        });
        setUpPasswordDialog.setCancelable(true);
        setUpPasswordDialog.show();
    }

    private void showInterPswdDialog() {

        final String password = getPassword();
        final InterPasswordDialog mInPswdDialog = new InterPasswordDialog(HomeActivity.this);

        mInPswdDialog.setCallBack(new InterPasswordDialog.MyCallBack() {
            @Override
            public void confirm() {
                if (TextUtils.isEmpty(mInPswdDialog.getPassword())) {
                    Toast.makeText(HomeActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
                } else if (password.equals(MD5Utils.encode(mInPswdDialog.getPassword()))) {

                    mInPswdDialog.dismiss();
                    startActivity(LostFindActivity.class);
                } else {
                    mInPswdDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "密码有误,请重新输入!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancle() {
                mInPswdDialog.dismiss();
            }
        });

        mInPswdDialog.setCancelable(true);
        mInPswdDialog.show();
    }

    private void savePswd(String affirmPwsd) {
        SharedPreferences.Editor edit = msharedPreferences.edit();
        edit.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPwsd));
        edit.commit();
    }

    private String getPassword() {
        String password = msharedPreferences.getString("PhoneAntiTheftPWD", null);
        if (TextUtils.isEmpty(password)) {
            return "";
        }
        return password;
    }

    private boolean isSetUpPassword() {
        String password = msharedPreferences.getString("PhoneAntiTheftPWD", null);
        return !TextUtils.isEmpty(password);
    }

    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(HomeActivity.this, cls);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
