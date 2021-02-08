package com.spidchenko.week2task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.spidchenko.week2task.ui.MainActivity;

public class StartActivityOnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "StartActivity.LOG_TAG";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d(TAG, "onReceive: RECEIVED" + intent.getExtras());
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}