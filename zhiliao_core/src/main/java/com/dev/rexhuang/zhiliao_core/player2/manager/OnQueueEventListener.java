package com.dev.rexhuang.zhiliao_core.player2.manager;

import android.support.v4.media.session.MediaSessionCompat;

import java.util.List;

/**
 * 歌单监听
 * *  created by RexHuang
 * *  on 2019/9/19
 */
public interface OnQueueEventListener {
    /**
     * 歌单变化
     */
    void onQueueChanged(List<MediaSessionCompat.QueueItem> queue);

    /**
     * 歌单模式改变
     */
    void onRepeatModeChanged(int repeatMode);
}
