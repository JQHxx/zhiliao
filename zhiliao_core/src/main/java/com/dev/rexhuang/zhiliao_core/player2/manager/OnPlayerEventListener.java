package com.dev.rexhuang.zhiliao_core.player2.manager;

import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;

/**
 * 播放监听
 */
public interface OnPlayerEventListener {
    /**
     * 切歌回调
     */
    void onMusicSwitch(MusicEntity musicEntity);

    /**
     * 开始播放,与 onMusicSwitch 的关系是先回调 onMusicSwitch，再回调 onPlayerStart
     */
    void onPlayerStart();

    /**
     * 暂停播放
     */
    void onPlayerPause();

    /**
     * 停止播放
     */
    void onPlayerStop();

    /**
     * 播放完成
     */
    void onPlayCompletion(MusicEntity musicEntity);

    /**
     * 正在缓冲
     */
    void onBuffering();

    /**
     * 发生错误
     */
    void onError(int errorCode, String errorMsg);
}
