package com.dev.rexhuang.zhiliao_core.utils;

import android.app.Activity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * *  created by RexHuang
 * *  on 2019/8/5
 */
public class MediaIDHelper {

    // Media IDs used on browseable items of MediaBrowser
    public static final String MEDIA_ID_EMPTY_ROOT = "__EMPTY_ROOT__";
    public static final String MEDIA_ID_ROOT = "__ROOT__";
    public static final String MEDIA_ID_MUSICS_BY_GENRE = "__BY_GENRE__";
    public static final String MEDIA_ID_MUSICS_BY_SEARCH = "__BY_SEARCH__";

    private static final char CATEGORY_SEPARATOR = '/';
    private static final char LEAF_SEPARATOR = '|';

    public static String createMediaID(String musicID, String... categories) {
        StringBuilder sb = new StringBuilder();
        int length = categories.length;
        if (categories != null) {
            for (int i = 0; i < length; i++) {
                if (!isValidCategory(categories[i])) {
                    throw new IllegalArgumentException("Invalid category: " + categories[i]);
                }
                sb.append(categories[i]);
                if (i < length - 1) {
                    sb.append(CATEGORY_SEPARATOR);
                }
            }
        }
        if (musicID != null) {
            sb.append(LEAF_SEPARATOR).append(musicID);
        }
        return sb.toString();
    }

    private static boolean isValidCategory(String category) {
        return category == null ||
                (
                        category.indexOf(CATEGORY_SEPARATOR) < 0 &&
                                category.indexOf(LEAF_SEPARATOR) < 0
                );
    }

    public static String extractMusicIDFromMediaID(String mediaID) {
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            return mediaID.substring(pos + 1);
        }
        return null;
    }

    public static String[] getHierarchy(String mediaID) {
        int pos = mediaID.indexOf(LEAF_SEPARATOR);
        if (pos >= 0) {
            mediaID = mediaID.substring(0, pos);
        }
        return mediaID.split(String.valueOf(CATEGORY_SEPARATOR));
    }

    public static String extractBrowseCategoryValueFromMediaID(String mediaID) {
        String[] hierarchy = getHierarchy(mediaID);
        if (hierarchy.length == 2) {
            return hierarchy[1];
        }
        return null;
    }

    public static boolean isBrowseable(@NonNull String mediaID) {
        return mediaID.indexOf(LEAF_SEPARATOR) < 0;
    }

    public static String getParentMediaID(String mediaID) {
        String[] hierarchy = getHierarchy(mediaID);
        if (!isBrowseable(mediaID)) {
            return createMediaID(null, hierarchy);
        }
        if (hierarchy.length <= 1) {
            return MEDIA_ID_ROOT;
        }
        String[] parentHierarchy = Arrays.copyOf(hierarchy, hierarchy.length - 1);
        return createMediaID(null, parentHierarchy);
    }

    public static boolean isMediaItemPlaying(Activity context, MediaBrowserCompat.MediaItem mediaItem) {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(context);
        if (controller != null && controller.getMetadata() != null) {
            String currentPlayingMediaId = controller.getMetadata().getDescription()
                    .getMediaId();
            String itemMusicId = MediaIDHelper.extractMusicIDFromMediaID(
                    mediaItem.getDescription().getMediaId());
            if (currentPlayingMediaId != null
                    && TextUtils.equals(currentPlayingMediaId, itemMusicId)) {
                return true;
            }
        }
        return false;
    }
}
