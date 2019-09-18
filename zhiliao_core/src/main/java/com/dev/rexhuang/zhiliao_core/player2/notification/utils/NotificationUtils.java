package com.dev.rexhuang.zhiliao_core.player2.notification.utils;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.MusicService;
import com.dev.rexhuang.zhiliao_core.player2.notification.NotificationConstructor;
import com.dev.rexhuang.zhiliao_core.player2.notification.factory.INotification;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;

import java.util.List;


/**
 * 通知栏工具类，主要提供一些公共的方法
 */
public class NotificationUtils {
    /**
     * 得到目标界面 Class
     */
    public static Class getTargetClass(String targetClass) {
        Class clazz = null;
        try {
            if (!TextUtils.isEmpty(targetClass)) {
                clazz = Class.forName(targetClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    /**
     * 设置content点击事件
     */
    public static PendingIntent createContentIntent(MusicService mService, NotificationConstructor mBuilder,
                                                    String musicId, Bundle bundle, Class targetClass) {
        MusicEntity musicEntity = null;
        List<MusicEntity> musicEntities = MusicProvider.getInstance().getMusicEntityList();
        for (MusicEntity info : musicEntities) {
            if (info.getId().equals(musicId)) {
                musicEntity = info;
                break;
            }
        }
        Intent openUI = new Intent(mService, targetClass);
        openUI.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        openUI.putExtra("notification_entry", INotification.ACTION_INTENT_CLICK);
        if (musicEntity != null) {
            openUI.putExtra("songInfo", musicEntity);
        }
        if (bundle != null) {
            openUI.putExtra("bundleInfo", bundle);
        }
        @SuppressLint("WrongConstant")
        PendingIntent pendingIntent;
        switch (mBuilder.getPendingIntentMode()) {
            case NotificationConstructor.MODE_ACTIVITY:
                pendingIntent = PendingIntent.getActivity(mService, INotification.REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
                break;
            case NotificationConstructor.MODE_BROADCAST:
                pendingIntent = PendingIntent.getBroadcast(mService, INotification.REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
                break;
            case NotificationConstructor.MODE_SERVICE:
                pendingIntent = PendingIntent.getService(mService, INotification.REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
                break;
            default:
                pendingIntent = PendingIntent.getActivity(mService, INotification.REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT);
                break;
        }
        return pendingIntent;
    }

    /**
     * 兼容8.0
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createNotificationChannel(MusicService mService, NotificationManager mNotificationManager) {
        if (mNotificationManager.getNotificationChannel(INotification.CHANNEL_ID) == null) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(INotification.CHANNEL_ID, mService.getString(R.string.notification_channel),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(mService.getString(R.string.notification_channel_description));

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
