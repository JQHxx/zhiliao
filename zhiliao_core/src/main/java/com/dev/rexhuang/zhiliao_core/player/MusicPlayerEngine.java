package com.dev.rexhuang.zhiliao_core.player;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

import java.lang.ref.WeakReference;

import static com.dev.rexhuang.zhiliao_core.player.MusicPlayerService.PLAYER_PREPARED;
import static com.dev.rexhuang.zhiliao_core.player.MusicPlayerService.PREPARE_ASYNC_UPDATE;
import static com.dev.rexhuang.zhiliao_core.player.MusicPlayerService.RELEASE_WAKELOCK;
import static com.dev.rexhuang.zhiliao_core.player.MusicPlayerService.TRACK_PLAY_ENDED;
import static com.dev.rexhuang.zhiliao_core.player.MusicPlayerService.TRACK_PLAY_ERROR;
import static com.dev.rexhuang.zhiliao_core.player.MusicPlayerService.TRACK_WENT_TO_NEXT;

/**
 * *  created by RexHuang
 * *  on 2019/8/2
 */
class MusicPlayerEngine implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener {

    private String TAG = "MusicPlayerEngine";

    private final WeakReference<MusicPlayerService> mService;

    private MediaPlayer mCurrentMediaPlayer = new MediaPlayer();

    private Handler mHandler;

    //是否已经初始化
    private boolean mIsInitialized = false;
    //是否已经初始化
    private boolean mIsPrepared = false;

    MusicPlayerEngine(MusicPlayerService service) {
        mService = new WeakReference<>(service);
        mCurrentMediaPlayer.setWakeMode(mService.get(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    public void setDataSource(final String path) {
        mIsInitialized = setDataSourceImpl(mCurrentMediaPlayer, path);
    }

    private boolean setDataSourceImpl(MediaPlayer player, String path) {
        if (path == null) return false;
        try {
            if (player.isPlaying())
                player.stop();
            mIsPrepared = false;
            player.reset();
//        boolean cacheSetting = PreferenceManager.getDefaultSharedPreferences(MusicApp.getAppContext()).getBoolean("key_cache_mode", true);
//        LogUtil.d(TAG, "缓存设置：" + cacheSetting);
            if (path.startsWith("content://") || path.startsWith("/storage")) {
                player.setDataSource(mService.get(), Uri.parse(path));
            }
//        else if (cacheSetting) {
//            //缓存开启，读取缓存
//            HttpProxyCacheServer proxy = MusicApp.getProxy();
//            String proxyUrl = proxy.getProxyUrl(path);
//            LogUtil.d(TAG, "设置缓存,缓存地址：proxyUrl=" + proxyUrl);
//            player.setDataSource(proxyUrl);
//        }
            else {
                player.setDataSource(path);
            }
            player.prepareAsync();
            player.setOnPreparedListener(this);
            player.setOnBufferingUpdateListener(this);
            player.setOnErrorListener(this);
            player.setOnCompletionListener(this);
        } catch (Exception todo) {
            return false;
        }
        return true;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public boolean isIsInitialized() {
        return mIsInitialized;
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public void start() {
        mCurrentMediaPlayer.start();
    }

    public void stop() {
        mCurrentMediaPlayer.reset();
        mIsInitialized = false;
        mIsPrepared = false;
    }

    public void release() {
        mCurrentMediaPlayer.release();
    }

    public void pause() {
        mCurrentMediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mCurrentMediaPlayer.isPlaying();
    }

    public long duration() {
        if (mIsPrepared) {
            return mCurrentMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public long position() {
        return mCurrentMediaPlayer.getCurrentPosition();
    }

    public void seek(long whereto) {
        mCurrentMediaPlayer.seekTo((int) whereto);
    }

    public void setVolume(float vol) {
        mCurrentMediaPlayer.setVolume(vol, vol);
    }

    public int getAudioSessionId() {
        return mCurrentMediaPlayer.getAudioSessionId();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                final MusicPlayerService service = mService.get();
                final TrackErrorInfo errorInfo = new TrackErrorInfo(service.getAudioId()
                        , service.getTitle());
                mIsInitialized = false;
                mCurrentMediaPlayer.release();
                mCurrentMediaPlayer = new MediaPlayer();
                mCurrentMediaPlayer.setWakeMode(service, PowerManager.PARTIAL_WAKE_LOCK);
                Message msg = mHandler.obtainMessage(TRACK_PLAY_ERROR, errorInfo);
                mHandler.sendMessageDelayed(msg, 2000);
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp == mCurrentMediaPlayer) {
            mHandler.sendEmptyMessage(TRACK_WENT_TO_NEXT);
        } else {
//            mService.get().mWakeLock.acquire(30000);
            mHandler.sendEmptyMessage(TRACK_PLAY_ENDED);
            mHandler.sendEmptyMessage(RELEASE_WAKELOCK);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
       Message message = mHandler.obtainMessage(PREPARE_ASYNC_UPDATE,percent);
       mHandler.sendMessage(message);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (!mIsPrepared) {
            mIsPrepared = true;
            Message message = mHandler.obtainMessage(PLAYER_PREPARED);
            mHandler.sendMessage(message);
        }
    }

    private class TrackErrorInfo {
        private String audioId;
        private String trackName;

        private TrackErrorInfo(String audioId, String trackName) {
            this.audioId = audioId;
            this.trackName = trackName;
        }

        public String getAudioId() {
            return audioId;
        }

        public void setAudioId(String audioId) {
            this.audioId = audioId;
        }

        public String getTrackName() {
            return trackName;
        }

        public void setTrackName(String trackName) {
            this.trackName = trackName;
        }
    }
}
