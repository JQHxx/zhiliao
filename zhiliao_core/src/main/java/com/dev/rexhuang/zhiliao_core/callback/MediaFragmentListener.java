package com.dev.rexhuang.zhiliao_core.callback;

import android.support.v4.media.MediaBrowserCompat;

/**
 * *  created by RexHuang
 * *  on 2019/8/10
 */
public interface MediaFragmentListener {
    MediaBrowserCompat getMediaBrowser();

    void onMediaItemSelected(MediaBrowserCompat.MediaItem mediaItem);

    void onPlayAction(String playActions);
}
