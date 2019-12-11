package com.dev.rexhuang.zhiliao_core.net;

import com.dev.rexhuang.zhiliao_core.R;
import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicSongListDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicUrlEntity;
import com.dev.rexhuang.zhiliao_core.entity.RecommendSongListEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public interface NeteaseRestService {
    @GET
    Observable<BannerEntity> getBanner(@Url String url);

    @GET
    Observable<RecommendSongListEntity> getRecommendSongList(@Url String url);

    @GET
    Observable<NeteaseMusicSongListDetailEntity> getSongListDetail(@Url String url);

    @GET
    Observable<NeteaseMusicEntity> getSearchMusic(@Url String url);

    @GET
    Observable<NeteaseMusicUrlEntity> getMusicUrl(@Url String url);

    @GET
    Observable<NeteaseMusicDetailEntity> getMusicDeatail(@Url String url);
}
