package com.dev.rexhuang.zhiliao;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.dev.rexhuang.zhiliao_core.base.ZhiliaoActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.player2.manager.MediaSessionConnection;

import butterknife.ButterKnife;

public class MainActivity extends ZhiliaoActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SAVED_MEDIA_ID = "com.dev.rexhuang.zhiliao.MEDIA_ID";
    public static final String MEDIA_ID_KEY = "mediaId";
    protected ZhiliaoSwitchFragment mZhiliaoSwitchFragment;

    private Bundle mSavedInstanceState;

    @Override
    public void loadContainerFragment(ZhiliaoFragment fragment) {
        getSupportDelegate().loadRootFragment(R.id.container, fragment);
    }

    public Bundle getSavedInstanceState() {
        return mSavedInstanceState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSavedInstanceState = savedInstanceState;
        if (mZhiliaoSwitchFragment == null) {
            mZhiliaoSwitchFragment = new MainSwitchFragment();
            String mediaId = initializeFromParams(mSavedInstanceState, getIntent());
            Bundle args = new Bundle();
            if (!TextUtils.isEmpty(mediaId)) {
                args.putString(MEDIA_ID_KEY, mediaId);
            }
            mZhiliaoSwitchFragment.setArguments(args);
            if (findFragment(MainSwitchFragment.class) == null) {
                loadContainerFragment(mZhiliaoSwitchFragment);
            }
        }
        //ButterKnife
        ButterKnife.bind(this);
        //Starrysky
        MediaSessionConnection.getInstance().connect();
//        切换主题
//        setTheme(R.style.AppTheme_Elegant_White);
    }

    private String initializeFromParams(Bundle savedInstanceState, Intent intent) {
        String mediaId = null;

        if (intent.getAction() != null && intent.getAction().equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {

        } else {
            if (savedInstanceState != null) {
                mediaId = savedInstanceState.getString(SAVED_MEDIA_ID);
            }
        }
        return mediaId;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaSessionConnection.getInstance().disconnect();
    }
}