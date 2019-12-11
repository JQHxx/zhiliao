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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import com.dev.rexhuang.zhiliao.search.adapter.NeteaseSearchAdapter;
import com.dev.rexhuang.zhiliao.search.adapter.SearchViewpagerAdapter;
import com.dev.rexhuang.zhiliao_core.api.musiclake.MusicLakeApi;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.BaseActivity;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.entity.ZhiliaoEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dev.rexhuang.zhiliao_core.player2.manager.OnPlayerEventListener;
import com.dev.rexhuang.zhiliao_core.player2.model.MusicProvider;
import com.dev.rexhuang.zhiliao_core.utils.AnimHelper;
import com.dev.rexhuang.zhiliao_core.utils.Utils;
import com.dyhdyh.widget.loadingbar2.LoadingBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;
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

    //TAG
    private static final String TAG = SearchFragment.class.getSimpleName();

    SearchContentFragment searchContentFragment;
    NeteaseSearchContentFragment neteaseSearchContentFragment;

    //queueDialog
    private QueueDialog queueDialog;

    //mRecordDbDao
    private RecordDbDao mRecordDbDao;
    private RecordAdapter mRecordAdapter;

    private String currentToken;

    private int currentPosition = 0;

    private List<BaseSearchContentFragment> fragmentList = new ArrayList<>();

    List<String> titleList = new ArrayList<>();

    private ObjectAnimator cover_play;
    private String play = "{faw-play}";
    private String pause = "{faw-pause}";
    private SongSearchEntity mSongSearchEntity;
    private List<MusicEntity> mMusicEntities;
    private OnPlayerEventListener onPlayerEventListener;

    @BindView(R.id.searchbar)
    LinearLayout searchbar;

//    @BindView(R.id.rl_loading)
//    RelativeLayout rl_loading;

    @BindView(R.id.search_tab)
    TabLayout search_tab;

    @BindView(R.id.search_vp)
    ViewPager search_vp;

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
            showRecord(true);
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
        if (MusicManager.getInstance().isPlaying()) {
            pauseMusic();
        } else {
            playMusic();
        }
    }

    public static SearchFragment newInstance(String musicId, Float rotation, String from) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = searchFragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString(BaseActivity.EXTRA_CURRENT_MEDIA_DESCRIPTION, musicId);
        args.putFloat(BaseActivity.EXTRA_CURRENT_MEDIA_ROTATION, rotation);
        args.putString(BaseActivity.FRGMENT_FROM, from);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public Object setLayout() {
        return R.layout.fragment_search;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        //check if is logined
        currentToken = UserManager.getInstance().getToken();
        if (TextUtils.isEmpty(currentToken)) {
            Toast.makeText(getActivity(), "您還沒登陸，在綫功能將不能使用！", Toast.LENGTH_SHORT).show();
        }

        //同步播放状态
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

        //监听搜索框的内容变化
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Logger.d("执行搜索");
                String query = searchEditText.getText().toString();
                if (!TextUtils.isEmpty(query)) {
                    mRecordDbDao.insertData(query);
                    mRecordAdapter.setNewData(mRecordDbDao.queryData(""));
                    showRecord(false);
                    fragmentList.get(search_vp.getCurrentItem()).showSearchContent(query);
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

        //监听播放器状态变化
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

        //填充搜索历史列表
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
                showRecord(false);
                fragmentList.get(search_vp.getCurrentItem()).showSearchContent(query);
            }
        });

        //搜索内容展示
        searchContentFragment = SearchContentFragment.newInstance(currentToken);
        neteaseSearchContentFragment = NeteaseSearchContentFragment.newInstance();
        fragmentList.add(searchContentFragment);
        fragmentList.add(neteaseSearchContentFragment);
        titleList.add("知了");
        titleList.add("网易云");
        search_vp.setAdapter(new SearchViewpagerAdapter(getChildFragmentManager(), fragmentList, titleList));
        search_vp.setCurrentItem(0);
        search_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String query = searchEditText.getText().toString();
                if (!TextUtils.isEmpty(query)) {
                    fragmentList.get(position).showSearchContent(query);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        search_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                search_vp.setCurrentItem(tab.getPosition());//点击哪个就跳转哪个界面
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        search_tab.setupWithViewPager(search_vp);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImmersionBar.setTitleBar(getActivity(), searchbar);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ImmersionBar.with(this).keyboardEnable(true).init();
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

    public void showRecord(boolean b){
        if (b){
            cl_record.setVisibility(View.VISIBLE);
            search_tab.setVisibility(View.GONE);
            search_vp.setVisibility(View.GONE);
        } else {
            cl_record.setVisibility(View.GONE);
            search_tab.setVisibility(View.VISIBLE);
            search_vp.setVisibility(View.VISIBLE);
        }
    }

}
