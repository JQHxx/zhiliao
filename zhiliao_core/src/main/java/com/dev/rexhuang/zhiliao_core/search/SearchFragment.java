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
import com.dev.rexhuang.zhiliao_core.detail.DetailFragment;
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
import me.yokeyword.fragmentation.ISupportFragment;

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
            queueAdapter = new QueueAdapter(R.layout.item_queue, MusicManager.getInstance().getPlayList());
//            queueAdapter = new QueueAdapter(R.layout.item_queue, queueManager.getPlayingQueue());
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
//                        MediaBrowserCompat.MediaItem item = (MediaBrowserCompat.MediaItem) adapter.getItem(position);
//                        queueManager.removeFromPlayingQueue(item);
//                        queueAdapter.notifyItemRemoved(position);
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
        Logger.d(MusicManager.getInstance().isPlaying());
        if (MusicManager.getInstance().isPlaying()) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    @Override
    public Object setLayout() {
        Logger.t(TAG).d("setLayout");
        return R.layout.fragment_search;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        searchEditText.requestFocus();
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
                }
            }
        }
        mSearchAdapter = new SearchAdapter(R.layout.item_music, new ArrayList<>());
        mSearchAdapter.setOnItemClickListener((adapter, view1, position) -> {
            MusicManager.getInstance().playMusic(mMusicEntities, position);
//            Toast.makeText(get_mActivity(), "onClick : " + position, Toast.LENGTH_SHORT).show();
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

        onPlayerEventListener = new OnPlayerEventListener() {
            @Override
            public void onMusicSwitch(MusicEntity musicEntity) {
                if (musicEntity != null) {
                    showPlaying(musicEntity, false);
                    if (queueAdapter != null) {
                        queueAdapter.notifyDataSetChanged();
                    }
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

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
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
