package com.example.jonathan.payjoypackagemonitor;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JobSchedulingUtils {
    private static final String TAG = "PJPM " + JobSchedulingUtils.class.getSimpleName();

    private static final int QUERY_PACKAGES_JOB_ID = 123;

    private static final long MIN_LATENCY_SECS = 10;
    private static final long OVERRIDE_DEADLINE_SECS = 30;
    private static final long INTERVAL_SECS = 60;

    private static int sSequenceNumber = 0;

    private static List<String> sPrevInstalledPackages = null;

    public static void scheduleJob(Context context) {
        Log.d(TAG, "scheduleJob: start");

        // Build job info to store conditions for scheduling the job.
        ComponentName serviceComponent = new ComponentName(context, TestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(QUERY_PACKAGES_JOB_ID, serviceComponent);
        // A workaround starting Android N to schedule periodic job for <= 15 secs:
        builder.setMinimumLatency(MIN_LATENCY_SECS * 1000);    // wait at least
        builder.setOverrideDeadline(OVERRIDE_DEADLINE_SECS * 1000);    // maximum delay
        builder.setPeriodic(INTERVAL_SECS * 1000);    // Starting Android N, >= 15 secs works.
        JobScheduler jobScheduler = context.getSystemService(JobScheduler.class);
        JobInfo jobInfo = builder.build();

        //Log.v(TAG, "scheduleJob: jobInfo=[" + jobInfo + "]");

        int result = jobScheduler.schedule(jobInfo);

        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "scheduleJob: end with success");
        } else {
            Log.e(TAG, "scgeduleJob: end with failure !!!");
        }
    }

    public static void performJob(Context context) {
        Log.d(TAG, "performJob: start: sSequenceNumber=[" + sSequenceNumber + "]");

/*
        try {
            PackageManager packageManager = context.getPackageManager();

            // This API is not reliable. In most cases, it returns all the installed packages.
            ChangedPackages changedPackages = packageManager.getChangedPackages(sSequenceNumber);

            if (changedPackages == null) {
                Log.i(TAG, "performJob: NO CHANGED PACKAGES");
            } else {
                List<String> changedPackageNames = changedPackages.getPackageNames();

                for (String pn : changedPackageNames) {
                    Log.v(TAG, "performJob: CHANGED PACKAGE NAME=[" + pn + "]");
                }
            }

            sSequenceNumber++;

        } catch (Exception e) {
            e.printStackTrace();

            sSequenceNumber = 0;
        }
*/
        List<String> installedPackages = PackageUtils.getInstalledPackageNames(context);
/*
        for (String pn : installedPackages) {
            Log.v(TAG, "performJob: INSTALLED PACKAGE NAME=[" + pn + "]");
        }
*/
        Log.v(TAG, "TOTAL NUMBER OF CURRENTLY INSTALLED PACKAGES=[" + installedPackages.size() + "]");

        if (sPrevInstalledPackages == null) {
            sPrevInstalledPackages = installedPackages;
        }

        Set<String> removedPackages = PackageUtils.operate2StringLists(sPrevInstalledPackages, installedPackages, PackageUtils.GET_1_MINUS_2_OR_REMOVED);
        Log.v(TAG, "TOTAL NUMBER OF RECENTLY REMOVED PACKAGES   =[" + removedPackages.size() + "]");

        Set<String> addedPackages = PackageUtils.operate2StringLists(sPrevInstalledPackages, installedPackages, PackageUtils.GET_2_MINUS_1_OR_ADDED);
        Log.v(TAG, "TOTAL NUMBER OF RECENTLY ADDED PACKAGES     =[" + addedPackages.size() + "]");

        // Prepare for next call:
        sSequenceNumber++;
        sPrevInstalledPackages = installedPackages;

        Log.d(TAG, "performJob: end");
    }
}