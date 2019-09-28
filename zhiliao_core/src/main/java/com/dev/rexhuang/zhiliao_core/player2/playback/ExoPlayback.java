package com.dev.rexhuang.zhiliao_core.player2.playback;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat.QueueItem;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.dev.rexhuang.zhiliao_core.player2.playback.download.ExoDownload;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.offline.FilteringManifestParser;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.manifest.DashManifestParser;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.hls.playlist.DefaultHlsPlaylistParserFactory;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.RandomTrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.google.android.exoplayer2.C.CONTENT_TYPE_MUSIC;
import static com.google.android.exoplayer2.C.USAGE_MEDIA;

/**
 * ExoPlayer播放器的具体封装
 * *  created by RexHuang
 * *  on 2019/9/5
 */
public final class ExoPlayback implements Playback {

    private static final String TAG = ExoPlayback.class.getSimpleName();

    public static final String ACTION_CHANGE_VOLUME = "ACTION_CHANGE_VOLUME";
    public static final String ACTION_DERAILLEUR = "ACTION_DERAILLEUR";

    public static final String ABR_ALGORITHM_DEFAULT = "default";
    public static final String ABR_ALGORITHM_RANDOM = "random";
    public static String abrAlgorithm = ABR_ALGORITHM_DEFAULT;

    public static final String EXTENSION_RENDERER_MODE_ON = "EXTENSION_RENDERER_MODE_ON";
    public static final String EXTENSION_RENDERER_MODE_OFF = "EXTENSION_RENDERER_MODE_OFF";
    public static String rendererMode = EXTENSION_RENDERER_MODE_OFF;

    private final Context mContext;
    private boolean mPlayOnFocusGain;
    private Callback mCallback;
    private final MusicProvider mMusicProvider;
    private String mCurrentMediaId;

    private SimpleExoPlayer mExoPlayer;
    private final ExoPlayerEventListener mEventListener = new ExoPlayerEventListener();

    private boolean mExoPlayerNullIsStopped = false;

    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;

    public ExoPlayback(Context context, MusicProvider musicProvider) {
        this.mContext = context;
        this.mMusicProvider = musicProvider;
        trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
    }

    @Override
    public void start() {

    }

    @Override
    public void stop(boolean notifyListeners) {
        releaseResources(true);
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public int getState() {
        if (mExoPlayer == null) {
            return mExoPlayerNullIsStopped ? PlaybackStateCompat.STATE_STOPPED : PlaybackStateCompat.STATE_NONE;
        }
        switch (mExoPlayer.getPlaybackState()) {
            case Player.STATE_IDLE:
                return PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;
            case Player.STATE_READY:
                return mExoPlayer.getPlayWhenReady() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;
            case Player.STATE_ENDED:
                return PlaybackStateCompat.STATE_NONE;
            default:
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return mPlayOnFocusGain || (mExoPlayer != null && mExoPlayer.getPlayWhenReady());
    }

    @Override
    public long getCurrentStreamPosition() {
        return mExoPlayer != null ? mExoPlayer.getCurrentPosition() : 0;
    }

    @Override
    public long getBufferedPosition() {
        return mExoPlayer != null ? mExoPlayer.getBufferedPosition() : 0;
    }

    @Override
    public void updateLastKnownStreamPosition() {
        // Nothing to do. Position maintained by ExoPlayer.
    }

    @Override
    public void play(QueueItem item, boolean isPlayWhenReady) {
        Logger.d("play");
        mPlayOnFocusGain = true;
        String mediaId = item.getDescription().getMediaId();
        boolean mediaHasChanged = !TextUtils.equals(mediaId, mCurrentMediaId);
        if (mediaHasChanged) {
            mCurrentMediaId = mediaId;
        }
        if (mediaHasChanged || mExoPlayer == null) {
            releaseResources(false);//release everything except the player
            MediaMetadataCompat track = mMusicProvider.getMusic(mediaId);
            //获取要播放的音乐的URL
            String source = track.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI);
            if (TextUtils.isEmpty(source)) {
                return;
            }
            source = source.replaceAll(" ", "%20");// Escape spaces for URLs
            //缓存歌曲
            if (ExoDownload.getInstance().isOpenCache()) {
                ExoDownload.getInstance().getDownloadTracker().toggleDownload(mediaId, Uri.parse(source), "");
            }

            if (mExoPlayer == null) {
                //曲目选择
                TrackSelection.Factory trackSelectionFactory;
                if (abrAlgorithm.equals(ABR_ALGORITHM_DEFAULT)) {
                    trackSelectionFactory = new AdaptiveTrackSelection.Factory();
                } else {
                    trackSelectionFactory = new RandomTrackSelection.Factory();
                }
                //使用扩展渲染器模式
                @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                        rendererMode.equals(EXTENSION_RENDERER_MODE_ON) ?
                                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON :
                                DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
                DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(mContext, extensionRendererMode);

                //曲目选择，使用默认就好
                trackSelector = new DefaultTrackSelector(trackSelectionFactory);
                trackSelector.setParameters(trackSelectorParameters);

                DefaultDrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;

                //创建一个ExoPlayer实例
                mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, renderersFactory,
                        trackSelector, drmSessionManager);

                //注册播放事件监听和日志调试监听(adb logcat | grep 'EventLogger\|ExoPlayerImpl')
                mExoPlayer.addListener(mEventListener);
                mExoPlayer.addAnalyticsListener(new EventLogger(trackSelector));
            }

            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .setUsage(USAGE_MEDIA)
                    .build();
            mExoPlayer.setAudioAttributes(audioAttributes, true);//第二个参数能使ExoPlayer自动管理焦点

            //DataSource.Factory用来构造MediaSource
            DataSource.Factory dataSourceFactory = ExoDownload.getInstance().buildDataSourceFactory(mContext);

            //构造ExoPlayer的实例对象，并将对象传达给Exoplayer，
            //有四种类型 ：DashMediaSource/SsMediaSource/HlsMediaSource/ProgressiveMediaSource
            //还提供ConcatenatingMediaSource/ClippingMediaSource/LoopingMediaSource/MergingMediaSource,这些MediaSource实现可通过合成实现更复杂的播放功能
            //ConcatenatingMediaSource可以提供播放列表的功能
            MediaSource mediaSource = buildMediaSource(dataSourceFactory, Uri.parse(source), null);
            mExoPlayer.prepare(mediaSource);
        }

        if (isPlayWhenReady) {
            //操作ExoPlayer用于play/pause
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    private MediaSource buildMediaSource(DataSource.Factory dataSourceFactory, Uri uri, @Nullable String overrideExtension) {
        //Exoplayer提供的工具用来检测Uri的类型
        @C.ContentType int type = Util.inferContentType(uri, overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(dataSourceFactory)
                        .setManifestParser(
                                new FilteringManifestParser<>(new DashManifestParser(), getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(dataSourceFactory)
                        .setManifestParser(
                                new FilteringManifestParser<>(new SsManifestParser(), getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(dataSourceFactory)
                        .setPlaylistParserFactory(
                                new DefaultHlsPlaylistParserFactory(getOfflineStreamKeys(uri)))
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                boolean isRtmpSource = uri.toString().toLowerCase().startsWith("rtmp://");
                boolean isFlacSource = uri.toString().toLowerCase().endsWith(".flac");
                if (isFlacSource) {
                    DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                    return new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory,
                            null, null);
                } else {
                    return new ExtractorMediaSource.Factory(isRtmpSource ? new RtmpDataSourceFactory() : dataSourceFactory)
                            .createMediaSource(uri);
                }
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private List<StreamKey> getOfflineStreamKeys(Uri uri) {
        return ExoDownload.getInstance().getDownloadTracker().getOfflineStreamKeys(uri);
    }

    @Override
    public void pause() {
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
        releaseResources(false);
    }

    @Override
    public void seekTo(long position) {
        //seek within the media
        if (mExoPlayer != null) {
            mExoPlayer.seekTo(position);
        }
    }

    @Override
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    public void setCurrentMediaId(String mediaId) {
        this.mCurrentMediaId = mediaId;
    }

    @Override
    public String getCurrentMediaId() {
        return mCurrentMediaId;
    }

    /**
     * 快进
     */
    @Override
    public void onFastForward() {
        if (mExoPlayer != null) {
            float currSpeed = mExoPlayer.getPlaybackParameters().speed;
            float currPitch = mExoPlayer.getPlaybackParameters().pitch;
            float newSpeed = currSpeed + 0.5f;
            mExoPlayer.setPlaybackParameters(new PlaybackParameters(newSpeed, currPitch));
        }
    }

    /**
     * 倒带
     */
    @Override
    public void onRewind() {
        if (mExoPlayer != null) {
            float currSpeed = mExoPlayer.getPlaybackParameters().speed;
            float currPitch = mExoPlayer.getPlaybackParameters().pitch;
            float newSpeed = currSpeed - 0.5f;
            if (newSpeed <= 0) {
                newSpeed = 0;
            }
            mExoPlayer.setPlaybackParameters(new PlaybackParameters(newSpeed, currPitch));
        }
    }

    /**
     * 指定语速refer是否以当前的速度为基数 multiple是倍率
     *
     * @param refer
     * @param multiple
     */
    @Override
    public void onDerailleur(boolean refer, float multiple) {
        if (mExoPlayer != null) {
            float currSpeed = mExoPlayer.getPlaybackParameters().speed;
            float currPitch = mExoPlayer.getPlaybackParameters().pitch;
            float newSpeed = refer ? currSpeed * multiple : multiple;
            if (newSpeed > 0) {
                mExoPlayer.setPlaybackParameters(new PlaybackParameters(newSpeed, currPitch));
            }
        }
    }

    /**
     * 设置音量
     */
    @Override
    public void setVolume(float audioVolume) {
        if (mExoPlayer != null) {
            mExoPlayer.setVolume(audioVolume);
        }
    }

    /**
     * 获取音量
     */
    @Override
    public float getVolume() {
        if (mExoPlayer != null) {
            return mExoPlayer.getVolume();
        } else {
            return -1;
        }
    }

    @Override
    public long getDuration() {
        if (mExoPlayer != null) {
            return mExoPlayer.getDuration();
        } else {
            return -1;
        }
    }

    private void releaseResources(boolean releasePlayer) {
        if (releasePlayer && mExoPlayer != null) {
            mExoPlayer.release();
            mExoPlayer.removeListener(mEventListener);
            mExoPlayer = null;
            mExoPlayerNullIsStopped = true;
            mPlayOnFocusGain = false;
        }
    }

    private final class ExoPlayerEventListener implements Player.EventListener {
        @Override
        public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
            // 在刷新时间线和/或清单时调用。请注意，如果时间轴已更改，那么位置不连续也可能发生。
            // 例如，由于从时间轴添加或删除了时间段，因此当前时间段索引可能已更改。
            // 这不会通过单独调用onPositionDiscontinuity（int）。
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // 当可用或选定的曲目改变时调用
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // 在播放器启动或停止加载源时调用。
        }

        /**
         * Player.STATE_IDLE    初始化状态，当播放器状态为stopped或者failed
         * Player.STATE_BUFFERING    加载中状态
         * Player.STATE_READY    可以播放的状态
         * Player.STATE_ENDED    已经播放完当前媒体的状态
         */
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            // 当Player.getPlayWhenReady()或Player.getPlaybackState()返回的值更改时调用。
            switch (playbackState) {
                case Player.STATE_IDLE:
                case Player.STATE_BUFFERING:
                case Player.STATE_READY:
                    if (mCallback != null) {
                        mCallback.onPlaybackStatusChanged(getState());
                    }
                    break;
                case Player.STATE_ENDED:
                    if (mCallback != null) {
                        mCallback.onCompletion();
                    }
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            //发生错误时调用。调用此方法后，播放状态会即转换为Player.STATE_IDLE。播放器实例仍然可以使用，并且如果不再需要调用Player.release（）
            final String what;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    what = error.getSourceException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    what = error.getRendererException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    what = error.getUnexpectedException().getMessage();
                    break;
                default:
                    what = "Unknown: " + error;

            }

            if (mCallback != null) {
                mCallback.onError("ExoPlayer error " + what);
            }
        }

        @Override
        public void onPositionDiscontinuity(int reason) {
            // 在不更改时间线的情况下发生位置不连续时调用。
            // 当当前窗口或周期索引改变时（由于回放从时间线中的一个周期过渡到下一个周期），
            // 或者当回放位置在当前正在播放的周期内跳变时（发生寻道），就会发生位置不连续执行或源在内部引入不连续性时）。
            // 如果由于更改时间线而发生位置不连续，则不会调用此方法。在这种情况下，将调用onTimelineChanged（Timeline，Object，int）。
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // 当前播放参数更改时调用。播放参数可能会因调用Player.setPlaybackParameters（PlaybackParameters）而改变，
            // 或者播放器本身可能会更改它们（例如，如果音频播放切换为直通模式，则无法再进行速度调节）。
        }

        @Override
        public void onSeekProcessed() {
            // 当播放器处理完所有pending seek请求时调用。
            // 保证在播放器状态改变并调用了onPlayerStateChanged（boolean，int）之后调用。
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            // 当Player.getRepeatMode() 返回值更改时调用.
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            // 当Player.getShuffleModeEnabled() 返回值更改时调用.
        }


    }

}
