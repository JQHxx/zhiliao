package com.dev.rexhuang.zhiliao_core.player2.model;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.widget.Toast;

import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.net.RestClient;
import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.utils.MediaIDHelper;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import okhttp3.ResponseBody;

import static com.dev.rexhuang.zhiliao_core.utils.MediaIDHelper.MEDIA_ID_MUSICS_BY_GENRE;
import static com.dev.rexhuang.zhiliao_core.utils.MediaIDHelper.MEDIA_ID_ROOT;
import static com.dev.rexhuang.zhiliao_core.utils.MediaIDHelper.createMediaID;

/**
 * *  created by RexHuang
 * *  on 2019/8/5
 */
public class MusicProvider {

    private static final String TAG = MusicProvider.class.getSimpleName();

    private MusicProviderSource mSource;
    private OnMusicData onMusicData;

    //Categorized caches for music track data:
    private ConcurrentMap<String, List<MediaMetadataCompat>> mMusicListByGenre;
    private final ConcurrentMap<String, MutableMediaMetadata> mMusicListById;

    private final Set<String> mFavoriteTracks;

    enum State {
        NON_INITIALIZED, INITIALIZING, INITIALIZED
    }

    private volatile State mCurrentState = State.NON_INITIALIZED;

    public interface Callback {
        void onMusicCatalogReady(boolean success);
    }

    public MusicProvider() {
        this(new RemoteJSONSource());
    }

    public MusicProvider(MusicProviderSource source) {
        mSource = source;
        mMusicListByGenre = new ConcurrentHashMap<>();
        mMusicListById = new ConcurrentHashMap<>();
        mFavoriteTracks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public Iterable<String> getGenres() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        return mMusicListByGenre.keySet();
    }

    public Iterable<MediaMetadataCompat> getShuffledMusic() {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        List<MediaMetadataCompat> shuffled = new ArrayList<>(mMusicListById.size());
        for (MutableMediaMetadata mutableMediaMetadata : mMusicListById.values()) {
            shuffled.add(mutableMediaMetadata.metadata);
        }
        Collections.shuffle(shuffled);
        return shuffled;
    }

    public List<MediaMetadataCompat> getMusicByGenre(String genre) {
        if (mCurrentState != State.INITIALIZED || !mMusicListByGenre.containsKey(genre)) {
            return Collections.emptyList();
        }
        return mMusicListByGenre.get(genre);
    }

    public List<MediaMetadataCompat> searchMusicBySongTitle(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_TITLE, query);
    }

    public List<MediaMetadataCompat> searchMusicByAlbum(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ALBUM, query);
    }

    public List<MediaMetadataCompat> searchMusicByArtist(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_ARTIST, query);
    }

    public List<MediaMetadataCompat> searchMusicByGenre(String query) {
        return searchMusic(MediaMetadataCompat.METADATA_KEY_GENRE, query);
    }

    private List<MediaMetadataCompat> searchMusic(String metadataField, String query) {
        if (mCurrentState != State.INITIALIZED) {
            return Collections.emptyList();
        }
        ArrayList<MediaMetadataCompat> result = new ArrayList<>();
        query = query.toLowerCase(Locale.US);
        for (MutableMediaMetadata track : mMusicListById.values()) {
            if (track.metadata.getString(metadataField).toLowerCase(Locale.US)
                    .contains(query)) {
                result.add(track.metadata);
            }
        }
        return result;
    }

    public MediaMetadataCompat getMusic(String musicId) {
        return mMusicListById.containsKey(musicId) ? mMusicListById.get(musicId).metadata : null;
    }

    public synchronized void updateMusicArt(String musicId, Bitmap albumArt, Bitmap icon) {
        MediaMetadataCompat metadata = getMusic(musicId);
        metadata = new MediaMetadataCompat.Builder(metadata)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, icon)
                .build();

        MutableMediaMetadata mutableMediaMetadata = mMusicListById.get(musicId);
        if (mutableMediaMetadata == null) {
            throw new IllegalStateException("Unexpected error: Inconsistent data structures in " +
                    "MusicProvider");
        }

        mutableMediaMetadata.metadata = metadata;
    }

    public void setFavorite(String musicId, boolean favorite) {
        if (favorite) {
            mFavoriteTracks.add(musicId);
        } else {
            mFavoriteTracks.remove(musicId);
        }
    }

    public boolean isInitialized() {
        return mCurrentState == State.INITIALIZED;
    }

    public boolean isFavorite(String musicId) {
        return mFavoriteTracks.contains(musicId);
    }

    @SuppressLint("StaticFieldLeak")
    public void retrieveMediaAsync(final Callback callback) {
        Logger.t(TAG).d("retrieveMediaAsync called");
        if (mCurrentState == State.INITIALIZED) {
            if (callback != null) {
                // Nothing to do, execute callback immediately
                callback.onMusicCatalogReady(true);
            }
        } else {
            new AsyncTask<Void, Void, State>() {

                @Override
                protected State doInBackground(Void... voids) {
                    retrieveMedia();
                    return mCurrentState;
                }

                @Override
                protected void onPostExecute(State current) {
                    if (callback != null) {
                        callback.onMusicCatalogReady(current == State.INITIALIZED);
                    }
                }
            }.execute();
        }
    }

    private synchronized void builListByGenre() {
        ConcurrentMap<String, List<MediaMetadataCompat>> newMusicListByGenre = new ConcurrentHashMap<>();

        for (MutableMediaMetadata m : mMusicListById.values()) {
            String genre = m.metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
            List<MediaMetadataCompat> list = newMusicListByGenre.get(genre);
            if (list == null) {
                list = new ArrayList<>();
                newMusicListByGenre.put(genre, list);
            }
            list.add(m.metadata);
        }
        mMusicListByGenre = newMusicListByGenre;
    }

    private synchronized void retrieveMedia() {
        try {
            if (mCurrentState == State.NON_INITIALIZED) {
                mCurrentState = State.INITIALIZING;
                Iterator<MediaMetadataCompat> tracks = mSource.iterator();
                while (tracks.hasNext()) {
                    MediaMetadataCompat item = tracks.next();
                    String musicId = item.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    mMusicListById.put(musicId, new MutableMediaMetadata(musicId, item));
                }
                builListByGenre();
                mCurrentState = State.INITIALIZED;
            }
        } finally {
            if (mCurrentState != State.INITIALIZED) {
                mCurrentState = State.NON_INITIALIZED;
            }
        }
    }

    public List<MediaBrowserCompat.MediaItem> getChildren(String mediaId, Resources resources) {
        List<MediaBrowserCompat.MediaItem> mediaItems = new ArrayList<>();

        if (!MediaIDHelper.isBrowseable(mediaId)) {
            return mediaItems;

        }

        if (MEDIA_ID_ROOT.equals(mediaId)) {
            mediaItems.add(createBrowsableMediaItemForRoot(resources));
        } else if (MEDIA_ID_MUSICS_BY_GENRE.equals(mediaId)) {
            for (String genre : getGenres()) {
                mediaItems.add(createBrowsableMediaItemForGenre(genre, resources));
            }
        } else if (mediaId.startsWith(MEDIA_ID_MUSICS_BY_GENRE)) {
            String genre = MediaIDHelper.getHierarchy(mediaId)[1];
            for (MediaMetadataCompat metadata : getMusicByGenre(genre)) {
                mediaItems.add(createMediaItem(metadata));
            }
        } else {
            Logger.t(TAG).d("Skipping unmatched mediaId: ", mediaId);
        }
        return mediaItems;
    }


    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForRoot(Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(MEDIA_ID_MUSICS_BY_GENRE)
                .setTitle(resources.getString(R.string.browse_genres))
                .setSubtitle(resources.getString(R.string.browse_genre_subtitle))
//                .setIconUri(Uri.parse("android.resource://" +
//                "com.example.android.zhiliao/d"))
                .build();
        return new MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createBrowsableMediaItemForGenre(String genre, Resources resources) {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setMediaId(createMediaID(null, MEDIA_ID_MUSICS_BY_GENRE, genre))
                .setTitle(genre)
                .setSubtitle(resources.getString(
                        R.string.browse_musics_by_genre_subtitle, genre))
                .build();
        return new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE);
    }

    private MediaBrowserCompat.MediaItem createMediaItem(MediaMetadataCompat metadata) {
        String genre = metadata.getString(MediaMetadataCompat.METADATA_KEY_GENRE);
        String hierarchyAwareMediaID = MediaIDHelper.createMediaID(
                metadata.getDescription().getMediaId(), MEDIA_ID_MUSICS_BY_GENRE, genre);
        MediaMetadataCompat copy = new MediaMetadataCompat.Builder(metadata)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, hierarchyAwareMediaID)
                .build();
        return new MediaBrowserCompat.MediaItem(copy.getDescription(),
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);
    }

    public void retrieveMetteAsync(String parentId, OnMusicData onMusicData) {
        RestClient.builder()
                .url(String.format("https://engine.mebtte.com/1/music?key=keyword&value=%s", parentId))
                .headers("authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTcyOTExIiwiZXhwIjoxNTkzOTY0MDQ5LCJpYXQiOjE1NjI4NjAwNDl9.d9f_6ikO6gDD5Dcra7nxSzkzI8lP6vI4UI4SR32aiRU")
                .success(new ISuccess<SongSearchEntity>() {
//                    @Override
//                    public void onSuccess(String response) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(SongListEntity songListEntity) {
//
//                    }

                    @Override
                    public void onSuccess(SongSearchEntity songSearchEntity) {
                        List<MediaBrowserCompat.MediaItem> items = new ArrayList<>();
//                        List<SongSearchEntity.DataEntity> data = songSearchEntity.getData();
                        List<MusicEntity> data = songSearchEntity.getData();
                        Logger.t(TAG).d(data.size());
//                        for (SongSearchEntity.DataEntity dataEntity : data) {
                        for (MusicEntity dataEntity : data) {
//                            Logger.t(TAG).d(dataEntity.getNormal());
                            MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat.Builder()
                                    .setMediaUri(Uri.parse(dataEntity.getNormal()))
                                    .setMediaId(dataEntity.getId())
                                    .setTitle(dataEntity.getName())
                                    .setSubtitle(dataEntity.getSingers().get(0).getName())
                                    .setIconUri(Uri.parse(dataEntity.getCover()))
                                    .build();
                            items.add(new MediaBrowserCompat.MediaItem(mediaDescriptionCompat, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
                        }
                        onMusicData.onReceiveFirst(items);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String message) {
                        Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()), code + message, Toast.LENGTH_LONG).show();
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        Toast.makeText(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()), "onFailure", Toast.LENGTH_LONG).show();
                    }
                })
                .build()
                .getMusic();
    }


    public interface OnMusicData {
        void onChange();

        void onReceiveFirst(List<MediaBrowserCompat.MediaItem> items);
    }
}
