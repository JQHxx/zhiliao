package com.dev.rexhuang.zhiliao.search;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.MainSwitchFragment;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.detail.DetailFragment;
import com.dev.rexhuang.zhiliao.find.adapter.SearchAdapter;
import com.dev.rexhuang.zhiliao.find.queue.QueueAdapter;
import com.dev.rexhuang.zhiliao.music_hall.MusicHallFragment;
import com.dev.rexhuang.zhiliao_core.api.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.zhiliaomodel.MusicProvider;
import com.dev.rexhuang.zhiliao_core.utils.AnimHelper;
import com.mikepenz.iconics.view.IconicsTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportHelper;

/**
 * *  created by RexHuang
 * *  on 2019/8/18
 */
public class SearchFragment extends ZhiliaoFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private SearchAdapter mSearchAdapter;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private QueueAdapter queueAdapter;
    private ObjectAnimator cover_play;
    private String play = "{faw-play}";
    private String pause = "{faw-pause}";
    private SongSearchEntity mSongSearchEntity;
    private List<MusicEntity> mMusicEntities;
    private OnPlayerEventListener onPlayerEventListener;
    @BindView(R.id.rv_search)
    RecyclerView rv_search;

    @BindView(R.id.searchEditText)
    AppCompatEditText searchEditText;

    @BindView(R.id.song_cover)
    AppCompatImageView song_cover;

    @BindView(R.id.song_description)
    AppCompatTextView song_description;

    @BindView(R.id.song_play_button)
    IconicsTextView song_play_button;

    @BindView(R.id.song_list_button)
    IconicsTextView song_list_button;

    @OnClick(R.id.back_arrow)
    void onClickBack() {
        Bundle args = getArguments();
        String from = "";
        if (args != null) {
            from = getArguments().getString(BaseActivity.FRGMENT_FROM);
        }
        if (TextUtils.equals(MainSwitchFragment.class.getSimpleName(), from)) {
            ((ISupportActivity) get_mActivity()).getSupportDelegate().showHideFragment(
                    SupportHelper.findFragment(getFragmentManager(), MainSwitchFragment.class), this);
        } else {
            getSupportDelegate().pop();
        }
    }

    @OnClick(R.id.controlbar)
    void onClickControl() {
        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
        DetailFragment detailFragment;
        if ((detailFragment = SupportHelper.findFragment(getFragmentManager(), DetailFragment.class)) == null) {
            detailFragment = DetailFragment.newInstance(
                    musicEntity != null ? musicEntity.getId() : null,
                    musicEntity != null ? song_cover.getRotation() : 0f, TAG);
            getSupportDelegate().start(detailFragment, ISupportFragment.SINGLETASK);
        } else {
            Bundle args = detailFragment.getArguments() == null ? new Bundle() : detailFragment.getArguments();
            if (musicEntity != null) {
                args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity.getId());
                args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, song_cover.getRotation());
                args.putString(BaseActivity.FRGMENT_FROM, TAG);
                detailFragment.setArguments(args);
            }
            ((ISupportActivity) get_mActivity()).getSupportDelegate().showHideFragment(detailFragment, this);
        }
    }

    @OnClick(R.id.song_list_button)
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

    @OnClick(R.id.song_play_button)
    void onClickPlay() {
        Logger.d(MusicManager.getInstance().isPlaying());
        if (MusicManager.getInstance().isPlaying()) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    public static SearchFragment newInstance(String musicId, Float rotation, String from) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = searchFragment.getArguments();
        if (args != null) {
            args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicId);
            args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, rotation);
            args.putString(BaseActivity.FRGMENT_FROM, from);
        } else {
            args = new Bundle();
            args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicId);
            args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, rotation);
            args.putString(BaseActivity.FRGMENT_FROM, from);
        }
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public Object setLayout() {
        Logger.t(TAG).d("setLayout");
        return R.layout.fragment_search;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        searchEditText.requestFocus();
        cover_play = AnimHelper.rotate(song_cover, "rotation", AnimHelper.DEFAULT_START_ROTATE,
                AnimHelper.DEFAULT_END_ROTATE, AnimHelper.DEFAULT_DURATION,
                ValueAnimator.INFINITE, ValueAnimator.RESTART);
        if (getArguments() != null) {
            String musicId = getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION) == null ?
                    MusicManager.getInstance().getNowPlayingSongId() :
                    getArguments().getString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION);
            Float rotation = getArguments().getFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION);
            if (song_cover != null) {
                song_cover.setRotation(rotation);
                cover_play = AnimHelper.rotate(song_cover, "rotation", rotation,
                        rotation + AnimHelper.DEFAULT_END_ROTATE, AnimHelper.DEFAULT_DURATION,
                        ValueAnimator.INFINITE, ValueAnimator.RESTART);
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
                showPlaying(musicEntity, true);
            }
        }
        mSearchAdapter = new SearchAdapter(R.layout.item_music, new ArrayList<>());
        mSearchAdapter.setOnItemClickListener((adapter, view1, position) -> {
            MusicManager.getInstance().playMusic(mMusicEntities, position);
        });
        rv_search.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        rv_search.setAdapter(mSearchAdapter);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Logger.d("执行搜索");
                String query = searchEditText.getText().toString();
                if (!TextUtils.isEmpty(query) && query.length() > 0) {
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

    private void showStopped() {
        song_play_button.setText(play);
        pauseAnimation();
    }

    private void showPlaying(MusicEntity musicEntity, boolean isPlayStart) {
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

    private void playMusic() {
        MusicManager.getInstance().playMusic();
    }

    private void pauseMusic() {
        MusicManager.getInstance().pauseMusic();
    }

    @Override
    public void onDestroyView() {
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
        stopAnimation();
        super.onDestroyView();
    }
}