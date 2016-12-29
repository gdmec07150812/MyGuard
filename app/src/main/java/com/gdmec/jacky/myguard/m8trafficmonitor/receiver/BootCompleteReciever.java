package com.gdmec.jacky.myguard.m8trafficmonitor.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gdmec.jacky.myguard.m8trafficmonitor.service.TrafficMonitoringService;
import com.gdmec.jacky.myguard.m8trafficmonitor.utils.SystemInfoUtils;


public class BootCompleteReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SystemInfoUtils.isServiceRunning(context, "com.gdmec.jacky.myguard.m8trafficmonitor.service.TrafficMonitoringService")) {
            Log.d("traffic service", "turn on");
            context.startService(new Intent(context, TrafficMonitoringService.class));
        }
    }
}
