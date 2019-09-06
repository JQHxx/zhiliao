package com.dev.rexhuang.zhiliao_core.player2;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media.MediaBrowserServiceCompat;
import androidx.media.session.MediaButtonReceiver;
import androidx.mediarouter.media.MediaRouter;

import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.dev.rexhuang.zhiliao_core.player2.playback.LocalPlayback;
import com.dev.rexhuang.zhiliao_core.player2.playback.PlaybackManager;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;
import com.google.android.exoplayer2.C;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/8/5
 */
public class MusicCompatService extends MediaBrowserServiceCompat implements QueueManager.MediaDataUpdateListener, PlaybackManager.PlaybackServiceCallback {

    private static final String TAG = MusicCompatService.class.getSimpleName();
    private static final String MEDIA_ROOT_ID = "_zhiliao_";
    private MediaSessionCompat mSession;
    private MediaRouter mMediaRouter;
    //    private MediaNotificationManager mMediaNotificationManager;
    private MusicProvider mMusicProvider;
    private PlaybackManager mPlaybackManager;
    private QueueManager queueManager;
    private final DelayedStopHandler mDelayedStopHandler = new DelayedStopHandler(this);
    private MediaPlayer mediaPlayer;
    public static final String ACTION_CMD = "com.dev.rexhuang.zhiliao.ACTION_CMD";
    // The key in the extras of the incoming Intent indicating the command that
    // should be executed (see {@link #onStartCommand})
    public static final String CMD_NAME = "CMD_NAME";
    // A value of a CMD_NAME key in the extras of the incoming Intent that
    // indicates that the music playback should be paused (see {@link #onStartCommand})
    public static final String CMD_PAUSE = "CMD_PAUSE";

    public static final String CMD_NEW_DATA = "CMD_NEW_DATA";
    // Delay stopSelf by using a handler.
    private static final int STOP_DELAY = 30000;
//    private PlaybackStateCompat playbackStateCompat = new PlaybackStateCompat()

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.t(TAG).d("MusicCompatService onCreate");
        mMusicProvider = new MusicProvider();
        //歌单
        queueManager = QueueManager.getInstance().setMediaDataUpdateListener(this);
        LocalPlayback playback = new LocalPlayback(this, mMusicProvider);
        mPlaybackManager = new PlaybackManager(this, getResources(), mMusicProvider, queueManager,
                playback);
//        initializeMediaPlayer();
        mSession = new MediaSessionCompat(this, "MusicCompatService");
        setSessionToken(mSession.getSessionToken());
//        mSession.setCallback(mSessionCallback);
        mSession.setCallback(mPlaybackManager.getMediaSessionCallback());
        mSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        Context context = getApplicationContext();
        Intent intent = new Intent(BaseActivity.MUSIC_SESSION_ACTION);
        PendingIntent pi = PendingIntent.getActivity(context, 99,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mSession.setSessionActivity(pi);
        mPlaybackManager.updatePlaybackState(null);
        mMediaRouter = MediaRouter.getInstance(getApplicationContext());
        //Notification暂时不弄

    }

    private void initializeMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                            .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1.0f)
                            .build());
                    PlayState.PLAYSTATE = PlayState.PLAYING;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp == mediaPlayer) {
                        next(mediaPlayer);
                    }
                }
            });
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {

                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    switch (what) {
                        case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                        case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                            mediaPlayer.release();
                            mediaPlayer = new MediaPlayer();
                            return true;
                        default:
                            break;
                    }
                    return true;
                }
            });

        }
    }

    private void next(MediaPlayer mediaPlayer) {
        synchronized (this) {
            QueueManager queueManager = QueueManager.getInstance();
            queueManager.next();
        }
        setDataSourceImpl();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.t(TAG).d("MusicCompatService onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            String command = intent.getStringExtra(CMD_NAME);
            if (ACTION_CMD.equals(action)) {
                if (CMD_PAUSE.equals(command)) {
                    mPlaybackManager.handlePauseRequest();
                }
            } else {
                MediaButtonReceiver.handleIntent(mSession, intent);
            }
        }
        // Reset the delay handler to enqueue a message to stop the service if
        // nothing is playing.
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlaybackManager.handleStopRequest(null);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mSession.release();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Logger.t(TAG).d("MusicCompatService onGetRoot");
        return new BrowserRoot(MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Logger.t(TAG).d("MusicCompatService onLoadChildren");
        result.detach();
        mMusicProvider.retrieveMetteAsync(parentId, new MusicProvider.OnMusicData() {
            @Override
            public void onChange() {

            }

            @Override
            public void onReceiveFirst(List<MediaBrowserCompat.MediaItem> items) {
                Logger.t(TAG).d("MusicCompatService onReceiveFirst " + items.size());
                result.sendResult(items);
            }
        });
    }

    private MediaSessionCompat.Callback mSessionCallback = new MediaSessionCompat.Callback() {
        @Override
        public void onPlay() {
            super.onPlay();
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), 1.0f)
                        .build());
                PlayState.PLAYSTATE = PlayState.PLAYING;
            }
        }

        @Override
        public void onPause() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                        .setState(PlaybackStateCompat.STATE_PAUSED, mediaPlayer.getCurrentPosition(), 1.0f)
                        .build());
                PlayState.PLAYSTATE = PlayState.NOT_PLAYING;
            }
        }

        @Override
        public void onCustomAction(String action, Bundle extras) {
            super.onCustomAction(action, extras);
            switch (action) {
                case CMD_NEW_DATA:
                    setDataSourceImpl();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
            super.onAddQueueItem(description);
        }

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description, int index) {
            super.onAddQueueItem(description, index);
        }

    };

    void setDataSourceImpl() {
        if (mediaPlayer == null) {
            initializeMediaPlayer();
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(QueueManager.getInstance().getCurrentItem().getDescription().getMediaUri().toString());
            Toast.makeText(MusicCompatService.this, QueueManager.getInstance().getCurrentItem().getDescription().getTitle(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 1.0f)
                .build());
        PlayState.PLAYSTATE = PlayState.NONE;
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onMediadataChange(MediaMetadataCompat metadata) {
//        setDataSourceImpl();
        mSession.setMetadata(metadata);
    }

    @Override
    public void onMetadataRetrieveError() {
        mPlaybackManager.updatePlaybackState("检索不到歌曲");
    }

    @Override
    public void onCurrentQueueIndexUpdated(int queueIndex) {
        mPlaybackManager.handlePlayRequest();
    }

    @Override
    public void onQueueUpdated(String title, List<MediaSessionCompat.QueueItem> newQueue) {
        mSession.setQueue(newQueue);
        mSession.setQueueTitle(title);
    }

    @Override
    public void onPlaybackStart() {
        mSession.setActive(true);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        startService(new Intent(getApplicationContext(), MusicCompatService.class));
    }

    @Override
    public void onNotificationRequired() {

    }

    @Override
    public void onPlaybackStop() {
        mSession.setActive(false);
        mDelayedStopHandler.removeCallbacksAndMessages(null);
        mDelayedStopHandler.sendEmptyMessageDelayed(0, STOP_DELAY);
    }

    @Override
    public void onPlaybackStateUpdated(PlaybackStateCompat newState) {
        mSession.setPlaybackState(newState);
    }

    /**
     * A simple handler that stops the service if playback is not active (playing)
     */
    private static class DelayedStopHandler extends Handler {
        private final WeakReference<MusicCompatService> mWeakReference;

        private DelayedStopHandler(MusicCompatService service) {
            mWeakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            MusicCompatService service = mWeakReference.get();
            if (service != null && service.mPlaybackManager.getPlayback() != null) {
                if (service.mPlaybackManager.getPlayback().isPlaying()) {
                    Logger.t(TAG).d("Ignoring delayed stop since the media player is in use.");
                    return;
                }
                Logger.t(TAG).d("Stopping service with delay handler.");
                service.stopSelf();
            }
        }
    }
}
