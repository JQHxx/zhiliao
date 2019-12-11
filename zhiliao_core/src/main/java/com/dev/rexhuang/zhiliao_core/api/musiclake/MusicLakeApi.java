package com.dev.rexhuang.zhiliao_core.api.musiclake;

import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.widget.Guideline;

import com.dev.rexhuang.zhiliao_core.api.BaseApi;
import com.dev.rexhuang.zhiliao_core.config.ConfigKeys;
import com.dev.rexhuang.zhiliao_core.config.Zhiliao;
import com.dev.rexhuang.zhiliao_core.entity.BannerEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicSongListDetailEntity;
import com.dev.rexhuang.zhiliao_core.entity.NeteaseMusicUrlEntity;
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
                        if (request != null) {
                            request.onRequestStart();
                        }
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
                        if (request != null) {
                            request.onRequestEnd();
                        }
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
                        if (request != null) {
                            request.onRequestStart();
                        }
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
                        if (request != null) {
                            request.onRequestEnd();
                        }
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
                        if (request != null) {
                            request.onRequestStart();
                        }
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
                        if (request != null) {
                            request.onRequestEnd();
                        }
                    }
                });
    }

    public static void getSearchMusic(String keyword, int limit, int offset, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "http://music.rexhuang.top/search?keywords=" + keyword + "&limit=" + limit + "&offset=" + offset;
        Observable observable = neteaseRestService.getSearchMusic(url);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NeteaseMusicEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        if (request != null) {
                            request.onRequestStart();
                        }
                    }

                    @Override
                    public void onNext(NeteaseMusicEntity neteaseMusicEntity) {
                        if (success != null) {
                            Log.e("getSearchMusic", "success != null");
                            success.onSuccess(neteaseMusicEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        e.getMessage();
                        Log.e("getSearchMusic", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getSearchMusic", "onComplete");
                        if (request != null) {
                            request.onRequestEnd();
                        }

                    }
                });
    }

    public static void getMusicUrl(int musicID, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "http://music.rexhuang.top/song/url?id=" + musicID;
        Observable observable = neteaseRestService.getMusicUrl(url);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NeteaseMusicUrlEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        if (request != null) {
                            request.onRequestStart();
                        }
                    }

                    @Override
                    public void onNext(NeteaseMusicUrlEntity neteaseMusicUrlEntity) {
                        if (success != null) {
                            Log.e("getMusicUrl", "success != null");
                            success.onSuccess(neteaseMusicUrlEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        e.getMessage();
                        Log.e("getMusicUrl", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getMusicUrl", "onComplete");
                        if (request != null) {
                            request.onRequestEnd();
                        }

                    }
                });
    }

    public static void getMusicDeatail(int musicID, IRequest request, ISuccess success, IFailure failure, IError error) {
        String url = "http://music.rexhuang.top/song/detail?ids=" + musicID;
        Observable observable = neteaseRestService.getMusicDeatail(url);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NeteaseMusicDetailEntity>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        if (request != null) {
                            request.onRequestStart();
                        }
                    }

                    @Override
                    public void onNext(NeteaseMusicDetailEntity neteaseMusicDetailEntity) {
                        if (success != null) {
                            Log.e("getMusicDeatail", "success != null");
                            success.onSuccess(neteaseMusicDetailEntity);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        e.getMessage();
                        Log.e("getMusicDeatail", "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.e("getMusicDeatail", "onComplete");
                        if (request != null) {
                            request.onRequestEnd();
                        }

                    }
                });
    }
}
