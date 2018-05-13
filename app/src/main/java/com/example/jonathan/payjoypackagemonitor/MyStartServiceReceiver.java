package com.example.jonathan.payjoypackagemonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

// This is no longer active, as a fake system Intent can't be generated if "adb root" does not work.
public class MyStartServiceReceiver extends BroadcastReceiver {
    private static final String TAG = MyStartServiceReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: start");

        JobSchedulingUtils.scheduleJob(context);

        Log.d(TAG, "onReceive: end");
    }
}
