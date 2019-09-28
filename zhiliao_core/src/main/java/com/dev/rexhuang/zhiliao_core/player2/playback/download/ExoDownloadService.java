package com.dev.rexhuang.zhiliao_core.player2.playback.download;

import android.app.Notification;

import androidx.annotation.Nullable;

import com.dev.rexhuang.zhiliao_core.R;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.PlatformScheduler;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationUtil;
import com.google.android.exoplayer2.util.Util;

/**
 * 媒体下载服务
 * *  created by RexHuang
 * *  on 2019/9/5
 */
public class ExoDownloadService extends DownloadService {

    private static final String CHANNEL_ID = "download_channel";
    private static final int JOB_ID = 1;
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    /**
     * 传入FOREGROUND_NOTIFICATION_ID，是因为这样服务位于前台需要通知，并且要求服务位于前台以确保进程不会被终止
     * 如果使用FOREGROUND_NOTIFICATION_ID_NONE，则服务可能会被后台杀死
     */
    public ExoDownloadService() {
        //传入FOREGROUND_NOTIFICATION_ID_NONE，则下载时不会出现通知栏，如果想要通知栏，则传入FOREGROUND_NOTIFICATION_ID
        super(ExoDownload.getInstance().isShowNotificationWhenDownload() ?
                        FOREGROUND_NOTIFICATION_ID :
                        FOREGROUND_NOTIFICATION_ID_NONE,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                CHANNEL_ID,
                R.string.exo_download_notification_channel_name);
    }

    @Override
    protected DownloadManager getDownloadManager() {
        return ExoDownload.getInstance().getDownloadManager();
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return Util.SDK_INT >= 21 ? new PlatformScheduler(this, JOB_ID) : null;
    }

    @Override
    protected Notification getForegroundNotification(DownloadManager.TaskState[] taskStates) {
        return DownloadNotificationUtil.buildProgressNotification(this, R.drawable.exo_controls_play,
                CHANNEL_ID, null, null, taskStates);
    }
}
