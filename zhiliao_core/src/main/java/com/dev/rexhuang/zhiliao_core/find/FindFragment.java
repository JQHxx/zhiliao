package com.dev.rexhuang.zhiliao_core.find;

import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.R2;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoMainFragment;
import com.dev.rexhuang.zhiliao_core.callback.SwitchMediaFragmentListener;
import com.dev.rexhuang.zhiliao_core.find.adapter.SearchAdapter;
import com.dev.rexhuang.zhiliao_core.player2.PlayState;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

//import com.dev.rexhuang.zhiliao_core.R;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class FindFragment extends ZhiliaoMainFragment {

    private static final String TAG = FindFragment.class.getSimpleName();
    private String mMediaId;
    private SearchAdapter mSearchAdapter;
    private String[] testMode = new String[]{
            "顺序播放", "随机播放", "列表循环"
    };
    private int modeIndex = 0;

    @BindView(R2.id.next_btn)
    AppCompatButton next_btn;
    @BindView(R2.id.previous_btn)
    AppCompatButton previous_btn;
    @BindView(R2.id.test_mode_btn)
    AppCompatButton test_mode_btn;
    @BindView(R2.id.test_btn)
    AppCompatButton test_btn;
    @BindView(R2.id.list_search)
    RecyclerView rv_search;

    @OnClick(R2.id.test_btn)
    void onClick() {
        test_btn.setText(mMediaId);
    }

    @OnClick(R2.id.next_btn)
    void onClickNext() {
        QueueManager.getInstance().next();
    }

    @OnClick(R2.id.previous_btn)
    void onClickPre() {
        QueueManager.getInstance().previous();
    }

    @OnClick(R2.id.test_mode_btn)
    void onClickMode() {
        modeIndex = modeIndex < 2 ? modeIndex + 1 : 0;
        test_mode_btn.setText(testMode[modeIndex]);
        QueueManager.getInstance().setPlayMode(modeIndex);

    }

    public FindFragment(String mediaId) {
        super();
        mMediaId = mediaId;
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_find;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        mSearchAdapter = new SearchAdapter(R.layout.fragment_media_list_item, new ArrayList<>());
        mSearchAdapter.setOnItemClickListener((adapter, view1, position) -> {

        });
        rv_search.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        rv_search.setAdapter(mSearchAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
