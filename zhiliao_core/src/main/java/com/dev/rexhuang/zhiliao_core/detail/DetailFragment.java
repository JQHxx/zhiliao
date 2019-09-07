package com.dev.rexhuang.zhiliao_core.detail;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.CheckedTextView;
import android.widget.ImageView;

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
import com.bumptech.glide.request.transition.Transition;
import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.R2;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;
import com.dev.rexhuang.zhiliao_core.search.SearchFragment;
import com.google.android.exoplayer2.C;
import com.youth.banner.transformer.DepthPageTransformer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/9/6
 */
public class DetailFragment extends ZhiliaoFragment {

    CoverFragment coverFragment;
    @BindView(R2.id.iv_bg_detail)
    AppCompatImageView iv_bg_detail;

    @BindView(R2.id.tv_detail_music_name)
    AppCompatTextView tv_detail_music_name;

    @BindView(R2.id.tv_singer_name)
    AppCompatTextView tv_singer_name;

    @BindView(R2.id.viewPager)
    ViewPager viewPager;

    @BindView(R2.id.rightTv)
    CheckedTextView rightTv;

    @BindView(R2.id.leftTv)
    CheckedTextView leftTv;

    @OnClick(R2.id.iv_detail_back)
    void onClickBack() {
        getSupportDelegate().pop();
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_detail;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        if (getArguments() != null) {
            String musicId = getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION) == null ?
                    MusicManager.getInstance().getNowPlayingSongId() :
                    getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            Float rotation = getArguments().getFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION);
            MusicEntity musicEntity = MusicProvider.getInstance().getMusicEntity(musicId);
            if (musicEntity != null) {
                tv_detail_music_name.setText(musicEntity.getName());
                tv_singer_name.setText(musicEntity.getSingers().get(0).getName());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(get_mActivity())
                                .asBitmap()
                                .load(musicEntity.getCover() != null ? musicEntity.getCover() : R.drawable.baidu)
                                .error(R.drawable.baidu)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(new SimpleTarget<Bitmap>() {

                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        iv_bg_detail.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                coverFragment.setCoverBitmap(resource);
                                                Drawable blur = createBlurredImageFromBitmap(resource, 12);
                                                iv_bg_detail.setImageDrawable(blur);
                                            }
                                        });
                                    }
                                });

                    }
                }).start();
            }
        }
//        if (getFragmentManager().findFragmentByTag())
        coverFragment = new CoverFragment();

        Bundle arg = coverFragment.getArguments();
//        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(getActivity());
//        MediaMetadataCompat metadata = controllerCompat.getMetadata();
        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
        if (musicEntity != null) {
            if (arg != null) {
                arg.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
//                arg.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
            } else {
                arg = new Bundle();
                arg.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
//                arg.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
            }
            coverFragment.setArguments(arg);
        }
        Fragment[] fragments = new Fragment[]{coverFragment};
        viewPager.setAdapter(new FragmentStatePagerAdapter(getFragmentManager()) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
