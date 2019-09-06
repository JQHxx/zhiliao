package com.dev.rexhuang.zhiliao_core.callback;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

/**
 * *  created by RexHuang
 * *  on 2019/8/10
 */
public interface SwitchMediaFragmentListener {
    MediaBrowserCompat getMediaBrowser();

    void onMediaItemSelected(MediaBrowserCompat.MediaItem mediaItem);

    void onMetadataChanged(MediaMetadataCompat metadata);

    void onPlaybackStateChanged(PlaybackStateCompat compat);
}
