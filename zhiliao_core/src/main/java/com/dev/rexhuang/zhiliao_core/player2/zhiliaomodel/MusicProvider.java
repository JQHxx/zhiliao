package com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel;

import android.graphics.Bitmap;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 提供音乐，最终通过QueueHelper转换为播放列表
 * *  created by RexHuang
 * *  on 2019/9/4
 */
public class MusicProvider {
    private LinkedHashMap<String, MusicEntity> mMusicEntityListById;
    private LinkedHashMap<String, MediaMetadataCompat> mMusicListById;

    public static MusicProvider getInstance() {
        return MusicProviderHolder.INSTANCE;
    }

    private static class MusicProviderHolder {
        private static final MusicProvider INSTANCE = new MusicProvider();
    }

    private MusicProvider() {
        mMusicEntityListById = new LinkedHashMap<>();
        mMusicListById = new LinkedHashMap<>();
    }

    public List<MusicEntity> getMusicEntityList() {
        return new ArrayList<>(mMusicEntityListById.values());
    }

    /**
     * 设置播放列表
     */
    public synchronized void setMusicList(List<MusicEntity> musicEntities) {
        mMusicEntityListById.clear();
        for (MusicEntity musicEntity : musicEntities) {
            mMusicEntityListById.put(musicEntity.getId(), musicEntity);
        }
        mMusicListById = covertToMediaMetadata(musicEntities);
    }

    /**
     * 添加一首歌
     */
    public synchronized void addMusicEntity(MusicEntity musicEntity) {
        mMusicEntityListById.put(musicEntity.getId(), musicEntity);
        mMusicListById.put(musicEntity.getId(), convertToMediaMetadata(musicEntity));
    }

    /**
     * 根据id检查是否有这首歌
     */
    public boolean hasMusicEntity(String musicId) {
        return mMusicEntityListById.containsKey(musicId);
    }

    /**
     * 根据musicId获取MusicEntity
     */
    public MusicEntity getMusicEntity(String musicId) {
        return hasMusicEntity(musicId) ? mMusicEntityListById.get(musicId) : null;
    }

    /**
     * 根据musicId获取索引
     */
    public int getIndexByMusicEntity(String musicId) {
        MusicEntity musicEntity = getMusicEntity(musicId);
        return musicEntity != null ? getMusicEntityList().indexOf(musicEntity) : -1;
    }

    /**
     * 获取List#MediaMetadataCompat
     */
    public List<MediaMetadataCompat> getMusicList() {
        return new ArrayList<>(mMusicListById.values());
    }

    /**
     * 获取乱序列表
     */
    public Iterable<MediaMetadataCompat> getShuffledMusic() {
        List<MediaMetadataCompat> shuffled = new ArrayList<>(mMusicListById.size());
        shuffled.addAll(mMusicListById.values());
        Collections.shuffle(shuffled);
        return shuffled;
    }

    /**
     * 根据id获取对应的MediaMetadataCompat对象
     */
    public MediaMetadataCompat getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId) : null;
    }

    /**
     * 更新封面art
     */
    public synchronized void updateMusicArt(String musicId, MediaMetadataCompat changeData, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = new MediaMetadataCompat.Builder(changeData)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)
                .build();
        mMusicListById.put(musicId, metadata);
    }

    private synchronized static LinkedHashMap<String, MediaMetadataCompat> covertToMediaMetadata(List<MusicEntity> musicEntities) {
        LinkedHashMap<String, MediaMetadataCompat> map = new LinkedHashMap<>();
        for (MusicEntity musicEntity : musicEntities) {
            MediaMetadataCompat metadataCompat = convertToMediaMetadata(musicEntity);
            map.put(musicEntity.getId(), metadataCompat);
        }
        return map;
    }

    private synchronized static MediaMetadataCompat convertToMediaMetadata(MusicEntity musicEntity) {
        String albumTitle = !TextUtils.isEmpty(musicEntity.getName()) ? musicEntity.getName() : "";
        String songCover = !TextUtils.isEmpty(musicEntity.getCover()) ? musicEntity.getCover() : "";
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, musicEntity.getId())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, musicEntity.getNormal());
        if (!TextUtils.isEmpty(albumTitle)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumTitle);
        }
        if (!TextUtils.isEmpty(musicEntity.getSingers().get(0).getName())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, musicEntity.getSingers().get(0).getName());
        }
        if (!TextUtils.isEmpty(songCover)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, songCover);
        }
        if (!TextUtils.isEmpty(musicEntity.getName())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, musicEntity.getName());
        }
        return builder.build();
    }
}
