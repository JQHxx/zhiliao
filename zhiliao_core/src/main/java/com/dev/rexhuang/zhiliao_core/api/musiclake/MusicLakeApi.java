package com.dev.rexhuang.zhiliao_core.api.musiclake;

import android.util.Log;
import android.widget.Toast;

import com.dev.rexhuang.zhiliao_core.api.BaseApi;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicSongListDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.RecommendSongListEntity;
import com.dev.rexhuang.zhiliao_core.net.NeteaseRestService;
import com.dev.rexhuang.zhiliao_core.net.RestCreator;
import com.dev.rexhuang.zhiliao_core.net.callback.IError;
import com.dev.rexhuang.zhiliao_core.net.callback.IFailure;
import com.dev.rexhuang.zhiliao_core.net.callback.IRequest;
import com.dev.rexhuang.zhiliao_core.net.callback.ISuccess;
import com.orhanobut.logger.Logger;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * *  created by RexHuang
 * *  on 2019/9/25
 */
public class MusicLakeApi extends BaseApi {
    private static final NeteaseRestService neteaseRestService = RestCreator.getNeteaseRestService();

    public static void getBanner(IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "http://music.rexhuang.top/banner";
        Observable observable = neteaseRestService.getBanner(url);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BannerEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BannerEntity bannerEntity) {
                        if (success != null) {
                            success.onSuccess(bannerEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError");
                    }

                    @Override
                    public void onComplete() {
                        Logger.d("onComplete");
                    }
                });
    }

    public static void getRecommendSongList(IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "http://music.rexhuang.top/personalized?limit=10";
        Observable observable = neteaseRestService.getRecommendSongList(url);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RecommendSongListEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(RecommendSongListEntity recommendSongListEntity) {
                        if (success != null) {
                            Log.e("getRecommendSongList", "success != null");
                            success.onSuccess(recommendSongListEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        e.getMessage();
                        Log.e("getRecommendSongList", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getRecommendSongList", "onComplete");
                    }
                });
    }

    public static void getSongListDetail(String songListID, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "http://music.rexhuang.top/playlist/detail?id=" + songListID;
        Observable observable = neteaseRestService.getSongListDetail(url);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NeteaseMusicSongListDetailEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        request.onRequestStart();
                    }

                    @Override
                    public void onNext(NeteaseMusicSongListDetailEntity neteaseMusicSongListDetailEntity) {
                        if (success != null) {
                            Log.e("getSongListDetail", "success != null");
                            success.onSuccess(neteaseMusicSongListDetailEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        e.getMessage();
                        Log.e("getSongListDetail", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getSongListDetail", "onComplete");
                        request.onRequestEnd();
                    }
                });
    }
}
