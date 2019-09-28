package com.dev.rexhuang.zhiliao.music_hall;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.MainActivity;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.event.MusicHallEvent;
import com.dev.rexhuang.zhiliao_core.api.musiclake.MusicLakeApi;
import com.dev.rexhuang.zhiliao_core.api.qq.QQMusicApi;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoMainFragment;
import com.dev.rexhuang.zhiliao_core.entity.ArtistsDataInfo;
import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.User;
import com.dev.rexhuang.zhiliao.music_hall.adapter.MultipleItemsCreator;
import com.dev.rexhuang.zhiliao.music_hall.adapter.MultipleRecyclerAdapter;
import com.dev.rexhuang.zhiliao.music_hall.adapter.SongListAdapter;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class MusicHallFragment extends ZhiliaoMainFragment {

    public static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTcyOTExIiwiZXhwIjoxNTkzOTY0MDQ5LCJpYXQiOjE1NjI4NjAwNDl9.d9f_6ikO6gDD5Dcra7nxSzkzI8lP6vI4UI4SR32aiRU";
    private MultipleItemsCreator creator;

    @BindView(R.id.rv_music_hall)
    RecyclerView rv_music_hall;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe_layout;


    //    @BindView(R2.id.rv_song_list)
    //    RecyclerView rv_song_list;

    private List<String> imagesArray = new ArrayList<>();
    private String[] images = {
            "http://img2.3lian.com/2014/f2/37/d/40.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg",
            "http://www.8kmm.com/UploadFiles/2012/8/201208140920132659.jpg",
            "http://f.hiphotos.baidu.com/image/h%3D200/sign=1478eb74d5a20cf45990f9df460b4b0c/d058ccbf6c81800a5422e5fdb43533fa838b4779.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/09fa513d269759ee50f1971ab6fb43166c22dfba.jpg"
    };
    private SongListEntity songListEntity;
    private SongListAdapter songListAdapter;
    private MultipleRecyclerAdapter multipleRecyclerAdapter;

    @Override
    public Object setLayout() {
        return R.layout.fragment_music_hall;
    }

    @Override
    public void onBindView(Bundle savedInstanceState, View view) {
        initViewList();
        LinearLayoutManager lp = new LinearLayoutManager(_mActivity);
        lp.setOrientation(RecyclerView.VERTICAL);
        rv_music_hall.setLayoutManager(lp);
        ZhiliaoApi.profile(TOKEN, null, new ISuccess<User>() {
            @Override
            public void onSuccess(User user) {
//                Toast.makeText(getActivity(), user.toString(), Toast.LENGTH_SHORT).show();
            }
        }, null, null);
        ZhiliaoApi.musicbillList(TOKEN, null, new ISuccess<SongListEntity>() {
            @Override
            public void onSuccess(SongListEntity songListEntity) {
                MusicHallFragment.this.songListEntity = songListEntity;
                creator = new MultipleItemsCreator(MusicHallFragment.this.songListEntity.getData(), imagesArray);
                multipleRecyclerAdapter = new MultipleRecyclerAdapter(_mActivity, creator.create());
                multipleRecyclerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        switch (view.getId()) {
                            case R.id.layout_singer:
                                ((MainActivity)getActivity()).getSupportDelegate().start(new ArtistListFragment());
                                break;
                            default:
                                break;
                        }
                    }
                });
                rv_music_hall.setAdapter(multipleRecyclerAdapter);
                MusicLakeApi.getBanner(null, new ISuccess<BannerEntity>() {
                    @Override
                    public void onSuccess(BannerEntity response) {
                        if (response != null) {
                            List<BannerEntity.BannersEntity> bannersEntities = response.getBanners();
                            List<String> images = new ArrayList<>();
                            for (BannerEntity.BannersEntity bannersEntity : bannersEntities) {
                                images.add(bannersEntity.getImageUrl());
                            }
                            creator.setImagesArray(images);
                            multipleRecyclerAdapter.setNewData(creator.create());
                        }
                    }
                }, null, null);
            }
        }, null, null);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Logger.d("onRefresh");
                refreshUI();
            }
        });
    }

    private void refreshUI() {
        ZhiliaoApi.musicbillList(TOKEN, null, new ISuccess<SongListEntity>() {
            @Override
            public void onSuccess(SongListEntity songListEntity) {
                MusicHallFragment.this.songListEntity = songListEntity;
                EventBus.getDefault().post(new MusicHallEvent(songListEntity.getData()));
            }
        }, null, null);
    }

    private void initViewList() {
        int size = images.length;
        for (int i = 0; i < size; i++) {
            imagesArray.add(images[i]);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MusicHallEvent event) {
        creator.setDataEntity(event.dataEntities);
        multipleRecyclerAdapter.setNewData(creator.create());
        multipleRecyclerAdapter.notifyDataSetChanged();
        swipe_layout.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
