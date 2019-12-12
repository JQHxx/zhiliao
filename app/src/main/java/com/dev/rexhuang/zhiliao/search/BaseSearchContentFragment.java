package com.dev.rexhuang.zhiliao.search;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;

/**
 * *  created by RexHuang
 * *  on 2019/12/11
 */
public abstract class BaseSearchContentFragment extends Fragment {

    protected String mCurrentQuery;

    protected View mHeaderView;

    protected IRequest request;

    protected abstract void showSearchContent(String query);

    protected abstract void setHeaderView();

    protected abstract void setRequest();

    protected View getHeaderView() {
        if (mHeaderView == null) {
            throw new IllegalStateException("HeaderView must not be null! ");
        }
        return mHeaderView;
    }

    protected IRequest getRequest() {

        return request;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderView();
        setRequest();
    }
}
