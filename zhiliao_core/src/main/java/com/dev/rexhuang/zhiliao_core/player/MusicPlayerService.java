package com.dev.rexhuang.zhiliao_core.player;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.dev.rexhuang.zhiliao_core.bean.Music;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * *  created by RexHuang
 * *  on 2019/8/2
 */
public class MusicPlayerService extends Service {

    //工作线程和Handler
    private MusicPlayerHandler mHandler;
    private HandlerThread mWorkThread;
    //主线程Handler
    private Handler mMainHandler;

    public static final String SHUTDOWN = "com.cyl.music_lake.shutdown";
    public static final int TRACK_WENT_TO_NEXT = 2; //下一首
    public static final int RELEASE_WAKELOCK = 3; //播放完成
    public static final int TRACK_PLAY_ENDED = 4; //播放完成
    public static final int TRACK_PLAY_ERROR = 5; //播放出错

    public static final int PREPARE_ASYNC_UPDATE = 7; //PrepareAsync装载进程
    public static final int PLAYER_PREPARED = 8; //mediaplayer准备完成

    public static final int AUDIO_FOCUS_CHANGE = 12; //音频焦点改变
    public static final int VOLUME_FADE_DOWN = 13; //音量改变减少
    public static final int VOLUME_FADE_UP = 14; //音量改变增加

    public static final String CMD_TOGGLE_PAUSE = "toggle_pause";//按键播放暂停
    public static final String CMD_NEXT = "next";//按键下一首
    public static final String CMD_PREVIOUS = "previous";//按键上一首
    public static final String CMD_PAUSE = "pause";//按键暂停
    public static final String CMD_PLAY = "play";//按键播放
    public static final String CMD_STOP = "stop";//按键停止
    public static final String CMD_FORWARD = "forward";//按键停止
    public static final String CMD_REWIND = "reward";//按键停止
    public static final String SERVICE_CMD = "cmd_service";//状态改变
    public static final String FROM_MEDIA_BUTTON = "media";//状态改变
    public static final String CMD_NAME = "name";//状态改变
    public static final String UNLOCK_DESKTOP_LYRIC = "unlock_lyric"; //音量改变增加

    private MusicPlayerEngine mPlayer = null;
    private static MusicPlayerService instance;
    private IMusicServiceStub mBindStub = new IMusicServiceStub(this);

    public Music mPlayingMusic = null;
    public PowerManager.WakeLock mWakeLock;
    private PowerManager powerManager;

    private MediaSessionManager mediaSessionManager;
    private AudioAndFocusManager audioAndFocusManager;

    private int mServiceStartId = -1;
    boolean mServiceInUse = false;

    private List<Music> mPlayQueue = new ArrayList<>();
    private int mPlayingPos = -1;


    public class MusicPlayerHandler extends Handler {
        private final WeakReference<MusicPlayerService> mService;
        private float mCurrentVolume = 1.0f;

        public MusicPlayerHandler(MusicPlayerService mService, final Looper looper) {
            super(looper);
            this.mService = new WeakReference<>(mService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MusicPlayerService service = mService.get();
            synchronized (mService) {
                switch (msg.what) {
                    case VOLUME_FADE_DOWN:
                        mCurrentVolume -= 0.05f;
                        if (mCurrentVolume > 0.2f) {
                            sendEmptyMessageDelayed(VOLUME_FADE_DOWN, 10);
                        } else {
                            mCurrentVolume = 0.2f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case VOLUME_FADE_UP:
                        mCurrentVolume += 0.01f;
                        if (mCurrentVolume < 1.0f) {
                            sendEmptyMessageDelayed(VOLUME_FADE_UP, 10);
                        } else {
                            mCurrentVolume = 1.0f;
                        }
                        service.mPlayer.setVolume(mCurrentVolume);
                        break;
                    case TRACK_WENT_TO_NEXT:
                        mMainHandler.post(() -> service.next(true));
                        break;
                    case TRACK_PLAY_ENDED:
                        break;
                    case TRACK_PLAY_ERROR:
                        break;
                    case RELEASE_WAKELOCK:
                        break;
                    case PREPARE_ASYNC_UPDATE:
                        break;
                    case PLAYER_PREPARED:
                        break;
                    case AUDIO_FOCUS_CHANGE:
                        switch (msg.arg1) {
                            default:
                        }
                        break;
                    default:
                        break;

                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //todo: 广播服务
//        //初始化广播
//        initReceiver();
        //初始化参数
        initConfig();
//        //初始化电话监听服务
//        initTelephony();
//        //初始化通知
//        initNotify();
        //初始化音乐播放服务
        initMediaPlayer();
    }

    /**
     * 参数配置，AudioManager、锁屏
     */
    @SuppressLint("InvalidWakeLockTag")
    private void initConfig() {
        //初始化主线程Handler
        mMainHandler = new Handler(Looper.getMainLooper());
//        PlayQueueManager.INSTANCE.getPlayModeId();

        //初始化工作线程
        mWorkThread = new HandlerThread("MusicPlayerThread");
        mWorkThread.start();

        mHandler = new MusicPlayerHandler(this, mWorkThread.getLooper());

        //电源键
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PlayerWakelockTag");

        //初始化和设置MediaSessionCompat
        mediaSessionManager = new MediaSessionManager(mBindStub, this, mMainHandler);
        audioAndFocusManager = new AudioAndFocusManager(this, mHandler);
    }

    /**
     * 初始化音乐播放服务
     */
    private void initMediaPlayer() {
        mPlayer = new MusicPlayerEngine(this);
        mPlayer.setHandler(mHandler);
//        reloadPlayQueue();
    }

    /**
     * 启动Service服务，执行onStartCommand
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mServiceStartId = startId;
        mServiceInUse = true;
        if (intent != null) {
            final String action = intent.getAction();
            if (SHUTDOWN.equals(action)) {
//                releaseServiceUiAndStop();
                return START_NOT_STICKY;
            }
            handleCommandIntent(intent);
        }
        return START_NOT_STICKY;
    }

    private void handleCommandIntent(Intent intent) {
        final String action = intent.getAction();
        final String command = SERVICE_CMD.equals(action) ? intent.getStringExtra(CMD_NAME) : null;
    }

    public String getAudioId() {
        if (mPlayingMusic != null) {
            return mPlayingMusic.getMid();
        } else {
            return null;
        }
    }

    /**
     * 获取标题
     *
     * @return
     */
    public String getTitle() {
        if (mPlayingMusic != null) {
            return mPlayingMusic.getTitle();
        }
        return null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBindStub;
    }

    /**
     * TODO :下一首
     */
    private void next(Boolean isAuto) {
        synchronized (this) {

        }
    }

    /**
     * 根据位置播放音乐
     *
     * @param position
     */
    public void playMusic(int position) {
        playCurrentAndNext();
    }

    public void play(Music music) {
        if (music == null) return;
        if (mPlayingPos == -1 || mPlayQueue.size() == 0) {
            mPlayQueue.add(music);
            mPlayingPos = 0;
        } else if (mPlayingPos < mPlayQueue.size()) {
            mPlayQueue.add(mPlayingPos, music);
        } else {
            mPlayQueue.add(mPlayQueue.size(), music);
        }
        mPlayingMusic = music;
        playCurrentAndNext();
    }

    /**
     * 播放当前歌曲
     */
    private void playCurrentAndNext() {
        Log.e("rex", "playCurrentAndNext");
        synchronized (this) {
            if (mPlayingPos >= mPlayQueue.size() || mPlayingPos < 0) {
                return;
            }
            mPlayingMusic = mPlayQueue.get(mPlayingPos);
//            if (mPlayingMusic.getUri() == null || !Objects.equals())
            mPlayer.setDataSource(mPlayingMusic.getUri());
            mediaSessionManager.updateMetaData(mPlayingMusic);
            audioAndFocusManager.requestAudioFocus();

            final Intent intent = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
            intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
            intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
            sendBroadcast(intent);
//            mPlayer.start();

        }
    }

    public int getAudioSessionId() {
        synchronized (this) {
            return mPlayer.getAudioSessionId();
        }
    }
}
