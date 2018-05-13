package com.example.jonathan.payjoypackagemonitor;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
    private static final String TAG = Util.class.getSimpleName();

    private static final int QUERY_PACKAGES_JOB_ID = 123;

    private static final long MIN_LATENCY_SECS = 10;
    private static final long OVERRIDE_DEADLINE_SECS = 30;
    private static final long INTERVAL_SECS = 10;

    private static int sSequenceNumber = 0;

    private static List<String> sPrevInstalledPackages = null;

    private static final int GET_1_MINUS_2_OR_REMOVED = 0;
    private static final int GET_2_MINUS_1_OR_ADDED   = 1;

    public static void scheduleJob(Context context) {
        Log.d(TAG, "scheduleJob: start");

        // Build job info to store conditions for scheduling the job.
        ComponentName serviceComponent = new ComponentName(context, TestJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(QUERY_PACKAGES_JOB_ID, serviceComponent);
        // A workaround starting Android N to schedule periodic job for <= 15 secs:
        builder.setMinimumLatency(MIN_LATENCY_SECS * 1000);    // wait at least
        builder.setOverrideDeadline(OVERRIDE_DEADLINE_SECS * 1000);    // maximum delay
        // builder.setPeriodic(INTERVAL_SECS * 1000);    // Starting Android N, >= 15 works.
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
        List<String> installedPackages = getInstalledPackageNames(context);
/*
        for (String pn : installedPackages) {
            Log.v(TAG, "performJob: INSTALLED PACKAGE NAME=[" + pn + "]");
        }
*/
        Log.v(TAG, "TOTAL NUMBER OF CURRENTLY INSTALLED PACKAGES=[" + installedPackages.size() + "]");

        if (sPrevInstalledPackages == null) {
            sPrevInstalledPackages = installedPackages;
        }

        Set<String> removedPackages = operate2StringLists(sPrevInstalledPackages, installedPackages, GET_1_MINUS_2_OR_REMOVED);
        Log.v(TAG, "TOTAL NUMBER OF RECENTLY REMOVED PACKAGES   =[" + removedPackages.size() + "]");

        Set<String> addedPackages = operate2StringLists(sPrevInstalledPackages, installedPackages, GET_2_MINUS_1_OR_ADDED);
        Log.v(TAG, "TOTAL NUMBER OF RECENTLY ADDED PACKAGES     =[" + addedPackages.size() + "]");

        // Prepare for next call:
        sSequenceNumber++;
        sPrevInstalledPackages = installedPackages;

        Log.d(TAG, "performJob: end");
    }

    public static List<String> getInstalledPackageNames(Context context) {
        //Log.d(TAG, "getInstalledPackageName");

        List<String> installedPackageNames = new ArrayList<>();

        try {
            PackageManager packageManager = context.getPackageManager();
            List<ApplicationInfo> appInfoList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo appInfo : appInfoList) {
                installedPackageNames.add(appInfo.packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Log.d(TAG, "getInstalledPackageName: end");

        return installedPackageNames;
    }

    public static Set<String> operate2StringLists(List<String> pkgList1, List<String> pkgList2, int operation) {
        Set<String> result = null;
        Set<String> pkgSet1 = new HashSet<String>(pkgList1);
        Set<String> pkgSet2 = new HashSet<String>(pkgList2);
        switch (operation) {
            case GET_1_MINUS_2_OR_REMOVED:
                pkgSet1.removeAll(pkgSet2);
                result = pkgSet1;
                break;
            case GET_2_MINUS_1_OR_ADDED:
                pkgSet2.removeAll(pkgSet1);
                result = pkgSet2;
                break;
            default:
                break;
        }
        return result;
    }
}
