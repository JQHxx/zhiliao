package com.dev.rexhuang.zhiliao.detail;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.utils.AnimHelper;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * *  created by RexHuang
 * *  on 2019/9/6
 */
public class CoverFragment extends Fragment {

    public static final String COVERFRAGMENT_TAG = "CoverFragment_tag";
    private static final String TAG = CoverFragment.class.getSimpleName();

    //AnimatorSet
    private ObjectAnimator cover_play;

    private Bundle args;
    private Unbinder mUnbinder;
    @BindView(R.id.civ_cover)
    CircleImageView civ_cover;

    public static CoverFragment newInstance(float rotation) {
        CoverFragment coverFragment = new CoverFragment();
        Bundle args = coverFragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, rotation);
        coverFragment.setArguments(args);
        return coverFragment;
    }

    public Object setLayout() {
        return R.layout.fragment_detail_cover;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view;
        if (setLayout() instanceof Integer) {
            view = inflater.inflate((Integer) setLayout(), container, false);
        } else if (setLayout() instanceof View) {
            view = (View) setLayout();
        } else {
            throw new ClassCastException("type of setLayout() must be int or View!");
        }

        if (view != null) {
            mUnbinder = ButterKnife.bind(this, view);
            onBindView(savedInstanceState, view);
        }
        return view;
    }

    public void onBindView(Bundle savedInstanceState, View view) {
        cover_play = AnimHelper.rotate(civ_cover, "rotation", AnimHelper.DEFAULT_START_ROTATE,
                AnimHelper.DEFAULT_END_ROTATE, AnimHelper.DEFAULT_DURATION,
                ValueAnimator.INFINITE, ValueAnimator.RESTART);
        args = getArguments();
        if (args != null) {
            Float rotation = args.getFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION);
            setCoverRotation(rotation);
        }
        if (MusicManager.getInstance().isPlaying()) {
            playAnimation();
        }
    }

    public void setCoverBitmap(Bitmap bitmap) {
        civ_cover.setImageBitmap(bitmap);
    }

    public void setCoverRotation(float rotation) {
        civ_cover.setRotation(rotation);
        cover_play = AnimHelper.rotate(civ_cover, "rotation", rotation,
                rotation + AnimHelper.DEFAULT_END_ROTATE, AnimHelper.DEFAULT_DURATION,
                ValueAnimator.INFINITE, ValueAnimator.RESTART);
    }

    public void playAnimation() {
        Logger.d("playAnimation");
        if (cover_play != null) {
            if (!cover_play.isStarted()) {
                cover_play.start();
            } else if (cover_play.isPaused()) {
                cover_play.resume();
            }
        }
    }

    public void pauseAnimation() {
        if (cover_play != null) {
            if (cover_play.isRunning()) {
                cover_play.pause();
            }
        }
    }

    public void stopAnimation() {
        if (cover_play != null) {
            cover_play.cancel();
        }
    }

    public void resetAnimation() {
        stopAnimation();
        cover_play = AnimHelper.rotate(civ_cover, "rotation", AnimHelper.DEFAULT_START_ROTATE,
                AnimHelper.DEFAULT_END_ROTATE, AnimHelper.DEFAULT_DURATION,
                ValueAnimator.INFINITE, ValueAnimator.RESTART);
    }

    @Override
    public void onDestroyView() {
        Logger.t(TAG).d("onDestroyView");
        stopAnimation();
        super.onDestroyView();

    }
}
