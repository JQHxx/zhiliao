package com.dev.rexhuang.zhiliao;

import android.app.Application;

import com.dev.rexhuang.zhiliao.login.UserManager;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.notification.NotificationConstructor;
import com.dev.rexhuang.zhiliao_core.player2.playback.download.ExoDownload;

import me.yokeyword.fragmentation.Fragmentation;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
@SuppressWarnings("SpellCheckingInspection")
public class ZhiliaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Zhiliao.getConfigurator()
                .withAppContext(this)
                .withApiHost("https://engine.mebtte.com/")
                .withLogTag("Zhiliao_Debug")
                .config();

        //测试用
        Fragmentation.builder()
                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
                .stackViewMode(Fragmentation.BUBBLE)
                .debug(BuildConfig.DEBUG)
                .install();

        //StarrSky
        MusicManager.initMusicManager(this);
        NotificationConstructor notificationConstructor = new NotificationConstructor.Builder()
                .setCreateSystemNotification(true)
                .bulid();
        MusicManager.getInstance().setNotificationConstructor(notificationConstructor);
        ExoDownload.initExoDownload(this);
        ExoDownload.getInstance().setOpenCache(true);

        //用户注册登录
        UserManager.init(this);
    }

}
