package com.dev.rexhuang.zhiliao_core.player2.manager;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.MusicService;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 与服务端连接的管理类
 */
@SuppressLint("StaticFieldLeak")
public class MediaSessionConnection {
    private static Context sContext;
    private ComponentName serviceComponent;
    private MediaBrowserCompat mediaBrowser;
    private boolean isConnected;
    private String rootMediaId;
    private PlaybackStateCompat playbackState = EMPTY_PLAYBACK_STATE;
    private MediaMetadataCompat nowPlaying = NOTHING_PLAYING;
    private MediaControllerCompat.TransportControls transportControls;
    private MediaControllerCompat mediaController;
    private MediaBrowserConnectionCallback mediaBrowserConnectionCallback;
    private MediaControllerCallback mMediaControllerCallback;
    private OnConnectListener mConnectListener;

    /**
     * 传入Context
     */
    public static void initConnection(Context context) {
        sContext = context;
    }

    private static volatile MediaSessionConnection sInstance;

    /**
     * 单例模式
     */
    public static MediaSessionConnection getInstance() {
        if (sInstance == null) {
            synchronized (MediaSessionConnection.class) {
                if (sInstance == null) {
                    sInstance = new MediaSessionConnection(new ComponentName(sContext, MusicService.class));
                }
            }
        }
        return sInstance;
    }

    /**
     * 构造好要使用的对象
     */
    private MediaSessionConnection(ComponentName serviceComponent) {
        this.serviceComponent = serviceComponent;
        mediaBrowserConnectionCallback = new MediaBrowserConnectionCallback();
        mMediaControllerCallback = new MediaControllerCallback();
        mediaBrowser = new MediaBrowserCompat(sContext, serviceComponent, mediaBrowserConnectionCallback, null);
    }

    public void subscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback) {
        mediaBrowser.subscribe(parentId, callback);
    }

    public void unsubscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback) {
        mediaBrowser.unsubscribe(parentId, callback);
    }

    /**
     * 服务连接上的回调操作
     */
    public void setConnectListener(OnConnectListener connectListener) {
        mConnectListener = connectListener;
    }

    /**
     * 是否已连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 获取rootMediaId
     */
    public String getRootMediaId() {
        return rootMediaId;
    }

    /**
     * 获取 MediaBrowserCompat
     */
    public MediaBrowserCompat getMediaBrowser() {
        return mediaBrowser;
    }

    /**
     * 获取当前播放的 PlaybackStateCompat
     */
    public PlaybackStateCompat getPlaybackState() {
        return playbackState;
    }

    /**
     * 获取当前播放的 MediaMetadataCompat
     */
    public MediaMetadataCompat getNowPlaying() {
        return nowPlaying;
    }

    /**
     * 获取播放控制器
     */
    public MediaControllerCompat.TransportControls getTransportControls() {
        return transportControls;
    }

    public MediaControllerCompat getMediaController() {
        return mediaController;
    }

    /**
     * 连接
     */
    public void connect() {
        if (!isConnected) {
            //进程被异常杀死时，App 被外部链接唤起时，connect 状态为 CONNECT_STATE_CONNECTING，
            //导致崩溃，所以要先执行 disconnect
            disconnectImpl();
            mediaBrowser.connect();
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (isConnected) {
            disconnectImpl();
            isConnected = false;
        }
    }

    private void disconnectImpl() {
        if (mediaController != null) {
            mediaController.unregisterCallback(mMediaControllerCallback);
        }
        mediaBrowser.disconnect();
    }

    /**
     * 连接回调
     */
    private class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        /**
         * 已连接上,获取MediaControllerCompat对象
         */
        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mediaController = new MediaControllerCompat(sContext, mediaBrowser.getSessionToken());
                mediaController.registerCallback(mMediaControllerCallback);
                transportControls = mediaController.getTransportControls();
                rootMediaId = mediaBrowser.getRoot();
                isConnected = true;
                if (mConnectListener != null) {
                    mConnectListener.onConnected();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * 连接阻塞时，直接断开连接
         */
        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
            disconnect();
            isConnected = false;
        }

        /**
         * 连接失败
         */
        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
            isConnected = false;
        }
    }

    /**
     * 接收播放状态改变的回调接口
     */
    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
            playbackState = state != null ? state : EMPTY_PLAYBACK_STATE;

            //状态监听,获取监听状态的回调列表
            CopyOnWriteArrayList<OnPlayerEventListener> mPlayerEventListeners = MusicManager.getInstance().getPlayerEventListeners();
            if (state != null) {
                //遍历回调列表,进行播放状态改变后的回调操作
                for (OnPlayerEventListener listener : mPlayerEventListeners) {
                    switch (state.getState()) {
                        case PlaybackStateCompat.STATE_PLAYING:
                            listener.onPlayerStart();
                            break;
                        case PlaybackStateCompat.STATE_PAUSED:
                            listener.onPlayerPause();
                            break;
                        case PlaybackStateCompat.STATE_STOPPED:
                            listener.onPlayerStop();
                            break;
                        case PlaybackStateCompat.STATE_ERROR:
                            listener.onError(state.getErrorCode(), state.getErrorMessage().toString());
                            break;
                        case PlaybackStateCompat.STATE_NONE:
                            String musicId = nowPlaying.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                            MusicEntity musicEntity = MusicProvider.getInstance().getMusicEntity(musicId);
                            listener.onPlayCompletion(musicEntity);
                            break;
                        case PlaybackStateCompat.STATE_BUFFERING:
                            listener.onBuffering();
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        /**
         * 播放器切换歌曲的回调
         */
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
            nowPlaying = metadata != null ? metadata : NOTHING_PLAYING;

            //状态监听
            CopyOnWriteArrayList<OnPlayerEventListener> mPlayerEventListeners = MusicManager.getInstance().getPlayerEventListeners();
            if (metadata != null) {
                for (OnPlayerEventListener listener : mPlayerEventListeners) {
                    String musicId = metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID);
                    MusicEntity musicEntity = MusicProvider.getInstance().getMusicEntity(musicId);
                    listener.onMusicSwitch(musicEntity);
                }
            }
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }

        /**
         * 连接中断
         */
        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            mediaBrowserConnectionCallback.onConnectionSuspended();
        }
    }

    /**
     * 什么都没播放时的状态
     */
    private static PlaybackStateCompat EMPTY_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
            .setState(PlaybackStateCompat.STATE_NONE, 0, 0f)
            .build();

    /**
     * 什么都没播放时的MediaMetadataCompat
     */
    private static MediaMetadataCompat NOTHING_PLAYING = new MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, "")
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
            .build();

    /**
     * 连接上服务后回调的接口的定义
     */
    public interface OnConnectListener {
        void onConnected();
    }
}
