package com.dev.rexhuang.zhiliao;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import com.dev.rexhuang.zhiliao.timer.ISplashListener;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;

import butterknife.ButterKnife;

public class SplashActivity extends ZhiliaoActivity implements ISplashListener {
    @Override
    public void loadContainerFragment(ZhiliaoFragment fragment) {
        getSupportDelegate().loadRootFragment(R.id.container, fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadContainerFragment(new SplashFragment().withISplashListener(this));
        //ButterKnife
        ButterKnife.bind(this);
    }

    @Override
    public void onSplashEnd(Bundle savedInstanceState, Intent intent) {
        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(mainIntent);
        this.finish();
    }

}
