package com.dev.rexhuang.zhiliao_core.player2;

import com.orhanobut.logger.Logger;

/**
 * *  created by RexHuang
 * *  on 2019/8/13
 */
public class PlayState {
    private static final String TAG = PlayState.class.getSimpleName();
    public static final int NONE = 0;
    public static final int PLAYING = 1;
    public static final int NOT_PLAYING = 2;
    public static int PLAYSTATE = 0;

    public static boolean isPlaying() {
        Logger.t(TAG).d("" + PlayState.PLAYSTATE);
        return PLAYSTATE == PLAYING ? Boolean.TRUE : Boolean.FALSE;
    }
}
