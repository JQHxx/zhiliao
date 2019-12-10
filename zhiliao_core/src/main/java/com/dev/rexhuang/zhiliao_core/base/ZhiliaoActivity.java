package com.dev.rexhuang.zhiliao_core.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public abstract class ZhiliaoActivity extends PermissionCheckActivity {

    public abstract void loadContainerFragment(ZhiliaoFragment zhiliaoFragment);

//    public abstract void setImmersionBar(View titleBar, int colorId);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏
//        ImmersionBar.with(this).init();
    }


}
