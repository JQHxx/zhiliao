package com.dev.rexhuang.zhiliao_core.net;

import com.dev.rexhuang.zhiliao_core.entity.ArtistsDataInfo;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public interface QQRestService {

    @Headers("referer: https://y.qq.com/portal/player.html")
    @GET
    Observable<ArtistsDataInfo> getQQArtists(@Url String url, @Query("data") String data);
}
