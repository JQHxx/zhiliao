package com.dev.rexhuang.zhiliao.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.search.adapter.NeteaseSearchAdapter;
import com.dev.rexhuang.zhiliao_core.api.musiclake.MusicLakeApi;
import com.dev.rexhuang.zhiliao_core.entity.MusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicUrlEntity;
import com.dev.rexhuang.zhiliao_core.entity.SingersEntity;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.player2.manager.MusicManager;
import com.dyhdyh.widget.loadingbar2.LoadingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * *  created by RexHuang
 * *  on 2019/12/11
 */
public class NeteaseSearchContentFragment extends BaseSearchContentFragment {

    private View mContainerView;
    private RecyclerView rv_search;
    private RelativeLayout rl_loading;
    private NeteaseSearchAdapter mNeteaseSearchAdapter;

    private static final int DEFAULT_LIMIT = 30;
    private int offset = 0;
    private int page = 1;
    private String query;


    public static NeteaseSearchContentFragment newInstance() {
        return new NeteaseSearchContentFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContainerView = inflater.inflate(R.layout.item_search_viewpager, container, false);
        rv_search = mContainerView.findViewById(R.id.rv_search);
        rl_loading = mContainerView.findViewById(R.id.rl_loading);
        mNeteaseSearchAdapter = new NeteaseSearchAdapter(R.layout.item_music, new ArrayList<>());
        mNeteaseSearchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                int musicId = mNeteaseSearchAdapter.getItem(position).getId();
                MusicLakeApi.getMusicDeatail(musicId, null, new ISuccess<NeteaseMusicDetailEntity>() {
                    @Override
                    public void onSuccess(NeteaseMusicDetailEntity response) {
                        if (response != null && response.getCode() == 200) {
                            String picUrl = response.getSongs().get(0).getAl().getPicUrl();
                            MusicLakeApi.getMusicUrl(musicId, null, new ISuccess<NeteaseMusicUrlEntity>() {
                                @Override
                                public void onSuccess(NeteaseMusicUrlEntity response) {
                                    if (response != null && response.getCode() == 200) {
                                        MusicEntity musicEntity = new MusicEntity();
                                        musicEntity.setId(String.valueOf(musicId));
                                        musicEntity.setName(mNeteaseSearchAdapter.getItem(position).getName());
                                        List<SingersEntity> singers = new ArrayList<>();
                                        SingersEntity singersEntity = new SingersEntity();
                                        singersEntity.setName(mNeteaseSearchAdapter.getItem(position).getArtists().get(0).getName());
                                        singers.add(singersEntity);
                                        musicEntity.setSingers(singers);
                                        musicEntity.setNormal(response.getData().get(0).getUrl());
                                        musicEntity.setCover(picUrl);
                                        MusicManager.getInstance().playMusicByEntity(musicEntity);
                                    }
                                }
                            }, null, null);
                        }
                    }
                },null,null);

            }
        });
        mNeteaseSearchAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                MusicLakeApi.getSearchMusic(query, DEFAULT_LIMIT, offset, null, new ISuccess<NeteaseMusicEntity>() {
                    @Override
                    public void onSuccess(NeteaseMusicEntity response) {
                        if (response != null && response.getCode() == 200) {
                            mNeteaseSearchAdapter.addData(response.getResult().getSongs());
                            mNeteaseSearchAdapter.loadMoreComplete();
                            page++;
                            offset = (page - 1) * DEFAULT_LIMIT;
                        }
                    }
                }, null, null);
            }
        }, rv_search);
        rv_search.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_search.setAdapter(mNeteaseSearchAdapter);
        return mContainerView;
    }

    @Override
    protected void showSearchContent(String query) {
        resetPage();
        MusicLakeApi.getSearchMusic(query, DEFAULT_LIMIT, offset, getRequest(), new ISuccess<NeteaseMusicEntity>() {
            @Override
            public void onSuccess(NeteaseMusicEntity response) {
                if (response != null && response.getCode() == 200) {
                    NeteaseSearchContentFragment.this.query = query;
                    page++;
                    offset = (page - 1) * DEFAULT_LIMIT;
                    if (mNeteaseSearchAdapter.getHeaderLayoutCount() <= 0) {
                        if (getHeaderView().getParent() != null) {
                            ((ViewGroup) (getHeaderView().getParent())).removeView(getHeaderView());
                        }
                        mNeteaseSearchAdapter.addHeaderView(getHeaderView());
                    }
                    mNeteaseSearchAdapter.setNewData(response.getResult().getSongs());
                }
            }
        }, null, null);
    }

    private void resetPage() {
        page = 1;
        offset = 0;
    }

    @Override
    protected void setHeaderView() {
        mHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_search_header, null);
        mHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
