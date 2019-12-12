package com.dev.rexhuang.zhiliao.splash;

import android.os.Bundle;
import android.view.View;

import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.timer.BaseTimerTask;
import com.dev.rexhuang.zhiliao.timer.ISplashListener;
import com.dev.rexhuang.zhiliao.timer.ITimerListener;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;

import java.text.MessageFormat;
import java.util.Timer;

import androidx.appcompat.widget.AppCompatTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class SplashFragment extends ZhiliaoFragment implements ITimerListener {

    @BindView(R.id.tv_spalsh_count)
    AppCompatTextView tv_splash;

    @OnClick(R.id.tv_spalsh_count)
    void onClickSplash() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        startToMain();
    }

    private Timer mTimer = null;
    private int mCount = 5;
    private ISplashListener mISplashListener;


    @Override
    public Object setLayout() {
        return R.layout.fragment_splash;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        initTimer();
    }

    private void initTimer() {
        mTimer = new Timer();
        final BaseTimerTask task = new BaseTimerTask(this);
        mTimer.schedule(task, 0, 1000);
    }

    @Override
    public void onTimer() {
        get_mActivity().runOnUiThread(() -> {
            if (tv_splash != null) {
                tv_splash.setText(MessageFormat.format("{0}s  跳过", mCount));
                mCount--;
                if (mCount < 0) {
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                        startToMain();
                    }
                }
            }
        });
    }

    private void startToMain() {
        if (mISplashListener != null) {
            mISplashListener.onSplashEnd();
        }
    }

    public SplashFragment withISplashListener(ISplashListener mISplashListener) {
        this.mISplashListener = mISplashListener;
        return this;
    }
}
