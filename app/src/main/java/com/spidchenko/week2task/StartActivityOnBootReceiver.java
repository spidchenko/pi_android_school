package com.spidchenko.week2task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.spidchenko.week2task.ui.LoginActivity;

public class StartActivityOnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent(context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
