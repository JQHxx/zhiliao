package com.dev.rexhuang.zhiliao_core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;

/**
 * *  created by RexHuang
 * *  on 2019/9/5
 */
public class Utils {
    public static String getUserAgent(Context context, String applicationName) {
        String versionName;
        try {
            String packageName = context.getPackageName();
            PackageInfo info = null;
            info = context.getPackageManager().getPackageInfo(packageName, 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "?";
        }
        return applicationName + "/" + versionName + " (Linux;Android " + Build.VERSION.RELEASE
                + ") " + ExoPlayerLibraryInfo.VERSION_SLASHY;

    }
}
