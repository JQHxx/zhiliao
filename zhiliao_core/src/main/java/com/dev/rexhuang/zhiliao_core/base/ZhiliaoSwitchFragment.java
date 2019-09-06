package com.dev.rexhuang.zhiliao_core.base;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
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
import com.dev.rexhuang.zhiliao_core.callback.MediaFragmentListener;
import com.dev.rexhuang.zhiliao_core.callback.SwitchFragmentListener;
import com.dev.rexhuang.zhiliao_core.callback.SwitchMediaFragmentListener;
import com.dev.rexhuang.zhiliao_core.detail.DetailFragment;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.find.queue.QueueAdapter;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;
import com.dev.rexhuang.zhiliao_core.search.SearchFragment;
import com.mikepenz.iconics.view.IconicsTextView;
import com.orhanobut.logger.Logger;

import java.util.LinkedHashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportFragment;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public abstract class ZhiliaoSwitchFragment extends ZhiliaoFragment implements SwitchMediaFragmentListener {

    private static final String TAG = ZhiliaoSwitchFragment.class.getSimpleName();
    protected static final LinkedHashMap<String, BaseFragment> FRAGMENTS = new LinkedHashMap<>();
    protected static final BaseFragment[] mFragments = new BaseFragment[4];
    private int mCurrentFragment;
    private int mPreviousFragment;
    private SwitchFragmentListener mSwitchFragmentListener;
    private MediaFragmentListener mMediaFragmentListener;
    private MediaBrowserCompat mediaBrowser;
    private String play = "{faw-play}";
    private String pause = "{faw-pause}";
    private Animation rotate;
    private ObjectAnimator objectAnimator;
    private QueueAdapter queueAdapter;
    private RecyclerView recyclerView;
    private QueueManager queueManager = QueueManager.getInstance();
    private Dialog dialog;

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
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        recyclerView = dialog.findViewById(R.id.rcv_songs);
        recyclerView.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        queueAdapter = new QueueAdapter(R.layout.item_queue, queueManager.getPlayingQueue());
        queueAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MediaBrowserCompat.MediaItem item = (MediaBrowserCompat.MediaItem) adapter.getItem(position);
                queueManager.setCurrentItem(item);
                queueAdapter.notifyDataSetChanged();
                mMediaFragmentListener.onMediaItemSelected(item);
            }
        });
        queueAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_more) {
                    MediaBrowserCompat.MediaItem item = (MediaBrowserCompat.MediaItem) adapter.getItem(position);
                    queueManager.removeFromPlayingQueue(item);
                    queueAdapter.notifyItemRemoved(position);
                }
            }
        });
        recyclerView.setAdapter(queueAdapter);
        dialog.show();
    }

    @OnClick(R2.id.song_play_button)
    void onClickPlay() {
        if (MusicManager.getInstance().isPlaying()) {
            playMusic();
        } else {
            pauseMusic();
        }
        //这里之前交给Activity相应
//        if (mMediaFragmentListener != null) {
//            if (PlayState.isPlaying()) {
//                mMediaFragmentListener.onPlayAction(PlayActions.PAUSE);
//            } else {
//                mMediaFragmentListener.onPlayAction(PlayActions.PLAY);
//            }
//
//        }
    }

    @OnClick(R2.id.search_bar)
    void onClickSearch() {
//        getSupportDelegate().showHideFragment(new SearchFragment(), mFragments[getCurrentFragment()]);
        SearchFragment searchFragment = new SearchFragment();
        Bundle arg = searchFragment.getArguments();
//        MediaControllerCompat controllerCompat = MediaControllerCompat.getMediaController(getActivity());
//        MediaMetadataCompat metadata = controllerCompat.getMetadata();
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
        Glide.with(get_mActivity())
                .load("https://static.mebtte.com/music_cover/e1ab493f2344148186eeda132278fbe4.jpeg")
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(song_cover);
        mMediaFragmentListener = (MediaFragmentListener) get_mActivity();
        mediaBrowser = getMediaBrowser();
        rotate = AnimationUtils.loadAnimation(get_mActivity(), R.anim.rotate);
        rotate.setInterpolator(new LinearInterpolator());
        objectAnimator = ObjectAnimator.ofFloat(song_cover, "rotation", 0f, 360f).setDuration(10000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
//        song_cover.getRotation()
        MusicManager.getInstance().addPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onMusicSwitch(MusicEntity musicEntity) {
                song_description.setText(String.format("%s - %s", musicEntity.getName(), musicEntity.getSingers().get(0).getName()));
                Glide.with(get_mActivity())
                        .load(musicEntity.getCover())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(song_cover);
            }

            @Override
            public void onPlayerStart() {
                song_play_button.setText(pause);
                if (objectAnimator.isPaused()) {
                    objectAnimator.resume();
                } else if (!objectAnimator.isStarted()) {
                    objectAnimator.start();
                }
            }

            @Override
            public void onPlayerPause() {
                song_play_button.setText(play);
                objectAnimator.pause();
            }

            @Override
            public void onPlayerStop() {

            }

            @Override
            public void onPlayCompletion(MusicEntity musicEntity) {

            }

            @Override
            public void onBuffering() {
                Toast.makeText(get_mActivity(), "正在缓冲,请稍等!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                Toast.makeText(get_mActivity(), "播放出错!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.t(TAG).d("onStart");
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(get_mActivity());
        if (controller != null) {
            onConnected();
        }
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
        Logger.t(TAG).d("onStop");
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(get_mActivity());
        if (controller != null) {
            controller.unregisterCallback(mMediaControllerCallback);
        }
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
    public MediaBrowserCompat getMediaBrowser() {
        return mMediaFragmentListener.getMediaBrowser();
    }

    @Override
    public void onMediaItemSelected(MediaBrowserCompat.MediaItem mediaItem) {
        mMediaFragmentListener.onMediaItemSelected(mediaItem);
    }

    public void onConnected() {
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(Objects.requireNonNull(getActivity()));
        Logger.t(TAG).d("onConnected, mediaController==null? ", controller == null);
        if (isDetached()) {
            return;
        }
        if (controller != null) {
            onMetadataChanged(controller.getMetadata());
            onPlaybackStateChanged(controller.getPlaybackState());
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    @Override
    public void onMetadataChanged(MediaMetadataCompat metadata) {
        Logger.t(TAG).d("onMetadataChanged" + metadata);
        if (getActivity() == null) {
            Logger.t(TAG).w("onMetadataChanged called when getActivity null," +
                    "this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }
        if (metadata == null) {
            return;
        }
        Logger.t(TAG).d("onMetadataChanged" + metadata);
        MediaDescriptionCompat descriptionCompat = metadata.getDescription();
        String title = (String) descriptionCompat.getTitle();
        String subTitle = (String) descriptionCompat.getSubtitle();
        Uri iconUri = descriptionCompat.getIconUri();
        if (title.length() > 0 || subTitle.length() > 0) {
            song_description.setText(String.format("%s - %s", descriptionCompat.getTitle(), descriptionCompat.getSubtitle()));
        }
        if (iconUri != null) {
            Glide.with(get_mActivity())
                    .load(descriptionCompat.getIconUri())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(song_cover);
        }
    }

    @Override
    public void onPlaybackStateChanged(PlaybackStateCompat state) {
        Logger.t(TAG).d("onPlaybackStateChanged" + state);
        if (getActivity() == null) {
            Logger.t(TAG).w("onPlaybackStateChanged called when getActivity null," +
                    "this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }
        if (state == null) {
            return;
        }
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                showPlaying();
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                showStopped();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                showStopped();
            default:
                break;
        }
    }

    private void showStopped() {
        song_play_button.setText(play);
//        song_cover.clearAnimation();
        objectAnimator.pause();
//        objectAnimator.end();
    }

    protected void showPlaying() {
        song_play_button.setText(pause);
        MediaDescriptionCompat descriptionCompat = queueManager.getCurrentItem().getDescription();
        song_description.setText(String.format("%s - %s", descriptionCompat.getTitle(), descriptionCompat.getSubtitle()));
        Glide.with(get_mActivity())
                .load(descriptionCompat.getIconUri())
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(song_cover);
//        if (rotate != null) {
//            song_cover.startAnimation(rotate);
//        } else {
//            song_cover.setAnimation(rotate);
//            song_cover.startAnimation(rotate);
//        }
        if (objectAnimator.isPaused()) {
            objectAnimator.resume();
        } else if (!objectAnimator.isStarted()) {
            objectAnimator.start();
        }

//        objectAnimator.
    }


    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    ZhiliaoSwitchFragment.this.onMetadataChanged(metadata);
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    ZhiliaoSwitchFragment.this.onPlaybackStateChanged(state);
                }
            };

    private void playMusic() {
        MusicManager.getInstance().playMusic();
    }

    private void pauseMusic() {
        MusicManager.getInstance().pauseMusic();
    }
}
