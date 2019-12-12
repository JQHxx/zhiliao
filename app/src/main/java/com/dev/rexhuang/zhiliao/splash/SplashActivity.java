package com.dev.rexhuang.zhiliao.splash;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.dev.rexhuang.zhiliao.MainActivity;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.login.LoginActivity;
import com.dev.rexhuang.zhiliao.login.UserManager;
import com.dev.rexhuang.zhiliao.timer.ISplashListener;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class SplashActivity extends ZhiliaoActivity implements ISplashListener {
    private FrameLayout container;
    private RxPermissions rxPermissions;
    //需要检查的权限
    private final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //获取电话状态
            Manifest.permission.READ_PHONE_STATE
//            Manifest.permission.CAMERA
    };

    @Override
    public void loadContainerFragment(ZhiliaoFragment fragment) {
        getSupportDelegate().loadRootFragment(R.id.container, fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = findViewById(R.id.container);
        checkPermission();
    }

    @SuppressLint("CheckResult")
    @Override
    public void checkPermission() {
        if (rxPermissions == null) {
            rxPermissions = new RxPermissions(this);
        }
        if (Utils.isMarshmallow()) {
            rxPermissions.request(mPermissionList)
                    .subscribe(granted -> {
                        if (granted) {
                            loadContainerFragment(
                                    new SplashFragment().withISplashListener(SplashActivity.this));
                        } else {
                            Snackbar.make(container, getResources().getString(R.string.permission_hint), Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getResources().getString(R.string.sure), view -> checkPermission()).show();
                        }
                    });
        }
    }

    @Override
    public void onSplashEnd() {
        Intent mainIntent = new Intent();
        if (UserManager.getInstance().isLogined()) {
            mainIntent.setComponent(new ComponentName(SplashActivity.this, MainActivity.class));
        } else {
            mainIntent.setComponent(new ComponentName(SplashActivity.this, LoginActivity.class));
        }
        startActivity(mainIntent);
        finish();
    }

}
