package com.dev.rexhuang.zhiliao_core.base;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dev.rexhuang.zhiliao_core.callback.MediaFragmentListener;
import com.dev.rexhuang.zhiliao_core.player2.MusicCompatService;
import com.dev.rexhuang.zhiliao_core.player2.manager.MediaSessionConnection;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.playback.PlayActions;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;
import com.orhanobut.logger.Logger;

import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public abstract class BaseActivity extends AppCompatActivity implements ISupportActivity, MediaFragmentListener {

    private static final String TAG = BaseActivity.class.getSimpleName();

    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "com.dev.rexhuang.zhiliao.CURRENT_MEDIA_DESCRIPTION";
    public static final String EXTRA_CURRENT_MEDIA_ROTATION =
            "com.dev.rexhuang.zhiliao.CURRENT_MEDIA_ROTATION";
    public static final String MUSIC_SESSION_ACTION = "com.dev.rexhuang.zhiliao.musicservice.pendingintent";
    protected MediaBrowserCompat mMediaBrowserCompat;

    private MediaBrowserCompat.MediaItem mCurrentMediaItem;

    private MediaControllerCompat mediaControllerCompat;

    protected ZhiliaoSwitchFragment mZhiliaoSwitchFragment;

    private final SupportActivityDelegate mDelegate = new SupportActivityDelegate(this);

    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return mDelegate;
    }

    @Override
    public ExtraTransaction extraTransaction() {
        return mDelegate.extraTransaction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate(savedInstanceState);
        Logger.t(TAG).d("BaseActivity onCreate init MediaBrowserCompat");
//        mMediaBrowserCompat = new MediaBrowserCompat(this,
//                new ComponentName(this, MusicCompatService.class), mConnectionCallback, null);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDelegate.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.t(TAG).d("BaseActivity onStart MediaBrowserCompat connect MusicCompatService");
//        mMediaBrowserCompat.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Logger.t(TAG).d("BaseActivity onStop MediaBrowserCompat disconnect MusicCompatService");
//        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(this);
//        if (controllerCompat != null){
//            controllerCompat.unregisterCallback(mMediaControllerCallback);
//        }
//        mMediaBrowserCompat.disconnect();
    }

    @Override
    protected void onDestroy() {
        mDelegate.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    @Override
    final public void onBackPressed() {
        mDelegate.onBackPressed();
    }

    @Override
    public void onBackPressedSupport() {
        mDelegate.onBackPressedSupport();
    }

    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mDelegate.getFragmentAnimator();
    }

    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mDelegate.setFragmentAnimator(fragmentAnimator);
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return mDelegate.onCreateFragmentAnimator();
    }

    @Override
    public void post(Runnable runnable) {
        mDelegate.post(runnable);
    }

    /**
     * 获取栈内的fragment对象
     */
    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {
        @Override
        public void onConnected() {
            Logger.t(TAG).d("BaseActivity MusicCompatService onConnected");
            super.onConnected();
            try {
                connectToSession(mMediaBrowserCompat.getSessionToken());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed() {
            super.onConnectionFailed();
        }

        @Override
        public void onConnectionSuspended() {
            super.onConnectionSuspended();
        }
    };

    private final MediaControllerCompat.Callback mMediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);
        }
    };

    private void connectToSession(MediaSessionCompat.Token sessionToken) throws RemoteException {
        Logger.t(TAG).d("BaseActivity setMediaController");
        mediaControllerCompat = new MediaControllerCompat(this, sessionToken);
        MediaControllerCompat.setMediaController(this, mediaControllerCompat);
        mediaControllerCompat.registerCallback(mMediaControllerCallback);
        if (mZhiliaoSwitchFragment != null){
            mZhiliaoSwitchFragment.onConnected();
        }
    }

    @Override
    public MediaBrowserCompat getMediaBrowser() {
        return mMediaBrowserCompat;
    }

    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem mediaItem) {
        synchronized (this) {
            setCurrentMediaItem(mediaItem);
            MediaControllerCompat.getMediaController(this).getTransportControls().sendCustomAction(MusicCompatService.CMD_NEW_DATA, null);
        }
    }

    @Override
    public void onPlayAction(String playActions) {
        switch (playActions) {
            case PlayActions.PLAY:
                mediaControllerCompat.getTransportControls().play();
                break;
            case PlayActions.PAUSE:
                mediaControllerCompat.getTransportControls().pause();
                break;
            case PlayActions.NEXT:
                break;
            case PlayActions.PREVIOUS:
                break;
            case PlayActions.ORDER:
                break;
            case PlayActions.SHUFFLE:
                break;
            case PlayActions.LOOP:
                break;
            default:
                break;
        }
    }

    public MediaBrowserCompat.MediaItem getCurrentMediaItem() {
        return mCurrentMediaItem;
    }

    public void setCurrentMediaItem(MediaBrowserCompat.MediaItem mCurrentMediaItem) {
        this.mCurrentMediaItem = mCurrentMediaItem;
        QueueManager.getInstance().setCurrentItem(getCurrentMediaItem());
    }
}
