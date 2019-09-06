package com.dev.rexhuang.zhiliao_core.player2.model;

import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import androidx.annotation.Nullable;

/**
 * *  created by RexHuang
 * *  on 2019/8/5
 */
public class MutableMediaMetadata {

    public MediaMetadataCompat metadata;
    public final String trackId;


    public MutableMediaMetadata(String trackId, MediaMetadataCompat metadata) {
        this.trackId = trackId;
        this.metadata = metadata;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || obj.getClass() != MutableMediaMetadata.class) {
            return false;
        }

        MutableMediaMetadata that = (MutableMediaMetadata) obj;
        return TextUtils.equals(trackId, that.trackId);
    }


    @Override
    public int hashCode() {
        return trackId.hashCode();
    }
}
