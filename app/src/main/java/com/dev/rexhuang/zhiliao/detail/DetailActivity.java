package com.dev.rexhuang.zhiliao.detail;

import android.content.Intent;
import android.os.Bundle;

import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;

public class DetailActivity extends ZhiliaoActivity {

    private DetailFragment detailFragment;

    @Override
    public void loadContainerFragment(ZhiliaoFragment zhiliaoFragment) {
        getSupportDelegate().loadRootFragment(R.id.container, zhiliaoFragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initDetailFragment();
        loadContainerFragment(detailFragment);
    }

    private void initDetailFragment() {
        Intent intent = getIntent();
        String musicId = intent.getStringExtra(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
        float rotation = intent.getFloatExtra(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, 0f);
        if (findFragment(DetailFragment.class) == null) {
            detailFragment = DetailFragment.newInstance(musicId, rotation, null);
        } else {
            Bundle args = detailFragment.getArguments();
            if (args == null) {
                args = new Bundle();
            }
            args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicId);
            args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, rotation);
            detailFragment.setArguments(args);
        }
    }


}
