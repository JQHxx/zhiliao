package com.dev.rexhuang.zhiliao_core.music_hall;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.R2;
import com.dev.rexhuang.zhiliao_core.api.ZhiliaoApi;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoFragment;
import com.dev.rexhuang.zhiliao_core.base.ZhiliaoMainFragment;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.SongListEntity;
import com.dev.rexhuang.zhiliao_core.entity.SongSearchEntity;
import com.dev.rexhuang.zhiliao_core.entity.User;
import com.dev.rexhuang.zhiliao_core.music_hall.adapter.MultipleItemEntity;
import com.dev.rexhuang.zhiliao_core.music_hall.adapter.MultipleItemType;
import com.dev.rexhuang.zhiliao_core.music_hall.adapter.MultipleItemsCreator;
import com.dev.rexhuang.zhiliao_core.music_hall.adapter.MultipleRecyclerAdapter;
import com.dev.rexhuang.zhiliao_core.music_hall.adapter.SongListAdapter;
import com.dev.rexhuang.zhiliao_core.music_hall.banner.BannerAdapter;
import com.dev.rexhuang.zhiliao_core.net.RestClient;
import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.dev.rexhuang.zhiliao_core.ui.ZhiliaoLoaderIndicator;
import com.victor.loading.newton.NewtonCradleLoading;
import com.wang.avi.AVLoadingIndicatorView;
import com.wenjian.loopbanner.LoopBanner;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import okhttp3.ResponseBody;

/**
 * *  created by RexHuang
 * *  on 2019/7/26
 */
public class MusicHallFragment extends ZhiliaoMainFragment {

    public static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMTcyOTExIiwiZXhwIjoxNTkzOTY0MDQ5LCJpYXQiOjE1NjI4NjAwNDl9.d9f_6ikO6gDD5Dcra7nxSzkzI8lP6vI4UI4SR32aiRU";

//    @BindView(R2.id.newton_cradle_loading)
//    NewtonCradleLoading newton_cradle_loading;
//
//    @BindView(R2.id.avi)
//    AVLoadingIndicatorView avi;

    @BindView(R2.id.rv_music_hall)
    RecyclerView rv_music_hall;

    //    @BindView(R2.id.rv_song_list)
//    RecyclerView rv_song_list;
    private final IRequest iRequest = new IRequest() {
        @Override
        public void onRequestStart() {
//            avi.show();
//            avi.setVisibility(View.VISIBLE);
        }

        @Override
        public void onRequestEnd() {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    avi.hide();
//                    avi.setVisibility(View.GONE);

                }
            }, 3000);

        }
    };
    //    @BindView(R2.id.banner)
//    LoopBanner banner;
    private List<String> imagesArray = new ArrayList<>();
    private String[] images = {
            "http://img2.3lian.com/2014/f2/37/d/40.jpg",
            "http://img2.3lian.com/2014/f2/37/d/39.jpg",
            "http://www.8kmm.com/UploadFiles/2012/8/201208140920132659.jpg",
            "http://f.hiphotos.baidu.com/image/h%3D200/sign=1478eb74d5a20cf45990f9df460b4b0c/d058ccbf6c81800a5422e5fdb43533fa838b4779.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/09fa513d269759ee50f1971ab6fb43166c22dfba.jpg"
    };
    private SongListEntity songListEntity;
    //    private ArrayList<Integer> localImages = new ArrayList<Integer>();
//    private List<String> networkImages;
    private SongListAdapter songListAdapter;
    private MultipleRecyclerAdapter multipleRecyclerAdapter;
    @SuppressLint("HandlerLeak")
    private static final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

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
                Toast.makeText(getActivity(),user.toString(),Toast.LENGTH_SHORT).show();
            }
        }, null, null);
        ZhiliaoApi.musicbillList(TOKEN,null, new ISuccess<SongListEntity>() {
            @Override
            public void onSuccess(SongListEntity songListEntity) {
                MusicHallFragment.this.songListEntity = songListEntity;
                MultipleItemsCreator creator = new MultipleItemsCreator(MusicHallFragment.this.songListEntity.getData(), imagesArray);
                MusicHallFragment.this.multipleRecyclerAdapter = new MultipleRecyclerAdapter(_mActivity, creator.create());
                rv_music_hall.setAdapter(multipleRecyclerAdapter);
            }
        },null,null);
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
}
