package com.dev.rexhuang.zhiliao_core.net;

import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;

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
}
