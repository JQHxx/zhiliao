package com.dev.rexhuang.zhiliao_core.player2.model;

import android.support.v4.media.MediaMetadataCompat;

import java.util.Iterator;

/**
 * *  created by RexHuang
 * *  on 2019/8/5
 */
public interface MusicProviderSource {
    String CUSTOM_METADATA_TRACK_SOURCE = "_SOURCE_";

    Iterator<MediaMetadataCompat> iterator();
}
