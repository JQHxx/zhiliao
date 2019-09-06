package com.dev.rexhuang.zhiliao_core.detail;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.R2;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * *  created by RexHuang
 * *  on 2019/9/6
 */
public class CoverFragment extends Fragment {

    private Unbinder mUnbinder;
    @BindView(R2.id.civ_cover)
    CircleImageView civ_cover;
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
        if (getArguments() != null) {
            String musicId = getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION) == null ?
                    MusicManager.getInstance().getNowPlayingSongId() :
                    getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            Float rotation = getArguments().getFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION);
            MusicEntity musicEntity = MusicProvider.getInstance().getMusicEntity(musicId);
            if (musicEntity != null) {
            }
        }
    }

    public void setCoverBitmap(Bitmap bitmap){
        civ_cover.setImageBitmap(bitmap);
    }
}
