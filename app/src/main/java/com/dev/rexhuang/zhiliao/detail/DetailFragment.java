package com.dev.rexhuang.zhiliao.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dev.rexhuang.zhiliao.QueueDialog;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.timer.TimeHelper;
import com.dev.rexhuang.zhiliao.timer.TimerTaskManager;
import com.dev.rexhuang.zhiliao.util.BitmapUtils;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.orhanobut.logger.Logger;
import com.youth.banner.transformer.DepthPageTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/9/6
 */
public class DetailFragment extends ZhiliaoFragment {

    private static final String TAG = DetailFragment.class.getSimpleName();
    private MyRunnable myRunnable;

    //Arguments存储变量
    private Bundle args;
    private float currentRotation;
    private MusicEntity currentMusicEntity;

    //播放器回调
    private OnPlayerEventListener onPlayerEventListener;

    //子Fragment
    private CoverFragment coverFragment;
    private LyricFragment lyricFragment;

    //时间
    private TimerTaskManager mTimeTaskManager;

    //PlayQueueDialog
    private QueueDialog queueDialog;

    private Drawable mFirstDrawable;

    private String[] play_mode_text;
    private Drawable[] play_mode_drawable;

    @BindView(R.id.iv_play_pause)
    ImageView iv_play_pause;

    @BindView(R.id.iv_bg_detail)
    AppCompatImageView iv_bg_detail;

    @BindView(R.id.tv_detail_music_name)
    AppCompatTextView tv_detail_music_name;

    @BindView(R.id.tv_singer_name)
    AppCompatTextView tv_singer_name;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.rightTv)
    CheckedTextView rightTv;

    @BindView(R.id.leftTv)
    CheckedTextView leftTv;

    @BindView(R.id.sb_progress)
    SeekBar sb_progress;

    @BindView(R.id.tv_progress)
    TextView tv_progress;

    @BindView(R.id.tv_duration)
    TextView tv_duration;

    @BindView(R.id.iv_playmode)
    ImageView iv_playmode;

    @SuppressWarnings("ConstantConditions")
    @OnClick(R.id.iv_detail_back)
    void onClickBack() {
        getActivity().finish();
    }

    @OnClick(R.id.iv_play_pause)
    void onClickPlayPause() {
        Logger.d(MusicManager.getInstance().isPlaying());
        if (MusicManager.getInstance().isPlaying()) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    @OnClick(R.id.iv_next)
    void onClickNext() {
        MusicManager.getInstance().skipToNext();
    }

    @OnClick(R.id.iv_prev)
    void onClickPrev() {
        MusicManager.getInstance().skipToPrevious();
    }

    @OnClick(R.id.iv_queue)
    void onClickQueue() {
        showQueueDialog();
    }

    public static DetailFragment newInstance(String musicId, Float rotation, String from) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle args = detailFragment.getArguments();
        if (args != null) {
            args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicId);
            args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, rotation);
            args.putString(BaseActivity.FRGMENT_FROM, from);
        } else {
            args = new Bundle();
            args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicId);
            args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, rotation);
            args.putString(BaseActivity.FRGMENT_FROM, from);
        }
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @OnClick(R.id.iv_playmode)
    void onClickMode() {
        nextMode();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_detail;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        getArgumentsInfo();
        initViewPager();
        initPlayEventListener();
        initProgressBar();
        checkMusicManager();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        play_mode_text = getActivity().getResources().getStringArray(R.array.play_mode_text);
        play_mode_drawable = new Drawable[]{getActivity().getDrawable(R.drawable.ic_order),
                getActivity().getDrawable(R.drawable.ic_repeat),
                getActivity().getDrawable(R.drawable.ic_loop)};
    }

    private void getArgumentsInfo() {
        args = getArguments();
        if (args != null) {
            String musicId = args.getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION) == null ?
                    MusicManager.getInstance().getNowPlayingSongId() :
                    args.getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            currentRotation = args.getFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION);
            currentMusicEntity = MusicProvider.getInstance().getMusicEntity(musicId);
            if (currentMusicEntity != null) {
                tv_detail_music_name.setText(currentMusicEntity.getName());
                tv_singer_name.setText(currentMusicEntity.getSingers().get(0).getName());
                Bitmap covetBitmap = currentMusicEntity.getCoverBitmap();
                if (covetBitmap == null) {
                    getCoverBitmap();
                } else {
                    myRunnable = new MyRunnable(covetBitmap);
                    iv_bg_detail.post(myRunnable);
                }
            }
        }
    }

    private void checkMusicManager() {
        switch (MusicManager.getInstance().getRepeatMode()) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                iv_playmode.setImageDrawable(play_mode_drawable[0]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                iv_playmode.setImageDrawable(play_mode_drawable[1]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                iv_playmode.setImageDrawable(play_mode_drawable[2]);
                break;
        }
        if (MusicManager.getInstance().isPlaying()) {
            showPlaying(currentMusicEntity, true, true);
            mTimeTaskManager.startToUpdateProgress();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initProgressBar() {
        sb_progress.setProgressDrawable(getActivity().getDrawable(R.drawable.progress));
        setProgressBar();
        mTimeTaskManager = new TimerTaskManager();
        mTimeTaskManager.setUpdateProgressTask(() -> {
            setProgressBar();
            setLyric();
        });
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tv_progress.setText(TimeHelper.ms2HMS(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //停止进度监听
                mTimeTaskManager.stopToUpdateProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //设置seekPosition
                int progress = sb_progress.getProgress();
                MusicManager.getInstance().seekTo(progress);
                //重新监听进度
                mTimeTaskManager.startToUpdateProgress();
            }
        });
    }

    private void setLyric() {
        lyricFragment.setLyric(false);
    }

    private void setProgressBar() {
        long position = MusicManager.getInstance().getPlayingPosition();
        long duration = MusicManager.getInstance().getDuration();
        long buffered = MusicManager.getInstance().getBufferedPosition();
        if (sb_progress.getMax() != duration) {
            sb_progress.setMax((int) duration);
        }
        sb_progress.setProgress((int) position);
        sb_progress.setSecondaryProgress((int) buffered);
        tv_progress.setText(TimeHelper.ms2HMS(position));
        tv_duration.setText(TimeHelper.ms2HMS(duration));
    }

    private void initPlayEventListener() {
        onPlayerEventListener = new OnPlayerEventListener() {
            @Override
            public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                if (queueDialog != null) {
                    queueDialog.onQueueChanged(queue);
                }
                if (MusicManager.getInstance().getPlayList().size() <= 0) {
                    showStopped();
                }
            }

            @Override
            public void onMusicSwitch(MusicEntity musicEntity) {
                if (musicEntity != null) {
                    showPlaying(musicEntity, false, true);
                    if (queueDialog != null) {
                        queueDialog.notifyDataSetChanged();
                    }
                    lyricFragment.setLyric(true);
                }
            }

            @Override
            public void onPlayerStart() {
                showPlaying(MusicManager.getInstance().getNowPlayingSongInfo(), true, false);
                mTimeTaskManager.startToUpdateProgress();
            }

            @Override
            public void onPlayerPause() {
                showPaused();
                mTimeTaskManager.stopToUpdateProgress();
            }

            @Override
            public void onPlayerStop() {
                showPaused();
                mTimeTaskManager.stopToUpdateProgress();
            }

            @Override
            public void onPlayCompletion(MusicEntity musicEntity) {
                showPaused();
                mTimeTaskManager.stopToUpdateProgress();
            }

            @Override
            public void onBuffering() {
                Toast.makeText(get_mActivity(), "正在缓冲,请稍等!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Toast.makeText(get_mActivity(), "播放出错!!", Toast.LENGTH_SHORT).show();
                mTimeTaskManager.stopToUpdateProgress();
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                if (queueDialog != null) {
                    queueDialog.onRepeatModeChanged(repeatMode);
                }
                String mode;
                switch (repeatMode) {
                    case PlaybackStateCompat.REPEAT_MODE_NONE:
                        mode = "顺序播放";
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_ONE:
                        mode = "单曲循环";
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_ALL:
                        mode = "列表循环";
                        break;
                    default:
                        mode = "顺序播放";
                        break;
                }
                Toast.makeText(get_mActivity(), mode, Toast.LENGTH_SHORT).show();
            }
        };
        MusicManager.getInstance().addPlayerEventListener(onPlayerEventListener);
    }

    private void initViewPager() {
        mFirstDrawable = iv_bg_detail.getBackground();
        if ((coverFragment = (CoverFragment) getChildFragmentManager().findFragmentByTag(CoverFragment.COVERFRAGMENT_TAG)) == null) {
            coverFragment = CoverFragment.newInstance(currentRotation);
        } else {
            Bundle coverArgs = coverFragment.getArguments() == null ? new Bundle() : coverFragment.getArguments();
            coverArgs.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, currentRotation);
            coverFragment.setArguments(coverArgs);
        }
        if ((lyricFragment = (LyricFragment) getChildFragmentManager().findFragmentByTag(LyricFragment.COVERFRAGMENT_TAG)) == null) {
            lyricFragment = LyricFragment.newInstance(currentMusicEntity.getId());
        } else {
            Bundle lyricArgs = lyricFragment.getArguments() == null ? new Bundle() : lyricFragment.getArguments();
            lyricArgs.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, currentMusicEntity.getId());
            lyricFragment.setArguments(lyricArgs);
        }
        Fragment[] fragments = new Fragment[]{coverFragment, lyricFragment};
        viewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }
        });
        viewPager.setPageTransformer(false, new DepthPageTransformer());
        viewPager.setOffscreenPageLimit(2);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    leftTv.setChecked(true);
                    rightTv.setChecked(false);
                } else if (position == 1) {
                    leftTv.setChecked(false);
                    rightTv.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 图片渐变切换动画
     */
    public void startChangeAnimation(ImageView imageView, Drawable bitmapDrawable) {
        Drawable oldDrawable = imageView.getDrawable();
        Drawable oldBitmapDrawable = null;
        if (oldDrawable == null) {
            oldBitmapDrawable = new ColorDrawable(Color.TRANSPARENT);
        } else if (oldDrawable instanceof TransitionDrawable) {
            oldBitmapDrawable = ((TransitionDrawable) oldDrawable).getDrawable(1);
        } else {
            oldBitmapDrawable = oldDrawable;
        }
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                oldBitmapDrawable,
                bitmapDrawable
        });
        imageView.setImageDrawable(td);
        td.startTransition(1000);
    }



    private void getCoverBitmap() {
        new Thread(() -> {
            Target<Bitmap> target = Glide.with(get_mActivity())
                    .asBitmap()
                    .load(currentMusicEntity.getCover() != null ? currentMusicEntity.getCover() : R.drawable.baidu)
                    .error(R.drawable.baidu)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            myRunnable = new MyRunnable(resource);
                            if (iv_bg_detail != null) {
                                iv_bg_detail.post(myRunnable);
                            }
                        }
                    });

        }).start();
    }

    private void showStopped() {
        showPaused();
        iv_bg_detail.setBackground(mFirstDrawable);
        tv_detail_music_name.setText("歌曲");
        tv_singer_name.setText("- 歌手 -");
        sb_progress.setProgress(0);
        if (coverFragment != null) {
            coverFragment.setCoverRotation(0f);
            coverFragment.setCoverDrawable(getActivity().getDrawable(R.drawable.diskte));
        }
        if (lyricFragment != null) {
//            lyricFragment.setLyric();
        }
    }

    private void showPaused() {
        iv_play_pause.setImageResource(R.drawable.ic_detail_play);
        pauseAnimation();
    }

    private void showPlaying(MusicEntity musicEntity, boolean isPlayStart, boolean isMusicSwitch) {
        if (musicEntity != null) {
            if (isMusicSwitch) {
                currentMusicEntity = musicEntity;
                tv_detail_music_name.setText(musicEntity.getName());
                tv_singer_name.setText(musicEntity.getSingers().get(0).getName());
                getCoverBitmap();
                resetAnimation();
            }
            if (isPlayStart) {
                iv_play_pause.setImageResource(R.drawable.ic_detail_pause);
                playAnimation();
            }
        }
    }

    private void playAnimation() {
        if (coverFragment != null) {
            coverFragment.playAnimation();
        }
    }

    private void pauseAnimation() {
        if (coverFragment != null) {
            coverFragment.pauseAnimation();
        }
    }

    private void stopAnimation() {
        if (coverFragment != null) {
            coverFragment.stopAnimation();
        }
    }

    private void resetAnimation() {
        if (coverFragment != null) {
            coverFragment.resetAnimation();
        }
    }

    private DetailHandler handler = new DetailHandler(this);
    private static final int UPDATE_UI = 123;

    public static class DetailHandler extends Handler {
        private WeakReference<DetailFragment> weakReference;

        public DetailHandler(DetailFragment detailFragment) {
            weakReference = new WeakReference<>(detailFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            DetailFragment detailFragment = weakReference.get();
            switch (msg.what) {
                case UPDATE_UI:
                    if (detailFragment != null) {
                    }
            }
            super.handleMessage(msg);
        }
    }

    public class MyRunnable implements Runnable {

        private Bitmap resource;

        MyRunnable(Bitmap resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            currentMusicEntity.setCoverBitmap(resource);
            coverFragment.setCoverBitmap(resource);
            Drawable blur = BitmapUtils.createBlurredImageFromBitmap(resource, 12);
            iv_bg_detail.setImageDrawable(blur);
        }
    }

    private void playMusic() {
        MusicManager.getInstance().playMusic();
    }

    private void pauseMusic() {
        MusicManager.getInstance().pauseMusic();
    }

    private void showQueueDialog() {
        if (queueDialog == null) {
            queueDialog = new QueueDialog(getActivity());
        }
        queueDialog.show();
    }

    private void nextMode() {
        switch (MusicManager.getInstance().getRepeatMode()) {
            case PlaybackStateCompat.REPEAT_MODE_NONE:
                MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE);
                iv_playmode.setImageDrawable(play_mode_drawable[1]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ONE:
                MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL);
                iv_playmode.setImageDrawable(play_mode_drawable[2]);
                break;
            case PlaybackStateCompat.REPEAT_MODE_ALL:
                MusicManager.getInstance().setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE);
                iv_playmode.setImageDrawable(play_mode_drawable[0]);
                break;
        }
        if (queueDialog != null) {
            queueDialog.nextMode();
        }

    }

    private void hideQueueDialog() {
        if (queueDialog != null) {
            queueDialog.hide();
        }
    }

    @Override
    public void onDestroyView() {
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
        if (myRunnable != null) {
            iv_bg_detail.removeCallbacks(myRunnable);
        }
        stopAnimation();
        if (queueDialog != null) {
            queueDialog.dismiss();
        }
        mTimeTaskManager.removeUpdateProgressTask();
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onBackPressedSupport() {
        return super.onBackPressedSupport();
    }


}
