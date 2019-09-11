package com.dev.rexhuang.zhiliao;

import android.os.Bundle;
import android.view.View;

import com.dev.rexhuang.zhiliao_core.base.BaseFragment;
import com.dev.rexhuang.zhiliao_core.base.FragmentKeys;
import com.dev.rexhuang.zhiliao.find.FindFragment;
import com.dev.rexhuang.zhiliao.music_hall.MusicHallFragment;
import com.dev.rexhuang.zhiliao.profile.ProfileFragment;
import com.dev.rexhuang.zhiliao.recommend.RecommendFragment;

import java.util.Map;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class MainSwitchFragment extends ZhiliaoSwitchFragment {

    private static final String TAG = MainSwitchFragment.class.getSimpleName();
    private String mediaId;

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
        getSupportDelegate().loadMultipleRootFragment(containerId, getCurrentFragment(), mFragments);
    }

}
