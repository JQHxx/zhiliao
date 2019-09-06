package com.dev.rexhuang.zhiliao_core.player2.zhiliaoplayback;

import android.support.v4.media.session.MediaSessionCompat.QueueItem;

/**
 * *  created by RexHuang
 * *  on 2019/9/4
 */
public interface Playback {
    void start();

    void stop(boolean notifyListeners);

    void setState(int state);

    int getState();

    boolean isConnected();

    boolean isPlaying();

    long getCurrentStreamPosition();

    long getBufferedPosition();

    long getDuration();

    void updateLastKnownStreamPosition();

    void play(QueueItem item, boolean isPlayWhenReady);

    void pause();

    void seekTo(long position);

    void setCurrentMediaId(String mediaId);

    String getCurrentMediaId();

    void onFastForward();

    void onRewind();

    void setVolume(float audioVolume);

    float getVolume();

    void onDerailleur(boolean refer, float multiple);

    interface Callback {
        void onCompletion();

        void onPlaybackStatusChanged(int state);

        void onError(String error);

        void setCurrentMediaId(String mediaId);
    }

    void setCallback(Callback callback);
}
