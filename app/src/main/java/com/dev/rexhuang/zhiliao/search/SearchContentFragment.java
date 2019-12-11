package com.dev.rexhuang.zhiliao.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.DialogHelper;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.find.adapter.SearchAdapter;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dyhdyh.widget.loadingbar2.LoadingBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/12/11
 */
public class SearchContentFragment extends BaseSearchContentFragment {

    //ContainerView
    private View mContainerView;

    //RecyclerView
    private RecyclerView rv_search;

    //LoadingView
    private RelativeLayout rl_loading;

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

    //RecyclerView Adapter
    private SearchAdapter mSearchAdapter;

    //播放实例列表MusicEntities
    private List<MusicEntity> mMusicEntities;

    //当前选择的曲目位置
    private int currentPosition = 0;

    private String currentToken;

    public static SearchContentFragment newInstance(String currentToken) {
        SearchContentFragment searchContentFragment = new SearchContentFragment();
        Bundle args = searchContentFragment.getArguments();
        if (args == null) {
            args = new Bundle();
        }
        args.putString("token", currentToken);
        searchContentFragment.setArguments(args);
        return searchContentFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContainerView = inflater.inflate(R.layout.item_search_viewpager, container, false);
        rv_search = mContainerView.findViewById(R.id.rv_search);
        rl_loading = mContainerView.findViewById(R.id.rl_loading);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_search.setLayoutManager(layoutManager);
        rv_search.setAdapter(mSearchAdapter);
        if (getArguments()!=null){
            currentToken = getArguments().getString("token");
        }
        return mContainerView;
    }

    @Override
    protected void showSearchContent(String query) {
        ZhiliaoApi.getMusic(currentToken, "keyword", query, getRequest(), new ISuccess<SongSearchEntity>() {
            @Override
            public void onSuccess(SongSearchEntity response) {
                SearchContentFragment.this.mMusicEntities = response.getData();
//                            MusicProvider.getInstance().setMusicList(SearchFragment.this.mMusicEntities);
                if (mMusicEntities == null || mMusicEntities.size() <= 0) {
                    Toast.makeText(getActivity(), "不好意思,没有您想要找的歌曲！", Toast.LENGTH_SHORT).show();
                } else {
                    if (mSearchAdapter.getHeaderLayoutCount() <= 0) {
                        if (getHeaderView().getParent() != null) {
                            ((ViewGroup) (getHeaderView().getParent())).removeView(getHeaderView());
                        }
                        mSearchAdapter.addHeaderView(getHeaderView());
                    }
                    mSearchAdapter.getData().clear();
                    mSearchAdapter.setNewData(mMusicEntities);
                }
            }
        }, null, null);
    }

    @Override
    protected void setHeaderView() {
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_header, null);
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicManager.getInstance().playMusic(mMusicEntities, 0);
            }
        });
    }

    @Override
    protected void setRequest() {
        request = new IRequest() {
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
    }
}
