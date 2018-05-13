package com.example.jonathan.payjoypackagemonitor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PackageUtils {
    public static final int GET_1_MINUS_2_OR_REMOVED = 0;
    public static final int GET_2_MINUS_1_OR_ADDED   = 1;

    // Get all the installed package names
    public static List<String> getInstalledPackageNames(Context context) {
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

        return installedPackageNames;
    }

    // Compare 2 string lists and return differences.
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
