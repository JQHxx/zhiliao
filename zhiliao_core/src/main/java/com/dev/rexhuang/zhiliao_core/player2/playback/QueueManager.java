package com.dev.rexhuang.zhiliao_core.player2.playback;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.player2.AlbumArtCache;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.dev.rexhuang.zhiliao_core.utils.MediaIDHelper;
import com.dev.rexhuang.zhiliao_core.utils.QueueHelper;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * *  created by RexHuang
 * *  on 2019/8/5
 */
public class QueueManager {
    private static final String TAG = QueueManager.class.getSimpleName();
    //顺序播放：0，随机播放：1，列表循环：2
    public static final int PLAY_MODE_LOOP = 0;
    public static final int PLAY_MODE_RANDOM = 1;
    public static final int PLAY_MODE_REPEAT = 2;
    private int mPlayMode = PLAY_MODE_LOOP;
    private int CMD_NEXT = 0;
    private int CMD_PRE = 1;

    private MusicProvider mMusicProvider;

//    private MetaDataUpdateListener mListener;

    private Resources mResources;

    private static QueueManager instance;
    private List<MediaBrowserCompat.MediaItem> mPlayingQueue2;
    private List<MediaSessionCompat.QueueItem> mPlayingQueue;
    private int mCurrentIndex;
    private MediaBrowserCompat.MediaItem mCurrentItem;
    private List<MediaBrowserCompat.MediaItem> mSavedQueue;
    private MediaDataUpdateListener mediaDataUpdateListener;
    private int queueIndex;

    void updateQueueIndex(){
        queueIndex = mPlayingQueue.size() -1;
    }

    public QueueManager setMediaDataUpdateListener(MediaDataUpdateListener mediaDataUpdateListener) {
        this.mediaDataUpdateListener = mediaDataUpdateListener;
        return instance;
    }

    public QueueManager() {
        Logger.t(TAG).d(TAG + " Initial");
        mSavedQueue = Collections.synchronizedList(new ArrayList<>());
        mPlayingQueue = Collections.synchronizedList(new ArrayList<>());
        mPlayingQueue2 = Collections.synchronizedList(new ArrayList<>());
        mCurrentIndex = 0;
    }

    public synchronized static QueueManager getInstance() {
        if (instance == null) {
            instance = new QueueManager();
        }
        return instance;
    }

    private boolean contains(MediaBrowserCompat.MediaItem mediaItem) {
        return mPlayingQueue2.contains(mediaItem);
    }

    /**
     * 增加歌曲到歌单
     *
     * @param mediaItem 歌曲
     */
    public void addToPlayingQueue(MediaBrowserCompat.MediaItem mediaItem) {
        if (mPlayingQueue2 == null) {
            mPlayingQueue2 = Collections.synchronizedList(new ArrayList<>());
        }

        if (!contains(mediaItem)) {
            Logger.t(TAG).d(TAG + " addToPlayingQueue " + mediaItem.getDescription().getTitle());
            mPlayingQueue2.add(mediaItem);
        }
    }

    /**
     * 删除歌单中的歌曲,推荐使用 @link{removeFromPlayingQueue(MediaBrowserCompat.MediaItem mediaItem))}
     *
     * @param index 歌曲索引
     * @return true为删除成功, false为删除失败
     */
    private boolean removeFromPlayingQueue(int index) {
        return removeItem(index);
    }

    /**
     * 删除歌单中的歌曲
     *
     * @param mediaItem 歌曲
     * @return true 删除成功, false 删除失败
     */
    public boolean removeFromPlayingQueue(MediaBrowserCompat.MediaItem mediaItem) {
        return removeItem(mediaItem);
    }

    private boolean removeItem(int index) {
        if (mPlayingQueue2.get(index) != null) {
            mPlayingQueue2.remove(index);
            return true;
        }
        return false;
    }

    private boolean removeItem(MediaBrowserCompat.MediaItem mediaItem) {
        if (contains(mediaItem)) {
            mPlayingQueue2.remove(mediaItem);
            return true;
        }
        return false;
    }

    /**
     * 获得当前播放歌单
     *
     * @return 播放歌单
     */
    public List<MediaBrowserCompat.MediaItem> getPlayingQueue() {
        return mPlayingQueue2;
    }

    /**
     * 获得当前播放歌曲
     *
     * @return 播放歌曲
     */
    public MediaBrowserCompat.MediaItem getCurrentItem() {
        return mCurrentItem;
    }

    /**
     * 获得当前播放歌曲的索引
     *
     * @return 播放歌曲的索引
     */
    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * 切换播放的歌曲
     *
     * @param mediaItem 要切换的歌曲
     */
    public void setCurrentItem(MediaBrowserCompat.MediaItem mediaItem) {
        synchronized (this) {
            if (!contains(mediaItem)) {
                addToPlayingQueue(mediaItem);
            }
            mCurrentItem = mediaItem;
            mCurrentIndex = mPlayingQueue2.indexOf(mCurrentItem);
        }
    }

    /**
     * 设置播放模式
     */
    public void setPlayMode(int mode) {
        if (mode == PLAY_MODE_LOOP || mode == PLAY_MODE_RANDOM || mode == PLAY_MODE_REPEAT) {
            if (mode == PLAY_MODE_RANDOM) {
                mSavedQueue.clear();
            }
            mPlayMode = mode;
        } else {
            Logger.t(TAG).d(TAG + " setPlayMode invalid mode");
        }
    }

    /**
     * 切换播放的歌曲
     *
     * @param index 索引
     */
    private void setCurrentItem(int index, int cmd) {
        if (index < 0 || index > mPlayingQueue2.size() - 1) {
            Logger.t(TAG).d(TAG + " setCurrentItem invalid index :" + index);
        } else {
            if (getPlayMode() == PLAY_MODE_RANDOM && mCurrentItem != null) {
                if (cmd == CMD_NEXT) {
                    mSavedQueue.add(mCurrentItem);
                } else if (cmd == CMD_PRE && mSavedQueue.size() > 0) {
                    mSavedQueue.remove(mSavedQueue.size() - 1);
                }
            }
            mCurrentItem = mPlayingQueue2.get(index);
            mCurrentIndex = mPlayingQueue2.indexOf(mCurrentItem);
        }
        if (mediaDataUpdateListener != null) {
//            MediaMetadataCompat metadataCompat = new MediaMetadataCompat();
//            mediaDataUpdateListener.onMediadataChange(getCurrentItem().getDescription());
        }
    }

    /**
     * 播放选中的mediaItem
     *
     * @param mediaItem 要播放的mediaItem
     */
    public void switchToNew(MediaBrowserCompat.MediaItem mediaItem) {
        if (!contains(mediaItem)) {
            addToPlayingQueue(mediaItem);
        }
        setCurrentItem(mediaItem);
    }

    /**
     * 播放下一首
     */
    public void next() {
        synchronized (this) {
            int nextIndex = getNextIndex();
            Logger.t(TAG).d(TAG + " nextIndex = " + nextIndex);
            if (getPlayMode() == PLAY_MODE_LOOP && nextIndex == -1) {
                Logger.t(TAG).d(TAG + " 顺序播放到底了");
            } else {
                setCurrentItem(nextIndex, CMD_NEXT);
            }
        }
    }

    /**
     * 播放上一首
     */
    public void previous() {
        synchronized (this) {
            int previousIndex = getPreviousIndex();
            Logger.t(TAG).d(TAG + " previousIndex = " + previousIndex);
            if (getPlayMode() == PLAY_MODE_LOOP && previousIndex == -1) {
                Logger.t(TAG).d(TAG + " 顺序播放到顶了");
            } else {
                setCurrentItem(previousIndex, CMD_PRE);
            }
        }
    }

    /**
     * 根据歌单的播放模式,获取上一首歌曲
     *
     * @return 上一首歌曲在歌单的索引
     */
    private int getPreviousIndex() {
        switch (getPlayMode()) {
            case PLAY_MODE_LOOP:
                if (mCurrentIndex - 1 >= 0) {
                    return mCurrentIndex - 1;
                }
                return -1;
            case PLAY_MODE_RANDOM:
                if (mSavedQueue.size() > 0) {
                    return mPlayingQueue2.indexOf(mSavedQueue.get(mSavedQueue.size() - 1));
                }
            case PLAY_MODE_REPEAT:
                if (mCurrentIndex - 1 >= 0) {
                    return mCurrentIndex - 1;
                } else {
                    return mPlayingQueue2.size() - 1;
                }
        }
        return 0;
    }

    /**
     * 根据歌单的播放模式，获取要播放的下一首歌
     *
     * @return 下一首歌曲在歌单的索引
     */
    public int getNextIndex() {
        switch (getPlayMode()) {
            case PLAY_MODE_LOOP:
                if (mCurrentIndex + 1 <= mPlayingQueue2.size() - 1) {
                    return mCurrentIndex + 1;
                }
                return -1;
            case PLAY_MODE_RANDOM:
                return getRandomIndex(mPlayingQueue2.size(), mCurrentIndex);
            case PLAY_MODE_REPEAT:
                if (mCurrentIndex + 1 <= mPlayingQueue2.size() - 1) {
                    return mCurrentIndex + 1;
                } else {
                    return 0;
                }
            default:
                return 0;
        }
    }

    public int getPlayMode() {
        return mPlayMode;
    }

    public int getRandomIndex(int bound, int currentIndex) {
        int index = new Random().nextInt(bound);
        if (index != currentIndex) {
            return index;
        }
        return getRandomIndex(bound, currentIndex);
    }

    public interface MediaDataUpdateListener {
        void onMediadataChange(MediaMetadataCompat metadata);

        void onMetadataRetrieveError();

        void onCurrentQueueIndexUpdated(int queueIndex);

        void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> newQueue);
    }

    //old method 暂时屏蔽

//    public QueueManager(MusicProvider musicProvider, Resources resources, MetaDataUpdateListener listener) {
//        this.mMusicProvider = musicProvider;
//        this.mListener = listener;
//        this.mResources = resources;
//
//        mPlayingQueue = Collections.synchronizedList(new ArrayList<>());
//        mCurrentIndex = 0;
//    }
//
//    public boolean isSameBrowsingCategory(String mediaId) {
//        String[] newBrowseHierarchy = MediaIDHelper.getHierarchy(mediaId);
//        MediaSessionCompat.QueueItem current = getCurrentMusic();
//        if (current == null) {
//            return false;
//        }
//        String[] currentBrowseHierarchy = MediaIDHelper.getHierarchy(current.getDescription().getMediaId());
//        return Arrays.equals(newBrowseHierarchy, currentBrowseHierarchy);
//    }
//
//    private void setCurrentQueueIndex(int index) {
//        if (index >= 0 && index < mPlayingQueue.size()) {
//            mCurrentIndex = index;
//            mListener.onCurrentQueueIndexUpdated(mCurrentIndex);
//        }
//    }
//
//    public boolean setCurrentQueueItem(long queueId) {
//        int index = QueueHelper.getMusicIndexOnQueue(mPlayingQueue, queueId);
//        setCurrentQueueIndex(index);
//        return index >= 0;
//    }
//
//    public boolean setCurrentQueueItem(String mediaId) {
//        int index = QueueHelper.getMusicIndexOnQueue(mPlayingQueue, mediaId);
//        setCurrentQueueIndex(index);
//        return index >= 0;
//    }
//
//    public boolean skipQueuePosition(int amount) {
//        int index = mCurrentIndex + amount;
//        if (index < 0) {
//            index = 0;
//        } else {
//            index %= mPlayingQueue.size();
//        }
//        if (!QueueHelper.isIndexPlayable(index, mPlayingQueue)) {
//            Logger.t(TAG).d("Cannot increment queue index by ", amount,
//                    ". Current=", mCurrentIndex, " queue length=", mPlayingQueue.size());
//            return false;
//        }
//        mCurrentIndex = index;
//        return true;
//    }
//
//    public boolean setQueueFromSearch(String query, Bundle extras) {
//        List<MediaSessionCompat.QueueItem> queue =
//                QueueHelper.getPlayingQueueFromSearch(query, extras, mMusicProvider);
//        setCurrentQueue(mResources.getString(R.string.search_queue_title), queue);
//        updateMetadata();
//        return queue != null && !queue.isEmpty();
//    }
//
//    public void setRandomQueue() {
//        setCurrentQueue(mResources.getString(R.string.random_queue_title),
//                QueueHelper.getRandomQueue(mMusicProvider));
//        updateMetadata();
//    }
//
//    public void setQueueFromMusic(String mediaId) {
//        Logger.t(TAG).d("setQueueFromMusic", mediaId);
//        boolean canReuseQueue = false;
//        if (isSameBrowsingCategory(mediaId)) {
//            canReuseQueue = setCurrentQueueItem(mediaId);
//        }
//        if (!canReuseQueue) {
//            String queueTitle = mResources.getString(R.string.browse_musics_by_genre_subtitle,
//                    MediaIDHelper.extractBrowseCategoryValueFromMediaID(mediaId));
//            setCurrentQueue(queueTitle,
//                    QueueHelper.getPlayingQueue(mediaId, mMusicProvider), mediaId);
//        }
//        updateMetadata();
//    }
//
//    public MediaSessionCompat.QueueItem getCurrentMusic() {
//        if (!QueueHelper.isIndexPlayable(mCurrentIndex, mPlayingQueue)) {
//            return null;
//        }
//        return mPlayingQueue.get(mCurrentIndex);
//    }
//
//    public int getCurrentQueueSize() {
//        if (mPlayingQueue == null) {
//            return 0;
//        }
//        return mPlayingQueue.size();
//    }
//
//    protected void setCurrentQueue(String title, List<MediaSessionCompat.QueueItem> newQueue) {
//        setCurrentQueue(title, newQueue, null);
//    }
//
//    protected void setCurrentQueue(String title, List<MediaSessionCompat.QueueItem> newQueue, String initialMediaId) {
//        mPlayingQueue = newQueue;
//        int index = 0;
//        if (initialMediaId != null) {
//            index = QueueHelper.getMusicIndexOnQueue(mPlayingQueue, initialMediaId);
//        }
//        mCurrentIndex = Math.max(index, 0);
//        mListener.onQueueUpdated(title, newQueue);
//    }
//
//    public void updateMetadata() {
//        MediaSessionCompat.QueueItem currentMusic = getCurrentMusic();
//        if (currentMusic == null) {
//            mListener.onMetadataRetrieveError();
//            return;
//        }
//        final String musicId = MediaIDHelper.extractMusicIDFromMediaID(
//                currentMusic.getDescription().getMediaId());
//        MediaMetadataCompat metadata = mMusicProvider.getMusic(musicId);
//        if (metadata == null) {
//            throw new IllegalArgumentException("Invalid musicId " + musicId);
//        }
//        mListener.onMetadataChanged(metadata);
//
//        if (metadata.getDescription().getIconBitmap() == null &&
//                metadata.getDescription().getIconUri() != null) {
//            String albumUri = metadata.getDescription().getIconUri().toString();
//            AlbumArtCache.getInstance().fetch(albumUri, new AlbumArtCache.FetchListener() {
//                @Override
//                public void onFetched(String artUrl, Bitmap bigImage, Bitmap iconImage) {
//                    mMusicProvider.updateMusicArt(musicId, bigImage, iconImage);
//
//                    MediaSessionCompat.QueueItem currentMusic = getCurrentMusic();
//                    if (currentMusic == null) {
//                        return;
//                    }
//                    String currentPlayingId = MediaIDHelper.extractMusicIDFromMediaID(
//                            currentMusic.getDescription().getMediaId()
//                    );
//                    if (musicId.equals(currentPlayingId)) {
//                        mListener.onMetadataChanged(mMusicProvider.getMusic(currentPlayingId));
//                    }
//                }
//            });
//        }
//    }
//
//    public interface MetaDataUpdateListener {
//        void onMetadataChanged(MediaMetadataCompat metadata);
//
//        void onMetadataRetrieveError();
//
//        void onCurrentQueueIndexUpdated(int queueIndex);
//
//        void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> newQueue);
//    }
}
