package com.spidchenko.week2task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryLevelReceiver extends BroadcastReceiver {
    private int mPreviousBatteryLevel;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
            if ((level > 0) && (scale > 0)) {
                int currentBatteryLevel = (level * 100) / scale;
                if (currentBatteryLevel != mPreviousBatteryLevel) {
                    Toast.makeText(context, currentBatteryLevel + "%", Toast.LENGTH_LONG).show();
                    mPreviousBatteryLevel = currentBatteryLevel;
                }
            }
        }
    }
}
