package com.dev.rexhuang.zhiliao.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.event.MessageEvent;
import com.dev.rexhuang.zhiliao.music_hall.MusicHallFragment;
import com.dev.rexhuang.zhiliao_core.api.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.entity.LyricEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.wcy.lrcview.LrcView;

/**
 * *  created by RexHuang
 * *  on 2019/9/16
 */
public class LyricFragment extends Fragment {

    public static final String COVERFRAGMENT_TAG = "LyricFragment_tag";

    private Unbinder mUnbinder;
    @BindView(R.id.lrcView)
    LrcView lrcView;
    private Bundle args;

    public static LyricFragment newInstance(String musicId) {
        LyricFragment lyricFragment = new LyricFragment();
        Bundle args = lyricFragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicId);
        lyricFragment.setArguments(args);
        return lyricFragment;
    }

    public Object setLayout() {
        return R.layout.fragment_detail_lyric;
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
        EventBus.getDefault().register(this);
        args = getArguments();
        initLrvView();

    }

    private void initLrvView() {
        lrcView.setNormalColor(R.color.white);
//        lrcView.setCurrentColor(R.color.switch_selected);
        lrcView.setDraggable(true, new LrcView.OnPlayClickListener() {
            @Override
            public boolean onPlayClick(long time) {
                MusicManager.getInstance().seekTo(time);
                return true;
            }
        });
        if (args != null) {
            getLyric(args.getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION));
        }
    }

    private void getLyric(String musicId) {
        ZhiliaoApi.getlyric(MusicHallFragment.TOKEN, musicId, null,
                new ISuccess<LyricEntity>() {
                    @Override
                    public void onSuccess(LyricEntity response) {
                        EventBus.getDefault().post(new MessageEvent(response.getData()));
                    }
                }, null, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        lrcView.loadLrc(event.data);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    public void setLyric(boolean isChangeLyc) {
        if (isChangeLyc) {
            getLyric(MusicManager.getInstance().getNowPlayingSongId());
        }
        long position = MusicManager.getInstance().getPlayingPosition();
        if (lrcView != null) {
            lrcView.updateTime(position);
        }
    }
}
