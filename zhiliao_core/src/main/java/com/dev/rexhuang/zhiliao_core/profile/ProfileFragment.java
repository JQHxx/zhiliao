package com.dev.rexhuang.zhiliao_core.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoMainFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class ProfileFragment extends ZhiliaoMainFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public Object setLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {

    }
}
