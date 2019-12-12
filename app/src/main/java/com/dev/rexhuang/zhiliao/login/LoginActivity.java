package com.dev.rexhuang.zhiliao.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dev.rexhuang.zhiliao.MainActivity;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;

/**
 * *  created by RexHuang
 * *  on 2019/9/20
 */
public class LoginActivity extends ZhiliaoActivity implements ILoginStatusListener {

    private LoginFragment loginFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginFragment = LoginFragment.newInstance().withLoginStatusListener(this);
        if (findFragment(LoginFragment.class) == null) {
            loadContainerFragment(loginFragment);
        }
    }

    @Override
    public void loadContainerFragment(ZhiliaoFragment loginFragment) {
        getSupportDelegate().loadRootFragment(R.id.container, loginFragment);
    }

    @Override
    public void onLogin() {
        startTomain();
    }

    private void startTomain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

}
