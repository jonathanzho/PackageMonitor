package com.example.jonathan.payjoypackagemonitor;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

public class TestJobService extends JobService {
    private static final String TAG = TestJobService.class.getSimpleName();

    // Do small ask here or start a big task in another thread.
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"onStartJob");

        // Reschedule job, as we don't set periodic job:
        JobSchedulingUtils.scheduleJob(getApplicationContext());    // reschedule the job

        // Perform job here:
        JobSchedulingUtils.performJob(getApplicationContext());

        // Inform
        jobFinished(params, false);    // false = Don't reschedule the job.

        return true;    // true = synchronously
    }

    // Handle half-finished job here.
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG,"onStopJob");

        // Clean up the job here.

        return true; // true = reschedule the job.
    }
}
