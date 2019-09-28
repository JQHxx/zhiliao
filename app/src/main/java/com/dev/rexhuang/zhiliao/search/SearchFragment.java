package com.dev.rexhuang.zhiliao.search;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.DialogHelper;
import com.dev.rexhuang.zhiliao.MainSwitchFragment;
import com.dev.rexhuang.zhiliao.QueueDialog;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.WarningDialog;
import com.dev.rexhuang.zhiliao.data.RecordAdapter;
import com.dev.rexhuang.zhiliao.data.RecordDbDao;
import com.dev.rexhuang.zhiliao.detail.DetailActivity;
import com.dev.rexhuang.zhiliao.find.adapter.SearchAdapter;
import com.dev.rexhuang.zhiliao.login.UserManager;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.dev.rexhuang.zhiliao_core.utils.AnimHelper;
import com.dev.rexhuang.zhiliao_core.utils.Utils;
import com.dyhdyh.widget.loadingbar2.LoadingBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.library.flowlayout.FlowLayoutManager;
import com.library.flowlayout.SpaceItemDecoration;
import com.mikepenz.iconics.view.IconicsTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.SupportHelper;

/**
 * *  created by RexHuang
 * *  on 2019/8/18
 */
public class SearchFragment extends ZhiliaoFragment {

    private static final String TAG = SearchFragment.class.getSimpleName();
    //searchRecyclerview
    private SearchAdapter mSearchAdapter;
    private View headerView;

    //queueDialog
    private QueueDialog queueDialog;

    //moreDialog
    private BottomSheetDialog moreDialog;
    private TextView tv_music;
    private TextView tv_close_more;
    private ConstraintLayout cl_next;
    private ConstraintLayout cl_add;
    private ConstraintLayout cl_download;
    private ConstraintLayout cl_share;
    private ConstraintLayout cl_singer;
    private ConstraintLayout cl_ablum;

    //mRecordDbDao
    private RecordDbDao mRecordDbDao;
    private RecordAdapter mRecordAdapter;

    private String currentToken;

    private IRequest request = new IRequest() {
        @Override
        public void onRequestStart() {
            if (rl_loading != null) {
                rl_loading.setVisibility(View.VISIBLE);
                LoadingBar.view(rl_loading)
                        .setFactoryFromResource(R.layout.loading_view)
                        .show();
            }
        }

        @Override
        public void onRequestEnd() {
            if (rl_loading != null) {
                rl_loading.setVisibility(View.GONE);
                LoadingBar.view(rl_loading).cancel();
            }
        }
    };

    private int currentPosition = 0;

    private ObjectAnimator cover_play;
    private String play = "{faw-play}";
    private String pause = "{faw-pause}";
    private SongSearchEntity mSongSearchEntity;
    private List<MusicEntity> mMusicEntities;
    private OnPlayerEventListener onPlayerEventListener;

    @BindView(R.id.rl_loading)
    RelativeLayout rl_loading;

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

    @BindView(R.id.iv_et_clear)
    ImageView iv_et_clear;

    @BindView(R.id.rv_record)
    RecyclerView rv_record;

    @BindView(R.id.cl_record)
    ConstraintLayout cl_record;

    @OnClick(R.id.iv_record_clear)
    void onClickClearRecord() {
        WarningDialog warningDialog = new WarningDialog(getActivity());
        warningDialog.setMessage("是否要清空搜索历史");
        warningDialog.setOnFirmListener(new WarningDialog.OnFirmListener() {
            @Override
            public void onFirm() {
                if (mRecordDbDao != null) {
                    mRecordDbDao.deleteData();
                    mRecordAdapter.setNewData(mRecordDbDao.queryData(""));
                }
            }
        });
        warningDialog.show();
    }

    @OnClick(R.id.iv_et_clear)
    void onClickClearSearchEdit() {
        if (searchEditText != null) {
            searchEditText.setText("");
            cl_record.setVisibility(View.VISIBLE);
            mSearchAdapter.removeHeaderView(headerView);
            mSearchAdapter.setNewData(null);
        }
    }

    @OnClick(R.id.back_arrow)
    void onClickBack() {
        Bundle args = getArguments();
        String from = "";
        if (args != null) {
            from = getArguments().getString(BaseActivity.FRGMENT_FROM);
        }
        if (TextUtils.equals(MainSwitchFragment.class.getSimpleName(), from)) {
//            ((ISupportActivity) get_mActivity()).getSupportDelegate().showHideFragment(
//                    SupportHelper.findFragment(getFragmentManager(), MainSwitchFragment.class), this);
            getSupportDelegate().pop();
        } else {
            getSupportDelegate().pop();
        }
    }

    @OnClick(R.id.controlbar)
    void onClickControl() {
        MusicEntity musicEntity = MusicManager.getInstance().getNowPlayingSongInfo();
        if (musicEntity != null) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicEntity != null ? musicEntity.getId() : null);
            intent.putExtra(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, musicEntity != null ? song_cover.getRotation() : 0f);
            intent.putExtra(BaseActivity.FRGMENT_FROM, SearchFragment.class.getSimpleName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    }

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
        currentToken = UserManager.getInstance().getToken();
        if (TextUtils.isEmpty(currentToken)) {
            Toast.makeText(getActivity(), "您還沒登陸，在綫功能將不能使用！", Toast.LENGTH_SHORT).show();
        }
        headerView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_header, null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicManager.getInstance().playMusic(mMusicEntities, 0);
            }
        });
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
                showPlaying(musicEntity, true, true);
            }
        }
        mSearchAdapter = new SearchAdapter(R.layout.item_music, new ArrayList<>());
        mSearchAdapter.setOnItemClickListener((adapter, view1, position) -> {
            String musicId = mSearchAdapter.getItem(position).getId();
            String musicName = mSearchAdapter.getItem(position).getName();
            if (!TextUtils.isEmpty(musicId) &&
                    !musicId.equals(MusicManager.getInstance().getNowPlayingSongId())) {
                MusicManager.getInstance().playMusicByEntity(mMusicEntities.get(position));
            } else if (!TextUtils.isEmpty(musicName)) {
                Logger.d(musicName + "is already playing");
            }
        });
        mSearchAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                currentPosition = position;
                switch (view.getId()) {
                    case R.id.iv_more:
                        if (moreDialog == null) {
                            moreDialog = DialogHelper.createMoreDialog(getActivity());
                            cl_next = moreDialog.findViewById(R.id.cl_next);
                            cl_add = moreDialog.findViewById(R.id.cl_add);
                            cl_download = moreDialog.findViewById(R.id.cl_download);
                            cl_share = moreDialog.findViewById(R.id.cl_share);
                            cl_singer = moreDialog.findViewById(R.id.cl_singer);
                            cl_ablum = moreDialog.findViewById(R.id.cl_album);
                            tv_music = moreDialog.findViewById(R.id.tv_music);
                            tv_close_more = moreDialog.findViewById(R.id.tv_close);
                            cl_next.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MusicManager.getInstance().playMusicByEntity(mMusicEntities.get(currentPosition));
                                    moreDialog.hide();
                                }
                            });
                            cl_add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    MusicManager.getInstance().addToMusicQueue(mMusicEntities.get(currentPosition));
                                    moreDialog.hide();
                                    Toast.makeText(getContext(), mMusicEntities.get(currentPosition).getName() + "已加入歌单!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            cl_download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            cl_share.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            cl_singer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            cl_ablum.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                            tv_close_more.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (moreDialog != null) {
                                        moreDialog.dismiss();
                                    }
                                }
                            });
                        }
                        tv_music.setText(mMusicEntities.get(position).getName());
                        moreDialog.show();
                        break;
                    default:
                        break;
                }
            }
        });
        rv_search.setLayoutManager(new LinearLayoutManager(get_mActivity()));
        rv_search.setAdapter(mSearchAdapter);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Logger.d("执行搜索");
                String query = searchEditText.getText().toString();
                if (!TextUtils.isEmpty(query) && query.length() > 0) {
                    mRecordDbDao.insertData(query);
                    mRecordAdapter.setNewData(mRecordDbDao.queryData(""));
                    cl_record.setVisibility(View.GONE);
                    ZhiliaoApi.getMusic(currentToken, "keyword", query, request, new ISuccess<SongSearchEntity>() {
                        @Override
                        public void onSuccess(SongSearchEntity response) {
                            SearchFragment.this.mSongSearchEntity = response;
                            SearchFragment.this.mMusicEntities = mSongSearchEntity.getData();
//                            MusicProvider.getInstance().setMusicList(SearchFragment.this.mMusicEntities);
                            if (mMusicEntities == null || mMusicEntities.size() <= 0) {
                                Toast.makeText(getActivity(), "不好意思,没有您想要找的歌曲！", Toast.LENGTH_SHORT).show();
                            } else {
                                if (mSearchAdapter.getHeaderLayoutCount() <= 0) {
                                    mSearchAdapter.addHeaderView(headerView);
                                }
                                mSearchAdapter.getData().clear();
                                mSearchAdapter.setNewData(mMusicEntities);
                            }
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
                    hideSoftInput(view);
                } else if (v.getId() == R.id.searchEditText && hasFocus) {
                    if (searchEditText.getText().length() > 0) {
                        iv_et_clear.setVisibility(View.VISIBLE);
                    } else {
                        iv_et_clear.setVisibility(View.GONE);
                    }
                }
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    iv_et_clear.setVisibility(View.VISIBLE);
                } else {
                    iv_et_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
                if (musicEntity != null) {
                    showPlaying(musicEntity, false, true);
                    if (queueDialog != null) {
                        queueDialog.setNewData(MusicManager.getInstance().getPlayList());
                    }
                }
            }

            @Override
            public void onPlayerStart() {
                showPlaying(MusicManager.getInstance().getNowPlayingSongInfo(), true, false);
//                song_play_button.setText(pause);
//                playAnimation();
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
        mRecordDbDao = new RecordDbDao(getActivity());
        mRecordAdapter = new RecordAdapter(R.layout.item_record);
        rv_record.setLayoutManager(new FlowLayoutManager());
        rv_record.addItemDecoration(new SpaceItemDecoration(Utils.dp2px(5)));
        rv_record.setAdapter(mRecordAdapter);
        mRecordAdapter.setNewData(mRecordDbDao.queryData(""));
        mRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String query = mRecordAdapter.getItem(position);
                searchEditText.setText(query);
                searchEditText.setSelection(query.length());
                hideSoftInput(searchEditText);
                cl_record.setVisibility(View.GONE);
                ZhiliaoApi.getMusic(currentToken, "keyword", query, request, new ISuccess<SongSearchEntity>() {
                    @Override
                    public void onSuccess(SongSearchEntity response) {
                        SearchFragment.this.mSongSearchEntity = response;
                        SearchFragment.this.mMusicEntities = mSongSearchEntity.getData();
                        if (mMusicEntities == null || mMusicEntities.size() <= 0) {
                            Toast.makeText(getActivity(), "不好意思,没有您想要找的歌曲！", Toast.LENGTH_SHORT).show();
                        } else {
                            if (mSearchAdapter.getHeaderLayoutCount() <= 0) {
                                mSearchAdapter.addHeaderView(headerView);
                            }
                            mSearchAdapter.getData().clear();
                            mSearchAdapter.setNewData(mMusicEntities);
                        }
                    }
                }, null, null);
            }
        });
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

    private void showPlaying(MusicEntity musicEntity, boolean isPlayStart, boolean isMusicSwitch) {
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

    @Override
    public void onDestroyView() {
        MusicManager.getInstance().removePlayerEventListener(onPlayerEventListener);
        stopAnimation();
        super.onDestroyView();
    }

    @Override
    public boolean onBackPressedSupport() {
//        Bundle args = getArguments();
//        String from = "";
//        if (args != null) {
//            from = getArguments().getString(BaseActivity.FRGMENT_FROM);
//        }
//        if (TextUtils.equals(MainSwitchFragment.class.getSimpleName(), from)) {
//            ((ISupportActivity) get_mActivity()).getSupportDelegate().showHideFragment(
//                    SupportHelper.findFragment(getFragmentManager(), MainSwitchFragment.class), this);
//        } else {
        getSupportDelegate().pop();
//        }
        return true;
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

    public void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) get_mActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
