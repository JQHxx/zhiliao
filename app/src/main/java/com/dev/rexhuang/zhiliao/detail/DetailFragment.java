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
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.QueueDialogHelper;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.TimeHelper;
import com.dev.rexhuang.zhiliao.TimerTaskManager;
import com.dev.rexhuang.zhiliao.find.queue.QueueAdapter;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.bean.Music;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.orhanobut.logger.Logger;
import com.youth.banner.transformer.DepthPageTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/9/6
 */
public class DetailFragment extends ZhiliaoFragment {

    private static final String TAG = DetailFragment.class.getSimpleName();
    private MyRunnable myRunnable;
    private Bundle args;
    private float currentRotation;
    private MusicEntity currentMusicEntity;
    private OnPlayerEventListener onPlayerEventListener;

    private CoverFragment coverFragment;
    private LyricFragment lyricFragment;
    private TimerTaskManager mTimeTaskManager;

    //PlayQueue
    private BottomSheetDialog queueDialog;
    //    private Dialog queueDialog;
    private RecyclerView recyclerView;
    private QueueAdapter queueAdapter;
    private TextView tv_close;

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

    @OnClick(R.id.iv_detail_back)
    void onClickBack() {
//        onBackPressedSupport();
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

    @Override
    public Object setLayout() {
        return R.layout.fragment_detail;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
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

        initViewPager();
        initPlayEventListener();
        initProgressBar();
        if (MusicManager.getInstance().isPlaying()) {
            showPlaying(currentMusicEntity, true, false);
            mTimeTaskManager.startToUpdateProgress();
        }
    }

    private void initProgressBar() {
        sb_progress.setProgressDrawable(getActivity().getDrawable(R.drawable.progress));
        setProgressBar();
        mTimeTaskManager = new TimerTaskManager();
        mTimeTaskManager.setUpdateProgressTask(new Runnable() {
            @Override
            public void run() {
                setProgressBar();
                setLyric();
            }
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
            public void onMusicSwitch(MusicEntity musicEntity) {
                if (musicEntity != null) {
                    showPlaying(musicEntity, false, true);
                    if (queueAdapter != null) {
                        queueAdapter.notifyDataSetChanged();
                    }
                    lyricFragment.setLyric(true);
                }
            }

            @Override
            public void onPlayerStart() {
//                iv_play_pause.setImageResource(R.drawable.ic_detail_pause);
//                playAnimation();
                showPlaying(MusicManager.getInstance().getNowPlayingSongInfo(), true, false);
                mTimeTaskManager.startToUpdateProgress();
            }

            @Override
            public void onPlayerPause() {
                showStopped();
                mTimeTaskManager.stopToUpdateProgress();
            }

            @Override
            public void onPlayerStop() {
                showStopped();
                mTimeTaskManager.stopToUpdateProgress();
            }

            @Override
            public void onPlayCompletion(MusicEntity musicEntity) {
                showStopped();
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
        };
        MusicManager.getInstance().addPlayerEventListener(onPlayerEventListener);
    }

    private void initViewPager() {
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
     *
     * @param imageView
     * @param bitmapDrawable
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

    /**
     * @param inSampleSize 图片像素的 1/n*n
     */
    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, int inSampleSize) {

        RenderScript rs = RenderScript.create(Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name()));
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final Allocation input = Allocation.createFromBitmap(rs, blurTemplate);
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(24f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);
        rs.destroy();

        return new BitmapDrawable(((Context) Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name())).getResources(), blurTemplate);
    }

    private void getCoverBitmap() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Target<Bitmap> target = Glide.with(get_mActivity())
                        .asBitmap()
                        .load(currentMusicEntity.getCover() != null ? currentMusicEntity.getCover() : R.drawable.baidu)
                        .error(R.drawable.baidu)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                handler.sendEmptyMessage()
                                myRunnable = new MyRunnable(resource);
                                if (iv_bg_detail != null) {
                                    iv_bg_detail.post(myRunnable);
                                }
                            }
                        });

            }
        }).start();
    }

    private void showStopped() {
        iv_play_pause.setImageResource(R.drawable.ic_detail_play);
        pauseAnimation();
    }

    private void showPlaying(MusicEntity musicEntity, boolean isPlayStart, boolean isNeedResetAnim) {
        if (musicEntity != null) {
            currentMusicEntity = musicEntity;
            tv_detail_music_name.setText(musicEntity.getName());
            tv_singer_name.setText(musicEntity.getSingers().get(0).getName());
            getCoverBitmap();
            if (isNeedResetAnim) {
                resetAnimation();
            }
            if (isPlayStart) {
                iv_play_pause.setImageResource(R.drawable.ic_detail_pause);
                playAnimation();
            }
        }
    }

    private void playAnimation() {
        coverFragment.playAnimation();
    }

    private void pauseAnimation() {
        coverFragment.pauseAnimation();
    }

    private void stopAnimation() {
        coverFragment.stopAnimation();
    }

    private void resetAnimation() {
        coverFragment.resetAnimation();
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
//                        detailFragment.currentMusicEntity.setCoverBitmap(resource);
//                        detailFragment.coverFragment.setCoverBitmap(resource);
//                        Drawable blur = createBlurredImageFromBitmap(resource, 12);
//                        detailFragment.iv_bg_detail.setImageDrawable(blur);
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
            Drawable blur = createBlurredImageFromBitmap(resource, 12);
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
            queueAdapter = new QueueAdapter(R.layout.item_queue, MusicManager.getInstance().getPlayList());
            queueDialog = QueueDialogHelper.createQueueDialog(getActivity());
            tv_close = queueDialog.findViewById(R.id.tv_close);
            recyclerView = queueDialog.findViewById(R.id.rcv_songs);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(queueAdapter);
            tv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideQueueDialog();
                }
            });
            queueAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    MusicManager.getInstance().playMusicByIndex(position);

                }
            });
            queueAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    if (view.getId() == R.id.iv_more) {
                    }
                }
            });
        }
        if (MusicManager.getInstance().getNowPlayingIndex() >= 0) {
            recyclerView.scrollToPosition(MusicManager.getInstance().getNowPlayingIndex());
        }
        queueDialog.show();
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
