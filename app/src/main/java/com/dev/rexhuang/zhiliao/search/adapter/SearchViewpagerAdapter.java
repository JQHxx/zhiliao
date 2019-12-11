package com.dev.rexhuang.zhiliao.search.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.dev.rexhuang.zhiliao.search.BaseSearchContentFragment;

import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/12/11
 */
public class SearchViewpagerAdapter extends FragmentPagerAdapter {

    List<BaseSearchContentFragment> mPages;
    List<String> mTitles;

    public SearchViewpagerAdapter(FragmentManager fm, List<BaseSearchContentFragment> mPages,List<String> mTitles) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
        this.mPages = mPages;
        this.mTitles = mTitles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mPages.get(position);
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
