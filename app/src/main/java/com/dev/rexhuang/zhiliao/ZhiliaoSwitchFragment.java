package com.dev.rexhuang.zhiliao;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.dev.rexhuang.zhiliao.detail.DetailActivity;
import com.dev.rexhuang.zhiliao.search.SearchFragment;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.BaseFragment;
import com.dev.rexhuang.zhiliao_core.base.FragmentKeys;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.callback.SwitchFragmentListener;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.dev.rexhuang.zhiliao_core.utils.AnimHelper;
import com.gyf.immersionbar.ImmersionBar;
import com.mikepenz.iconics.view.IconicsTextView;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportHelper;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public abstract class ZhiliaoSwitchFragment extends ZhiliaoFragment implements SwitchFragmentListener {

    private static final String TAG = ZhiliaoSwitchFragment.class.getSimpleName();
    protected static final LinkedHashMap<String, BaseFragment> FRAGMENTS = new LinkedHashMap<>();
    protected static final LinkedList<String> NAMES = new LinkedList<>();
    protected static final BaseFragment[] mFragments = new BaseFragment[4];
    private static final int DEFAULT_FRAGMENT_INDEX = 0;
    private int mCurrentFragment = DEFAULT_FRAGMENT_INDEX;
    private int mPreviousFragment = DEFAULT_FRAGMENT_INDEX;
    private SwitchFragmentListener mSwitchFragmentListener;
    private String play = "{faw-play}";
    private String pause = "{faw-pause}";

    //Animator
    private ObjectAnimator cover_play;

    private QueueDialog queueDialog;
    private OnPlayerEventListener onPlayerEventListener;
    private SwitchHandler handler = new SwitchHandler(this);
    private static final int UPDATE_QUEUE = 102000000;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.search_bar)
    LinearLayout search_bar;

    @BindView(R.id.song_cover)
    AppCompatImageView song_cover;

    @BindView(R.id.song_description)
    AppCompatTextView song_description;

    @BindView(R.id.song_play_button)
    IconicsTextView song_play_button;

    @BindView(R.id.song_list_button)
    IconicsTextView song_list_button;

    @OnClick(R.id.song_list_button)
    void onClickList() {
        showQueueDialog();
    }

    @OnClick(R.id.song_play_button)
    void onClickPlay() {
        Logger.d(MusicManager.getInstance().isPlaying());
        if (MusicManager.getInstance().isPlaying()) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    @OnClick(R.id.search_bar)
    void onClickSearch() {
        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
        SearchFragment searchFragment;
        if ((searchFragment = SupportHelper.findFragment(getFragmentManager(), SearchFragment.class)) == null) {
            searchFragment = SearchFragment.newInstance(
                    musicEntity != null ? musicEntity.getId() : null,
                    musicEntity != null ? song_cover.getRotation() : 0f, MainSwitchFragment.class.getSimpleName());
            getSupportDelegate().start(searchFragment, ISupportFragment.SINGLETASK);
        } else {
            Bundle args = searchFragment.getArguments() == null ? new Bundle() : searchFragment.getArguments();
            if (musicEntity != null) {
                args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
                args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
                args.putString(BaseActivity.FRGMENT_FROM, MainSwitchFragment.class.getSimpleName());
                searchFragment.setArguments(args);
            }
            ((ISupportActivity) get_mActivity()).getSupportDelegate().showHideFragment(searchFragment, this);
        }

    }

    @OnClick(R.id.controlbar)
    void onClickControl() {
        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
        if (musicEntity != null) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity != null ? musicEntity.getId() : null);
            intent.putExtra(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, musicEntity != null ? song_cover.getRotation() : 0f);
            intent.putExtra(BaseActivity.FRGMENT_FROM, MainSwitchFragment.class.getSimpleName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    }

    @BindView(R.id.title)
    protected AppCompatTextView mTitleTv;

    @BindViews({R.id.iv_music_hall, R.id.iv_recommend, R.id.iv_find, R.id.iv_profile})
    protected IconicsTextView[] sw_ivs;

    @BindViews({R.id.tv_music_hall, R.id.tv_recommend, R.id.tv_find, R.id.tv_profile})
    protected TextView[] sw_tvs;

    @OnClick(R.id.layout_bottom_music_hall)
    void onClickMusicHall() {
        Toast.makeText(_mActivity, "onClickMusicHall", Toast.LENGTH_SHORT).show();
        getSupportDelegate().showHideFragment(FRAGMENTS.get(FragmentKeys.MUSIC_HALL.name())
                , mFragments[getCurrentFragment()]);
        setCurrentFragment(FragmentKeys.MUSIC_HALL.ordinal());
        if (mSwitchFragmentListener != null) {
            mSwitchFragmentListener.onSwitchEnd();
        }
    }

    @OnClick(R.id.layout_bottom_recommend)
    void onClickRecommend() {
        Toast.makeText(_mActivity, "onClickRecommend", Toast.LENGTH_SHORT).show();
        getSupportDelegate().showHideFragment(FRAGMENTS.get(FragmentKeys.RECOMMEND.name())
                , mFragments[getCurrentFragment()]);
        setCurrentFragment(FragmentKeys.RECOMMEND.ordinal());
        if (mSwitchFragmentListener != null) {
            mSwitchFragmentListener.onSwitchEnd();
        }
    }

    @OnClick(R.id.layout_bottom_find)
    void onClickFind() {
        Toast.makeText(_mActivity, "onClickFind", Toast.LENGTH_SHORT).show();
        getSupportDelegate().showHideFragment(FRAGMENTS.get(FragmentKeys.FIND.name())
                , mFragments[getCurrentFragment()]);
        setCurrentFragment(FragmentKeys.FIND.ordinal());
        if (mSwitchFragmentListener != null) {
            mSwitchFragmentListener.onSwitchEnd();
        }
    }

    @OnClick(R.id.layout_bottom_profile)
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
        setPreviousFragment(this.mCurrentFragment);
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
        cover_play = AnimHelper.rotate(song_cover, "rotation", AnimHelper.DEFAULT_START_ROTATE,
                AnimHelper.DEFAULT_END_ROTATE, AnimHelper.DEFAULT_DURATION,
                ValueAnimator.INFINITE, ValueAnimator.RESTART);
        onPlayerEventListener = new OnPlayerEventListener() {
            @Override
            public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
                if (queueDialog != null) {
                    queueDialog.onQueueChanged(queue);
                }
                if (MusicManager.getInstance().getPlayList().size() <= 0) {
                    showStopped();
                }
            }

            @Override
            public void onMusicSwitch(MusicEntity musicEntity) {
                showPlaying(musicEntity, false, true);
                if (queueDialog != null) {
                    queueDialog.setNewData(MusicManager.getInstance().getPlayList());
                }
            }

            @Override
            public void onPlayerStart() {
                showPlaying(MusicManager.getInstance().getNowPlayingSongInfo(), true, false);
            }

            @Override
            public void onPlayerPause() {
                showPaused();
            }

            @Override
            public void onPlayerStop() {
                showPaused();
            }

            @Override
            public void onPlayCompletion(MusicEntity musicEntity) {
                showPaused();
            }

            @Override
            public void onBuffering() {
                Toast.makeText(get_mActivity(), "正在缓冲,请稍等!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Toast.makeText(get_mActivity(), "播放出错!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                if (queueDialog != null) {
                    queueDialog.onRepeatModeChanged(repeatMode);
                }
                String mode;
                switch (repeatMode) {
                    case PlaybackStateCompat.REPEAT_MODE_NONE:
                        mode = "顺序播放";
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_ONE:
                        mode = "单曲循环";
                        break;
                    case PlaybackStateCompat.REPEAT_MODE_ALL:
                        mode = "列表循环";
                        break;
                    default:
                        mode = "顺序播放";
                        break;
                }
                Toast.makeText(get_mActivity(), mode, Toast.LENGTH_SHORT).show();
            }
        };
        MusicManager.getInstance().addPlayerEventListener(onPlayerEventListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImmersionBar.setTitleBar(getActivity(), toolbar);
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ImmersionBar.with(this).keyboardEnable(true).init();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
        stopAnimation();
        super.onDestroyView();
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

    @Override
    public void onSwitchEnd() {
        mTitleTv.setText((String) Objects.requireNonNull(NAMES.toArray())[getCurrentFragment()]);
        final int mPreviousIndex = getPreviousFragment();
        final int mCurrentIndex = getCurrentFragment();
        sw_ivs[mPreviousIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_unselected));
        sw_tvs[mPreviousIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_unselected));
        sw_ivs[mCurrentIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_selected));
        sw_tvs[mCurrentIndex].setTextColor(_mActivity.getResources().getColor(R.color.switch_selected));
    }

    private void showQueueDialog() {
        if (queueDialog == null) {
            queueDialog = new QueueDialog(getActivity());
        }
        queueDialog.show();
    }

    private void hideQueueDialog() {
        if (queueDialog != null) {
            queueDialog.hide();
        }
    }

    private void showStopped() {
        showPaused();
        song_description.setText("知了音乐 让生活充满音乐");
        Glide.with(getActivity()).clear(song_cover);
        song_cover.setRotation(0f);
        song_cover.setImageDrawable(getActivity().getDrawable(R.drawable.diskte));
    }

    private void showPaused() {
        song_play_button.setText(play);
        pauseAnimation();
    }

    protected void showPlaying(MusicEntity musicEntity, boolean isPlayStart, boolean isMusicSwitch) {
        if (musicEntity != null) {
            if (isMusicSwitch) {
                song_description.setText(String.format("%s - %s", musicEntity.getName(), musicEntity.getSingers().get(0).getName()));
                Glide.with(get_mActivity())
                        .load(musicEntity.getCover())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .placeholder(R.drawable.diskte)
                        .error(R.drawable.diskte)
                        .into(song_cover);
                resetAnimation();
            }
            if (isPlayStart) {
                song_play_button.setText(pause);
                playAnimation();
            }
        }
    }

    private void playAnimation() {
        if (cover_play != null) {
            if (!cover_play.isStarted()) {
                cover_play.start();
            } else if (cover_play.isPaused()) {
                cover_play.resume();
            }
        }
    }

    private void pauseAnimation() {
        if (cover_play != null) {
            if (cover_play.isRunning()) {
                cover_play.pause();
            }
        }
    }

    private void stopAnimation() {
        if (cover_play != null) {
            cover_play.cancel();
        }
    }

    private void resetAnimation() {
        if (cover_play != null) {
            stopAnimation();
            cover_play = AnimHelper.rotate(song_cover, "rotation", AnimHelper.DEFAULT_START_ROTATE,
                    AnimHelper.DEFAULT_END_ROTATE, AnimHelper.DEFAULT_DURATION,
                    ValueAnimator.INFINITE, ValueAnimator.RESTART);
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
            }
        }
    }
}
