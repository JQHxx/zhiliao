package com.dev.rexhuang.zhiliao.splash;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import com.dev.rexhuang.zhiliao.MainActivity;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.login.LoginActivity;
import com.dev.rexhuang.zhiliao.login.UserManager;
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
//        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
        Intent mainIntent = new Intent();
        if (UserManager.getInstance().isLogin()) {
            mainIntent.setComponent(new ComponentName(SplashActivity.this, MainActivity.class));
        } else {
            mainIntent.setComponent(new ComponentName(SplashActivity.this, LoginActivity.class));
        }
        startActivity(mainIntent);
        this.finish();
    }

}
