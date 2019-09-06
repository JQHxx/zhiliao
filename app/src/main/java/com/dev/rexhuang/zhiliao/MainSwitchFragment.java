package com.dev.rexhuang.zhiliao;

import android.os.Bundle;
import android.view.View;

import com.dev.rexhuang.zhiliao_core.base.BaseFragment;
import com.dev.rexhuang.zhiliao_core.base.FragmentKeys;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoSwitchFragment;
import com.dev.rexhuang.zhiliao_core.callback.SwitchFragmentListener;
import com.dev.rexhuang.zhiliao_core.find.FindFragment;
import com.dev.rexhuang.zhiliao_core.music_hall.MusicHallFragment;
import com.dev.rexhuang.zhiliao_core.profile.ProfileFragment;
import com.dev.rexhuang.zhiliao_core.recommend.RecommendFragment;

import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class MainSwitchFragment extends ZhiliaoSwitchFragment implements SwitchFragmentListener {

    private static final LinkedList<String> NAMES = new LinkedList<>();
    private String mediaId;
    private int mShowPosition = 0;

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        if (getArguments() != null) {
            mediaId = (String) getArguments().get(MainActivity.MEDIA_ID_KEY);
        }
        setISwitchFragmentListener(this);
        super.onBindView(savedInstanceState, view);
    }

    @Override
    public void setFragments() {
        FRAGMENTS.put(FragmentKeys.MUSIC_HALL.name(), new MusicHallFragment());
        FRAGMENTS.put(FragmentKeys.RECOMMEND.name(), new RecommendFragment());
        FRAGMENTS.put(FragmentKeys.FIND.name(), new FindFragment(mediaId));
        FRAGMENTS.put(FragmentKeys.PROFILE.name(), new ProfileFragment());
        NAMES.add("音乐馆");
        NAMES.add("推荐");
        NAMES.add("发现");
        NAMES.add("我的");
        int index = 0;
        for (Map.Entry<String, BaseFragment> entry : FRAGMENTS.entrySet()) {
            mFragments[index] = entry.getValue();
            index++;
        }
    }

    @Override
    public void loadFragments(int containerId) {
        getSupportDelegate().loadMultipleRootFragment(containerId, mShowPosition, mFragments);
    }

    @Override
    public void onSwitchEnd() {
        mTitleTv.setText((String) Objects.requireNonNull(NAMES.toArray())[getCurrentFragment()]);
        final int mPreviousIndex = getPreviousFragment();
        final int mCurrentIndex = getCurrentFragment();
        sw_ivs[mPreviousIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_unselected));
        sw_tvs[mPreviousIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_unselected));
        sw_ivs[mCurrentIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_selected));
        sw_tvs[mCurrentIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_selected));
    }
}
