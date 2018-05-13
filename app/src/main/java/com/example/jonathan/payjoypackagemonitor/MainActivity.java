package com.example.jonathan.payjoypackagemonitor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Customization starts

        // Be ready for a demo!
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Schedule the very first job:
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG+"Thread-Runnable", "run");

                JobSchedulingUtils.scheduleJob(getApplicationContext());
            }
        }).start();

        Log.d(TAG, "onCreate: end");
    }
}
