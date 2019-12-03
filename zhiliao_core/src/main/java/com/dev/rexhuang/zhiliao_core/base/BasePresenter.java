package com.dev.rexhuang.zhiliao_core.base;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

/**
 * *  created by RexHuang
 * *  on 2019/9/28
 */
public class BasePresenter<T> {
    private Reference<T> mViewRef;

    public void attachView(T mView) {
        if (mViewRef == null) {
            mViewRef = new SoftReference<>(mView);
        }
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
        }
    }


}
