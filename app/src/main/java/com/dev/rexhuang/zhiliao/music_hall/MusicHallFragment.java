package com.dev.rexhuang.zhiliao.music_hall;

import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao.MainActivity;
import com.dev.rexhuang.zhiliao.R;
import com.dev.rexhuang.zhiliao.event.MusicHallEvent;
import com.dev.rexhuang.zhiliao.login.UserManager;
import com.dev.rexhuang.zhiliao_core.api.musiclake.MusicLakeApi;
import com.dev.rexhuang.zhiliao_core.api.zhiliao.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoMainFragment;
import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;
import com.dev.rexhuang.zhiliao_core.entity.RecommendSongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao.music_hall.adapter.MultipleItemsCreator;
import com.dev.rexhuang.zhiliao.music_hall.adapter.MultipleRecyclerAdapter;
import com.dev.rexhuang.zhiliao.music_hall.adapter.SongListAdapter;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class MusicHallFragment extends ZhiliaoMainFragment {

    private final int count = 3;
    private CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
    private MultipleItemsCreator creator;

    @BindView(R.id.rv_music_hall)
    RecyclerView rv_music_hall;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipe_layout;


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
        initBannerList();
        LinearLayoutManager lp = new LinearLayoutManager(_mActivity);
        lp.setOrientation(RecyclerView.VERTICAL);
        rv_music_hall.setLayoutManager(lp);
//        ZhiliaoApi.profile(UserManager.getInstance().getToken(), null, new ISuccess<User>() {
//            @Override
//            public void onSuccess(User user) {
//            }
//        }, null, null);
        creator = new MultipleItemsCreator();
        multipleRecyclerAdapter = new MultipleRecyclerAdapter(_mActivity, creator.create());
        multipleRecyclerAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.layout_singer:
                        ((MainActivity) getActivity()).getSupportDelegate().start(new ArtistListFragment());
                        break;
                    default:
                        break;
                }
            }
        });
        rv_music_hall.setAdapter(multipleRecyclerAdapter);
        getData();
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Logger.d("onRefresh");
                refreshUI();
            }
        });
    }

    private void refreshUI() {
        getData();
    }

    private void initBannerList() {
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
        multipleRecyclerAdapter.setNewData(event.multipleItemEntities);
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

    private void getData() {
        ZhiliaoApi.musicbillList(UserManager.getInstance().getToken(), null, new ISuccess<SongListEntity>() {
            @Override
            public void onSuccess(SongListEntity songListEntity) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        MusicHallFragment.this.songListEntity = songListEntity;
                        creator.setDataEntity(songListEntity.getData());
                        try {
                            cyclicBarrier.await();
                        } catch (BrokenBarrierException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new MusicHallEvent(creator.create()));
                    }
                }.start();
            }
        }, null, null);
        MusicLakeApi.getBanner(null, new ISuccess<BannerEntity>() {
            @Override
            public void onSuccess(BannerEntity response) {
                if (response != null && response.getCode() == 200) {
                    List<BannerEntity.BannersEntity> bannersEntities = response.getBanners();
                    creator.setImagesArray(bannersEntities);
                    try {
                        cyclicBarrier.await();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null, null);
        MusicLakeApi.getRecommendSongList(null, new ISuccess<RecommendSongListEntity>() {
            @Override
            public void onSuccess(RecommendSongListEntity response) {
                if (response != null && response.getCode() == 200) {
                    List<RecommendSongListEntity.ResultEntity> resultEntities = response.getResult();
                    creator.setRecommendListEntity(resultEntities);
                    try {
                        cyclicBarrier.await();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, null, null);
    }
}
