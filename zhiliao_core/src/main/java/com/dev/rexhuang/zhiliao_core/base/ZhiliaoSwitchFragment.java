package com.dev.rexhuang.zhiliao_core.base;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.R2;
import com.dev.rexhuang.zhiliao_core.callback.SwitchFragmentListener;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.detail.DetailFragment;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.find.queue.QueueAdapter;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;
import com.dev.rexhuang.zhiliao_core.search.SearchFragment;
import com.mikepenz.iconics.view.IconicsTextView;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public abstract class ZhiliaoSwitchFragment extends ZhiliaoFragment {

    private static final String TAG = ZhiliaoSwitchFragment.class.getSimpleName();
    protected static final LinkedHashMap<String, BaseFragment> FRAGMENTS = new LinkedHashMap<>();
    protected static final BaseFragment[] mFragments = new BaseFragment[4];
    private int mCurrentFragment;
    private int mPreviousFragment;
    private SwitchFragmentListener mSwitchFragmentListener;
    private String play = "{faw-play}";
    private String pause = "{faw-pause}";
    private Animation rotate;
    private ObjectAnimator objectAnimator;
    private QueueAdapter queueAdapter;
    private RecyclerView recyclerView;
    private QueueManager queueManager = QueueManager.getInstance();
    private Dialog dialog;
    private OnPlayerEventListener onPlayerEventListener;
    private SwitchHandler handler = new SwitchHandler(this);
    private static final int UPDATE_QUEUE = 102000000;

    @BindView(R2.id.search_bar)
    LinearLayout search_bar;

    @BindView(R2.id.song_cover)
    AppCompatImageView song_cover;

    @BindView(R2.id.song_description)
    AppCompatTextView song_description;

    @BindView(R2.id.song_play_button)
    IconicsTextView song_play_button;

    @BindView(R2.id.song_list_button)
    IconicsTextView song_list_button;

    @OnClick(R2.id.song_list_button)
    void onClickList() {
        //1、使用Dialog、设置style
        dialog = new Dialog(get_mActivity(), R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(get_mActivity(), R.layout.dialog_playqueue, null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        final float scale = ((Context) Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name())).getResources().getDisplayMetrics().density;
        int height = (int) (400 * scale + 0.5f);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
        dialog.show();

        recyclerView = dialog.findViewById(R.id.rcv_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        queueAdapter = new QueueAdapter(R.layout.item_queue, MusicManager.getInstance().getPlayList());
        recyclerView.setAdapter(queueAdapter);
        queueAdapter.bindToRecyclerView(recyclerView);
        recyclerView.scrollToPosition(MusicManager.getInstance().getNowPlayingIndex());
//        queueAdapter = new QueueAdapter(R.layout.item_queue, queueManager.getPlayingQueue());
        queueAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MusicManager.getInstance().playMusicByIndex(position);
            }
        });
        queueAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_more) {
//                    MusicEntity item = MusicManager.getInstance().getPlayList().get(position);
//                    MusicProvider.getInstance().
//                    queueAdapter.notifyItemRemoved(position);
                }
            }
        });

        dialog.show();
    }

    @OnClick(R2.id.song_play_button)
    void onClickPlay() {
        Logger.d(MusicManager.getInstance().isPlaying());
        if (MusicManager.getInstance().isPlaying()) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    @OnClick(R2.id.search_bar)
    void onClickSearch() {
        SearchFragment searchFragment = new SearchFragment();
        Bundle arg = searchFragment.getArguments();
        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
        if (musicEntity != null) {
            if (arg != null) {
                arg.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
                arg.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
            } else {
                arg = new Bundle();
                arg.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
                arg.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
            }
            searchFragment.setArguments(arg);
        }
        getSupportDelegate().start(searchFragment, ISupportFragment.SINGLETASK);
//        SearchFragment searchFragment = new SearchFragment();
//        FragmentTransaction transaction = get_mActivity().getSupportFragmentManager().beginTransaction();
//        transaction.add(R.id.container, searchFragment);
//        transaction.show(searchFragment);
//        transaction.hide(this);
//        transaction.commit();
//        Intent intent = new Intent("com.dev.rexHuang.Zhiliao.action.search");
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        get_mActivity().startActivity(intent);
    }

    @OnClick(R2.id.controlbar)
    void onClickControl() {
        DetailFragment detailFragment = new DetailFragment();
        Bundle arg = detailFragment.getArguments();
        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
        if (musicEntity != null) {
            if (arg != null) {
                arg.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
                arg.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
            } else {
                arg = new Bundle();
                arg.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
                arg.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
            }
            detailFragment.setArguments(arg);
        }
        getSupportDelegate().start(detailFragment, ISupportFragment.SINGLETASK);
    }

    @BindView(R2.id.title)
    protected AppCompatTextView mTitleTv;

    @BindViews({R2.id.iv_music_hall, R2.id.iv_recommend, R2.id.iv_find, R2.id.iv_profile})
    protected IconicsTextView[] sw_ivs;

    @BindViews({R2.id.tv_music_hall, R2.id.tv_recommend, R2.id.tv_find, R2.id.tv_profile})
    protected TextView[] sw_tvs;

    @OnClick(R2.id.layout_bottom_music_hall)
    void onClickMusicHall() {
        Toast.makeText(_mActivity, "onClickMusicHall", Toast.LENGTH_SHORT).show();
        getSupportDelegate().showHideFragment(FRAGMENTS.get(FragmentKeys.MUSIC_HALL.name())
                , mFragments[getCurrentFragment()]);
        setCurrentFragment(FragmentKeys.MUSIC_HALL.ordinal());
        if (mSwitchFragmentListener != null) {
            mSwitchFragmentListener.onSwitchEnd();
        }
    }

    @OnClick(R2.id.layout_bottom_recommend)
    void onClickRecommend() {
        Toast.makeText(_mActivity, "onClickRecommend", Toast.LENGTH_SHORT).show();
        getSupportDelegate().showHideFragment(FRAGMENTS.get(FragmentKeys.RECOMMEND.name())
                , mFragments[getCurrentFragment()]);
        setCurrentFragment(FragmentKeys.RECOMMEND.ordinal());
        if (mSwitchFragmentListener != null) {
            mSwitchFragmentListener.onSwitchEnd();
        }
    }

    @OnClick(R2.id.layout_bottom_find)
    void onClickFind() {
        Toast.makeText(_mActivity, "onClickFind", Toast.LENGTH_SHORT).show();
        getSupportDelegate().showHideFragment(FRAGMENTS.get(FragmentKeys.FIND.name())
                , mFragments[getCurrentFragment()]);
        setCurrentFragment(FragmentKeys.FIND.ordinal());
        if (mSwitchFragmentListener != null) {
            mSwitchFragmentListener.onSwitchEnd();
        }
    }

    @OnClick(R2.id.layout_bottom_profile)
    void onClickProfile() {
        Toast.makeText(_mActivity, "onClickProfile", Toast.LENGTH_SHORT).show();
        getSupportDelegate().showHideFragment(FRAGMENTS.get(FragmentKeys.PROFILE.name())
                , mFragments[getCurrentFragment()]);
        setCurrentFragment(FragmentKeys.PROFILE.ordinal());
        if (mSwitchFragmentListener != null) {
            mSwitchFragmentListener.onSwitchEnd();
        }
    }

    /**
     * data：setFragments
     */
    public abstract void setFragments();

    public abstract void loadFragments(int containerId);

    private void setCurrentFragment(int index) {
        this.mPreviousFragment = this.mCurrentFragment;
        this.mCurrentFragment = index;
    }

    protected int getCurrentFragment() {
        return this.mCurrentFragment;
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_zhiliao;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        setFragments();
        loadFragments(R.id.layout_container);
        setCurrentFragment(0);
        setPreviousFragment(0);
//        Glide.with(get_mActivity())
//                .load("https://static.mebtte.com/music_cover/e1ab493f2344148186eeda132278fbe4.jpeg")
//                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
//                .into(song_cover);
        rotate = AnimationUtils.loadAnimation(get_mActivity(), R.anim.rotate);
        rotate.setInterpolator(new LinearInterpolator());
        objectAnimator = ObjectAnimator.ofFloat(song_cover, "rotation", 0f, 360f).setDuration(10000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        onPlayerEventListener = new OnPlayerEventListener() {
            @Override
            public void onMusicSwitch(MusicEntity musicEntity) {
                showPlaying(musicEntity, false);
                if (queueAdapter != null) {
                    queueAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onPlayerStart() {
                song_play_button.setText(pause);
                playAnimation();
            }

            @Override
            public void onPlayerPause() {
                showStopped();
            }

            @Override
            public void onPlayerStop() {
                showStopped();
            }

            @Override
            public void onPlayCompletion(MusicEntity musicEntity) {
                showStopped();
            }

            @Override
            public void onBuffering() {
                Toast.makeText(get_mActivity(), "正在缓冲,请稍等!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Toast.makeText(get_mActivity(), "播放出错!!", Toast.LENGTH_SHORT).show();
            }
        };
        MusicManager.getInstance().addPlayerEventListener(onPlayerEventListener);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

//    @Override
//    public void onSupportVisible() {
//        super.onSupportVisible();
//        Logger.t(TAG).d("onSupportVisible");
////        MediaControllerCompat controller = MediaControllerCompat.getMediaController(get_mActivity());
//        if (mediaBrowser.isConnected()) {
//            onConnected();
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
    }

    public void setISwitchFragmentListener(SwitchFragmentListener mSwitchFragmentListener) {
        this.mSwitchFragmentListener = mSwitchFragmentListener;
    }

    public int getPreviousFragment() {
        return mPreviousFragment;
    }

    public void setPreviousFragment(int mPreviousFragment) {
        this.mPreviousFragment = mPreviousFragment;
    }


    private void showStopped() {
        song_play_button.setText(play);
        pauseAnimation();
    }

    protected void showPlaying(MusicEntity musicEntity, boolean isPlayStart) {
        if (musicEntity != null) {
            song_description.setText(String.format("%s - %s", musicEntity.getName(), musicEntity.getSingers().get(0).getName()));
            Glide.with(get_mActivity())
                    .load(musicEntity.getCover())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(song_cover);
            if (isPlayStart) {
                song_play_button.setText(pause);
                playAnimation();
            }
        }
    }

    private void playAnimation() {
        if (objectAnimator != null) {
            if (!objectAnimator.isStarted()) {
                objectAnimator.start();
            } else if (objectAnimator.isPaused()) {
                objectAnimator.resume();
            }
        }
    }

    private void pauseAnimation() {
        if (objectAnimator != null) {
            if (objectAnimator.isRunning()) {
                objectAnimator.pause();
            }
        }
    }

    private void playMusic() {
        MusicManager.getInstance().playMusic();
    }

    private void pauseMusic() {
        MusicManager.getInstance().pauseMusic();
    }

    private static class SwitchHandler extends Handler {
        private WeakReference<ZhiliaoSwitchFragment> zhiliaoSwitchFragmentWeakReference;

        public SwitchHandler(ZhiliaoSwitchFragment zhiliaoSwitchFragment) {
            zhiliaoSwitchFragmentWeakReference = new WeakReference<>(zhiliaoSwitchFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ZhiliaoSwitchFragment zhiliaoSwitchFragment = zhiliaoSwitchFragmentWeakReference.get();
            switch (msg.what) {
                case UPDATE_QUEUE:
                    zhiliaoSwitchFragment.queueAdapter.setNewData(MusicManager.getInstance().getPlayList());
                    zhiliaoSwitchFragment.queueAdapter.notifyDataSetChanged();
            }
        }
    }
}
