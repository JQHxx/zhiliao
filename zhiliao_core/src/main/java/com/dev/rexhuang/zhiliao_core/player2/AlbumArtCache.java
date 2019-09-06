package com.dev.rexhuang.zhiliao_core.player2;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;

import com.dev.rexhuang.zhiliao_core.utils.BitmapHelper;
import com.orhanobut.logger.Logger;

/**
 * *  created by RexHuang
 * *  on 2019/8/7
 */
public final class AlbumArtCache {
    private static final String TAG = AlbumArtCache.class.getSimpleName();

    private static final int MAX_ALBUM_ART_CACHE_SIZE = 12 * 1024 * 1024;//12MB
    private static final int MAX_ART_WIDTH = 800;
    private static final int MAX_ART_HEIGHT = 480;

    private static final int MAX_ART_WIDTH_ICON = 128;
    private static final int MAX_ART_HEIGHT_ICON = 128;

    private static final int BIG_BITMAP_INDEX = 0;
    private static final int ICON_BITMAP_INDEX = 1;

    private final LruCache<String, Bitmap[]> mCache;

    private static final AlbumArtCache sInstance = new AlbumArtCache();

    public static AlbumArtCache getInstance() {
        return sInstance;
    }

    private AlbumArtCache() {
        int maxSize = Math.min(MAX_ALBUM_ART_CACHE_SIZE,
                (int) Math.min(Integer.MAX_VALUE, Runtime.getRuntime().maxMemory() / 4));
        mCache = new LruCache<String, Bitmap[]>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap[] value) {
                return value[BIG_BITMAP_INDEX].getByteCount()
                        + value[ICON_BITMAP_INDEX].getByteCount();
            }
        };
    }

    public Bitmap getBitImage(String artUrl) {
        Bitmap[] result = mCache.get(artUrl);
        return result == null ? null : result[BIG_BITMAP_INDEX];
    }

    public Bitmap getIconImage(String artUrl) {
        Bitmap[] result = mCache.get(artUrl);
        return result == null ? null : result[ICON_BITMAP_INDEX];
    }

    @SuppressLint("StaticFieldLeak")
    public void fetch(final String artUrl, final FetchListener listener) {
        Bitmap[] bitmap = mCache.get(artUrl);
        if (bitmap != null) {
            Logger.t(TAG).d("getOrFetch: album art is in cache, using it", artUrl);
            if (listener != null) {
                listener.onFetched(artUrl, bitmap[BIG_BITMAP_INDEX], bitmap[ICON_BITMAP_INDEX]);
            }
            return;
        }
        Logger.t(TAG).d("getOrFetch: starting asynctask to fetch ", artUrl);

        new AsyncTask<Void, Void, Bitmap[]>() {
            @Override
            protected Bitmap[] doInBackground(Void... voids) {
                Bitmap[] bitmaps;
                try {
                    Bitmap bitmap = BitmapHelper.fetchAndRescaleBitmap(artUrl,
                            MAX_ART_WIDTH, MAX_ART_HEIGHT);
                    Bitmap icon = BitmapHelper.scaleBitmap(bitmap,
                            MAX_ART_WIDTH_ICON, MAX_ART_HEIGHT_ICON);
                    bitmaps = new Bitmap[]{bitmap, icon};
                    mCache.put(artUrl, bitmaps);
                } catch (Exception e) {
                    return null;
                }
                Logger.t(TAG).d("doInBackground: putting bitmap in cache. cache size=" +
                        mCache.size());
                return bitmaps;
            }

            @Override
            protected void onPostExecute(Bitmap[] bitmaps) {
                if (bitmaps == null) {
                    listener.onError(artUrl, new IllegalArgumentException("got null bitmaps"));
                } else {
                    listener.onFetched(artUrl,
                            bitmaps[BIG_BITMAP_INDEX], bitmaps[ICON_BITMAP_INDEX]);
                }
            }
        }.execute();
    }

    public static abstract class FetchListener {
        public abstract void onFetched(String artUrl, Bitmap bigImage, Bitmap iconImage);

        public void onError(String artUrl, Exception e) {
            Logger.t(TAG).e(e, "AlbumArtFetchListener: error while downloading " + artUrl);
        }
    }
}

