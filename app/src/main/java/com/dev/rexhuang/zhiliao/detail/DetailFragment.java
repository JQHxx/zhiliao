package com.dev.rexhuang.zhiliao.detail;

import android.animation.ObjectAnimator;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.dev.rexhuang.zhiliao.MainSwitchFragment;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.search.SearchFragment;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;
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

    @OnClick(R.id.iv_detail_back)
    void onClickBack() {
        Bundle args = getArguments();
        String from = "";
        if (args != null) {
            from = getArguments().getString(BaseActivity.FRGMENT_FROM);
        }
        if (TextUtils.equals(MainSwitchFragment.class.getSimpleName(), from)) {
            getSupportDelegate().popTo(MainSwitchFragment.class, false);
        } else if (TextUtils.equals(SearchFragment.class.getSimpleName(), from)) {
            getSupportDelegate().popTo(SearchFragment.class, false);
        } else {
            getSupportDelegate().pop();
        }
    }

    @OnClick(R.id.iv_bg_detail)
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

        if (MusicManager.getInstance().isPlaying()) {
            showPlaying(currentMusicEntity, true);
        }
    }

    private void initPlayEventListener() {
        onPlayerEventListener = new OnPlayerEventListener() {
            @Override
            public void onMusicSwitch(MusicEntity musicEntity) {
                if (musicEntity != null) {
                    showPlaying(musicEntity, false);
                }
            }

            @Override
            public void onPlayerStart() {
//                iv_play_pause.setImageResource(R.drawable.ic_detail_pause);
//                playAnimation();
                showPlaying(MusicManager.getInstance().getNowPlayingSongInfo(), true);
            }

            @Override
            public void onPlayerPause() {
                showStopped();
            }

            @Override
            public void onPlayerStop() {
                showStopped();
            }

            @Override
            public void onPlayCompletion(MusicEntity musicEntity) {
                showStopped();
            }

            @Override
            public void onBuffering() {
                Toast.makeText(get_mActivity(), "正在缓冲,请稍等!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Toast.makeText(get_mActivity(), "播放出错!!", Toast.LENGTH_SHORT).show();
            }
        };
        MusicManager.getInstance().addPlayerEventListener(onPlayerEventListener);
    }

    private void initViewPager() {
        if ((coverFragment = (CoverFragment) getChildFragmentManager().findFragmentByTag(CoverFragment.COVERFRAGMENT_TAG)) == null) {
            coverFragment = CoverFragment.newInstance(currentRotation);
        } else {
            Bundle args = coverFragment.getArguments() == null ? new Bundle() : coverFragment.getArguments();
            args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, currentRotation);
            coverFragment.setArguments(args);
        }
        Fragment[] fragments = new Fragment[]{coverFragment};
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
        viewPager.setOffscreenPageLimit(1);
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
        script.setRadius(10f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

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
                                iv_bg_detail.post(myRunnable);
                            }
                        });

            }
        }).start();
    }

    private void showStopped() {
        iv_play_pause.setImageResource(R.drawable.ic_detail_play);
        pauseAnimation();
    }

    private void showPlaying(MusicEntity musicEntity, boolean isPlayStart) {
        if (musicEntity != null) {
            currentMusicEntity = musicEntity;
            tv_detail_music_name.setText(musicEntity.getName());
            tv_singer_name.setText(musicEntity.getSingers().get(0).getName());
            getCoverBitmap();
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

    @Override
    public void onDestroyView() {
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
        if (myRunnable != null) {
            iv_bg_detail.removeCallbacks(myRunnable);
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
