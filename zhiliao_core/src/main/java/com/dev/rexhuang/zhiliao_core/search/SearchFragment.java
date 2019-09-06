package com.dev.rexhuang.zhiliao_core.search;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
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
import com.dev.rexhuang.zhiliao_core.api.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.callback.MediaFragmentListener;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.find.adapter.SearchAdapter;
import com.dev.rexhuang.zhiliao_core.find.queue.QueueAdapter;
import com.dev.rexhuang.zhiliao_core.music_hall.MusicHallFragment;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.PlayState;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.playback.PlayActions;
import com.dev.rexhuang.zhiliao_core.player2.playback.QueueManager;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;
import com.mikepenz.iconics.view.IconicsTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/8/18
 */
public class SearchFragment extends ZhiliaoFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private SearchAdapter mSearchAdapter;
    private MediaFragmentListener mMediaFragmentListener;
    private String mMediaId;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private QueueAdapter queueAdapter;
    private QueueManager queueManager = QueueManager.getInstance();
    private ObjectAnimator objectAnimator;
    private String play = "{faw-play}";
    private String pause = "{faw-pause}";
    private MediaBrowserCompat mediaBrowser;
    private SongSearchEntity mSongSearchEntity;
    private List<MusicEntity> mMusicEntities;
    OnPlayerEventListener onPlayerEventListener;
    @BindView(R2.id.rv_search)
    RecyclerView rv_search;

    @BindView(R2.id.searchEditText)
    AppCompatEditText searchEditText;

    @BindView(R2.id.song_cover)
    AppCompatImageView song_cover;

    @BindView(R2.id.song_description)
    AppCompatTextView song_description;

    @BindView(R2.id.song_play_button)
    IconicsTextView song_play_button;

    @BindView(R2.id.song_list_button)
    IconicsTextView song_list_button;

    @OnClick(R2.id.back_arrow)
    void onClickBack() {
        getSupportDelegate().pop();
//        getSupportDelegate().h
    }

    @OnClick(R2.id.song_list_button)
    void onClickList() {
        if (dialog == null) {
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
            final float scale = ((Context) Zhiliao.getConfig(ConfigKeys.APPLICATION_CONTEXT.name())).getResources().getDisplayMetrics().density;
            int height = (int) (400 * scale + 0.5f);
            //设置对话框大小
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height);
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
        }
        queueAdapter.notifyDataSetChanged();
        dialog.show();
    }

    @OnClick(R2.id.song_play_button)
    void onClickPlay() {
//        song_play_button.setText(playButtonState[index % playButtonState.length]);
//        index++;
        if (mMediaFragmentListener != null) {
            if (PlayState.isPlaying()) {
                mMediaFragmentListener.onPlayAction(PlayActions.PAUSE);
            } else {
                mMediaFragmentListener.onPlayAction(PlayActions.PLAY);
            }

        }
    }

    @Override
    public Object setLayout() {
        Logger.t(TAG).d("setLayout");
        return R.layout.fragment_search;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
//        mMediaFragmentListener = (MediaFragmentListener) get_mActivity();
//        mediaBrowser = mMediaFragmentListener.getMediaBrowser();
        objectAnimator = ObjectAnimator.ofFloat(song_cover, "rotation", 0f, 360f).setDuration(10000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        if (getArguments() != null) {
            String musicId = getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION) == null ?
                    MusicManager.getInstance().getNowPlayingSongId() :
                    getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            Float rotation = getArguments().getFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION);
            if (song_cover != null) {
                song_cover.setRotation(rotation);
                objectAnimator = ObjectAnimator.ofFloat(song_cover, "rotation", rotation, rotation + 360f).setDuration(10000);
                objectAnimator.setInterpolator(new LinearInterpolator());
                objectAnimator.setRepeatCount(-1);
                objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            }
            MusicEntity musicEntity = MusicProvider.getInstance().getMusicEntity(musicId);
            if (musicEntity != null) {
                song_description.setText(String.format("%s - %s", musicEntity.getName(), musicEntity.getSingers().get(0).getName()));

                Glide.with(get_mActivity())
                        .load(musicEntity.getCover())
                        .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                        .into(song_cover);
            }
            if (MusicManager.getInstance().isPlaying()) {
                song_play_button.setText(pause);
                if (objectAnimator.isPaused()) {
                    objectAnimator.resume();
                } else if (!objectAnimator.isStarted()) {
                    objectAnimator.start();
//                    objectAnimator.setFloatValues(rotation);
                }
            }
        }

        onPlayerEventListener = new OnPlayerEventListener() {
            @Override
            public void onMusicSwitch(MusicEntity musicEntity) {
                if (musicEntity != null) {
                    song_description.setText(String.format("%s - %s", musicEntity.getName(), musicEntity.getSingers().get(0).getName()));
                    Glide.with(get_mActivity())
                            .load(musicEntity.getCover())
                            .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                            .into(song_cover);
                }
            }

            @Override
            public void onPlayerStart() {
                song_play_button.setText(pause);
                if (objectAnimator != null) {
                    if (objectAnimator.isPaused()) {
                        objectAnimator.resume();
                    } else if (!objectAnimator.isStarted()) {
                        objectAnimator.start();
                    }
                }
            }

            @Override
            public void onPlayerPause() {
                song_play_button.setText(play);
                if (objectAnimator != null) {
                    if (objectAnimator.isRunning()) {
                        objectAnimator.pause();
                    }
                }
            }

            @Override
            public void onPlayerStop() {
                song_play_button.setText(play);
                if (objectAnimator != null) {
                    objectAnimator.end();
                }
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
        };
        MusicManager.getInstance().addPlayerEventListener(onPlayerEventListener);
        mSearchAdapter = new SearchAdapter(R.layout.item_music, new ArrayList<>());
        mSearchAdapter.setOnItemClickListener((adapter, view1, position) -> {
            //暂时屏蔽
            /*MediaBrowserCompat.MediaItem mediaItem = (MediaBrowserCompat.MediaItem) adapter.getItem(position);
            if (!PlayState.isPlaying()) {
                mMediaFragmentListener.onMediaItemSelected(mediaItem);
            } else {
                QueueManager.getInstance().addToPlayingQueue(mediaItem);
            }*/

            //starrySky
//            if (mediaBrowser != null && mediaBrowser.isConnected()) {
            MusicManager.getInstance().playMusic(mMusicEntities, position);
            Toast.makeText(get_mActivity(), "onClick : " + position, Toast.LENGTH_SHORT).show();

//            }
        });
        rv_search.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        rv_search.setAdapter(mSearchAdapter);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Logger.d("执行搜索");
                String query = searchEditText.getText().toString();
                if (!TextUtils.isEmpty(query) && query.length() > 0) {
//                    暂时屏蔽
//                    subscribeData(query);
                    ZhiliaoApi.getMusic(MusicHallFragment.TOKEN, "keyword", query, null, new ISuccess<SongSearchEntity>() {
                        @Override
                        public void onSuccess(SongSearchEntity response) {
                            SearchFragment.this.mSongSearchEntity = response;
                            SearchFragment.this.mMusicEntities = mSongSearchEntity.getData();
                            MusicProvider.getInstance().setMusicList(SearchFragment.this.mMusicEntities);
                            mSearchAdapter.getData().clear();
                            mSearchAdapter.setNewData(MusicProvider.getInstance().getMusicList());
                        }
                    }, null, null);
                }
            }
            return false;
        });
        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.searchEditText && !hasFocus) {
                    InputMethodManager imm = (InputMethodManager) get_mActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
//        if (mediaBrowser.isConnected()) {
//            onConnected();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        MediaControllerCompat controller = MediaControllerCompat.getMediaController(get_mActivity());
//        if (controller != null) {
//            controller.unregisterCallback(mMediaControllerCallback);
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
    }

    private void onConnected() {
        if (isDetached()) {
            return;
        }
        if (mMediaId == null) {
            mMediaId = mMediaFragmentListener.getMediaBrowser().getRoot();
        }

        // Unsubscribing before subscribing is required if this mediaId already has a subscriber
        // on this MediaBrowser instance. Subscribing to an already subscribed mediaId will replace
        // the callback, but won't trigger the initial callback.onChildrenLoaded.
        //
        // This is temporary: A bug is being fixed that will make subscribe
        // consistently call onChildrenLoaded initially, no matter if it is replacing an existing
        // subscriber or not. Currently this only happens if the mediaID has no previous
        // subscriber or if the media content changes on the service side, so we need to
        // unsubscribe first.
        Logger.d("onConnectd subscribe");
        mMediaFragmentListener.getMediaBrowser().unsubscribe(mMediaId);
        mMediaFragmentListener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

        // Add MediaController callback so we can redraw the list when metadata changes:
        MediaControllerCompat controller = MediaControllerCompat.getMediaController(Objects.requireNonNull(getActivity()));
        if (controller != null) {
            onMetadataChanged(controller.getMetadata());
            onPlaybackStateChanged(controller.getPlaybackState());
            controller.registerCallback(mMediaControllerCallback);
        }
    }

    private void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        Logger.t(TAG).d("onPlaybackStateChanged" + state.getState());
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                showPlaying();
                break;
            case PlaybackStateCompat.STATE_STOPPED:
                showStopped();
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                showStopped();
            case PlaybackStateCompat.STATE_ERROR:
                Toast.makeText(get_mActivity(), state.getErrorMessage(), Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private void onMetadataChanged(MediaMetadataCompat metadata) {
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

    private final MediaControllerCompat.Callback mMediaControllerCallback =
            new MediaControllerCompat.Callback() {
                @Override
                public void onMetadataChanged(MediaMetadataCompat metadata) {
                    super.onMetadataChanged(metadata);
                    SearchFragment.this.onMetadataChanged(metadata);
                }

                @Override
                public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
                    super.onPlaybackStateChanged(state);
                    SearchFragment.this.onPlaybackStateChanged(state);
                }
            };

    private final MediaBrowserCompat.SubscriptionCallback mSubscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId,
                                             @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    //暂时屏蔽
//                    try {
//                        Toast.makeText(get_mActivity(), "fragment onChildrenLoaded, parentId=" + parentId +
//                                "  count=" + children.size(), Toast.LENGTH_SHORT).show();
//                        mSearchAdapter.getData().clear();
//                        for (MediaBrowserCompat.MediaItem item : children) {
//                            mSearchAdapter.addData(item);
//                        }
//                        mSearchAdapter.notifyDataSetChanged();
//                    } catch (Throwable t) {
//                        t.printStackTrace();
//                    }
                }

                @Override
                public void onError(@NonNull String id) {
                    Toast.makeText(getActivity(), "onError", Toast.LENGTH_LONG).show();
                }
            };

    private void subscribeData(String mediaId) {
        mMediaId = mediaId;
        Logger.t(TAG).d("onClick subscribe");
        mMediaFragmentListener.getMediaBrowser().unsubscribe(mMediaId);
        mMediaFragmentListener.getMediaBrowser().subscribe(mMediaId, mSubscriptionCallback);

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

    private void showStopped() {
        song_play_button.setText(play);
//        song_cover.clearAnimation();
        objectAnimator.pause();
//        objectAnimator.end();
    }

//    private List<MusicEntity> convertToMusicEntity(List<SongSearchEntity.DataEntity> dataEntities){
//        List<MusicEntity> musicEntities = new ArrayList<>(dataEntities.size());
////        for (S)
//    }

    public int[] bubbleSort(int[] sourceArr) {
        int[] arr = Arrays.copyOf(sourceArr, sourceArr.length);
        int length = arr.length;
        for (int i = 1; i < length; i++) {
            boolean flag = true;
            for (int j = 0; j < length - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = tmp;
                    flag = false;
                }
            }

            if (flag) {
                break;
            }
        }
        return arr;
    }

    public int[] selectSort(int[] sourceArr) {
        int[] arr = Arrays.copyOf(sourceArr, sourceArr.length);
        int length = arr.length;
        for (int i = 0; i < length - 1; i++) {
            int min = i;
            for (int j = i + 1; j <= length - 1; j++) {
                if (arr[min] > arr[j]) {
                    min = j;
                }
            }
            if (min != i) {
                int tmp = arr[i];
                arr[i] = arr[min];
                arr[min] = tmp;
            }
        }

        return arr;
    }

    public int[] insertSort(int[] sourceArr) {
        int[] arr = Arrays.copyOf(sourceArr, sourceArr.length);
        int length = arr.length;
        for (int i = 1; i < length; i++) {
            int tmp = arr[i];
            int j = i;
            while (j > 0 && arr[j - 1] > tmp) {
                arr[j] = arr[j - 1];
                j--;
            }
            if (j != i) {
                arr[j] = tmp;
            }
        }
        return arr;
    }

    public int[] shellSort(int[] sourceArr) {
        int[] arr = Arrays.copyOf(sourceArr, sourceArr.length);
        int length = arr.length;

        return arr;
    }

}
