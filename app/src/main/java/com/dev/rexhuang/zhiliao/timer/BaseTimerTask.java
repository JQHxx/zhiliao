package com.dev.rexhuang.zhiliao.timer;

import java.util.TimerTask;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class BaseTimerTask extends TimerTask {
    private ITimerListener mITimerListener;

    public BaseTimerTask(ITimerListener timerListener) {
        this.mITimerListener = timerListener;
    }

    @Override
    public void run() {
        if (mITimerListener != null) {
            mITimerListener.onTimer();
        }
    }
}
