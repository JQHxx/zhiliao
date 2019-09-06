package com.dev.rexhuang.zhiliao_core.base;

import android.widget.Toast;

/**
 * *  created by RexHuang
 * *  on 2019/8/26
 */
public abstract class ZhiliaoMainFragment extends ZhiliaoFragment {
    //再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;


    @Override
    public boolean onBackPressedSupport() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            _mActivity.finish();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            Toast.makeText(_mActivity, "再按一次退出应用", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
